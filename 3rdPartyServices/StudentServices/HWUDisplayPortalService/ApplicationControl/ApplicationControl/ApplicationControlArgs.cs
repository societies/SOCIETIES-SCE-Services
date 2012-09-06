using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ApplicationControl
{
    public class ApplicationControlArgs : System.EventArgs
    {
        private String exeName;

        public String AppServiceName
        {
            get
            {
                return this.exeName;
            }
            set
            {
                this.exeName = value;
            }
        }
        public ApplicationControlArgs(String exeName)
        {
            this.exeName = exeName;
        }
    }
}
