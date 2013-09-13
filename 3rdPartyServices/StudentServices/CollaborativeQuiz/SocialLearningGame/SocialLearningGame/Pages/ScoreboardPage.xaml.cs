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

namespace SocialLearningGame.Pages
{
    /// <summary>
    /// Interaction logic for ScoreboardPage.xaml
    /// </summary>
    public partial class ScoreboardPage : Page
    {
        private static ScoreboardPage _instance = new ScoreboardPage();
        public static ScoreboardPage Instance { get { _instance.refreshPage(); return _instance; } }
        private List<UserScore> allUsers;
        private List<TextBlock> childNodes;
     
        

        //<TextBlock Canvas.Left="59" Canvas.Top="120" Height="44" Name="studentName" Text="You" Width="400" FontSize="28" MinWidth="50" MaxWidth="400" />

        private ScoreboardPage()
        {
            InitializeComponent();
            childNodes = new List<TextBlock>();
            refreshPage();
        }


        private void refreshPage()
        {
            foreach (TextBlock oldTB in childNodes)
            {
                scoreBoard.Children.Remove(oldTB);
            }
            childNodes.Clear();
            Console.WriteLine("Getting new user score...");
            allUsers = GameLogic.getAllUsers().OrderByDescending(o => o.score).ToList();
            double top_margin = 0;
            int rank = 1;
            TextBlock tb;
            foreach (UserScore user in allUsers)
            {
                tb = new TextBlock();
                tb.SetValue(Canvas.LeftProperty, 59.0);
                tb.SetValue(Canvas.TopProperty, 120.0 + top_margin);
                tb.Height = 44;
                tb.Width = 400;
                tb.Text = user.name;
                tb.FontSize = 30;
                scoreBoard.Children.Add(tb);
                childNodes.Add(tb);
                top_margin = top_margin + 63.0;
                //        <TextBlock Canvas.Left="523" Canvas.Top="134" Name="studentRank" MinWidth="20" MinHeight="20" FontSize="20" />
                //<TextBlock Canvas.Left="612" Canvas.Top="134" Name="scoreBlock" MinWidth="20" MinHeight="20" FontSize="20" />
                tb = new TextBlock();
                tb.SetValue(Canvas.LeftProperty, 523.0);
                tb.SetValue(Canvas.TopProperty, 67.5 + top_margin);//= new System.Windows.Thickness(523, 120+top_margin, 1, 1);
                tb.FontSize = 20;
                tb.Text = rank.ToString();
                scoreBoard.Children.Add(tb);
                childNodes.Add(tb);
                rank++;

                tb = new TextBlock();
                tb.SetValue(Canvas.LeftProperty, 612.0);
                tb.SetValue(Canvas.TopProperty, 67.5 + top_margin);
                tb.FontSize = 20;
                tb.Text = user.score.ToString();
                scoreBoard.Children.Add(tb);
                childNodes.Add(tb);
            }
        }

        private void backButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {

        }

        private void nextButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {

        }
    }
}
