using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using MyTvUI.Agenda;

namespace MyTvUI.Agenda
{
    class Timetable
    {
        public enum Day { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY };
        Hashtable mondayList;
        Hashtable tuesdayList;
        Hashtable wednesdayList;
        Hashtable thursdayList;
        Hashtable fridayList;
        Hashtable saturdayList;
        Hashtable sundayList;

        public Timetable()
        {
            mondayList = new Hashtable();
            tuesdayList = new Hashtable();
        }
    }
}
