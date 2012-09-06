using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Controls;
using System.Threading;
using System.Windows.Shapes;
namespace HWUPortal
{
    class ButtonWaitThread
    {
        Button button;
        Ellipse thumbstick;
        public ButtonWaitThread(MainWindow mainWindow, Button button, Ellipse thumbstick)
        {
            this.button = button;
            this.thumbstick = thumbstick;
        }
        public void run()
        {
            Thread.Sleep(3000);
            if (MainWindow.IsItemMidpointInContainer(button, thumbstick))
            {
                Console.WriteLine("3 secs have passed. clicking button");
                button.RaiseEvent(new System.Windows.RoutedEventArgs(Button.ClickEvent));
            }
        }
    }
}
