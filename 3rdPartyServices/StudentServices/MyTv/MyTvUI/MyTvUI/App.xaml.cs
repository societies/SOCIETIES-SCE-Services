using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Windows;
using System.Windows.Threading;
using System.Reflection;
using System;
using System.IO;

namespace MyTvUI
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        public App()
        {
            Console.WriteLine("My TV onStartup");
            AppDomain.CurrentDomain.AssemblyResolve +=
                new ResolveEventHandler(ResolveAssembly);

            // proceed starting app...
        }

        static Assembly ResolveAssembly(object sender, ResolveEventArgs args)
        {
            Assembly parentAssembly = Assembly.GetExecutingAssembly();

            var name = args.Name.Substring(0, args.Name.IndexOf(',')) + ".dll";

            Console.WriteLine("Searching for dependency: " + name.ToString() + " in ->");
            string[] resourceList = parentAssembly.GetManifestResourceNames();
            for (int i = 0; i < resourceList.Length; i++)
            {
                Console.WriteLine(resourceList[i]);
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
            Console.WriteLine("Application exit event recieved");
        }
    }
}
