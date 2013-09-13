/* 
 * Copyright (coffee) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;
namespace MyTvUI
{
    class SocketClient
    {
        Socket echoSocket;
        String userID;
        String endPoint;
        int port;
        Boolean connected;

        public SocketClient()
        {
            connected = false;
        }


        public Boolean connect()
        {
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(endPoint), port);
            echoSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                echoSocket.Connect(ip);
                connected = true;
                return true;
            }
            catch (SocketException e)
            {
                Console.WriteLine("SOCKET_CLIENT: Unable to connect to service client on node: "+endPoint+" on port: "+port);
                Console.WriteLine(e.ToString());
            }
            return false;
        }


        public void disconnect()
        {
            if (echoSocket!=null){
                echoSocket.Shutdown(SocketShutdown.Both);
                echoSocket.Close();
                echoSocket = null;
			} 
		    connected = false;
        }



        public String sendMessage(String message)
        {
            String response = "";
            if (connected)
            {
                Console.WriteLine("SOCKET_CLIENT: Sending message to service client:");
                Console.WriteLine(message);

                echoSocket.Send(Encoding.ASCII.GetBytes(message));
                byte[] data = new byte[1024];
                int receivedDataLength = echoSocket.Receive(data);
                response = Encoding.ASCII.GetString(data, 0, receivedDataLength);
                disconnect();
                Console.WriteLine("SOCKET_CLIENT: received -> " + response);
            }
            else
            {
                if (connect())
                {
                    response = sendMessage(message);
                }
            }
            return response;
        }


        //public String getChannelPreference()
        //{
        //    String response = "";
        //    if (connected)
        //    {
        //        Console.WriteLine("SOCKET_CLIENT: Getting channel preference from service client");

        //        String request = "START_MSG\n" +
        //            "CHANNEL_REQUEST\n" +
        //            "END_MSG\n";
        //        echoSocket.Send(Encoding.ASCII.GetBytes(request));
        //        byte[] data = new byte[1024];
        //        int receivedDataLength = echoSocket.Receive(data);
        //        response = Encoding.ASCII.GetString(data, 0, receivedDataLength);
        //        disconnect();
        //    }
        //    else
        //    {
        //        if (connect())
        //        {
        //            response = getChannelPreference();
        //        }
        //    }
        //    return response;
        //}


        //public String getMutedPreference()
        //{
        //    String response = "";
        //    if (connected)
        //    {
        //        Console.WriteLine("SOCKET_CLIENT: Getting muted preference from service client");

        //        String request = "START_MSG\n" +
        //            "MUTED_REQUEST\n" +
        //            "END_MSG\n";
        //        echoSocket.Send(Encoding.ASCII.GetBytes(request));
        //        byte[] data = new byte[1024];
        //        int receivedDataLength = echoSocket.Receive(data);
        //        response = Encoding.ASCII.GetString(data, 0, receivedDataLength);
        //        disconnect();
        //    }
        //    else
        //    {
        //        if (connect())
        //        {
        //            response = getMutedPreference();
        //        }
        //    }
        //    return response;
        //}


        #region connection parameters
        public Boolean getSessionParameters()
        {
            if (retrieveUserID() && retrieveEndPoint() && retrievePort())
            {
                return true;
            }
            else
            {
                return false;
            }

        }

        private Boolean retrieveUserID(){
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 2114);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine("SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(e.ToString());
                return false;
            }

            //get current user
            Console.WriteLine("SOCKET_CLIENT: Retrieving user ID");
            server.Send(Encoding.ASCII.GetBytes("CURRENT_USER"));
            byte[] data = new byte[1024];
            int receivedDataLength = 0;
            receivedDataLength = server.Receive(data);
            if (receivedDataLength < 1)
            {
                server.Close();
                return false;
            }
            userID = Encoding.ASCII.GetString(data, 0, receivedDataLength);
            Console.WriteLine("SOCKET_CLIENT: Received user identity from server: " + userID);

            server.Close();
            return true;
        }

        private Boolean retrieveEndPoint(){
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 2114);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine("SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(e.ToString());
                return false;
            }

            //get current end point
            Console.WriteLine("SOCKET_CLIENT: Retrieving endpoint of service client");
            server.Send(Encoding.ASCII.GetBytes("VIRGO_ENDPOINT_IPADDRESS"));
            byte[] data = new byte[1024];
            int receivedDataLength = 0;
            receivedDataLength = server.Receive(data);
            if (receivedDataLength < 1)
            {
                server.Close();
                return false;
            }
            endPoint = data[0] + "." + data[1] + "." + data[2] + "." + data[3];
            Console.WriteLine("SOCKET_CLIENT: Received end point of service client: " + endPoint);

            server.Close();
            return true;
        }

        private Boolean retrievePort()
        {
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 2114);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine("SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(e.ToString());
                return false;
            }

            //get service client listen port
            Console.WriteLine("SOCKET_CLIENT: Retrieving listen port of service client");
            server.Send(Encoding.ASCII.GetBytes("SERVICE_PORT->MyTv"));
            byte[] data = new byte[1024];
            int receivedDataLength = 0;
            receivedDataLength = server.Receive(data);
            if (receivedDataLength < 1)
            {
                server.Close();
                return false;
            }

            try
            {
                port = BitConverter.ToInt32(data, 0);
                Console.WriteLine("SOCKET_CLIENT: Received listen port of service client: " + port.ToString());
            }
            catch (Exception e)
            {
                Console.WriteLine("Error converting bytes to port");
                Console.WriteLine(e.ToString());
                server.Close();
                return false;
            }
       
            server.Close();
            return true;
        }

        #endregion connection parameters

        public String getUserID()
        {
            return this.userID;
        }
    } 
}
