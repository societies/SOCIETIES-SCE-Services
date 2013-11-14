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
    public partial class ApplicationForm : Form
    {
        protected static log4net.ILog log = log4net.LogManager.GetLogger(typeof(ApplicationForm));

        MainWindow mainWindow;
        private Boolean isServiceRunning = false;

        public Boolean isServiceRunningOnThisForm()
        {
            return this.isServiceRunning;
        }
        public ApplicationForm(MainWindow mainWindow)
        {
            this.mainWindow = mainWindow;
            InitializeComponent();

            appControlPanel.appExit += new ApplicationControlPanel.ApplicationExitedHandler(onExeDestroyed);

        }


        private void onExeDestroyed(object sender, ApplicationControlArgs args)
        {
            this.mainWindow.onExeDestroyed(sender, args);
            this.Hide();
            this.isServiceRunning = false;
        }



        public string ExeName
        {
            get { return this.appControlPanel.ExeName; }
            set { this.appControlPanel.ExeName = value; }
        }


        public void LoadExe(EventArgs e)
        {

            this.appControlPanel.LoadExe(e);
            this.isServiceRunning = true;
        }

        internal void DestroyExe(System.Windows.RoutedEventArgs e)
        {
            this.appControlPanel.DestroyExe(e);
        }

        private void appControlPanel_Leave(object sender, EventArgs e)
        {
            this.Focus();
             Console.WriteLine(DateTime.Now + "\t" +this.AccessibleName + " got focus");
        }
    }
}
