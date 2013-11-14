using System;
using System.Collections;
using System.ComponentModel;
using System.Diagnostics;
using System.Drawing;
using System.Data;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using System.Threading;
namespace HWUPortal
{
    public class StandaloneAppControl
    {
        protected static log4net.ILog log = log4net.LogManager.GetLogger(typeof(StandaloneAppControl));

        public delegate void ApplicationExitedHandler(object sender, ApplicationControlArgs args);

        public event ApplicationExitedHandler appExit;
        /// <summary>
        /// Track if the application has been created
        /// </summary>
        bool created = false;

        /// <summary>
        /// Handle to the application Window
        /// </summary>
        IntPtr appWin;

        /// <summary>
        /// The name of the exe to launch
        /// </summary>
        private string exeName = "";

        /// <summary>
        /// Get/Set if we draw the tick marks
        /// </summary>
        [
        Category("Data"),
        Description("Name of the executable to launch"),
        DesignerSerializationVisibility(DesignerSerializationVisibility.Visible)
        ]
        public string ExeName
        {
            get
            {
                return exeName;
            }
            set
            {
                exeName = value;
            }
        }


        /// <summary>
        /// Constructor
        /// </summary>
        public StandaloneAppControl()
        {
        }


        [DllImport("user32.dll", EntryPoint = "GetWindowThreadProcessId", SetLastError = true,
             CharSet = CharSet.Unicode, ExactSpelling = true,
             CallingConvention = CallingConvention.StdCall)]
        private static extern long GetWindowThreadProcessId(long hWnd, long lpdwProcessId);

        [DllImport("user32.dll", SetLastError = true)]
        private static extern IntPtr FindWindow(string lpClassName, string lpWindowName);

        [DllImport("user32.dll", SetLastError = true)]
        private static extern long SetParent(IntPtr hWndChild, IntPtr hWndNewParent);

        [DllImport("user32.dll", EntryPoint = "GetWindowLongA", SetLastError = true)]
        private static extern long GetWindowLong(IntPtr hwnd, int nIndex);

        [DllImport("user32.dll", EntryPoint = "SetWindowLongA", SetLastError = true)]
        private static extern long SetWindowLong(IntPtr hwnd, int nIndex, uint dwNewLong);

        [DllImport("user32.dll", SetLastError = true)]
        private static extern long SetWindowPos(IntPtr hwnd, long hWndInsertAfter, long x, long y, long cx, long cy, long wFlags);

        [DllImport("user32.dll", SetLastError = true)]
        private static extern bool MoveWindow(IntPtr hwnd, int x, int y, int cx, int cy, bool repaint);

        [DllImport("user32.dll", EntryPoint = "PostMessageA", SetLastError = true)]
        private static extern bool PostMessage(IntPtr hwnd, uint Msg, uint wParam, uint lParam);

        private const int SWP_NOOWNERZORDER = 0x200;
        private const int SWP_NOREDRAW = 0x8;
        private const int SWP_NOZORDER = 0x4;
        private const int SWP_SHOWWINDOW = 0x0040;
        private const int WS_EX_MDICHILD = 0x40;
        private const int SWP_FRAMECHANGED = 0x20;
        private const int SWP_NOACTIVATE = 0x10;
        private const int SWP_ASYNCWINDOWPOS = 0x4000;
        private const int SWP_NOMOVE = 0x2;
        private const int SWP_NOSIZE = 0x1;
        private const int GWL_STYLE = (-16);
        private const int WS_VISIBLE = 0x10000000;
        private const int WM_CLOSE = 0x10;
        private const int WS_CHILD = 0x40000000;

        Process p = null;

        /// <summary>
        /// Creeate control when visibility changes
        /// </summary>
        /// <param name="e">Not used</param>
        public void LoadExe(EventArgs e)
        {
             Console.WriteLine(DateTime.Now + "\t" +"Starting exe" + exeName);
            // If control needs to be initialized/created
            if (created == false)
            {

                // Mark that control is created
                created = true;

                // Initialize handle value to invalid
                this.appWin = IntPtr.Zero;

                // Start the remote application

                try
                {
                    // Start the process
                    p = System.Diagnostics.Process.Start(this.exeName);

                    p.Exited += p_Exited;
                    p.EnableRaisingEvents = true;
                    // Wait for process to be created and enter idle condition
                    p.WaitForInputIdle();

                    //BUG fix for windows vista/7
                    System.Threading.Thread.Sleep(50);
                   
                    while (p.MainWindowHandle == IntPtr.Zero)
                    {
                        Thread.Sleep(1000);
                         Console.WriteLine(DateTime.Now + "\t" +"waiting for main handle");
                        p.Refresh();
                    }
                    // Get the main handle
                    this.appWin = p.MainWindowHandle;
                    if (this.appWin == IntPtr.Zero)
                    {
                        Console.WriteLine("AppWin is bad!");
                    }
                }
                catch (Exception ex)
                {
                    log.Error("", ex);
                }

                // Put it into this form
                //SetParent(appWin, this.Handle);

                // Remove border 
                //SetWindowLong(appWin, GWL_STYLE, WS_VISIBLE);

                // Move the window to overlay it on this window
                //MoveWindow(appWin, 0, 0, this.Width, this.Height, true);
                 Console.WriteLine(DateTime.Now + "\t" +"started exe" + exeName);

            }


        }
        private void p_Exited(object sender, EventArgs args)
        {
            Console.WriteLine("Received exit event");
            ApplicationControlArgs cArgs = new ApplicationControlArgs(this.exeName, true);
            appExit(this, cArgs);

            this.created = false;
            this.appWin = IntPtr.Zero;
        }


        public void DestroyExe(EventArgs e)
        {
            Console.WriteLine("Destroying exe " + p.ProcessName);
            if (appWin == IntPtr.Zero)
            {
                Console.WriteLine("Booo2!");
            }
            //REMOVE EVENT HANDLER
            p.Exited -= p_Exited;
            // Stop the application
            if (appWin == IntPtr.Zero)
            {
                Console.WriteLine("Booo!");
            }

                Console.WriteLine("appWin has not been set properly!");
                if (p != null && !p.HasExited)
                {
                    if (this.appWin != IntPtr.Zero)
                    {
                        Console.WriteLine("p is null and p has not been started appparently!");
                        try
                        {
                            Console.WriteLine("Closing main window" + p.ProcessName);

                            p.CloseMainWindow();

                            //Wait 2s to exit.
                            p.WaitForExit(2000);

                            if (!p.HasExited)
                            {
                                Console.WriteLine("Closing down process");
                                //p.Close();


                                Console.WriteLine("process has not exited yet");

                                Console.WriteLine("killing process");
                                p.Kill();
                                p.WaitForExit();
                                Console.WriteLine("killed process");



                            }
                            else
                            {
                                //CLOSE LOCAL RESOURCES HELD
                                p.Close();
                            }
                            Console.WriteLine("Closed");
                        }
                        catch (Exception exc)
                        {
                            Console.WriteLine("", exc);
                        }
                    }
                    else
                    {
                        p.Kill();
                        p.WaitForExit();
                    }
                // Post a close message
                //PostMessage(appWin, WM_CLOSE, 0, 0);

                // Delay for it to get the message
                //System.Threading.Thread.Sleep(1000);

                // Clear internal handle
                this.appWin = IntPtr.Zero;
                this.created = false;
                 Console.WriteLine(DateTime.Now + "\t" +"Destroyed exe" + this.exeName);
            }
            else
            {
                            Console.WriteLine(DateTime.Now + "\t" +"Could not destroy exe" + this.exeName);
            }


        }


    }


}