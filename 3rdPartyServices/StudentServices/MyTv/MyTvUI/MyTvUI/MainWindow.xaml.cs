using System;
using System.Collections;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Threading;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using CoreAudioApi;
using Microsoft.Kinect;
using Microsoft.Kinect.Toolkit;

namespace MyTvUI
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        #region variables
        //user variables
        private String userID;
        private int currentChannel = 0;
        private Boolean currentlyMuted = true;

        //socket variables
        private SocketClient socketClient;
        private SocketServer socketServer;
        private Boolean commsInitialised = false;

        //window variables
        private bool closing = false;

        //button variables
        private ImageBrush channelBg_deselected;
        private ImageBrush channelBg_selected;
        private ImageBrush offBg_deselected;
        private ImageBrush offBg_selected;
        private ImageBrush muteBg_deselected;
        private ImageBrush muteBg_selected;
        private ImageBrush unmuteBg_deselected;
        private ImageBrush unmuteBg_selected;

        //console variables
        private FileStream ostrm;
        private StreamWriter writer;
        private TextWriter oldOut = Console.Out;

        //Channel constants
        private static readonly String channel0 = "http://www.macs.hw.ac.uk/~ceesmm1/societies/mytv/channels/splashScreen.html";
        private static readonly String channel1 = "http://www.macs.hw.ac.uk/~ceesmm1/societies/mytv/channels/channel1.html";
        private static readonly String channel2 = "http://www.macs.hw.ac.uk/~ceesmm1/societies/mytv/channels/channel2.html";
        private static readonly String channel3 = "http://www.macs.hw.ac.uk/~ceesmm1/societies/mytv/channels/channel3.html";
        private static readonly String channel4 = "http://www.macs.hw.ac.uk/~ceesmm1/societies/mytv/channels/channel4.html";

        private static readonly String[] channels = { channel0, channel1, channel2, channel3, channel4 };

        private KinectSensorChooser sensorChooser;

        //activity feed variables
        //private ArrayList activities;

        #endregion variables

        #region window
        public MainWindow()
        {
            //redirect console output
            try
            {
                String userProfile = System.Environment.GetEnvironmentVariable("USERPROFILE");
                String directory = userProfile + @"\societies\myTvLogs.txt";
                ostrm = new FileStream(directory, FileMode.Create, FileAccess.Write);
                writer = new StreamWriter(ostrm);
                writer.AutoFlush = true;
                Console.SetOut(writer);
            }
            catch (Exception e)
            {
                Console.WriteLine("Cannot open logs.txt for writing");
                Console.WriteLine(e.Message);
                //return;
            }

            try
            {
                //initialise GUI
                //using (StreamWriter writer = new StreamWriter("logs.txt", true))
                //{
                //    writer.WriteLine("Initialising GUI");
                //}
                Console.WriteLine("Initialising GUI");
                InitializeComponent();
                tvBrowser.Navigated += new NavigatedEventHandler(tvBrowser_Navigated);

                //load button images
                channelBg_deselected = new ImageBrush(getImageSourceFromResource("MyTvUI", "Images/channel_background.png"));
                channelBg_selected = new ImageBrush(getImageSourceFromResource("MyTvUI", "Images/channel_background_selected.png"));
                offBg_deselected = new ImageBrush(getImageSourceFromResource("MyTvUI", "Images/off_button.png"));
                offBg_selected = new ImageBrush(getImageSourceFromResource("MyTvUI", "Images/off_button_selected.png"));
                muteBg_deselected = new ImageBrush(getImageSourceFromResource("MyTvUI", "Images/volume_down.png"));
                muteBg_selected = new ImageBrush(getImageSourceFromResource("MyTvUI", "Images/volume_down_selected.png"));
                unmuteBg_deselected = new ImageBrush(getImageSourceFromResource("MyTvUI", "Images/volume_up.png"));
                unmuteBg_selected = new ImageBrush(getImageSourceFromResource("MyTvUI", "Images/volume_up_selected.png"));

                //initialise GUI settings
                //channel = 0
                //offButton.Fill = offBg_selected;
                //mute = true
                //volumeDown.Fill = muteBg_selected;

                //initialise socket server to listen for service client connections
                Console.WriteLine("Initialising SocketServer and SocketClient");
                if (initialiseSocketServer() && initialiseSocketClient())
                {
                    commsInitialised = true;
                    //get preferences
                    Console.WriteLine("Initialising personalisable parameters");
                    this.userID = socketClient.getUserID();
                    //if EMMA -> getPreferences
                    //if ARTHUR -> getUserIntent
                    //if(this.userID.Equals("emma.societies.local.macs.hw.ac.uk"))
                    //{
                    Console.WriteLine("Getting preferences for user: " + userID);
                    initialisePreferences();
                    //}else if(this.userID.Equals("arthur.societies.local.macs.hw.ac.uk"))
                    //{
                    //  Console.WriteLine("Getting intent for user: "+userID);
                    //  initialiseIntent();
                    //}

                }

                //initialise activity feeds
                ArrayList activities = new ArrayList();
                listBox1.ItemsSource = activities;
                ActivityFeedManager afMgr = new ActivityFeedManager(activities);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }

            this.sensorChooser = new KinectSensorChooser();
            this.sensorChooser.KinectChanged += SensorChooserOnKinectChanged;
            this.sensorChooserUi.KinectSensorChooser = this.sensorChooser;

        }

        //when window loaded
        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            this.sensorChooser.Start();
        }

        //close window
        private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            Console.WriteLine("Window closing called");

            if (commsInitialised)
            {
                Console.WriteLine("Send GUI closing message to service client");
                String response = socketClient.sendMessage(
                           "START_MSG\n" +
                           "GUI_STOPPED\n" +
                           "END_MSG\n");
                if (response.Contains("RECEIVED"))
                {
                    Console.WriteLine("Service client received shutdown message");
                }
            }

            Console.WriteLine("Stopping kinect");
            closing = true;

            if (this.sensorChooser != null)
            {
                // //log.Debug("Stopping Kinect");
                this.sensorChooser.Stop();

                if (this.sensorChooser.Kinect != null)
                    UnbindSensor(this.sensorChooser.Kinect);
            }
            Console.WriteLine("Stopping sockets");
            //stop sockets
            if (commsInitialised)
            {
                socketClient.disconnect();
                socketServer.stopSocketServer();
            }

            Console.WriteLine("Flushing logs");
            if (writer != null)
            {
                Console.SetOut(oldOut);
                writer.Close();
                ostrm.Close();
            }
        }
        #endregion window

        #region serviceactions
        private Boolean setChannel(int channel)
        {
            Console.WriteLine("Setting channel to " + channel);
            tvBrowser.Navigate(channel1);
            //channel1Button.Fill = channelBg_selected;
            //channel2Button.Fill = channelBg_deselected;
            //channel3Button.Fill = channelBg_deselected;
            //channel4Button.Fill = channelBg_deselected;
            //offButton.Fill = offBg_deselected;
            if (commsInitialised)
            {
                Console.WriteLine("Sending channel " + channel + " action to UAM");
                String response = socketClient.sendMessage(
                "START_MSG\n" +
                "USER_ACTION\n" +
                "channel\n" +
                "1\n" +
                "END_MSG\n");
                if (response.Contains("RECEIVED"))
                {
                    Console.WriteLine("UAM received channel " + channel + " action");
                    //set channel button backgrounds
                    currentChannel = channel;
                }
            }
            return true;
        }

        private Boolean setDefaultChannel()
        {
            return setChannel(0);
        }

        private Boolean setDefaultVolume()
        {
            Console.WriteLine("Setting muted to default - true");
            MMDeviceEnumerator devEnum = new MMDeviceEnumerator();
            MMDevice defaultDevice = devEnum.GetDefaultAudioEndpoint(EDataFlow.eRender, ERole.eMultimedia);
            defaultDevice.AudioEndpointVolume.Mute = true;
            volumeDown.Fill = muteBg_selected;
            volumeUp.Fill = unmuteBg_deselected;
            currentlyMuted = true;
            return true;
        }

        private Boolean setMute()
        {
            Console.WriteLine("Setting muted");
            //mute volume
            MMDeviceEnumerator devEnum = new MMDeviceEnumerator();
            MMDevice defaultDevice = devEnum.GetDefaultAudioEndpoint(EDataFlow.eRender, ERole.eMultimedia);
            defaultDevice.AudioEndpointVolume.Mute = true;

            volumeDown.Fill = muteBg_selected;
            volumeUp.Fill = unmuteBg_deselected;

            if (commsInitialised)
            {
                Console.WriteLine("Sending muted action to UAM");
                String response = socketClient.sendMessage(
               "START_MSG\n" +
               "USER_ACTION\n" +
               "muted\n" +
               "true\n" +
               "END_MSG\n");
                if (response.Contains("RECEIVED"))
                {
                    Console.WriteLine("UAM received muted action");
                    //set mute button backgrounds
                    currentlyMuted = true;
                }
            }
            return true;
        }

        private Boolean setUnMute()
        {
            Console.WriteLine("Setting unmuted");
            //unmute volume
            MMDeviceEnumerator devEnum = new MMDeviceEnumerator();
            MMDevice defaultDevice = devEnum.GetDefaultAudioEndpoint(EDataFlow.eRender, ERole.eMultimedia);
            defaultDevice.AudioEndpointVolume.Mute = false;

            volumeDown.Fill = muteBg_deselected;
            volumeUp.Fill = unmuteBg_selected;

            if (commsInitialised)
            {
                Console.WriteLine("Sending unmuted action to UAM");
                String response = socketClient.sendMessage(
                "START_MSG\n" +
                "USER_ACTION\n" +
                "muted\n" +
                "false\n" +
                "END_MSG\n");
                if (response.Contains("RECEIVED"))
                {
                    Console.WriteLine("UAM received unmuted action");
                    //set mute button backgrounds
                    currentlyMuted = false;
                }
            }
            return true;
        }

        private void initialisePreferences()
        {
            //set channel
            Console.WriteLine("Requesting channel preference");
            String prefRequest = "START_MSG\n" +
                    "CHANNEL_PREFERENCE_REQUEST\n" +
                    "END_MSG\n";

            String channelPref = socketClient.sendMessage(prefRequest).Trim();
            Console.WriteLine("Got channel preference: [" + channelPref + "]");
            if (channelPref.Equals("1"))
            {
                Console.WriteLine("Personalising to channel 1");
                setChannel(1);
            }
            else if (channelPref.Equals("2"))
            {
                Console.WriteLine("Personalising to channel 2");
                setChannel(2);
            }
            else if (channelPref.Equals("3"))
            {
                Console.WriteLine("Personalising to channel 3");
                setChannel(3);
            }
            else if (channelPref.Equals("4"))
            {
                Console.WriteLine("Personalising to channel 4");
                setChannel(4);
            }
            else if (channelPref.Equals("0"))
            {
                Console.WriteLine("Personalising to channel 0");
                setChannel(0);
            }
            else  //default channel is 0
            {
                setDefaultChannel();
            }
            //MessageBox.Show("Got channel preference");

            //set muted
            Console.WriteLine("Requesting mute preference");
            String muteRequest = "START_MSG\n" +
                    "MUTED_PREFERENCE_REQUEST\n" +
                    "END_MSG\n";

            String mutedPref = socketClient.sendMessage(muteRequest).Trim();
            Console.WriteLine("Got mute preference: [" + mutedPref + "]");
            if (mutedPref.Equals("false"))
            {
                //unmute tv
                Console.WriteLine("Personalising volume to unmuted");
                setUnMute();
            }
            else if (mutedPref.Equals("true"))
            {
                Console.WriteLine("Personalising volume to muted");
                setMute();
            }
            else  //default state is muted
            {
                setDefaultVolume();
            }
            //MessageBox.Show("Got muted preference");
        }

        private void initialiseIntent()
        {
            //set channel
            Console.WriteLine("Requesting channel intent");
            String intentRequest = "START_MSG\n" +
                    "CHANNEL_INTENT_REQUEST\n" +
                    "END_MSG\n";

            String channelIntent = socketClient.sendMessage(intentRequest);
            Console.WriteLine("Got channel intent :" + channelIntent);
            if (channelIntent.Equals("1"))
            {
                setChannel(1);
            }
            else if (channelIntent.Equals("2"))
            {
                setChannel(2);
            }
            else if (channelIntent.Equals("3"))
            {
                setChannel(3);
            }
            else if (channelIntent.Equals("4"))
            {
                setChannel(4);
            }
            else  //default channel is 0
            {
                setChannel(0);
            }

            //set muted
            Console.WriteLine("Requesting mute preference");
            String muteRequest = "START_MSG\n" +
                    "MUTED_INTENT_REQUEST\n" +
                    "END_MSG\n";

            String mutedIntent = socketClient.sendMessage(muteRequest);
            Console.WriteLine("Got muted intent: " + mutedIntent);
            if (mutedIntent.Equals("false"))
            {
                //unmute tv
                setUnMute();
            }
            else  //default state is muted
            {
                //mute tv
                setMute();
            }
        }

        #endregion serviceactions

        /*
         * Perhaps won't be used if dynamic personalisation not applied
         */
        #region preference updates
        //public int setChannelParameter(int channel)
        //{
        //    int result;
        //    switch (channel)
        //    {
        //        case 0: 
        //            tvBrowser.Navigate(channel0);
        //            result = 0;
        //            break;
        //        case 1: 
        //            tvBrowser.Navigate(channel1);
        //            result = 1;
        //            break;
        //        case 2: 
        //            tvBrowser.Navigate(channel2);
        //            result = 2;
        //            break;
        //        case 3: 
        //            tvBrowser.Navigate(channel3);
        //            result = 3;
        //            break;
        //        case 4: 
        //            tvBrowser.Navigate(channel4);
        //            result = 4;
        //            break;
        //        default:
        //            tvBrowser.Navigate(channel0);
        //            result = 0;
        //            break;
        //    }
        //    return result;
        //}

        //public Boolean setMutedParameter(Boolean muted)
        //{
        //    Boolean result;
        //    if (muted)
        //    {
        //        //mute volume
        //        result = true;
        //    }
        //    else
        //    {
        //        //unmute volume
        //        result = false;
        //    }
        //    return result;
        //}
        #endregion preference updates

        #region sockets
        private Boolean initialiseSocketServer()
        {
            try
            {
                socketServer = new SocketServer(this);
                Thread serverThread = new Thread(new ThreadStart(socketServer.run));
                serverThread.Start();
            }
            catch (Exception e)
            {
                Console.WriteLine("Error initialising SocketServer");
                Console.WriteLine(e.ToString());
                return false;
            }
            return true;
        }

        private Boolean initialiseSocketClient()
        {
            socketClient = new SocketClient();
            if (socketClient.getSessionParameters())
            {
                userID = socketClient.getUserID();
                Console.WriteLine("Received user identity: " + userID);

                //send handshake message with GUI IP address
                String myIP = this.getLocalIPAddress();
                if (myIP != null)
                {
                    Console.WriteLine("Starting handshake");
                    Console.WriteLine("Sending service client my local IP address: " + myIP);
                    if (socketClient.connect())
                    {
                        String response = socketClient.sendMessage(
                        "START_MSG\n" +
                        "GUI_STARTED\n" +
                        myIP + "\n" +
                        "END_MSG\n");
                        if (response.Contains("RECEIVED"))
                        {
                            Console.WriteLine("Handshake complete");
                            return true;
                        }
                        else
                        {
                            Console.WriteLine("Handshake failed");
                        }
                    }
                    else
                    {
                        Console.WriteLine("Could not connect to service client");
                    }
                }
                else
                {
                    Console.WriteLine("Error - could not get IP address of local machine");
                }
            }
            else
            {
                Console.WriteLine("Error - could not get session parameters - userID, endpoint IP and port");
            }
            return false;
        }
        #endregion sockets

        #region " Kinect Events "

        /// <summary>
        /// Called when the KinectSensorChooser gets a new sensor
        /// </summary>
        /// <param name="sender">sender of the event</param>
        /// <param name="args">event arguments</param>
        private void SensorChooserOnKinectChanged(object sender, KinectChangedEventArgs args)
        {
            //log.Debug("Kinect sensor changed");

            if (args.OldSensor != null)
            {
                //log.Debug("Unbinding old sensor");

                try
                {
                    KinectSensor oldSensor = args.OldSensor;

                    UnbindSensor(oldSensor);

                    //log.Debug("Completed unbinding old sensor");
                }
                catch (InvalidOperationException ex)
                {
                    // KinectSensor might enter an invalid state while enabling/disabling streams or stream features.
                    // E.g.: sensor might be abruptly unplugged.
                    //log.Warn("Error unbinding old sensor", ex);
                }
            }

            if (args.NewSensor != null)
            {
                //log.Debug("Binding new sensor");

                try
                {
                    KinectSensor newSensor = args.NewSensor;

                    BindSensor(newSensor);

                    //log.Debug("Completed binding new sensor");
                }
                catch (InvalidOperationException ex)
                {
                    // KinectSensor might enter an invalid state while enabling/disabling streams or stream features.
                    // E.g.: sensor might be abruptly unplugged.
                    //log.Warn("Error binding new sensor", ex);
                }

            }
        }

        private static void BindSensor(KinectSensor sensor)
        {
            if (sensor == null)
                return;

            //log.Debug("BindSensor()");
            // Sensor
            sensor.Start();

            sensor.DepthStream.Enable(DepthImageFormat.Resolution640x480Fps30);

            try
            {
#if DEBUG
                // near mode for debug
                sensor.DepthStream.Range = DepthRange.Near;
#endif

                // NB the skeleton stream is used to track the silhouette of the player on all pages
                sensor.SkeletonStream.EnableTrackingInNearRange = true;
                //sensor.SkeletonStream.TrackingMode = SkeletonTrackingMode.Seated;
                // Turn on the skeleton stream to receive skeleton frames
                sensor.SkeletonStream.Enable();
            }
            catch (InvalidOperationException ex)
            {
                // Non Kinect for Windows devices do not support Near mode, so reset back to default mode.
                sensor.DepthStream.Range = DepthRange.Default;
                sensor.SkeletonStream.EnableTrackingInNearRange = false;
                //log.Warn("Error Setting depth range to near mode", ex);
            }
        }

        private static void UnbindSensor(KinectSensor sensor)
        {
            if (sensor == null)
                return;

            //log.Debug("UnbindSensor()");

            sensor.DepthStream.Range = DepthRange.Default;
            sensor.SkeletonStream.EnableTrackingInNearRange = false;
            sensor.DepthStream.Disable();
            sensor.SkeletonStream.Disable();
            sensor.AudioSource.Stop();
            sensor.Stop();
        }

        #endregion

        #region " Button click handlers "

        private void channelButtonClick(object sender, RoutedEventArgs e)
        {
            if (sender == channel1Button)
                setChannel(1);
            else if (sender == channel2Button)
                setChannel(2);
            else if (sender == channel3Button)
                setChannel(3);
            else if (sender == channel4Button)
                setChannel(4);
        }

        private void offButtonClick(object sender, RoutedEventArgs e)
        {

        }

        private void exitButtonClick(object sender, RoutedEventArgs e)
        {
            Environment.Exit(0x00);
        }

        private void volumeUpButtonClick(object sender, RoutedEventArgs e)
        {
            setUnMute();
        }

        private void volumeDownButtonClick(object sender, RoutedEventArgs e)
        {
            setMute();
        }

        #endregion

        #region additional

        internal static ImageSource getImageSourceFromResource(string psAssemblyName, string psResourceName)
        {
            Uri oUri = new Uri("pack://application:,,,/" + psAssemblyName + ";component/" + psResourceName, UriKind.RelativeOrAbsolute);
            return BitmapFrame.Create(oUri);
        }
        
        private void tvBrowser_WindowLoaded(object sender, RoutedEventArgs e)
        {
            //tvBrowser.Navigate("http://www.macs.hw.ac.uk/~ceesmm1/societies/mytv/channels/splashScreen.html");
        }

        void tvBrowser_Navigated(object sender, NavigationEventArgs e)
        {
            SetSilent(tvBrowser, true);
        }

        //Code to supress Java Script errors in web browser
        public static void SetSilent(WebBrowser browser, bool silent)
        {
            if (browser == null)
                throw new ArgumentNullException("browser");

            // get an IWebBrowser2 from the document
            IOleServiceProvider sp = browser.Document as IOleServiceProvider;
            if (sp != null)
            {
                Guid IID_IWebBrowserApp = new Guid("0002DF05-0000-0000-C000-000000000046");
                Guid IID_IWebBrowser2 = new Guid("D30C1661-CDAF-11d0-8A3E-00C04FC9E26E");

                object webBrowser;
                sp.QueryService(ref IID_IWebBrowserApp, ref IID_IWebBrowser2, out webBrowser);
                if (webBrowser != null)
                {
                    webBrowser.GetType().InvokeMember("Silent", BindingFlags.Instance | BindingFlags.Public | BindingFlags.PutDispProperty, null, webBrowser, new object[] { silent });
                }
            }
        }

        [ComImport, Guid("6D5140C1-7436-11CE-8034-00AA006009FA"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
        private interface IOleServiceProvider
        {
            [PreserveSig]
            int QueryService([In] ref Guid guidService, [In] ref Guid riid, [MarshalAs(UnmanagedType.IDispatch)] out object ppvObject);
        }

        private String getLocalIPAddress()
        {
            string localIP = null;
            IPHostEntry host = Dns.GetHostEntry(Dns.GetHostName());
            foreach (IPAddress ip in host.AddressList)
            {
                if (ip.AddressFamily == AddressFamily.InterNetwork)
                {
                    localIP = ip.ToString();
                    break;
                }
            }
            return localIP;
        }

        private void mytvWindow_Closed(object sender, EventArgs e)
        {
            Console.WriteLine("Window closed called");
        }

        #endregion additional



    }

}
