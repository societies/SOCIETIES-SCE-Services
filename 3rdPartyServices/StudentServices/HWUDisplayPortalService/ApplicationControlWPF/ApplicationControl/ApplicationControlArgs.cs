using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ApplicationControl
{
    public class ApplicationControlArgs : System.EventArgs
    {
        private String exeName;
        private Boolean gracefulExit;

        public String AppServiceName
        {
            get
            {
                return this.exeName;
            }
        }

        public bool GracefullyExited
        {
            get
            {
                return this.gracefulExit;
            }
        }
        public ApplicationControlArgs(String exeName, bool gracefulExit)
        {
            this.exeName = exeName;
            this.gracefulExit = gracefulExit;
        }
    }
}
