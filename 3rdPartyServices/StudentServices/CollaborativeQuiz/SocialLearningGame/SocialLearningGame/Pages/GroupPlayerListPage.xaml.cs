using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using SocialLearningGame.Entities;
using SocialLearningGame.Logic;

namespace SocialLearningGame.Pages
{
    /// <summary>
    /// Interaction logic for Page1.xaml
    /// </summary>
    public partial class GroupPlayerListPage : Page
    {

        private static int index;
        private static int maxCount;
        private List<String> players = new List<String>();
        private SolidColorBrush highlight;

        public GroupPlayerListPage()
        {
            InitializeComponent();
            players = GameLogic._userSession.currentCis.contributors;
            maxCount = players.Count();

            if (maxCount > 20)
            {
                nextButton.Visibility = Visibility.Visible;
            }

            loadPage();
        }

        private void loadPage()
        {
            groupsTitle.Text = "Contributors of Community " + GameLogic._userSession.currentCis.cisName;
            
            TextBlock tb;
            int x;
            int row = 1;
            int column = 0;
            for (x = index; x < index + 20; x++)
            {
                if (x < maxCount)
                {
                    String s = players[x];
                    if (s.Equals(GameLogic._userSession.user.userJid))
                    {
                        highlight = Brushes.DarkOrange;
                    }
                    else
                    {
                        highlight = Brushes.Black;
                    }
                    tb = new TextBlock();
                    if (row == 6)
                    {
                        row = 1;
                        column++;
                    }
                    tb.Text = s;
                    tb.FontSize = 25;
                    tb.Foreground = highlight;
                    tb.TextAlignment = TextAlignment.Center;
                    tb.VerticalAlignment = VerticalAlignment.Center;
                    Grid.SetRow(tb, row);
                    Grid.SetColumn(tb, column);
                    groupListGrid.Children.Add(tb);
                    row++;
                }
            }

        }

        private void backButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            index = index - 20;
            if (index == 0)
            {
                backButton.Visibility = Visibility.Hidden;
                nextButton.Visibility = Visibility.Visible;
            }
            else if (index < maxCount - 20)
            {
                nextButton.Visibility = Visibility.Visible;
            }
            loadPage();
        }

        private void nextButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            index = index + 20;
            if (index + 20 >= maxCount)
            {
                backButton.Visibility = Visibility.Visible;
                nextButton.Visibility = Visibility.Hidden;
            }
            if (index > 0)
            {
                backButton.Visibility = Visibility.Visible;
            }
            loadPage();
        }
    } 
}
