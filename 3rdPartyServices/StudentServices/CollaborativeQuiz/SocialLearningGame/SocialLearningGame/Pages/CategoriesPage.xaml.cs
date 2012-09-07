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
using RestSharp;

namespace SocialLearningGame.Pages
{
    /// <summary>
    /// Interaction logic for CategoriesPage.xaml
    /// </summary>
    public partial class CategoriesPage : Page
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

        //challenge variables
        bool challenge;
        Student friend;
        #endregion variables

        #region page
        public CategoriesPage()
        {
            InitializeComponent();

            //event handlers for buttons
            homeButton.Click += new RoutedEventHandler(navigateHome);
            generalKnowledgeButton.Click += new RoutedEventHandler(navigateGeneralKnowledge);
            courseRelatedButton.Click += new RoutedEventHandler(navigateCourseRelated);
            //not a challenge
            challenge = false;
            availableCategories();
        }

        public CategoriesPage(Student friend)
        {
            InitializeComponent();

            //event handlers for buttons
            homeButton.Click += new RoutedEventHandler(navigateHome);
            generalKnowledgeButton.Click += new RoutedEventHandler(navigateGeneralKnowledge);
            courseRelatedButton.Click += new RoutedEventHandler(navigateCourseRelated);
            
            //challenge is true set the friend to the friend passed in
            challenge = true;
            this.friend = friend;
            availableCategoriesChallenge();
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

        private void availableCategories()
        {
            if (MainWindow.student.score < 10)
            {
                MusPic.Visibility = System.Windows.Visibility.Hidden;
                musicButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else
            {
                musicButton.Click += new RoutedEventHandler(navigateMusic);
            }
            if (MainWindow.student.score < 25)
            {
                VGPic.Visibility = System.Windows.Visibility.Hidden;
                videoGamesButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else
            {
                videoGamesButton.Click += new RoutedEventHandler(navigateVideoGames);
            }
            if (MainWindow.student.score < 35)
            {
                SpPic.Visibility = System.Windows.Visibility.Hidden;
                sportsButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else
            {
                sportsButton.Click += new RoutedEventHandler(navigateSports);
            }
            if (MainWindow.student.score < 50)
            {
                HistPic.Visibility = System.Windows.Visibility.Hidden;
                historyButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else
            {
                historyButton.Click += new RoutedEventHandler(navigateHistory);
            }
            if (MainWindow.student.score < 65)
            {
                SciPic.Visibility = System.Windows.Visibility.Hidden;
                scienceButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else 
            {
                scienceButton.Click += new RoutedEventHandler(navigateScience);
            }
            if (MainWindow.student.score < 80)
            {
                TechPic.Visibility = System.Windows.Visibility.Hidden;
                technologyButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else
            {
                technologyButton.Click += new RoutedEventHandler(navigateTechnology);
            }
        }

        private void availableCategoriesChallenge()
        {
            if (MainWindow.student.score < 10 || friend.score < 10)
            {
                MusPic.Visibility = System.Windows.Visibility.Hidden;
                musicButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else if (MainWindow.student.score > 10 && friend.score > 10)
            {
                musicButton.Click += new RoutedEventHandler(navigateMusic);
            }

            if (MainWindow.student.score < 25 || friend.score < 25)
            {
                VGPic.Visibility = System.Windows.Visibility.Hidden;
                videoGamesButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else if (MainWindow.student.score > 25 || friend.score > 25)
            {
                videoGamesButton.Click += new RoutedEventHandler(navigateMusic);
            }

            if (MainWindow.student.score < 35 || friend.score < 35)
            {
                SpPic.Visibility = System.Windows.Visibility.Hidden;
                sportsButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else if (MainWindow.student.score > 35 && friend.score > 35)
            {
                sportsButton.Click += new RoutedEventHandler(navigateMusic);
            }

            if (MainWindow.student.score < 50 || friend.score < 50)
            {
                HistPic.Visibility = System.Windows.Visibility.Hidden;
                historyButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else if (MainWindow.student.score > 50 && friend.score > 50)
            {
                historyButton.Click += new RoutedEventHandler(navigateMusic);
            }

            if (MainWindow.student.score < 65 || friend.score < 65)
            {
                SciPic.Visibility = System.Windows.Visibility.Hidden;
                scienceButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else if (MainWindow.student.score > 65 && friend.score > 65)
            {
                scienceButton.Click += new RoutedEventHandler(navigateMusic);
            }

            if (MainWindow.student.score < 80 || friend.score < 80)
            {
                TechPic.Visibility = System.Windows.Visibility.Hidden;
                technologyButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else if (MainWindow.student.score > 80 && friend.score > 80)
            {
                technologyButton.Click += new RoutedEventHandler(navigateMusic);
            }
        }
        #endregion page

        #region buttons
        
        public void navigateHome(object Sender, RoutedEventArgs e)
        {
            this.NavigationService.Navigate(new HomePage());
        }

        //methods for navigating to the play page once a category is selected, if challenging a friend pass the friend and the category, otherwise just the category 
        public void navigateGeneralKnowledge(object Sender, RoutedEventArgs e)
        {
            if(!challenge)
                this.NavigationService.Navigate(new PlayPage("general knowledge")); //navigate to the play page passing in general knowledge as the category
            else
            {
                this.NavigationService.Navigate(new PlayPage(friend, "general knowledge")); //navigate to play page passing friend and category when challenge is true
            }
        }

        public void navigateCourseRelated(object Sender, RoutedEventArgs e)
        {
            if (!challenge)
                this.NavigationService.Navigate(new PlayPage("course related")); //navigate to play page passing course related in as category
            else
            {
                this.NavigationService.Navigate(new PlayPage(friend, "course related")); 
            }
        }

        public void navigateMusic(object Sender, RoutedEventArgs e)
        {
            if (!challenge)
                this.NavigationService.Navigate(new PlayPage("music"));
            else
            {
                this.NavigationService.Navigate(new PlayPage(friend, "music"));
            }
        }

        public void navigateVideoGames(object sender, RoutedEventArgs e)
        {
            if (!challenge)
                this.NavigationService.Navigate(new PlayPage("video games"));
            else
            {
                this.NavigationService.Navigate(new PlayPage(friend, "video games"));
            }
        }

        public void navigateSports(object sender, RoutedEventArgs e)
        {
            if (!challenge)
                this.NavigationService.Navigate(new PlayPage("sports"));
            else
            {
                this.NavigationService.Navigate(new PlayPage(friend, "sports"));
            }
        }

        public void navigateHistory(object sender, RoutedEventArgs e)
        {
            if (!challenge)
                this.NavigationService.Navigate(new PlayPage("history"));
            else
            {
                this.NavigationService.Navigate(new PlayPage(friend,"history"));
            }
        }

        public void navigateScience(object sender, RoutedEventArgs e)
        {
            if (!challenge)
                this.NavigationService.Navigate(new PlayPage("science"));
            else
            {
                this.NavigationService.Navigate(new PlayPage(friend,"science"));
            }
        }

        public void navigateTechnology(object sender, RoutedEventArgs e)
        {
            if (!challenge)
                this.NavigationService.Navigate(new PlayPage("technology"));
            else
            {
                this.NavigationService.Navigate(new PlayPage(friend,"technology"));
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
            CheckButton(generalKnowledgeButton, RightHand);
            CheckButton(courseRelatedButton, RightHand);
            CheckButton(musicButton, RightHand);
            CheckButton(videoGamesButton, RightHand);
            CheckButton(sportsButton, RightHand);
            CheckButton(historyButton, RightHand);
            CheckButton(scienceButton, RightHand);
            CheckButton(technologyButton, RightHand);
        }

        private void ScalePosition(FrameworkElement element, Joint joint)
        {
            //convert the value to X/Y
            Joint scaledJoint = joint.ScaleTo(1280, 720,.3f,.3f);

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
