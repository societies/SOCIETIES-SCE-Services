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
        private static log4net.ILog log = log4net.LogManager.GetLogger(typeof(GameLogic));

        public static readonly AbstractPose[] PossiblePoses = { 
                                                                   new Pose1(), new Pose2(), 
                                                                   new Pose3(), new Pose4(), 
                                                                   new Pose5(), new Pose6(), 
                                                                   new Pose7(), new Pose8(), 
                                                                   new Pose9(), new Pose10() 
                                                               };


        //random number initialised used to choose next pose, question, etc
        private static readonly Random random = new Random();

        private static ServerComms _comms;

        private static GameSession _gameSession;
        public static Category RequestedCategory { get; set; }
        public static QuestionDifficulty RequestedDifficulty { get; set; }


        private static void InitComms()
        {
            log.Debug("Init comms");

            _comms = new ServerComms();
        }

        public static GameSession NewGame()
        {
            log.Debug("Starting new game");

            _gameSession = new GameSession();

            if (_comms == null)
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

            // Current user
            _gameSession.MainUser = _comms.GetMainUser();

            // TODO: Additional users
            // TODO: Initialize challenges

            QuestionsInGame = 6;

            _gameSession.Stage = GameStage.ReadyToStart;

            log.Debug("Finished setting up new game");
            return _gameSession;
        }

        public static QuestionRound NextQuestion()
        {
            log.Debug("Next round...");

            QuestionRound round = new QuestionRound();

            round.Question = PickRandomQuestion(RequestedCategory, RequestedDifficulty);
            log.Debug("round.Question = " + round.Question);
            round.Category = RequestedCategory;
            log.Debug("round.Category = " + round.Category);
            round.RoundNumber = CurrentRoundNumber + 1;
            log.Debug("round.RoundNumber = " + round.RoundNumber);
            round.RequiredGrammar = PickRandomGrammar();
            log.Debug("round.RequiredGrammar = " + round.RequiredGrammar);
            round.RequiredPose = PickRandomPose();
            log.Debug("round.RequiredPose = " + round.RequiredPose);
            round.AnswerMethod = PickRandomAnswerMethod();
            log.Debug("round.AnswerMethod = " + round.AnswerMethod);

            _gameSession.CurrentRound = round;
            _gameSession.QuestionHistory.Enqueue(round);

            return round;
        }

        public static bool UserAnsweredQuestion(int index)
        {
            CurrentRound.UserAnswer = index;

            return (index == CurrentRound.Question.CorrectAnswer);
        }

        public static void EndGame()
        {
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

            // get a list of questions matching our difficulty
            List<Question> availableQuestions = new List<Question>(_gameSession.AllQuestions);

            // remove questions based on difficulty
            if (difficulty != QuestionDifficulty.Any)
            {
                availableQuestions.RemoveAll(q => q.Difficulty != difficulty);
            }

            // remove questions not in our category
            if (category != Category.All)
            {
                availableQuestions.RemoveAll(q => q.CategoryID != category.ID);
            }

            // now remove any questions already asked
            availableQuestions.RemoveAll(question => _gameSession.QuestionHistory.Select(round => round.Question).Contains(question));

            // no questions left in any category and any difficulty?
            if (availableQuestions.Count == 0 && category == Category.All && difficulty == QuestionDifficulty.Any)
            {
                log.Debug("No more questions");
                return null; // just give up
            }

            // no questions left in this category? try an easier difficulty
            if (availableQuestions.Count == 0 && category != Category.All && difficulty == QuestionDifficulty.Hard)
                return PickRandomQuestion(category, QuestionDifficulty.Medium);

            // no questions left in this category? try an easier difficulty
            if (availableQuestions.Count == 0 && category != Category.All && difficulty == QuestionDifficulty.Medium)
                return PickRandomQuestion(category, QuestionDifficulty.Easy);

            // no questions left in this category? try any category
            if (availableQuestions.Count == 0 && category != Category.All && difficulty == QuestionDifficulty.Easy)
                return PickRandomQuestion(Category.All, QuestionDifficulty.Easy);

            // no questions left in any category? try on medium
            if (availableQuestions.Count == 0 && category == Category.All && difficulty == QuestionDifficulty.Easy)
                return PickRandomQuestion(Category.All, QuestionDifficulty.Medium);

            // no questions left in any category? try on any difficulty
            if (availableQuestions.Count == 0 && category == Category.All && difficulty == QuestionDifficulty.Medium)
                return PickRandomQuestion(Category.All, QuestionDifficulty.Any);

            // there's questions available? return a random one
            return availableQuestions[random.Next(availableQuestions.Count)];
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
                    if (round.UserAnswer == round.Question.CorrectAnswer)
                        right += 1;
                }
                return right;
            }
        }

        public static int Score
        {
            get
            {
                int score = 0;
                foreach (QuestionRound round in _gameSession.QuestionHistory)
                {
                    if (round.UserAnswer == round.Question.CorrectAnswer)
                        score += round.Question.PointsIfCorrect;
                }
                return score;
            }
        }

        public static QuestionRound CurrentRound
        {
            get { return _gameSession.CurrentRound; }
        }

    }
}
