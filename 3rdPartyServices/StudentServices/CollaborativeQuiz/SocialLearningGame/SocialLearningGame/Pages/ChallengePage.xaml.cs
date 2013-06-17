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
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Shapes;
using Coding4Fun.Kinect.Wpf;
using Coding4Fun.Kinect.Wpf.Controls;
using Microsoft.Kinect;

namespace SocialLearningGame.Pages
{
    /// <summary>
    /// Interaction logic for ChallengePage.xaml
    /// </summary>
    public partial class ChallengePage : Page
    {
        private static ChallengePage _instance = new ChallengePage();
        public static ChallengePage Instance { get { return _instance; } }

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
        private ChallengePage()
        {
            InitializeComponent();

            //student = MainWindow.student;
            friendNumber = 0;

            //if (MainWindow.challenges == null)
            //{
            //    Console.WriteLine("friend");
            //    getFriendList(0);
            //}
            //else
            //{
            //    Console.WriteLine("challenge");
            //    getChallengeList(0);
            //}
        }

        public void nextFriends(object Sender, RoutedEventArgs e)
        {
            //if (MainWindow.allStudents.Count < 8)
            //    getFriendList(0);
            //else
            //    getFriendList(friendNumber += 8);
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
                        //friend = MainWindow.allStudents.ElementAt(i);
                        //this.NavigationService.Navigate(new CategoriesPage(friend));
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
                Console.WriteLine("sender = " + challenger);
                //for (int i = 0; i < MainWindow.challenges.Count; i++)
                //{
                //    if (("button"+i).Equals(sender.Name))
                //    {
                //        Console.WriteLine(MainWindow.challenges.ElementAt(i).challenger.name);
                //        this.NavigationService.Navigate(new PlayPage(MainWindow.challenges.ElementAt(i)));
                //    }
                //}
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



        #region friends
        //get the list of the student's friend/CISs
        private void getFriendList(int start)
        {
            //set the visibility of the back button, so it is only visible if the user can go back a page
            if (friendNumber == 0)
            {
                back.Visibility = System.Windows.Visibility.Hidden;
                //backButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else
            {
                back.Visibility = System.Windows.Visibility.Visible;
                //backButton.Visibility = System.Windows.Visibility.Visible;
            }
            // update the textblocks in the layout to display the names of the users friends
            TextBlock[] textblocks = { friend1, friend2, friend3, friend4, friend5, friend6, friend7, friend8 };
            int index = 0;
            int amount = 8;
            //set the amount of friends that will be displayed on the page, this is a maximum of 8, but if there are not 8 friends left to display
            //is set to the friends count minus 1
            //also set the visibility of the next button
            //if (MainWindow.allStudents.Count <= start + amount)
            //{
            //    amount = MainWindow.allStudents.Count;
            //    next.Visibility = System.Windows.Visibility.Hidden;
            //    nextButton.Visibility = System.Windows.Visibility.Hidden;
            //}
            //else
            //{
            //    amount = 8;
            //    next.Visibility = System.Windows.Visibility.Visible;
            //    nextButton.Visibility = System.Windows.Visibility.Visible;
            //}

            //Create the hoverbuttons and add them to the buttons array, and display them onscreen
            buttons = new HoverButton[amount];
            try
            {
                //for (int i = start; start < (start + amount); i++)
                //{
                //    if (MainWindow.allStudents.ElementAt(i) != null)
                //    {
                //        textblocks[index].Text = MainWindow.allStudents.ElementAt(i).name;
                //        var button = new HoverButton();
                //        button.Name = "button"+i;
                //        button.ImageSize = 100;
                //        button.TimeInterval = 1500;
                //        button.Height = 50;
                //        button.Width = 50;
                //        canvas1.Children.Add(button);
                //        Canvas.SetLeft(button, Canvas.GetLeft(textblocks[index]));
                //        Canvas.SetTop(button, Canvas.GetTop(textblocks[index]));

                //        //event handler for the button
                //        button.Click += new RoutedEventHandler(navigateCategoriesChallengeSent);
                //        buttons[i] = button;
                //        index += 1;
                //    }
                //    else
                //    {
                //        textblocks[index].Text = "";
                //        index += 1;
                //    }

                //}
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

        void getChallengeList(int start)
        {
            //set the visibility of the back button, so it is only visible if the user can go back a page
            if (friendNumber == 0)
            {
                back.Visibility = System.Windows.Visibility.Hidden;
                //backButton.Visibility = System.Windows.Visibility.Hidden;
            }
            else
            {
                back.Visibility = System.Windows.Visibility.Visible;
                //backButton.Visibility = System.Windows.Visibility.Visible;
            }
            // update the textblocks in the layout to display the names of the users friends
            TextBlock[] textblocks = { friend1, friend2, friend3, friend4, friend5, friend6, friend7, friend8 };
            int index = 0;
            int amount = 8;
            //set the amount of friends that will be displayed on the page, this is a maximum of 8, but if there are not 8 friends left to display
            //is set to the friends count minus 1
            //also set the visibility of the next button
            //if (MainWindow.challenges.Count <= start + amount)
            //{
            //    amount = MainWindow.challenges.Count;
            //    next.Visibility = System.Windows.Visibility.Hidden;
            //    nextButton.Visibility = System.Windows.Visibility.Hidden;
            //}
            //else
            //{
            //    amount = 8;
            //    next.Visibility = System.Windows.Visibility.Visible;
            //    nextButton.Visibility = System.Windows.Visibility.Visible;
            //}

            ////Create the hoverbuttons and add them to the buttons array, and display them onscreen
            //buttons = new HoverButton[amount];
            //try
            //{
            //    for (int i = start; start < (start + amount); i++)
            //    {
            //        if (MainWindow.challenges.ElementAt(i) != null)
            //        {
            //            textblocks[index].Text = MainWindow.challenges.ElementAt(i).challenger.name +", "+MainWindow.challenges.ElementAt(i).category;
            //            var button = new HoverButton();
            //            button.Name = MainWindow.challenges.ElementAt(i).challenger.name + MainWindow.challenges.ElementAt(i).challenger.score;
            //            button.ImageSize = 100;
            //            button.TimeInterval = 1500;
            //            button.Height = 50;
            //            button.Width = 50;
            //            canvas1.Children.Add(button);
            //            Canvas.SetLeft(button, Canvas.GetLeft(textblocks[index]));
            //            Canvas.SetTop(button, Canvas.GetTop(textblocks[index]));

            //            //event handler for the button
            //            button.Click += new RoutedEventHandler(navigateAcceptChallenge);
            //            buttons[i] = button;
            //            index += 1;
            //        }
            //        else
            //        {
            //            textblocks[index].Text = "";
            //            index += 1;
            //        }

            //    }
            //}
            //catch (Exception e)
            //{
            //    for (int i = index; i < textblocks.Length; i++)
            //    {
            //        textblocks[i].Text = "";
            //    }
            //    Console.WriteLine("Error: " + e.Message);
            //}
        }
        #endregion friends

    }
}
