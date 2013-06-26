namespace SocialLearningGame.Pages.Components
{
    using System.Windows.Controls;
    using System.Windows.Media;

    /// <summary>
    /// Interaction logic
    /// </summary>
    public partial class AnswerDisplay : UserControl
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="SelectionDisplay"/> class. 
        /// </summary>
        /// <param name="itemId">ID of the item that was selected</param>
        public AnswerDisplay(string answer, bool correct)
        {
            this.InitializeComponent();

            this.messageTextBlock.Text = answer;

            if (correct)
            {
                this.grid.Background = Brushes.Green;
                this.correctTextBlock.Text = "Correct";
            }
            else
            {
                this.grid.Background = Brushes.Red;
                this.correctTextBlock.Text = "Incorrect";
            }

        }

        /// <summary>
        /// Called when the OnLoaded storyboard completes.
        /// </summary>
        /// <param name="sender">Event sender</param>
        /// <param name="e">Event arguments</param>
        private void OnLoadedStoryboardCompleted(object sender, System.EventArgs e)
        {
            var parent = (Panel)this.Parent;
            parent.Children.Remove(this);
        }
    }
}
