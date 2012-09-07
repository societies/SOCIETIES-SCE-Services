// (c) Copyright Microsoft Corporation.
// This source is subject to the Microsoft Public License (Ms-PL).
// Please see http://go.microsoft.com/fwlink/?LinkID=131993 for details.
// All other rights reserved.

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Media;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using Microsoft.Kinect;
using Coding4Fun.Kinect.Wpf;
using Coding4Fun.Kinect;
using Coding4Fun.Kinect.Wpf.Controls;
using Microsoft.Samples.Kinect.WpfViewers;
using System.IO;
using System.Threading;


namespace SocialLearningGame.Pages
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class HomePage : Page
    {
        #region variables
        //variables used to detect hand over buttons
        private static double _topBoundary;
        private static double _bottomBoundary;
        private static double _leftBoundary;
        private static double _rightBoundary;
        private static double _itemLeft;
        private static double _itemTop;

        //other variables used
        bool closing = false;
        const int skeletonCount = 6;
        Skeleton[] allSkeletons = new Skeleton[skeletonCount];

        Thread thread1;
        Student friend = new Student();
        String category;
        int score;
        Challenge challenge = new Challenge();
        Challenge currentChallenge = new Challenge();
        #endregion variables

        #region page
        public HomePage()
        {
            InitializeComponent();

            name.Text = MainWindow.student.name;

            playButton.Click += new RoutedEventHandler(navigatePlay);
            scoreboardButton.Click += new RoutedEventHandler(navigateScoreboard);
            categoriesButton.Click += new RoutedEventHandler(navigateCategories);
            challengeButton.Click += new RoutedEventHandler(navigateChallenge);
            quitButton.Click += new RoutedEventHandler(navigateCloseWindow);
            Thread delete = new Thread(new ThreadStart(deleteChallenge));
            if (MainWindow.challengesFrom != null)
            {
                Console.WriteLine("challenges from user exist");

                if (MainWindow.challengesFrom.ElementAt(0).challengerScore > MainWindow.challengesFrom.ElementAt(0).challengedScore)
                {
                    challenge = MainWindow.challengesFrom.ElementAt(0);
                    statusBarText.Text = "You won the challenge against " + challenge.challenged.name + "! Score: " + challenge.challengerScore + "-" + challenge.challengedScore;
                    delete.Start();
                }
                else if (MainWindow.challengesFrom.ElementAt(0).challengedScore > MainWindow.challengesFrom.ElementAt(0).challengerScore)
                {
                    challenge = MainWindow.challengesFrom.ElementAt(0);
                    statusBarText.Text = challenge.challenger.name + " won the challenge. Score: " + challenge.challengerScore + "-" + challenge.challengedScore;
                    delete.Start();
                }
                else if (MainWindow.challengesFrom.ElementAt(0).challengedScore == MainWindow.challengesFrom.ElementAt(0).challengerScore)
                {
                    challenge = MainWindow.challengesFrom.ElementAt(0);
                    statusBarText.Text = "You and " + challenge.challenger.name + " drew! Score: " + challenge.challengerScore + "-" + challenge.challengedScore;
                    delete.Start();
                }
            }
        }

        public HomePage(String message)
        {
            InitializeComponent();

            name.Text = MainWindow.student.name;

            playButton.Click += new RoutedEventHandler(navigatePlay);
            scoreboardButton.Click += new RoutedEventHandler(navigateScoreboard);
            categoriesButton.Click += new RoutedEventHandler(navigateCategories);
            challengeButton.Click += new RoutedEventHandler(navigateChallenge);
            quitButton.Click += new RoutedEventHandler(navigateCloseWindow);
            statusBarText.Text = message;
        }

        //pass in a friend and category to challenge, and the score that the challenger got
        public HomePage(Student friend, String category, int score)
        {
            InitializeComponent();

            name.Text = MainWindow.student.name;

            playButton.Click += new RoutedEventHandler(navigatePlay);
            scoreboardButton.Click += new RoutedEventHandler(navigateScoreboard);
            categoriesButton.Click += new RoutedEventHandler(navigateCategories);
            challengeButton.Click += new RoutedEventHandler(navigateChallenge);
            quitButton.Click += new RoutedEventHandler(navigateCloseWindow);
            thread1 = new Thread(new ThreadStart(sendChallenge));
            this.friend = friend;
            this.category = category;
            this.score = score;
            thread1.Start();
            try
            {
                statusBarText.Text = category + " challenge sent to " + friend.name;
            }
            catch (NullReferenceException e)
            {
                Console.WriteLine(e.Message);
                Console.WriteLine("Error updating the status bar");
            }
        }

        public HomePage(Challenge challenge, int score)
        {
            InitializeComponent();

            name.Text = MainWindow.student.name;

            playButton.Click += new RoutedEventHandler(navigatePlay);
            scoreboardButton.Click += new RoutedEventHandler(navigateScoreboard);
            categoriesButton.Click += new RoutedEventHandler(navigateCategories);
            challengeButton.Click += new RoutedEventHandler(navigateChallenge);
            quitButton.Click += new RoutedEventHandler(navigateCloseWindow);
            thread1 = new Thread(new ThreadStart(updateChallenge));

            challenge.challengedScore = score;
            currentChallenge = challenge;
            thread1.Start();
            
            if (challenge.challengerScore > challenge.challengedScore)
            {
                statusBarText.Text = challenge.challenger.name +" won the challenge. Score: "+ challenge.challengerScore+"-"+challenge.challengedScore;
            }
            else if (challenge.challengedScore > challenge.challengerScore)
            {
                statusBarText.Text = "You won the challenge! Score: " + challenge.challengerScore + "-" + challenge.challengedScore;
            }
            else
            {
                statusBarText.Text = "You and " + challenge.challenger.name + " drew! Score: " + challenge.challengerScore + "-" + challenge.challengedScore;
            }
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            kinectSensorChooser1.KinectSensorChanged += new DependencyPropertyChangedEventHandler(kinectSensorChooser1_KinectSensorChanged);

           
        }

        private void Window_Closing(object sender, RoutedEventArgs e)
        {
            closing = true;
            StopKinect(kinectSensorChooser1.Kinect);
        }
        //method to send a challenge, done on a separate thread, so inside just calls the method from the MainWindow 
        private void sendChallenge()
        {
            MainWindow.sendChallenge(friend, category, score);
        }

        private void updateChallenge()
        {
            MainWindow.updateChallenge(currentChallenge);
        }

        private void deleteChallenge()
        {
            MainWindow.deleteChallenge(challenge);
        }
        #endregion page

        #region buttons

        public void navigatePlay(object Sender, RoutedEventArgs e)
        {
            this.NavigationService.Navigate(new PlayPage());
        }

        public void navigateScoreboard(object Sender, RoutedEventArgs e)
        {
            this.NavigationService.Navigate(new ScoreboardPage());
        }

        public void navigateChallenge(object Sender, RoutedEventArgs e)
        {
            this.NavigationService.Navigate(new ChallengePage());
        }

        public void navigateCategories(object Sender, RoutedEventArgs e)
        {
            this.NavigationService.Navigate(new CategoriesPage());
        }

        public void navigateCloseWindow(object Sender, RoutedEventArgs e)
        {
            closing = true;
            StopKinect(kinectSensorChooser1.Kinect);
            try
            {
                //if the application was opened by another window close the current window not the other application
                if (Application.Current.Windows.Count > 1)
                {
                    for (int i = 0; i < Application.Current.Windows.Count ; i++)
                    {
                        if(Application.Current.Windows[i].GetType().ToString().Equals("SocialLearningGame.MainWindow"))
                            Application.Current.Windows[i].Close();
                    }
                }
                //otherwise close the main window
                else
                    Application.Current.MainWindow.Close();
            }
            catch (InvalidOperationException e2)
            {
                Console.WriteLine("Exception: " + e2);
            }
        }
        private static void CheckButton(HoverButton button, Ellipse thumbStick)
        {
            try
            {
                if (IsItemMidpointInContainer(button, thumbStick))
                {
                    button.Hovering();
                }
                else
                {
                    button.Release();
                }
            }
            catch (System.InvalidOperationException e)
            {
                //Console.WriteLine("Error: "+ e.Message);
            }
        }

        public static bool IsItemMidpointInContainer(FrameworkElement container, FrameworkElement target)
        {
            FindValues(container, target);

            if (_itemTop < _topBoundary || _bottomBoundary < _itemTop)
            {
                //Midpoint of target is outside of top or bottom
                return false;
            }

            if (_itemLeft < _leftBoundary || _rightBoundary < _itemLeft)
            {
                //Midpoint of target is outside of left or right
                return false;
            }

            return true;
        }

        private static void FindValues(FrameworkElement container, FrameworkElement target)
        {
            var containerTopLeft = container.PointToScreen(new Point());
            var itemTopLeft = target.PointToScreen(new Point());

            _topBoundary = containerTopLeft.Y;
            _bottomBoundary = _topBoundary + container.ActualHeight;
            _leftBoundary = containerTopLeft.X;
            _rightBoundary = _leftBoundary + container.ActualWidth;

            //use midpoint of item (width or height divided by 2)
            _itemLeft = itemTopLeft.X + (target.ActualWidth / 2);
            _itemTop = itemTopLeft.Y + (target.ActualHeight / 2);
        }
        #endregion buttons


        #region gesture processing
        Skeleton GetFirstSkeleton(AllFramesReadyEventArgs e)
        {
            using (SkeletonFrame skeletonFrameData = e.OpenSkeletonFrame())
            {
                if (skeletonFrameData == null)
                {
                    return null;
                }

                skeletonFrameData.CopySkeletonDataTo(allSkeletons);

                //get the first tracked skeleton
                Skeleton first = (from s in allSkeletons
                                  where s.TrackingState == SkeletonTrackingState.Tracked
                                  select s).FirstOrDefault();

                return first;

            }
        }

        void sensor_AllFramesReady(object sender, AllFramesReadyEventArgs e)
        {
            if (closing)
            {
                return;
            }

            //Get a skeleton
            Skeleton first = GetFirstSkeleton(e);

            if (first == null)
            {
                return;
            }
            
            GetCameraPoint(first, e);
            ScalePosition(RightHand, first.Joints[JointType.HandRight]);
            
            CheckButton(playButton, RightHand);
            CheckButton(challengeButton, RightHand);
            CheckButton(categoriesButton, RightHand);
            CheckButton(scoreboardButton, RightHand);
            CheckButton(quitButton, RightHand);
        }

        private void ScalePosition(FrameworkElement element, Joint joint)
        {
            //convert the value to X/Y
            Joint scaledJoint = joint.ScaleTo(1280, 720, .3f, .3f);

            Canvas.SetLeft(element, scaledJoint.Position.X);
            Canvas.SetTop(element, scaledJoint.Position.Y);

        }
        #endregion gesture processing

        #region camera stuff
        void GetCameraPoint(Skeleton first, AllFramesReadyEventArgs e)
        {

            using (DepthImageFrame depth = e.OpenDepthImageFrame())
            {
                if (depth == null ||
                    kinectSensorChooser1.Kinect == null)
                {
                    return;
                }


                //Map a joint location to a point on the depth map
                //right hand
                DepthImagePoint rightDepthPoint =
                    depth.MapFromSkeletonPoint(first.Joints[JointType.HandRight].Position);

                //Map a depth point to a point on the color image
                //right hand
                ColorImagePoint rightColorPoint =
                    depth.MapToColorImagePoint(rightDepthPoint.X, rightDepthPoint.Y,
                    ColorImageFormat.RgbResolution640x480Fps30);

                //Set location
                CameraPosition(RightHand, rightColorPoint);

            }
        }

        private void CameraPosition(FrameworkElement element, ColorImagePoint point)
        {
            //Divide by 2 for width and height so point is right in the middle 
            // instead of in top/left corner
            Canvas.SetLeft(element, point.X - element.Width / 2);
            Canvas.SetTop(element, point.Y - element.Height / 2);

        }
        #endregion camera stuff


        #region kinect processing
        void kinectSensorChooser1_KinectSensorChanged(object sender, DependencyPropertyChangedEventArgs e)
        {
            KinectSensor old = (KinectSensor)e.OldValue;

            StopKinect(old);

            KinectSensor sensor = (KinectSensor)e.NewValue;

            if (sensor == null)
            {
                return;
            }

            var parameters = new TransformSmoothParameters
            {
                Smoothing = 0.3f,
                Correction = 0.0f,
                Prediction = 0.0f,
                JitterRadius = 1.0f,
                MaxDeviationRadius = 0.5f
            };
            sensor.SkeletonStream.Enable(parameters);

            //sensor.SkeletonStream.Enable();

            sensor.AllFramesReady += new EventHandler<AllFramesReadyEventArgs>(sensor_AllFramesReady);
            sensor.DepthStream.Enable(DepthImageFormat.Resolution640x480Fps30);
            sensor.ColorStream.Enable(ColorImageFormat.RgbResolution640x480Fps30);

            try
            {
                sensor.Start();
            }
            catch (InvalidOperationException)
            {
                kinectSensorChooser1.AppConflictOccurred();
            }
        }


        private void StopKinect(KinectSensor sensor)
        {
            if (sensor != null)
            {
                if (sensor.IsRunning)
                {
                    //stop sensor 
                    sensor.Stop();

                    //stop audio if not null
                    if (sensor.AudioSource != null)
                    {
                        sensor.AudioSource.Stop();
                    }
                }
            }
        }
        #endregion kinect processing


    }
}







