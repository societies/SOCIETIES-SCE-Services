using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Shapes;
using Microsoft.Kinect;
using Microsoft.Kinect.Toolkit;
using Microsoft.Kinect.Toolkit.Controls;

namespace HWUPortal
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        protected static log4net.ILog log = log4net.LogManager.GetLogger(typeof(MainWindow));

        private ServiceInfo runningService;
        private UserSession userSession;
        private readonly KinectCircleButton[] serviceButtons;

        #region sockets
        private SocketServer socketServer;
        private BinaryDataTransfer binaryDataTransferServer;
        private ServiceSocketServer serviceSocketServer;
        private Thread socketThread;
        private Thread binaryTransferThread;
        private Thread serviceSocketThread;
        #endregion sockets

        private ImageViewer iViewer;
        private bool loggedIn = false;

        private StandaloneAppControl standaloneAppControl;

        public KinectSensorChooser SensorChooser { get; private set; }

        public MainWindow()
        {
            Thread.CurrentThread.Name = "MainWindowThread";

            Console.WriteLine("Init components");
            this.InitializeComponent();

            this.webBrowser.Visibility = System.Windows.Visibility.Hidden;
            serviceButtons = new KinectCircleButton[] { this.serviceButton1, this.serviceButton2, this.serviceButton3, this.serviceButton4, this.serviceButton5 };

            foreach (KinectCircleButton button in serviceButtons)
            {
                button.Visibility = System.Windows.Visibility.Hidden;
                //button.Visible = false;
                //button.Enabled = false;
                button.IsEnabled = false;
            }

           // log.Error("Be sure to re-enable me #61");
            this.closeShowingServiceBtn.IsEnabled = false;
            this.closeShowingServiceBtn.Visibility = System.Windows.Visibility.Hidden;
            this.logoutButton.IsEnabled = false;
            this.logoutButton.Visibility = System.Windows.Visibility.Hidden;
            //log.Error("Be sure to re-enable me #67");
            this.Visibility = System.Windows.Visibility.Hidden;

            Console.WriteLine("Setup background threads");
            this.runningService = new ServiceInfo();
            this.binaryDataTransferServer = new BinaryDataTransfer(this);

            socketServer = new SocketServer(this);
            socketThread = new Thread(socketServer.run);
            socketThread.IsBackground = true;
            socketThread.Start();

            binaryDataTransferServer = new BinaryDataTransfer(this);
            binaryTransferThread = new Thread(binaryDataTransferServer.run);
            binaryTransferThread.IsBackground = true;
            binaryTransferThread.Start();

            serviceSocketServer = new ServiceSocketServer();
            serviceSocketThread = new Thread(serviceSocketServer.run);
            serviceSocketThread.IsBackground = true;
            serviceSocketThread.Start();

            standaloneAppControl = new StandaloneAppControl();
            standaloneAppControl.appExit += new StandaloneAppControl.ApplicationExitedHandler(onExeDestroyed);

            Console.WriteLine("Init Kinect sensor");
            // initialize the sensor chooser and UI
            this.SensorChooser = new KinectSensorChooser();
            this.SensorChooser.KinectChanged += SensorChooserOnKinectChanged;
            this.sensorChooserUi.KinectSensorChooser = this.SensorChooser;
#if DEBUG
            //log.Warn("Sensor auto-start is disabled during debug");
            //this.SocietiesLogo.MouseUp += new System.Windows.Input.MouseButtonEventHandler(SocietiesLogo_MouseDoubleClick);
            this.SensorChooser.Start();
#else
            this.SensorChooser.Start();
#endif
            // Bind the sensor chooser's current sensor to the KinectRegion
            Binding regionSensorBinding = new Binding("Kinect") { Source = this.SensorChooser };
            BindingOperations.SetBinding(this.kinectRegion, KinectRegion.KinectSensorProperty, regionSensorBinding);
            Console.WriteLine("Kinect initialized");

            iViewer = new ImageViewer();
            Console.WriteLine("ImageViewer initialized");

        }

        #region " Service Control Methods "

        public delegate void startServiceDelegate(String serviceName);
        public void startService(string serviceName)
        {
            ServiceInfo sInfo = this.userSession.getService(serviceName.Trim());

            if (sInfo != null)
            {
                if (log.IsDebugEnabled)
                    Console.WriteLine(serviceName + " starting now");

                if (sInfo.button.Dispatcher.CheckAccess())
                {
                    if (log.IsDebugEnabled)
                        Console.WriteLine("dispatcher has access. raising event");
                    //sInfo.button.Release();
                    //sInfo.button.RaiseEvent(new RoutedEventArgs(Button.ClickEvent));
                    this.startService(new RoutedEventArgs(Button.ClickEvent), sInfo);
                }
                else
                {
                    if (log.IsDebugEnabled)
                        Console.WriteLine("dispatcher doesn't have access. using delegate");
                    sInfo.button.Dispatcher.Invoke(new startServiceDelegate(startService), serviceName);

                }
                //if (sInfo.button.InvokeRequired)
                //{
                //    sInfo.button.Invoke(new startServiceDelegate(this.startService), serviceName);
                //}
                //else
                //{
                //sInfo.button.PerformClick();
                //sInfo.button.RaiseEvent(new RoutedEventArgs(Button.ClickEvent));
                //}

                this.runningService = sInfo;
            }
            else
            {
                if (log.IsDebugEnabled)
                    log.Warn(serviceName + " service doesn't exist in session");
            }
        }

        private void startService(EventArgs e, ServiceInfo sInfo)
        {
            Console.WriteLine("Starting service: " + sInfo.serviceName);

            if (sInfo.serviceType == ServiceType.EXE || sInfo.serviceType == ServiceType.JAR)
            {
                // NB: Always unbind the sensor - we don't want the user waving around and poking buttons in the background
                UnbindSensor();

                try
                {
                    this.startExe(e, sInfo);
                }
                catch (Exception ex)
                {
                    log.Error(String.Format("Error starting service '{0}' with exe file {1}", sInfo.serviceName, sInfo.serviceExe), ex);
                }

            }
            else if (sInfo.serviceType == ServiceType.WEB)
            {
                this.startWeb(sInfo);

            }

            this.runningService = sInfo;
            this.closeShowingServiceBtn.Content = "Close " + sInfo.serviceName;
            this.enableThisButton(this.closeShowingServiceBtn, true);
            if (log.IsDebugEnabled)
                Console.WriteLine("Service: " + sInfo.serviceName + " has started. Sending information messsage to display portal.");
            try
            {
                ServiceInformationSocketClient sClient = new ServiceInformationSocketClient();
                sClient.sendServiceInformationEvent(userSession.getIPAddress(), userSession.getPort(), sInfo.serviceName, ServiceInformationSocketClient.ServiceRuntimeInformation.STARTED_SERVICE);
            }
            catch (System.Net.Sockets.SocketException ex)
            {
                log.Error("Exception in socket while trying to send started service event", ex);
            }

        }

        private void startExe(EventArgs e, ServiceInfo sInfo)
        {

            //appWindow = new ApplicationWindow(this, sInfo);

            //appWindow.Show();
            //appWindow.startExe(e);

            standaloneAppControl.ExeName = sInfo.serviceExe;

            standaloneAppControl.LoadExe(e);

            //Canvas.SetLeft(appControlPanelStdAlone, 320);
            //Canvas.SetTop(appControlPanelStdAlone, 20);
            //appControl.ExeName = sInfo.serviceExe;
            //appControl.LoadExe(e);
            //wfhDate.Visibility = System.Windows.Visibility.Visible;
        }

        private void startWeb(ServiceInfo sInfo)
        {
            Console.WriteLine("Starting web service");
            //webBrowser = new System.Windows.Controls.WebBrowser();
            //appControl.Controls.Add(webBrowser);
            webBrowser.Navigate(new Uri(sInfo.serviceURL));
            //webBrowser.Url = new Uri(sInfo.serviceURL);
            webBrowser.Visibility = System.Windows.Visibility.Visible;
            //            if (log.IsDebugEnabled)  Console.WriteLine(webBrowser.IsVisible);
            webBrowser.Focus();

        }

        public void onExeDestroyed(object sender, ApplicationControlArgs args)
        {
            if (!args.GracefullyExited)
            {
                log.Warn("Service did not exit gracefully");
            }
            else
            {
                if (log.IsDebugEnabled)
                    Console.WriteLine("Service exited gracefully");
            }

            if (this.runningService != null)
            {
                if (log.IsDebugEnabled) Console.WriteLine("Closing running service");
                //if (!this.runningService.serviceName.Equals(string.Empty))
                //{
                //raise close service event;
                //  this.closeShowingServiceBtn.RaiseEvent(new RoutedEventArgs(Button.ClickEvent));
                //}

                ServiceInformationSocketClient socket = new ServiceInformationSocketClient();
                socket.sendServiceInformationEvent(this.userSession.getIPAddress(), userSession.getPort(), runningService.serviceName, ServiceInformationSocketClient.ServiceRuntimeInformation.STOPPED_SERVICE);
                if (log.IsDebugEnabled) Console.WriteLine("sent service information");
                this.runningService = new ServiceInfo();
                if (log.IsDebugEnabled) Console.WriteLine("running service re-initialised");
                try
                {

                    //this.closeShowingServiceBtn.Content = "";
                    setButtonContext(this.closeShowingServiceBtn, " ");
                    //this.closeShowingServiceBtn.Text = "";
                    this.enableThisButton(this.closeShowingServiceBtn, false);
                }
                catch (Exception ex)
                {
                    log.Error("error destroying service", ex);
                }

            }

            BindSensor();
        }

        public void onExeServiceStopped(ServiceInfo sInfo)
        {
            BindSensor();
            if (sInfo.serviceExe.Equals(this.runningService.serviceExe))
            {

                //appWindow.Close();
            }
        }

        public void stopService(RoutedEventArgs e, ServiceInfo sInfo)
        {
            if (sInfo.serviceType == ServiceType.EXE || sInfo.serviceType == ServiceType.JAR)
            {
                if (log.IsDebugEnabled)
                    Console.WriteLine("Stopping Service (executable).");
                //appWindow.Close();
                standaloneAppControl.DestroyExe(e);
                //appControl.DestroyExe(e);
                //wfhDate.Visibility = System.Windows.Visibility.Hidden;
            }
            else if (sInfo.serviceType == ServiceType.WEB)
            {
                if (log.IsDebugEnabled)
                    Console.WriteLine("Stopping Service (Web).");
                this.webBrowser.Navigate("about:blank");
                this.webBrowser.Visibility = System.Windows.Visibility.Hidden;
            }

            BindSensor();
            this.runningService = new ServiceInfo();
        }

        #endregion

        #region " User Session Handling "

        internal Boolean isLoggedIn()
        {
            return this.loggedIn;
        }

        internal Boolean isUserLoggedIn(String user)
        {
            if (this.userSession.getUserIdentity().Equals(string.Empty))
            {
                return false;
            }


            if (this.userSession.getUserIdentity().ToLower().IndexOf(user.ToLower()) > -1)
            {
                return true;
            }
            else if (user.ToLower().IndexOf(this.userSession.getUserIdentity().ToLower()) > -1)
            {
                return true;
            }

            return false;
        }

        private delegate void logOutDelegate();
        internal void logOut()
        {
            if (log.IsDebugEnabled)
                Console.WriteLine("Logout called");
            try
            {
                this.logoutMethod(logoutButton, new RoutedEventArgs());
                //if (logoutButton.Dispatcher.CheckAccess())
                //{
                //                if (log.IsDebugEnabled) 
                //Console.WriteLine("Directly invoking button click on logout");
                //    logoutButton.RaiseEvent(new RoutedEventArgs(Button.ClickEvent));
                //}
                //else
                //{
                //                if (log.IsDebugEnabled)  
                //Console.WriteLine("Logout delegate method called");
                //    logoutButton.Dispatcher.Invoke(new logOutDelegate(logOut));
                //}

            }
            catch (Exception e)
            {
                log.Error("Error logging out", e);
            }


            //if (this.InvokeRequired)
            //{
            //    this.Invoke(new logOutDelegate(this.logOut));
            //}
            //else
            //{
            //logoutButton.PerformClick();
            //logoutButton.RaiseEvent(new RoutedEventArgs(Button.ClickEvent));


            //}

        }

        private delegate void loginDelegate(UserSession userSession);
        internal void Login(UserSession userSession)
        {

           
            if (this.Dispatcher.CheckAccess())
            {
                this.setApplicationVisible(true);
                if (log.IsDebugEnabled)
                    Console.WriteLine("User: " + userSession.getUserIdentity() + " logging in and loading " + userSession.getServices().Count + " services");

                if (!this.loggedIn)
                {
                    this.serviceSocketServer.setUserSession(userSession, this);
                    //this.enableThisButton(this.closeShowingServiceBtn, true);
                    this.enableThisButton(this.logoutButton, true);


                    this.userSession = userSession;
                    this.loggedIn = true;
                    List<ServiceInfo> services = userSession.getServices();

                    int i = 0;

                    for (i = 0; i < serviceButtons.Length; i++)
                    {
                        if (i < services.Count)
                        {
                            ServiceInfo sInfo = services.ElementAt(i);
                            KinectCircleButton button = serviceButtons.ElementAt(i);
                            this.enableThisButton(button, true);
                            sInfo.button = button;
                            this.setServiceInfo(button, sInfo);
                        }
                        else
                        {
                            this.enableThisButton(serviceButtons.ElementAt(i), false);

                        }
                    }

                }
                else
                {
                    if (log.IsDebugEnabled)
                        log.Warn("User already logged in");
                }
            }
            else
            {
                
                this.Dispatcher.Invoke(new loginDelegate(Login), userSession);
               
            }
           
        }

        private void logoutButton_Click(object sender, RoutedEventArgs e)
        {
            if (this.logoutMethod(sender, e))
            {
                this.socketServer.helperLogoutMethod();

            }
        }

        private delegate bool logoutMethodDelegate(object sender, RoutedEventArgs e);
        private bool logoutMethod(object sender, RoutedEventArgs e)
        {
            if (this.Dispatcher.CheckAccess())
            {
                if (log.IsDebugEnabled)
                    Console.WriteLine("logoutButton_Clicked from sender: " + sender.ToString());
                if (this.loggedIn)
                {

                    String user = this.userSession.getUserIdentity();
                    if (log.IsDebugEnabled)
                        Console.WriteLine("logging out user");

                    if (log.IsDebugEnabled)
                        Console.WriteLine("Stopping service");

                    if (!this.runningService.serviceName.Equals(string.Empty))
                    {
                        this.stopService(e, this.runningService);
                        this.runningService = new ServiceInfo();
                    }
                    ServiceInformationSocketClient client = new ServiceInformationSocketClient();
                    client.sendLogoutEvent(userSession.getIPAddress(), userSession.getPort());

                    this.userSession = new UserSession();
                    foreach (KinectCircleButton button in serviceButtons)
                    {
                        this.enableThisButton(button, false);
                    }

                    this.enableThisButton(this.closeShowingServiceBtn, false);
                    this.enableThisButton(this.logoutButton, false);

                    if (log.IsDebugEnabled)
                        Console.WriteLine("User: " + user + " is logged out");
                    //this.notifyIcon1.ShowBalloonTip(5000, "SOCIETIES Display Portal", "User: " + user + " has logged out", ToolTipIcon.Info);
                    if (iViewer != null)
                    {
                        if (!iViewer.IsDisposed)
                        {
                            disposeComponent(iViewer);


                        }
                    }

                    this.loggedIn = false;

                    this.setApplicationVisible(false);
                    return true;
                }
                else
                {
                    if (log.IsDebugEnabled)
                        log.Warn("no user is logged in");
                    return false;
                }
            }
            else
            {
                return (bool)this.Dispatcher.Invoke(new logoutMethodDelegate(logoutMethod), sender, e);
            }


        }

        #endregion

        #region " Service button handlers "
        private void serviceButton_Click(object sender, RoutedEventArgs e)
        {
            this.startService(e, (ServiceInfo)((KinectCircleButton)serviceButton1).Tag);
        }
        #endregion

        #region " Kinect taming code "

        /// <summary>
        /// Called when the KinectSensorChooser gets a new sensor
        /// </summary>
        /// <param name="sender">sender of the event</param>
        /// <param name="args">event arguments</param>
        private void SensorChooserOnKinectChanged(object sender, KinectChangedEventArgs args)
        {
            Console.WriteLine("Kinect sensor changed");

            if (args.OldSensor != null)
            {
                Console.WriteLine("Unbinding old sensor");

                try
                {
                    KinectSensor oldSensor = args.OldSensor;

                    UnbindSensor(oldSensor);

                    Console.WriteLine("Completed unbinding old sensor");
                }
                catch (InvalidOperationException ex)
                {
                    // KinectSensor might enter an invalid state while enabling/disabling streams or stream features.
                    // E.g.: sensor might be abruptly unplugged.
                    log.Warn("Error unbinding old sensor", ex);
                }
            }

            if (args.NewSensor != null)
            {
                Console.WriteLine("Binding new sensor");

                try
                {
                    KinectSensor newSensor = args.NewSensor;

                    BindSensor(newSensor);

                    Console.WriteLine("Completed binding new sensor");
                }
                catch (InvalidOperationException ex)
                {
                    // KinectSensor might enter an invalid state while enabling/disabling streams or stream features.
                    // E.g.: sensor might be abruptly unplugged.
                    log.Warn("Error binding new sensor", ex);
                }

            }
        }

        private void BindSensor()
        {
            if (this.SensorChooser == null)
            {
                log.Error("Attempt to bind sensor when sensor chooser was null");
                return;
            }

            BindSensor(this.SensorChooser.Kinect);
        }

        private static void BindSensor(KinectSensor sensor)
        {
            if (sensor == null)
                return;

            Console.WriteLine("BindSensor(KinectSensor)");
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
            }
            catch (InvalidOperationException ex)
            {
                // Non Kinect for Windows devices do not support Near mode, so reset back to default mode.
                sensor.DepthStream.Range = DepthRange.Default;
                sensor.SkeletonStream.EnableTrackingInNearRange = false;
                log.Warn("Error Setting depth range to near mode", ex);
            }

            // Turn on the skeleton stream to receive skeleton frames
            sensor.SkeletonStream.Enable();

        }

        private void UnbindSensor()
        {
            if (this.SensorChooser == null)
            {
                log.Error("Attempt to bind sensor when sensor chooser was null");
                return;
            }

            UnbindSensor(this.SensorChooser.Kinect);
        }

        private static void UnbindSensor(KinectSensor sensor)
        {
            if (sensor == null)
                return;

            Console.WriteLine("UnbindSensor()");

            if (sensor.DepthStream != null)
                sensor.DepthStream.Disable();
            if (sensor.SkeletonStream != null)
                sensor.SkeletonStream.Disable();
            if (sensor.AudioSource != null)
                sensor.AudioSource.Stop();

            sensor.Stop();
        }

        #endregion

        #region " Window Events "

        private void WindowClosing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            Console.WriteLine("Window is closing");
            if (socketServer != null)
            {
                this.logOut();
                this.socketServer.close();

                this.binaryDataTransferServer.close();

                this.loggedIn = false;

                if (this.socketServer == null)
                {
                    if (log.IsDebugEnabled)
                        Console.WriteLine("socket server destroyed");

                }
                if (this.binaryDataTransferServer == null)
                {
                    if (log.IsDebugEnabled)
                        Console.WriteLine("binary data transfer socket server destroyed");
                }

            }
            else
            {
                if (log.IsDebugEnabled)
                    Console.WriteLine("SocketService is already null");
            }

            if (this.SensorChooser != null)
                this.SensorChooser.Stop();

            UnbindSensor();
        }

#if DEBUG
        private void SocietiesLogo_MouseDoubleClick(object sender, System.Windows.Input.MouseButtonEventArgs e)
        {
            this.SensorChooser.Start();
        }
#endif
        #endregion

        private delegate void showImageDelegate(String location);
        internal void showImage(String location)
        {
            if (this.Dispatcher.CheckAccess())
            {
                if (iViewer.IsDisposed)
                {
                    iViewer = new ImageViewer();
                }
                iViewer.setImage(location);

                iViewer.Show();
            }
            else
            {
                this.Dispatcher.Invoke(new showImageDelegate(showImage), location);
            }
            //if (this.InvokeRequired)
            //{
            // this.Invoke(new showImageDelegate(this.showImage), location);
            //}
            //else
            //{
            //if (iViewer.IsDisposed)
            //{
            //    iViewer = new ImageViewer();
            //}
            //iViewer.setImage(location);

            //iViewer.Show();
            //}
        }

        private delegate void addNewNotificationDelegate(String serviceName, String text);
        internal void addNewNotification(String serviceName, string text)
        {
            if (this.Dispatcher.CheckAccess())
            {

                this.notificationsWindow.addNewNotification(serviceName, text);

            }
            else
            {
                this.Dispatcher.Invoke(new addNewNotificationDelegate(addNewNotification), serviceName, text);
            }
            //if (this.InvokeRequired)
            //{
            //    this.Invoke(new addNewNotificationDelegate(this.addNewNotification), serviceName, text);
            //}
            //else
            //{
            //if (this.notificationsWindow.IsDisposed)
            //{
            //    this.notificationsWindow = new TextNotificationsWindow();


            //}
            //this.notificationsWindow.addNewNotification(serviceName, text);
            //this.notificationsWindow.Show();
            //}

        }

        private delegate void setApplicationVisibleDelegate(Boolean enabled);
        private void setApplicationVisible(Boolean enabled)
        {
            if (log.IsDebugEnabled)
                Console.WriteLine("Changing application visibility");
            if (this.Dispatcher.CheckAccess())
            {
                if (log.IsDebugEnabled)
                    Console.WriteLine("Changing application visibility - dispatcher has access");
                if (enabled)
                {
                    this.Visibility = System.Windows.Visibility.Visible;
                }
                else
                {
                    this.Visibility = System.Windows.Visibility.Hidden;
                }

            }
            else
            {
                if (log.IsDebugEnabled)
                    Console.WriteLine("Changing application visibility through dispatcher");
                
                this.Dispatcher.Invoke(new setApplicationVisibleDelegate(setApplicationVisible), enabled);
            }
        }

        public delegate void setServiceInfoDelegate(KinectCircleButton button, ServiceInfo serviceInfo);
        public void setServiceInfo(KinectCircleButton button, ServiceInfo sInfo)
        {
            if (button.Dispatcher.CheckAccess())
            {
                button.Tag = sInfo;
                button.Content = sInfo.serviceName;
            }
            else
            {
                button.Dispatcher.Invoke(new setServiceInfoDelegate(setServiceInfo), button, sInfo);
            }
            //if (button.InvokeRequired)
            //{
            //    button.Invoke(new setServiceInfoDelegate(this.setServiceInfo), button, sInfo);
            //}
            //else
            //{
            //button.Tag = sInfo;

            //button.Text = sInfo.serviceName;
            //}
        }

        public delegate void setButtonContextDelegate(KinectCircleButton button, String content);
        public void setButtonContext(KinectCircleButton button, String content)
        {

            if (button.Dispatcher.CheckAccess())
            {
                if (log.IsDebugEnabled)
                    Console.WriteLine("I have access to change the content of the button. Current content: " + button.Content + "| new content: " + content);
                button.Content = content;
            }
            else
            {
                if (log.IsDebugEnabled)
                    Console.WriteLine("I don't have access to the button. Using delegate method");
                button.Dispatcher.Invoke(new setButtonContextDelegate(setButtonContext), button, content);
            }

        }

        public delegate void enableThisButtonDelegate(KinectCircleButton button, Boolean enabled);
        public void enableThisButton(KinectCircleButton button, Boolean enabled)
        {

            if (button.Dispatcher.CheckAccess())
            {
                button.IsEnabled = enabled;
                if (enabled)
                {
                    if (log.IsDebugEnabled)
                        Console.WriteLine("enabling button" + button.Name);
                    button.Visibility = System.Windows.Visibility.Visible;
                }
                else
                {
                    if (log.IsDebugEnabled)
                        Console.WriteLine("disabling button" + button.Name);
                    button.Visibility = System.Windows.Visibility.Hidden;
                }
            }
            else
            {
                if (log.IsDebugEnabled)
                    Console.WriteLine("using dispatcher to enable/disable button");
                button.Dispatcher.Invoke(new enableThisButtonDelegate(enableThisButton), button, enabled);
            }

        }

        public delegate void enableViewingPanelDelegate(Panel panel, Boolean enabled);
        public void enableViewingPanel(Panel panel, Boolean enabled)
        {
            if (panel.Dispatcher.CheckAccess())
            {
                panel.IsEnabled = enabled;
                if (enabled)
                {
                    panel.Visibility = System.Windows.Visibility.Visible;
                }
                else
                {
                    panel.Visibility = System.Windows.Visibility.Hidden;
                }
            }
            else
            {
                panel.Dispatcher.Invoke(new enableViewingPanelDelegate(enableViewingPanel), panel, enabled);
            }
            //if (panel.InvokeRequired)
            //{
            //    panel.Invoke(new enableViewingPanelDelegate(this.enableViewingPanel), panel, enabled);
            //}
            //else
            //{
            //panel.IsEnabled = enabled;
            //if (enabled)
            //{
            //    panel.Visibility = System.Windows.Visibility.Visible;
            //}
            //else
            //{
            //    panel.Visibility = System.Windows.Visibility.Hidden;
            //}
            //panel.Enabled = enabled;
            //panel.Visible = enabled;
            //}
        }

        private void closeShowingServiceBtn_Click(object sender, RoutedEventArgs e)
        {
            if (log.IsDebugEnabled) Console.WriteLine("Close showing service button clicked");
            if (!this.runningService.serviceName.Equals(string.Empty))
            {
                this.stopService(e, this.runningService);
                ServiceInformationSocketClient socket = new ServiceInformationSocketClient();
                socket.sendServiceInformationEvent(this.userSession.getIPAddress(), userSession.getPort(), runningService.serviceName, ServiceInformationSocketClient.ServiceRuntimeInformation.STOPPED_SERVICE);
                this.runningService = new ServiceInfo();

                setButtonContext(this.closeShowingServiceBtn, "");
                //this.closeShowingServiceBtn.Text = "";
                this.enableThisButton(this.closeShowingServiceBtn, false);
                if (log.IsDebugEnabled) Console.WriteLine("Disabled button: " + this.closeShowingServiceBtn.Name);

            }
        }

        private delegate void disposeComponentDelegate(ImageViewer objtoDispose);
        private void disposeComponent(ImageViewer objtoDispose)
        {
            if (objtoDispose.InvokeRequired)
            {
                objtoDispose.Invoke(new disposeComponentDelegate(disposeComponent), objtoDispose);
            }
            else
            {
                objtoDispose.Dispose();
            }
        }


    }

}
