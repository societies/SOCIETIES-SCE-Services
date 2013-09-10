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
                                                                   new Pose9(), new Pose10() 
                                                               };


        //log.random number initialised used to choose next pose, question, etc
        private static readonly Random random = new Random();

        private static ServerComms _comms;
        private static ClientComms _clientComms;

        private static GameSession _gameSession;
        public static Category RequestedCategory { get; set; }
        public static QuestionDifficulty RequestedDifficulty { get; set; }
        private static string clientIP;
        private static int clientPort;
        private static string serverIP;
        private static int serverPort;
        private static string thisUser;
        public static string userName;
        public static List<UserScore> allUsers;
        private static List<Question> answeredQuestions = new List<Question>();
        private static List<UserAnsweredQ> roundAnsweredQ;


        private static void InitComms()
        {
          //log.  log.Debug("Init comms");

            _comms = new ServerComms();
            
        }

        public static void addAnsweredQ(UserAnsweredQ answered)
        {
            roundAnsweredQ.Add(answered);
        }

        public static GameSession NewGame()
        {
           //log. log.Debug("Starting new game");
            roundAnsweredQ = new List<UserAnsweredQ>();
            _clientComms = new ClientComms();
            _clientComms.getSessionParameters();
            _gameSession = new GameSession();
            thisUser = _clientComms.getUserID();
            String client_address_port = _clientComms.getAddressPort();
            String[] add = client_address_port.Split(':');
            clientIP = add[0];
            clientPort = Convert.ToInt32(add[1]);
            Console.WriteLine("User: " + thisUser + ". Speaking to client on: " + client_address_port);
            String server_address_port = _clientComms.speakToClient(add[0], Convert.ToInt32(add[1]));
            add = server_address_port.Split(':');
           serverIP = add[0];
           serverPort = Convert.ToInt32(add[1]);
           //log. serverIP = "127.0.0.1";
            //log.serverPort = 12474;
            Console.WriteLine("Recieved server IP & Port. Can now start to retrieve data...");

            Console.WriteLine("Getting users & scores...");
            allUsers = _clientComms.speakToServer(thisUser, serverIP, serverPort);
          //log.  UserScore u = new UserScore();
         //log.   u.userJid="william.societies.local.macs.hw.ac.uk";
         //log.   u.score=0;
        //log. //log.   u.name="william";
        //log.    allUsers.Add(u);

            if (allUsers == null)
            {
               //log. log.Warn("Server returned null list of users, cannot continue game");
                _gameSession.Stage = GameStage.SetupError;
                return _gameSession;
            }

            Console.WriteLine("This user is: " + thisUser.ToString());

            Console.WriteLine("Next we want the users history...");

            Console.WriteLine("Now gettings questions & categories");
            Category[] categories = _clientComms.getCategories(serverIP, serverPort);
          
            if (categories == null)
            {
               //log. log.Warn("Server returned null list of categories, cannot continue game");
                _gameSession.Stage = GameStage.SetupError;
                return _gameSession;
            }

            Question[] questions = _clientComms.getQuestions(serverIP, serverPort);
            if (questions == null)
            {
              //log.  log.Warn("Server returned null list of questions, cannot continue game");
                _gameSession.Stage = GameStage.SetupError;
                return _gameSession;
            }

            //log.GET PREVIOUS QUESTIONS
            List<UserAnsweredQ> answeredQ= _clientComms.getAnsweredQuestions(serverIP, serverPort);
            if (answeredQ != null)
            {
               
                foreach (UserAnsweredQ ansQ in answeredQ)
                {
                    Console.WriteLine(ansQ.question.questionText);
                    answeredQuestions.Add(ansQ.question);
                }
                _gameSession.AllAnsweredQuestions.AddRange(answeredQuestions);

            }

            
            List<Question> availQ = questions.ToList();
            availQ.RemoveAll(q1 => answeredQuestions.Any(q2 => q2.questionID == q1.questionID));
            _gameSession.AllCategories.AddRange(categories);
            _gameSession.AllQuestions.AddRange(availQ);



            foreach (Category category in categories)
            {
                List<Question> categoryQuestions = (from q in questions
                                                    where q.categoryID == category.ID
                                                    select q).ToList();

                _gameSession.CategoryQuestionMap.Add(category, categoryQuestions);
            }

            //log. Current user
            _gameSession.MainUser = getUser();

            //log. TODO: Additional users
            //log. TODO: Initialize challenges

          //log.  QuestionsInGame = 6;
            QuestionsInGame = availQ.Count();
            _gameSession.Stage = GameStage.ReadyToStart;
            
           

           /* if (_comms == null)
                InitComms();

            log.Debug("Downloading game data from university CSS");
            log.Debug("Downloading game users...");
            List<User> users = _comms.ListUsers();
            if (users == null)
            {
                log.Warn("Server returned null list of users, cannot continue game");
                _gameSession.Stage = GameStage.SetupError;
                return _gameSession;
            }

            log.Debug("Downloading categories...");
            List<Category> categories = _comms.ListCategories();
            if (categories == null)
            {
                log.Warn("Server returned null list of categories, cannot continue game");
                _gameSession.Stage = GameStage.SetupError;
                return _gameSession;
            }

            log.Debug("Downloading questions...");
            List<Question> questions = _comms.ListQuestions();
            if (questions == null)
            {
                log.Warn("Server returned null list of questions, cannot continue game");
                _gameSession.Stage = GameStage.SetupError;
                return _gameSession;
            }

            _gameSession.AllCategories.AddRange(categories);
            _gameSession.AllQuestions.AddRange(questions);

            foreach (Category category in categories)
            {
                List<Question> categoryQuestions = (from q in questions
                                                    where q.CategoryID == category.ID
                                                    select q).ToList();

                _gameSession.CategoryQuestionMap.Add(category, categoryQuestions);
            }

            //log. Current user
            _gameSession.MainUser = _comms.GetMainUser();

            //log. TODO: Additional users
            //log. TODO: Initialize challenges

            QuestionsInGame = 6;

            _gameSession.Stage = GameStage.ReadyToStart;
            */
           //log. log.Debug("Finished setting up new game");
            return _gameSession; 
        }

  

        public static QuestionRound NextQuestion()
        {
          //log.  log.Debug("Next round...");

            QuestionRound round = new QuestionRound();

            round.Question = PickRandomQuestion(RequestedCategory, RequestedDifficulty);
              //log.  log.Debug("round.Question = " + round.Question);
                round.Category = RequestedCategory;
              //log.  log.Debug("round.Category = " + round.Category);
                round.RoundNumber = CurrentRoundNumber + 1;
            //log.    log.Debug("round.RoundNumber = " + round.RoundNumber);
                round.RequiredGrammar = PickRandomGrammar();
             //log.   log.Debug("round.RequiredGrammar = " + round.RequiredGrammar);
                round.RequiredPose = PickRandomPose();
            //log.    log.Debug("round.RequiredPose = " + round.RequiredPose);
                round.AnswerMethod = PickRandomAnswerMethod();
            //log.    log.Debug("round.AnswerMethod = " + round.AnswerMethod);

                _gameSession.CurrentRound = round;
                _gameSession.QuestionHistory.Enqueue(round);


                return round;
           
        }


   

        public static bool UserAnsweredQuestion(int index)
        {
            CurrentRound.UserAnswer = index;

            return (index == CurrentRound.Question.correctAnswer);
        }

        public static void EndGame()
        {
            //log.SEND TO SERVER - ONLY NEED TO SEND MY SCORE AS IT WILL BE THE ONLY USER CHANGED LOCALLY
            _clientComms.sendProgress(serverIP, serverPort, getUser(), roundAnsweredQ);
            _gameSession.CurrentRound = null;
            _gameSession.Stage = GameStage.Complete;
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

        private static Question PickRandomQuestion(Category category, QuestionDifficulty difficulty)
        {
            if (category == null)
                category = Category.All;

            //log. get a list of questions matching our difficulty
            List<Question> availableQuestions = new List<Question>(_gameSession.AllQuestions);

            //log. remove questions based on difficulty
            if (difficulty != QuestionDifficulty.Any)
            {
                availableQuestions.RemoveAll(q => q.difficulty != difficulty);
            }

            //log. remove questions not in our category
            if (category != Category.All)
            {
                availableQuestions.RemoveAll(q => q.categoryID != category.ID);
            }
            foreach (Question q in availableQuestions)
            {
                Console.WriteLine(q.questionText);
            }

            Console.WriteLine("All Questions...");
            foreach (Question q in availableQuestions)
            {
                Console.WriteLine(q.questionID);
            }
            Console.WriteLine("Answered Questions...");
            foreach (Question q in answeredQuestions)
            {
                Console.WriteLine(q.questionID);
            }
            Console.WriteLine(answeredQuestions);

            //log. now remove any questions already asked
          //log.  availableQuestions.RemoveAll(question => _gameSession.QuestionHistory.Select(round => round.Question).Contains(question));
          //log.  availableQuestions.RemoveAll(question => answeredQuestions.Contains(question));
           //log. List<String> list1;
        //log.    List<String> list2;
           //log. list1.RemoveAll(c => list2.Any(c2 => c2.Length == c.Length));//log. && c2.City == c.City));
           //log. availableQuestions = availableQuestions.RemoveAll(q => availableQuestions.All(q1 => (q.questionID.Equals(q1.questionID)));//log..questionID==q1.questionID));
            //log.(q1 => q.questionID.Equals(q1.questionID)));//log..Equals().ToList();
           
            
            Console.WriteLine("All Questions Now...");
            foreach (Question q in availableQuestions)
            {
                Console.WriteLine(q.questionID);
   
            }


            //log. no questions left in any category and any difficulty?
            if (availableQuestions.Count == 0 && category == Category.All && difficulty == QuestionDifficulty.Any)
            {
             //log.   log.Debug("No more questions");
                return null; //log. just give up
            }

            //log. no questions left in this category? try an easier difficulty
            if (availableQuestions.Count == 0 && category != Category.All && difficulty == QuestionDifficulty.Hard)
                return PickRandomQuestion(category, QuestionDifficulty.Medium);

            //log. no questions left in this category? try an easier difficulty
            if (availableQuestions.Count == 0 && category != Category.All && difficulty == QuestionDifficulty.Medium)
                return PickRandomQuestion(category, QuestionDifficulty.Easy);

            //log. no questions left in this category? try any category
            if (availableQuestions.Count == 0 && category != Category.All && difficulty == QuestionDifficulty.Easy)
                return PickRandomQuestion(Category.All, QuestionDifficulty.Easy);

            //log. no questions left in any category? try on medium
            if (availableQuestions.Count == 0 && category == Category.All && difficulty == QuestionDifficulty.Easy)
                return PickRandomQuestion(Category.All, QuestionDifficulty.Medium);

            //log. no questions left in any category? try on any difficulty
            if (availableQuestions.Count == 0 && category == Category.All && difficulty == QuestionDifficulty.Medium)
                return PickRandomQuestion(Category.All, QuestionDifficulty.Any);

            //log. there's questions available? return a random one
            return availableQuestions[random.Next(availableQuestions.Count)];
        }

        public static Boolean updateUserScore(int score)
        {
            int x = 0;
            Boolean found = false;
            while(x<allUsers.Count && !found)
            {
                if (allUsers[x].userJid.Equals(thisUser))
                {
                    allUsers[x].score = allUsers[x].score + score;
                    found = true;
                    Console.WriteLine("Users new score: " + allUsers[x].score);
                }
                x++;
            }
            return found;
        }

        public static List<UserScore> getAllUsers()
        {
            return allUsers;
        }

        public static UserScore getUser()
        {
            Boolean found = false;
            int x = 0;
            while (x < allUsers.Count && !found)
            {
                if (allUsers[x].userJid.Equals(thisUser))
                {
                    found = true;
                }
                else{
                x++;
                }
            }
            if (found)
            {
                return allUsers[x];
            }
            return null;
        }



        public static int QuestionsInGame { get; set; }

        public static int CurrentRoundNumber
        {
            get
            {
                return _gameSession.QuestionHistory.Count;
            }
        }

        public static int CorrectAnswerCount
        {
            get
            {
                int right = 0;
                foreach (QuestionRound round in _gameSession.QuestionHistory)
                {
                    if (round.UserAnswer == round.Question.correctAnswer)
                        right += 1;
                }
                return right;
            }
        }

        public static int Score
        {
            get
            {
               //log. int score = 0;
               /* foreach (QuestionRound round in _gameSession.QuestionHistory)
                {
                    if (round.UserAnswer == round.Question.correctAnswer)
                        score += round.Question.pointsIfCorrect;
                }*/
                return GameLogic.getUser().score;
            }
        }

        public static QuestionRound CurrentRound
        {
            get { return _gameSession.CurrentRound; }
        }

    }
}
