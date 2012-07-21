namespace HWUPortal
{
    partial class NotificationPanel
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
            this.notificationsPanel = new System.Windows.Forms.Panel();
            this.lblNotifications = new System.Windows.Forms.Label();
            this.txtNofications = new System.Windows.Forms.RichTextBox();
            this.notificationsPanel.SuspendLayout();
            this.SuspendLayout();
            // 
            // notificationsPanel
            // 
            this.notificationsPanel.BackColor = System.Drawing.SystemColors.ActiveCaption;
            this.notificationsPanel.Controls.Add(this.lblNotifications);
            this.notificationsPanel.Controls.Add(this.txtNofications);
            this.notificationsPanel.Dock = System.Windows.Forms.DockStyle.Fill;
            this.notificationsPanel.Location = new System.Drawing.Point(0, 0);
            this.notificationsPanel.Name = "notificationsPanel";
            this.notificationsPanel.Size = new System.Drawing.Size(780, 146);
            this.notificationsPanel.TabIndex = 15;
            // 
            // lblNotifications
            // 
            this.lblNotifications.AutoSize = true;
            this.lblNotifications.Font = new System.Drawing.Font("Berlin Sans FB", 21.75F, System.Drawing.FontStyle.Italic, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblNotifications.ForeColor = System.Drawing.SystemColors.ControlDarkDark;
            this.lblNotifications.Location = new System.Drawing.Point(6, 4);
            this.lblNotifications.Name = "lblNotifications";
            this.lblNotifications.Size = new System.Drawing.Size(230, 32);
            this.lblNotifications.TabIndex = 13;
            this.lblNotifications.Text = "Your Notifications";
            // 
            // txtNofications
            // 
            this.txtNofications.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.txtNofications.Location = new System.Drawing.Point(0, 42);
            this.txtNofications.Margin = new System.Windows.Forms.Padding(30);
            this.txtNofications.Name = "txtNofications";
            this.txtNofications.ReadOnly = true;
            this.txtNofications.Size = new System.Drawing.Size(781, 104);
            this.txtNofications.TabIndex = 12;
            this.txtNofications.Text = "";
            // 
            // NotificationPanel
            // 
            this.ClientSize = new System.Drawing.Size(781, 146);
            this.Controls.Add(this.notificationsPanel);
            this.Name = "NotificationPanel";
            this.Text = "NotificationPanel";
            this.notificationsPanel.ResumeLayout(false);
            this.notificationsPanel.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Panel notificationsPanel;
        private System.Windows.Forms.Label lblNotifications;
        private System.Windows.Forms.RichTextBox txtNofications;

    }
}