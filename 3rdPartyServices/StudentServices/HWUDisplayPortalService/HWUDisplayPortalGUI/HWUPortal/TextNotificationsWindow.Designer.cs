namespace HWUPortal
{
    partial class TextNotificationsWindow
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.activityPanel = new System.Windows.Forms.FlowLayoutPanel();
            this.SuspendLayout();
            // 
            // activityPanel
            // 
            this.activityPanel.AutoScroll = true;
            this.activityPanel.AutoSize = true;
            this.activityPanel.Dock = System.Windows.Forms.DockStyle.Fill;
            this.activityPanel.FlowDirection = System.Windows.Forms.FlowDirection.TopDown;
            this.activityPanel.Location = new System.Drawing.Point(0, 0);
            this.activityPanel.Name = "activityPanel";
            this.activityPanel.Size = new System.Drawing.Size(794, 866);
            this.activityPanel.TabIndex = 15;
            // 
            // TextNotificationsWindow
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoSize = true;
            this.ClientSize = new System.Drawing.Size(794, 866);
            this.Controls.Add(this.activityPanel);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.SizableToolWindow;
            this.MaximumSize = new System.Drawing.Size(810, 900);
            this.MinimumSize = new System.Drawing.Size(810, 146);
            this.Name = "TextNotificationsWindow";
            this.Text = "TextNotificationsWindow";
            this.Load += new System.EventHandler(this.TextNotificationsWindow_Load);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.FlowLayoutPanel activityPanel;
    }
}