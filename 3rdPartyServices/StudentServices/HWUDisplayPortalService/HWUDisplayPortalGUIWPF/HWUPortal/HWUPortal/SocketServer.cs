using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using System.ComponentModel;

namespace HWUPortal
{
    class SocketServer
    {
        MainWindow gui;
        UserSession userSession;
        private volatile TcpListener server;
        private volatile TcpClient client;
        int downloadedServices = 0;
        int numOfServices = 0;
        private NetworkStream stream;
        private byte[] okBytes;
        private byte[] notOKBytes;
        private volatile bool acceptingConnections = true;

        private IPAddress remoteIPAddress;

        public SocketServer(MainWindow portalGui)
        {
            this.gui = portalGui;
            String ok = "OK\n";
            String notOK = "NotOK\n";
            okBytes = System.Text.Encoding.ASCII.GetBytes(ok.ToCharArray());
            notOKBytes = System.Text.Encoding.ASCII.GetBytes(notOK);
        }
        public void run()
        {
            System.Threading.Thread.CurrentThread.Name = "SocketClient";

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
                while (acceptingConnections)
                {
                    Console.Write("Waiting for a connection... ");

                    // Perform a blocking call to accept requests.
                    // You could also user server.AcceptSocket() here.
                    client = server.AcceptTcpClient();
                    Console.WriteLine("Connected!");

                    // Get a stream object for reading and writing
                    stream = client.GetStream();
                    this.remoteIPAddress = ((IPEndPoint)client.Client.RemoteEndPoint).Address;
                    int i;

                    // Loop to receive all the data sent by the client.
                    i = stream.Read(bytes, 0, bytes.Length);

                    while (i != 0)
                    {
                        // Translate data bytes to a ASCII string.
                        data = System.Text.Encoding.ASCII.GetString(bytes, 0, i);
                        Console.WriteLine(String.Format("Received: {0}", data));
                        if (this.gui.isLoggedIn())
                        {

                            String text = "";
                            String serviceName = "";
                            Boolean receivingText = false;
                            if (data.IndexOf("SHOW_TEXT") > -1)
                            {
                                receivingText = true;

                                Console.WriteLine("Processing text");

                                while (receivingText)
                                {
                                    while (i != 0)
                                    {
                                        /*if (data.IndexOf("END_TEXT") > -1)
                                        {
                                            receivingText = false;
                                            i = 0;
                                        }
                                        else */
                                        if (data.StartsWith("SHOW_TEXT"))
                                        {
                                            Console.WriteLine("Processing SHOW_TEXT");
                                            String[] splitData = data.Split('\n');
                                            String userId = splitData[1];
                                            serviceName = splitData[2];
                                            Console.WriteLine("userId : " + userId);
                                            if (userId.IndexOf(this.userSession.getUserIdentity()) > -1)
                                            {
                                                Console.WriteLine("Starting to show text\n\n");
                                                for (int n = 3; n < splitData.Length; n++)
                                                {
                                                    if (splitData[n].IndexOf("END_TEXT") > -1)
                                                    {
                                                        break;
                                                    }
                                                    Console.WriteLine("concatenating text line: " + n);
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



                                        Console.WriteLine("\nText for notification panel:\n" + text + "\nEnds text\n");
                                        if (data.IndexOf("END_TEXT") > -1)
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
                                //this.gui.showText(text);
                                this.gui.addNewNotification(serviceName, text);
                                stream.Write(okBytes, 0, okBytes.Length);
                            }
                            else
                            {
                                this.processInSessionCommand(data);
                            }
                        }
                        else
                        {
                            if (!this.createUserSession(data))
                            {
                                stream.Write(notOKBytes, 0, notOKBytes.Length);
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

        private void processingText(String input)
        {

        }

        
        //this is called by the gui when the logout is performed manually and not through the socket
        public void helperLogoutMethod()
        {

            this.downloadedServices = 0;
            this.numOfServices = 0;
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
                        this.stream.Write(okBytes, 0, okBytes.Length);
                        //this is now set in the GUI
                        //this.downloadedServices = 0;
                        //this.numOfServices = 0;
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
                    this.stream.Write(okBytes, 0, okBytes.Length);
                }
                else if (lines[0].IndexOf("SHOW_IMAGE") > -1)
                {
                    String userId = lines[1];
                    String imageLocation = lines[2];
                    this.gui.showImage(imageLocation);
                    this.stream.Write(okBytes, 0, okBytes.Length);
                }
            }

        }

        private bool createUserSession(String input)
        {
            this.userSession = new UserSession(this.remoteIPAddress);
            String[] lines = input.Split('\n');
            if (lines.Length > 0)
            {
                if (lines[0].IndexOf("LOGIN") > -1)
                {
                    if (lines.Length < 4)
                    {
                        Console.WriteLine("message not in correct format");
                        return false;
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


                    for (int i = lineStart; i < lineEnd; i += 3)
                    {
                        ServiceInfo sInfo = new ServiceInfo();
                        sInfo.serviceName = lines[i];
                        sInfo.serviceURL = lines[i + 1];
                        string requiresKinect = lines[i + 2];
                        string command = "RequiresKinect=";
                        if (requiresKinect.IndexOf(command) > -1)
                        {

                            //requiresKinect = requiresKinect.Remove(0, command.Count());
                            if (requiresKinect.IndexOf("true") > -1)
                            {
                                sInfo.requiresKinect = true;
                            }
                            else
                            {
                                sInfo.requiresKinect = false;
                            }
                        }
                        this.numOfServices++;
                        this.downloadFile(sInfo);
                    }

                    if (this.numOfServices >= this.downloadedServices)
                    {
                        this.gui.Login(userSession);
                    }
                    Console.WriteLine(userSession.ToString());
                    return true;
                }
                else
                {
                    Console.WriteLine("Ignoring received data");
                    return false;
                }
            }
            return true;
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
                this.userSession.addService(sInfo);
                this.downloadedServices++;
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
                stream.Write(okBytes, 0, okBytes.Length);

            }
            //MessageBox.Show("Download completed!");
        }


        internal void close()
        {
            this.acceptingConnections = false;
            if (this.client != null)
            {
                this.client.Close();
            }
            
            this.server.Stop();
        }


    }
}
