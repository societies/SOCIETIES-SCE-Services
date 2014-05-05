﻿using System;
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
        int port = 4322;
        private volatile bool listening = true;

        private static String USER_SESSION_STARTED = "USER_SESSION_STARTED";
        private static String USER_SESSION_ENDED = "USER_SESSION_ENDED";

        private MainWindow window;

        public SocketServer(MainWindow window)
        {
            this.window = window;

            String ok = "RECEIVED\n";
            String notOK = "FAILED\n";
            okBytes = System.Text.Encoding.ASCII.GetBytes(ok.ToCharArray());
            notOKBytes = System.Text.Encoding.ASCII.GetBytes(notOK);
        }

        public void run()
        {
            while (listening)
            {
                listenSocket();
            }
        }

        public void stopSocketServer()
        {
            listening = false;
            try{
                if (client != null)
                {
                    client.Close();
                }
                server.Stop();
            }
            catch(Exception e)
            {
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
            }
        }

        public void listenSocket()
        {
            try
            {
                server = new TcpListener(IPAddress.Any, port);
                server.Start();
            }
            catch (Exception e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_SERVER: Could not listen on port: " + port);
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return;
            }


            try
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_SERVER: Waiting for connection from service client on port: " + port);
                client = server.AcceptTcpClient();
            }
            catch (Exception e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_SERVER: Accept failed: " + port);
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                server.Stop();
                return;
            }

            Console.WriteLine(DateTime.Now + "\t" +"SOCKET_SERVER: Connected accepted from service client!");

            // Buffer for reading data
            byte[] bytes = new byte[1024];
            string data = "";

            try
            {
                // Get a stream object for reading and writing
                stream = client.GetStream();
                int i = stream.Read(bytes, 0, bytes.Length);
                data = System.Text.Encoding.ASCII.GetString(bytes, 0, i);
                while (i == 1024)
                {
                    i = stream.Read(bytes, 0, bytes.Length);
                    Console.WriteLine(DateTime.Now + "\t" + String.Format("Reading data from stream", data));
                    data = data +System.Text.Encoding.ASCII.GetString(bytes, 0, i);
                }
               // int i = stream.Read(bytes, 0, bytes.Length);
                if (data.Length>0)
                {
                    // Translate data bytes to a ASCII string.
                    //data = System.Text.Encoding.ASCII.GetString(bytes, 0, i);
                    Console.WriteLine(DateTime.Now + "\t" +String.Format("SOCKET_SERVER: got new input: {0}", data));

                    if (data.IndexOf("START_MSG") > -1)
                    {
                        Console.WriteLine(DateTime.Now + "\t" +"SOCKET_SERVER: Processing new message...");

                        String[] splitData = data.Split('\n');
                        String command = splitData[1];

                        if (command.Equals(USER_SESSION_STARTED))
                        {
                            Console.WriteLine(DateTime.Now + "\t" +"SOCKET_SERVER: "+USER_SESSION_STARTED + " message received");
                            stream.Write(okBytes, 0, okBytes.Length);
                        }
                        else if (command.Equals(USER_SESSION_ENDED))
                        {
                            //not used
                            Console.WriteLine(DateTime.Now + "\t" +"SOCKET_SERVER: "+USER_SESSION_ENDED + "message received");
                            stream.Write(okBytes, 0, okBytes.Length);
                            //close down
                            //window.Close();
                        }
                        else if (command.Equals("ACTIVITY_FEED"))
                        {
                            String activityString = splitData[2];
                            List<MarshaledActivity> activities = Newtonsoft.Json.JsonConvert.DeserializeObject<List<MarshaledActivity>>(activityString);
                            stream.Write(okBytes, 0, okBytes.Length);
                            this.window.updateActivities(activities);
                        }
                        else
                        {
                            Console.WriteLine(DateTime.Now + "\t" + "SOCKET_SERVER: Unknown command received from service client");
                            stream.Write(notOKBytes, 0, notOKBytes.Length);
                        }
                    }
                    client.Close();
                    server.Stop();
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                client.Close();
                server.Stop();
            }
        }
                        

        //                Boolean receivingText = false;
                        
        //                    receivingText = true;
                            

        //                    while (receivingText)
        //                    {
        //                        while (i != 0)
        //                        {
        //                            String[] splitData = data.Split('\n');
        //                            String commandType = splitData[1];

        //                            //process command
        //                            if (commandType == "CLIENT_IP")
        //                            {
        //                                Console.WriteLine(DateTime.Now + "\t" +"Processing IP of service client");
        //                                String clientIp = splitData[2];
        //                                Console.WriteLine(DateTime.Now + "\t" +"Client IP is: " + clientIp);
        //                                //socketClient.connectToServiceClient(clientIp);
        //                            }
        //                            else if (commandType == "PREF_OUTCOME")
        //                            {
        //                                Console.WriteLine(DateTime.Now + "\t" +"Processing preference outcome");
        //                                String outcome = splitData[2];
        //                                Console.WriteLine(DateTime.Now + "\t" +"Preference update is: " + outcome);
        //                            }

        //                            if (data.IndexOf("END_MSG") > -1)
        //                            {
        //                                receivingText = false;
        //                                i = 0;
        //                            }
        //                            else
        //                            {
        //                                Console.WriteLine(DateTime.Now + "\t" +"reading from stream");
        //                                i = stream.Read(bytes, 0, bytes.Length);
        //                                data = System.Text.Encoding.ASCII.GetString(bytes, 0, i);
        //                            }
        //                        }
        //                    }
        //                    stream.Write(okBytes, 0, okBytes.Length);
        //                }
        //                else
        //                {
        //                    stream.Write(notOKBytes, 0, notOKBytes.Length);
        //                }

        //                i = stream.Read(bytes, 0, bytes.Length);
        //            }

        //            // Shutdown and end connection
        //            client.Close();
        //        }
        //    }
        //    catch (SocketException e)
        //    {
        //        Console.WriteLine(DateTime.Now + "\t" +e.ToString());
        //        client.Close();
        //        server.Stop();
        //    }
        //    catch (Exception e)
        //    {
        //        Console.WriteLine(DateTime.Now + "\t" +e.ToString());
        //        client.Close();
        //        server.Stop();
        //    }
        //}



    }
}
