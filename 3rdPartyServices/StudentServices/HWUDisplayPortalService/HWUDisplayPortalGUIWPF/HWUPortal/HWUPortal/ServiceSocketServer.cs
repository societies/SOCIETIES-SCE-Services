using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Windows.Controls;

namespace HWUPortal
{
    class ServiceSocketServer
    {

        protected static log4net.ILog log = log4net.LogManager.GetLogger(typeof(ServiceSocketServer));


        private UserSession currentUserSession;

        private Socket listener;

        private Boolean alive = true;

        private Socket socket;

        private MainWindow mainWindow;

        public void setUserSession(UserSession session, MainWindow mainWindow)
        {
            this.currentUserSession = session;
            this.mainWindow = mainWindow;
        }


        public void run()
        {

            if (log.IsDebugEnabled) log.Debug("Starting: Creating Socket object");

            listener = new Socket(AddressFamily.InterNetwork,
            SocketType.Stream,
            ProtocolType.Tcp);

            listener.Bind(new IPEndPoint(IPAddress.Any, 2114));
            listener.Listen(10);
            //listener.Blocking = true;
            while (alive)
            {
                try
                {

                    if (log.IsDebugEnabled) log.Debug("Waiting for connection on port 2114");

                    socket = listener.Accept();

                    string receivedValue = string.Empty;
                    Boolean finishedReceiving = false;
                    while (!finishedReceiving)
                    {
                        if (log.IsDebugEnabled) log.Debug(socket.RemoteEndPoint.ToString());
                        byte[] receivedBytes = new byte[1024];
                        if (log.IsDebugEnabled) log.Debug("waiting to receive");
                        int numBytes = socket.Receive(receivedBytes);
                        if (log.IsDebugEnabled) log.Debug("Receiving .");

                        receivedValue = Encoding.ASCII.GetString(receivedBytes, 0, numBytes);
                        receivedValue = receivedValue.Normalize();
                        if (log.IsDebugEnabled) log.Debug("Received message: " + receivedValue);
                        if (receivedValue.Equals(String.Empty))
                        {
                            if (log.IsDebugEnabled) log.Debug("received empty string. ignoring");

                        }
                        else if (receivedValue.IndexOf("CURRENT_USER") > -1)
                        {
                            byte[] jidInBytes = Encoding.ASCII.GetBytes(this.currentUserSession.getUserIdentity());

                            socket.Send(jidInBytes);
                            finishedReceiving = true;
                        }
                        else if (receivedValue.IndexOf("VIRGO_ENDPOINT_IPADDRESS") > -1)
                        {
                            byte[] addressInBytes = this.currentUserSession.getIPAddress().GetAddressBytes();
                            socket.Send(addressInBytes);
                            finishedReceiving = true;
                        }
                        else if (receivedValue.IndexOf("SERVICE_PORT") > -1)
                        {

                            String serviceName = receivedValue.Remove(0, "SERVICE_PORT->".Length);
                            ServiceInfo sInfo = currentUserSession.getService(serviceName);
                            if (sInfo == null)
                            {
                                byte[] errorInBytes = Encoding.ASCII.GetBytes("Invalid_Service_Name");
                                socket.Send(errorInBytes);
                                finishedReceiving = true;
                            }
                            else
                            {
                                byte[] portInBytes = BitConverter.GetBytes(sInfo.servicePortNumber);
                                socket.Send(portInBytes);
                                finishedReceiving = true;
                            }
                        }
                        else if (receivedValue.IndexOf("STOP_SERVICE") > -1)
                        {
                            String serviceName = receivedValue.Remove(0, "STOP_SERVICE->".Length);
                            ServiceInfo sInfo = currentUserSession.getService(serviceName);
                            if (sInfo == null)
                            {
                                byte[] errorInBytes = Encoding.ASCII.GetBytes("Invalid_Service_Name");
                                socket.Send(errorInBytes);
                                finishedReceiving = true;
                            }
                            else
                            {
                                byte[] okInBytes = Encoding.ASCII.GetBytes("OK");

                                if (log.IsDebugEnabled) log.Debug("Stopping service: " + sInfo.serviceName);
                                socket.Send(okInBytes);
                                finishedReceiving = true;
                                mainWindow.stopService(new System.Windows.RoutedEventArgs(Button.ClickEvent), sInfo);
                            }
                        }
                        receivedBytes = new byte[1024];
                    }

                }

                catch (Exception exc)
                {
                    log.Error("", exc);
                    listener.Close();
                    alive = false;
                }
            }

        }
    }
}
