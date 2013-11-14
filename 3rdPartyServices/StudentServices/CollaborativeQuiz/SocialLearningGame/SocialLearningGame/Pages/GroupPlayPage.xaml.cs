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
using Microsoft.Kinect.Toolkit.Controls;
using System.Windows.Controls.Primitives;
using SocialLearningGame.Comms;


namespace SocialLearningGame.Pages
{
    /// <summary>
    /// Interaction logic for Page1.xaml
    /// </summary>
    public partial class GroupPlayPage : Page
    {

        private static Groups group;
        private const int CREATE = 0, INVITE = 1, PLAY = 2, DELETE = 3, LEAVE=4, PLAYERS=5; 
        private static Dictionary<KinectCircleButton, int> buttonDic = new Dictionary<KinectCircleButton, int>();
        private static List<UIElement> childNodes = new List<UIElement>();

        public GroupPlayPage()
        {
            InitializeComponent();
            GameLogic._userSession.player = GameStage.GROUP;
            GameLogic.getGroupInformation();
            loadPage();
        }

        private void loadPage()
        {
            Console.WriteLine(DateTime.Now + "\t" +"Loading page...");
            removeChildNodes();
            //CHECK NOTIFICATIONS AND SET APPROPRIATLY
            int notifications = GameLogic._userSession.allNotifications.Count;
            if (notifications > 0)
            {
                notificationCircle.Visibility = Visibility.Visible;
                notificationText.Text = notifications.ToString();
            }
            else
            {
                notificationCircle.Visibility = Visibility.Hidden;
            }
            //CHECK IF USER IS IN A GROUP
            GameLogic.getGroupInformation();
            Console.WriteLine(DateTime.Now + "\t" +"GOT GROUP INFORMATION");
            group = GameLogic._userSession.currentGroup;
            if (group == null)
            {
                //No Group - set UI accordingly
                setUINoGroup();
            }
            else
            {
                //Has group - set UI accourdinly
                setUIWithGroup();
            }
        }

        private void removeChildNodes()
        {
            List<KinectCircleButton> nodes = buttonDic.Keys.ToList();
            foreach (KinectCircleButton k in nodes)
            {
                groupGrid.Children.Remove(k);
            }
            foreach (UIElement u in childNodes)
            {
                groupGrid.Children.Remove(u);
            }
            buttonDic.Clear();
            childNodes.Clear();
        }

        private void setUINoGroup()
        {
            groupText.Text = "Group Play";
            
            TextBlock tb = new TextBlock();
            tb.Text = "Create Group";
            tb.TextAlignment = TextAlignment.Center;
            tb.FontSize = 28;
            Grid.SetColumn(tb, 1);
            Grid.SetRow(tb, 1);
            groupGrid.Children.Add(tb);

            childNodes.Add(tb);

            KinectCircleButton kb = new KinectCircleButton();
            Grid.SetColumn(kb, 1);
            Grid.SetRow(kb, 2);
            kb.Click += buttonClick;
            groupGrid.Children.Add(kb);
            buttonDic.Add(kb, CREATE);


        }

        private void setUIWithGroup()
        {
            groupText.Text = group.groupName;

            TextBlock tb = new TextBlock();
            tb.Text = "Invite Players";
            tb.TextAlignment = TextAlignment.Center;
            tb.FontSize = 28;
            Grid.SetColumn(tb, 0);
            Grid.SetRow(tb, 1);
            groupGrid.Children.Add(tb);

            childNodes.Add(tb);

            KinectCircleButton kb = new KinectCircleButton();
            Grid.SetColumn(kb, 0);
            Grid.SetRow(kb, 2);
            kb.Click += buttonClick;
            groupGrid.Children.Add(kb);
            buttonDic.Add(kb, INVITE);

            tb = new TextBlock();
            tb.Text = "Play";
            tb.TextAlignment = TextAlignment.Center;
            tb.FontSize = 28;
            Grid.SetColumn(tb, 1);
            Grid.SetRow(tb, 1);
            groupGrid.Children.Add(tb);

            childNodes.Add(tb);


            kb = new KinectCircleButton();
            Grid.SetColumn(kb, 1);
            Grid.SetRow(kb, 2);
            kb.Click += buttonClick;
            groupGrid.Children.Add(kb);
            buttonDic.Add(kb, PLAY);

            tb = new TextBlock();
            tb.TextAlignment = TextAlignment.Center;
            tb.FontSize = 28;
            Grid.SetColumn(tb, 2);
            Grid.SetRow(tb, 1);

            kb = new KinectCircleButton();
            Grid.SetColumn(kb, 2);
            Grid.SetRow(kb, 2);
            kb.Click += buttonClick;
            if (group.admin.Equals(GameLogic._userSession.currentUser.userJid))
            {
                tb.Text = "Delete/Leave Group";
                buttonDic.Add(kb, DELETE);
            }
            else
            {
                tb.Text = "Leave Group";
                buttonDic.Add(kb, LEAVE);
            }

            groupGrid.Children.Add(tb);
            childNodes.Add(tb);
            groupGrid.Children.Add(kb);

            tb = new TextBlock();
            tb.Text = "Group Players";
            tb.TextAlignment = TextAlignment.Center;
            tb.FontSize = 28;
            Grid.SetColumn(tb, 1);
            Grid.SetRow(tb, 4);
            groupGrid.Children.Add(tb);

            childNodes.Add(tb);

            kb = new KinectCircleButton();
            Grid.SetColumn(kb, 1);
            Grid.SetRow(kb, 5);
            kb.Click += buttonClick;
            groupGrid.Children.Add(kb);
            buttonDic.Add(kb, PLAYERS);


        }

        private void buttonClick(object sender, RoutedEventArgs e)
        {
            if (buttonDic.ContainsKey((KinectCircleButton)sender))
            {

                int x = buttonDic[(KinectCircleButton)sender];
                switch (x)
                {
                    case CREATE: createGroup();
                        break;
                    case INVITE: MainWindow.SwitchPage(new InvitePage());
                        break;
                    case DELETE: deleteGroup();
                        break;
                    case PLAY:   MainWindow.SwitchPage(new PlayPage(null));
                        break;
                    case LEAVE: leaveGroup();
                        break;
                    case PLAYERS: MainWindow.SwitchPage(new GroupPlayerListPage());
                        break;
                }
            }
            else
            {
                if (sender == notificationButton)
                {
                    MainWindow.SwitchPage(new NotificationsPage());
                }
                else if (sender == scoreboardButton)
                {
                    MainWindow.SwitchPage(new ScoreboardPage());
                }
            }
        }

        private void defaultButtonClick(object send, RoutedEventArgs e)
        {
            if ((KinectCircleButton)send == notificationButton)
            {
                MainWindow.SwitchPage(new NotificationsPage());
            }
            else if ((KinectCircleButton)send == scoreboardButton)
            {
                MainWindow.SwitchPage(new ScoreboardPage());
            }
        }

        private void leaveGroup()
        {
            GameLogic.postRemoteData(DataType.LEAVE_GROUP, null);
            loadPage();
        }

        private void createGroup()
        {
            GameLogic.postRemoteData(DataType.CREATE_GROUP, null);
            GameLogic.getRemoteData(DataType.ALL_GROUPS);
            loadPage();
        }

        private void deleteGroup()
        {
            GameLogic.postRemoteData(DataType.DELETE_GROUP, null);
            GameLogic.getGroupInformation();
            loadPage();
        }

    }
}
