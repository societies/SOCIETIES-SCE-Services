using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;
namespace HWUPortal
{
    class ServiceInformationSocketClient
    {
        protected static log4net.ILog log = log4net.LogManager.GetLogger(typeof(ServiceInformationSocketClient));

        public static readonly String started_Service = "STARTED_SERVICE";
        public static readonly String stopped_Service = "STOPPED_SERVICE";
        public static readonly String logged_Out = "LOGGED_OUT";
        [Flags]
        public enum ServiceRuntimeInformation
        {
            STARTED_SERVICE, STOPPED_SERVICE
        }


        public void sendLogoutEvent(IPAddress remoteIPAddress, int port)
        {
            IPEndPoint ip = new IPEndPoint(remoteIPAddress, port);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                log.Error("Unable to connect to server.", e);
                return;
            }
            server.Send(Encoding.ASCII.GetBytes(logged_Out));
            server.Close();
        }
        public void sendServiceInformationEvent(IPAddress remoteIPAddress, int port, String serviceName, ServiceRuntimeInformation sInformation)
        {
            
                Console.WriteLine(DateTime.Now + "\t" +"Sending serviceInformation message: " + sInformation + " for service: " + serviceName + " using: " + remoteIPAddress + ":" + port);
            IPEndPoint ip = new IPEndPoint(remoteIPAddress, port);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

            try
            {
                server.Connect(ip);
                
                    Console.WriteLine(DateTime.Now + "\t" +"Sending :  " + sInformation.ToString() + ":" + serviceName);
                server.Send(Encoding.ASCII.GetBytes(sInformation.ToString() + ":" + serviceName));
                
                    Console.WriteLine(DateTime.Now + "\t" +"message sent");
                server.Close();

                
                    Console.WriteLine(DateTime.Now + "\t" +"socket closed");

            }
            catch (SocketException e)
            {
                log.Error("Exception while trying to send service information message over socket", e);
            }
            //string input = Console.ReadLine();
            //if (input == "exit")
            //    break;

            //byte[] data = new byte[1024];
            //int receivedDataLength = server.Receive(data);
            //string stringData = Encoding.ASCII.GetString(data, 0, receivedDataLength);



        }
        public static void main()
        {
            ServiceRuntimeInformation sinfo = new ServiceRuntimeInformation();
            sinfo = ServiceRuntimeInformation.STARTED_SERVICE;
            
                Console.WriteLine(DateTime.Now + "\t" + " "+ sinfo);
        }
    }


}
