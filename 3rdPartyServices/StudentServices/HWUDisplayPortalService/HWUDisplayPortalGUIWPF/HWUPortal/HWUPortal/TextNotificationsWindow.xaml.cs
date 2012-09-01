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

namespace HWUPortal
{
    /// <summary>
    /// Interaction logic for TextNotificationsWindow.xaml
    /// </summary>
    public partial class TextNotificationsWindow : Window
    {
        public TextNotificationsWindow()
        {
            InitializeComponent();
        }

        internal delegate void addNewNotificatioDelegate(String serviceName, String text);
        internal void addNewNotification(String serviceName, string text)
        {
            //if (this.InvokeRequired)
            //{
            //    Invoke(new addNewNotificatioDelegate(this.addNewNotification), serviceName, text);
            //}
            //else
            //{
            //    int counter = activityPanel.Controls.Count;
            //    if (counter == 5)
            //    {
            //        Console.WriteLine("Removing older panel");
            //        activityPanel.Controls.RemoveAt(0);
            //    }
            //    NotificationPanel panel = new NotificationPanel(serviceName, text);

            //    this.activityPanel.Controls.Add(panel);


            //}
        }
    }
}
