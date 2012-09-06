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
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace HWUPortal
{
    /// <summary>
    /// Interaction logic for NotificationPanel.xaml
    /// </summary>
    public partial class NotificationPanel : UserControl
    {
        private string serviceName;
        private string text;
        private TxtNotificationsWindow parentPanel;

        
        public NotificationPanel()
        {
            InitializeComponent();
        }

        public NotificationPanel(TxtNotificationsWindow parentPanel, string serviceName, string text)
        {
            this.InitializeComponent();
            // TODO: Complete member initialization
            this.parentPanel = parentPanel;
            this.serviceName = serviceName;
            this.text = text;
            this.txtNotifications.Text = text;
            this.lblNotifications.Content = serviceName;
        }

        private void button1_Click(object sender, RoutedEventArgs e)
        {
            this.parentPanel.removeMe(this);
        }

        public string getText()
        {
            return this.text;
        }

        public string getServiceName()
        {
            return this.serviceName;
        }
    }
}
