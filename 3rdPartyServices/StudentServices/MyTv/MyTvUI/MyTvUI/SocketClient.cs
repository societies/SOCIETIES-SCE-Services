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


        public Boolean connectToServiceClient()
        {
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(endPoint), 4321);
            echoSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                echoSocket.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine("Unable to connect to service client on node: "+endPoint);
                Console.WriteLine(e.ToString());
                return false;
            }
            return true;
        }



        public Boolean sendMessage(String message)
        {
            if (echoSocket != null)
            {
                echoSocket.Send(Encoding.ASCII.GetBytes(message));
                byte[] data = new byte[1024];
                int receivedDataLength = echoSocket.Receive(data);
                String response = Encoding.ASCII.GetString(data, 0, receivedDataLength);
                if (!response.Contains("RECEIVED"))
                {
                    return false;
                }
            }
            else
            {
                Console.WriteLine("Error - echoSocket is null");
                return false;
            }
            return true;
        }



        public void disconnectFromServiceClient()
        {
            if (echoSocket != null)
            {
                echoSocket.Shutdown(SocketShutdown.Both);
                echoSocket.Close();
                echoSocket = null;
            }
        }

        

        public Boolean getSessionParameters()
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
                Console.WriteLine(e.ToString());
                return false;
            }

            //get current user
            Console.WriteLine("Retrieving user ID");
            server.Send(Encoding.ASCII.GetBytes("CURRENT_USER"));
            byte[] data = new byte[1024];
            int receivedDataLength = server.Receive(data);
            userID = Encoding.ASCII.GetString(data, 0, receivedDataLength);
            Console.WriteLine("Received user identity from server: " + userID);

            //get current end point
            Console.WriteLine("Retrieving endpoint of service client");
            server.Send(Encoding.ASCII.GetBytes("VIRGO_ENDPOINT_IPADDRESS"));
            data = new byte[1024];
            receivedDataLength = server.Receive(data);
            endPoint = Encoding.ASCII.GetString(data, 0, receivedDataLength);
            Console.WriteLine("Received end point of service client: " + endPoint);

            server.Close();
            return true;
        }

        public String getUserID()
        {
            return this.userID;
        }
    } 
}
