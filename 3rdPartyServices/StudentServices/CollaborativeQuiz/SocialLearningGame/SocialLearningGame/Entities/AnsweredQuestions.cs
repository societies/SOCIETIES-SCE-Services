using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SocialLearningGame.Entities
{
   public class AnsweredQuestions
    {
       public int answeredQID { get; set; }
        public String userID { get; set; }
        public String cisName { get; set; }
        public int questionID { get; set; }
        public Boolean answeredCorrect { get; set; }
    }
}
