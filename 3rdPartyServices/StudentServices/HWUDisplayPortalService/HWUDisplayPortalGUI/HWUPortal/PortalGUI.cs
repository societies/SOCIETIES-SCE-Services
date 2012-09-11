using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Threading;

namespace HWUPortal
{
    public partial class portalGUI : Form
    {
        WebBrowser webBrowser;

        private ServiceInfo runningService;
        private UserSession userSession = new UserSession();
        private List<Button> myButtons;
        //SocketServer socketServer;
        SocketServerV2 socketServer;
        BinaryDataTransfer binaryDataTransferServer;
        ImageViewer iViewer;
        private Boolean loggedIn = false;
        TextNotificationsWindow notificationsWindow;
        public portalGUI()
        {
            iViewer = new ImageViewer();
            InitializeComponent();
            this.runningService = new ServiceInfo();
            //this.binaryDataTransferServer = new BinaryDataTransfer();
            socketServer = new SocketServerV2(this);
            Thread socketThread = new Thread(socketServer.run);
            socketThread.Start();

            binaryDataTransferServer = new BinaryDataTransfer(this);
            Thread binaryTransferThread = new Thread(binaryDataTransferServer.run);
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
                button.Visible = false;
                button.Enabled = false;
            }


        }


        private void stopService(EventArgs e, ServiceInfo sInfo)
        {
            if (sInfo.serviceType == ServiceType.EXE || sInfo.serviceType == ServiceType.JAR)
            {
                applicationControl.ExeName = sInfo.serviceExe;
                applicationControl.DestroyExe(e);

            }
            else if (sInfo.serviceType == ServiceType.WEB)
            {
                if (this.webBrowser != null)
                {
                    if (!this.webBrowser.IsDisposed)
                    {
                        this.webBrowser.Dispose();


                    }
                }
            }

            this.runningService = new ServiceInfo();
        }



        public delegate void startServiceDelegate(String serviceName);

        public void startService(string serviceName)
        {
            ServiceInfo sInfo = this.userSession.getService(serviceName);
            if (sInfo != null)
            {
                Console.WriteLine(serviceName + " starting now");
                if (sInfo.button.InvokeRequired)
                {
                    sInfo.button.Invoke(new startServiceDelegate(this.startService), serviceName);
                }
                else
                {
                    sInfo.button.PerformClick();
                }

                this.runningService = sInfo;
            }
            else
            {
                Console.WriteLine(serviceName + " service doesn't exist in session");
            }
        }


        public delegate void enableServiceButtonDelegate(Button button, Boolean enabled);
        public void enableServiceButton(Button button, Boolean enabled)
        {
            if (button.InvokeRequired)
            {
                button.Invoke(new enableServiceButtonDelegate(this.enableServiceButton), button, enabled);
            }
            else
            {
                button.Enabled = enabled;
                button.Visible = enabled;

            }
        }

        public delegate void setServiceInfoDelegate(Button button, ServiceInfo serviceInfo);

        public void setServiceInfo(Button button, ServiceInfo sInfo)
        {
            if (button.InvokeRequired)
            {
                button.Invoke(new setServiceInfoDelegate(this.setServiceInfo), button, sInfo);
            }
            else
            {
                button.Tag = sInfo;
                button.Text = sInfo.serviceName;
            }
        }

        public delegate void enableThisButtonDelegate(Button button, Boolean enabled);

        public void enableThisButton(Button button, Boolean enabled)
        {
            if (button.InvokeRequired)
            {
                button.Invoke(new enableThisButtonDelegate(this.enableThisButton), button, enabled);
            }
            else
            {
                button.Enabled = enabled;
                button.Visible = enabled;
            }
        }

        public delegate void enableViewingPanelDelegate(Panel panel, Boolean enabled);
        public void enableViewingPanel(Panel panel, Boolean enabled)
        {
            if (panel.InvokeRequired)
            {
                panel.Invoke(new enableViewingPanelDelegate(this.enableViewingPanel), panel, enabled);
            }
            else
            {
                panel.Enabled = enabled;
                panel.Visible = enabled;
            }
        }


        internal delegate void loginDelegate(UserSession userSession);

        internal void Login(UserSession userSession)
        {
            if (this.InvokeRequired)
            {
                Invoke(new loginDelegate(this.Login), userSession);
            }
            else
            {
                Console.WriteLine("User: " + userSession.getUserIdentity() + " logging in and loading " + userSession.getServices().Count + " services");

                if (!this.loggedIn)
                {
                    this.notifyIcon1.Text = "Current User: " + userSession.getUserIdentity();
                    this.notifyIcon1.ShowBalloonTip(5000, "SOCIETIES Display Portal", "User " + userSession.getUserIdentity() + " is logging in.", ToolTipIcon.Info);
                    this.notifyIcon1.Visible = true;

                    //this.enableThisButton(this.closeShowingServiceBtn, true);
                    this.enableThisButton(this.logoutButton, true);
                    this.enableViewingPanel(this.applicationControl, true);
                  
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
                            this.enableServiceButton(button, true);
                            sInfo.button = button;
                            this.setServiceInfo(button, sInfo);
                        }
                        else
                        {
                            this.enableServiceButton(myButtons.ElementAt(i), false);

                        }
                    }

                }
                else
                {
                    Console.WriteLine("User already logged in");
                }
            }
        }

        private void startExe(EventArgs e, ServiceInfo sInfo)
        {

            applicationControl.ExeName = sInfo.serviceExe;
            applicationControl.LoadExe(e);

        }

        private void startWeb(ServiceInfo sInfo)
        {
            webBrowser = new WebBrowser();
            webBrowser.Size = applicationControl.Size;
            applicationControl.Controls.Add(webBrowser);
            webBrowser.Url = new Uri(sInfo.serviceURL);

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
            this.closeShowingServiceBtn.Text = "Close " + sInfo.serviceName;
            this.enableThisButton(this.closeShowingServiceBtn, true);

            ServiceInformationSocketClient sClient = new ServiceInformationSocketClient();
            sClient.sendServiceInformationEvent(userSession.getIPAddress(), sInfo.serviceName, ServiceInformationSocketClient.ServiceRuntimeInformation.STARTED_SERVICE);
            
        }
        private void serviceButton1_Click(object sender, EventArgs e)
        {
            this.startService(e, (ServiceInfo)serviceButton1.Tag);
        }

        private void serviceButton2_Click(object sender, EventArgs e)
        {
            this.startService(e, (ServiceInfo)serviceButton2.Tag);
        }

        private void serviceButton3_Click(object sender, EventArgs e)
        {

            this.startService(e, (ServiceInfo)serviceButton3.Tag);

        }

        private void serviceButton4_Click(object sender, EventArgs e)
        {
            this.startService(e, (ServiceInfo)serviceButton4.Tag);

        }
        private void serviceButton5_Click(object sender, EventArgs e)
        {
            this.startService(e, (ServiceInfo)serviceButton5.Tag);
        }

        private void closeShowingService_Click(object sender, EventArgs e)
        {
            if (!this.runningService.serviceName.Equals(string.Empty))
            {
                this.stopService(e, this.runningService);
                this.runningService = new ServiceInfo();
                this.closeShowingServiceBtn.Text = "";
                this.enableThisButton(this.closeShowingServiceBtn, false);
                ServiceInformationSocketClient socket = new ServiceInformationSocketClient();
                socket.sendServiceInformationEvent(this.userSession.getIPAddress(), this.runningService.serviceName, ServiceInformationSocketClient.ServiceRuntimeInformation.STOPPED_SERVICE);
            }
        }

        internal delegate void showImageDelegate(String location);
        internal void showImage(String location)
        {
            if (this.InvokeRequired)
            {
                this.Invoke(new showImageDelegate(this.showImage), location);
            }
            else
            {
                if (iViewer.IsDisposed)
                {
                    iViewer = new ImageViewer();
                }
                iViewer.setImage(location);
                
                iViewer.Show(this);
            }
        }


        private void logoutButton_Click(object sender, EventArgs e)
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

                this.userSession = new UserSession();
                foreach (Button button in myButtons)
                {
                    this.enableServiceButton(button, false);
                }
                this.enableViewingPanel(applicationControl, false);
                this.enableThisButton(this.closeShowingServiceBtn, false);
                this.enableThisButton(this.logoutButton, false);
                
                Console.WriteLine("User: " + user + " is logged out");
                this.notifyIcon1.ShowBalloonTip(5000, "SOCIETIES Display Portal", "User: " + user + " has logged out", ToolTipIcon.Info);
                this.loggedIn = false;
            }
            else
            {
                Console.WriteLine("user already logged out");
            }

        }



        internal delegate void logOutDelegate();
        internal void logOut()
        {
            if (this.InvokeRequired)
            {
                this.Invoke(new logOutDelegate(this.logOut));
            }
            else
            {
                logoutButton.PerformClick();
            }
            
        }

        private void portalGUI_Closed(object sender, FormClosingEventArgs e)
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


        internal delegate void addNewNotificationDelegate(String serviceName, String text);
        internal void addNewNotification(String serviceName, string text)
        {
            if (this.InvokeRequired)
            {
                this.Invoke(new addNewNotificationDelegate(this.addNewNotification), serviceName, text);
            }
            else
            {
                if (this.notificationsWindow.IsDisposed)
                {
                    this.notificationsWindow = new TextNotificationsWindow();


                }
                this.notificationsWindow.addNewNotification(serviceName, text);
                this.notificationsWindow.Show();
            }

        }

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
            }else if (user.ToLower().IndexOf(this.userSession.getUserIdentity().ToLower()) >-1){
                return true;
            }

            return false;
        }

    }



}
