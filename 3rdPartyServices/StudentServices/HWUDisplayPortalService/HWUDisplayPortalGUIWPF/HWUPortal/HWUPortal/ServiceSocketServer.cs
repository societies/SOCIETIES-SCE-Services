using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace ServiceSocketServer
{
    class ServiceSocketServer
    {





        private Socket listener;

        private Boolean alive = true;

        private Socket socket;


        public void run()
        {

            Console.WriteLine("Starting: Creating Socket object");

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

                    Console.WriteLine("Waiting for connection on port 2114");

                    socket = listener.Accept();

                    string receivedValue = string.Empty;
                    Boolean finishedReceiving = false;
                    while (!finishedReceiving)
                    {
                        Console.WriteLine(socket.RemoteEndPoint.ToString());
                        byte[] receivedBytes = new byte[1024];
                        Console.WriteLine("waiting to receive");
                        int numBytes = socket.Receive(receivedBytes);
                        Console.WriteLine("Receiving .");

                        receivedValue = Encoding.ASCII.GetString(receivedBytes, 0, numBytes);
                        receivedValue = receivedValue.Normalize();
                        Console.WriteLine("Received message: " + receivedValue);
                        if (receivedValue.Equals(String.Empty))
                        {
                            Console.WriteLine("received empty string. ignoring");

                        }
                        else if (receivedValue.IndexOf("CURRENT_USER") > -1)
                        {
                            byte[] jidInBytes = Encoding.ASCII.GetBytes("laura@societies.local");
                            socket.Send(jidInBytes);
                            finishedReceiving = true;
                        }
                        receivedBytes = new byte[1024];
                    }

                }

                catch (Exception exc)
                {
                    Console.WriteLine(exc.ToString());
                    listener.Close();
                }
            }

        }
    }
}
