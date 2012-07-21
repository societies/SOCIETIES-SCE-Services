using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Collections.Generic;
using System.ComponentModel;
using System.Windows.Forms;

namespace HWUPortal
{
    class SocketServer
    {
        private volatile portalGUI gui;

        private UserSession userSession;

        private ProtocolSteps protocolStatus = ProtocolSteps.WaitingForSession;

        private const string END_SERVICES = "END_SERVICES";

        private const string START_SERVICE = "START_SERVICE";

        private const string LOGIN = "LOGIN";

        private const string LOGOUT = "LOGOUT";

        private Socket listener;

        private Boolean alive = true;

        private Socket socket;

        
        public SocketServer(portalGUI masterGui)
        {
            this.gui = masterGui;
            
            
        }

        public void run()
        {

            Console.WriteLine("Starting: Creating Socket object");

            listener = new Socket(AddressFamily.InterNetwork,
            SocketType.Stream,
            ProtocolType.Tcp);

            listener.Bind(new IPEndPoint(IPAddress.Any, 2112));
            listener.Listen(10);
            //listener.Blocking = true;
            while (alive)
            {
                try
                {

                    Console.WriteLine("Waiting for connection on port 2112");

                    socket = listener.Accept();

                    string receivedValue = string.Empty;
                    while (alive)
                    {
                        Console.WriteLine(socket.RemoteEndPoint.ToString());
                        byte[] receivedBytes = new byte[1024];
                        int numBytes = socket.Receive(receivedBytes);
                        Console.WriteLine("Receiving .");

                        receivedValue = Encoding.ASCII.GetString(receivedBytes, 0, numBytes);
                        receivedValue = receivedValue.Normalize();
                        Console.WriteLine("Received message: " + receivedValue);
                        if (receivedValue.Equals(String.Empty))
                        {
                            Console.WriteLine("received empty string. ignoring");

                        }
                        else
                        {
                            if (protocolStatus.Equals(ProtocolSteps.WaitingForSession))
                            {
                                if (receivedValue.IndexOf(LOGIN) > -1)
                                {
                                    Console.WriteLine("processing login");
                                    
                                    
                                    Thread.Sleep(1000);
                                    this.protocolStatus = ProtocolSteps.WaitingForJID;

                                }
                            }
                            else if (protocolStatus.Equals(ProtocolSteps.WaitingForJID))
                            {
                                Console.WriteLine("processing jid");
                                
                                Thread.Sleep(1000);
                                this.userSession = new UserSession();
                                userSession.setUserIdentity(receivedValue);
                                this.protocolStatus = ProtocolSteps.WaitingForMoreServices;

                            }
                            else if (protocolStatus.Equals(ProtocolSteps.WaitingForMoreServices))
                            {
                                if (receivedValue.IndexOf(END_SERVICES) > -1)
                                {
                                    Console.WriteLine("processing end services");
                                    
                                    Thread.Sleep(1000);
                                    //now instruct the GUI to load the services
                                    
                                    this.gui.Login(userSession);

                                    this.protocolStatus = ProtocolSteps.InSession;
                                    break;


                                }
                                else
                                {
                                    Console.WriteLine("processing another service");
                                    
                                    Thread.Sleep(1000);
                                    Console.WriteLine("Splitting" + receivedValue);
                                    ServiceInfo sInfo = this.getService(receivedValue);
                                    Console.WriteLine(sInfo.ToString());
                                    this.downloadFile(sInfo);
                                    //userSession is updated when the file download is complete see: private void Completed(object sender, AsyncCompletedEventArgs e)
                                }
                            }
                            else if (protocolStatus.Equals(ProtocolSteps.InSession))
                            {

                                if (receivedValue.IndexOf(START_SERVICE) > -1)
                                {
                                    Console.WriteLine("processing start service");
                                    String serviceName = this.getServiceName(receivedValue);
                                    this.gui.startService(serviceName);
                                }
                                else
                                {
                                    if (receivedValue.IndexOf(LOGOUT) > -1)
                                    {
                                        Console.WriteLine("processing logout");
                                        if (this.endSession(receivedValue, userSession.getUserIdentity()))
                                        {
                                            this.protocolStatus = ProtocolSteps.WaitingForSession;
                                            this.userSession = new UserSession();
                                            this.gui.logOut();
                                        }
                                        break;
                                    }

                                }
                            }

                            if (receivedValue.IndexOf("[FINAL]") > -1)
                            {
                                break;
                            }
                        }
                    }

                }

                catch (Exception exc)
                {
                    Console.WriteLine(exc.ToString());
                    listener.Close();
                }
            }
            
        }

        private void downloadFile(ServiceInfo sInfo)
        {
            String userProfile = System.Environment.GetEnvironmentVariable("USERPROFILE");
            String directory = userProfile + @"\Downloads\";
            String fileName = this.extractFilename(sInfo.serviceURL);

            String pathToExe = directory + fileName;
            sInfo.serviceExe = pathToExe;

            if (fileName.EndsWith(".exe"))
            {
                sInfo.serviceType = ServiceType.EXE;
                WebClient webClient = new WebClient();
                webClient.DownloadFileCompleted += new AsyncCompletedEventHandler(Completed);
                webClient.DownloadProgressChanged += new DownloadProgressChangedEventHandler(ProgressChanged);
                webClient.DownloadFileAsync(new Uri(sInfo.serviceURL), pathToExe, sInfo);
            }
            else
            {
                sInfo.serviceType = ServiceType.WEB;
            }
            Console.WriteLine(sInfo.ToString());
        }

        private string extractFilename(string path)
        {
            String[] list = path.Split('/');
            return list[list.Length - 1];


        }
        private void ProgressChanged(object sender, DownloadProgressChangedEventArgs e)
        {
            //progressBar.Value = e.ProgressPercentage;
        }

        private void Completed(object sender, AsyncCompletedEventArgs e)
        {
            ServiceInfo sInfo = (ServiceInfo)e.UserState;
            this.userSession.addService(sInfo);
            //MessageBox.Show("Download completed!");
        }
        private enum ProtocolSteps
        {
            WaitingForSession, WaitingForJID, WaitingForMoreServices, InSession
        }

        private ServiceInfo getService(String str)
        {
            Console.WriteLine("Spliting: " + str);
            String[] strs = str.Split(',');
            Console.WriteLine("split into: "+strs.Length);
            foreach (String s in strs){
                Console.WriteLine("Split: " + s);
            }
            ServiceInfo info = new ServiceInfo();
            if (strs.Length == 2)
            {

                info.serviceName = strs[0].Trim();
                info.serviceURL = strs[1].Trim();

                return info;
            }

            info.serviceName = string.Empty;
            return info;
        }

        private Boolean endSession(String input, String jid)
        {
            String[] strs = input.Split(' ');

            if (strs.Length == 2)
            {
                if (strs[0].Equals(LOGOUT))
                {
                    if (strs[1].Equals(jid))
                    {
                        return true;
                    }
                }
            }

            return false;
        }

        private String getServiceName(String input)
        {
            String serviceName = input.Replace(START_SERVICE, "");
            return serviceName.Trim();
        }


        internal void close()
        {
            if (this.listener != null)
            {
                try
                {
                    alive = false;


                 
                    if (listener.Connected)
                    {
                        listener.Disconnect(true);
                    }
                    
                    //listener.Shutdown(SocketShutdown.Both);
                    listener.Dispose();
                    socket.Close();
                   
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.ToString());
                }
            }
        }
    }


}