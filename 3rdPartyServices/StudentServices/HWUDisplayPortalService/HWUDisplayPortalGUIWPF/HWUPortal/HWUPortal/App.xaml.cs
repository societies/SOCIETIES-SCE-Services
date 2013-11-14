/* 
 * Copyright (coffee) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

using System;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Windows;

namespace HWUPortal
{

    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        protected static log4net.ILog log = log4net.LogManager.GetLogger(typeof(App));
        protected static StreamWriter writer;
        public App()
            : base()
        {
             Console.WriteLine(DateTime.Now + "\t" +"Display Portal Starting up");
            AppDomain.CurrentDomain.AssemblyResolve +=
                new ResolveEventHandler(ResolveAssembly);

            // proceed starting app...
            log4net.Config.XmlConfigurator.Configure(new System.IO.FileInfo("./Resources/log4net.config.xml"));
            log.Info("Logging configured");

            //SETTING UP CONSOLE REDIRECTION

            String userProfile = System.Environment.GetEnvironmentVariable("USERPROFILE");
            String directory = userProfile + @"\HWUPortalLogs\";


            if (!Directory.Exists(directory))
            {
                Directory.CreateDirectory(directory);
            }
            String logFilename = directory + "portal_log.log";

            writer = new StreamWriter(logFilename);
            writer.AutoFlush = true;

           
           Console.SetOut(writer);
           Console.WriteLine(DateTime.Now + "\t" + "Logs are ready");
            


        }

        private static Assembly ResolveAssembly(object sender, ResolveEventArgs args)
        {
            Assembly parentAssembly = Assembly.GetExecutingAssembly();

            var name = args.Name.Substring(0, args.Name.IndexOf(',')) + ".dll";

            // Console.WriteLine(DateTime.Now + "\t" +"Searching for dependency: " + name.ToString() + " in ->");

            //string[] resourceList = parentAssembly.GetManifestResourceNames();
            //for (int i = 0; i < resourceList.Length; i++)
            //{
            //     Console.WriteLine(DateTime.Now + "\t" +resourceList[i]);
            //}

            var resourceName = parentAssembly.GetManifestResourceNames()
                .First(s => s.EndsWith(name));

            // Console.WriteLine(DateTime.Now + "\t" +"Found resource " + resourceName);

            using (Stream stream = parentAssembly.GetManifestResourceStream(resourceName))
            {
                byte[] block = new byte[stream.Length];
                stream.Read(block, 0, block.Length);
                return Assembly.Load(block);
            }
        }

        //richarddingwall.name/2009/05/14/wpf-how-to-combine-mutliple-assemblies-into-a-single-exe/

        private void Application_Exit(object sender, ExitEventArgs e)
        {
             Console.WriteLine(DateTime.Now + "\t" +"Portal exiting");
        }
    }
}
