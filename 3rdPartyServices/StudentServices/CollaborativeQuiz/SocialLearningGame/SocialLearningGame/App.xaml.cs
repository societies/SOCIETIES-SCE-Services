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
using System.Collections.Generic;
using System.Globalization;

namespace SocialLearningGame
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        private static Dictionary<String, Assembly> assemblyDictionary = new Dictionary<string, Assembly>();
        protected static StreamWriter writer;
       // protected static log4net.ILog log = log4net.LogManager.GetLogger(typeof(App));

    public App()

    {
        //SETTING UP CONSOLE REDIRECTION

        String userProfile = System.Environment.GetEnvironmentVariable("USERPROFILE");
        String directory = userProfile + @"\HWUPortalLogs\";


        if (!Directory.Exists(directory))
        {
            Directory.CreateDirectory(directory);
        }
        String logFilename = directory + "quiz_log.log";

        writer = new StreamWriter(logFilename);
        writer.AutoFlush = true;


        Console.SetOut(writer);
        Console.WriteLine(DateTime.Now + "\t" + "Logs are ready");
        Console.WriteLine(DateTime.Now + "\t" +"Getting assemblies");
        AppDomain.CurrentDomain.AssemblyResolve += OnResolveAssembly;

   }

 

    private static Assembly OnResolveAssembly(object sender, ResolveEventArgs args)

    {

        Assembly executingAssembly = Assembly.GetExecutingAssembly();

        AssemblyName assemblyName = new AssemblyName(args.Name);


        string path = assemblyName.Name + ".dll";
        Console.WriteLine(DateTime.Now + "\t" +"Path:" + path);

        if (assemblyName.CultureInfo.Equals(CultureInfo.InvariantCulture) == false)

        {

           path = String.Format(@"{0}\{1}", assemblyName.CultureInfo, path);

        }

 

        using (Stream stream = executingAssembly.GetManifestResourceStream(path))

        {

            if (stream == null)

                return null;

 

            byte[] assemblyRawBytes = new byte[stream.Length];

            stream.Read(assemblyRawBytes, 0, assemblyRawBytes.Length);

            return Assembly.Load(assemblyRawBytes);

        }



    }




        //richarddingwall.name/2009/05/14/wpf-how-to-combine-mutliple-assemblies-into-a-single-exe/

    }
}
