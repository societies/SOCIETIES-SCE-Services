using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using System.Collections;

namespace HWUPortal
{
    /// <summary>
    /// Interaction logic for TextNotificationsWindow.xaml
    /// </summary>
    public partial class TxtNotificationsWindow : UserControl
    {
        protected static log4net.ILog log = log4net.LogManager.GetLogger(typeof(TxtNotificationsWindow));
        
        public TxtNotificationsWindow()
        {
            InitializeComponent();
            this.addNewNotification("Display Portal", "There are no notifications to show");
        }

        internal delegate void addNewNotificatioDelegate(String serviceName, String text);
        internal void addNewNotification(String serviceName, string text)
        {

            if (this.Dispatcher.CheckAccess())
            {
                if (this.activityPanel.Children.Count == 1)
                {
                    IEnumerator e = this.activityPanel.Children.GetEnumerator();
                    e.Reset();
                    e.MoveNext();
                    NotificationPanel p = (NotificationPanel)e.Current;
                    if (p.getText().IndexOf("There are no notifications to show") > -1)
                    {
                        this.activityPanel.Children.RemoveAt(0);
                    }
                }
                NotificationPanel panel = new NotificationPanel(this, serviceName, text);
                int counter = activityPanel.Children.Count;
                if (counter == 5)
                {
                                Console.WriteLine(DateTime.Now + "\t" +"Removing older panel");
                    activityPanel.Children.RemoveAt(0);
                }
                else //make room!
                {
                                Console.WriteLine(DateTime.Now + "\t" +"height of notification panel is " + panel.Height);

                    //this.Height = this.Height + 170;

                    if (this.activityPanel.Children.Count != 0 && this.activityPanel.Children.Count % 2 == 0)
                    {
                        this.Height = this.Height + 170;
                    }
                }

                            Console.WriteLine(DateTime.Now + "\t" +"Height of text notification window is " + this.ActualHeight);
                activityPanel.Children.Add(panel);
                this.UpdateLayout();
                            Console.WriteLine(DateTime.Now + "\t" +"Number of notification panels: " + activityPanel.Children.Count);
            }
            else
            {
                this.Dispatcher.Invoke(new addNewNotificatioDelegate(addNewNotification), serviceName, text);
            }
            //if (this.InvokeRequired)
            //{
            //    Invoke(new addNewNotificatioDelegate(this.addNewNotification), serviceName, text);
            //}
            //else
            //{
            //    int counter = activityPanel.Controls.Count;
            //    if (counter == 5)
            //    {
            //                    Console.WriteLine(DateTime.Now + "\t" +"Removing older panel");
            //        activityPanel.Controls.RemoveAt(0);
            //    }
            //    NotificationPanel panel = new NotificationPanel(serviceName, text);

            //    this.activityPanel.Controls.Add(panel);


            //}
        }

        public void removeMe(NotificationPanel childPanel)
        {

            this.activityPanel.Children.Remove(childPanel);

            if (this.activityPanel.Children.Count == 0)
            {
                this.addNewNotification("Display Portal", "There are no notifications to show");
            }
            if (this.activityPanel.Children.Count % 2 == 0)
            {
                this.Height = this.Height - 170;
            }

        }
    }
}
