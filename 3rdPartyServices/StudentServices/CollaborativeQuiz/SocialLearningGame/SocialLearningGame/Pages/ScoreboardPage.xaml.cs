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
    /// Interaction logic for ScoreboardPage.xaml
    /// </summary>
    public partial class ScoreboardPage : Page
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
        int scoreNumber;
        #endregion variables

        #region page
        public ScoreboardPage()
        {
            InitializeComponent();
            scoreBlock.Text = MainWindow.student.score.ToString();
            homeButton.Click += new RoutedEventHandler(navigateHome);
            nextButton.Click +=new RoutedEventHandler(nextScores);
            backButton.Click +=new RoutedEventHandler(previousScores);
            scoreNumber = 0;
            createScoreboard(scoreNumber);
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


        void createScoreboard(int start)
        {
            //set the visibility of the back button 
            if (scoreNumber == 0)
            {
                back.Visibility = System.Windows.Visibility.Hidden;
                backButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else
            {
                back.Visibility = System.Windows.Visibility.Visible;
                backButton.Visibility = System.Windows.Visibility.Visible;
            }

            //put the textblocks for the friends names, scores and ranks in arrays
            TextBlock[] people = { person1, person2, person3, person4, person5 };
            TextBlock[] scores = { person1score, person2score, person3score, person4score, person5score };
            TextBlock[] ranks = { rank1, rank2, rank3, rank4, rank5 };
            
            //sort the students list of friends, incl. user, by score
            List<Student> scoreboardStats = new List<Student>();
            scoreboardStats.AddRange(MainWindow.allStudents);
            scoreboardStats.Add(MainWindow.student);
            scoreboardStats.Sort(delegate(Student s1, Student s2){
                return s2.score.CompareTo(s1.score);
            });

            //find the current players rank
            studentRank.Text = (scoreboardStats.FindIndex(delegate(Student s) { return s.Equals(MainWindow.student); })+1).ToString();
            studentName.Text = MainWindow.student.name;

            int index = 0;
            int amount = 5;
            //set the visibility of the next button and find out if there are enough friends to fill the page
            //set the amount to be either 5 or the amount of friends left (less than 5)
            if (scoreboardStats.Count <= start + amount)
            {
                amount = scoreboardStats.Count;
                next.Visibility = System.Windows.Visibility.Hidden;
                nextButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else
            {
                amount = 5;
                next.Visibility = System.Windows.Visibility.Visible;
                nextButton.Visibility = System.Windows.Visibility.Visible;
            }
            //loop through the list a maximum of 5 times per page
            try
            {
                for (int i = start; start < (start + amount)-1; i++)
                {
                    //set the name, score and rank of each friend
                    people[index].Text = scoreboardStats.ElementAt(i).name;
                    scores[index].Text = scoreboardStats.ElementAt(i).score.ToString();
                    ranks[index].Text = (i + 1).ToString();
                    index += 1;
                }
            }
            //if something goes wrong catch the Exception and make sure any remaining boxes remain blank
            catch (Exception e)
            {
                for (int i = index; i < people.Length; i++)
                {
                    people[i].Text = "";
                    scores[i].Text = "";
                    ranks[i].Text = "";
                }
                Console.WriteLine("Error: " + e.Message);
            }
        }
        #endregion page

        #region buttons
        public void navigateHome(object Sender, RoutedEventArgs e)
        {
            this.NavigationService.Navigate(new HomePage());
        }

        //if the user selects the next button create the scoreboard with the next 5 friends
        public void nextScores(object Sender, RoutedEventArgs e)
        {
            if ((MainWindow.allStudents.Count + 1) < scoreNumber + 5)
                createScoreboard(scoreNumber);
            else
                createScoreboard(scoreNumber += 5); //test to see if this works on more than 2 pages
        }

        //if the user selects the previous button create the scoreboard with the previous 5 friends
        public void previousScores(object Sender, RoutedEventArgs e)
        {
            if (scoreNumber == 0)
                createScoreboard(0);
            else
                createScoreboard(scoreNumber -= 5);
        }

        private static void CheckButton(HoverButton button, Ellipse hand)
        {
            try
            {
                if (IsItemMidpointInContainer(button, hand))
                {
                    button.Hovering();
                }
                else
                {
                    button.Release();
                }
            }
            catch(System.InvalidOperationException e)
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
            
            CheckButton(homeButton, RightHand);
            CheckButton(nextButton, RightHand);
            CheckButton(backButton, RightHand);
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
