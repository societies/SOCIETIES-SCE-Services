using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;

namespace MyTvUI
{
    class ActivityFeedManager
    {
        ArrayList activities;

        public ActivityFeedManager(ArrayList activities)
        {
            this.activities = activities;

            //testing only
            //addActivities();
        }

        public void handleNewActivity(String newActivity)
        {
            activities.Add(newActivity);
        }


        //Testing only
        private void addActivities()
        {
            handleNewActivity("James has shared his PersonalCalendar service");
            handleNewActivity("Laura has joined the HWU Tennis CIS");
        }
    }
}
