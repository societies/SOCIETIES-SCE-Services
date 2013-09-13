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
using SocialLearningGame.Entities;
using System.IO;

namespace SocialLearningGame.Comms
{
    class ServerComms
    {
        /*  protected static log4net.ILog log = log4net.LogManager.GetLogger(typeof(ServerComms));

          public string ServerURL { get; private set; }

          private readonly RestClient _client;

          //public ServerComms(String serverUrl)
          public ServerComms()
          {
              //this.ServerURL = serverUrl;
              this.ServerURL = Properties.Settings.Default.SERVER_URL;

              _client = new RestClient();
              _client.BaseUrl = ServerURL;
              _client.Timeout = 5000;
              log.Debug("REST client timeout is " + _client.Timeout + "ms");
          }

         /* public List<UserScore> ListUsers()
          {
              ////get request for the student
              //RestRequest request = new RestRequest("user/all");
              ////request.AddParameter("id", student.ID);

              //IRestResponse<List<User>> response = _client.Execute<List<User>>(request);
              //return response.Data;

              List<string[]> data = ReadSeparatedFile("users.tsv", '\t');
              log.Debug(data);
              List<UserScore> users = new List<UserScore>();
              log.Debug("Loading mock users...");
              foreach (string[] entry in data)
              {
                  UserScore user = new UserScore()
                  {
                      userJid = Int32.Parse(entry[0]),
                      name = entry[1]
                  };
                  users.Add(user);
                  log.Debug(" - " + user.ToString());
              }

              log.Debug("Loaded " + users.Count + " mock users");
              return users;

          }*/

        /*     public List<Category> ListCategories()
             {
                 ////get request for the student
                 //RestRequest request = new RestRequest("category/all");
                 ////request.AddParameter("id", student.ID);

                 //IRestResponse<List<Category>> response = _client.Execute<List<Category>>(request);
                 //return response.Data;

                 List<string[]> data = ReadSeparatedFile("categories.tsv", '\t');
                 List<Category> categories = new List<Category>();
                 log.Debug("Loading mock categories...");
                 foreach (string[] entry in data)
                 {
                     Category category = new Category()
                     {
                         ID = Int32.Parse(entry[0]),
                         Name = entry[1]
                     };
                     categories.Add(category);
                     log.Debug(" - " + category.ToString());
                 }

                 log.Debug("Loaded " + categories.Count + " mock categories");
                 return categories;

             }

             public List<Question> ListQuestions()
             {
                 ////get request for the student
                 //RestRequest request = new RestRequest("question/all");
                 ////request.AddParameter("id", student.ID);

                 //IRestResponse<List<Question>> response = _client.Execute<List<Question>>(request);
                 //return response.Data;


                 List<string[]> data = ReadSeparatedFile("questions.tsv", '\t');
                 List<Question> questions = new List<Question>();
                 log.Debug("Loading mock questions...");
                 foreach (string[] entry in data)
                 {
                     log.Debug("Entry: " + entry + " Size: " + entry.Length);
                     Question question = new Question()
                     {
                         questionID = Int32.Parse(entry[0]),
                         categoryID = Int32.Parse(entry[1]),
                         questionText = entry[2],
                         answer1 = entry[3],
                         answer2 = entry[4],
                         answer3 = entry[5],
                         answer4 = entry[6],
                         correctAnswer = Int32.Parse(entry[7])
                     };
                     questions.Add(question);
                     log.Debug(" - " + question.ToString());
                 }

                 log.Debug("Loaded " + questions.Count + " mock questions");
                 return questions;

             }

             public UserScore GetMainUser()
             {
                 return new UserScore();
             }

             private List<string[]> ReadSeparatedFile(String filename, char separator)
             {
                 List<string[]> values = new List<string[]>();

                 FileStream file = File.OpenRead(filename);
                 TextReader reader = new StreamReader(new BufferedStream(file));

                 String line;
                 while ((line = reader.ReadLine()) != null)
                 {
                     line = line.Trim();

                     // empty line
                     if (line.Length == 0)
                         continue;

                     // skip comments
                     if (line.StartsWith("#"))
                         continue;

                     string[] tokens = line.Split(separator);
                     values.Add(tokens);
                 }

                 reader.Close();
                 file.Close();

                 return values;
             }
         } */
    }
}
