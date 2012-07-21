using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using System.ComponentModel;

namespace HWUPortal
{
    class SocketServerV2
    {
        portalGUI gui;
        UserSession userSession;
        TcpListener server;

        int downloadedServices = 0;
        int numOfServices = 0;
        
        public SocketServerV2(portalGUI portalGui)
        {
            this.gui = portalGui;
        }
        public void run()
        {
            try
            {
                // set the TcpListener on port 2112
                int port = 2112;
                server = new TcpListener(IPAddress.Any, port);

                // Start listening for client requests
                server.Start();


                // Buffer for reading data
                byte[] bytes = new byte[1024];
                string data;

                //Enter the listening loop
                while (true)
                {
                    Console.Write("Waiting for a connection... ");

                    // Perform a blocking call to accept requests.
                    // You could also user server.AcceptSocket() here.
                    TcpClient client = server.AcceptTcpClient();
                    Console.WriteLine("Connected!");

                    // Get a stream object for reading and writing
                    NetworkStream stream = client.GetStream();

                    int i;

                    // Loop to receive all the data sent by the client.
                    i = stream.Read(bytes, 0, bytes.Length);

                    while (i != 0)
                    {
                        // Translate data bytes to a ASCII string.
                        data = System.Text.Encoding.ASCII.GetString(bytes, 0, i);
                        Console.WriteLine(String.Format("Received: {0}", data));
                        String text = "";
                        String serviceName = "";
                        if (data.IndexOf("SHOW_TEXT") > -1)
                        {
                            Console.WriteLine("Processing text");
                            
                            while (i != 0)
                            {
                                
                                if (data.IndexOf("SHOW_TEXT") > -1)
                                {
                                    Console.WriteLine("Processing SHOW_TEXT");
                                    String[] splitData = data.Split('\n');
                                    String userId = splitData[1];
                                    serviceName = splitData[2];
                                    Console.WriteLine("userId : "+userId);
                                    if (userId.IndexOf(this.userSession.getUserIdentity()) > -1)
                                    {
                                        Console.WriteLine("Starting to show text\n\n");
                                        for (int n = 3; n < splitData.Length; n++)
                                        {
                                            Console.WriteLine("concatenating text line: "+n);
                                            text = string.Concat(text, splitData[n]);
                                            //this.gui.appendText(splitData[n]);
                                            
                                            Console.WriteLine(String.Format("Concatenating text line: \n{0}", splitData[n]));
                                        }
                                    }

                                }
                                else
                                {
                                    Console.WriteLine("processing another 1024 bytes");
                                    
                                    

                                    Console.WriteLine(String.Format("Adding to txtBox: {0}", data));
                                    text = string.Concat(text, data);
                                    //this.gui.appendText(data);
                                }
                                i = stream.Read(bytes, 0, bytes.Length);
                                data = System.Text.Encoding.ASCII.GetString(bytes, 0, i);
                                
                                
                                Console.WriteLine("\nText for notification panel:\n" + text + "\nEnds text\n");
                            }
                            //this.gui.showText(text);
                            this.gui.addNewNotification(serviceName, text);
                        }
                        else
                        {
                            if (!this.gui.isLoggedIn())
                            {
                                this.createUserSession(data);
                            }
                            else
                            {
                                this.processInSessionCommand(data);
                            }
                        }

                        // Process the data sent by the client.
                        //data = data.ToUpper();

                        //byte[] msg = System.Text.Encoding.ASCII.GetBytes(data);

                        // Send back a response.
                        //stream.Write(msg, 0, msg.Length);
                        //Console.WriteLine(String.Format("Sent: {0}", data));

                        i = stream.Read(bytes, 0, bytes.Length);

                    }

                    // Shutdown and end connection
                    client.Close();
                }
            }
            catch (SocketException e)
            {
                Console.WriteLine(e.ToString());
            }


            Console.WriteLine("Hit enter to continue...");
            Console.Read();
        }

        private void processingText(String input)
        {

        }
        private void processInSessionCommand(string input)
        {
            String[] lines = (input.Split('\n'));
            String text = string.Empty;
            if (lines.Length > 1)
            {
                if (lines[0].IndexOf("LOGOUT") > -1)
                {
                    if (lines[1].Trim().IndexOf(this.userSession.getUserIdentity()) > -1)
                    {

                        this.gui.logOut();
                        
                        this.downloadedServices = 0;
                        this.numOfServices = 0;
                    }
                    else
                    {
                        Console.WriteLine("Request to logout but identity provided is not current user identity");
                    }

                }
                else if (lines[0].IndexOf("START SERVICE") > -1)
                {
                    String userId = lines[1];
                    String serviceName = lines[2];

                    this.gui.startService(serviceName);
                }
                else if (lines[0].IndexOf("SHOW_IMAGE") > -1)
                {
                    String userId = lines[1];
                    String imageLocation = lines[2];
                    this.gui.showImage(imageLocation);
                }
            }
            
        }

        private void createUserSession(String input)
        {
            this.userSession = new UserSession();
            String[] lines = input.Split('\n');
            if (lines.Length < 4)
            {
                Console.WriteLine("message not in correct format");
                return;
            }

            int lineStart = 2;
            int lineEnd = 0;
            for (int i = 0; i < lines.Length; i++)
            {
                Console.WriteLine("Line: " + i + " " + lines[i]);
                if (lines[i].IndexOf("END_SERVICES") > -1)
                {
                    lineEnd = i;
                }

            }

            if (lines[0].IndexOf("LOGIN") > -1)
            {
                userSession.setUserIdentity(lines[1].Trim());
            }


            for (int i = lineStart; i < lineEnd; i += 2)
            {
                ServiceInfo sInfo = new ServiceInfo();
                sInfo.serviceName = lines[i];
                sInfo.serviceURL = lines[i + 1];
                this.numOfServices++;
                this.downloadFile(sInfo);
            }

            Console.WriteLine(userSession.ToString());
        }



        private void downloadFile(ServiceInfo sInfo)
        {
            String userProfile = System.Environment.GetEnvironmentVariable("USERPROFILE");
            String directory = userProfile + @"\Downloads\";
            String fileName = this.extractFilename(sInfo.serviceURL);

            String pathToExe = directory + fileName;
            sInfo.serviceExe = pathToExe;

            if (fileName.ToLower().EndsWith(".exe") || fileName.ToLower().EndsWith(".jar"))
            {
                if (fileName.ToLower().EndsWith(".exe"))
                {

                    sInfo.serviceType = ServiceType.EXE;
                }
                else
                {
                    sInfo.serviceType = ServiceType.JAR;
                }
                if (sInfo.serviceURL.StartsWith("http"))
                {

                    WebClient webClient = new WebClient();
                    webClient.DownloadFileCompleted += new AsyncCompletedEventHandler(Completed);
                    webClient.DownloadProgressChanged += new DownloadProgressChangedEventHandler(ProgressChanged);
                    webClient.DownloadFileAsync(new Uri(sInfo.serviceURL), pathToExe, sInfo);
                }
                else
                {
                    sInfo.serviceExe = sInfo.serviceURL;

                }
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
            this.downloadedServices++;
            Console.WriteLine("Downloaded services: " + downloadedServices);
            Console.WriteLine("Number of services: " + numOfServices);
            if (this.numOfServices > this.downloadedServices)
            {
                Console.WriteLine("Waiting for download to finish");
            }
            else
            {
                this.gui.Login(userSession);
               
            }
            //MessageBox.Show("Download completed!");
        }


        internal void close()
        {

            this.server.Stop();
        }


    }
}
