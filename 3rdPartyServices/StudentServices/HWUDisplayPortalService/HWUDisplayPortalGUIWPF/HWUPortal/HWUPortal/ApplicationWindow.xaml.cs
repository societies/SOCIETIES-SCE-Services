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
using ApplicationControl;
namespace HWUPortal
{
    /// <summary>
    /// Interaction logic for ApplicationWindow.xaml
    /// </summary>
    public partial class ApplicationWindow : Window
    {
        System.Windows.Forms.FlowLayoutPanel flpPanel;
        ApplicationControl.ApplicationControl appControl;
        private ServiceInfo currentService;
        private MainWindow mainWindow;

        public ApplicationWindow(MainWindow mainWindow, ServiceInfo sInfo)
        {
            InitializeComponent();
            this.mainWindow = mainWindow;
            this.currentService = sInfo;
            flpPanel = this.wfhDate.Child as System.Windows.Forms.FlowLayoutPanel;
            appControl = new ApplicationControl.ApplicationControl();
            appControl.appExit += new ApplicationControl.ApplicationControl.ApplicationExitedHandler(onExeDestroyed);

            appControl.Width = 1000;
            appControl.Height = 600;
            flpPanel.Controls.Add(appControl);

        }

        public void startExe(EventArgs e)
        {
            appControl.ExeName = currentService.serviceExe;
            appControl.LoadExe(e);
        }

        private void onExeDestroyed(object sender, ApplicationControlArgs args)
        {
            if (!this.currentService.serviceName.Equals(string.Empty))
            {
                this.currentService = new ServiceInfo();
                mainWindow.Focus();
                this.Close();
                
            }


        }

        private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            appControl.DestroyExe(e);
            //appControl.Dispose();

        }
    }
}
