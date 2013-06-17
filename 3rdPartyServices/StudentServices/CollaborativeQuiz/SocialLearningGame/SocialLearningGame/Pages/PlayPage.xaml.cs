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
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using Microsoft.Kinect;
using Microsoft.Speech.AudioFormat;
using Microsoft.Speech.Recognition;
using SocialLearningGame.Entities;
using SocialLearningGame.Logic;
using SocialLearningGame.Pages.Components;
using SocialLearningGame.Speech;
using System.Globalization;

namespace SocialLearningGame.Pages
{
    /// <summary>
    /// Interaction logic for PlayPage.xaml
    /// </summary>
    public partial class PlayPage : Page
    {
        protected static log4net.ILog log = log4net.LogManager.GetLogger(typeof(PlayPage));

        private static readonly Uri correct = new Uri("/SocialLearningGame;component/Resources/yes.png", UriKind.Relative);
        public static readonly double SpeechConfidenceThreshold = 0.7;

        // Skeleton tracking vars
        private Skeleton[] allSkeletons = new Skeleton[8]; // NB: this is a class var for memory efficency
        private bool userInCorrectPose;
        private readonly Object poseLockObject = new Object();

        private SpeechRecognitionEngine _speechEngine;

        public PlayPage()
        {
            InitializeComponent();

            // setup speech
            MainWindow.Instance.SensorChooser.KinectChanged += new EventHandler<Microsoft.Kinect.Toolkit.KinectChangedEventArgs>(SensorChooser_KinectChanged);

            if (MainWindow.Instance.SensorChooser.Kinect != null)
            {
                KinectSensor kinect = MainWindow.Instance.SensorChooser.Kinect;
                EnableSpeechEngine(kinect);
                EnableSkeletonTracking(kinect);
            }

            // start the game
            GameLogic.NewGame();
            NextQuestion();
        }

        #region " Kinect events "

        private void SensorChooser_KinectChanged(object sender, Microsoft.Kinect.Toolkit.KinectChangedEventArgs args)
        {
            log.Debug("Kinect sensor changed");

            if (args.OldSensor != null)
            {
                log.Debug("Unbinding old sensor");

                try
                {
                    args.OldSensor.AudioSource.Stop();

                    log.Debug("Completed unbinding old sensor");
                }
                catch (InvalidOperationException ex)
                {
                    // KinectSensor might enter an invalid state while enabling/disabling streams or stream features.
                    // E.g.: sensor might be abruptly unplugged.
                    log.Warn("Error unbinding old sensor", ex);
                }
            }

            if (args.NewSensor != null)
            {
                KinectSensor newSensor = args.NewSensor;

                log.Debug("Binding new sensor");

                try
                {
                    EnableSkeletonTracking(newSensor);
                    EnableSpeechEngine(newSensor);

                    log.Debug("Completed binding new sensor");
                }
                catch (InvalidOperationException ex)
                {
                    // KinectSensor might enter an invalid state while enabling/disabling streams or stream features.
                    // E.g.: sensor might be abruptly unplugged.
                    log.Warn("Error binding new sensor", ex);
                }

            }
        }

        #endregion

        #region " Skeleton tracking "

        private void EnableSkeletonTracking(KinectSensor sensor)
        {
            /* The following is used for skeleton drawing */
            // Create the drawing group we'll use for drawing
            this.drawingGroup = new DrawingGroup();
            // Create an image source that we can use in our image control
            this.imageSource = new DrawingImage(this.drawingGroup);
            // Display the drawing using our image control
            CurrentPoseBox.Source = this.imageSource;

            // Add an event handler to be called whenever there is new color frame data
            sensor.SkeletonFrameReady += this.sensor_SensorSkeletonFrameReady;
        }

        //gets the first skeleton for the skeleton array - this skeleton is the player
        private Skeleton GetFirstSkeleton(SkeletonFrame skeletonFrameData)
        {
            if (skeletonFrameData == null)
            {
                return null;
            }

            if (skeletonFrameData.SkeletonArrayLength != allSkeletons.Length)
                allSkeletons = new Skeleton[skeletonFrameData.SkeletonArrayLength];

            skeletonFrameData.CopySkeletonDataTo(allSkeletons);

            //get the first tracked skeleton
            Skeleton first = (from s in allSkeletons
                              where s.TrackingState == SkeletonTrackingState.Tracked
                              select s).FirstOrDefault();

            return first;
        }

        private void sensor_SensorSkeletonFrameReady(object sender, SkeletonFrameReadyEventArgs e)
        {
            if (GameLogic.CurrentRound == null
                || GameLogic.CurrentRound.RequiredPose == null)
                return;

            //Get a skeleton
            Skeleton firstSkeleton;
            using (SkeletonFrame frame = e.OpenSkeletonFrame())
            {
                firstSkeleton = GetFirstSkeleton(frame);
            }

            if (firstSkeleton == null)
            {
                lock (poseLockObject)
                {
                    userInCorrectPose = false;
                }
                return;
            }

            GameLogic.CurrentRound.RequiredPose.SetCurrentJoints(
                firstSkeleton.Joints[JointType.Head],
                firstSkeleton.Joints[JointType.HandLeft],
                firstSkeleton.Joints[JointType.HandRight],
                firstSkeleton.Joints[JointType.WristLeft],
                firstSkeleton.Joints[JointType.WristRight],
                firstSkeleton.Joints[JointType.ElbowLeft],
                firstSkeleton.Joints[JointType.ElbowRight],
                firstSkeleton.Joints[JointType.HipLeft],
                firstSkeleton.Joints[JointType.HipRight],
                firstSkeleton.Joints[JointType.KneeLeft],
                firstSkeleton.Joints[JointType.KneeRight],
                firstSkeleton.Joints[JointType.ElbowLeft],
                firstSkeleton.Joints[JointType.ElbowRight]
                );


            lock (poseLockObject)
            {

                if (GameLogic.CurrentRound.AnswerMethod != AnswerMethod.BodyPoseAndSpeech)
                {
                    userInCorrectPose = false;
                }
                else
                {
                    bool nowInCorrectPose = GameLogic.CurrentRound.RequiredPose.IsPoseValid();

                    if (userInCorrectPose && !nowInCorrectPose)
                    {
                        userInCorrectPose = false;
                        UpdatePoseIconBorder();
                    }
                    else if (!userInCorrectPose && nowInCorrectPose)
                    {
                        userInCorrectPose = true;
                        UpdatePoseIconBorder();
                    }

                }
            }

#if DEBUG
            using (DrawingContext dc = this.drawingGroup.Open())
            {
                // Draw a transparent background to set the render size
                dc.DrawRectangle(Brushes.Transparent, null, new Rect(0.0, 0.0, RenderWidth, RenderHeight));

                RenderSkeleton(dc, firstSkeleton, GameLogic.CurrentRound.RequiredPose);

                // prevent drawing outside of our render area
                this.drawingGroup.ClipGeometry = new RectangleGeometry(new Rect(0.0, 0.0, RenderWidth, RenderHeight));
            }
#endif
        }

        private void UpdatePoseIconBorder()
        {
            if (!userInCorrectPose)
            {
                // no longer in correct pose
                //poseIcon.BorderBrush = Brushes.Green;
                poseIcon.BorderThickness = new Thickness(0);
            }
            else
            {
                // now in correct pose
                poseIcon.BorderBrush = Brushes.Green;
                poseIcon.BorderThickness = new Thickness(2);
            }

        }

        #region " Skeleton drawing "

        /// <summary>
        /// Thickness of drawn joint lines
        /// </summary>
        private const double JointThickness = 3;

        /// <summary>
        /// Thickness of body center ellipse
        /// </summary>
        private const double BodyCenterThickness = 10;

        /// <summary>
        /// Thickness of clip edge rectangles
        /// </summary>
        private const double ClipBoundsThickness = 10;

        /// <summary>
        /// Brush used to draw skeleton center point
        /// </summary>
        private readonly Brush centerPointBrush = Brushes.Blue;

        /// <summary>
        /// Brush used for drawing joints that are currently tracked
        /// </summary>
        private readonly Brush trackedJointBrush = new SolidColorBrush(Color.FromArgb(255, 68, 192, 68));

        /// <summary>
        /// Brush used for drawing joints that are currently inferred
        /// </summary>        
        private readonly Brush inferredJointBrush = Brushes.Yellow;

        /// <summary>
        /// Pen used for drawing bones that are currently tracked
        /// </summary>
        private readonly Pen trackedBonePen = new Pen(Brushes.Green, 6);

        /// <summary>
        /// Pen used for drawing bones that are currently inferred
        /// </summary>        
        private readonly Pen inferredBonePen = new Pen(Brushes.Gray, 1);

        /// <summary>
        /// Drawing group for skeleton rendering output
        /// </summary>
        private DrawingGroup drawingGroup;

        /// <summary>
        /// Drawing image that we will display
        /// </summary>
        private DrawingImage imageSource;


        //private double RenderHeight = this.CurrentPoseBox.Height;
        //private double RenderWidth = this.CurrentPoseBox.Width;
        private double RenderWidth = 1280;
        private double RenderHeight = 720;


        private void RenderSkeleton(DrawingContext dc, Skeleton skel, AbstractPose pose)
        {
            RenderClippedEdges(skel, dc);

            if (skel.TrackingState == SkeletonTrackingState.Tracked)
            {
                DrawBonesAndJoints(skel, dc, pose);
            }
            else if (skel.TrackingState == SkeletonTrackingState.PositionOnly)
            {
                dc.DrawEllipse(
                this.centerPointBrush,
                null,
                this.SkeletonPointToScreen(skel.Position),
                BodyCenterThickness,
                BodyCenterThickness);
            }
        }

        /// <summary>
        /// Draws indicators to show which edges are clipping skeleton data
        /// </summary>
        /// <param name="skeleton">skeleton to draw clipping information for</param>
        /// <param name="drawingContext">drawing context to draw to</param>
        private void RenderClippedEdges(Skeleton skeleton, DrawingContext drawingContext)
        {
            if (skeleton.ClippedEdges.HasFlag(FrameEdges.Bottom))
            {
                drawingContext.DrawRectangle(
                    Brushes.Red,
                    null,
                    new Rect(0, RenderHeight - ClipBoundsThickness, RenderWidth, ClipBoundsThickness));
            }

            if (skeleton.ClippedEdges.HasFlag(FrameEdges.Top))
            {
                drawingContext.DrawRectangle(
                    Brushes.Red,
                    null,
                    new Rect(0, 0, RenderWidth, ClipBoundsThickness));
            }

            if (skeleton.ClippedEdges.HasFlag(FrameEdges.Left))
            {
                drawingContext.DrawRectangle(
                    Brushes.Red,
                    null,
                    new Rect(0, 0, ClipBoundsThickness, RenderHeight));
            }

            if (skeleton.ClippedEdges.HasFlag(FrameEdges.Right))
            {
                drawingContext.DrawRectangle(
                    Brushes.Red,
                    null,
                    new Rect(RenderWidth - ClipBoundsThickness, 0, ClipBoundsThickness, RenderHeight));
            }
        }

        /// <summary>
        /// Draws a skeleton's bones and joints
        /// </summary>
        /// <param name="skeleton">skeleton to draw</param>
        /// <param name="drawingContext">drawing context to draw to</param>
        private void DrawBonesAndJoints(Skeleton skeleton, DrawingContext drawingContext, AbstractPose pose)
        {
            // Render Torso
            this.DrawBone(skeleton, drawingContext, JointType.Head, JointType.ShoulderCenter);
            this.DrawBone(skeleton, drawingContext, JointType.ShoulderCenter, JointType.ShoulderLeft);
            this.DrawBone(skeleton, drawingContext, JointType.ShoulderCenter, JointType.ShoulderRight);
            this.DrawBone(skeleton, drawingContext, JointType.ShoulderCenter, JointType.Spine);
            this.DrawBone(skeleton, drawingContext, JointType.Spine, JointType.HipCenter);
            this.DrawBone(skeleton, drawingContext, JointType.HipCenter, JointType.HipLeft);
            this.DrawBone(skeleton, drawingContext, JointType.HipCenter, JointType.HipRight);

            // Left Arm
            this.DrawBone(skeleton, drawingContext, JointType.ShoulderLeft, JointType.ElbowLeft);
            this.DrawBone(skeleton, drawingContext, JointType.ElbowLeft, JointType.WristLeft);
            this.DrawBone(skeleton, drawingContext, JointType.WristLeft, JointType.HandLeft);

            // Right Arm
            this.DrawBone(skeleton, drawingContext, JointType.ShoulderRight, JointType.ElbowRight);
            this.DrawBone(skeleton, drawingContext, JointType.ElbowRight, JointType.WristRight);
            this.DrawBone(skeleton, drawingContext, JointType.WristRight, JointType.HandRight);

            // Left Leg
            this.DrawBone(skeleton, drawingContext, JointType.HipLeft, JointType.KneeLeft);
            this.DrawBone(skeleton, drawingContext, JointType.KneeLeft, JointType.AnkleLeft);
            this.DrawBone(skeleton, drawingContext, JointType.AnkleLeft, JointType.FootLeft);

            // Right Leg
            this.DrawBone(skeleton, drawingContext, JointType.HipRight, JointType.KneeRight);
            this.DrawBone(skeleton, drawingContext, JointType.KneeRight, JointType.AnkleRight);
            this.DrawBone(skeleton, drawingContext, JointType.AnkleRight, JointType.FootRight);

            // Render Joints
            foreach (Joint joint in skeleton.Joints)
            {
                Brush drawBrush = null;
                bool correct;

                if (joint.TrackingState == JointTrackingState.Tracked && pose != null)
                {
                    switch (joint.JointType)
                    {
                        case JointType.Head:
                            correct = pose.IsHeadCorrect();
                            break;

                        case JointType.HandLeft:
                            correct = pose.IsHandLeftCorrect();
                            break;

                        case JointType.HandRight:
                            correct = pose.IsHandRightCorrect();
                            break;

                        case JointType.WristLeft:
                            correct = pose.IsWristLeftCorrect();
                            break;

                        case JointType.WristRight:
                            correct = pose.IsWristRightCorrect();
                            break;

                        case JointType.ElbowLeft:
                            correct = pose.IsElbowLeftCorrect();
                            break;

                        case JointType.ElbowRight:
                            correct = pose.IsElbowRightCorrect();
                            break;

                        case JointType.HipLeft:
                            correct = pose.IsHipLeftCorrect();
                            break;

                        case JointType.HipRight:
                            correct = pose.IsHipRightCorrect();
                            break;

                        case JointType.KneeLeft:
                            correct = pose.IsKneeLeftCorrect();
                            break;

                        case JointType.KneeRight:
                            correct = pose.IsKneeRightCorrect();
                            break;

                        case JointType.AnkleLeft:
                            correct = pose.IsAnkleLeftCorrect();
                            break;

                        case JointType.AnkleRight:
                            correct = pose.IsAnkleRightCorrect();
                            break;

                        default:
                            correct = true;
                            break;
                    }

                    drawBrush = correct ? this.trackedJointBrush : this.inferredJointBrush;
                }
                else if (joint.TrackingState == JointTrackingState.Tracked && pose == null)
                {
                    drawBrush = this.trackedJointBrush;
                }
                else if (joint.TrackingState == JointTrackingState.Inferred)
                {
                    drawBrush = this.inferredJointBrush;
                }

                if (drawBrush != null)
                {
                    Point position = this.SkeletonPointToScreen(joint.Position);

                    FormattedText formattedText = new FormattedText(
                           joint.JointType + " (" + position.X + "," + position.Y + ")",
                           CultureInfo.GetCultureInfo("en-us"),
                           FlowDirection.LeftToRight,
                           new Typeface("Verdana"),
                           10,
                           drawBrush);

                    drawingContext.DrawEllipse(drawBrush, null, position, JointThickness, JointThickness);
                    drawingContext.DrawText(formattedText, position);
                }
            }
        }

        /// <summary>
        /// Draws a bone line between two joints
        /// </summary>
        /// <param name="skeleton">skeleton to draw bones from</param>
        /// <param name="drawingContext">drawing context to draw to</param>
        /// <param name="jointType0">joint to start drawing from</param>
        /// <param name="jointType1">joint to end drawing at</param>
        private void DrawBone(Skeleton skeleton, DrawingContext drawingContext, JointType jointType0, JointType jointType1)
        {
            Joint joint0 = skeleton.Joints[jointType0];
            Joint joint1 = skeleton.Joints[jointType1];

            // If we can't find either of these joints, exit
            if (joint0.TrackingState == JointTrackingState.NotTracked ||
                joint1.TrackingState == JointTrackingState.NotTracked)
            {
                return;
            }

            // Don't draw if both points are inferred
            if (joint0.TrackingState == JointTrackingState.Inferred &&
                joint1.TrackingState == JointTrackingState.Inferred)
            {
                return;
            }

            // We assume all drawn bones are inferred unless BOTH joints are tracked
            Pen drawPen = this.inferredBonePen;
            if (joint0.TrackingState == JointTrackingState.Tracked && joint1.TrackingState == JointTrackingState.Tracked)
            {
                drawPen = this.trackedBonePen;
            }

            drawingContext.DrawLine(drawPen, this.SkeletonPointToScreen(joint0.Position), this.SkeletonPointToScreen(joint1.Position));
        }

        /// <summary>
        /// Maps a SkeletonPoint to lie within our render space and converts to Point
        /// </summary>
        /// <param name="skelpoint">point to map</param>
        /// <returns>mapped point</returns>
        private Point SkeletonPointToScreen(SkeletonPoint skelpoint)
        {
            //return new Point(skelpoint.X, skelpoint.Y);

            // Convert point to depth space.  
            // We are not using depth directly, but we do want the points in our 640x480 output resolution.
            DepthImagePoint depthPoint = MainWindow.Instance.SensorChooser.Kinect.CoordinateMapper.MapSkeletonPointToDepthPoint(skelpoint, DepthImageFormat.Resolution640x480Fps30);
            return new Point(depthPoint.X, depthPoint.Y);
        }

        #endregion

        #endregion

        #region " Voice recognition "

        //what to do if the speech is recognised
        private void SpeechRecognized(object sender, SpeechRecognizedEventArgs e)
        {
            // Speech utterance confidence below which we treat speech as if it hadn't been heard
            if (e.Result.Confidence < SpeechConfidenceThreshold)
            {
                log.Debug(String.Format("Speech below confidence level of {0}: {1} ({2})",
                    SpeechConfidenceThreshold,
                    e.Result.Text,
                    e.Result.Confidence));
                return;
            }


            String spokenText = e.Result.Text.ToUpper();

            if ("QUIT GAME".Equals(spokenText)
                || "EXIT GAME".Equals(spokenText))
            {
                log.Debug(String.Format("Quit due to voice command: {0} ({1})",
                    e.Result.Text,
                    e.Result.Confidence));
                Environment.Exit(0x00);
            }

            // if the current mode doesn't require speech, don't do any of this
            if (GameLogic.CurrentRound.AnswerMethod != AnswerMethod.Speech
                && GameLogic.CurrentRound.AnswerMethod != AnswerMethod.BodyPoseAndSpeech)
                return;

            // if the current mode is pose and speech, don't process speech unless the pose is correct
            if (GameLogic.CurrentRound.AnswerMethod == AnswerMethod.BodyPoseAndSpeech)
            {
                lock (poseLockObject)
                {
                    if (!userInCorrectPose)
                        return;
                }
            }


            if (GameLogic.CurrentRound.RequiredGrammar == Logic.Grammar.Color)
            {
                if (spokenText.Equals("GREEN"))
                    SelectAnswer(1);
                else if (spokenText.Equals("YELLOW"))
                    SelectAnswer(2);
                else if (spokenText.Equals("RED"))
                    SelectAnswer(3);
                else if (spokenText.Equals("BLUE"))
                    SelectAnswer(4);
            }
            else if (GameLogic.CurrentRound.RequiredGrammar == Logic.Grammar.Number)
            {
                if (spokenText.Equals("ONE"))
                    SelectAnswer(1);
                else if (spokenText.Equals("TWO"))
                    SelectAnswer(2);
                else if (spokenText.Equals("THREE"))
                    SelectAnswer(3);
                else if (spokenText.Equals("FOUR"))
                    SelectAnswer(4);
            }

        }

        //what to do if the speech is rejected
        private void SpeechRejected(object sender, SpeechRecognitionRejectedEventArgs e)
        {
            // do nothing
        }

        private void EnableSpeechEngine(KinectSensor sensor)
        {
            log.Debug("Enabling speech engine");

            if (_speechEngine != null)
            {
                try
                {
                    log.Debug("Stopping old speech engine");
                    _speechEngine.RecognizeAsyncStop();
                }
                catch (Exception ex)
                {
                    log.Warn("Error stopping old speech engine", ex);
                }
            }

            // Speech recognition
            try
            {
                RecognizerInfo ri = SpeechUtils.GetKinectRecognizer();
                this._speechEngine = new SpeechRecognitionEngine(ri.Id);

                // For long recognition sessions (a few hours or more), it may be beneficial to turn off adaptation of the acoustic model. 
                // This will prevent recognition accuracy from degrading over time.
                ////speechEngine.UpdateRecognizerSetting("AdaptationOn", 0);

                Choices grammar = new Choices();

                grammar.Add("red");
                grammar.Add("green");
                grammar.Add("blue");
                grammar.Add("yellow");
                grammar.Add("one");
                grammar.Add("two");
                grammar.Add("three");
                grammar.Add("four");

                grammar.Add("quit game");
                grammar.Add("exit game");

                GrammarBuilder gb = new GrammarBuilder { Culture = _speechEngine.RecognizerInfo.Culture };
                gb.Append(grammar);

                // Create the actual Grammar instance, and then load it into the speech recognizer.
                Microsoft.Speech.Recognition.Grammar g = new Microsoft.Speech.Recognition.Grammar(gb);

                _speechEngine.LoadGrammar(g);

                _speechEngine.SpeechRecognized += SpeechRecognized;
                _speechEngine.SpeechRecognitionRejected += SpeechRejected;

                this._speechEngine.SetInputToAudioStream(
                    sensor.AudioSource.Start(),
                    new SpeechAudioFormatInfo(EncodingFormat.Pcm, 16000, 16, 1, 32000, 2, null));
                this._speechEngine.RecognizeAsync(RecognizeMode.Multiple);

                log.Debug("Speech engine grammar updated");
            }
            catch (Exception ex)
            {
                this._speechEngine = null;
                log.Error("Error setting up speech engine", ex);
                return;
            }

            log.Debug("Speech engine enabled");
        }

        #endregion

        #region " Guesture tracking "

        private void answerButton_Click(object sender, RoutedEventArgs e)
        {
            if (GameLogic.CurrentRound.AnswerMethod != AnswerMethod.Guesture)
                return;

            if (sender == answerButton1)
                SelectAnswer(1);
            else if (sender == answerButton2)
                SelectAnswer(2);
            else if (sender == answerButton3)
                SelectAnswer(3);
            else if (sender == answerButton4)
                SelectAnswer(4);
        }

        #endregion

        private void NextQuestion()
        {
            userInCorrectPose = false;
            UpdatePoseIconBorder();
            //playerNameBox.Text = GameLogic.CurrentRound.

            if (GameLogic.CurrentRound != null &&
                GameLogic.CurrentRound.RoundNumber == GameLogic.QuestionsInGame)
            {
                EndGame();
                return;
            }

            QuestionRound round = GameLogic.NextQuestion();

            if (round.Question == null)
            {
                // we've run out of questions
                // TODO: Do something sensible
                return;
            }


#if DEBUG
            // TODO: Remove me after testing
            //round.AnswerMethod = AnswerMethod.BodyPoseAndSpeech;
            //log.Warn("Testing mode: AnswerMethod = " + round.AnswerMethod);
            // TODO: Remove until here

            // quick hack to skip kinect actions if in debugging mode
            if (_speechEngine == null)
            {
                log.Warn("Speech engine is null, reverting to AnswerMethod.Guesture");
                round.AnswerMethod = AnswerMethod.Guesture;
            }
            //else if (_speechEngine.AudioState == AudioState.Stopped)
            //{
            //    log.Warn("Speech engine is offline, reverting to AnswerMethod.Guesture");
            //    round.AnswerMethod = AnswerMethod.Guesture;
            //}
#endif

            questionBox.Text = round.Question.QuestionText;
            questionNumberBox.Text = GameLogic.CurrentRoundNumber + "/" + GameLogic.QuestionsInGame;

            ////set the answers in the answer boxes
            ////get answers 1-4 from corresponding question
            answerBox1.Text = round.Question.Answer1;
            answerBox2.Text = round.Question.Answer2;
            answerBox3.Text = round.Question.Answer3;
            answerBox4.Text = round.Question.Answer4;

            //set the status bar for the current answer format
            handIcon.Visibility = System.Windows.Visibility.Hidden;
            micIcon.Visibility = System.Windows.Visibility.Hidden;
            poseIcon.Visibility = System.Windows.Visibility.Hidden;

            switch (round.AnswerMethod)
            {
                case AnswerMethod.BodyPoseAndSpeech:
                    micIcon.Visibility = System.Windows.Visibility.Visible;
                    poseIcon.Visibility = System.Windows.Visibility.Visible;
                    poseIconImage.Source = new BitmapImage(round.RequiredPose.ImageUri);

                    statusBarText.Text = "Pose: " + round.RequiredPose.Name + " and ";

                    if (round.RequiredGrammar == Logic.Grammar.Color)
                    {
                        statusBarText.Text += "say \"GREEN\",\"RED\",\"YELLOW\", or \"BLUE\"";
                    }
                    else if (round.RequiredGrammar == Logic.Grammar.Number)
                    {
                        statusBarText.Text += "say \"ONE\",\"TWO\",\"THREE\", or \"FOUR\"";
                    }

                    break;
                case AnswerMethod.Speech:
                    micIcon.Visibility = System.Windows.Visibility.Visible;
                    if (round.RequiredGrammar == Logic.Grammar.Color)
                    {
                        statusBarText.Text = "Say \"GREEN\",\"RED\",\"YELLOW\", or \"BLUE\"";
                    }
                    else if (round.RequiredGrammar == Logic.Grammar.Number)
                    {
                        statusBarText.Text = "Say \"ONE\",\"TWO\",\"THREE\", or \"FOUR\"";
                    }
                    break;

                case AnswerMethod.Guesture:
                    handIcon.Visibility = System.Windows.Visibility.Visible;
                    statusBarText.Text = "Select an option by waving at the coloured circle";
                    break;
            }

            //update score box
            scoreBox.Text = GameLogic.Score.ToString();

        }

        private void SelectAnswer(int answerIndex)
        {
            if (GameLogic.CurrentRound.AnswerMethod == AnswerMethod.BodyPoseAndSpeech)
            {
                lock (poseLockObject)
                {
                    if (!userInCorrectPose)
                        return;
                }
            }

            if (answerIndex != 1) answerBox1.Text = "";
            if (answerIndex != 2) answerBox2.Text = "";
            if (answerIndex != 3) answerBox3.Text = "";
            if (answerIndex != 4) answerBox4.Text = "";

            bool result = GameLogic.UserAnsweredQuestion(answerIndex);

            // TODO: Display result
            String answer;
            switch (answerIndex)
            {
                case 1:
                    answer = GameLogic.CurrentRound.Question.Answer1;
                    break;
                case 2:
                    answer = GameLogic.CurrentRound.Question.Answer2;
                    break;
                case 3:
                    answer = GameLogic.CurrentRound.Question.Answer3;
                    break;
                case 4:
                    answer = GameLogic.CurrentRound.Question.Answer4;
                    break;
                default:
                    answer = "Unknown answer";
                    break;
            }

            bool correct = (answerIndex == GameLogic.CurrentRound.Question.CorrectAnswer);

            var selectionDisplay = new AnswerDisplay(answer, correct);
            Grid.SetRowSpan(selectionDisplay, 6);
            Grid.SetColumnSpan(selectionDisplay, 5);
            this.grid.Children.Add(selectionDisplay);

            // next round
            NextQuestion();
        }

        private void EndGame()
        {
            GameLogic.EndGame();
            MainWindow.SwitchPage(new GameOver());
        }


    }
}
