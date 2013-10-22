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
            addActivities();
        }

        public void handleNewActivity(String newActivity)
        {
            activities.Add(newActivity);
        }

        public void addActivities()
        {
            handleNewActivity("My Updates coming soon!");
           // handleNewActivity("Laura has joined the HWU Tennis CIS");
        }
    }
}
