using System;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Windows;

namespace MyTvUI
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
      //  private static readonly log4net.ILog log = log4net.LogManager.GetLogger(typeof(App));
        protected static StreamWriter writer;
        public App()
        {
            //log4net.Config.XmlConfigurator.Configure(new FileInfo("./Resources/log4net.config.xml"));

            //log.Info("My TV onStartup");
            AppDomain.CurrentDomain.AssemblyResolve +=
                new ResolveEventHandler(ResolveAssembly);

            //set up to redirect console logs to file
            String userProfile = System.Environment.GetEnvironmentVariable("USERPROFILE");
            String directory = userProfile + @"\HWUPortalLogs\";


            if (!Directory.Exists(directory))
            {
                Directory.CreateDirectory(directory);
            }
            String logFilename = directory + "mytv_log.log";

            writer = new StreamWriter(logFilename, true);
            writer.AutoFlush = true;


            Console.SetOut(writer);
            Console.WriteLine(DateTime.Now + "\t" + "Logs are ready");

            // proceed starting app...
        }

        static Assembly ResolveAssembly(object sender, ResolveEventArgs args)
        {
            Assembly parentAssembly = Assembly.GetExecutingAssembly();

            var name = args.Name.Substring(0, args.Name.IndexOf(',')) + ".dll";

            //log.Info("Searching for dependency: " + name.ToString() + " in ->");
            string[] resourceList = parentAssembly.GetManifestResourceNames();
            for (int i = 0; i < resourceList.Length; i++)
            {
                Console.WriteLine(resourceList[i]);
                //log.Info(resourceList[i]);
            }

            var resourceName = parentAssembly.GetManifestResourceNames()
                .First(s => s.EndsWith(name));

            using (Stream stream = parentAssembly.GetManifestResourceStream(resourceName))
            {
                byte[] block = new byte[stream.Length];
                stream.Read(block, 0, block.Length);
                return Assembly.Load(block);
            }
        }

        private void Application_Exit(object sender, ExitEventArgs e)
        {
            //log.Info("Application exit event recieved");
        }
    }
}
