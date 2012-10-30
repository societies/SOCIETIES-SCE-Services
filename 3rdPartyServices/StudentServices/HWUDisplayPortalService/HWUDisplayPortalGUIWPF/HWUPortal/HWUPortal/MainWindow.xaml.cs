using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Threading;

using Microsoft.Kinect;
using Microsoft.Kinect.Interop;
using Coding4Fun.Kinect.Wpf;
using Coding4Fun.Kinect;
using Coding4Fun.Kinect.Wpf.Controls;

namespace HWUPortal
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {

        /*
         * */
        private ServiceInfo runningService;
        private UserSession userSession = new UserSession();
        private List<HoverButton> myButtons;
        //SocketServer socketServer;

        #region sockets
        SocketServer socketServer;
        BinaryDataTransfer binaryDataTransferServer;
        ServiceSocketServer serviceSocketServer;
        Thread socketThread;
        Thread binaryTransferThread;
        Thread serviceSocketThread;
        #endregion sockets

        ImageViewer iViewer;
        private Boolean loggedIn = false;

        

        

        System.Windows.Forms.FlowLayoutPanel flpPanel;
      
        StandaloneAppControl standaloneAppControl;

        #region Kinectvariables
        KinectSensor kinect;

        const int skeletonCount = 6;
        Skeleton[] allSkeletons = new Skeleton[skeletonCount];
        //variables used to detect hand over close button
        private static double _topBoundary;
        private static double _bottomBoundary;
        private static double _leftBoundary;
        private static double _rightBoundary;
        private static double _itemLeft;
        private static double _itemTop;
        #endregion Kinectvariables
        /*
         * 
         */
        public MainWindow()
        {
            Thread.CurrentThread.Name = "MainWindowThread";
            
            InitializeComponent();
            this.webBrowser.Visibility = System.Windows.Visibility.Hidden;
            RightHand.BringIntoView();
            /* 
             * kinect initialisation */
            this.StartKinectST();

            /*
             *
             */
            
            InitializeComponent();
            this.runningService = new ServiceInfo();
            //this.binaryDataTransferServer = new BinaryDataTransfer();

            socketServer = new SocketServer(this);
            socketThread = new Thread(socketServer.run);
            socketThread.Start();

            binaryDataTransferServer = new BinaryDataTransfer(this);
            binaryTransferThread = new Thread(binaryDataTransferServer.run);
            binaryTransferThread.Start();

            serviceSocketServer = new ServiceSocketServer();
            serviceSocketThread = new Thread(serviceSocketServer.run);
            serviceSocketThread.Start();


            //flpPanel = this.wfhDate.Child as System.Windows.Forms.FlowLayoutPanel;


            standaloneAppControl = new StandaloneAppControl();
            
            //appControl = new ApplicationControl.ApplicationControl();
            //appControl.appExit += new ApplicationControl.ApplicationControl.ApplicationExitedHandler(onExeDestroyed);


            //appControl.Width = 1000;
            //appControl.Height = 600;
            //flpPanel.Controls.Add(appControl);


            myButtons = new List<HoverButton>();
            myButtons.Add(this.serviceButton1);
            myButtons.Add(this.serviceButton2);
            myButtons.Add(this.serviceButton3);
            myButtons.Add(this.serviceButton4);
            myButtons.Add(this.serviceButton5);


            foreach (HoverButton button in myButtons)
            {
                button.Visibility = System.Windows.Visibility.Hidden;
                //button.Visible = false;
                //button.Enabled = false;
                button.IsEnabled = false;
            }
            this.closeShowingServiceBtn.IsEnabled = false;
            this.closeShowingServiceBtn.Visibility = System.Windows.Visibility.Hidden;
            this.logoutButton.IsEnabled = false;
            this.logoutButton.Visibility = System.Windows.Visibility.Hidden;

            /*
             * 
             */

            this.Visibility = System.Windows.Visibility.Hidden;
        }

        internal delegate void showImageDelegate(String location);
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

        internal delegate void addNewNotificationDelegate(String serviceName, String text);
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

        internal Boolean isLoggedIn()
        {
            return this.loggedIn;
        }

        public delegate void startServiceDelegate(String serviceName);

        public void startService(string serviceName)
        {
            ServiceInfo sInfo = this.userSession.getService(serviceName);
            if (sInfo != null)
            {
                Console.WriteLine(serviceName + " starting now");

                if (sInfo.button.Dispatcher.CheckAccess())
                {
                    Console.WriteLine("dispatcher has access. raising event");
                    //sInfo.button.Release();
                    //sInfo.button.RaiseEvent(new RoutedEventArgs(Button.ClickEvent));
                    this.startService(new RoutedEventArgs(Button.ClickEvent), sInfo);
                }
                else
                {
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
                Console.WriteLine(serviceName + " service doesn't exist in session");
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
            Console.WriteLine(webBrowser.IsVisible);
            webBrowser.Focus();

        }
        private void startService(EventArgs e, ServiceInfo sInfo)
        {
            Console.WriteLine("Starting service: " + sInfo.serviceName);
            KinectSensor.KinectSensors.StatusChanged -= KinectSensors_StatusChanged;
            if (sInfo.serviceType == ServiceType.EXE || sInfo.serviceType == ServiceType.JAR)
            {
                if (sInfo.requiresKinect)
                {
                    if (this.kinect != null)
                    {
                        this.kinect.Stop();
                    }
                    Console.WriteLine("Service requires kinect. Kinect stopped");
                    this.kinect = null;
                }
                try
                {
                    this.startExe(e, sInfo);

                }
                catch (Exception ex)
                {
                    Console.WriteLine(ex.Message);
                }

            }
            else if (sInfo.serviceType == ServiceType.WEB)
            {
                this.startWeb(sInfo);

            }
            
            this.runningService = sInfo;
            this.closeShowingServiceBtn.Content = "Close " + sInfo.serviceName;
            this.enableThisButton(this.closeShowingServiceBtn, true);
            Console.WriteLine("Service: " + sInfo.serviceName + " has started. Sending information messsage to display portal.");
            try
            {
                ServiceInformationSocketClient sClient = new ServiceInformationSocketClient();
                sClient.sendServiceInformationEvent(userSession.getIPAddress(), userSession.getPort(), sInfo.serviceName, ServiceInformationSocketClient.ServiceRuntimeInformation.STARTED_SERVICE);
            }
            catch (System.Net.Sockets.SocketException exc)
            {
                Console.WriteLine("Exception in socket while trying to send started service event");
            }

        }


        internal delegate void logOutDelegate();
        internal void logOut()
        {
            if (logoutButton.Dispatcher.CheckAccess())
            {
                logoutButton.RaiseEvent(new RoutedEventArgs(Button.ClickEvent));
            }
            else
            {
                logoutButton.Dispatcher.Invoke(new logOutDelegate(logOut));
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

        internal delegate void setApplicationVisibleDelegate(Boolean enabled);

        internal void setApplicationVisible(Boolean enabled)
        {
            if (this.Dispatcher.CheckAccess())
            {
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
                this.Dispatcher.Invoke(new setApplicationVisibleDelegate(setApplicationVisible), enabled);
            }
        }
        internal delegate void loginDelegate(UserSession userSession);

        internal void Login(UserSession userSession)
        {
            this.setApplicationVisible(true);
            if (this.Dispatcher.CheckAccess())
            {
                Console.WriteLine("User: " + userSession.getUserIdentity() + " logging in and loading " + userSession.getServices().Count + " services");

                if (!this.loggedIn)
                {
                    this.serviceSocketServer.setUserSession(userSession);
                    //this.enableThisButton(this.closeShowingServiceBtn, true);
                    this.enableThisButton(this.logoutButton, true);


                    this.userSession = userSession;
                    this.loggedIn = true;
                    List<ServiceInfo> services = userSession.getServices();

                    int i = 0;

                    for (i = 0; i < myButtons.Count; i++)
                    {
                        if (i < services.Count)
                        {
                            ServiceInfo sInfo = services.ElementAt(i);
                            HoverButton button = myButtons.ElementAt(i);
                            this.enableThisButton(button, true);
                            sInfo.button = button;
                            this.setServiceInfo(button, sInfo);
                        }
                        else
                        {
                            this.enableThisButton(myButtons.ElementAt(i), false);

                        }
                    }

                }
                else
                {
                    Console.WriteLine("User already logged in");
                }
            }
            else
            {
                this.Dispatcher.Invoke(new loginDelegate(Login), userSession);
            }
            //if (this.InvokeRequired)
            //{
            //    Invoke(new loginDelegate(this.Login), userSession);
            //}
            //else
            //{

            //}
        }

        public delegate void setServiceInfoDelegate(HoverButton button, ServiceInfo serviceInfo);

        public void setServiceInfo(HoverButton button, ServiceInfo sInfo)
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


        public delegate void enableThisButtonDelegate(HoverButton button, Boolean enabled);

        public void enableThisButton(HoverButton button, Boolean enabled)
        {
            if (button.Dispatcher.CheckAccess())
            {
                button.IsEnabled = enabled;
                if (enabled)
                {
                    button.Visibility = System.Windows.Visibility.Visible;
                }
                else
                {
                    button.Visibility = System.Windows.Visibility.Hidden;
                }
            }
            else
            {
                button.Dispatcher.Invoke(new enableThisButtonDelegate(enableThisButton), button, enabled);
            }
            //if (button.InvokeRequired)
            //{
            //    button.Invoke(new enableThisButtonDelegate(this.enableThisButton), button, enabled);
            //}
            //else
            //{
            //button.IsEnabled = enabled;
            //if (enabled)
            //{
            //    button.Visibility = System.Windows.Visibility.Visible;
            //}
            //else
            //{
            //    button.Visibility = System.Windows.Visibility.Hidden;
            //}
            //button.Enabled = enabled;
            //button.Visible = enabled;
            //}
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

        //public delegate void enableApplicationControlDelegate(ApplicationControl.ApplicationControl appCtrl, Boolean enabled);

        //public void enableViewingPanel(ApplicationControl.ApplicationControl appCtrl, Boolean enabled)
        //{
        //    if (appCtrl.InvokeRequired)
        //    {
        //        appCtrl.Invoke(new enableApplicationControlDelegate(enableViewingPanel), appCtrl, enabled);
        //    }
        //    else
        //    {
        //        appCtrl.Visible = enabled;
        //        appCtrl.Enabled = enabled;

        //    }
        //}


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

        private void WindowClosing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            kinectSensorChooser1.KinectSensorChanged -= kinectSensorChooser1_KinectSensorChanged;
            this.StopKinect(this.kinect);
            if (socketServer != null)
            {
                this.logOut();
                this.socketServer.close();

                this.binaryDataTransferServer.close();

                this.loggedIn = false;

                if (this.socketServer == null)
                {
                    Console.WriteLine("socket server destroyed");

                }
                if (this.binaryDataTransferServer == null)
                {
                    Console.WriteLine("binary data transfer socket server destroyed");
                }

            }
            else
            {
                Console.WriteLine("SocketService is already null");
            }


        }

        private void WindowClosed(object sender, EventArgs e)
        {
            Console.WriteLine("window closed");
        }

        public void onExeDestroyed(object sender, ApplicationControlArgs args)
        {
            if (!args.GracefullyExited)
            {
                Console.WriteLine("Service did not exit gracefully");
            }
            if (!this.runningService.serviceName.Equals(string.Empty))
            {
                //raise close service event;
                this.closeShowingServiceBtn.RaiseEvent(new RoutedEventArgs(Button.ClickEvent));
            }


        }

        private void closeShowingServiceBtn_Click(object sender, RoutedEventArgs e)
        {
            if (!this.runningService.serviceName.Equals(string.Empty))
            {
                this.stopService(e, this.runningService);
                ServiceInformationSocketClient socket = new ServiceInformationSocketClient();
                socket.sendServiceInformationEvent(this.userSession.getIPAddress(), userSession.getPort(), runningService.serviceName, ServiceInformationSocketClient.ServiceRuntimeInformation.STOPPED_SERVICE);
                this.runningService = new ServiceInfo();
                this.closeShowingServiceBtn.Content = "";
                //this.closeShowingServiceBtn.Text = "";
                this.enableThisButton(this.closeShowingServiceBtn, false);
               
            }
        }

        private void logoutButton_Click(object sender, RoutedEventArgs e)
        {
            if (this.loggedIn)
            {

                String user = this.userSession.getUserIdentity();
                Console.WriteLine("logging out user");

                Console.WriteLine("Stopping service");

                if (!this.runningService.serviceName.Equals(string.Empty))
                {
                    this.stopService(e, this.runningService);
                    this.runningService = new ServiceInfo();
                }
                ServiceInformationSocketClient client = new ServiceInformationSocketClient();
                client.sendLogoutEvent(userSession.getIPAddress(), userSession.getPort());

                this.userSession = new UserSession();
                foreach (HoverButton button in myButtons)
                {
                    this.enableThisButton(button, false);
                }

                this.enableThisButton(this.closeShowingServiceBtn, false);
                this.enableThisButton(this.logoutButton, false);

                Console.WriteLine("User: " + user + " is logged out");
                //this.notifyIcon1.ShowBalloonTip(5000, "SOCIETIES Display Portal", "User: " + user + " has logged out", ToolTipIcon.Info);
                if (iViewer != null)
                {
                    if (!iViewer.IsDisposed)
                    {
                        iViewer.Dispose();


                    }
                }

                this.loggedIn = false;

                this.socketServer.helperLogoutMethod();
                this.setApplicationVisible(false);
            }
            else
            {
                Console.WriteLine("user already logged out");
            }
           
        }

        public void onExeServiceStopped(ServiceInfo sInfo)
        {
            KinectSensor.KinectSensors.StatusChanged += KinectSensors_StatusChanged;
            this.DiscoverKinectSensor();
            if (sInfo.serviceExe.Equals(this.runningService.serviceExe))
            {

                //appWindow.Close();
            }
        }
        private void stopService(RoutedEventArgs e, ServiceInfo sInfo)
        {
            if (sInfo.serviceType == ServiceType.EXE || sInfo.serviceType == ServiceType.JAR)
            {
                //appWindow.Close();
                standaloneAppControl.DestroyExe(e);
                //appControl.DestroyExe(e);
                //wfhDate.Visibility = System.Windows.Visibility.Hidden;
                KinectSensor.KinectSensors.StatusChanged += KinectSensors_StatusChanged;
                DiscoverKinectSensor();
            }
            else if (sInfo.serviceType == ServiceType.WEB)
            {
                this.webBrowser.Navigate("about:blank");
                this.webBrowser.Visibility = System.Windows.Visibility.Hidden;
            }

            this.runningService = new ServiceInfo();
        }

        private void serviceButton1_Click(object sender, RoutedEventArgs e)
        {

            this.startService(e, (ServiceInfo)serviceButton1.Tag);
         
        }

        private void serviceButton2_Click(object sender, RoutedEventArgs e)
        {

            this.startService(e, (ServiceInfo)serviceButton2.Tag);
            
        }

        private void serviceButton3_Click(object sender, RoutedEventArgs e)
        {

            this.startService(e, (ServiceInfo)serviceButton3.Tag);
           
        }

        private void serviceButton4_Click(object sender, RoutedEventArgs e)
        {

            this.startService(e, (ServiceInfo)serviceButton4.Tag);
          
        }

        private void serviceButton5_Click(object sender, RoutedEventArgs e)
        {

            this.startService(e, (ServiceInfo)serviceButton5.Tag);
          
        }












        /**
         * kinect code
         * */
        #region kinect processing
        void StartKinectST()
        {
            Console.WriteLine("starting kinect");


            DiscoverKinectSensor();

            kinectSensorChooser1.KinectSensorChanged += new DependencyPropertyChangedEventHandler(kinectSensorChooser1_KinectSensorChanged);
            //kinect = KinectSensor.KinectSensors.FirstOrDefault(s => s.Status == KinectStatus.Connected); // Get first Kinect Sensor
            //kinect.SkeletonStream.Enable(); // Enable skeletal tracking

            //skeletonData = new Skeleton[kinect.SkeletonStream.FrameSkeletonArrayLength]; // Allocate ST data

            //kinect.SkeletonFrameReady += new EventHandler<SkeletonFrameReadyEventArgs>(kinect_SkeletonFrameReady); // Get Ready for Skeleton Ready Events

            //kinect.Start(); // Start Kinect sensor
        }

        private void DiscoverKinectSensor()
        {
            KinectSensor.KinectSensors.StatusChanged += KinectSensors_StatusChanged;
            this.kinect = KinectSensor.KinectSensors.FirstOrDefault(s => s.Status == KinectStatus.Connected); // Get first Kinect Sensor
            this.InitialiseKinectSensor();
            Console.WriteLine("Discovered kinect");

        }

        private void KinectSensors_StatusChanged(object sender, StatusChangedEventArgs args)
        {
            Console.WriteLine("Kinect status changed");
            switch (args.Status)
            {
                case KinectStatus.Connected:
                    if (this.kinect == null)
                    {
                        this.kinect = args.Sensor;
                    }
                    break;
                case KinectStatus.Disconnected:
                    if (this.kinect == args.Sensor)
                    {
                        this.kinect = null;
                        this.kinect = KinectSensor.KinectSensors.FirstOrDefault(s => s.Status == KinectStatus.Connected);
                        if (this.kinect == null)
                        {
                            Console.WriteLine("Kinect is disconnected"); //show a visual box and exit application
                        }
                    }
                    break;
            }
        }

        private void InitialiseKinectSensor()
        {

            if (this.kinect != null)
            {
                this.kinect.Start();
                Console.WriteLine("Kinect started");
            }
        }
        private void UninitialiseKinectSensor()
        {
            if (this.kinect != null)
            {
                this.kinect.Stop();
                Console.WriteLine("Kinect stopped");
            }
        }





        private void Window_Loaded(object sender, RoutedEventArgs e)
        {


        }

        private void Window_Unloaded(object sender, RoutedEventArgs e)
        {
            Console.WriteLine("Unloaded window!");



        }

        void kinectSensorChooser1_KinectSensorChanged(object sender, DependencyPropertyChangedEventArgs e)
        {
            KinectSensor old = (KinectSensor)e.OldValue;

            StopKinect(old);

            KinectSensor sensor = (KinectSensor)e.NewValue;

            if (sensor == null)
            {
                return;
            }

            var parameters = new TransformSmoothParameters
            {
                Smoothing = 0.3f,
                Correction = 0.0f,
                Prediction = 0.0f,
                JitterRadius = 1.0f,
                MaxDeviationRadius = 0.5f
            };
            sensor.SkeletonStream.Enable(parameters);

            //sensor.SkeletonStream.Enable();

            sensor.AllFramesReady += new EventHandler<AllFramesReadyEventArgs>(sensor_AllFramesReady);
            sensor.DepthStream.Enable(DepthImageFormat.Resolution640x480Fps30);
            sensor.ColorStream.Enable(ColorImageFormat.RgbResolution640x480Fps30);

            try
            {
                sensor.Start();
            }
            catch (InvalidOperationException)
            {
                kinectSensorChooser1.AppConflictOccurred();
            }
        }


        private void StopKinect(KinectSensor sensor)
        {

            if (sensor != null)
            {
                if (sensor.IsRunning)
                {
                    //stop sensor 
                    sensor.Stop();


                    //stop audio if not null
                    if (sensor.AudioSource != null)
                    {
                        sensor.AudioSource.Stop();
                    }

                }
                sensor.Dispose();
            }
        }

        void sensor_AllFramesReady(object sender, AllFramesReadyEventArgs e)
        {
            if (this.kinect == null)
            {
                return;
            }

            //Get a skeleton
            Skeleton first = GetFirstSkeleton(e);

            if (first == null)
            {
                return;
            }

            GetCameraPoint(first, e);
            ScalePosition(RightHand, first.Joints[JointType.HandRight]);

            //CheckButton(playButton, RightHand);
            //CheckButton(challengeButton, RightHand);
            //CheckButton(categoriesButton, RightHand);
            //CheckButton(scoreboardButton, RightHand);
            //CheckButton(quitButton, RightHand);
            foreach (HoverButton hButton in myButtons)
            {
                CheckButton(hButton, RightHand);
            }

            CheckButton(this.logoutButton, RightHand);

            if (!this.runningService.serviceName.Equals(String.Empty))
            {
                CheckButton(this.closeShowingServiceBtn, RightHand);
            }
        }

        #region buttons
        private void CheckButton(HoverButton button, Ellipse thumbStick)
        {
            if (this.kinect != null)
            {
                try
                {
                    if (IsItemMidpointInContainer(button, thumbStick))
                    {
                        //Console.WriteLine("button hovering");
                        
                        //button.RaiseEvent(new RoutedEventArgs(Button.MouseEnterEvent));
                        button.Hovering();
                        //if (!onButtonWaiting)
                        //{
                            //onButtonWaiting = true;
                            //ButtonWaitThread thread = new ButtonWaitThread(this, button, thumbStick);
                            //thread.run();
                            //Console.WriteLine("Button thread started");
                        //}
                    }
                    else
                    {
                        //Console.WriteLine("button release");
                        button.Release();

                    }
                }
                catch (System.InvalidOperationException e)
                {
                    Console.WriteLine("Error: " + e);
                }
            }
        }

        public static bool IsItemMidpointInContainer(FrameworkElement container, FrameworkElement target)
        {
            FindValues(container, target);

            if (_itemTop < _topBoundary || _bottomBoundary < _itemTop)
            {
                //Midpoint of target is outside of top or bottom
                return false;
            }

            if (_itemLeft < _leftBoundary || _rightBoundary < _itemLeft)
            {
                //Midpoint of target is outside of left or right
                return false;
            }

            return true;
        }
        //this has to be checked.
        private static void FindValues(FrameworkElement container, FrameworkElement target)
        {
            var containerTopLeft = container.PointToScreen(new Point());
            var itemTopLeft = target.PointToScreen(new Point());

            _topBoundary = containerTopLeft.Y;
            _bottomBoundary = _topBoundary + container.ActualHeight;
            _leftBoundary = containerTopLeft.X;
            _rightBoundary = _leftBoundary + container.ActualWidth;

            //use midpoint of item (width or height divided by 2)
            _itemLeft = itemTopLeft.X + (target.ActualWidth / 2);
            _itemTop = itemTopLeft.Y + (target.ActualHeight / 2);
        }
        #endregion buttons
        #region gesture processing
        Skeleton GetFirstSkeleton(AllFramesReadyEventArgs e)
        {
            using (SkeletonFrame skeletonFrameData = e.OpenSkeletonFrame())
            {
                if (skeletonFrameData == null)
                {
                    return null;
                }

                skeletonFrameData.CopySkeletonDataTo(allSkeletons);

                //get the first tracked skeleton
                Skeleton first = (from s in allSkeletons
                                  where s.TrackingState == SkeletonTrackingState.Tracked
                                  select s).FirstOrDefault();

                return first;

            }
        }

        private void ScalePosition(FrameworkElement element, Joint joint)
        {
            //Console.WriteLine("Unscaled joint: " + joint.Position.X + "," + joint.Position.Y);
            //convert the value to X/Y
            //Joint scaledJoint = joint.ScaleTo(360, 730, .3f, .3f);
            Joint scaledJoint = joint.ScaleTo(1360, 730, .3f, .3f);
            //Joint scaledJoint = joint.ScaleTo(mainWindow.WindowStartupLocation);
            //Console.WriteLine("Scaled Joint: " + scaledJoint.Position.X + "," + scaledJoint.Position.Y);
            Canvas.SetLeft(element, scaledJoint.Position.X);
            Canvas.SetTop(element, scaledJoint.Position.Y);

        }
        #endregion gesture processing
        #region camera stuff
        void GetCameraPoint(Skeleton first, AllFramesReadyEventArgs e)
        {

           // CoordinateMapper cm = new CoordinateMapper(this.kinect);
            // DepthImagePoint rightDepthPoint = cm.MapSkeletonPointToDepthPoint(first.Joints[JointType.HandRight].Position, DepthImageFormat.Resolution640x480Fps30);
            // ColorImagePoint rightColorPoint = cm.MapSkeletonPointToColorPoint(first.Joints[JointType.HandRight].Position, ColorImageFormat.RgbResolution640x480Fps30);
            //CameraPosition(RightHand, rightColorPoint);

             using (DepthImageFrame depth = e.OpenDepthImageFrame())
            {
                if (depth == null ||
                    kinectSensorChooser1.Kinect == null)
                {
                    return;
                }



                //Map a joint location to a point on the depth map
                //right hand
                DepthImagePoint rightDepthPoint =
                    
                    depth.MapFromSkeletonPoint(first.Joints[JointType.HandRight].Position);

                //Map a depth point to a point on the color image
                //right hand
                ColorImagePoint rightColorPoint =
                    depth.MapToColorImagePoint(rightDepthPoint.X, rightDepthPoint.Y,
                    ColorImageFormat.RgbResolution640x480Fps30);


                //Set location
                CameraPosition(RightHand, rightColorPoint);

            } 
        }

        private void CameraPosition(FrameworkElement element, ColorImagePoint point)
        {
            //Divide by 2 for width and height so point is right in the middle 
            // instead of in top/left corner
            Canvas.SetLeft(element, point.X - element.Width / 2);
            Canvas.SetTop(element, point.Y - element.Height / 2);

        }
        #endregion camera stuff


    }

        #endregion kinect processing

}
