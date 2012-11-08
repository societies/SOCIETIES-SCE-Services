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
        public void sendServiceInformationEvent(IPAddress remoteIPAddress, int port, String serviceName, ServiceRuntimeInformation sInformation)
        {
            Console.WriteLine("Sending serviceInformation message: "+sInformation+" for service: "+serviceName+" using: "+remoteIPAddress+":"+port);
            IPEndPoint ip = new IPEndPoint(remoteIPAddress, port);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

            try
            {
                server.Connect(ip);
                Console.WriteLine("Sending :  " + sInformation.ToString() + ":" + serviceName);
                server.Send(Encoding.ASCII.GetBytes(sInformation.ToString() + ":" + serviceName));
                Console.WriteLine("message sent");
                server.Close();

                Console.WriteLine("socket closed");
                
            }
            catch (SocketException e)
            {
                Console.WriteLine("Exception while trying to send service information message over socket. \n"+e.Message);
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
            Console.WriteLine(sinfo);
        }
    }


}
