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
using System.Collections.Generic;
using System.Windows.Controls;
using SocialLearningGame.Logic;
using SocialLearningGame.Entities;
using Microsoft.Kinect.Toolkit.Controls;
using System.Linq;
using System;
using System.Windows.Media;
using System.Windows;

namespace SocialLearningGame.Pages
{
    /// <summary>
    /// Interaction logic for ScoreboardPage.xaml
    /// </summary>
    public partial class SelectCIS : Page
    {
        private List<String> allCis;
        private Dictionary<KinectCircleButton, String> buttonPlayToCis;
        private Dictionary<KinectCircleButton, String> buttonViewToCis;
        private static int index;
        private static int maxCount;



        //<TextBlock Canvas.Left="59" Canvas.Top="120" Height="44" Name="studentName" Text="You" Width="400" FontSize="28" MinWidth="50" MaxWidth="400" />

        public SelectCIS()
        {
            InitializeComponent();
            buttonPlayToCis = new Dictionary<KinectCircleButton, String>();
            buttonViewToCis = new Dictionary<KinectCircleButton, String>();
            allCis = GameLogic.getCisNames();

            maxCount = allCis.Count();
            if (maxCount > 5)
            {
                nextButton.Visibility = Visibility.Visible;
            }
            refreshGroupPage();
        }

        private void refreshGroupPage()
        {
            selectCISGrid.Children.Clear();

       //     < Grid.Row="0" Grid.Column="0" Text="CIS" FontSize="32" />
       // <TextBlock Grid.Row="0" Grid.Column="1" FontSize="32" Text="View Contributors"  />
//<TextBlock Grid.Row="0" Grid.Column="2" FontSize="32" Text="Play"  />

            TextBlock cisText = new TextBlock();
            cisText.Text="CIS";
            cisText.FontSize= 32;
            Grid.SetRow(cisText, 0);
            Grid.SetColumn(cisText, 0);

            TextBlock viewText = new TextBlock();
            viewText.Text = "View Contributors";
            viewText.FontSize = 32;
            Grid.SetRow(viewText, 0);
            Grid.SetColumn(viewText, 1);

            TextBlock playText = new TextBlock();
            playText.Text = "Play";
            playText.FontSize = 32;
            Grid.SetRow(playText, 0);
            Grid.SetColumn(playText, 2);

            selectCISGrid.Children.Add(cisText);
            selectCISGrid.Children.Add(viewText);
            selectCISGrid.Children.Add(playText);


            int row = 1;
            int column = 0;
            TextBlock tb;
            String cis;
            int x;
            for (x = index; x < index + 5; x++)
            {
                if (x < allCis.Count())
                {
                    cis = allCis[x];

                    tb = new TextBlock();
                    tb.FontSize = 28;
                    Grid.SetRow(tb, row);
                    Grid.SetColumn(tb, column);
                    tb.Text = cis;
                    selectCISGrid.Children.Add(tb);

                    column++;

                    KinectCircleButton viewButton = new KinectCircleButton();
                    Grid.SetColumn(viewButton, column);
                    Grid.SetRow(viewButton, row);

                    viewButton.Click += new RoutedEventHandler(viewClick);
                    selectCISGrid.Children.Add(viewButton);
                    buttonViewToCis.Add(viewButton, cis);


                    column++;

                    KinectCircleButton button = new KinectCircleButton();
                 //   tb.FontSize = 28;
                    Grid.SetRow(button, row);
                    Grid.SetColumn(button, column);
                  //  tb.Text = cis.score.ToString();
                    selectCISGrid.Children.Add(button);
                    buttonPlayToCis.Add(button, cis);
                    button.Click += new RoutedEventHandler(kinnectClick);
                    column = 0;
                    row++;
                }
            }
        }

        private void kinnectClick(object sender, RoutedEventArgs e)
        {
            String cisString = buttonPlayToCis[((KinectCircleButton) sender)];
            Cis cis = GameLogic.getCis(cisString);
            GameLogic._userSession.currentCis = cis;
            MainWindow.SwitchPage(new PlayPage(null));
        }

        private void viewClick(object sender, RoutedEventArgs e)
        {
            String cisString = buttonViewToCis[((KinectCircleButton)sender)];
            Cis cis = GameLogic.getCis(cisString);
            GameLogic._userSession.currentCis = cis;
            MainWindow.SwitchPage(new GroupPlayerListPage());
        }

        private void backButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            index = index - 5;
            if (index == 0)
            {
                backButton.Visibility = Visibility.Hidden;
                nextButton.Visibility = Visibility.Visible;
            }
            else if (index < maxCount - 5)
            {
                nextButton.Visibility = Visibility.Visible;
            }

                refreshGroupPage();
        }

        private void nextButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            index = index + 5;
            if (index + 5 >= maxCount)
            {
                backButton.Visibility = Visibility.Visible;
                nextButton.Visibility = Visibility.Hidden;
            }
            if (index > 0)
            {
                backButton.Visibility = Visibility.Visible;
            }
                refreshGroupPage();
        }
    }
}
