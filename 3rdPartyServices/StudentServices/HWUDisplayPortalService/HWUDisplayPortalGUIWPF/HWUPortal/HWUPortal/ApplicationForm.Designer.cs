namespace HWUPortal
{
    partial class ApplicationForm
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
            this.appControlPanel = new HWUPortal.ApplicationControlPanel();
            this.SuspendLayout();
            // 
            // appControlPanel
            // 
            this.appControlPanel.Dock = System.Windows.Forms.DockStyle.Top;
            this.appControlPanel.ExeName = "";
            this.appControlPanel.IsApplicationRunning = false;
            this.appControlPanel.Location = new System.Drawing.Point(0, 0);
            this.appControlPanel.Name = "appControlPanel";
            this.appControlPanel.Size = new System.Drawing.Size(1324, 690);
            this.appControlPanel.TabIndex = 0;
            this.appControlPanel.Leave += new System.EventHandler(this.appControlPanel_Leave);
            // 
            // ApplicationForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1324, 662);
            this.Controls.Add(this.appControlPanel);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.Name = "ApplicationForm";
            this.Text = "ApplicationForm";
            this.ResumeLayout(false);

        }

        #endregion

        private HWUPortal.ApplicationControlPanel appControlPanel;
    }
}