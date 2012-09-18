using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using System.ComponentModel;

namespace MyTvUI
{
    class SocketServer
    {
        private volatile TcpListener server;
        private volatile TcpClient client;

        private NetworkStream stream;
        private byte[] okBytes;
        private byte[] notOKBytes;
        private volatile bool acceptingConnections = true;
        //private volatile bool connected = false;

        public SocketServer()
        {
            String ok = "RECEIVED\n";
            String notOK = "FAILED\n";
            okBytes = System.Text.Encoding.ASCII.GetBytes(ok.ToCharArray());
            notOKBytes = System.Text.Encoding.ASCII.GetBytes(notOK);
        }

        public void run()
        {
            try
            {
                // set the TcpListener on port 4322
                int port = 4322;
                server = new TcpListener(IPAddress.Any, port);

                // Start listening for client requests
                server.Start();

                // Buffer for reading data
                byte[] bytes = new byte[1024];
                string data;

                //Enter the listening loop
                while (acceptingConnections)
                {
                    Console.Write("Waiting for a connection... ");

                    // Perform a blocking call to accept requests.
                    // You could also user server.AcceptSocket() here.
                    client = server.AcceptTcpClient();
                    Console.WriteLine("Connected!");

                    // Get a stream object for reading and writing
                    stream = client.GetStream();

                    int i;

                    // Loop to receive all the data sent by the client.
                    i = stream.Read(bytes, 0, bytes.Length);

                    while (i != 0)
                    {
                        // Translate data bytes to a ASCII string.
                        data = System.Text.Encoding.ASCII.GetString(bytes, 0, i);
                        Console.WriteLine(String.Format("Received: {0}", data));

                        Boolean receivingText = false;
                        if (data.IndexOf("START_MSG") > -1)
                        {
                            receivingText = true;
                            Console.WriteLine("Processing message");

                            while (receivingText)
                            {
                                while (i != 0)
                                {
                                    String[] splitData = data.Split('\n');
                                    String commandType = splitData[1];

                                    //process command
                                    if (commandType == "CLIENT_IP")
                                    {
                                        Console.WriteLine("Processing IP of service client");
                                        String clientIp = splitData[2];
                                        Console.WriteLine("Client IP is: " + clientIp);
                                        //socketClient.connectToServiceClient(clientIp);
                                    }
                                    else if (commandType == "PREF_OUTCOME")
                                    {
                                        Console.WriteLine("Processing preference outcome");
                                        String outcome = splitData[2];
                                        Console.WriteLine("Preference update is: " + outcome);
                                    }

                                    if (data.IndexOf("END_MSG") > -1)
                                    {
                                        receivingText = false;
                                        i = 0;
                                    }
                                    else
                                    {
                                        Console.WriteLine("reading from stream");
                                        i = stream.Read(bytes, 0, bytes.Length);
                                        data = System.Text.Encoding.ASCII.GetString(bytes, 0, i);
                                    }
                                }
                            }
                            stream.Write(okBytes, 0, okBytes.Length);
                        }
                        else
                        {
                            stream.Write(notOKBytes, 0, notOKBytes.Length);
                        }

                        i = stream.Read(bytes, 0, bytes.Length);
                    }

                    // Shutdown and end connection
                    client.Close();
                }
            }
            catch (SocketException e)
            {
                Console.WriteLine(e.ToString());
                client.Close();
                server.Stop();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
                client.Close();
                server.Stop();
            }
        }
    }
}
