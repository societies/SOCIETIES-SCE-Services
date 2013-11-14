using System;
using System.Collections;
using System.ComponentModel;
using System.Diagnostics;
using System.Drawing;
using System.Data;
using System.Windows;
using System.Runtime.InteropServices;
using System.Threading;
using System.Windows.Interop;

namespace HWUPortal
{

    /// <summary>
    /// Application Display Control
    /// </summary>
    [
    ToolboxBitmap(typeof(ApplicationControlPanel), "AppControl.bmp"),
    ]
    public class ApplicationControlPanel : System.Windows.Forms.Panel
    {
        protected static log4net.ILog log = log4net.LogManager.GetLogger(typeof(ApplicationControlPanel));

        public delegate void ApplicationExitedHandler(object sender, ApplicationControlArgs args);

        public event ApplicationExitedHandler appExit;
        /// <summary>
        /// Track if the application has been created
        /// </summary>
        bool created = false;
        Process p = null;
        /// <summary>
        /// Handle to the application Window
        /// </summary>
        IntPtr appWin;

        /// <summary>
        /// The name of the exe to launch
        /// </summary>
        private string exeName = "";


        private bool isExeRunning = false;

        public bool IsApplicationRunning
        {
            get
            {
                return this.isExeRunning;
            }
            set
            {
                this.isExeRunning = value;
            }
        }
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
        public ApplicationControlPanel()
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

        [DllImport("user32.dll", ExactSpelling = true, CharSet = CharSet.Auto)]
        public static extern IntPtr GetParent(IntPtr hWnd);

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

        /// <summary>
        /// Force redraw of control when size changes
        /// </summary>
        /// <param name="e">Not used</param>
        protected override void OnSizeChanged(EventArgs e)
        {
            this.Invalidate();
            base.OnSizeChanged(e);
        }



        //protected override void OnRenderSizeChanged(SizeChangedInfo sInfo)
        //{
        //    this.InvalidateVisual();

        //    base.OnRenderSizeChanged(sInfo);
        //}

        public void setSize(int width, int height)
        {
            this.Width = width;
            this.Height = height;
        }
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
                appWin = IntPtr.Zero;

                // Start the remote application

                try
                {
                    // Start the process
                    p = System.Diagnostics.Process.Start(this.exeName);


                    // Wait for process to be created and enter idle condition
                    bool idle = p.WaitForInputIdle();
                     Console.WriteLine(DateTime.Now + "\t" +"process entered idle state: " + idle);
                    //BUG fix for windows vista/7
                    System.Threading.Thread.Sleep(50);
                    p.OutputDataReceived += new DataReceivedEventHandler(outputDataReceived);
                    while (p.MainWindowHandle == IntPtr.Zero)
                    {
                        Thread.Sleep(1000);
                         Console.WriteLine(DateTime.Now + "\t" +"waiting for main handle");
                        p.Refresh();
                    }
                    // Get the main handle
                    appWin = p.MainWindowHandle;

                    // Put it into this form


                    SetParent(appWin, this.Handle);

                    // Remove border 
                    SetWindowLong(appWin, GWL_STYLE, WS_VISIBLE);

                    // Move the window to overlay it on this window

                    MoveWindow(appWin, 0, 0, (int)this.Width, (int)this.Height, true);

                     Console.WriteLine(DateTime.Now + "\t" +"started exe " + exeName);
                    p.Exited += new EventHandler(p_Exited);
                    p.EnableRaisingEvents = true;

                    this.isExeRunning = true;
                }
                catch (Exception ex)
                {
                    log.Error("", ex);
                    //MessageBox.Show(this, ex.Message, "Error");
                    ApplicationControlArgs cArgs = new ApplicationControlArgs(this.exeName, false);
                    this.appExit(this, cArgs);

                }



                            Console.WriteLine(DateTime.Now + "\t" +"Finished starting exe");
            }

            //base.OnVisibleChanged(e);

        }

        private void p_Exited(object sender, EventArgs args)
        {

            ApplicationControlArgs cArgs = new ApplicationControlArgs(this.exeName, true);
            appExit(this, cArgs);
        }

        private void outputDataReceived(object sender, DataReceivedEventArgs args)
        {
                        Console.WriteLine(DateTime.Now + "\t" +"Process: " + args.Data);
        }
        public void DestroyExe(EventArgs e)
        {
                        Console.WriteLine(DateTime.Now + "\t" +"Destroying exe");
            // Stop the application
            if (appWin != IntPtr.Zero)
            {
                            Console.WriteLine(DateTime.Now + "\t" +"closing down: " + appWin);
                IntPtr parentWindowHandle = new WindowInteropHelper(Application.Current.MainWindow).Handle;
                            Console.WriteLine(DateTime.Now + "\t" +"application pointer: " + parentWindowHandle);
                Window win = new Window();
                IntPtr winHandle = new WindowInteropHelper(win).Handle;
                SetParent(appWin, winHandle);
                            Console.WriteLine(DateTime.Now + "\t" +"equals parent" + (GetParent(winHandle) == parentWindowHandle));

                // Post a close message
                //PostMessage(p.MainWindowHandle, WM_CLOSE, 0, 0);
                //p.Close();
                //p.Dispose();
                // Delay for it to get the message
                System.Threading.Thread.Sleep(2000);

                PostMessage(appWin, WM_CLOSE, 0, 0);
                // Clear internal handle
                appWin = IntPtr.Zero;


                this.created = false;
                            Console.WriteLine(DateTime.Now + "\t" +"Destroyed exe" + this.exeName);
            }
            else
            {
                            Console.WriteLine(DateTime.Now + "\t" +"Could not destroy exe" + this.exeName);
            }

            //base.OnHandleDestroyed(e);

        }


        //<summary>
        //Update display of the executable
        //</summary>
        /// <summary>
        /// 
        /// </summary>
        /// <param name="e"></param>
        //<param name="e">Not used</param>

        protected override void OnResize(EventArgs e)
        {
            if (this.appWin != IntPtr.Zero)
            {
                MoveWindow(appWin, 0, 0, (int)this.Width, (int)this.Height, true);
            }
            //base.OnResize(e);
        }

        private void InitializeComponent()
        {
            this.SuspendLayout();
            // 
            // ApplicationControlPanel
            // 
            this.Size = new System.Drawing.Size(1300, 690);

            this.ResumeLayout(false);

        }


        //    protected override void ParentLayoutInvalidated(UIElement child)
        //    {
        //                    Console.WriteLine(DateTime.Now + "\t" +"ParentLayoutInvalidated");
        //        if (this.appWin != IntPtr.Zero)
        //        {
        //            MoveWindow(appWin, 0, 0, (int)this.Width, (int)this.Height, true);
        //        }
        //        base.ParentLayoutInvalidated(child);
        //    }

        //    protected override void OnRender(System.Windows.Media.DrawingContext dc)
        //    {
        //                    Console.WriteLine(DateTime.Now + "\t" +"onRender");
        //        if (this.appWin != IntPtr.Zero)
        //        {
        //            MoveWindow(appWin, 0, 0, (int)this.Width, (int)this.Height, true);
        //        }
        //        base.OnRender(dc);
        //        this.Visibility = System.Windows.Visibility.Visible;
        //        this.Focus();
        //    }



    }


}