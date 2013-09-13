/* 
 * Copyright (coffee) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

using System;
using System.Threading;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using Microsoft.Kinect;
using Microsoft.Kinect.Toolkit;
using Microsoft.Kinect.Toolkit.Controls;
using SocialLearningGame.Logic;
using SocialLearningGame.Pages;

namespace SocialLearningGame
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        //protected static log4net.ILog log = log4net.LogManager.GetLogger(typeof(MainWindow));
        private static MainWindow _windowInstance;
        public static MainWindow Instance { get { return _windowInstance; } }

        public KinectSensorChooser SensorChooser { get; private set; }

      //  private readonly CommsManager commsManager;

        public MainWindow()
            : base()
        {
            
         //   log4net.Config.XmlConfigurator.Configure();
          //  //log.Debug("Init components");
            this.InitializeComponent();
          //  log4net.Config.XmlConfigurator.Configure(new System.IO.FileInfo("./Resources/log4net.config.xml"));
            //log.Info("Logging configured");
           // //log.Debug("Init Kinect sensor");
            // initialize the sensor chooser and UI
            this.SensorChooser = new KinectSensorChooser();
            this.SensorChooser.KinectChanged += SensorChooserOnKinectChanged;
            this.sensorChooserUi.KinectSensorChooser = this.SensorChooser;
#if DEBUG
            ////log.Warn("Sensor auto-start is disabled during debug");
            //this.RightHand.MouseUp += new System.Windows.Input.MouseButtonEventHandler(sensorChooserUi_MouseDoubleClick);
            this.SensorChooser.Start();
#else
            this.SensorChooser.Start();
#endif

            // Bind the sensor chooser's current sensor to the KinectRegion
            Binding regionSensorBinding = new Binding("Kinect") { Source = this.SensorChooser };
            BindingOperations.SetBinding(this.kinectRegion, KinectRegion.KinectSensorProperty, regionSensorBinding);
       //     //log.Debug("Kinect initialized");

            Page p = LoadingPage.Instance; // Hack to get this to init on the right thread;
            Page q = CommsError.Instance; // Hack to get this to init on the right thread;
            Page r = HomePage.Instance;// Hack to get this to init on the right thread;

            _windowInstance = this;

            /*String HOST_URL = "puma-paddy-3";
            String USERNAME = "osgims";
            String PASSWORD = "osgims";
            commsManager = new CommsManager(HOST_URL, USERNAME, PASSWORD);

            String nodeName = "myNode1";

            commsManager.RegisterListener(nodeName);*/

            this.Show();
        }

#if DEBUG
        public void sensorChooserUi_MouseDoubleClick(object sender, System.Windows.Input.MouseButtonEventArgs e)
        {
            this.SensorChooser.Start();
        }
#endif

        #region " Window events "

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            Console.WriteLine("I am loaded...");
            // load data (on a different thread)
            Thread loaderThread = new Thread(new ThreadStart(LoadingThread));
            loaderThread.Name = "Data loader thread";
            loaderThread.Start();
        }

        private void WindowClosing(object sent, System.ComponentModel.CancelEventArgs e)
        {
            if (this.SensorChooser != null)
            {
               // //log.Debug("Stopping Kinect");
                this.SensorChooser.Stop();

                if (this.SensorChooser.Kinect != null)
                    UnbindSensor(this.SensorChooser.Kinect);
            }
        }

        #endregion

        #region " Kinect Events "

        /// <summary>
        /// Called when the KinectSensorChooser gets a new sensor
        /// </summary>
        /// <param name="sender">sender of the event</param>
        /// <param name="args">event arguments</param>
        private void SensorChooserOnKinectChanged(object sender, KinectChangedEventArgs args)
        {
            //log.Debug("Kinect sensor changed");

            if (args.OldSensor != null)
            {
                //log.Debug("Unbinding old sensor");

                try
                {
                    KinectSensor oldSensor = args.OldSensor;

                    UnbindSensor(oldSensor);

                    //log.Debug("Completed unbinding old sensor");
                }
                catch (InvalidOperationException ex)
                {
                    // KinectSensor might enter an invalid state while enabling/disabling streams or stream features.
                    // E.g.: sensor might be abruptly unplugged.
                    //log.Warn("Error unbinding old sensor", ex);
                }
            }

            if (args.NewSensor != null)
            {
                //log.Debug("Binding new sensor");

                try
                {
                    KinectSensor newSensor = args.NewSensor;

                    BindSensor(newSensor);

                    //log.Debug("Completed binding new sensor");
                }
                catch (InvalidOperationException ex)
                {
                    // KinectSensor might enter an invalid state while enabling/disabling streams or stream features.
                    // E.g.: sensor might be abruptly unplugged.
                    //log.Warn("Error binding new sensor", ex);
                }

            }
        }

        private static void BindSensor(KinectSensor sensor)
        {
            if (sensor == null)
                return;

            //log.Debug("BindSensor()");
            // Sensor
            sensor.Start();

            sensor.DepthStream.Enable(DepthImageFormat.Resolution640x480Fps30);

            try
            {
#if DEBUG
                // near mode for debug
                sensor.DepthStream.Range = DepthRange.Near;
#endif

                // NB the skeleton stream is used to track the silhouette of the player on all pages
                sensor.SkeletonStream.EnableTrackingInNearRange = true;
                //sensor.SkeletonStream.TrackingMode = SkeletonTrackingMode.Seated;
                // Turn on the skeleton stream to receive skeleton frames
                sensor.SkeletonStream.Enable();
            }
            catch (InvalidOperationException ex)
            {
                // Non Kinect for Windows devices do not support Near mode, so reset back to default mode.
                sensor.DepthStream.Range = DepthRange.Default;
                sensor.SkeletonStream.EnableTrackingInNearRange = false;
                //log.Warn("Error Setting depth range to near mode", ex);
            }
        }

        private static void UnbindSensor(KinectSensor sensor)
        {
            if (sensor == null)
                return;

            //log.Debug("UnbindSensor()");

            sensor.DepthStream.Range = DepthRange.Default;
            sensor.SkeletonStream.EnableTrackingInNearRange = false;
            sensor.DepthStream.Disable();
            sensor.SkeletonStream.Disable();
            sensor.AudioSource.Stop();
            sensor.Stop();
        }

        #endregion

        #region " Page switching "
        private delegate void SwitchPageDelegate(Page newPage);
        public static void SwitchPage(Page newPage)
        {
            Console.WriteLine("Switch page: " + newPage.GetType().ToString());

            if (newPage == null)
            {
             //   //log.Error("SwitchPage - Page cannot be null");
                return;
            }

            if (!_windowInstance.Dispatcher.CheckAccess())
            {
                ////log.Debug("Cross threading call required");
                _windowInstance.Dispatcher.Invoke(new SwitchPageDelegate(SwitchPage), newPage);
                return;
            }

            if (newPage.GetType() == typeof(HomePage)
                || newPage.GetType() == typeof(LoadingPage)
                || newPage.GetType() == typeof(CommsError))
            {
                _windowInstance.menuButton.Visibility = Visibility.Hidden;
            }
            else
            {
                _windowInstance.menuButton.Visibility = Visibility.Visible;
            }

        //    //log.Debug("Switching to page " + newPage.GetType().Name);
            _windowInstance.title.Text = newPage.Title;
            _windowInstance._mainFrame.NavigationService.Navigate(newPage);
          ////log.Debug("Switched");
        }


        #endregion

        private void LoadingThread()
        {
            SwitchPage(LoadingPage.Instance);

            GameSession session = GameLogic.NewGame(null);

            if (session.Stage == GameStage.SetupError)
            {
                SwitchPage(CommsError.Instance);
            }
            //else if (student.First == 1)
            //{
            //    _mainFrame.NavigationService.Navigate(new Pages.Instruction());
            //}
            else
            {
                SwitchPage(HomePage.Instance);
            }
        }

        private void exitButtonClick(object sender, RoutedEventArgs e)
        {
            if (SensorChooser != null && SensorChooser.Kinect != null)
                UnbindSensor(SensorChooser.Kinect);

            Environment.Exit(0x00);
        }

        private void menuButtonClick(object sender, RoutedEventArgs e)
        {
            SwitchPage(HomePage.Instance);
        }

    }
}
