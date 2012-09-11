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

        public static readonly enum ServiceRuntimeInformation
        {
            STARTED_SERVICE=started_Service, STOPPED_SERVICE=stopped_Service
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
            server.Send(Encoding.ASCII.GetBytes(sInformation.ToString()+":"+serviceName));
            //byte[] data = new byte[1024];
            //int receivedDataLength = server.Receive(data);
            //string stringData = Encoding.ASCII.GetString(data, 0, receivedDataLength);
            
            server.Close();
            
        }
    }
}
