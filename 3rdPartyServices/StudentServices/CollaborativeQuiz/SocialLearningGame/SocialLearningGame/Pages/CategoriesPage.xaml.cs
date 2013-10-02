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

using System.Windows;
using System.Windows.Controls;
using SocialLearningGame.Entities;
using SocialLearningGame.Logic;
using System.Collections.Generic;
using Microsoft.Kinect.Toolkit.Controls;
using System;
using System.Linq;


namespace SocialLearningGame.Pages
{
    /// <summary>
    /// Interaction logic for CategoriesPage.xaml
    /// </summary>
    public partial class CategoriesPage : Page
    {

        private static Dictionary<KinectCircleButton, Category> buttonToCat = new Dictionary<KinectCircleButton, Category>();
        private static List<Category> categoriesToAdd = new List<Category>();
        private static List<Category> categories = new List<Category>();
        private static List<Category> superCategories = new List<Category>();
        private static List<String> userInterests = new List<String>();
        private static List<String> keywords = new List<String>();
        private static List<UIElement> childNodes = new List<UIElement>();
        private static int index;
        private static int maxCount;
        private int general = 0;
        private int personal = 0;

        public CategoriesPage()
        {
            InitializeComponent();
            categories = new List<Category>(GameLogic._userSession.allCategories);
            Console.WriteLine("GOT CATEGORIES" + categories.ToString());
            superCategories = new List<Category>();
            userInterests = new List<String>(GameLogic._userSession.userInterests);
            keywords = getKeywords(userInterests);

            sortCategories();

            index = 0;
            maxCount = categories.Count();

            if (maxCount > 6)
            {
                nextButton.Visibility = Visibility.Visible;
            }

            addButtons();
        }

        private List<String> getKeywords(List<String> userInterests)
        {
            List<String> keywords = new List<String>();
            foreach (String s in userInterests)
            {
               String[] array = s.Split(' ');
               foreach (String s2 in array)
               {
                   Console.WriteLine("Adding " + s2);
                   keywords.Add(s2.Trim());
               }
            }
            return keywords;
        }

        private void sortCategories() 
        {           
            foreach (Category c in categories.ToList())
            {
                Console.WriteLine(c.Name);
                if (c.superCatID == 0)
                {
                    superCategories.Add(c);
                    categories.Remove(c);
                    if (c.Name.Equals("General"))
                    {
                        general = c.categoryID;
                        Console.WriteLine("Got general ID");
                    }
                    else if (c.Name.Equals("Personal"))
                    {
                        personal = c.categoryID;
                    }
                }
            }
            
        }

 
        private void addButtons()
        {
            foreach (UIElement u in childNodes)
            {
                catGrid.Children.Remove(u);
            }
            buttonToCat.Clear();
            Console.WriteLine("In add buttons");

            int xCount = 0;
            int yCount = 0;
            int x;
            Category c;
            Console.WriteLine("INDEX: " + index + ", MAX:" + maxCount + ", SIZE: " + categories.Count());
            for(x = index; x < index + 6; x++)
            {
                if (x < categories.Count())
                {
                    c = categories[x];
                    if (xCount == 3)
                    {
                        yCount += 3;
                        xCount = 0;
                    }
                    if (c.superCatID == general)
                    {
                        Console.WriteLine("General Q found, Add Button");
                        createButton(c, xCount, yCount);
                        xCount++;
                    }
                    if (c.superCatID == personal)
                    {
                        if (keywords.Contains(c.Name) || userInterests.Contains(c.Name))
                        {
                            Console.WriteLine("Keywords match, creating a button");
                            createButton(c, xCount, yCount);
                            xCount++;
                        }
                        else
                        {
                            Console.WriteLine("Keywords don't match, not creating a button for...");
                            Console.WriteLine(c.Name);
                        }
                    }
                }
            }
        }

        private void createButton(Category c, int xCount, int yCount)
        {
                            TextBlock tb = new TextBlock();
                tb.Text = c.Name;
                tb.TextAlignment = TextAlignment.Center;
                tb.FontSize = 28;
                Grid.SetColumn(tb, xCount);
                Grid.SetRow(tb, yCount);
                catGrid.Children.Add(tb);
                childNodes.Add(tb);
              //  yCount++;


                KinectCircleButton button = new KinectCircleButton();
             //   button.Height = 150.0;
              //  button.MaxWidth = 200.0;
                button.Click += buttonClick;
                buttonToCat.Add(button, c);
             //  Grid.SetRow(button, xCount);
              // Grid.SetColumn(button, yCount);//.LeftProperty, 30.0*xCount);
               // button.SetValue(Canvas.TopProperty, 100.0 + (15.0*yCount));
                Grid.SetColumn(button, xCount);
                Grid.SetRow(button, yCount+1);
                catGrid.Children.Add(button);
                childNodes.Add(button);
               // CategoryPage.Children.Add(button);
                
              //  Canvas.SetLeft(button, 55 + (150 * xCount));
               // Canvas.SetTop(button, 105 + ( 200 * yCount));
                xCount++;
                
                //ADD A NEW BUTTON

            }
        
        private void buttonClick(object sender, RoutedEventArgs e)
        {
            Category c = buttonToCat[(KinectCircleButton)sender];
            Console.WriteLine(c.Name);
           /* List<Category> c = GameLogic.getCategories();
            Category cc1 = null;
            foreach (Category f in c)
            {
                if(f.Name.Equals("Math"))
                {
                    cc1=f;
                }
            }*/
            MainWindow.SwitchPage(new PlayPage(c));
        }

        private void backButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            index = index - 6;
            if (index == 0)
            {
                backButton.Visibility = Visibility.Hidden;
                nextButton.Visibility = Visibility.Visible;
            }
            else if (index < maxCount - 6)
            {
                nextButton.Visibility = Visibility.Visible;
            }
            addButtons();
        }

        private void nextButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            index = index + 6;
            if (index + 6 >= maxCount)
            {
                backButton.Visibility = Visibility.Visible;
                nextButton.Visibility = Visibility.Hidden;
            }
            if (index > 0)
            {
                backButton.Visibility = Visibility.Visible;
            }
            addButtons();
    
        }
    }
}
