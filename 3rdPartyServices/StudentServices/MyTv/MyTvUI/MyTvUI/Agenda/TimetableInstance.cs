using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace MyTvUI.Agenda
{
    class TimetableInstance
    {
        String description;
        String location;

        public TimetableInstance(String description, String location)
        {
            this.description = description;
            this.location = location;
        }

        public String getDescription()
        {
            return this.description;
        }

        public String getLocation()
        {
            return this.location;
        }
    }
}
