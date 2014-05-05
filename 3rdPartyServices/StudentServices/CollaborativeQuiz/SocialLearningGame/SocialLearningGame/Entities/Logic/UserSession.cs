using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SocialLearningGame.Entities;

namespace SocialLearningGame.Logic
{
    public class UserSession
    {
        public User user {get; set;}

        public Cis currentCis { get; set; }

        public GameStage currentPlayer { get; set; }

        public GameStage gameStage { get; set; }

        public QuestionRound quizRound { get; set; }

        public UserSession()
        {
        }


    }
}

