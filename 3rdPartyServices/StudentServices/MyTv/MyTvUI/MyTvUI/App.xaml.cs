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

        public App()
        {
            //log4net.Config.XmlConfigurator.Configure(new FileInfo("./Resources/log4net.config.xml"));

            //log.Info("My TV onStartup");
            AppDomain.CurrentDomain.AssemblyResolve +=
                new ResolveEventHandler(ResolveAssembly);

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
