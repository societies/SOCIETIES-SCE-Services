using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SocialLearningGame.Entities
{
    public class DataStore
    {
        private static List<Question> questions { get; set; }
        private static List<Category> categories { get; set; }
        private static List<UserScore> userScores { get; set; }
        private static List<UserAnsweredQ> userAnsweredQ { get; set; }

        public DataStore()
        {
        }


    }
}
