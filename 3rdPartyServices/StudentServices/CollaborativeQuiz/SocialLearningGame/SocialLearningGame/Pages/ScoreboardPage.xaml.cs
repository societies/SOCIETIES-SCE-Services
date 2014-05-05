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
using System.Linq;
using System;
using System.Windows.Media;
using System.Windows;

namespace SocialLearningGame.Pages
{
    /// <summary>
    /// Interaction logic for ScoreboardPage.xaml
    /// </summary>
    public partial class ScoreboardPage : Page
    {
        private List<User> allUsers;
        private List<Cis> allCis;
        private List<TextBlock> childNodes;
        private SolidColorBrush highlight;
        private static int index;
        private static int maxCount;
     
        

        //<TextBlock Canvas.Left="59" Canvas.Top="120" Height="44" Name="studentName" Text="You" Width="400" FontSize="28" MinWidth="50" MaxWidth="400" />

        public ScoreboardPage()
        {
            InitializeComponent();
        
            childNodes = new List<TextBlock>();
            if (GameLogic._userSession.currentPlayer == GameStage.USER)
            {
                playerGroup.Text = "Player";
                List<User> users = GameLogic.getAllUsers();
                allUsers = users.OrderByDescending(o => o.score).ToList();
                maxCount = allUsers.Count();
                if (maxCount > 5)
                {
                    nextButton.Visibility = Visibility.Visible;
                }
                refreshUserPage();
            }
            else if (GameLogic._userSession.currentPlayer == GameStage.CIS)
            {
                playerGroup.Text = "CIS";
                allCis = GameLogic.getAllCis();
                if (allCis == null)
                {
                    Console.WriteLine(DateTime.Now + "\t" + "ALL CIS LIST IS NULL");
                }
                allCis = allCis.OrderByDescending(o => o.score).ToList();
                maxCount = allCis.Count();
                if (maxCount > 5)
                {
                    nextButton.Visibility = Visibility.Visible;
                }
                refreshGroupPage();
            }
        }


        private void refreshUserPage()
        {
            foreach (TextBlock oldTB in childNodes)
            {
                scoreBoardGrid.Children.Remove(oldTB);
            }
            childNodes.Clear();
              
            int row = 1;
            int column = 0;
            int rank = 1;
            TextBlock tb;
            int x = index;
            User user;
            for (x = index; x < index + 5; x++)
            {
                if (x < allUsers.Count())
                {
                    user = allUsers[x];
                    if (user.userJid.Equals(GameLogic._userSession.user.userJid))
                    {
                        highlight = Brushes.DarkOrange;
                    }
                    else
                    {
                        highlight = Brushes.Black;
                    }
                    tb = new TextBlock();
                    tb.Text = user.userJid;
                    tb.FontSize = 28;
                    tb.Foreground = highlight;
                    Grid.SetRow(tb, row);
                    Grid.SetColumn(tb, column);
                    scoreBoardGrid.Children.Add(tb);
                    childNodes.Add(tb);

                    column++;

                    tb = new TextBlock();
                    tb.FontSize = 28;
                    tb.Foreground = highlight;
                    Grid.SetRow(tb, row);
                    Grid.SetColumn(tb, column);
                    tb.Text = rank.ToString();
                    scoreBoardGrid.Children.Add(tb);
                    childNodes.Add(tb);
                    rank++;
                    column++;

                    tb = new TextBlock();
                    tb.FontSize = 28;
                    tb.Foreground = highlight;
                    Grid.SetRow(tb, row);
                    Grid.SetColumn(tb, column);
                    tb.Text = user.score.ToString();
                    scoreBoardGrid.Children.Add(tb);
                    childNodes.Add(tb);
                    column = 0;
                    row++;
                }
            }
        }

        private void refreshGroupPage()
        {
            foreach (TextBlock oldTB in childNodes)
            {
                scoreBoardGrid.Children.Remove(oldTB);
            }
            childNodes.Clear();
            Console.WriteLine(DateTime.Now + "\t" +"Getting new group score...");
            int row = 1;
            int column = 0;
            int rank = 1;
            TextBlock tb;
            Cis cis;
            int x;
            for (x = index; x < index + 5; x++)
            {
                if (x < allCis.Count())
                {
                    cis = allCis[x];
                        if (cis.contributors.Contains(GameLogic._userSession.user.userJid))
                        {
                            highlight = Brushes.DarkOrange;
                        }
                        else
                        {
                            highlight = Brushes.Black;
                        }

                    tb = new TextBlock();
                    tb.FontSize = 28;
                    Grid.SetRow(tb, row);
                    Grid.SetColumn(tb, column);
                    tb.Foreground = highlight;
                    tb.Text = cis.cisName;
                    scoreBoardGrid.Children.Add(tb);
                    childNodes.Add(tb);

                    column++;

                    tb = new TextBlock();
                    tb.FontSize = 28;
                    tb.Foreground = highlight;
                    Grid.SetRow(tb, row);
                    Grid.SetColumn(tb, column);
                    tb.Text = rank.ToString();
                    scoreBoardGrid.Children.Add(tb);
                    childNodes.Add(tb);
                    rank++;

                    column++;

                    tb = new TextBlock();
                    tb.FontSize = 28;
                    tb.Foreground = highlight;
                    Grid.SetRow(tb, row);
                    Grid.SetColumn(tb, column);
                    tb.Text = cis.score.ToString();
                    scoreBoardGrid.Children.Add(tb);
                    childNodes.Add(tb);
                    column = 0;
                    row++;
                }
            }
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
            if (GameLogic._userSession.currentPlayer==GameStage.USER)
            {               
                refreshUserPage();
            }
            else if (GameLogic._userSession.currentPlayer==GameStage.CIS)
            {
                refreshGroupPage();
            }
        }

        private void nextButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            index = index + 5;
            if (index + 5 >=  maxCount)
            {
                backButton.Visibility = Visibility.Visible;
                nextButton.Visibility = Visibility.Hidden;
            }
            if (index > 0)
            {
                backButton.Visibility = Visibility.Visible;
            }
            if (GameLogic._userSession.currentPlayer == GameStage.USER)
            {
                refreshUserPage();
            }
            else if (GameLogic._userSession.currentPlayer == GameStage.CIS)
            {
                refreshGroupPage();
            }
        }
    }
}
