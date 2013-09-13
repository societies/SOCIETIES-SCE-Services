using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using System.ComponentModel;
using System.IO;
using System.Collections;


namespace HWUPortal
{
    class SocketServer
    {
        protected static log4net.ILog log = log4net.LogManager.GetLogger(typeof(SocketServer));

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
                    if (log.IsDebugEnabled) log.Debug("Waiting for a connection... ");

                    // Perform a blocking call to accept requests.
                    // You could also user server.AcceptSocket() here.
                    client = server.AcceptTcpClient();
                    if (log.IsDebugEnabled) log.Debug("Connected!");

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
                        if (log.IsDebugEnabled) log.Debug(String.Format("Received: {0}", data));
                        if (this.gui.isLoggedIn())
                        {

                            String text = "";
                            String serviceName = "";
                            Boolean receivingText = false;
                            if (data.IndexOf("SHOW_TEXT") > -1)
                            {
                                receivingText = true;

                                if (log.IsDebugEnabled) log.Debug("Processing text");

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
                                            if (log.IsDebugEnabled) log.Debug("Processing SHOW_TEXT");
                                            String[] splitData = data.Split('\n');
                                            String userId = splitData[1];
                                            serviceName = splitData[2];
                                            if (log.IsDebugEnabled) log.Debug("userId : " + userId);
                                            if (userId.IndexOf(this.userSession.getUserIdentity()) > -1)
                                            {
                                                if (log.IsDebugEnabled) log.Debug("Starting to show text\n\n");
                                                for (int n = 3; n < splitData.Length; n++)
                                                {
                                                    if (splitData[n].IndexOf("END_TEXT") > -1)
                                                    {
                                                        break;
                                                    }
                                                    if (log.IsDebugEnabled) log.Debug("concatenating text line: " + n);
                                                    text = string.Concat(text, splitData[n]);
                                                    //this.gui.appendText(splitData[n]);

                                                    if (log.IsDebugEnabled) log.Debug(String.Format("Concatenating text line: \n{0}", splitData[n]));
                                                }
                                            }

                                        }
                                        else
                                        {
                                            if (log.IsDebugEnabled) log.Debug("processing another 1024 bytes");



                                            if (log.IsDebugEnabled) log.Debug(String.Format("Adding to txtBox: {0}", data));
                                            text = string.Concat(text, data);
                                            //this.gui.appendText(data);
                                        }



                                        if (log.IsDebugEnabled) log.Debug("\nText for notification panel:\n" + text + "\nEnds text\n");
                                        if (data.IndexOf("END_TEXT") > -1)
                                        {
                                            receivingText = false;
                                            i = 0;
                                        }
                                        else
                                        {
                                            if (log.IsDebugEnabled) log.Debug("reading from stream");
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
                        //            if (log.IsDebugEnabled)  log.Debug(String.Format("Sent: {0}", data));

                        i = stream.Read(bytes, 0, bytes.Length);

                    }

                    // Shutdown and end connection
                    client.Close();
                }
            }
            catch (Exception e)
            {
                log.Error("", e);

                if (client != null)
                {
                    try
                    {
                        client.Close();
                    }
                    catch (Exception ex2)
                    {
                        log.Error("Error closing client", ex2);
                    }
                }

                if (server != null)
                {
                    try
                    {
                        server.Stop();
                    }
                    catch (Exception ex2)
                    {
                        log.Error("Error stopping server", ex2);
                    }
                }
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
            if (lines.Length > 0)
            {
                if (lines[0].IndexOf("LOGOUT") > -1)
                {
                    if (lines[1].Trim().IndexOf(this.userSession.getUserIdentity()) > -1)
                    {
                        if (log.IsDebugEnabled) log.Debug("Logout method called on gui");
                        this.gui.logOut();
                        this.stream.Write(okBytes, 0, okBytes.Length);
                        //this is now set in the GUI
                        //this.downloadedServices = 0;
                        //this.numOfServices = 0;
                    }
                    else
                    {
                        if (log.IsDebugEnabled) log.Debug("Request to logout but identity provided is not current user identity");
                    }

                }
                else if (lines[0].IndexOf("START_SERVICE") > -1)
                {
                    String userId = lines[1];
                    String serviceName = lines[2];
                    if (log.IsDebugEnabled) log.Debug("Starting service: " + serviceName);
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
                if ((lines[0].IndexOf("LOGIN") > -1) && (lines[1].IndexOf("USER") > -1))
                {


                    userSession.setUserIdentity(lines[1].Trim());



                    userSession.setUserIdentity(lines[1].Trim().Remove(0, "USER:".Length));

                    if (lines[2].IndexOf("PORTAL_PORT") > -1)
                    {
                        int portalPort = Int32.Parse(lines[2].Trim().Remove(0, "PORTAL_PORT:".Length));
                        if (log.IsDebugEnabled) log.Debug("Parsed portal port: " + portalPort);
                        userSession.setPort(portalPort);

                    }

                    int servicesToFollow = 0;
                    if (lines[3].IndexOf("NUM_SERVICES") > -1)
                    {
                        servicesToFollow = Int32.Parse(lines[3].Trim().Remove(0, "NUM_SERVICES:".Length));
                        if (log.IsDebugEnabled) log.Debug("Parsed number of services: " + servicesToFollow);
                    }

                    if (servicesToFollow == 0)
                    {
                        if (log.IsDebugEnabled) log.Debug(userSession.ToString());
                        this.gui.Login(userSession);
                        this.stream.Write(okBytes, 0, okBytes.Length);
                        return true;
                    }


                    if (lines[4].IndexOf("START_SERVICES") > -1)
                    {
                        if (log.IsDebugEnabled) log.Debug("Reading services ");
                        int pointer = 5;
                        String line = lines[pointer];
                        while (line.IndexOf("END_SERVICES") == -1)
                        {
                            if (log.IsDebugEnabled) log.Debug("step 1, processing line: " + line);
                            if (line.IndexOf("START_SERVICE_INFO:") > -1)
                            {

                                int readingServiceNumber = Int32.Parse((line.Remove(0, "START_SERVICE_INFO:".Length)));
                                if (log.IsDebugEnabled) log.Debug("Reading service: " + readingServiceNumber);
                                ServiceInfo sInfo = new ServiceInfo();
                                line = lines[pointer++];
                                while (line.IndexOf("END_SERVICE_INFO:" + readingServiceNumber) == -1)
                                {
                                    if (line.IndexOf("SERVICE_NAME_" + readingServiceNumber + ":") > -1)
                                    {
                                        sInfo.serviceName = line.Remove(0, ("SERVICE_NAME_" + readingServiceNumber + ":").Length);
                                    }
                                    else if (line.IndexOf("SERVICE_EXE_" + readingServiceNumber + ":") > -1)
                                    {
                                        sInfo.serviceURL = line.Remove(0, ("SERVICE_EXE_" + readingServiceNumber + ":").Length);
                                    }
                                    else if (line.IndexOf("SERVICE_PORT_" + readingServiceNumber + ":") > -1)
                                    {
                                        sInfo.servicePortNumber = Int32.Parse(line.Remove(0, ("SERVICE_PORT_" + readingServiceNumber + ":").Length));
                                    }
                                    else if (line.IndexOf("RequiresKinect_" + readingServiceNumber + ":") > -1)
                                    {
                                        String value = line.Remove(0, ("RequiresKinect_" + readingServiceNumber + ":").Length);
                                        if (value.IndexOf("true") > -1)
                                        {
                                            sInfo.requiresKinect = true;
                                        }
                                        else
                                        {
                                            sInfo.requiresKinect = false;
                                        }
                                    }


                                    line = lines[pointer++];
                                }

                                if (log.IsDebugEnabled) log.Debug("Read service info " + readingServiceNumber);

                                this.numOfServices++;
                                this.downloadFile(sInfo);
                            }
                            line = lines[pointer++];
                        }

                        if (log.IsDebugEnabled) log.Debug("read all services");
                    }

                    //int startServicesLineNumber = 0;
                    //int endServicesLineNumber = 0;

                    //for (int i = 4; i < lines.Length; i++)
                    //{
                    //                if (log.IsDebugEnabled)  log.Debug("Line: " + i + " " + lines[i]);
                    //    if (lines[i].IndexOf("END_SERVICES") > -1)
                    //    {
                    //        endServicesLineNumber = i;
                    //    }else if (lines[i].IndexOf("START_SERVICES") > -1)
                    //    {
                    //        startServicesLineNumber = i;
                    //    }

                    //}


                    //for (int i = lineStart; i < lineEnd; i += 6)
                    //{
                    //    ServiceInfo sInfo = new ServiceInfo();
                    //    sInfo.serviceName = lines[i]; 
                    //    sInfo.serviceURL = lines[i + 1]; 
                    //    string requiresKinect = lines[i + 2];
                    //    string command = "RequiresKinect:";
                    //    if (requiresKinect.IndexOf(command) > -1)
                    //    {

                    //        //requiresKinect = requiresKinect.Remove(0, command.Count());
                    //        if (requiresKinect.IndexOf("true") > -1)
                    //        {
                    //            sInfo.requiresKinect = true;
                    //        }
                    //        else
                    //        {
                    //            sInfo.requiresKinect = false;
                    //        }
                    //    }
                    //    this.numOfServices++;
                    //    this.downloadFile(sInfo);
                    //}
                    if (log.IsDebugEnabled) log.Debug(userSession.ToString());
                    return true;
                }
                else
                {
                    if (log.IsDebugEnabled) log.Debug("Ignoring received data");
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

            if (!Directory.Exists(directory))
            {
                Directory.CreateDirectory(directory);
            }

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
                this.Completed(this, new AsyncCompletedEventArgs(null, false, sInfo));
                //this.gui.Login(this.userSession);


            }
            if (log.IsDebugEnabled) log.Debug(sInfo.ToString());
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
            if (log.IsDebugEnabled) log.Debug("Downloaded services: " + downloadedServices);
            if (log.IsDebugEnabled) log.Debug("Number of services: " + numOfServices);
            if (this.numOfServices > this.downloadedServices)
            {
                if (log.IsDebugEnabled) log.Debug("Waiting for download to finish");
            }
            else
            {
                if (log.IsDebugEnabled) log.Debug("Starting user login");
                this.gui.Login(userSession);
                if (log.IsDebugEnabled) log.Debug("User logged in. Sending OK to societies platform");
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
