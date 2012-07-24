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
    public partial class NotificationPanel : Panel
    {
        
        public NotificationPanel(String labelText, String text)
        {
            InitializeComponent();
            
            this.txtNofications.Text = text;
            this.lblNotifications.Text = labelText;
        }
        public NotificationPanel()
        {
            InitializeComponent();
            

        }

        
    }
}
