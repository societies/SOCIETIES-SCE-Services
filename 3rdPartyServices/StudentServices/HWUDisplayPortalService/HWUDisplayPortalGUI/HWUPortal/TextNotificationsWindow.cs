using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace HWUPortal
{
    public partial class TextNotificationsWindow : Form
    {
        public TextNotificationsWindow()
        {
            InitializeComponent();
        }
        
        internal delegate void addNewNotificatioDelegate(String serviceName, String text);
        internal void addNewNotification(String serviceName, string text)
        {
            if (this.InvokeRequired)
            {
                Invoke(new addNewNotificatioDelegate(this.addNewNotification), serviceName, text);
            }
            else
            {
                int counter = activityPanel.Controls.Count;
                if (counter == 5)
                {
                    Console.WriteLine("Removing older panel");
                    activityPanel.Controls.RemoveAt(0);
                }
                NotificationPanel panel = new NotificationPanel(serviceName, text);

                this.activityPanel.Controls.Add(panel);
                
                
            }
        }

        private void TextNotificationsWindow_Load(object sender, EventArgs e)
        {

        }


    }
}
