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
                Console.WriteLine("Unable to connect to server.");
                return;
            }
            server.Send(Encoding.ASCII.GetBytes(logged_Out));
            server.Close();
        }
        public void sendServiceInformationEvent(IPAddress remoteIPAddress, String serviceName, ServiceRuntimeInformation sInformation)
        {
            IPEndPoint ip = new IPEndPoint(remoteIPAddress, 2121);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine("Unable to connect to server.");
                return;
            }
            //string input = Console.ReadLine();
            //if (input == "exit")
            //    break;
            server.Send(Encoding.ASCII.GetBytes(sInformation.ToString() + ":" + serviceName));
            //byte[] data = new byte[1024];
            //int receivedDataLength = server.Receive(data);
            //string stringData = Encoding.ASCII.GetString(data, 0, receivedDataLength);

            server.Close();

        }
        public static void main()
        {
            ServiceRuntimeInformation sinfo = new ServiceRuntimeInformation();
            sinfo = ServiceRuntimeInformation.STARTED_SERVICE;
            Console.WriteLine(sinfo);
        }
    }


}
