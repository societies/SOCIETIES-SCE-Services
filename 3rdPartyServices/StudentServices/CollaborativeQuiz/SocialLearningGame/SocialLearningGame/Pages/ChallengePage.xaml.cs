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
using Microsoft.Kinect;
using Coding4Fun.Kinect.Wpf;
using Coding4Fun.Kinect;
using Coding4Fun.Kinect.Wpf.Controls;
using Microsoft.Samples.Kinect.WpfViewers;
using System.IO;

namespace SocialLearningGame.Pages
{
    /// <summary>
    /// Interaction logic for ChallengePage.xaml
    /// </summary>
    public partial class ChallengePage : Page
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
        Student student = new Student();

        HoverButton[] buttons;
        int friendNumber; //used to know which friend is at the start of the current list, if 0 then first page

        #endregion variables

        #region page
        public ChallengePage()
        {
            InitializeComponent();

            student = MainWindow.student;
            friendNumber = 0;

            if (MainWindow.challenges == null)
            {
                Console.WriteLine("friend");
                getFriendList(0);
            }
            else
            {
                Console.WriteLine("challenge");
                getChallengeList(0);
            }
            //create event handler for home button
            homeButton.Click += new RoutedEventHandler(navigateHome);
            nextButton.Click += new RoutedEventHandler(nextFriends);
            backButton.Click += new RoutedEventHandler(previousFriends);
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            kinectSensorChooser1.KinectSensorChanged += new DependencyPropertyChangedEventHandler(kinectSensorChooser1_KinectSensorChanged);
        }

        private void Window_Closing(object sender, RoutedEventArgs e)
        {
            MainWindow.student = student;
            closing = true;
            StopKinect(kinectSensorChooser1.Kinect);
        }
        #endregion page

        #region buttons
        public void navigateHome(object Sender, RoutedEventArgs e)
        {

            this.NavigationService.Navigate(new HomePage());
        }

        public void nextFriends(object Sender, RoutedEventArgs e)
        {
            if (MainWindow.allStudents.Count < 8)
                getFriendList(0);
            else
                getFriendList(friendNumber += 8);
        }

        public void previousFriends(object Sender, RoutedEventArgs e)
        {
            if (friendNumber != 0)
            {
                getFriendList(friendNumber -= 8);
            }
            else
                getFriendList(0);
        }

        public void navigateCategoriesChallengeSent(object Sender, RoutedEventArgs e)
        {
            Student friend = new Student();
            var sender = new HoverButton();
            sender = (HoverButton)Sender;
            
            try
            {
                for (int i = 0; i < buttons.Length; i++)
                {
                    if (("button" + i).Equals(sender.Name))
                    {
                        friend = MainWindow.allStudents.ElementAt(i);
                        this.NavigationService.Navigate(new CategoriesPage(friend));
                    }
                }

            }
            catch (Exception ex)
            {
                Console.WriteLine("Error: " + ex.Message);
            }
            
        }

        public void navigateAcceptChallenge(object Sender, RoutedEventArgs e)
        {
            var sender = new HoverButton();
            sender = (HoverButton)Sender;

            try
            {
                var challenger = sender.Name;
                Console.WriteLine("sender = "+challenger);
                for (int i = 0; i < MainWindow.challenges.Count; i++)
                {
                    if (("button"+i).Equals(sender.Name))
                    {
                        Console.WriteLine(MainWindow.challenges.ElementAt(i).challenger.name);
                        this.NavigationService.Navigate(new PlayPage(MainWindow.challenges.ElementAt(i)));
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception: " + ex);
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
                //Console.WriteLine("Error: " + e.Message);
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
            CheckButton(homeButton, RightHand);
            CheckButton(nextButton, RightHand);
            CheckButton(backButton, RightHand);
            try
            {
                //call CheckButton for any existing friend buttons
                if (buttons.Length > 0)
                {
                    for (int i = 0; i < buttons.Length; i++)
                    {
                        CheckButton(buttons[i], RightHand);
                    }
                }
            }
            catch(NullReferenceException ex)
            {
                Console.WriteLine("Exception: "+ex.Message);
            }
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

        #region friends
        //get the list of the student's friend/CISs
        private void getFriendList(int start)
        {
            //set the visibility of the back button, so it is only visible if the user can go back a page
            if (friendNumber == 0)
            {
                back.Visibility = System.Windows.Visibility.Hidden;
                backButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else
            {
                back.Visibility = System.Windows.Visibility.Visible;
                backButton.Visibility = System.Windows.Visibility.Visible;
            }
            // update the textblocks in the layout to display the names of the users friends
            TextBlock[] textblocks = { friend1, friend2, friend3, friend4, friend5, friend6, friend7, friend8 };
            int index = 0;
            int amount = 8;
            //set the amount of friends that will be displayed on the page, this is a maximum of 8, but if there are not 8 friends left to display
            //is set to the friends count minus 1
            //also set the visibility of the next button
            if (MainWindow.allStudents.Count <= start + amount)
            {
                amount = MainWindow.allStudents.Count;
                next.Visibility = System.Windows.Visibility.Hidden;
                nextButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else
            {
                amount = 8;
                next.Visibility = System.Windows.Visibility.Visible;
                nextButton.Visibility = System.Windows.Visibility.Visible;
            }

            //Create the hoverbuttons and add them to the buttons array, and display them onscreen
            buttons = new HoverButton[amount];
            try
            {
                for (int i = start; start < (start + amount); i++)
                {
                    if (MainWindow.allStudents.ElementAt(i) != null)
                    {
                        textblocks[index].Text = MainWindow.allStudents.ElementAt(i).name;
                        var button = new HoverButton();
                        button.Name = "button"+i;
                        button.ImageSize = 100;
                        button.TimeInterval = 1500;
                        button.Height = 50;
                        button.Width = 50;
                        canvas1.Children.Add(button);
                        Canvas.SetLeft(button, Canvas.GetLeft(textblocks[index]));
                        Canvas.SetTop(button, Canvas.GetTop(textblocks[index]));

                        //event handler for the button
                        button.Click += new RoutedEventHandler(navigateCategoriesChallengeSent);
                        buttons[i] = button;
                        index += 1;
                    }
                    else
                    {
                        textblocks[index].Text = "";
                        index += 1;
                    }

                }
            }
            catch (Exception e)
            {
                for (int i = index; i < textblocks.Length; i++)
                {
                    textblocks[i].Text = "";
                }
                Console.WriteLine("Error: "+e.Message);
            }
 
        }

        void getChallengeList(int start)
        {
            //set the visibility of the back button, so it is only visible if the user can go back a page
            if (friendNumber == 0)
            {
                back.Visibility = System.Windows.Visibility.Hidden;
                backButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else
            {
                back.Visibility = System.Windows.Visibility.Visible;
                backButton.Visibility = System.Windows.Visibility.Visible;
            }
            // update the textblocks in the layout to display the names of the users friends
            TextBlock[] textblocks = { friend1, friend2, friend3, friend4, friend5, friend6, friend7, friend8 };
            int index = 0;
            int amount = 8;
            //set the amount of friends that will be displayed on the page, this is a maximum of 8, but if there are not 8 friends left to display
            //is set to the friends count minus 1
            //also set the visibility of the next button
            if (MainWindow.challenges.Count <= start + amount)
            {
                amount = MainWindow.challenges.Count;
                next.Visibility = System.Windows.Visibility.Hidden;
                nextButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else
            {
                amount = 8;
                next.Visibility = System.Windows.Visibility.Visible;
                nextButton.Visibility = System.Windows.Visibility.Visible;
            }

            //Create the hoverbuttons and add them to the buttons array, and display them onscreen
            buttons = new HoverButton[amount];
            try
            {
                for (int i = start; start < (start + amount); i++)
                {
                    if (MainWindow.challenges.ElementAt(i) != null)
                    {
                        textblocks[index].Text = MainWindow.challenges.ElementAt(i).challenger.name +", "+MainWindow.challenges.ElementAt(i).category;
                        var button = new HoverButton();
                        button.Name = MainWindow.challenges.ElementAt(i).challenger.name + MainWindow.challenges.ElementAt(i).challenger.score;
                        button.ImageSize = 100;
                        button.TimeInterval = 1500;
                        button.Height = 50;
                        button.Width = 50;
                        canvas1.Children.Add(button);
                        Canvas.SetLeft(button, Canvas.GetLeft(textblocks[index]));
                        Canvas.SetTop(button, Canvas.GetTop(textblocks[index]));

                        //event handler for the button
                        button.Click += new RoutedEventHandler(navigateAcceptChallenge);
                        buttons[i] = button;
                        index += 1;
                    }
                    else
                    {
                        textblocks[index].Text = "";
                        index += 1;
                    }

                }
            }
            catch (Exception e)
            {
                for (int i = index; i < textblocks.Length; i++)
                {
                    textblocks[i].Text = "";
                }
                Console.WriteLine("Error: " + e.Message);
            }
        }
        #endregion friends

    }
}
