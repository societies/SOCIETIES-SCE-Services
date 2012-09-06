namespace HWUPortal
{
    partial class portalGUI
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
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(portalGUI));
            this.lowerPanel = new System.Windows.Forms.Panel();
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.logoutButton = new System.Windows.Forms.Button();
            this.notifyIcon1 = new System.Windows.Forms.NotifyIcon(this.components);
            this.closeShowingServiceBtn = new System.Windows.Forms.Button();
            this.pictureBox2 = new System.Windows.Forms.PictureBox();
            this.serviceButton5 = new System.Windows.Forms.Button();
            this.serviceButton4 = new System.Windows.Forms.Button();
            this.serviceButton3 = new System.Windows.Forms.Button();
            this.serviceButton2 = new System.Windows.Forms.Button();
            this.serviceButton1 = new System.Windows.Forms.Button();
            this.lblServices = new System.Windows.Forms.Label();
            this.applicationControl = new ApplicationControl();
            this.lowerPanel.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox2)).BeginInit();
            this.SuspendLayout();
            // 
            // lowerPanel
            // 
            this.lowerPanel.BackColor = System.Drawing.SystemColors.ButtonFace;
            this.lowerPanel.Controls.Add(this.pictureBox1);
            this.lowerPanel.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.lowerPanel.Location = new System.Drawing.Point(0, 973);
            this.lowerPanel.Name = "lowerPanel";
            this.lowerPanel.Size = new System.Drawing.Size(1594, 83);
            this.lowerPanel.TabIndex = 6;
            // 
            // pictureBox1
            // 
            this.pictureBox1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.pictureBox1.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("pictureBox1.BackgroundImage")));
            this.pictureBox1.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Stretch;
            this.pictureBox1.Location = new System.Drawing.Point(3, -18);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(480, 100);
            this.pictureBox1.TabIndex = 0;
            this.pictureBox1.TabStop = false;
            // 
            // logoutButton
            // 
            this.logoutButton.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Stretch;
            this.logoutButton.Font = new System.Drawing.Font("Berlin Sans FB", 15.75F, System.Drawing.FontStyle.Italic, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.logoutButton.ForeColor = System.Drawing.SystemColors.ControlDarkDark;
            this.logoutButton.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.logoutButton.Location = new System.Drawing.Point(1320, 891);
            this.logoutButton.Name = "logoutButton";
            this.logoutButton.Size = new System.Drawing.Size(262, 76);
            this.logoutButton.TabIndex = 8;
            this.logoutButton.Text = "Log Out";
            this.logoutButton.UseVisualStyleBackColor = true;
            this.logoutButton.Visible = false;
            this.logoutButton.Click += new System.EventHandler(this.logoutButton_Click);
            // 
            // notifyIcon1
            // 
            this.notifyIcon1.Icon = ((System.Drawing.Icon)(resources.GetObject("notifyIcon1.Icon")));
            this.notifyIcon1.Text = "notifyIcon1";
            this.notifyIcon1.Visible = true;
            // 
            // closeShowingServiceBtn
            // 
            this.closeShowingServiceBtn.Font = new System.Drawing.Font("Berlin Sans FB", 15.75F, System.Drawing.FontStyle.Italic, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.closeShowingServiceBtn.ForeColor = System.Drawing.SystemColors.ControlDarkDark;
            this.closeShowingServiceBtn.Location = new System.Drawing.Point(1041, 891);
            this.closeShowingServiceBtn.Name = "closeShowingServiceBtn";
            this.closeShowingServiceBtn.Size = new System.Drawing.Size(262, 76);
            this.closeShowingServiceBtn.TabIndex = 9;
            this.closeShowingServiceBtn.Text = "Close Current Service";
            this.closeShowingServiceBtn.UseVisualStyleBackColor = true;
            this.closeShowingServiceBtn.Visible = false;
            this.closeShowingServiceBtn.Click += new System.EventHandler(this.closeShowingService_Click);
            // 
            // pictureBox2
            // 
            this.pictureBox2.BackColor = System.Drawing.Color.Transparent;
            this.pictureBox2.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox2.Image")));
            this.pictureBox2.Location = new System.Drawing.Point(-1, -1);
            this.pictureBox2.Name = "pictureBox2";
            this.pictureBox2.Size = new System.Drawing.Size(319, 127);
            this.pictureBox2.TabIndex = 10;
            this.pictureBox2.TabStop = false;
            // 
            // serviceButton5
            // 
            this.serviceButton5.BackColor = System.Drawing.SystemColors.ButtonFace;
            this.serviceButton5.Enabled = false;
            this.serviceButton5.Font = new System.Drawing.Font("Berlin Sans FB", 15.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.serviceButton5.ForeColor = System.Drawing.SystemColors.ControlDarkDark;
            this.serviceButton5.Image = ((System.Drawing.Image)(resources.GetObject("serviceButton5.Image")));
            this.serviceButton5.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.serviceButton5.Location = new System.Drawing.Point(66, 584);
            this.serviceButton5.Margin = new System.Windows.Forms.Padding(15);
            this.serviceButton5.Name = "serviceButton5";
            this.serviceButton5.Size = new System.Drawing.Size(201, 55);
            this.serviceButton5.TabIndex = 4;
            this.serviceButton5.Text = "Service 5";
            this.serviceButton5.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.serviceButton5.UseVisualStyleBackColor = false;
            this.serviceButton5.Click += new System.EventHandler(this.serviceButton5_Click);
            // 
            // serviceButton4
            // 
            this.serviceButton4.BackColor = System.Drawing.SystemColors.ButtonFace;
            this.serviceButton4.Font = new System.Drawing.Font("Berlin Sans FB", 15.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.serviceButton4.ForeColor = System.Drawing.SystemColors.ControlDarkDark;
            this.serviceButton4.Image = ((System.Drawing.Image)(resources.GetObject("serviceButton4.Image")));
            this.serviceButton4.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.serviceButton4.Location = new System.Drawing.Point(66, 499);
            this.serviceButton4.Margin = new System.Windows.Forms.Padding(15);
            this.serviceButton4.Name = "serviceButton4";
            this.serviceButton4.Size = new System.Drawing.Size(201, 55);
            this.serviceButton4.TabIndex = 3;
            this.serviceButton4.Text = "Service 4";
            this.serviceButton4.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.serviceButton4.UseVisualStyleBackColor = false;
            this.serviceButton4.Click += new System.EventHandler(this.serviceButton4_Click);
            // 
            // serviceButton3
            // 
            this.serviceButton3.BackColor = System.Drawing.SystemColors.ButtonFace;
            this.serviceButton3.Font = new System.Drawing.Font("Berlin Sans FB", 15.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.serviceButton3.ForeColor = System.Drawing.SystemColors.ControlDarkDark;
            this.serviceButton3.Image = ((System.Drawing.Image)(resources.GetObject("serviceButton3.Image")));
            this.serviceButton3.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.serviceButton3.Location = new System.Drawing.Point(66, 414);
            this.serviceButton3.Margin = new System.Windows.Forms.Padding(15);
            this.serviceButton3.Name = "serviceButton3";
            this.serviceButton3.Size = new System.Drawing.Size(201, 55);
            this.serviceButton3.TabIndex = 2;
            this.serviceButton3.Text = "Service 3";
            this.serviceButton3.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.serviceButton3.UseVisualStyleBackColor = false;
            this.serviceButton3.Click += new System.EventHandler(this.serviceButton3_Click);
            // 
            // serviceButton2
            // 
            this.serviceButton2.BackColor = System.Drawing.SystemColors.ButtonFace;
            this.serviceButton2.Font = new System.Drawing.Font("Berlin Sans FB", 15.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.serviceButton2.ForeColor = System.Drawing.SystemColors.ControlDarkDark;
            this.serviceButton2.Image = ((System.Drawing.Image)(resources.GetObject("serviceButton2.Image")));
            this.serviceButton2.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.serviceButton2.Location = new System.Drawing.Point(66, 329);
            this.serviceButton2.Margin = new System.Windows.Forms.Padding(15);
            this.serviceButton2.Name = "serviceButton2";
            this.serviceButton2.Size = new System.Drawing.Size(201, 55);
            this.serviceButton2.TabIndex = 1;
            this.serviceButton2.Text = "Service 2";
            this.serviceButton2.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.serviceButton2.UseVisualStyleBackColor = false;
            this.serviceButton2.Click += new System.EventHandler(this.serviceButton2_Click);
            // 
            // serviceButton1
            // 
            this.serviceButton1.BackColor = System.Drawing.SystemColors.ButtonFace;
            this.serviceButton1.Font = new System.Drawing.Font("Berlin Sans FB", 15.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.serviceButton1.ForeColor = System.Drawing.SystemColors.ControlDarkDark;
            this.serviceButton1.Image = ((System.Drawing.Image)(resources.GetObject("serviceButton1.Image")));
            this.serviceButton1.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.serviceButton1.Location = new System.Drawing.Point(66, 244);
            this.serviceButton1.Margin = new System.Windows.Forms.Padding(15);
            this.serviceButton1.Name = "serviceButton1";
            this.serviceButton1.Size = new System.Drawing.Size(201, 55);
            this.serviceButton1.TabIndex = 0;
            this.serviceButton1.Text = "Service 1";
            this.serviceButton1.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.serviceButton1.UseVisualStyleBackColor = false;
            this.serviceButton1.Click += new System.EventHandler(this.serviceButton1_Click);
            // 
            // lblServices
            // 
            this.lblServices.AutoSize = true;
            this.lblServices.BackColor = System.Drawing.Color.Transparent;
            this.lblServices.Font = new System.Drawing.Font("Berlin Sans FB", 21.75F, System.Drawing.FontStyle.Italic, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblServices.ForeColor = System.Drawing.SystemColors.GradientActiveCaption;
            this.lblServices.Location = new System.Drawing.Point(51, 171);
            this.lblServices.Name = "lblServices";
            this.lblServices.Size = new System.Drawing.Size(230, 32);
            this.lblServices.TabIndex = 14;
            this.lblServices.Text = "Available Services";
            this.lblServices.Visible = false;
            // 
            // applicationControl
            // 
            this.applicationControl.BackColor = System.Drawing.SystemColors.Control;
            this.applicationControl.ExeName = "";
            this.applicationControl.Location = new System.Drawing.Point(317, 47);
            this.applicationControl.Name = "applicationControl";
            this.applicationControl.Size = new System.Drawing.Size(1265, 822);
            this.applicationControl.TabIndex = 11;
            this.applicationControl.Visible = false;
            // 
            // portalGUI
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.SystemColors.ControlDarkDark;
            this.ClientSize = new System.Drawing.Size(1594, 1056);
            this.Controls.Add(this.lblServices);
            this.Controls.Add(this.logoutButton);
            this.Controls.Add(this.closeShowingServiceBtn);
            this.Controls.Add(this.applicationControl);
            this.Controls.Add(this.pictureBox2);
            this.Controls.Add(this.lowerPanel);
            this.Controls.Add(this.serviceButton5);
            this.Controls.Add(this.serviceButton4);
            this.Controls.Add(this.serviceButton3);
            this.Controls.Add(this.serviceButton2);
            this.Controls.Add(this.serviceButton1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
            this.Name = "portalGUI";
            this.Text = "Service Portal";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.portalGUI_Closed);
            this.lowerPanel.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox2)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button serviceButton1;
        private System.Windows.Forms.Button serviceButton2;
        private System.Windows.Forms.Button serviceButton3;
        private System.Windows.Forms.Button serviceButton4;
        private System.Windows.Forms.Button serviceButton5;
        private System.Windows.Forms.PictureBox pictureBox1;
        private System.Windows.Forms.Panel lowerPanel;
        private System.Windows.Forms.Button logoutButton;
        private System.Windows.Forms.Button closeShowingServiceBtn;
        private System.Windows.Forms.PictureBox pictureBox2;
        private ApplicationControl applicationControl;
        private System.Windows.Forms.NotifyIcon notifyIcon1;
        private System.Windows.Forms.Label lblServices;
    }
}

