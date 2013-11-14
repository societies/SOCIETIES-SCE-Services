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
        public UserScore currentUser { get; set; }
        public Groups currentGroup { get; set; }
        public List<Question> allQuestions { get; set; }
        public List<Category> allCategories { get; set; }
        public List<UserAnsweredQ> userAnsweredQuestions { get; set; }
        public List<UserAnsweredQ> groupAnsweredQuestions { get; set; }
        public List<Question> availableUserQuestions { get; set; }
        public List<Question> availableGroupQuestions { get; set; }
        public List<UserScore> allUsers { get; set; }
        public List<String> allGroupPlayers { get; set; }
        public List<Groups> allGroups { get; set; }
        public List<PendingJoins> allNotifications { get; set; }
        public List<String> userInterests { get; set; }
        public GameStage gameStage { get; set; }
        public GameStage player { get; set; }
        public List<Question> questionStack { get; set; }
        public QuestionRound questionRound { get; set; }
        public UserAnsweredQ answeredQuestion { get; set; }
        public List<String> invitedUsers { get; set; }

        public UserSession()
        {
        }


    }
}

