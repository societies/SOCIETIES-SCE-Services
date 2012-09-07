using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;
namespace SocialLearningGame
{
    class SocketClient
    {
        public String getUserIdentity()
        {
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 2114);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine("Unable to connect to server.");
                return "";
            }
            //string input = Console.ReadLine();
            //if (input == "exit")
            //    break;
            server.Send(Encoding.ASCII.GetBytes("CURRENT_USER"));
            byte[] data = new byte[1024];
            int receivedDataLength = server.Receive(data);
            string stringData = Encoding.ASCII.GetString(data, 0, receivedDataLength);
            Console.WriteLine("Received user identity from server: "+ stringData);
            server.Close();
            return stringData;
        }

    }

    
}
