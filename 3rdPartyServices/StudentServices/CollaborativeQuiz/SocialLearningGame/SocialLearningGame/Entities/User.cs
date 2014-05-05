using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SocialLearningGame.Entities
{
   public class User
    {

        public String userJid { get; set; }
        public List<String> cisList { get; set; }
        public int score { get; set; }
    }
}
