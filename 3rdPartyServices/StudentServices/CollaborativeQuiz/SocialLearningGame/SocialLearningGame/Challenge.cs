using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SocialLearningGame
{
    public class Challenge
    {
        public int id { get; set; }
        public Student challenger { get; set; }
        public Student challenged { get; set; }
        public string category { get; set; }
        public int challengerScore { get; set; }
        public int challengedScore { get; set; }
    }
}
