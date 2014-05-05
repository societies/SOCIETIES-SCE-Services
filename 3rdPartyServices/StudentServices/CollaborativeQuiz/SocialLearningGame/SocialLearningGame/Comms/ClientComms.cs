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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUT_ORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
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
using SocialLearningGame.Pages;
namespace SocialLearningGame.Comms
{
    public class ClientComms
    {
        //SOCKET VARIABLES
        private static String clientIP;
        private static String serverIP;
        private static int clientPort;
        private static int serverPort;
        private static String userID;
        public static Boolean connected;
        private static Socket server;

        //PORTAL IP & PORT
        private String portalIP = "127.0.0.1";
        private int portalPort = 2114;

        //MESSAGE VARIABLES
        private static String GET_USER = "GET_USER";
        private static String NEXT_QUESTION = "NEXT_QUESTION";
        private static String ANSWER_QUESTION = "ANSWER_QUESTION";
        private static String GET_ALL_USERS = "GET_ALL_USERS";
        private static String GET_ALL_CIS = "GET_ALL_CIS";
        private static String GET_CATEGORIES = "GET_CATEGORIES";
       // private static String GET_USER_INTERESTS = "GET_USER_INTERESTS";
        private static String GET_CIS_NAMES = "GET_CIS_NAMES";
        private static String GET_CIS = "GET_CIS";
        private static String POST_ACTIVITY = "POST_ACTIVITY";

        private static String NULL_REPLY = "NULL";
        private static String TRUE_REPLY = "TRUE";

        private static String REQUEST_SERVERIP = "REQUEST_SERVER\n";
        private static String CURRENT_USER = "CURRENT_USER";
        private static String VIRGO_ENDPOINT_IPADDRESS = "VIRGO_ENDPOINT_IPADDRESS";
        private static String SERVICE_PORT = "SERVICE_PORT->Collaborative Quiz";
        private static String RETRIEVE_SCORES = "RETRIEVE_SCORES\n";
        
        private static String RETRIEVE_QUESTIONS = "RETRIEVE_QUESTIONS\n";
        private static String RETRIEVE_USER_HISTORY = "RETRIEVE_USER_HISTORY\n";
        
        private static String RETRIEVE_INVITED_PLAYERS = "RETRIEVE_INVITED_PLAYERS\n";
        private static String RETRIEVE_ALL_USERS = "RETRIEVE_ALL_USERS\n";
        private static String UPLOAD_PROGRESS = "UPLOAD_PROGRESS\n";
        private static String RETRIEVE_GROUP_PLAYERS = "RETRIEVE_GROUP_PLAYERS\n";
        private static String LEAVE_GROUP = "LEAVE_GROUP\n";
        private static String RETRIEVE_USER_GROUP = "RETRIEVE_USER_GROUP\n";
        private static String RETRIEVE_NOTIFICATIONS = "RETRIEVE_NOTIFICATIONS\n";
        private static String CREATE_GROUP = "CREATE_GROUP\n";
        private static String INVITE_USER = "INVITE_USER\n";
        private static String ADD_USER_TO_GROUP = "ADD_USER_TO_GROUP\n";
        private static String REMOVE_USER_FROM_GROUP = "REMOVE_USER_FROM_GROUP\n";
        private static String DELETE_GROUP = "DELETE_GROUP\n";
        private static String RESET_USER = "RESET_USER\n";
        private static String DELETE_NOTIFICATIONS = "DELETE_NOTIFICATIONS\n";
        private static String RETRIEVE_GROUPS = "RETRIEVE_GROUPS\n";
        private static String USER = "USER\n";
        private static String GROUP = "GROUP\n";

        

        public ClientComms()
        {
        }


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
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(portalIP), portalPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return false;
            }

            //get current user
            Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Retrieving user ID");
            server.Send(Encoding.ASCII.GetBytes(CURRENT_USER));
            byte[] data = new byte[1024];
            int receivedDataLength = 0;
            receivedDataLength = server.Receive(data);
            if (receivedDataLength < 1)
            {
                server.Close();
                return false;
            }
            userID = Encoding.ASCII.GetString(data, 0, receivedDataLength);
            Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Received user identity from server: " + userID);

            server.Close();
            return true;
        }

        private Boolean retrieveEndPoint()
        {
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(portalIP), portalPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return false;
            }

            //get current end point
            Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Retrieving endpoint of service client");
            server.Send(Encoding.ASCII.GetBytes(VIRGO_ENDPOINT_IPADDRESS));
            byte[] data = new byte[1024];
            int receivedDataLength = 0;
            receivedDataLength = server.Receive(data);
            if (receivedDataLength < 1)
            {
                server.Close();
                return false;
            }
            clientIP = data[0] + "." + data[1] + "." + data[2] + "." + data[3];
            Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Received end point of service client: " + clientIP);

            server.Close();
            return true;
        }

        private Boolean retrievePort()
        {
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(portalIP), portalPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return false;
            }

            Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Retrieving listen port of service client");
            server.Send(Encoding.ASCII.GetBytes(SERVICE_PORT));
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
                clientPort = BitConverter.ToInt32(data, 0);
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Received listen port of service client: " + clientPort.ToString());
            }
            catch (Exception e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"Error converting bytes to port");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                server.Close();
                return false;
            }

            server.Close();
            return true;
        }

        #endregion connection parameters

        public String getUserID()
        {
            return userID;
        }

        public Boolean connectToServer(String connectIP, int connectPort)
        {
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(connectIP), connectPort);
            server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return false;
            }
            return true;
        }

        public void disconnectFromServer()
        {
            server.Close();
        }


        public Boolean getSocietiesServer()
        {
            //CONNECT TO SERVER
            if (connectToServer(clientIP, clientPort))
            {
                server.Send(Encoding.ASCII.GetBytes(REQUEST_SERVERIP));
                String[] reply = recieveMessage().Split(':');
                serverIP = reply[0];
                serverPort = Convert.ToInt32(reply[1]);
                disconnectFromServer();
                return true;
            }
            return false;
            /*   Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO CLIENT ON " + clientIP + " " + clientPort.ToString());
               IPEndPoint ip = new IPEndPoint(IPAddress.Parse(clientIP), clientPort);
               Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
               try
               {
                   server.Connect(ip);
               }
               catch (SocketException e)
               {
                   Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                   Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                   return false;
               }

               //get current user
               server.Send(Encoding.ASCII.GetBytes(REQUEST_SERVERIP));
               Console.WriteLine(DateTime.Now + "\t" +"SENT TO CLIENT: " + REQUEST_SERVERIP);
               String reply = recieveMessage(server);
               String[] replyMsg = reply.Split(':');
               serverIP = replyMsg[0];
               serverPort = Convert.ToInt32(replyMsg[1]); 
               Console.WriteLine(DateTime.Now + "\t" +"SERVER IP: " + serverIP + " SERVER PORT: " + serverPort);
               return true;*/

        }

       /* public List<String> getUserInterests(String userID)
        {
            if (connectToServer(clientIP, clientPort))
            {
                server.Send(Encoding.ASCII.GetBytes(GET_USER_INTERESTS + "\n" + userID + "\n"));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (!reply.Equals(NULL_REPLY))
                {
                    List<String> interests = JsonConvert.DeserializeObject<List<String>>(reply);
                    disconnectFromServer();
                    return interests;
                }
                else
                {
                    disconnectFromServer();
                }

            }
            return new List<String>();
        }*/

        public User getUser()
        {
            if (connectToServer(serverIP, serverPort))
            {
                Console.WriteLine(DateTime.Now + "\t" + "Connected To CollabQuizServer");
                Console.WriteLine(DateTime.Now + "\t" + "SENDING " + GET_USER);
                server.Send(Encoding.ASCII.GetBytes(GET_USER + "\n" + userID +"\n"));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (!reply.Equals(NULL_REPLY))
                {
                    User user = JsonConvert.DeserializeObject<User>(reply);
                    disconnectFromServer();
                    return user;
                }
                else
                {
                    disconnectFromServer();
                }
            }
            return null;
        }

        public Boolean answerQuestion(AnsweredQuestions question)
        {
            if (connectToServer(serverIP, serverPort))
            {
                String questionString = JsonConvert.SerializeObject(question);
                server.Send(Encoding.ASCII.GetBytes(ANSWER_QUESTION + "\n" + questionString + "\n"));
                String reply = recieveMessage();
                if (reply.Equals(TRUE_REPLY))
                {
                    disconnectFromServer();
                    return true;
                }
                else
                {
                    disconnectFromServer();
                    return false;
                }
            }
            return false;
        }

        public List<Category> getCategories()
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(GET_CATEGORIES + "\n" + userID+"\n"));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (!reply.Equals(NULL_REPLY))
                {
                    List<Category> categories = JsonConvert.DeserializeObject<List<Category>>(reply);
                    disconnectFromServer();
                    return categories;
                }
                else
                {
                    disconnectFromServer();
                }
            }
            return new List<Category>();
        }

        public Question getNextQuestion(String userID, String cisName, String category)
        {
            if (cisName == null)
            {
                cisName = NULL_REPLY;
            }
            if (category == null)
            {
                category = NULL_REPLY;
            }
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(NEXT_QUESTION + "\n" + userID + "\n" + cisName + "\n" + category + "\n"));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (!reply.Equals(NULL_REPLY))
                {
                    Question question = JsonConvert.DeserializeObject<Question>(reply);
                    disconnectFromServer();
                    return question;
                }
                else
                {
                    disconnectFromServer();
                }
            }
            return null;
        }

        public void postActivity(String cisName, String correct)
        {
            if (connectToServer(clientIP, clientPort))
            {
                server.Send(Encoding.ASCII.GetBytes(POST_ACTIVITY + "\n" + cisName + "\n" + correct + "\n"));
            }
        }

        public List<User> getAllUsers()
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(GET_ALL_USERS + "\n"));
                String reply = recieveMessage();
                if (!reply.Equals(NULL_REPLY))
                {
                    List<User> usersList = JsonConvert.DeserializeObject<List<User>>(reply);
                    disconnectFromServer();
                    return usersList;
                }
                else
                {
                    disconnectFromServer();
                }
            }
            return new List<User>();
        }

        public List<Cis> getAllCis()
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(GET_ALL_CIS + "\n"));
                String reply = recieveMessage();
                if (!reply.Equals(NULL_REPLY))
                {
                    List<Cis> cisList = JsonConvert.DeserializeObject<List<Cis>>(reply);
                    disconnectFromServer();
                    return cisList;
                }
                else
                {
                    disconnectFromServer();
                }
            }
            return new List<Cis>();
        }

        public Cis getCis(String cisName)
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(GET_CIS + "\n" + cisName + "\n"));
                String reply = recieveMessage();
                if (!reply.Equals(NULL_REPLY))
                {
                    Cis cis = JsonConvert.DeserializeObject<Cis>(reply);
                    disconnectFromServer();
                    return cis;
                }
                else
                {
                    disconnectFromServer();
                }
            }
            return null;
        }

      /*  public List<Category> getCategories()
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(RETRIEVE_CATEGORIES));
                String reply = recieveMessage();// if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (!reply.Equals(NULL_REPLY))
                {
                    List<Category> categories = JsonConvert.DeserializeObject<List<Category>>(reply);
                    disconnectFromServer();
                    return categories;
                }
                else
                {
                    disconnectFromServer();
                    return new List<Category>();
                }
            }
            return null;

            
            Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return null;
            }

            //get current user
            server.Send(Encoding.ASCII.GetBytes(RETRIEVE_CATEGORIES));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "RETRIEVE_CATEGORIES\n");
            String reply = recieveMessage(server);
            Console.WriteLine(DateTime.Now + "\t" +"Reply:" + reply + "!");
            if (!reply.Equals(NULL_REPLY) && !reply.Equals(EMPTY_REPLY))
            {
                Console.WriteLine(DateTime.Now + "\t" +"Category List: " + reply);
            }
            else
            {
                Console.WriteLine(DateTime.Now + "\t" +"SERVER DOESNT HAVE CATEGORIES TO SEND");
                return new List<Category>();
            }

            server.Close();

            List<Category> c = JsonConvert.DeserializeObject<List<Category>>(reply);
            Console.WriteLine(DateTime.Now + "\t" +"C: " + c[0].ToString());
            return c; 
        }*/

        public List<Question> getQuestions()
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(RETRIEVE_QUESTIONS));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (!reply.Equals(NULL_REPLY))
                {
                    List<Question> questions = JsonConvert.DeserializeObject<List<Question>>(reply);
                    disconnectFromServer();
                    return questions;
                }
                else
                {
                    disconnectFromServer();
                    return new List<Question>();
                }
            }
            return null;
            /*
            Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return null;
            }

            //get current user
            server.Send(Encoding.ASCII.GetBytes(RETRIEVE_QUESTIONS));
            String reply = recieveMessage(server);
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + RETRIEVE_QUESTIONS);
            if (!reply.Equals(NULL_REPLY) && !reply.Equals(EMPTY_REPLY))
            {
                Console.WriteLine(DateTime.Now + "\t" +"Question List: " + reply);
            }
            else
            {
                Console.WriteLine(DateTime.Now + "\t" +"SERVER DOESNT HAVE QUESTIONS TO SEND");
                return new List<Question>();
            }

            server.Close();

            List<Question> q = JsonConvert.DeserializeObject<List<Question>>(reply);
            Console.WriteLine(DateTime.Now + "\t" +"Q: " + q[0].ToString());
            return q; */
        }

        //GET ANSWERED Q's
        public List<UserAnsweredQ> getAnsweredQuestions(String player)
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(RETRIEVE_USER_HISTORY));
                server.Send(Encoding.ASCII.GetBytes(player + "\n"));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (!reply.Equals(NULL_REPLY))
                {
                    List<UserAnsweredQ> answers = JsonConvert.DeserializeObject<List<UserAnsweredQ>>(reply);
                    disconnectFromServer();
                    return answers;
                }
                else
                {
                    disconnectFromServer();
                    return new List<UserAnsweredQ>();
                }
            }
            return null;
            /*Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return null;
            }
            server.Send(Encoding.ASCII.GetBytes(RETRIEVE_USER_HISTORY));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "RETRIEVE_USER_HISTORY");

            server.Send(Encoding.ASCII.GetBytes(player + "\n"));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + player);

            String reply =recieveMessage(server);
            Console.WriteLine(DateTime.Now + "\t" +"GOT A REPLY");
            
            Console.WriteLine(DateTime.Now + "\t" +"Reply:" + reply + "!");
            if (!reply.Equals(NULL_REPLY) && !reply.Equals(EMPTY_REPLY))
            {
                Console.WriteLine(DateTime.Now + "\t" +"Answered question List: " + reply);
            }
            else
            {
                Console.WriteLine(DateTime.Now + "\t" +"SERVER DOESNT HAVE QUESTIONS TO SEND");
                return new List<UserAnsweredQ>();
            }
            server.Close();
            List<UserAnsweredQ> q = JsonConvert.DeserializeObject<List<UserAnsweredQ>>(reply);
            if (q.Count() > 0)
            {
                Console.WriteLine(DateTime.Now + "\t" +"Q: " + q[0].ToString());
            }
            return q;*/
        }

        public List<String> getCisNames()
        {
            if (connectToServer(clientIP, clientPort))
            {
                server.Send(Encoding.ASCII.GetBytes(GET_CIS_NAMES + "\n" ));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (!reply.Equals(NULL_REPLY))
                {
                    List<String> cisNames = JsonConvert.DeserializeObject<List<String>>(reply);
                    disconnectFromServer();
                    return cisNames;
                }
                else
                {
                    disconnectFromServer();
                    
                }
            }
            return new List<String>();
        }

        public List<String> getInvitedUsers(String groupID)
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(RETRIEVE_INVITED_PLAYERS));
                server.Send(Encoding.ASCII.GetBytes(groupID + "\n"));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (!reply.Equals(NULL_REPLY))
                {
                    List<String> invitedUsers = JsonConvert.DeserializeObject<List<String>>(reply);
                    disconnectFromServer();
                    return invitedUsers;
                }
                else
                {
                    disconnectFromServer();
                    return new List<String>();
                }
            }
            return null;

            /*  Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
              IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
              Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
              try
              {
                  server.Connect(ip);
              }
              catch (SocketException e)
              {
                  Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                  Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                  return null;
              }
              server.Send(Encoding.ASCII.GetBytes(RETRIEVE_INVITED_PLAYERS));
              Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "RETRIEVE_INVITED_PLAYERS");
              server.Send(Encoding.ASCII.GetBytes(groupID + "\n"));
              Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + groupID);

              String reply = recieveMessage(server);

              Console.WriteLine(DateTime.Now + "\t" +"GOT A REPLY");
              Console.WriteLine(DateTime.Now + "\t" +"Reply:" + reply + "!");
              if (!reply.Equals(NULL_REPLY) && !reply.Equals(EMPTY_REPLY))
              {
                  Console.WriteLine(DateTime.Now + "\t" +"Answered question List: " + reply);
              }
              else
              {
                  Console.WriteLine(DateTime.Now + "\t" +"SERVER DOESNT HAVE QUESTIONS TO SEND");
                  return new List<String>();
              }
              server.Close();

              List<String> players = JsonConvert.DeserializeObject<List<String>>(reply);
              return players; */
        }

    /*    public List<String> getUserInterests()
        {
            if (connectToServer(clientIP, clientPort))
            {
                server.Send(Encoding.ASCII.GetBytes(GET_USER_INTERESTS));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (!reply.Equals(NULL_REPLY))
                {
                    List<String> interests = JsonConvert.DeserializeObject<List<String>>(reply);
                    disconnectFromServer();
                    return interests;
                }
                else
                {
                    disconnectFromServer();
                    return new List<String>();
                }

            }
            return null;
            
            Console.WriteLine(DateTime.Now + "\t" +"TRYING TO CLIENT TO Server ON " + clientIP + " " + clientPort.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(clientIP), clientPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return null;
            }
            server.Send(Encoding.ASCII.GetBytes(REQUEST_USER_INTERESTS));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "REQUEST_USER_INTERESTS\n");
            String reply = recieveMessage(server);
            Console.WriteLine(DateTime.Now + "\t" +"Reply:" + reply + "!");
            if (!reply.Equals(NULL_REPLY) && !reply.Equals(EMPTY_REPLY))
            {
                Console.WriteLine(DateTime.Now + "\t" +"Category List: " + reply);
            }
            else
            {
                Console.WriteLine(DateTime.Now + "\t" +"SERVER DOESNT HAVE CATEGORIES TO SEND");
                server.Close();
                return new List<String>();
            }

            server.Close();

            List<String> userInterests = JsonConvert.DeserializeObject<List<String>>(reply);
            Console.WriteLine(DateTime.Now + "\t" +userInterests);
            return userInterests; 
        }*/

        //CHANGE TO RECEIVE ACK, SEND QUESTIONS USER HAS ANSWERED
        public Boolean sendProgress(Object user, UserAnsweredQ answeredQ)
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(UPLOAD_PROGRESS));
                if (user is UserScore)
                {
                    server.Send(Encoding.ASCII.GetBytes(USER));
                }
                else if (user is Groups)
                {
                    server.Send(Encoding.ASCII.GetBytes(GROUP));
                }
                var sendUserScore = JsonConvert.SerializeObject(user);
                server.Send(Encoding.ASCII.GetBytes(sendUserScore + "\n"));
                var sendAnsweredQ = JsonConvert.SerializeObject(answeredQ);
                server.Send(Encoding.ASCII.GetBytes(sendAnsweredQ + "\n"));
                disconnectFromServer();
                return true;
            }
            return false;
            /* Console.WriteLine(DateTime.Now + "\t" +"UPLOADING PROGRESS TO SERVER");
             Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
             IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
             Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
             try
             {
                 server.Connect(ip);
             }
             catch (SocketException e)
             {
                 Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                 Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                 return;
             }
             server.Send(Encoding.ASCII.GetBytes(UPLOAD_PROGRESS));
             if(user is UserScore)
             {
                 Console.WriteLine(DateTime.Now + "\t" +"SENT AS USER");
                 server.Send(Encoding.ASCII.GetBytes("USER\n"));
             }
             else
             {
                 Console.WriteLine(DateTime.Now + "\t" +"SENT AS GROUP");
                 server.Send(Encoding.ASCII.GetBytes("GROUP\n"));
             }
             var sendUserScore = JsonConvert.SerializeObject(user);
             Console.WriteLine(DateTime.Now + "\t" +"Sending ... " + sendUserScore);
             server.Send(Encoding.ASCII.GetBytes(sendUserScore+"\n"));
             var sendAnsweredQ = JsonConvert.SerializeObject(answeredQ);
             Console.WriteLine(DateTime.Now + "\t" +"Sending..." + sendAnsweredQ);

             server.Send(Encoding.ASCII.GetBytes(sendAnsweredQ + "\n"));
             Console.WriteLine(DateTime.Now + "\t" +"THE LIST OF QUESTIONS ANSWERED ARE NOW UPLOADED!!!");


             server.Close();*/
        }



        public Boolean userLeaveGroup(String userJid)
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(LEAVE_GROUP));
                server.Send(Encoding.ASCII.GetBytes(userJid + "\n"));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (reply.Equals(TRUE_REPLY))
                {
                    disconnectFromServer();
                    return true;
                }
                else
                {
                    disconnectFromServer();
                    return false;
                }
            }
            return false;
            /*  Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
              IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
              Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
              try
              {
                  server.Connect(ip);
              }
              catch (SocketException e)
              {
                  Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                  Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                  return false;
              }
              server.Send(Encoding.ASCII.GetBytes(LEAVE_GROUP));
              Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "LEAVE_GROUP");
              server.Send(Encoding.ASCII.GetBytes(userJid + "\n"));
              Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + userJid);

              String reply = recieveMessage(server);
              if (reply.Equals("TRUE"))
              {
                  server.Close();
                  Console.WriteLine(DateTime.Now + "\t" +"RETURNING TRUE FROM CREATE GROUP");
                  return true;
              }

              server.Close();
              return false;*/
        }

        public Groups getUsersGroup()
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(RETRIEVE_USER_GROUP));
                server.Send(Encoding.ASCII.GetBytes(userID + "\n"));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (!reply.Equals(NULL_REPLY))
                {
                    Groups group = JsonConvert.DeserializeObject<Groups>(reply);
                    disconnectFromServer();
                    return group;
                }
                else
                {
                    disconnectFromServer();
                    Groups group = new Groups();
                    group.groupName = null;
                    return group;
                }
            }
            return null;
            /* Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
             IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
             Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
             try
             {
                 server.Connect(ip);
             }
             catch (SocketException e)
             {
                 Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                 Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                 return null;
             }
             server.Send(Encoding.ASCII.GetBytes(RETRIEVE_USER_GROUP));
             Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "RETRIEVE_USER_GROUP");
             server.Send(Encoding.ASCII.GetBytes(userID + "\n"));
             Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + userID);

             String reply = recieveMessage(server);

             Console.WriteLine(DateTime.Now + "\t" +"GOT A REPLY");
             Console.WriteLine(DateTime.Now + "\t" +"Reply:" + reply + "!");
             if (!reply.Equals(NULL_REPLY))
             {
                 Console.WriteLine(DateTime.Now + "\t" +"Answered question List: " + reply);
             }
             else
             {
                 Console.WriteLine(DateTime.Now + "\t" +"SERVER DOESNT HAVE QUESTIONS TO SEND");
                 return null;
             }
             server.Close();

             Groups group = JsonConvert.DeserializeObject<Groups>(reply);
             return group;
             */
        }

        public List<String> getGroupPlayers(String groupID)
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(RETRIEVE_GROUP_PLAYERS));
                server.Send(Encoding.ASCII.GetBytes(groupID + "\n"));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (!reply.Equals(NULL_REPLY))
                {
                    List<String> groupPlayers = JsonConvert.DeserializeObject<List<String>>(reply);
                    disconnectFromServer();
                    return groupPlayers;
                }
                else
                {
                    disconnectFromServer();
                    return new List<String>();
                }
            }
            return null;
            /* Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
             IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
             Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
             try
             {
                 server.Connect(ip);
             }
             catch (SocketException e)
             {
                 Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                 Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                 return null;
             }
             server.Send(Encoding.ASCII.GetBytes(RETRIEVE_GROUP_PLAYERS));
             Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "RETRIEVE_GROUP_PLAYERS");
             server.Send(Encoding.ASCII.GetBytes(groupID + "\n"));
             Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + groupID);

             String reply = recieveMessage(server);

             Console.WriteLine(DateTime.Now + "\t" +"GOT A REPLY");
             Console.WriteLine(DateTime.Now + "\t" +"Reply:" + reply + "!");
             if (!reply.Equals(NULL_REPLY) && !reply.Equals(EMPTY_REPLY))
             {
                 Console.WriteLine(DateTime.Now + "\t" +"Answered question List: " + reply);
             }
             else
             {
                 Console.WriteLine(DateTime.Now + "\t" +"SERVER DOESNT HAVE QUESTIONS TO SEND");
                 return new List<String>();
             }
             server.Close();

             List<String> users = JsonConvert.DeserializeObject<List<String>>(reply);
             return users;*/

        }

        public List<PendingJoins> getGroupNotifications()
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(RETRIEVE_NOTIFICATIONS));
                server.Send(Encoding.ASCII.GetBytes(userID + "\n"));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (!reply.Equals(NULL_REPLY))
                {
                    List<PendingJoins> pendingJoins = JsonConvert.DeserializeObject<List<PendingJoins>>(reply);
                    disconnectFromServer();
                    return pendingJoins;
                }
                else
                {
                    disconnectFromServer();
                    return new List<PendingJoins>();
                }
            }
            return null;
            /*
            Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return null;
            }
            server.Send(Encoding.ASCII.GetBytes(RETRIEVE_NOTIFICATIONS));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "RETRIEVE_NOTIFICATIONS");
            server.Send(Encoding.ASCII.GetBytes(userID + "\n"));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + userID);

            String reply = recieveMessage(server);

            Console.WriteLine(DateTime.Now + "\t" +"GOT A REPLY");
            Console.WriteLine(DateTime.Now + "\t" +"Reply:" + reply + "!");
            if (!reply.Equals(NULL_REPLY) && !reply.Equals(EMPTY_REPLY))
            {
                Console.WriteLine(DateTime.Now + "\t" +"Answered question List: " + reply);
            }
            else
            {
                Console.WriteLine(DateTime.Now + "\t" +"SERVER DOESNT HAVE QUESTIONS TO SEND");
                return new List<PendingJoins>();
            }
            server.Close();

            List<PendingJoins> notifications = JsonConvert.DeserializeObject<List<PendingJoins>>(reply);
            return notifications;*/

        }

        public Boolean createGroup(String userID)
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(CREATE_GROUP));
                server.Send(Encoding.ASCII.GetBytes(userID + "\n"));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (reply.Equals(TRUE_REPLY))
                {
                    disconnectFromServer();
                    return true;
                }
                else
                {
                    disconnectFromServer();
                }

            }
            return false;
            /*
            Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return false;
            }
            server.Send(Encoding.ASCII.GetBytes(CREATE_GROUP));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "CREATE_GROUP");
            server.Send(Encoding.ASCII.GetBytes(userID + "\n"));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + userID);

            String reply = recieveMessage(server);
            if (reply.Equals("TRUE"))
            {
                server.Close();
                Console.WriteLine(DateTime.Now + "\t" +"RETURNING TRUE FROM CREATE GROUP");
                return true;
            }
          
            server.Close();
            return false; */

        }

        public List<String> getAllPlayers()
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(RETRIEVE_ALL_USERS));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (!reply.Equals(NULL_REPLY))
                {
                    List<String> allPlayers = JsonConvert.DeserializeObject<List<String>>(reply);
                    disconnectFromServer();
                    return allPlayers;
                }
                else
                {
                    disconnectFromServer();
                    return new List<String>();
                }

            }
            return null;
            /*
            Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return null;
            }
            server.Send(Encoding.ASCII.GetBytes(RETRIEVE_ALL_USERS));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "RETRIEVE_ALL_USERS");

            String reply = recieveMessage(server);

            Console.WriteLine(DateTime.Now + "\t" +"GOT A REPLY");
            Console.WriteLine(DateTime.Now + "\t" +"Reply:" + reply + "!");
            if (!reply.Equals(NULL_REPLY) && !reply.Equals(EMPTY_REPLY))
            {
                Console.WriteLine(DateTime.Now + "\t" +"Answered question List: " + reply);
            }
            else
            {
                Console.WriteLine(DateTime.Now + "\t" +"SERVER DOESNT HAVE QUESTIONS TO SEND");
                return new List<String>();
            }
            server.Close();

            List<String> allUsers = JsonConvert.DeserializeObject<List<String>>(reply);
            return allUsers;*/

        }

        public Boolean inviteUserToGroup(String fromUser, String toUser, String groupName)
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(INVITE_USER));
                server.Send(Encoding.ASCII.GetBytes(fromUser + "\n"));
                server.Send(Encoding.ASCII.GetBytes(toUser + "\n"));
                server.Send(Encoding.ASCII.GetBytes(groupName + "\n"));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (reply.Equals(TRUE_REPLY))
                {
                    disconnectFromServer();
                    return true;
                }
                else
                {
                    disconnectFromServer();
                    return false;
                }
            }
            return false;
            /*
            return false;
            Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return false;
            }
            server.Send(Encoding.ASCII.GetBytes(INVITE_USER));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "INVITE_USER");
            server.Send(Encoding.ASCII.GetBytes(fromUser + "\n"));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + fromUser);
            server.Send(Encoding.ASCII.GetBytes(toUser + "\n"));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + toUser);
            server.Send(Encoding.ASCII.GetBytes(groupName + "\n"));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + groupName);
            String reply = recieveMessage(server);
            if (reply.Equals("TRUE"))
            {
                server.Close();
                Console.WriteLine(DateTime.Now + "\t" +"RETURNING TRUE FROM CREATE GROUP");
                return true;
            }

            server.Close();
            return false; */
        }

        public Boolean addUserToGroup(String groupName, String userID)
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(ADD_USER_TO_GROUP));
                server.Send(Encoding.ASCII.GetBytes(groupName + "\n"));
                server.Send(Encoding.ASCII.GetBytes(userID + "\n"));
                String reply = recieveMessage();// if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (reply.Equals(TRUE_REPLY))
                {
                    disconnectFromServer();
                    return true;
                }
                else
                {
                    disconnectFromServer();
                    return false;
                }
            }
            return false;
            /*
            Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return false;
            }
            server.Send(Encoding.ASCII.GetBytes(ADD_USER_TO_GROUP));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "ADD_USER_TO_GROUP");
            server.Send(Encoding.ASCII.GetBytes(groupName + "\n"));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + groupName);
            server.Send(Encoding.ASCII.GetBytes(userID + "\n"));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + userID);

            String reply = recieveMessage(server);

            Console.WriteLine(DateTime.Now + "\t" +"GOT A REPLY");
            Console.WriteLine(DateTime.Now + "\t" +"Reply:" + reply + "!");
            if (!reply.Equals("TRUE\n"))
            {
                Console.WriteLine(DateTime.Now + "\t" +"Answered question List: " + reply);
                server.Close();
                return true;
            }
            else
            {
                Console.WriteLine(DateTime.Now + "\t" +"SERVER DOESNT HAVE QUESTIONS TO SEND");
                server.Close();
                return false;
            }
             * */
        }

        //TODO A REPLY
        public Boolean removeUserFromGroup(String userID)
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(REMOVE_USER_FROM_GROUP));
                server.Send(Encoding.ASCII.GetBytes(userID + "\n"));
                disconnectFromServer();
                return true;
            }
            return false;
            /* Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
             IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
             Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
             try
             {
                 server.Connect(ip);
             }
             catch (SocketException e)
             {
                 Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                 Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                 return false;
             }
             server.Send(Encoding.ASCII.GetBytes(REMOVE_USER_FROM_GROUP));
             Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "REMOVE_USER_FROM_GROUP");
             server.Send(Encoding.ASCII.GetBytes(userID + "\n"));
             Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + userID);

             return true;*/

        }

        public Boolean deleteGroup(String groupID)
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(DELETE_GROUP));
                server.Send(Encoding.ASCII.GetBytes(groupID + "\n"));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (reply.Equals(TRUE_REPLY))
                {
                    disconnectFromServer();
                    return true;
                }
                else
                {
                    disconnectFromServer();
                    return false;
                }
            }
            return false;
            /*
            Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return false;
            }
            server.Send(Encoding.ASCII.GetBytes(DELETE_GROUP));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "DELETE_GROUP");
            server.Send(Encoding.ASCII.GetBytes(groupID + "\n"));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + groupID);

            String reply = recieveMessage(server);
            if (reply.Equals("TRUE"))
            {
                server.Close();
                Console.WriteLine(DateTime.Now + "\t" +"RETURNING TRUE FROM DELETE GROUP");
                return true;
            }

            server.Close();
            return false;
            */
        }

        public Boolean resetUser(String userID)
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(RESET_USER));
                server.Send(Encoding.ASCII.GetBytes(userID + "\n"));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (reply.Equals(TRUE_REPLY))
                {
                    disconnectFromServer();
                    return true;
                }
                else
                {
                    disconnectFromServer();
                    return false;
                }
            }
            return false;
            /*
            Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return false;
            }
            server.Send(Encoding.ASCII.GetBytes(RESET_USER));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "RESET_USER");
            server.Send(Encoding.ASCII.GetBytes(userID + "\n"));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + userID);

            String reply = recieveMessage(server);
            if (reply.Equals("TRUE"))
            {
                server.Close();
                Console.WriteLine(DateTime.Now + "\t" +"RETURNING TRUE FROM CREATE GROUP");
                return true;
            }

            server.Close();
            return false;
            */
        }

        public List<Groups> getGroups()
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(RETRIEVE_GROUPS));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (!reply.Equals(NULL_REPLY))
                {
                    List<Groups> groups = JsonConvert.DeserializeObject<List<Groups>>(reply);
                    disconnectFromServer();
                    return groups;
                }
                else
                {
                    disconnectFromServer();
                    return new List<Groups>();
                }
            }
            return null;
            /*Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return null;
            }
            server.Send(Encoding.ASCII.GetBytes(RETRIEVE_GROUPS));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "RETRIEVE_GROUPS");

            String reply = recieveMessage(server);
            if (!reply.Equals(NULL_REPLY) && !reply.Equals(EMPTY_REPLY))
            {
                server.Close();
                Console.WriteLine(DateTime.Now + "\t" +"RETURNING TRUE FROM CREATE GROUP");
                return Newtonsoft.Json.JsonConvert.DeserializeObject<List<Groups>>(reply);
            }


            server.Close();
            return new List<Groups>();*/

        }

        public Boolean deleteNotifications(List<PendingJoins> notifcations)
        {
            if (connectToServer(serverIP, serverPort))
            {
                server.Send(Encoding.ASCII.GetBytes(DELETE_NOTIFICATIONS));
                String sendNotifications = "";
                try
                {
                    Newtonsoft.Json.JsonConvert.SerializeObject(notifcations);
                }
                catch (Exception e)
                {

                }
                server.Send(Encoding.ASCII.GetBytes(sendNotifications + "\n"));
                String reply = recieveMessage(); //if(reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (reply.Equals(TRUE_REPLY))
                {
                    disconnectFromServer();
                    return true;
                }
                else
                {
                    disconnectFromServer();
                    return false;
                }
            }
            return false;
            /*
            Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return false;
            }
            server.Send(Encoding.ASCII.GetBytes(DELETE_NOTIFICATIONS));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "DELETE_NOTIFICATIONS");
            String sendNotifications = Newtonsoft.Json.JsonConvert.SerializeObject(notifcations);
            server.Send(Encoding.ASCII.GetBytes(sendNotifications + "\n"));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + sendNotifications);

            String reply = recieveMessage(server);
            if (reply.Equals("TRUE"))
            {
                server.Close();
                Console.WriteLine(DateTime.Now + "\t" +"RETURNING TRUE FROM CREATE GROUP");
                return true;
            }

            server.Close();
            return false;
            */
        }


        public Boolean deleteNotifications(PendingJoins notifcations)
        {
            if (connectToServer(serverIP, serverPort))
            {
                List<PendingJoins> joins = new List<PendingJoins>();
                joins.Add(notifcations);
                server.Send(Encoding.ASCII.GetBytes(DELETE_NOTIFICATIONS));
                String sendNotifications = Newtonsoft.Json.JsonConvert.SerializeObject(joins);
                server.Send(Encoding.ASCII.GetBytes(sendNotifications + "\n"));
                String reply = recieveMessage();// if (reply.Equals("ERROR")) MainWindow.SwitchPage(new CommsError());
                if (reply.Equals(TRUE_REPLY))
                {
                    disconnectFromServer();
                    return true;
                }
                else
                {
                    disconnectFromServer();
                    return false;
                }
            }
            return false;
            /*List<PendingJoins> joins = new List<PendingJoins>();
            joins.Add(notifcations);
            Console.WriteLine(DateTime.Now + "\t" +"TRYING TO SPEAK TO Server ON " + serverIP + " " + serverPort.ToString());
            IPEndPoint ip = new IPEndPoint(IPAddress.Parse(serverIP), serverPort);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                server.Connect(ip);
            }
            catch (SocketException e)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SOCKET_CLIENT: Unable to connect to server.");
                Console.WriteLine(DateTime.Now + "\t" +e.ToString());
                return false;
            }
            server.Send(Encoding.ASCII.GetBytes(DELETE_NOTIFICATIONS));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + "DELETE_NOTIFICATIONS");
            String sendNotifications = Newtonsoft.Json.JsonConvert.SerializeObject(joins);
            server.Send(Encoding.ASCII.GetBytes(sendNotifications + "\n"));
            Console.WriteLine(DateTime.Now + "\t" +"SENT TO SERVER: " + sendNotifications);

            String reply = recieveMessage(server);
            if (reply.Equals("TRUE"))
            {
                server.Close();
                Console.WriteLine(DateTime.Now + "\t" +"RETURNING TRUE FROM CREATE GROUP");
                return true;
            }

            server.Close();
            return false;
            */
        }


        private String recieveMessage()
        {
                byte[] data = new byte[12288];
                int receivedDataLength = 0;
                StringBuilder sb = new StringBuilder();
                receivedDataLength = server.Receive(data);
                Console.WriteLine(DateTime.Now + "\t" + "Size of data recieved" + data.Length);
                sb.Append(Encoding.ASCII.GetString(data, 0, receivedDataLength).Trim());
                Console.WriteLine(DateTime.Now + "\t" + "Size of string builder..." + sb.Length);
                while (receivedDataLength == 12288)
                {
                   // Console.WriteLine(DateTime.Now + "\t" +"Reading next 1024 bytes");

                    receivedDataLength = server.Receive(data);
                    sb.Append(Encoding.ASCII.GetString(data, 0, receivedDataLength).Trim());
    
                }
                Console.WriteLine(DateTime.Now + "\t" +sb.ToString());
                return sb.ToString();
            
        }

      /*  private String recieveMessage()
        {
            byte[] data = new byte[1024];
            int receivedDataLength = 0;
            StringBuilder sb = new StringBuilder();
            receivedDataLength = server.Receive(data);
            Console.WriteLine(DateTime.Now + "\t" +receivedDataLength.ToString());
            sb.Append(Encoding.ASCII.GetString(data, 0, receivedDataLength).Trim());
            while (receivedDataLength == 1024)
            {
                Console.WriteLine(DateTime.Now + "\t" +"Reading next 1024 bytes");
                receivedDataLength = server.Receive(data);
                sb.Append(Encoding.ASCII.GetString(data, 0, receivedDataLength).Trim());
                /*      if (receivedDataLength < 1)
                      {
                          Console.WriteLine(DateTime.Now + "\t" +"DIDNT GET A REPLY");
                          server.Close();
                          return null;
                      }
                      else
                      {
                          Console.WriteLine(DateTime.Now + "\t" +"REPLY:" + reply + " .");
                          sb.Append(Encoding.ASCII.GetString(data, 0, receivedDataLength).Trim());
                         // reply = reply + Encoding.ASCII.GetString(data, 0, receivedDataLength).Trim();
                          if (receivedDataLength < 1024)
                          {
                              break;
                          }
                      }
            }
          //  receivedDataLength = server.Receive(data);
           // sb.Append(Encoding.ASCII.GetString(data, 0, receivedDataLength).Trim());
            Console.WriteLine(DateTime.Now + "\t" +sb.ToString());
        
            return sb.ToString();
        }*/

      /*  public String receiveQuestions()
        {
            StringBuilder sb = new StringBuilder();
            try
            {
               // int port = 2113;
                byte[] bytes = new byte[1024];
                string data;


            
                    //  TcpClient incoming = server.AcceptTcpClient();
                    //  NetworkStream stream = incoming.GetStream();

                    int i;
                    int counter = 0;
                    Boolean inTransfer = true;
                  //  StringBuilder sb = new StringBuilder();


                    // Loop to receive all the data sent by the client.
                    //i = server.Receive(bytes);

                    //i = stream.Read(bytes, 0, bytes.Length);
                    // FileStream fileStream = null;
                    String text = string.Empty;
                   // data = System.Text.Encoding.ASCII.GetString(bytes, 0, i);
                    //   if (this.gui.isUserLoggedIn(data.Trim()))
                    //    {
                  //  i = stream.Read(bytes, 0, bytes.Length);
                    i = server.Receive(bytes);
                    data = System.Text.Encoding.ASCII.GetString(bytes, 0, i);

                    while (i != 0)
                    {

                        if (inTransfer)
                        {
                            counter = counter + i;

                            //fileStream.Write(bytes, 0, i);
                            sb.Append(data);
                        }

                        else
                        {
                            inTransfer = false;
                           // data = System.Text.Encoding.ASCII.GetString(bytes, 0, i);
                            if (log.IsDebugEnabled) log.Debug(String.Format("Received: {0}", data));


                            this.outputFileName = data.Trim();

                            this.transferImageInProgress = true;
                            this.createfullPath();

                            fileStream = File.OpenWrite(outputFileName);
                            if (log.IsDebugEnabled) log.Debug("Writing to file: " + outputFileName);

                            //stream.Flush();
                        }


                        i = server.Receive(bytes);
                        //            if (log.IsDebugEnabled)  log.Debug("After :" + i);
                    }
                 //   if (log.IsDebugEnabled) log.Debug("copied: " + this.outputFileName);
                //    if (log.IsDebugEnabled) log.Debug("bytes read:" + counter);
                    
              //      fileStream.Close();
                  //  fileStream = null;
                    inTransfer = false;
              //      this.gui.showImage(this.outputFileName);
                }

           //     incoming.Close();

            


           // }
            catch (Exception exc)
            {
                Console.WriteLine(DateTime.Now + "\t" +"Error:" + exc.ToString());
           //     log.Error("", exc);
            }
            if(sb.Length < 1 )
            {
                return null;
            }
            return sb.ToString();
            
        }*/

    }


}
