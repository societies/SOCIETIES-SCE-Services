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
using SocialLearningGame.Comms;
using Microsoft.Kinect.Toolkit.Controls;
using System.Windows.Controls.Primitives;


namespace SocialLearningGame.Pages
{
    /// <summary>
    /// Interaction logic for Page1.xaml
    /// </summary>
    public partial class NotificationsPage : Page
    {

        private static Dictionary<KinectCircleButton, PendingJoins> acceptDic = new Dictionary<KinectCircleButton, PendingJoins>();
        private static Dictionary<KinectCircleButton, PendingJoins> declineDic = new Dictionary<KinectCircleButton, PendingJoins>();
        private static List<UIElement> childNodes = new List<UIElement>();
        private List<PendingJoins> notifcations;
        private static Boolean first = false;

        private static int index;
        private static int maxCount;

        public NotificationsPage()
        {
            InitializeComponent();
            
            if (GameLogic._userSession.allNotifications.Count() > 5)
            {
                nextButton.Visibility = Visibility.Visible;
            }
            showNotifications();
            index = 0;
        }

        private void showNotifications()
        {
            notifcations = GameLogic._userSession.allNotifications;

            foreach (UIElement u in childNodes)
            {
                notGrid.Children.Remove(u);
            }
            TextBlock tb = new TextBlock();
            tb.Text = "Refreshing notifications";
            tb.FontSize = 30;
            Grid.SetRow(tb, 1);
            Grid.SetColumn(tb, 2);
            notGrid.Children.Add(tb);
            
            maxCount = notifcations.Count();
            notGrid.Children.Remove(tb);
            //IF ONE WAS JUST DELETED THAT WAS ON ITS ON PAGE
            if (index == maxCount)
            {
                Console.WriteLine(DateTime.Now + "\t" +"IT WAS ONE ON THE APGE");
                index = index - 5;
                if (index == 0)
                {
                    backButton.Visibility = Visibility.Hidden;
                }
            }

            int row = 1;
            PendingJoins join;
            int x;
            for (x = index; x < index + 5; x++)
            {
                if (x < maxCount && maxCount!=0)
                {
                    join = notifcations[x];
                    //CREATE TEXT BLOCK
                    tb = new TextBlock();
                    tb.Text = getName(join.fromUser) + " invited you to join  " + join.groupName;
                    tb.FontSize = 28;
                    Grid.SetColumnSpan(tb, 2);
                    Grid.SetColumn(tb, 0);
                    Grid.SetRow(tb, row);
                    notGrid.Children.Add(tb);
                    //CREATE ACCEPT BUTTON
                    KinectCircleButton kb = new KinectCircleButton();
                    kb.Click += kb_AcceptClick;
                    Grid.SetColumn(kb, 2);
                    Grid.SetRow(kb, row);
                    notGrid.Children.Add(kb);
                    acceptDic.Add(kb, join);
                    childNodes.Add(kb);
                    //CREATE DECLINE BUTTON
                    kb = new KinectCircleButton();
                    kb.Click += kb_DeclineClick;
                    Grid.SetColumn(kb, 3);
                    Grid.SetRow(kb, row);
                    notGrid.Children.Add(kb);
                    declineDic.Add(kb, join);
                    childNodes.Add(tb);
                    childNodes.Add(kb);
                    row++;
                }
            }
        }

        private void kb_DeclineClick(object sender, RoutedEventArgs e)
        {
            PendingJoins join = declineDic[(KinectCircleButton)sender];
            GameLogic.postRemoteData(DataType.DELETE_NOTIFICATION, join);
            GameLogic._userSession.allNotifications.Remove(join);
            acceptDic.Clear();
            declineDic.Clear();
            showNotifications();
            

            
        }

        void kb_AcceptClick(object sender, RoutedEventArgs e)
        {
            Console.WriteLine(DateTime.Now + "\t" +"Accept button clicked");
            PendingJoins join = acceptDic[(KinectCircleButton)sender];
            Console.WriteLine(DateTime.Now + "\t" +"Got join: " + join.fromUser);
            GameLogic.postRemoteData(DataType.ACCEPT_NOTIFICATION, join.groupName);
            GameLogic.postRemoteData(DataType.DELETE_NOTIFICATION, join);
            Console.WriteLine(DateTime.Now + "\t" +"Added user to group!!!");
            declineDic.Clear();
            acceptDic.Clear();
            GameLogic._userSession.allNotifications.Remove(join);
            MainWindow.SwitchPage(new GroupPlayPage());
            
        }

        private String getName(String jid)
        {
            return jid.Split( new char[] {'.'} , 2)[0];
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
            showNotifications();
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
            showNotifications();
        }
    }
}
