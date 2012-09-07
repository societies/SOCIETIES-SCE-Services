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
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using RestSharp; //commmunicating with server
using System.Threading;
using System.IO;
using ServiceStack.Text; //JSON (de)serialisation
using System.Reflection; //handling assembly reference

namespace SocialLearningGame
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        //variables to use in whole project (user details etc.)
        public static int playerScore;
        public static Student student = new Student();
        public static string baseURL = "http://societies.local.macs.hw.ac.uk:5678/laura.rest.server/"; //137.195.27.87
        public static List<Question> questionSet = new List<Question>();
        public static List<Student> allStudents = new List<Student>();
        public static List<Challenge> challenges = new List<Challenge>();
        public static List<Challenge> challengesFrom = new List<Challenge>();

        bool errored = false;

        public MainWindow()
        {
            //event handler to resolve assembly references when running as a standalone exe
            AppDomain.CurrentDomain.AssemblyResolve += new ResolveEventHandler(ResolveAssembly);
            
            InitializeComponent();

            //initialise variables
            challenges = null;
            challengesFrom = null;
            Thread questions = new Thread(new ThreadStart(getQuestionSet));
            questions.Start();
            getStudent();

            try
            {
                if (!errored)
                {
                    if (student.first == 1)
                    {
                        _mainFrame.NavigationService.Navigate(new Pages.Instruction());
                    }
                    else
                    {
                        _mainFrame.NavigationService.Navigate(new Pages.HomePage());
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error in Main Window, trying to load home page" + ex.Message);
            }
        }

        //get student HERE!
        void getStudent()
        {
            //use the socket client to find the jid for the current user
            SocketClient socketClient = new SocketClient();
            string id = socketClient.getUserIdentity();
            Console.WriteLine(id);
            //get the current user information by using a get request to the server
            //find the student and get the friends list
            //create a new rest client
            var client = new RestClient();
            //set the baseurl to be where the server is
            client.BaseUrl = baseURL;
            //get request for the student
            var request = new RestRequest("student");
            //add parameters, ID, to find the correct student
            request.AddParameter("id", id);
            try
            {
                //get the response from the server
                IRestResponse<Student> response = client.Execute<Student>(request);

                if (response.Content != null)
                {
                    //get the student details from the response
                    student.id = response.Data.id;
                    student.name = response.Data.name;
                    student.score = response.Data.score;
                    student.first = response.Data.first;
                    Console.WriteLine("first:" + student.first);
                }
                if(student.name.Equals(""))
                {
                    _mainFrame.NavigationService.Navigate(new Pages.NotRegistered());
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception: " + e.Message);
                Console.WriteLine("Error trying to get the student from the server");
                _mainFrame.NavigationService.Navigate(new Pages.NotRegistered());
                errored = true;
            }
            
            //get a list of any challenges the current player has
            Thread challenges = new Thread(new ThreadStart(getChallengesTo));
            challenges.Start();
            
            //get a list of all the students excluding the current player
            getStudentList();
            getChallengesFrom();
            
        }

        void getStudentList()
        {
            
            //create a new rest client
            var client = new RestClient();
            //set the baseurl to be where the server is
            client.BaseUrl = baseURL;
            //get request for the student
            var request = new RestRequest("student/all");
            request.AddParameter("id", student.id);
            try
            {
                var response = client.Execute<List<Student>>(request);
                allStudents = response.Data;
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception: " + e.Message);
                Console.WriteLine("Error trying to get the list of students from server");
            }
        }

        //closeWindow method which updates the server with scores etc. when the window is closed
        private void WindowClosing(object sent, System.ComponentModel.CancelEventArgs e)
        {
            updateStudent();
        }

        void updateStudent()
        {
            //create a new rest client
            var client = new RestClient();
            //set the baseurl to be where the server is
            client.BaseUrl = baseURL;
            //get request for the student
            var request = new RestRequest("student", Method.POST);
            request.RequestFormat = RestSharp.DataFormat.Json;
            string body = request.JsonSerializer.Serialize(student);
            request.AddParameter("application/json", body, ParameterType.RequestBody);
            try
            {
                var response = client.Execute(request);
                Console.WriteLine(response.Content);
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception: " + e.Message);
                Console.WriteLine("Error trying to update the student");
            }
        }


        //do all communication with the server here :) get questions elsewhere, as need to know if a category is specified
        public static void sendChallenge(Student friend, String category, int score)
        {
            //create a new challenge to send to the server
            Challenge challenge = new Challenge();
            challenge.challenger = student;
            challenge.challenged = friend;
            challenge.category = category;
            challenge.challengerScore = score;

            //create a new rest client
            var client = new RestClient();
            //set the baseurl to be where the server is
            client.BaseUrl = baseURL;
            //post request for the challenge
            var request = new RestRequest("challenge", Method.POST);
            request.RequestFormat = RestSharp.DataFormat.Json; //change the request format to JSON
            var body = request.JsonSerializer.Serialize(challenge); //serialise the challenge object to JSON object
            request.AddParameter("application/json", body, ParameterType.RequestBody); //make the serialised challenge the body of the post request
            try
            {
                var response = client.Execute<Challenge>(request);

                Console.WriteLine(response.Content);
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception: " + e.Message);
                Console.WriteLine("Error trying to send a challenge");
            }
        }

        public static void updateChallenge(Challenge challenge)
        {
            //create a new rest client
            var client = new RestClient();
            //set the baseurl to be where the server is
            client.BaseUrl = baseURL;
            //post request for the challenge
            var request = new RestRequest("challenge", Method.POST);
            request.RequestFormat = RestSharp.DataFormat.Json; //change the request format to JSON
            var body = request.JsonSerializer.Serialize(challenge); //serialise the challenge object to JSON object
            request.AddParameter("application/json", body, ParameterType.RequestBody); //make the serialised challenge the body of the post request
            try
            {
                var response = client.Execute<Challenge>(request);

                Console.WriteLine(response.Content);
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception: " + e.Message);
                Console.WriteLine("Error trying to update a challenge");
            }
        }

        public static void deleteChallenge(Challenge challenge)
        {
            //create a new rest client
            var client = new RestClient();
            //set the baseurl to be where the server is
            client.BaseUrl = baseURL;
            //post request for the challenge
            var request = new RestRequest("challenge/delete", Method.DELETE);
            request.AddParameter("id",challenge.id);
            try
            {
                var response = client.Execute(request);

                Console.WriteLine(response.Content);
                getChallengesFrom();
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception: "+ e.Message);
                Console.WriteLine("Error trying to delete a challenge");
            }
        }

        public static void getQuestionSet()
        {
            //get all the questions as a list of Question type from the server
            var client = new RestClient();
            client.BaseUrl = baseURL;
            var request = new RestRequest("question");
            try
            {
                var response = client.Execute<List<Question>>(request);
                questionSet = response.Data;
                //in case response returns some null values remove anything that does not have a question
                questionSet.RemoveAll(delegate(Question q) { return q.question == null; });
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception: " + e.Message);
                Console.WriteLine("Error trying to get the question set from server");
            }

        }

        public static void getChallengesTo()
        {
            try
            {
                var client = new RestClient();
                client.BaseUrl = baseURL;
                var request = new RestRequest("challenge");
                request.AddParameter("id", student.id);
                try
                {
                    var response = client.Execute<List<Challenge>>(request);
                    if (response == null)
                    {
                        challenges = null;
                    }
                    else
                    {
                        challenges = response.Data;
                        challenges.RemoveAll(delegate(Challenge c) { return c.challenger == null; });
                    }
                }
                catch (Exception e)
                {
                    Console.WriteLine("Exception: " + e.Message);
                    Console.WriteLine("Error trying to get a list of challenges to the user from the server");
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception:" + e.Message);
                Console.WriteLine("Error trying to get student id");
            }

            
        }

        //called to get a list of any challenges that the user has sent that have been completed
        public static void getChallengesFrom()
        {
            var client = new RestClient();
            client.BaseUrl = baseURL;
            var request = new RestRequest("challenge/from");
            request.AddParameter("id", student.id);
            try
            {
                var response = client.Execute<List<Challenge>>(request);
                if (response.Data == null)
                {
                    challengesFrom = null;
                }
                else
                {
                    challengesFrom = response.Data;
                    challengesFrom.RemoveAll(delegate(Challenge c) { return c.challenger == null; });
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception: " + e.Message);
                Console.WriteLine("Error trying to get a list of challenges from the user from server");
            }
            

        }

        //Code to get assemblies when running as a single executable
        static Assembly ResolveAssembly(object sender, ResolveEventArgs args)
        {
            Assembly parentAssembly = Assembly.GetExecutingAssembly();

            var name = args.Name.Substring(0, args.Name.IndexOf(',')) + ".dll";
            var resourceName = parentAssembly.GetManifestResourceNames()
                .First(s => s.EndsWith(name));

            using (Stream stream = parentAssembly.GetManifestResourceStream(resourceName))
            {
                byte[] block = new byte[stream.Length];
                stream.Read(block, 0, block.Length);
                return Assembly.Load(block);
            }
        }


        //richarddingwall.name/2009/05/14/wpf-how-to-combine-mutliple-assemblies-into-a-single-exe/

    }
}
