using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using System.IO;
namespace HWUPortal
{
    //PROTOCOL:
    // filename \n
    // userIdentity \n
    // file
    class BinaryDataTransfer
    {
        protected static log4net.ILog log = log4net.LogManager.GetLogger(typeof(BinaryDataTransfer));

        String outputFileName;
        Boolean transferImageInProgress = false;


        TcpListener server;
        MainWindow gui;

        public BinaryDataTransfer(MainWindow gui)
        {
            this.gui = gui;



        }
        public void run()
        {
            System.Threading.Thread.CurrentThread.Name = "BinaryDataTransferThread";
            try
            {
                int port = 2113;
                server = new TcpListener(IPAddress.Any, port);
                server.Start();
                byte[] bytes = new byte[1024];
                string data;

                while (true)
                {
                    TcpClient incoming = server.AcceptTcpClient();
                    NetworkStream stream = incoming.GetStream();

                    int i;
                    int counter = 0;


                    // Loop to receive all the data sent by the client.
                    i = stream.Read(bytes, 0, bytes.Length);
                    FileStream fileStream = null;
                    String text = string.Empty;
                    data = System.Text.Encoding.ASCII.GetString(bytes, 0, i);
                    if (this.gui.isUserLoggedIn(data.Trim()))
                    {
                        i = stream.Read(bytes, 0, bytes.Length);
                        while (i != 0)
                        {

                            if (transferImageInProgress)
                            {
                                counter = counter + i;

                                fileStream.Write(bytes, 0, i);
                            }

                            else
                            {

                                data = System.Text.Encoding.ASCII.GetString(bytes, 0, i);
                                if (log.IsDebugEnabled) log.Debug(String.Format("Received: {0}", data));


                                this.outputFileName = data.Trim();

                                this.transferImageInProgress = true;
                                this.createfullPath();

                                fileStream = File.OpenWrite(outputFileName);
                                if (log.IsDebugEnabled) log.Debug("Writing to file: " + outputFileName);

                                //stream.Flush();
                            }


                            i = stream.Read(bytes, 0, bytes.Length);
                            //            if (log.IsDebugEnabled)  log.Debug("After :" + i);
                        }
                        if (log.IsDebugEnabled) log.Debug("copied: " + this.outputFileName);
                        if (log.IsDebugEnabled) log.Debug("bytes read:" + counter);
                        fileStream.Close();
                        fileStream = null;
                        this.transferImageInProgress = false;
                        this.gui.showImage(this.outputFileName);
                    }

                    incoming.Close();

                }


            }
            catch (Exception exc)
            {
                log.Error("", exc);
            }
        }

        private void createfullPath()
        {
            String userProfile = System.Environment.GetEnvironmentVariable("USERPROFILE");
            String directory = userProfile + @"\Downloads\";
            this.outputFileName = directory + this.outputFileName;
        }

        internal void close()
        {

            this.server.Stop();
        }
    }


}
