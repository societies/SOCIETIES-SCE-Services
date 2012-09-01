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
using ApplicationControl;

namespace HWUPortal
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        /*
         * 
         */

        System.Windows.Forms.WebBrowser webBrowser;

        private ServiceInfo runningService;
        private UserSession userSession = new UserSession();
        private List<Button> myButtons;
        //SocketServer socketServer;
        SocketServer socketServer;
        BinaryDataTransfer binaryDataTransferServer;
        ImageViewer iViewer;
        private Boolean loggedIn = false;
        TextNotificationsWindow notificationsWindow;
        System.Windows.Forms.NotifyIcon notifyIcon1;
        ApplicationControl.ApplicationControl appControl;
        Thread socketThread;
        Thread binaryTransferThread;
        System.Windows.Forms.FlowLayoutPanel flpPanel;
        /*
         * 
         */
        public MainWindow()
        {
            InitializeComponent();
            
            flpPanel = this.wfhDate.Child as System.Windows.Forms.FlowLayoutPanel;
            appControl = new ApplicationControl.ApplicationControl();
            appControl.Width = 1000;
            appControl.Height = 600;
            flpPanel.Controls.Add(appControl);

            /*
             *
             */
            iViewer = new ImageViewer();
            InitializeComponent();
            this.runningService = new ServiceInfo();
            //this.binaryDataTransferServer = new BinaryDataTransfer();
            socketServer = new SocketServer(this);
            socketThread = new Thread(socketServer.run);
            socketThread.Start();

            binaryDataTransferServer = new BinaryDataTransfer(this);
            binaryTransferThread = new Thread(binaryDataTransferServer.run);
            binaryTransferThread.Start();

            myButtons = new List<Button>();
            myButtons.Add(this.serviceButton1);
            myButtons.Add(this.serviceButton2);
            myButtons.Add(this.serviceButton3);
            myButtons.Add(this.serviceButton4);
            myButtons.Add(this.serviceButton5);
            notificationsWindow = new TextNotificationsWindow();
            foreach (Button button in myButtons)
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
            notifyIcon1 = new System.Windows.Forms.NotifyIcon();
        }

        internal delegate void showImageDelegate(String location);
        internal void showImage(String location)
        {
            if (this.Dispatcher.CheckAccess()){
                                if (iViewer.IsDisposed)
                {
                    iViewer = new ImageViewer();
                }
                iViewer.setImage(location);

                iViewer.Show();
            }else{
                this.Dispatcher.Invoke(new showImageDelegate(showImage),location);
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
            if (this.Dispatcher.CheckAccess()){
                                if (!this.notificationsWindow.IsInitialized)
                {
                    this.notificationsWindow = new TextNotificationsWindow();


                }
                this.notificationsWindow.addNewNotification(serviceName, text);
                this.notificationsWindow.Show();
            }else{
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

                if (sInfo.button.Dispatcher.CheckAccess()){
                    sInfo.button.RaiseEvent(new RoutedEventArgs(Button.ClickEvent));
                }else{
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

            appControl.ExeName = sInfo.serviceExe;
            appControl.LoadExe(e);

        }

        private void startWeb(ServiceInfo sInfo)
        {
            webBrowser = new System.Windows.Forms.WebBrowser();
            webBrowser.Size = flpPanel.Size;
           
            flpPanel.Controls.Add(webBrowser);
            //appControl.Controls.Add(webBrowser);
            webBrowser.Url = new Uri(sInfo.serviceURL);
            webBrowser.Show();

        }
        private void startService(EventArgs e, ServiceInfo sInfo)
        {
            if (sInfo.serviceType == ServiceType.EXE || sInfo.serviceType == ServiceType.JAR)
            {
                this.startExe(e, sInfo);

            }
            else if (sInfo.serviceType == ServiceType.WEB)
            {
                this.startWeb(sInfo);

            }

            this.runningService = sInfo;
            this.closeShowingServiceBtn.Content = "Close " + sInfo.serviceName;
            this.enableThisButton(this.closeShowingServiceBtn, true);

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


        internal delegate void loginDelegate(UserSession userSession);

        internal void Login(UserSession userSession)
        {
            if (this.Dispatcher.CheckAccess())
            {
                Console.WriteLine("User: " + userSession.getUserIdentity() + " logging in and loading " + userSession.getServices().Count + " services");

                if (!this.loggedIn)
                {
                    this.notifyIcon1.Text = "Current User: " + userSession.getUserIdentity();
                    this.notifyIcon1.ShowBalloonTip(5000, "SOCIETIES Display Portal", "User " + userSession.getUserIdentity() + " is logging in.", System.Windows.Forms.ToolTipIcon.Info);
                    this.notifyIcon1.Visible = true;

                    //this.enableThisButton(this.closeShowingServiceBtn, true);
                    this.enableThisButton(this.logoutButton, true);
                    this.enableViewingPanel(this.appControl, true);

                    this.userSession = userSession;
                    this.loggedIn = true;
                    List<ServiceInfo> services = userSession.getServices();

                    int i = 0;

                    for (i = 0; i < myButtons.Count; i++)
                    {
                        if (i < services.Count)
                        {
                            ServiceInfo sInfo = services.ElementAt(i);
                            Button button = myButtons.ElementAt(i);
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

        public delegate void setServiceInfoDelegate(Button button, ServiceInfo serviceInfo);

        public void setServiceInfo(Button button, ServiceInfo sInfo)
        {
            if (button.Dispatcher.CheckAccess()){
               button.Tag = sInfo;
                button.Content = sInfo.serviceName;
            }
            else{
                button.Dispatcher.Invoke(new setServiceInfoDelegate(setServiceInfo),button,sInfo);
            }
            //if (button.InvokeRequired)
            //{
            //    button.Invoke(new setServiceInfoDelegate(this.setServiceInfo), button, sInfo);
            //}
            //else
            //{
                //button.Tag = sInfo;
                //button.Content = sInfo.serviceName;
                //button.Text = sInfo.serviceName;
            //}
        }
        

        public delegate void enableThisButtonDelegate(Button button, Boolean enabled);

        public void enableThisButton(Button button, Boolean enabled)
        {
            if (button.Dispatcher.CheckAccess()){
                                button.IsEnabled = enabled;
                if (enabled)
                {
                    button.Visibility = System.Windows.Visibility.Visible;
                }
                else
                {
                    button.Visibility = System.Windows.Visibility.Hidden;
                }
            }else{
                button.Dispatcher.Invoke(new enableThisButtonDelegate(enableThisButton),button,enabled);
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
            if (panel.Dispatcher.CheckAccess()){
                            panel.IsEnabled = enabled;
            if (enabled)
            {
                panel.Visibility = System.Windows.Visibility.Visible;
            }
            else
            {
                panel.Visibility = System.Windows.Visibility.Hidden;
            }
            }else{
                panel.Dispatcher.Invoke(new enableViewingPanelDelegate(enableViewingPanel), panel,enabled);
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

        public delegate void enableApplicationControlDelegate(ApplicationControl.ApplicationControl appCtrl, Boolean enabled);

        public void enableViewingPanel(ApplicationControl.ApplicationControl appCtrl, Boolean enabled){
            if (appCtrl.InvokeRequired){
                appCtrl.Invoke(new enableApplicationControlDelegate(enableViewingPanel), appCtrl, enabled);
            }else{
                appCtrl.Visible = enabled;
                appCtrl.Enabled = enabled;
            }
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

        private void AppClosing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            if (socketServer != null)
            {
                this.logOut();
                this.socketServer.close();
                this.binaryDataTransferServer.close();
                this.loggedIn = false;
                
            }
            else
            {
                Console.WriteLine("SocketService is already null");
            }
        }

        private void AppClosed(object sender, EventArgs e)
        {
            Console.WriteLine("application closed");
        }

        private void serviceButton1_Click(object sender, RoutedEventArgs e)
        {
            
        }
    }
}
