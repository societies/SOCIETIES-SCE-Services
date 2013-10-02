using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SocialLearningGame.Entities
{
    public class UserAnsweredQ
    {
        public int id { get; set; }
        public String userJid { get; set; }
        public int questionID { get; set; }
        public Boolean answeredCorrect { get; set; }
    }
}
