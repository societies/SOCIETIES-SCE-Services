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
using Microsoft.Kinect;
using Coding4Fun.Kinect.Wpf;
using Coding4Fun.Kinect.Wpf.Controls;
using Microsoft.Samples.Kinect.WpfViewers;
using Microsoft.Speech.AudioFormat;
using Microsoft.Speech.Recognition;
using RestSharp;

namespace SocialLearningGame.Pages
{
    /// <summary>
    /// Interaction logic for PlayPage.xaml
    /// </summary>
    public partial class PlayPage : Page
    {
        #region variables

        const int skeletonCount = 6;
        Skeleton[] allSkeletons = new Skeleton[skeletonCount];
        int score;
        int counter;
        KinectSensor sensor;
        bool closing = false;

        //random number initialised used to choose next pose
        int ranNum;
        Random random = new Random();

        //variables for changing the bitmap image source
        BitmapImage imgSource;
        Uri currentUri;

        //boolean values for determining whether each stage of answering is complete
        bool correctPose = false;
        bool answerGiven = false;
        bool pressureMat = false;

        //creating an array of different poses available
        private static Uri uri1 = new Uri("/SocialLearningGame;component/Resources/Pose1.png", UriKind.Relative);
        private static Uri uri2 = new Uri("/SocialLearningGame;component/Resources/Pose2.png", UriKind.Relative);
        private static Uri uri3 = new Uri("/SocialLearningGame;component/Resources/Pose3.png", UriKind.Relative);
        private static Uri uri4 = new Uri("/SocialLearningGame;component/Resources/Pose4.png", UriKind.Relative);
        private static Uri uri5 = new Uri("/SocialLearningGame;component/Resources/Pose5.png", UriKind.Relative);
        private static Uri uri6 = new Uri("/SocialLearningGame;component/Resources/Pose6.png", UriKind.Relative);
        private static Uri uri7 = new Uri("/SocialLearningGame;component/Resources/Pose7.png", UriKind.Relative);
        private static Uri uri8 = new Uri("/SocialLearningGame;component/Resources/Pose8.png", UriKind.Relative);
        private static Uri uri9 = new Uri("/SocialLearningGame;component/Resources/Pose9.png", UriKind.Relative);
        private static Uri uri10 = new Uri("/SocialLearningGame;component/Resources/Pose10.png", UriKind.Relative);

        Uri[] sources = { uri1, uri2, uri3, uri4, uri5, uri6, uri7, uri8, uri9, uri10 };

        private static Uri correct = new Uri("/SocialLearningGame;component/Resources/yes.png", UriKind.Relative);

        //variables for use with voice recognition
        String[] availGrammars = { "colours", "numbers" };
        private String currentGrammar;
        private SpeechRecognitionEngine speechEngine;
        //Get the voice recogniser from the kinect
        RecognizerInfo ri;

        //variable to set the current question
        Question currentQuestion = new Question();
        String category = null;
        
        bool challengebool = false;
        bool challenged = false;
        Student friend;
        Challenge currentChallenge = new Challenge();

        List<Question> questions = new List<Question>();
        #endregion variables

        #region page
        //main constructor method
        public PlayPage() 
        {
            InitializeComponent();
            //initialise variables
            score = 0;
            counter = 1;
            this.category = "all";
            getQuestionSet();
            nextQuestion();
        }

        //Constructor which takes a category as input and ensures only questions from that category are used
        public PlayPage(String category)
        {
            InitializeComponent();

            //initialise variables
            score = 0;
            counter = 1;
            this.category = category;
            getQuestionSet();
            nextQuestion();
        }

        public PlayPage(Student friend, string category)
        {
            InitializeComponent();

            score = 0;
            counter = 1;
            this.friend = friend;
            challengebool = true;
            this.category = category;
            getQuestionSet();
            nextQuestion();
        }

        public PlayPage(Challenge challenge)
        {
            InitializeComponent();

            score = 0;
            counter = 1;
            currentChallenge = challenge;
            Console.WriteLine(challenge.category);
           
            this.category = challenge.category;
            challenged = true;
            getQuestionSet();
            nextQuestion();
        }

        //what to do when the page is loaded
        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            kinectSensorChooser1.KinectSensorChanged += new DependencyPropertyChangedEventHandler(kinectSensorChooser1_KinectSensorChanged);
        }

        //what to do when the page is closing
        private void Window_Closing(object sender, RoutedEventArgs e )
        {
            //update the students total score with the score from the current game
            MainWindow.student.score = MainWindow.student.score+this.score;
            closing = true;
            StopKinect(kinectSensorChooser1.Kinect);
        }
        #endregion page

        #region kinect processing

        //called if a new kinect sensor is plugged in, i.e. if user forgot to connect the device, or was unplugged during play
        void kinectSensorChooser1_KinectSensorChanged(object sender, DependencyPropertyChangedEventArgs e)
        {
            KinectSensor old = (KinectSensor)e.OldValue;

            StopKinect(old);

            sensor = (KinectSensor)e.NewValue;

            if (sensor == null)
            {
                return;
            }

            //parameters for smoothing movement
            var parameters = new TransformSmoothParameters
            {
                Smoothing = 0.3f,
                Correction = 0.0f,
                Prediction = 0.0f,
                JitterRadius = 1.0f,
                MaxDeviationRadius = 0.5f
            };

            //Enabling the skeleton tracking comment out corresponding one, with/without smoothing
            //sensor.SkeletonStream.Enable(parameters);

            sensor.SkeletonStream.Enable();

            sensor.AllFramesReady += new EventHandler<AllFramesReadyEventArgs>(sensor_AllFramesReady);
            sensor.DepthStream.Enable(DepthImageFormat.Resolution640x480Fps30);
            sensor.ColorStream.Enable(ColorImageFormat.RgbResolution640x480Fps30);

            //try to start the sensor
            try
            {
                sensor.Start();
            }
            catch (InvalidOperationException)
            {
                kinectSensorChooser1.AppConflictOccurred();
            }

            //Get the voice recogniser from the kinect
            ri = GetKinectRecognizer();

            changeGrammar();

            
        }

        //stop the kinect called when the window is closed, or the sensor is replaced 
        private void StopKinect(KinectSensor sensor)
        {
            if (sensor != null)
            {
                if (sensor.IsRunning)
                {
                    //stop sensor 
                    sensor.Stop();

                    //stop audio if not null
                    if (sensor.AudioSource != null)
                    {
                        sensor.AudioSource.Stop();
                        this.speechEngine.RecognizeAsyncCancel();
                        this.speechEngine.RecognizeAsyncStop();
                    }
                }
            }
        }

        #endregion kinect processing

        #region pose recognition
        private void checkPose(Joint head, Joint handLeft, Joint handRight, Joint hipLeft, Joint hipRight, Joint elbowLeft, Joint elbowRight)
        {
            //Pose with both hands in the air (1)
            if (handLeft.Position.Y > head.Position.Y && handRight.Position.Y > head.Position.Y &&
                currentUri == new Uri("/SocialLearningGame;component/Resources/Pose1.png", UriKind.Relative))
            {
                //correct pose given so get a new pose (once question answered correctly)
                //set a bool variable to true
                correctPose = true;
            }
            //pose with right hand in the air and left hand down below the waist
            else if (handRight.Position.Y > head.Position.Y && handLeft.Position.Y < hipLeft.Position.Y - 0.1 &&
                currentUri == new Uri("/SocialLearningGame;component/Resources/Pose2.png", UriKind.Relative))
            {
                correctPose = true;
            }
            //pose with left hand in the air and right hand down below the waist 
            else if (handLeft.Position.Y > head.Position.Y && handRight.Position.Y < hipRight.Position.Y - 0.1 &&
                currentUri == new Uri("/SocialLearningGame;component/Resources/Pose3.png", UriKind.Relative))
            {
                correctPose = true;
            }
            //pose with both hands below the waist
            else if (handRight.Position.Y < hipRight.Position.Y - 0.1 && handLeft.Position.Y < hipLeft.Position.Y - 0.1 &&
                currentUri == new Uri("/SocialLearningGame;component/Resources/Pose4.png", UriKind.Relative))
            {
                correctPose = true;
            }
            //pose with right hand out and left hand below the waist
            else if ((handRight.Position.Y <= elbowRight.Position.Y + 0.1 && handRight.Position.Y >= elbowRight.Position.Y - 0.1) &&
                handLeft.Position.Y < hipLeft.Position.Y - 0.1 &&
                currentUri == new Uri("/SocialLearningGame;component/Resources/Pose5.png", UriKind.Relative))
            {
                correctPose = true;
            }
            //pose with left hand out and right hand below the waist
            else if ((handLeft.Position.Y <= elbowLeft.Position.Y + 0.1 && handLeft.Position.Y >= elbowLeft.Position.Y - 0.1) &&
                handRight.Position.Y < hipRight.Position.Y - 0.1 &&
                currentUri == new Uri("/SocialLearningGame;component/Resources/Pose6.png", UriKind.Relative))
            {
                correctPose = true;
            }
            //pose with both hands out (all hand and elbow joints equal on the Y axis)
            else if ((handRight.Position.Y <= elbowRight.Position.Y + 0.1 && handRight.Position.Y >= elbowRight.Position.Y - 0.1) &&
                (handLeft.Position.Y <= elbowLeft.Position.Y + 0.1 && handLeft.Position.Y >= elbowLeft.Position.Y - 0.1) &&
                currentUri == new Uri("/SocialLearningGame;component/Resources/Pose7.png", UriKind.Relative))
            {
                correctPose = true;
            }
            //leaning to the right
            else if (hipRight.Position.Y <= hipLeft.Position.Y - 0.05 && currentUri == new Uri("/SocialLearningGame;component/Resources/Pose8.png", UriKind.Relative))
            {
                correctPose = true;
            }
            //leaning to the left
            else if ((hipLeft.Position.Y <= hipRight.Position.Y - 0.05) && (currentUri == new Uri("/SocialLearningGame;component/Resources/Pose9.png", UriKind.Relative)))
            {
                correctPose = true;
            }
            //both hands together and above head 
            else if (((handRight.Position.X <= handLeft.Position.X - 0.1 || handRight.Position.X <= handLeft.Position.X + 0.1) && (handRight.Position.Y <= handLeft.Position.Y - 0.1 || handRight.Position.Y <= handLeft.Position.Y + 0.1)) &&
                (handLeft.Position.Y > head.Position.Y + 0.1 && handRight.Position.Y > head.Position.Y + 0.1) &&
                currentUri == new Uri("/SocialLearningGame;component/Resources/Pose10.png", UriKind.Relative))
            {
                correctPose = true;
            }
        }

        #endregion pose recognition

        #region questions and answers
        //set the next pose, questions and answers etc
        private void nextQuestion()
        {
            //set the next pose in the currentPose image box
            ranNum = random.Next(sources.Length); //generate a random number from 0 to the size of sources
            currentUri = sources[ranNum];
            imgSource = new BitmapImage(currentUri);
            currentPose.Source = imgSource;

            //set the next voice commands grammar to be used
            ranNum = random.Next(availGrammars.Length);

            currentGrammar = availGrammars[ranNum];

            //set the next question in the questionBox text block (random)
            ranNum = random.Next(questions.Count);
            currentQuestion = questions.ElementAt(ranNum);
            //MessageBox.Show(currentQuestion.answer1);
            questionBox.Text = currentQuestion.question;

            //set the answers in the answer boxes
            //get answers 1-4 from corresponding question
            answerBox1.Text = currentQuestion.answer1;
            answerBox2.Text = currentQuestion.answer2;
            answerBox3.Text = currentQuestion.answer3;
            answerBox4.Text = currentQuestion.answer4;

            //set the status bar for the current grammar
            if (currentGrammar.Equals("colours"))
            {
                statusBarText.Text = "Say \"GREEN\",\"RED\",\"YELLOW\", or \"BLUE\"";
            }
            else
            {
                statusBarText.Text = "Say \"ONE\",\"TWO\",\"THREE\", or \"FOUR\"";
            }

            //set boolean values back to false
            correctPose = false;
            answerGiven = false;

            //switch the grammar set to the currentGrammar
            changeGrammar();

            //update score box
            scoreBox.Text = score.ToString();

        }

        //gets the questions within the category selected by the user -- if a category has been selected
        private void getQuestionSet()
        {
            if (!category.Equals("all"))
            {
                //make the question set the questions contained only in a given category
                questions.AddRange(MainWindow.questionSet.FindAll(delegate(Question q) { return q.category.Equals(category); }));
            }
            else
            {
                questions.AddRange(MainWindow.questionSet.FindAll(delegate(Question q) { return q.category.Equals("general knowledge"); }));
                questions.AddRange(MainWindow.questionSet.FindAll(delegate(Question q) { return q.category.Equals("course related"); }));
                if (MainWindow.student.score >= 10)
                {
                    questions.AddRange(MainWindow.questionSet.FindAll(delegate(Question q) { return q.category.Equals("music"); }));
                }
                if (MainWindow.student.score >= 25)
                {
                    questions.AddRange(MainWindow.questionSet.FindAll(delegate(Question q) { return q.category.Equals("video games"); }));
                }
                if (MainWindow.student.score >= 35)
                {
                    questions.AddRange(MainWindow.questionSet.FindAll(delegate(Question q) { return q.category.Equals("sports"); }));
                }
                if (MainWindow.student.score >= 50)
                {
                    questions.AddRange(MainWindow.questionSet.FindAll(delegate(Question q) { return q.category.Equals("history"); }));
                }
                if (MainWindow.student.score >= 65)
                {
                    questions.AddRange(MainWindow.questionSet.FindAll(delegate(Question q) { return q.category.Equals("science"); }));
                }
                if (MainWindow.student.score >= 80)
                {
                    questions.AddRange(MainWindow.questionSet.FindAll(delegate(Question q) { return q.category.Equals("technology"); }));
                }
            }
        }

        #endregion questions and answers

        #region skeleton tracking

        //gets the first skeleton for the skeleton array - this skeleton is the player
        Skeleton GetFirstSkeleton(AllFramesReadyEventArgs e)
        {
            using (SkeletonFrame skeletonFrameData = e.OpenSkeletonFrame())
            {
                if (skeletonFrameData == null)
                {
                    return null;
                }

                skeletonFrameData.CopySkeletonDataTo(allSkeletons);

                //get the first tracked skeleton
                Skeleton first = (from s in allSkeletons
                                  where s.TrackingState == SkeletonTrackingState.Tracked
                                  select s).FirstOrDefault();

                return first;
            }
        }

        //method called when all frames are ready to take outside input
        void sensor_AllFramesReady(object sender, AllFramesReadyEventArgs e)
        {
            if (closing)
            {
                return;
            }

            //Get a skeleton
            Skeleton first = GetFirstSkeleton(e);

            if (first == null)
            {
                return;
            }

            GetCameraPoint(first, e);
            
            //if the correct pose has been done and an answer has been given, and the user has not completed the quiz add 1 to the question counter
            //and get the next question
            if ((correctPose && answerGiven) && counter < 4) //TODO add pressure mat boolean (&& pressureMat)
            {
                counter += 1;
                nextQuestion();
            }
            //if the user has answered all the questions return to the homepage and let the user know their score
            else if (counter == 4 && (correctPose && answerGiven))
            {
                questionBox.Text = "";
                answerBox1.Text = "";
                answerBox2.Text = "";
                answerBox3.Text = "";
                answerBox4.Text = "";
                currentPose.Source = null;
                if (!challengebool && !challenged)
                {
                    this.NavigationService.Navigate(new HomePage("Well Done! You scored " + score.ToString() + " points!"));
                }
                else if (challenged)
                {
                    try
                    {
                        this.NavigationService.Navigate(new HomePage(currentChallenge, score));
                    }
                    catch (NullReferenceException ex)
                    {
                        Console.WriteLine("Exception: " + ex);
                        Console.WriteLine("Error navigating Home");
                        this.NavigationService.Navigate(new HomePage("Sorry something went wrong"));
                    }
                }
                else
                {
                    try
                    {
                        this.NavigationService.Navigate(new HomePage(friend, category, score));
                    }
                    catch (NullReferenceException ex)
                    {
                        Console.WriteLine("Error: " + ex.Message);
                        Console.WriteLine("Error navigating Home");
                        this.NavigationService.Navigate(new HomePage("Sorry something went wrong"));
                    }
                }
            }
            //otherwise check the pose, and voice recognition is being carried out elsewhere, and take input from pressure mats
            else
            {
                //check to see if the user has completed the correct pose relating to the picture shown
                if (!correctPose)
                {
                    checkPose(first.Joints[JointType.Head], first.Joints[JointType.HandLeft], first.Joints[JointType.HandRight], first.Joints[JointType.HipLeft], first.Joints[JointType.HipRight], first.Joints[JointType.ElbowLeft], first.Joints[JointType.ElbowRight]);
                }
                else
                {
                    //let the user know the correct pose has been done by displaying a tick on screen
                    currentPose.Source = new BitmapImage(correct);
                }

                if (!pressureMat)
                {
                    //check to see whether the user is on or off the pressure mat
                }
                else
                {
                    //let the user know that the correct pressure mat action has been taken
                }
                //input from pressure mats??
            }

        }

        #endregion skeleton tracking

        #region camera
        void GetCameraPoint(Skeleton first, AllFramesReadyEventArgs e)
        {

            using (DepthImageFrame depth = e.OpenDepthImageFrame())
            {
                if (depth == null ||
                    kinectSensorChooser1.Kinect == null)
                {
                    return;
                }
            }
        }

        private void CameraPosition(FrameworkElement element, ColorImagePoint point)
        {
            //Divide by 2 for width and height so point is right in the middle 
            // instead of in top/left corner
            Canvas.SetLeft(element, point.X - element.Width / 2);
            Canvas.SetTop(element, point.Y - element.Height / 2);

        }
        #endregion camera

        #region voice recognition
        //set up the kinect to be the input source for voice recognition
        private static RecognizerInfo GetKinectRecognizer()
        {
            foreach (RecognizerInfo recognizer in SpeechRecognitionEngine.InstalledRecognizers())
            {
                string value;
                recognizer.AdditionalInfo.TryGetValue("Kinect", out value);
                if ("True".Equals(value, StringComparison.OrdinalIgnoreCase) && "en-US".Equals(recognizer.Culture.Name, StringComparison.OrdinalIgnoreCase))
                {
                    return recognizer;
                }
            }

            return null;
        }

        //to change the grammar from numbers to colours/colours to numbers
        private void changeGrammar()
        {
            if (null != ri)
            {

                this.speechEngine = new SpeechRecognitionEngine(ri.Id);

                //set the grammar to be the current option (colours or numbers)
                var grammar = new Choices();

                if (currentGrammar.Equals("colours"))
                {
                    grammar.Add("red");
                    grammar.Add("green");
                    grammar.Add("blue");
                    grammar.Add("yellow");
                }
                else
                {
                    grammar.Add("one");
                    grammar.Add("two");
                    grammar.Add("three");
                    grammar.Add("four");
                }

                var gb = new GrammarBuilder { Culture = ri.Culture };
                gb.Append(grammar);

                // Create the actual Grammar instance, and then load it into the speech recognizer.
                var g = new Grammar(gb);

                speechEngine.LoadGrammar(g);

                speechEngine.SpeechRecognized += SpeechRecognized;
                speechEngine.SpeechRecognitionRejected += SpeechRejected;

                speechEngine.SetInputToAudioStream(sensor.AudioSource.Start(), 
                    new SpeechAudioFormatInfo(EncodingFormat.Pcm, 16000, 16, 1, 32000, 2, null));
                speechEngine.RecognizeAsync(RecognizeMode.Multiple);
            }
        }

        //what to do if the speech is recognised
        private void SpeechRecognized(object sender, SpeechRecognizedEventArgs e)
        {
            // Speech utterance confidence below which we treat speech as if it hadn't been heard
            const double ConfidenceThreshold = 0.7;

            if (answerGiven == false)
            {
                if (e.Result.Confidence >= ConfidenceThreshold)
                {
                    //do something with the recognised words
                    //if the player gets the answer right and answerGiven is false change correctAnswerGiven to true
                    //if the recognised word is in the correct grammar set answerGiven to true

                    //get the recognised word
                    var word = e.Result.Text.ToUpperInvariant();
                    //if the current grammar is colours check which colour has been recognised
                    if (currentGrammar.Equals("colours"))
                    {
                        switch (word)
                        {
                            case "GREEN":
                                answerBox2.Text = "";
                                answerBox3.Text = "";
                                answerBox4.Text = "";
                                if (currentQuestion.correctAnswer.Equals(currentQuestion.answer1))
                                {
                                    score = score + 1;
                                }
                                answerGiven = true;
                                break;

                            case "RED":
                                answerBox1.Text = "";
                                answerBox3.Text = "";
                                answerBox4.Text = "";
                                if (currentQuestion.correctAnswer.Equals(currentQuestion.answer2))
                                {
                                    score = score + 1;
                                }
                                answerGiven = true;
                                break;

                            case "YELLOW":
                                answerBox1.Text = "";
                                answerBox2.Text = "";
                                answerBox4.Text = "";
                                if (currentQuestion.correctAnswer.Equals(currentQuestion.answer3))
                                {
                                    score = score + 1;
                                }
                                answerGiven = true;
                                break;

                            case "BLUE":
                                answerBox1.Text = "";
                                answerBox3.Text = "";
                                answerBox2.Text = "";
                                if (currentQuestion.correctAnswer.Equals(currentQuestion.answer4))
                                    score = score + 1;
                                answerGiven = true;
                                break;
                        }
                    }
                    //if the current grammar is numbers check which number has been recognised
                    else
                    {
                        switch (word)
                        {
                            case "ONE":
                                answerBox2.Text = "";
                                answerBox3.Text = "";
                                answerBox4.Text = "";
                                if (currentQuestion.correctAnswer.Equals(currentQuestion.answer1))
                                {
                                    score++;
                                }
                                answerGiven = true;
                                break;

                            case "TWO":
                                answerBox1.Text = "";
                                answerBox3.Text = "";
                                answerBox4.Text = "";
                                if (currentQuestion.correctAnswer.Equals(currentQuestion.answer2))
                                {
                                    score++;
                                }
                                answerGiven = true;
                                break;

                            case "THREE":
                                answerBox1.Text = "";
                                answerBox4.Text = "";
                                answerBox2.Text = "";
                                if (currentQuestion.correctAnswer.Equals(currentQuestion.answer3))
                                {
                                    score++;
                                }
                                answerGiven = true;
                                break;

                            case "FOUR":
                                answerBox1.Text = "";
                                answerBox2.Text = "";
                                answerBox3.Text = "";
                                if (currentQuestion.correctAnswer.Equals(currentQuestion.answer4))
                                {
                                    score++;
                                }
                                answerGiven = true;
                                break;
                        }
                    }
                }
            }
            else
            {
                return;
            }
        }

        //what to do if the speech is rejected
        private void SpeechRejected(object sender, SpeechRecognitionRejectedEventArgs e)
        {
            Console.WriteLine("Speech has been rejected");
        }

        #endregion voice recognition
    }
}
