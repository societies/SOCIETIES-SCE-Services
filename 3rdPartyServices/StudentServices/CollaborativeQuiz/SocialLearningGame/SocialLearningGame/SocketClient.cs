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
namespace SocialLearningGame
{
    class SocketClient
    {
        String userIdentity = string.Empty;
        String serverIPAddress = "";
        String userName = "";
        public String getUserIdentity()
        {
            if (this.userIdentity == string.Empty)
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
                Console.WriteLine("Received user identity from server: " + stringData);

                server.Send(Encoding.ASCII.GetBytes("VIRGO_ENDPOINT_IPADDRESS"));
                receivedDataLength = server.Receive(data);
                string endPointData = Encoding.ASCII.GetString(data, 0, receivedDataLength);
                Console.Write("Received IPAddress from server: " + endPointData);

                
                server.Close();
                this.userIdentity = stringData;
                this.serverIPAddress = endPointData;
                return stringData;
            }
            
                return this.userIdentity;
            
        }

        public String getUserName()
        {
            if (this.userName == string.Empty)
            {
                if (this.serverIPAddress == string.Empty)
                {
                    this.getUserIdentity();
                }

                IPEndPoint ip = new IPEndPoint(IPAddress.Parse(this.serverIPAddress), 2131);
                Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                try
                {
                    server.Connect(ip);
                }
                catch (SocketException e)
                {
                    Console.WriteLine("Unable to connect to socket server on virgo ");
                    return "";
                }

                server.Send(Encoding.ASCII.GetBytes("SOCIAL_LEARNING_GET_INFO"));
                byte[] data = new byte[1024];
                int receivedDataLength = server.Receive(data);
                string stringData = Encoding.ASCII.GetString(data, 0, receivedDataLength);
                Console.WriteLine("Received username from virgo app");

                server.Close();
                this.userName = stringData;
            }
            return this.userName;

        }

        public String getServerIPAddress()
        {
            if (this.serverIPAddress == string.Empty)
            {
                this.getUserIdentity();

            }

            return this.serverIPAddress;
        }
    }

    
}
