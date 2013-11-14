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
using SocialLearningGame.Comms;
using SocialLearningGame.Pages;
using SocialLearningGame.Entities;
using SocialLearningGame.Logic.Poses;

namespace SocialLearningGame.Logic
{
    public static class GameLogic
    {
      //log.  private static log4net.ILog log = log4net.LogManager.GetLogger(typeof(GameLogic));

        public static readonly AbstractPose[] PossiblePoses = { 
                                                                   new Pose1(), new Pose2(), 
                                                                   new Pose3(), new Pose4(), 
                                                                   new Pose5(), new Pose6(), 
                                                                   new Pose7(), new Pose8(), 
                                                                   new Pose9()
                                                               };


        //log.random number initialised used to choose next pose, question, etc
        private static readonly Random random = new Random();

        private static ClientComms _clientComms;

        public static Category RequestedCategory { get; set; }
        public static UserSession _userSession;
        private static String userJID;
        private static Boolean connectedToSocieties = false;

        public static int questionCount = 0;

        public static Boolean connectToSocieties()
        {
            if (!connectedToSocieties)
            {
                //SET UP A NEW USER SESSION
                _userSession = new UserSession();

                //SPEAK TO DISPLAYPORTAL
                _clientComms = new ClientComms();
                if (!_clientComms.getSessionParameters())
                {
                    Console.WriteLine(DateTime.Now + "\t" +"Setting game error here 1");
                    _userSession.gameStage = GameStage.SetupError;
                    return false;
                }
                userJID = _clientComms.getUserID();
                //SPEAKING TO CLIENT
                _clientComms.getSocietiesServer();
                Console.WriteLine(DateTime.Now + "\t" +"Recieved server IP & Port. Can now start to retrieve data...");
                connectedToSocieties = true;
                return true;
            }
            return true;

            //NOW HAVE CLIENT AND SERVER IP STORED
        }

        private static void setUserNames()
        {
            int counter = 0;
            while (counter < _userSession.allUsers.Count())
            {
                _userSession.allUsers[counter].name = _userSession.allUsers[counter].userJid.Split(new char[] { '.' }, 2)[0];
                counter++;
            }
        }

        //METHOD TO ACCESS CLIENT COMMS
        public static void getRemoteData(DataType dataType)
        {
            switch (dataType)
            {
                case DataType.ALL_USERS :
                    _userSession.allUsers = _clientComms.getAllUsers();
                    setUserNames();
                    break;
                case DataType.ALL_CATEGORIES :
                    _userSession.allCategories = _clientComms.getCategories();
                    break;
                case DataType.ALL_QUESTIONS:
                    _userSession.allQuestions = _clientComms.getQuestions();
                    break;
                case DataType.ALL_GROUPS :
                    _userSession.allGroups = _clientComms.getGroups();
                    break;
                case DataType.ALL_GROUP_PLAYERS:
                    _userSession.allGroupPlayers = _clientComms.getGroupPlayers(_userSession.currentGroup.groupID.ToString());
                    break;
                case DataType.CURRENT_GROUP:
                    _userSession.currentGroup = _clientComms.getUsersGroup();
                    break;
                case DataType.ALL_USER_ANSWERED:
                    _userSession.userAnsweredQuestions = _clientComms.getAnsweredQuestions(_userSession.currentUser.userJid);
                    break;
                case DataType.ALL_GROUP_ANSWERED:
                    _userSession.groupAnsweredQuestions = _clientComms.getAnsweredQuestions(_userSession.currentGroup.groupName);
                    break;
                case DataType.ALL_NOTIFICATIONS:
                    _userSession.allNotifications = _clientComms.getGroupNotifications();
                    break;
                case DataType.USER_INTERESTS:
                    _userSession.userInterests = _clientComms.getUserInterests();
                    break;
                case DataType.INVITED_USERS:
                    _userSession.invitedUsers = _clientComms.getInvitedUsers(_userSession.currentGroup.groupName);
                    break;
            }
        }

        //METHOD TO POST DATA
        public static void postRemoteData(DataType dataType, Object arg)
        {
            switch (dataType)
            {
                case DataType.POST_PROGRESS :
                    if (_userSession.player == GameStage.USER)
                    {
                        _clientComms.sendProgress(_userSession.currentUser, _userSession.answeredQuestion);
                    }
                    else if (_userSession.player == GameStage.GROUP)
                    {
                        _clientComms.sendProgress(_userSession.currentGroup, _userSession.answeredQuestion);
                    }
                    break;
                case DataType.CREATE_GROUP:
                    if (!_clientComms.createGroup(_userSession.currentUser.userJid))
                    {
                        getRemoteData(DataType.ALL_GROUPS);
                        MainWindow.SwitchPage(new CommsError());
                    }
                    break;
                case DataType.DELETE_GROUP:
                    if (!_clientComms.deleteGroup(_userSession.currentGroup.groupID.ToString()))
                    {
                        getRemoteData(DataType.ALL_GROUPS);
                        MainWindow.SwitchPage(new CommsError());
                    }
                    break;
                case DataType.LEAVE_GROUP:
                    if (!_clientComms.removeUserFromGroup(_userSession.currentUser.userJid))
                    {
                        getRemoteData(DataType.ALL_GROUPS);
                        MainWindow.SwitchPage(new CommsError());
                    }
                    break;
                case DataType.INVITE_PLAYER:
                    if (!_clientComms.inviteUserToGroup(_userSession.currentUser.userJid, (String) arg, _userSession.currentGroup.groupName))
                    {
                        MainWindow.SwitchPage(new CommsError());
                    }
                    break;
                case DataType.ACCEPT_NOTIFICATION:
                    if (!_clientComms.addUserToGroup((String) arg, _userSession.currentUser.userJid))
                    {
                        MainWindow.SwitchPage(new CommsError());
                    }
                    break;
                case DataType.DELETE_NOTIFICATION:
                    if (!_clientComms.deleteNotifications((PendingJoins) arg))
                    {
                        MainWindow.SwitchPage(new CommsError());
                    }
                    break;

                  
            }
        }

        public static UserSession newUserSession()
        {
            _userSession = new UserSession();
            _userSession.gameStage = GameStage.InProgress;
            //CONNECT TO SOCIETIES
            //IF FAILURE - DO NOT CONTINUE
            if (!connectedToSocieties)
            {
                if (!connectToSocieties())
                {
                    return _userSession;
                }
            }
            //CONNECTED TO SOCIETIES, NOW WE CAN CONTINUE

            //GET ALL USERS
            Console.WriteLine(DateTime.Now + "\t" +"Getting all users");
            getRemoteData(DataType.ALL_USERS);
            if (_userSession.allUsers == null)
            {
                _userSession.gameStage = GameStage.SetupError;
            }
            //SET THIS USER IN USER SESSION
            foreach (UserScore user in _userSession.allUsers)
            {
                if (user.userJid == userJID)
                {
                    _userSession.currentUser = user;
                }
            }

            //GET ALL CATEGORIES
            Console.WriteLine(DateTime.Now + "\t" +"Getting all categories");
            getRemoteData(DataType.ALL_CATEGORIES);
            if (_userSession.allCategories == null)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SET UP ERROR WITH CATEGORIES");
                _userSession.gameStage = GameStage.SetupError;
            }

            //GET ALL QUESTIONS
            Console.WriteLine(DateTime.Now + "\t" +"Getting all questions");
            getRemoteData(DataType.ALL_QUESTIONS);
            if (_userSession.allQuestions == null)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SET UP ERROR WITH QUESTIONS");
                _userSession.gameStage = GameStage.SetupError;
            }

            //GET ALL GROUPS
            Console.WriteLine(DateTime.Now + "\t" +"Getting all groups");
            getRemoteData(DataType.ALL_GROUPS);
            if (_userSession.allGroups == null)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SET UP ERROR WITH ALL GROUP");
                _userSession.gameStage = GameStage.SetupError;
            }

            //GET USERS GROUP
            Console.WriteLine(DateTime.Now + "\t" +"Getting users group");
            getRemoteData(DataType.CURRENT_GROUP);
            if (_userSession.currentGroup == null)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SET UP ERROR WITH USERS GROUP");
                _userSession.gameStage = GameStage.SetupError;
            }
            //EMPTY GROUP IS RETURNED IF NULL
            if (_userSession.currentGroup.groupName == null)
            {
                _userSession.currentGroup = null;
            }
            
           
            
            //GET PREVIOUS ANSWERS
            //GET USER ANSWERS
            Console.WriteLine(DateTime.Now + "\t" +"Getting all user answered questions");
            getRemoteData(DataType.ALL_USER_ANSWERED);
            if (_userSession.userAnsweredQuestions == null)
            {
                _userSession.gameStage = GameStage.SetupError;
            }
            //IF USER IS IN GROUP, GET GROUP ANSWERS, AND GROUP PLAYERS
            if (_userSession.currentGroup != null)
            {
                Console.WriteLine(DateTime.Now + "\t" +"Getting all group answered questions");
                getRemoteData(DataType.ALL_GROUP_ANSWERED);
                if (_userSession.groupAnsweredQuestions == null)
                {
                    Console.WriteLine(DateTime.Now + "\t" +"SET UP ERROR WITH GROUP ANSWERS");
                    _userSession.gameStage = GameStage.SetupError;
                }
                Console.WriteLine(DateTime.Now + "\t" +"Getting all group players");
                getRemoteData(DataType.ALL_GROUP_PLAYERS);
                if (_userSession.allGroupPlayers== null)
                {
                    Console.WriteLine(DateTime.Now + "\t" +"SET UP ERROR WITH GROUP PLAYERS");
                    _userSession.gameStage = GameStage.SetupError;
                }
            }

            //GET NOTIFICATIONS
            Console.WriteLine(DateTime.Now + "\t" +"Getting all notifications");
            getRemoteData(DataType.ALL_NOTIFICATIONS);
            if (_userSession.allNotifications == null)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SET UP ERROR WITH NOTIFICATIONS");
                _userSession.gameStage = GameStage.SetupError;
            }
            
            //GET USER INTERESTS
            Console.WriteLine(DateTime.Now + "\t" +"Getting all user interests");
            getRemoteData(DataType.USER_INTERESTS);
            if (_userSession.userInterests == null)
            {
                Console.WriteLine(DateTime.Now + "\t" +"SET UP ERROR WITH USERS INTERESTS");
                _userSession.gameStage = GameStage.SetupError;
            }

            if (_userSession.gameStage == GameStage.InProgress)
            {
                _userSession.gameStage = GameStage.ReadyToStart;
            }

            _userSession.player = GameStage.USER;

            return _userSession;
        }



        public static void NewGame(Category userCategory)
        {

            _userSession.gameStage = GameStage.InProgress;

            if (_userSession.player == GameStage.USER)
            {
                //SET UP QUESTIONS FOR USER

                //REMOVE PREVIOUSLY ANSWERED QUESTIONS
                _userSession.availableUserQuestions = new List<Question>(_userSession.allQuestions);
                _userSession.availableUserQuestions.RemoveAll(q1 => _userSession.userAnsweredQuestions.Any(q2 => q1.questionID == q2.questionID));

                //REMOVE SO ONLY REQUESTED CATEGORY REMAIN
                if (userCategory != null)
                {
                    _userSession.availableUserQuestions.RemoveAll(q1 => q1.categoryID != userCategory.categoryID);
                }
            }
            else if (_userSession.player == GameStage.GROUP)
            {
                //SET UP QUESTIONS FOR GROUP
                _userSession.availableGroupQuestions = new List<Question>(_userSession.allQuestions);
                _userSession.availableGroupQuestions.RemoveAll(q1 => _userSession.groupAnsweredQuestions.Any(q2 => q1.questionID == q2.questionID));
            }

            _userSession.questionRound = new QuestionRound();


            _userSession.gameStage = GameStage.ReadyToStart;

        }


        public static void getGroupInformation()
        {
            //GET USERS GROUP
            getRemoteData(DataType.CURRENT_GROUP);
            if (_userSession.currentGroup.groupName == null)
            {
                _userSession.currentGroup = null;
            }

            //IF USER HAS A GROUP, GET PLAYERS
            if (_userSession.currentGroup != null)
            {
                getRemoteData(DataType.ALL_GROUP_PLAYERS);
                if (_userSession.allGroupPlayers == null)
                {
                    //THERE IS A COMMUNICATION ERROR
                    MainWindow.SwitchPage(new CommsError());
                }

                //GET INVITED USERS
                getRemoteData(DataType.INVITED_USERS);
                if (_userSession.invitedUsers == null)
                {
                    MainWindow.SwitchPage(new CommsError());
                }
            }

            //GET NOTIFICATIONS
            getRemoteData(DataType.ALL_NOTIFICATIONS);
            if (_userSession.allNotifications == null)
            {
                _userSession.gameStage = GameStage.SetupError;
            }
        }
  
        //GET THE NEXT QUESTION ROUND
        public static void NextQuestion()
        {
            _userSession.questionRound.Question = PickRandomQuestion();
            _userSession.questionRound.RoundNumber++;
            _userSession.questionRound.RequiredGrammar = PickRandomGrammar();
            _userSession.questionRound.RequiredPose = PickRandomPose();
            _userSession.questionRound.AnswerMethod = PickRandomAnswerMethod();
        }


            

  

        private static AnswerMethod PickRandomAnswerMethod()
        {
            Array values = Enum.GetValues(typeof(AnswerMethod));
            return (AnswerMethod)values.GetValue(random.Next(values.Length));
        }

        private static AbstractPose PickRandomPose()
        {
            return PossiblePoses[random.Next(PossiblePoses.Length)];
        }

        private static Grammar PickRandomGrammar()
        {
            Array values = Enum.GetValues(typeof(Grammar));
            return (Grammar)values.GetValue(random.Next(values.Length));
        }

        private static Question PickRandomQuestion()
        {
            if (_userSession.player == GameStage.USER)
            {
                //CHECK IF QUESTIONS ARE AVAILABLE
                if (_userSession.availableUserQuestions.Count() == 0)
                {
                    //NO QUESTIONS AVAILABLE
                    return null;
                }

                //QUESTION IS AVAILABLE
                return _userSession.availableUserQuestions[random.Next(_userSession.availableUserQuestions.Count())];
            }
            else if (_userSession.player == GameStage.GROUP)
            {
                Console.WriteLine(DateTime.Now + "\t" +"In group questions");
                //CHECK IF QUESTIONS ARE AVAILABLE
                if (_userSession.availableGroupQuestions.Count() == 0)
                {
                    //NO QUESTIONS AVAILABLE
                    return null;
                }

                //QUESTION IS AVAILABLE
                return _userSession.availableGroupQuestions[random.Next(_userSession.availableGroupQuestions.Count())];
            }
            return null;
        }


    }
}
