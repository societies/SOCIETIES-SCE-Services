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
using System.Windows.Forms;
using System.Windows.Forms.Integration;
using CoreAudioApi;
using Microsoft.Kinect;
using Microsoft.Kinect.Toolkit;
using Microsoft.Kinect.Toolkit.Controls;
using System.Windows.Data;
using System.Collections.Generic;
using Newtonsoft.Json;
using System.Windows.Media.Animation;

namespace MyTvUI
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
       // private static readonly log4net.ILog log = log4net.LogManager.GetLogger(typeof(MainWindow));

        #region variables
        //user variables
        private String userID;
        private int currentChannel = 0;
        private Boolean currentlyMuted = true;
        System.Windows.Forms.WebBrowser tvBrowser;

        //socket variables
        private SocketClient socketClient;
        private SocketServer socketServer;
        private Boolean commsInitialised = false;
        private Boolean showActivities = false;

        //window variables
        private bool closing = false;

        //console variables
        FileStream ostrm;
        StreamWriter writer;
        TextWriter oldOut = Console.Out;

        //button variables
        private ImageBrush channelBg_deselected;
        private ImageBrush channelBg_selected;
        private ImageBrush offBg_deselected;
        private ImageBrush offBg_selected;
        private ImageBrush muteBg_deselected;
        private ImageBrush muteBg_selected;
        private ImageBrush unmuteBg_deselected;
        private ImageBrush unmuteBg_selected;


        //Channel constants

        //private string[] channels = new string { };
        private static readonly String channel0 = "http://www2.macs.hw.ac.uk/~im143/mytv/splashScreen.html";
        private static readonly String channel1 = "http://www2.macs.hw.ac.uk/~sww2/societies/bbc_news.html";
        private static readonly String channel2 = "http://www2.macs.hw.ac.uk/~sww2/societies/bbc_one.html";
        private static readonly String channel3 = "http://www2.macs.hw.ac.uk/~sww2/societies/bbc_two.html";
        private static readonly String channel4 = "http://www2.macs.hw.ac.uk/~sww2/societies/cbbc.html";

        private static readonly String[] channels = { channel0, channel1, channel2, channel3, channel4 };

        public KinectSensorChooser sensorChooser { get; private set; }

        List<MarshaledActivity> activities; //= new List<ActivityList>();

        //activity feed variables
        //private ArrayList activities;

        #endregion variables

        #region window
        public MainWindow()
            : base()
        {
            this.activities = new List<MarshaledActivity>();
            Closing += Window_Closing;

            //redirect console output
             try
            {
                //initialise GUI
                Console.WriteLine(DateTime.Now + "\t" +"Initialising GUI!!!!");
                InitializeComponent();

               // setChannel(0);
             //   tvBrowser.Navigated += new NavigatedEventHandler(tvBrowser_Navigated);


                // askFreeNav();
                //webView.InvokeScript("execScript", new Object[] {"document.body.style.overflow ='hidden'", "JavaScript"});

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
                Console.WriteLine(DateTime.Now + "\t" +"Initialising SocketServer and SocketClient");
                if (initialiseSocketServer() && initialiseSocketClient())
                {
                    commsInitialised = true;
                    //get preferences
                    Console.WriteLine(DateTime.Now + "\t" +"Initialising personalisable parameters");
                    this.userID = socketClient.getUserID();
                    //if EMMA -> getPreferences
                    //if ARTHUR -> getUserIntent
                    //if(this.userID.Equals("emma.societies.local.macs.hw.ac.uk"))
                    //{
                    
                    //}else if(this.userID.Equals("arthur.societies.local.macs.hw.ac.uk"))
                    //{
                    //  Console.WriteLine(DateTime.Now + "\t" +"Getting intent for user: "+userID);
                    //  initialiseIntent();
                    //}
                   // getActivities();

                }
            }
            catch (Exception e)
            {
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
            }

            Console.WriteLine(DateTime.Now + "\t" +"Init Kinect sensor");
            // initialize the sensor chooser and UI
            this.sensorChooser = new KinectSensorChooser();
            this.sensorChooser.KinectChanged += SensorChooserOnKinectChanged;
            this.sensorChooserUi.KinectSensorChooser = this.sensorChooser;

            ////log.Warn("Sensor auto-start is disabled during debug");
            //this.SocietiesLogo.MouseUp += new System.Windows.Input.MouseButtonEventHandler(SocietiesLogo_MouseDoubleClick);
            this.sensorChooser.Start();
         
           
            // Bind the sensor chooser's current sensor to the KinectRegion
            System.Windows.Data.Binding regionSensorBinding = new System.Windows.Data.Binding("Kinect") { Source = this.sensorChooser };
            BindingOperations.SetBinding(this.kinectRegion, KinectRegion.KinectSensorProperty, regionSensorBinding);

            Console.WriteLine(DateTime.Now + "\t" + "Getting preferences for user: " + userID);
            //  getActivities();
            //  displayActivities();
           


        }

      /*  public void tryThis() {
        System.Windows.Forms.Integration.WindowsFormsHost host =
       new System.Windows.Forms.Integration.WindowsFormsHost();
        System.Windows.Forms.WebBrowser webB = new System.Windows.Forms.WebBrowser();
        webB.ScriptErrorsSuppressed = true;
        webB.Navigate("http://ec2-54-218-113-176.us-west-2.compute.amazonaws.com/AF-S/mytvscreen.html");
        host.Child = webB;
        host.Width = 200;
        host.Height = 450;
        Thickness margin = host.Margin;
            margin.Top= -60;
            host.Margin = margin;
        dockPanel1.Children.Add(host);
         //   <WebBrowser Margin="0, -60, 0, 0"  Height="450" Name="webView" Width="200" />
    }



        private void dockPanel1_Loaded_1(object sender, RoutedEventArgs e)
        {
            tryThis();
        }

        private void askFreeNav()
        {
            dockPanel1.Children.RemoveAt(0);
            tryThis();
        }*/

        //when window loaded
        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            this.sensorChooser.Start();
            Console.WriteLine(DateTime.Now + "\t" + "In webloading handle");
            System.Windows.Forms.Integration.WindowsFormsHost host =
           new System.Windows.Forms.Integration.WindowsFormsHost();
            this.tvBrowser = new System.Windows.Forms.WebBrowser();
            tvBrowser.ScriptErrorsSuppressed = true;
            tvBrowser.ScrollBarsEnabled = false;
            tvBrowser.Navigate(channel0);
            host.Child = tvBrowser;
            host.Width = 737;
            host.Height = 418;
            Canvas.SetLeft(host, 71);
            Canvas.SetTop(host, 10);
            canvas1.Children.Add(host);

            initialisePreferences();

            String[] InterNetCache = System.IO.Directory.GetFiles(Environment.GetFolderPath(Environment.SpecialFolder.InternetCache));
            foreach (String s in InterNetCache) {
                Console.WriteLine(DateTime.Now + "\t" + "Deleting cache of IE at " + s);
                File.Delete(s);
            }
        }

        //close window
        private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            Console.WriteLine(DateTime.Now + "\t" +"Window closing called");

            if (commsInitialised)
            {
                Console.WriteLine(DateTime.Now + "\t" +"Send GUI closing message to service client");
                String response = socketClient.sendMessage(
                           "START_MSG\n" +
                           "GUI_STOPPED\n" +
                           "END_MSG\n");
                if (response.Contains("RECEIVED"))
                {
                    Console.WriteLine(DateTime.Now + "\t" +"Service client received shutdown message");
                }
            }

            Console.WriteLine(DateTime.Now + "\t" +"Stopping kinect");
            closing = true;

            if (this.sensorChooser != null)
            {
                // //log.Debug("Stopping Kinect");
                this.sensorChooser.Stop();

                if (this.sensorChooser.Kinect != null)
                    UnbindSensor(this.sensorChooser.Kinect);
            }
            Console.WriteLine(DateTime.Now + "\t" +"Stopping sockets");
            //stop sockets
            if (commsInitialised)
            {
                socketClient.disconnect();
                socketServer.stopSocketServer();
            }            

        }
        #endregion window

        #region serviceactions
        private Boolean setChannel(int channel)
        {
            Console.WriteLine(DateTime.Now + "\t" +"Setting channel to " + channel);
            switch (channel)
            {
                case 0:
                    Console.WriteLine(DateTime.Now + "\t" + "Loading URL " + channel0);
                    tvBrowser.Navigate(channel0);
                    break;
                case 1 :
                    Console.WriteLine(DateTime.Now + "\t" + "Loading URL " + channel1);
                    tvBrowser.Navigate(channel1);
                    break;
                case 2:
                    Console.WriteLine(DateTime.Now + "\t" + "Loading URL " + channel2);
                    tvBrowser.Navigate(channel2);
                    break;
                case 3:
                    Console.WriteLine(DateTime.Now + "\t" + "Loading URL " + channel3);
                    tvBrowser.Navigate(channel3);
                    break;
                case 4:
                    Console.WriteLine(DateTime.Now + "\t" + "Loading URL " + channel4);
                    tvBrowser.Navigate(channel4);
                    break;
                 default: 
                    Console.WriteLine(DateTime.Now + "\t We have a channel we dont know what to do with");
                    break;

            }
            //channel1Button.Fill = channelBg_selected;
            //channel2Button.Fill = channelBg_deselected;
            //channel3Button.Fill = channelBg_deselected;
            //channel4Button.Fill = channelBg_deselected;
            //offButton.Fill = offBg_deselected;
            
            return true;
        }

        private Boolean setDefaultChannel()
        {
            return setChannel(0);
        }

    /*    private Boolean setDefaultVolume()
        {
            Console.WriteLine(DateTime.Now + "\t" +"Setting muted to default - true");
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
            Console.WriteLine(DateTime.Now + "\t" +"Setting muted");
            //mute volume
            MMDeviceEnumerator devEnum = new MMDeviceEnumerator();
            MMDevice defaultDevice = devEnum.GetDefaultAudioEndpoint(EDataFlow.eRender, ERole.eMultimedia);
            defaultDevice.AudioEndpointVolume.Mute = true;

            volumeDown.Fill = muteBg_selected;
            volumeUp.Fill = unmuteBg_deselected;

            if (commsInitialised)
            {
                Console.WriteLine(DateTime.Now + "\t" +"Sending muted action to UAM");
                String response = socketClient.sendMessage(
               "START_MSG\n" +
               "USER_ACTION\n" +
               "muted\n" +
               "true\n" +
               "END_MSG\n");
                if (response.Contains("RECEIVED"))
                {
                    Console.WriteLine(DateTime.Now + "\t" +"UAM received muted action");
                    //set mute button backgrounds
                    currentlyMuted = true;
                }
            }
            return true;
        }

        private Boolean setUnMute()
        {
            Console.WriteLine(DateTime.Now + "\t" +"Setting unmuted");
            //unmute volume
            MMDeviceEnumerator devEnum = new MMDeviceEnumerator();
            MMDevice defaultDevice = devEnum.GetDefaultAudioEndpoint(EDataFlow.eRender, ERole.eMultimedia);
            defaultDevice.AudioEndpointVolume.Mute = false;

            volumeDown.Fill = muteBg_deselected;
            volumeUp.Fill = unmuteBg_selected;

            if (commsInitialised)
            {
                Console.WriteLine(DateTime.Now + "\t" +"Sending unmuted action to UAM");
                String response = socketClient.sendMessage(
                "START_MSG\n" +
                "USER_ACTION\n" +
                "muted\n" +
                "false\n" +
                "END_MSG\n");
                if (response.Contains("RECEIVED"))
                {
                    Console.WriteLine(DateTime.Now + "\t" +"UAM received unmuted action");
                    //set mute button backgrounds
                    currentlyMuted = false;
                }
            }
            return true;
        } */

      //  public delegate void updateTheActivities(List<MarshaledActivity> activities);
        public void updateActivities(List<MarshaledActivity> activities)
        {
            Console.WriteLine(DateTime.Now + "\t" + "I have recieved activities! " + activities.Count.ToString());
            activityGrid.Dispatcher.Invoke(new displayTheActivities(this.displayActivities), new object[] {activities});
            //displayActivities(activities);
        
        }

        public void fadeIn()
        {
            DoubleAnimation fadeInAnimation = new DoubleAnimation(0, 1, new Duration(TimeSpan.FromSeconds(1.5)));
            Storyboard.SetTarget(fadeInAnimation, activityGrid);
            Storyboard.SetTargetProperty(fadeInAnimation, new PropertyPath(UIElement.OpacityProperty));
            Storyboard sb = new Storyboard();
            sb.Children.Add(fadeInAnimation);
            sb.Begin();
        }

        public void fadeOut()
        {
            DoubleAnimation fadeInAnimation = new DoubleAnimation(1, 0, new Duration(TimeSpan.FromSeconds(1.5)));
            Storyboard.SetTarget(fadeInAnimation, activityGrid);
            Storyboard.SetTargetProperty(fadeInAnimation, new PropertyPath(UIElement.OpacityProperty));
            Storyboard sb = new Storyboard();
            sb.Children.Add(fadeInAnimation);
            fadeInAnimation.Completed += ani_Completed; 
            sb.Begin();
        }

        void ani_Completed(object sender, EventArgs e)
        {
            if (showActivities)
            {

                doActivities();
            }
        }

        private void doActivities()
        {

            //CLEAR EXISITING ACTIVITIES
            activityGrid.Children.Clear();

            int counter = 0;

            

           
            Grid textGrid;
            Boolean isTarget;
            foreach (MarshaledActivity act in this.activities)
            {
                Border border = new Border();
                border.Margin = new Thickness(5, 0, 0, 0);
            border.BorderThickness = new Thickness(0, 1, 0, 0);
            border.BorderBrush = Brushes.DarkOrange;
                //CHECK ACTIVITIY HAS A TARGET
                TextBlock activityText = new TextBlock();
                activityText.Margin = new Thickness(-200, 5, 0, 5);
                activityText.Width = 200;
                activityText.TextWrapping = TextWrapping.Wrap;
                String text = act.Object + ": " + act.verb;
                text = text + '\n';
                if (act.target != null && act.target.Trim().Length > 0)
                {
                    text = act.target + '\n';
                }
                text = text + act.actor + '\n';
                text = text + act.published;
                activityText.Text = text;
      
                border.Child = activityText;
                Grid.SetRow(border, counter);
                counter++;
                activityGrid.Children.Add(border);
                /* isTarget = false;
                 if(act.target!=null && act.target.Trim().Length>0) 
                 {
                     isTarget=true;

                 }
                 string actVerbText = act.Object + " " + act.verb;
                 int remainder = actVerbText.Length % 35;
                 int gridCount = actVerbText.Length / 35;
                 if(remainder>0) {
                     gridCount++;
                 }
                 Console.WriteLine(gridCount);
                 //SET UP BORDER
                

                 //NEW GRID FOR ACTIVITIY
                 textGrid = new Grid();

                 RowDefinition row1 = new RowDefinition();
                 RowDefinition row2 = new RowDefinition();
                 RowDefinition row3 = new RowDefinition();
                 RowDefinition row4 = new RowDefinition(); 

                 ColumnDefinition col1 = new ColumnDefinition();
                 col1.Width = new GridLength(350);


                 row1.Height = new GridLength(20*gridCount);
                 row2.Height = new GridLength(20);
                 row3.Height = new GridLength(20);
                 row4.Height = new GridLength(20);



                 textGrid.RowDefinitions.Add(row1);
                 textGrid.RowDefinitions.Add(row2);
                 textGrid.RowDefinitions.Add(row3);
                 textGrid.RowDefinitions.Add(row4);
                 textGrid.ColumnDefinitions.Add(col1);


                 int index = 0;

                 TextBlock actObjectVerb = new TextBlock();
                 //actObjectVerb.Margin = new Thickness(-150, 0, 0, 0);
                // actObjectVerb.Width = 200;
                
                 actObjectVerb.MaxWidth = 200;
                 actObjectVerb.TextAlignment = TextAlignment.Justify;
             //    actObjectVerb.Height = 20 * gridCount;
                 actObjectVerb.Text = actVerbText;
                // actObjectVerb.FontSize = 24;
                 actObjectVerb.TextWrapping = TextWrapping.Wrap;
                 //String text = act.Object + 

             //    sp.Children.Add(actObjectVerb);
                 //vb.Child = sp;
                 Grid.SetRow(actObjectVerb, index);
                     index++;
                     textGrid.Children.Add(actObjectVerb);


                 if (isTarget)
                 {
                     TextBlock actTarget = new TextBlock();
                     actTarget.Text = act.target;
                     Grid.SetRow(actTarget, index);
                     index++;
                     textGrid.Children.Add(actTarget);
                 }

                 TextBlock actActor = new TextBlock();
                 actActor.Text = act.actor;
                 Grid.SetRow(actActor, index);
                 index++;
                 textGrid.Children.Add(actActor);

                 TextBlock actPublished = new TextBlock();
                 actPublished.Text = act.published;
                 Grid.SetRow(actPublished, index);
                 textGrid.Children.Add(actPublished);    

                 Grid.SetRow(border, counter);


                 textGrid.Margin = new Thickness(2);
                 border.Margin = new Thickness(2);




                 border.Child = textGrid;

                 activityGrid.Children.Add(border);
                 counter++;
              */ 
             }
             fadeIn();


        }

        public delegate void displayTheActivities(List<MarshaledActivity> activities);
        private void displayActivities(List<MarshaledActivity> activities)
        {
            this.activities = activities;
            if (showActivities)
            {         
                fadeOut();
            }
        }

        /*private void getActivities()
        {
            Console.WriteLine(DateTime.Now + "\t" + "Requesting activities");
            String activityRequest = "START_MSG\n" +
                "ACTIVITY_FEED_REQUEST\n" +
                "END_MSG\n";
            String response = socketClient.sendMessage(activityRequest);
            Console.WriteLine(DateTime.Now + "\t" + "Got Response: " + response);
            activities = JsonConvert.DeserializeObject<List<ActivityList>>(response);
            Console.WriteLine(DateTime.Now + "\t" + "Recieved : " + activities.Count);
            
        }*/

        private void initialisePreferences()
        {
            //set channel
            Console.WriteLine(DateTime.Now + "\t" +"Requesting channel preference");
            String prefRequest = "START_MSG\n" +
                    "CHANNEL_PREFERENCE_REQUEST\n" +
                    "END_MSG\n";

            String channelPref = socketClient.sendMessage(prefRequest).Trim();
            Console.WriteLine(DateTime.Now + "\t" +"Got channel preference: [" + channelPref + "]");
            if (channelPref.Equals("1"))
            {
                Console.WriteLine(DateTime.Now + "\t" +"Personalising to channel 1");
                setChannel(1);
            }
            else if (channelPref.Equals("2"))
            {
                Console.WriteLine(DateTime.Now + "\t" +"Personalising to channel 2");
                setChannel(2);
            }
            else if (channelPref.Equals("3"))
            {
                Console.WriteLine(DateTime.Now + "\t" +"Personalising to channel 3");
                setChannel(3);
            }
            else if (channelPref.Equals("4"))
            {
                Console.WriteLine(DateTime.Now + "\t" +"Personalising to channel 4");
                setChannel(4);
            }
            else if (channelPref.Equals("0"))
            {
                Console.WriteLine(DateTime.Now + "\t" +"Personalising to channel 0");
                setChannel(0);
            }
            else  //default channel is 0
            {
                setDefaultChannel();
            }
            //MessageBox.Show("Got channel preference");

            //set muted
            Console.WriteLine(DateTime.Now + "\t" +"Requesting mute preference");
            String muteRequest = "START_MSG\n" +
                    "MUTED_PREFERENCE_REQUEST\n" +
                    "END_MSG\n";

            String mutedPref = socketClient.sendMessage(muteRequest).Trim();
            Console.WriteLine(DateTime.Now + "\t" +"Got mute preference: [" + mutedPref + "]");
            if (mutedPref.Equals("false"))
            {
                //unmute tv
                Console.WriteLine(DateTime.Now + "\t" +"Personalising volume to unmuted");
                setUnMute();
            }
            else if (mutedPref.Equals("true"))
            {
                Console.WriteLine(DateTime.Now + "\t" +"Personalising volume to muted");
                setMute();
            }
            else  //default state is muted
            {
                setMute();
            }

            String activityRequest = "START_MSG\n" +
                    "ACTIVITY_PREFERENCE_REQUEST\n" +
                    "END_MSG\n";

            String activityPref = socketClient.sendMessage(activityRequest).Trim();
            if (activityPref.Equals("on"))
            {
                showActivities = true;
                activityText.Text = "on";
                doActivities();
            }
            else
            {
                showActivities = false;
                activityText.Text = "off";
                fadeOut();
            }

            //MessageBox.Show("Got muted preference");*/
        }

        private void initialiseIntent()
        {
            //set channel
            Console.WriteLine(DateTime.Now + "\t" +"Requesting channel intent");
            String intentRequest = "START_MSG\n" +
                    "CHANNEL_INTENT_REQUEST\n" +
                    "END_MSG\n";

            String channelIntent = socketClient.sendMessage(intentRequest);
            Console.WriteLine(DateTime.Now + "\t" +"Got channel intent :" + channelIntent);
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
            Console.WriteLine(DateTime.Now + "\t" +"Requesting mute preference");
            String muteRequest = "START_MSG\n" +
                    "MUTED_INTENT_REQUEST\n" +
                    "END_MSG\n";

            String mutedIntent = socketClient.sendMessage(muteRequest);
            Console.WriteLine(DateTime.Now + "\t" +"Got muted intent: " + mutedIntent);
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
                Console.WriteLine(DateTime.Now + "\t" +"Error initialising SocketServer");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
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
                Console.WriteLine(DateTime.Now + "\t" +"Received user identity: " + userID);

                //send handshake message with GUI IP address
                String myIP = this.getLocalIPAddress();
                if (myIP != null)
                {
                    Console.WriteLine(DateTime.Now + "\t" +"Starting handshake");
                    Console.WriteLine(DateTime.Now + "\t" +"Sending service client my local IP address: " + myIP);
                    if (socketClient.connect())
                    {
                        String response = socketClient.sendMessage(
                        "START_MSG\n" +
                        "GUI_STARTED\n" +
                        myIP + "\n" +
                        "END_MSG\n");
                        if (response.Contains("RECEIVED"))
                        {
                            Console.WriteLine(DateTime.Now + "\t" +"Handshake complete");
                            return true;
                        }
                        else
                        {
                            Console.WriteLine(DateTime.Now + "\t" +"Handshake failed");
                        }
                    }
                    else
                    {
                        Console.WriteLine(DateTime.Now + "\t" +"Could not connect to service client");
                    }
                }
                else
                {
                    Console.WriteLine(DateTime.Now + "\t" +"Error - could not get IP address of local machine");
                }
            }
            else
            {
                Console.WriteLine(DateTime.Now + "\t" +"Error - could not get session parameters - userID, endpoint IP and port");
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
                    Console.WriteLine(DateTime.Now + "\t" +"Bound new kinect sensor");
                    //log.Debug("Completed binding new sensor");
                }
                catch (InvalidOperationException ex)
                {
                    // KinectSensor might enter an invalid state while enabling/disabling streams or stream features.
                    // E.g.: sensor might be abruptly unplugged.
                    Console.WriteLine(DateTime.Now + "\t" +"Error binding new sensor", ex);
                }

            }
        }

        private static void BindSensor(KinectSensor sensor)
        {
            Console.WriteLine(DateTime.Now + "\t" +"Binding sensor!");
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
              //  sensor.DepthStream.Range = DepthRange.Near;
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

        private void activity_buttonClick(object sender, RoutedEventArgs e)
        {
            Console.WriteLine(DateTime.Now + "\t" + "Activity button click!");
            showActivities = !showActivities;
            String result = "off";
            if (showActivities)
            {
                result = "on";
            }

            Console.WriteLine(DateTime.Now + "\t" + "Sending activity action to UAM "+result);
            String response = socketClient.sendMessage(
            "START_MSG\n" +
            "USER_ACTION\n" +
            "activity_feed\n" +
            result + "\n" +
            "END_MSG\n");
            if (response.Contains("RECEIVED"))
            {
                Console.WriteLine(DateTime.Now + "\t" + "UAM received activity action");
            }

            if (showActivities)
            {
                activityText.Text = "On";
                doActivities();
            }
            else
            {
                activityText.Text = "Off";
                fadeOut();
            }

        }
        

        private void channelButtonClick(object sender, RoutedEventArgs e)
        {
            if (sender == channel1Button)
            {
                setChannel(1);
                sendActionToPlatform(1);
            }
            else if (sender == channel2Button)
            {
                setChannel(2);
                sendActionToPlatform(2);
            }
            else if (sender == channel3Button)
            {
                setChannel(3);
                sendActionToPlatform(3);
            }
            else if (sender == channel4Button)
            {
                setChannel(4);
                sendActionToPlatform(4);
            }
        }

        private void sendActionToPlatform(int channel) {
            if (commsInitialised)
            {
                Console.WriteLine(DateTime.Now + "\t" + "Sending channel " + channel + " action to UAM");
                String response = socketClient.sendMessage(
                "START_MSG\n" +
                "USER_ACTION\n" +
                "channel\n" +
                channel + "\n" +
                "END_MSG\n");
                if (response.Contains("RECEIVED"))
                {
                    Console.WriteLine(DateTime.Now + "\t" + "UAM received channel " + channel + " action");
                    //set channel button backgrounds
                    currentChannel = channel;
                }
            }
        }

        private void clearWBButtonClick(object sender, RoutedEventArgs e)
        {
          //  askFreeNav();
        }

        

        private void offButtonClick(object sender, RoutedEventArgs e)
        {
            setChannel(0);
            sendActionToPlatform(0);
        }

        private void exitButtonClick(object sender, RoutedEventArgs e)
        {
            Close();
        }

        private void volumeUpButtonClick(object sender, RoutedEventArgs e)
        {
            setUnMute();
            if (commsInitialised)
            {
                Console.WriteLine(DateTime.Now + "\t" + "Sending unmute action to UAM");
                String response = socketClient.sendMessage(
                "START_MSG\n" +
                "USER_ACTION\n" +
                "muted\n" +
                "false\n" +
                "END_MSG\n");
                if (response.Contains("RECEIVED"))
                {
                    Console.WriteLine(DateTime.Now + "\t" + "UAM received channel unmute action");
                }
            }
        }

        private void volumeDownButtonClick(object sender, RoutedEventArgs e)
        {
            setMute();
            if (commsInitialised)
            {
                Console.WriteLine(DateTime.Now + "\t" + "Sending mute action to UAM");
                String response = socketClient.sendMessage(
                "START_MSG\n" +
                "USER_ACTION\n" +
                "muted\n" +
                "true\n" +
                "END_MSG\n");
                if (response.Contains("RECEIVED"))
                {
                    Console.WriteLine(DateTime.Now + "\t" + "UAM received channel mute action");
                }
            }
        }

        #endregion

        #region additional

        internal static ImageSource getImageSourceFromResource(string psAssemblyName, string psResourceName)
        {
            Uri oUri = new Uri("pack://application:,,,/" + psAssemblyName + ";component/" + psResourceName, UriKind.RelativeOrAbsolute);
            return BitmapFrame.Create(oUri);
        }
        
    //    private void tvBrowser_WindowLoaded(object sender, RoutedEventArgs e)
       // {
     //       //tvBrowser.Navigate("http://www.macs.hw.ac.uk/~ceesmm1/societies/mytv/channels/splashScreen.html");
    //    }

     //   void tvBrowser_Navigated(object sender, NavigationEventArgs e)
     //   {
     //       SetSilent(tvBrowser, true);
    //    }

        //Code to supress Java Script errors in web browser
        public static void SetSilent(System.Windows.Controls.WebBrowser browser, bool silent)
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
            Console.WriteLine(DateTime.Now + "\t" +"Window closed called");
        }

        #endregion additional

        private void canvas1_Loaded_1(object sender, RoutedEventArgs e)
        {


   // <WebBrowser Canvas.Left="71" Canvas.Top="10" Height="418" Name="tvBrowser" Width="737" Loaded="tvBrowser_WindowLoaded" Navigated="tvBrowser_Navigated" Cursor="None" DataContext="{Binding}" />

        }

         private Boolean setMute()
          {
             //log.Info("Setting muted");
          //Console.WriteLine("Setting muted");
              //mute volume
              MMDeviceEnumerator devEnum = new MMDeviceEnumerator();
              MMDevice defaultDevice = devEnum.GetDefaultAudioEndpoint(EDataFlow.eRender, ERole.eMultimedia); 
              defaultDevice.AudioEndpointVolume.Mute = true;
            /*  if (commsInitialised)
              {
              //log.Info("Sending muted action to UAM");
             //Console.WriteLine("Sending muted action to UAM");
                  String response = socketClient.sendMessage(
                 "START_MSG\n" +
                 "USER_ACTION\n" +
                 "END_MSG\n");
                  if (response.Contains("RECEIVED"))
                  {
                     //log.Info("UAM received muted action");
                     //Console.WriteLine("UAM received muted action");
                      //set mute button backgrounds
                      currentlyMuted = true;
                  }
              }*/
             return true;
         }


  
          private Boolean setUnMute()
          {
            //log.Info("Setting unmuted");
             //Console.WriteLine("Setting unmuted");
              //unmute volume
              MMDeviceEnumerator devEnum = new MMDeviceEnumerator();
              MMDevice defaultDevice = devEnum.GetDefaultAudioEndpoint(EDataFlow.eRender, ERole.eMultimedia);
               defaultDevice.AudioEndpointVolume.Mute = false;
  
           /*   if (commsInitialised)
              {
                 //log.Info("Sending unmuted action to UAM");
                //Console.WriteLine("Sending unmuted action to UAM");
                  String response = socketClient.sendMessage(
                  "START_MSG\n" +
                  "USER_ACTION\n" +
                  "END_MSG\n");
                  if (response.Contains("RECEIVED"))
                  {
                    //log.Info("UAM received unmuted action");
                   //Console.WriteLine("UAM received unmuted action");
                      //set mute button backgrounds
                      currentlyMuted = false;
                  }
              }*/
              return true;
        }
       




    }

}
