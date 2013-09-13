using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SocialLearningGame.Entities
{
    class UserAnsweredQ
    {
        private String userJid { get; set; }
        private Question question { get; set; }
        private Boolean answeredCorrect { get; set; }
    }
}
