using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Threading;

namespace HWUPortal
{
    public partial class ImageViewer : Form
    {
        
        public ImageViewer()
        {
            InitializeComponent();
            //binDataTransfer = new BinaryDataTransfer();
            //Thread transferThread = new Thread(binDataTransfer.run);
            //transferThread.Start();
        }

        private void TestForm_Load(object sender, EventArgs e)
        {

        }

        internal delegate void setImageDelegate(String location);

        internal void setImage(String location)
        {
            if (this.pictureBox.InvokeRequired)
            {
                this.pictureBox.Invoke(new setImageDelegate(this.setImage), location);
            }
            else
            {
                this.pictureBox.ImageLocation = location;

            }
        }

    }
}
