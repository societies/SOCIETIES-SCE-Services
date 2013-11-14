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
using SocialLearningGame.Comms;
using SocialLearningGame.Logic;
using Microsoft.Kinect.Toolkit.Controls;
using System.Windows.Controls.Primitives;


namespace SocialLearningGame.Pages
{
    /// <summary>
    /// Interaction logic for Page1.xaml
    /// </summary>
    public partial class InvitePage : Page
    {
        private Dictionary<KinectCircleButton, String> buttonToUser = new Dictionary<KinectCircleButton, String>();
        private List<UIElement> childNodes = new List<UIElement>();
        List<String> players;
        List<String> invitedPlayers;
        List<String> groupPlayers;


        private int index;
        private int maxCount;

        public InvitePage()
        {
            InitializeComponent();
            players = GameLogic._userSession.allUsers.Select(user => user.userJid).ToList();
            invitedPlayers = GameLogic._userSession.invitedUsers;
            groupPlayers = GameLogic._userSession.allGroupPlayers;
            index = 0;
            maxCount = players.Count;

            if (maxCount > 4)
            {
                nextButton.Visibility = Visibility.Visible;
            }

            listMembers();
        }

        private void sortLists()
        {
            //REMOVE PLAYERS ALREADY IN GROUP
            players = players.Except(groupPlayers).ToList();
            //REMOVE INVITED PLAYERS
            players = players.Except(invitedPlayers).ToList();
        }


        private void listMembers()
        {
            foreach (UIElement u in childNodes)
            {
                inviteGrid.Children.Remove(u);
            }

            sortLists();

            int row = 1;
            TextBlock tb;
            KinectCircleButton kb;
            int x;
            for (x = index; x < index + 4; x++)
            {
                if (x < players.Count())
                {
                    tb = new TextBlock();
                    tb.Text = players[x].Split(new char[] { '.' }, 2)[0];
                    tb.FontSize = 28;
                    Grid.SetColumn(tb, 0);
                    Grid.SetColumnSpan(tb, 2);
                    Grid.SetRow(tb, row);
                    inviteGrid.Children.Add(tb);
                    kb = new KinectCircleButton();
                    kb.Click += kb_Click;
                    Grid.SetRow(kb, row);
                    Grid.SetColumn(kb, 2);
                    inviteGrid.Children.Add(kb);
                    buttonToUser.Add(kb, players[x]);
                    childNodes.Add(tb);
                    childNodes.Add(kb);
                }
                row++;
            }
            
        }


        void kb_Click(object sender, RoutedEventArgs e)
        {
            String userJid = buttonToUser[(KinectCircleButton)sender];
            GameLogic.postRemoteData(DataType.INVITE_PLAYER, userJid);
            GameLogic._userSession.invitedUsers.Add(userJid);
            listMembers();
        }

        private void backButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            index = index - 4;
            if (index == 0)
            {
                backButton.Visibility = Visibility.Hidden;
                nextButton.Visibility = Visibility.Visible;
            }
            else if (index < maxCount - 4)
            {
                nextButton.Visibility = Visibility.Visible;
            }
            listMembers();
        }

        private void nextButton_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            index = index + 4;
            if (index + 4 >= maxCount)
            {
                backButton.Visibility = Visibility.Visible;
                nextButton.Visibility = Visibility.Hidden;
            }
            if (index > 0)
            {
                backButton.Visibility = Visibility.Visible;
            }
            listMembers();
        }


    }
}
