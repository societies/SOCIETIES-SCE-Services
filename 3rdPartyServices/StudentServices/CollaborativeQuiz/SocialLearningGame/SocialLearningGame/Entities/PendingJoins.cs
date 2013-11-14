using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SocialLearningGame.Entities
{
    public class PendingJoins
    {
        public int joinID { get; set; }
        public String toUser { get; set; }
        public String fromUser { get; set; }
        public String groupName { get; set; }
    }
}
