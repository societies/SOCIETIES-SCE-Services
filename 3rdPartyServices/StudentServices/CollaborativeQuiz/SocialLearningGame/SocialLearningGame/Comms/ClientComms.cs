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
using Newtonsoft.Json;
using SocialLearningGame.Entities;
namespace SocialLearningGame.Comms
{
    class ClientComms
    {
        Socket echoSocket;
        String userID;
        String endPoint;
        int port;
        Boolean connected;
        String serverIP;
        int serverPort;
        private static String START_MSG = "START_MSG\n";
        private static String END_MSG = "END_MSG\n";
        private static String REQUEST_SERVERIP = "REQUEST_SERVER\n";
        private static String RETRIEVE_QUESTIONS = "RETRIEVE_QUESTIONS\n";
        private static String NULL_REPLY = "NULL";

        public ClientComms()
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
                Console.WriteLine("SOCKET_CLIENT: Unable to connect to service client on node: " + endPoint + " on port: " + port);
                Console.WriteLine(e.ToString());
            }
            return false;
        }


        public void disconnect()
        {
            if (echoSocket != null)
            {
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

        private Boolean retrieveUserID()
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

        private Boolean retrieveEndPoint()
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
            server.Send(Encoding.ASCII.GetBytes("SERVICE_PORT->SocialLearning"));
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

        public String getAddressPort()
        {
            return this.endPoint + ":" + this.port.ToString();
     
        }

        public String speakToClient(String address, int port)
        {
            Console.WriteLine("TRYING TO SPEAK TO CLIENT ON " + address + " " + port.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(address), port);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine("SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(e.ToString());
                return null;
            }

            //get current user
            server.Send(Encoding.ASCII.GetBytes(REQUEST_SERVERIP));
            Console.WriteLine("SENT TO CLIENT: " + REQUEST_SERVERIP);
            byte[] data = new byte[1024];
            int receivedDataLength = 0;
            receivedDataLength = server.Receive(data);
            if (receivedDataLength < 1)
            {
                Console.WriteLine("DIDNT GET A REPLY");
                server.Close();
                return null;
            }
            Console.WriteLine("GOT A REPLY");
            String reply = Encoding.ASCII.GetString(data, 0, receivedDataLength).Trim();
            Console.WriteLine("Reply:" + reply + "!");
          /*  if (!reply.Equals(NULL_REPLY))
            {
                String[] replyMsg = reply.Split(':');
                serverIP = replyMsg[0];
                serverPort = Convert.ToInt32(replyMsg[1]);
                Console.WriteLine("GUI: Received server IP: " + serverIP + " & Port: " + serverPort);
                getQuestions(serverIP, serverPort);
                speakToServer(userID, serverIP, serverPort);
            }
            else
            {
                Console.WriteLine("CLIENT DOES NOT HAVE SERVER IP");
            }*/

            server.Close();
            return reply;
        }

        public List<UserScore> speakToServer(String user, String address, int port)
        {
            Console.WriteLine("TRYING TO SPEAK TO Server ON " + address + " " + port.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(address), port);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine("SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(e.ToString());
                return null;
            }

            //get current user
            server.Send(Encoding.ASCII.GetBytes("RETRIEVE_SCORES\n"));
            server.Send(Encoding.ASCII.GetBytes(user+"\n"));

            Console.WriteLine("SENT TO SERVER: " + REQUEST_SERVERIP);
            byte[] data = new byte[1024];
            int receivedDataLength = 0;
            receivedDataLength = server.Receive(data);
            if (receivedDataLength < 1)
            {
                Console.WriteLine("DIDNT GET A REPLY");
                server.Close();
                return null;
            }
            Console.WriteLine("GOT A REPLY");
            String reply = Encoding.ASCII.GetString(data, 0, receivedDataLength).Trim();
            Console.WriteLine("Reply:" + reply + "!");
            //NOW CHANGE THE USERS INTO A LIST OF USER OBJECTS
            Console.WriteLine("Converting JSON to USER Objects!!!");
            List<UserScore> usersList = new List<UserScore>();
            if (!reply.Equals("NULL"))
            {
                usersList = JsonConvert.DeserializeObject<List<UserScore>>(reply);

                int x = 0;
                while (x < usersList.Count)
                {
                    Console.WriteLine("User is: " + usersList[x].userJid);
                    var name = (usersList[x].userJid.Substring(0, usersList[x].userJid.IndexOf('.')));
                    Console.WriteLine("New name: " + name);
                    usersList[x].name = name;
                    Console.WriteLine("Confirmed: " + usersList[x].name);
                    x++;
                }
            }

           /* if (!reply.Equals(NULL_REPLY))
            {
                String[] replyMsg = reply.Split(':');
                serverIP = replyMsg[0];
                serverPort = Convert.ToInt32(replyMsg[1]);
                Console.WriteLine("GUI: Received server IP: " + serverIP + " & Port: " + serverPort);
            }
            else
            {
                Console.WriteLine("CLIENT DOES NOT HAVE SERVER IP");
            }*/

            server.Close();
          //  getQuestions("127.0.0.1", port);
            return usersList;
        }

        public Category[] getCategories(String address, int port)
        {
            Console.WriteLine("TRYING TO SPEAK TO Server ON " + address + " " + port.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(address), port);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine("SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(e.ToString());
                return null;
            }

            //get current user
            //  server.Send(Encoding.ASCII.GetBytes("bla\n"));
            server.Send(Encoding.ASCII.GetBytes("RETRIEVE_CATEGORIES\n"));
            Console.WriteLine("SENT TO SERVER: " + "RETRIEVE_CATEGORIES\n");
            byte[] data = new byte[1024];
            int receivedDataLength = 0;
            receivedDataLength = server.Receive(data);
            if (receivedDataLength < 1)
            {
                Console.WriteLine("DIDNT GET A REPLY");
                server.Close();
                return null;
            }
            Console.WriteLine("GOT A REPLY");
            String reply = Encoding.ASCII.GetString(data, 0, receivedDataLength).Trim();
            Console.WriteLine("Reply:" + reply + "!");
            if (!reply.Equals(NULL_REPLY))
            {
                Console.WriteLine("Category List: " + reply);
            }
            else
            {
                Console.WriteLine("SERVER DOESNT HAVE CATEGORIES TO SEND");
                return null;
            }

            server.Close();

            Category[] c = JsonConvert.DeserializeObject<Category[]>(reply);
            Console.WriteLine("C: " + c[0].ToString());
            return c;
        }

        public Question[] getQuestions(String address, int port)
        {
            Console.WriteLine("TRYING TO SPEAK TO Server ON " + address + " " + port.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(address), port);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine("SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(e.ToString());
                return null;
            }

            //get current user
          //  server.Send(Encoding.ASCII.GetBytes("bla\n"));
            server.Send(Encoding.ASCII.GetBytes("RETRIEVE_QUESTIONS\n"));
            Console.WriteLine("SENT TO SERVER: " + RETRIEVE_QUESTIONS);
            byte[] data = new byte[1024];
            int receivedDataLength = 0;
            receivedDataLength = server.Receive(data);
            if (receivedDataLength < 1)
            {
                Console.WriteLine("DIDNT GET A REPLY");
                server.Close();
                return null;
            }
            Console.WriteLine("GOT A REPLY");
            String reply = Encoding.ASCII.GetString(data, 0, receivedDataLength).Trim();
            Console.WriteLine("Reply:" + reply + "!");
            if (!reply.Equals(NULL_REPLY))
            {
                Console.WriteLine("Question List: " + reply);
            }
            else
            {
                Console.WriteLine("SERVER DOESNT HAVE QUESTIONS TO SEND");
                return null;
            }

            server.Close();

            Question[] q = JsonConvert.DeserializeObject<Question[]>(reply);
            Console.WriteLine("Q: " + q[0].ToString());
            return q;
        }

        //GET ANSWERED Q's
        public List<UserAnsweredQ> getAnsweredQuestions(String address, int port)
        {
            Console.WriteLine("TRYING TO SPEAK TO Server ON " + address + " " + port.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(address), port);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine("SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(e.ToString());
                return null;
            }

            //get current user
            //  server.Send(Encoding.ASCII.GetBytes("bla\n"));
            server.Send(Encoding.ASCII.GetBytes("RETRIEVE_USER_HISTORY\n"));
            Console.WriteLine("SENT TO SERVER: " + "RETRIEVE_USER_HISTORY");
            server.Send(Encoding.ASCII.GetBytes(userID+"\n"));
            Console.WriteLine("SENT TO SERVER: " + userID);
            byte[] data = new byte[1024];
            int receivedDataLength = 0;
            String reply ="";
            while (receivedDataLength < 1025)
            {
                receivedDataLength = server.Receive(data);
                if (receivedDataLength < 1)
                {
                    Console.WriteLine("DIDNT GET A REPLY");
                    server.Close();
                    return null;
                }
                else
                {
                    reply = reply+Encoding.ASCII.GetString(data, 0, receivedDataLength).Trim();
                    if (receivedDataLength < 1024)
                    {
                        break;
                    }
                }
            }
            Console.WriteLine("GOT A REPLY");
            
            Console.WriteLine("Reply:" + reply + "!");
            if (!reply.Equals(NULL_REPLY))
            {
                Console.WriteLine("Answered question List: " + reply);
            }
            else
            {
                Console.WriteLine("SERVER DOESNT HAVE QUESTIONS TO SEND");
                return null;
            }

            server.Close();


           // Newtonsoft.Json.Linq.JArray jArray = Newtonsoft.Json.Linq.JArray.Parse(reply);
          //  Console.WriteLine(jArray);
         //   List<UserAnsweredQ> q2 = jArray.ToObject<List<UserAnsweredQ>>();

            List<UserAnsweredQ> q = JsonConvert.DeserializeObject<List<UserAnsweredQ>>(reply);
            if (q.Count() > 0)
            {
                Console.WriteLine("Q: " + q[0].ToString());
            }
            return q;
        }

        //CHANGE TO RECEIVE ACK, SEND QUESTIONS USER HAS ANSWERED
        public void sendProgress(String address, int port, UserScore user, List<UserAnsweredQ> answeredQ)
        {
            Console.WriteLine("UPLOADING PROGRESS TO SERVER");
            Console.WriteLine("TRYING TO SPEAK TO Server ON " + address + " " + port.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(address), port);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine("SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(e.ToString());
                return;
            }

            //get current user
            //  server.Send(Encoding.ASCII.GetBytes("bla\n"));
            var sendUserScore = JsonConvert.SerializeObject(user);
            server.Send(Encoding.ASCII.GetBytes("UPLOAD_PROGRESS\n"));
            server.Send(Encoding.ASCII.GetBytes(sendUserScore+"\n"));
            var sendAnsweredQ = JsonConvert.SerializeObject(answeredQ);
            Console.WriteLine("Sending..." + sendAnsweredQ);

            server.Send(Encoding.ASCII.GetBytes(sendAnsweredQ + "\n"));
            Console.WriteLine("THE LIST OF QUESTIONS ANSWERED ARE NOW UPLOADED!!!");

            /*Console.WriteLine("SENT TO SERVER: JSON USER STRING");
            byte[] data = new byte[1024];
            int receivedDataLength = 0;
            receivedDataLength = server.Receive(data);
            if (receivedDataLength < 1)
            {
                Console.WriteLine("DIDNT GET A REPLY");
                server.Close();
                return;
            }
            Console.WriteLine("GOT A REPLY");
            String reply = Encoding.ASCII.GetString(data, 0, receivedDataLength).Trim();
            Console.WriteLine("Reply:" + reply + "!");
            if (!reply.Equals(NULL_REPLY))
            {
                Console.WriteLine("Question List: " + reply);
            }
            else
            {
                Console.WriteLine("SERVER DOESNT HAVE QUESTIONS TO SEND");
                return null;
            }*/

            server.Close();

          //  Question[] q = JsonConvert.DeserializeObject<Question[]>(reply);
            //Console.WriteLine("Q: " + q[0].ToString());
            //return q;
        }

    }
}
