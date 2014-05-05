package ac.hw.services.collabquiz.comms;


import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import ac.hw.services.collabquiz.ICollabQuizServer;




import com.google.gson.Gson;



public class CommsServerListener implements Runnable {

	//LOGGER
	private static final Logger log = LoggerFactory.getLogger(CommsServerListener.class);

	//SOCKET DATA
	private ServerSocket serverSocket;
	private int port;
	private String address;
	private boolean running;
	
	private ICollabQuizServer collabQuiz;








	public CommsServerListener(ICollabQuizServer collabQuiz)
	{
		this.collabQuiz = collabQuiz;
		this.running = true;
		init();
	}

	public void run() {
		listen();		
	}
	
	public void kill() {
		this.running = false;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init() {
		try {
			this.serverSocket = new ServerSocket(0);
			this.port = this.serverSocket.getLocalPort();
			this.address = this.serverSocket.getInetAddress().getLocalHost().getHostAddress();
			this.serverSocket.close();
			log.debug("Socket will listen on: " +address+":"+port);
		} catch (IOException e) {
			log.debug("Error when trying to get port and address!");
		}
	}

	public void listen() {
		try {
			serverSocket = new ServerSocket(port);
		
			while(this.running)
			{
				
				log.debug("Listening for connection from GUI");

				Socket clientSocket = serverSocket.accept();
				log.debug("Connection made");
				new Thread(new CommsServerAction(this.collabQuiz, clientSocket)).start();//clientSocket, userAnswerRepo, userScoreRepo, questionRepo, categoryRepo, userGroupsRepo, groupsRepo, pendingJoinsRepo)).start();
			}
		}catch(Exception e){

		}
				//CommsServerAction action = new CommsServerAction(clientSocket);
				//Thread thread = new Thread(action);
				//thread.start();
/*
				String result = "";
				BufferedReader stdIn = null;
				PrintWriter out = null;
				try{
					stdIn = new BufferedReader(
							new InputStreamReader(clientSocket.getInputStream()));
					out = new PrintWriter(new BufferedOutputStream(clientSocket.getOutputStream()),true);
					result = stdIn.readLine();
					boolean reading = true;
					while(reading)
					{
						//result = stdIn.readLine();
						if(result.matches(RETRIEVE_SCORES))
						{
							log.debug("RETRIEVING SCORES!");
							//GET SCORES
							List<UserScore> userScoresList = userScoreRepo.list();
							if(!userScoresList.isEmpty())
							{
								log.debug("REPLY ISNT EMPTY");
								String sendScores = objectToJSON(userScoresList);
								out.println(sendScores);
							}
							else
							{
								log.debug("REPLY IS EMPTY");
								out.println("[]");
							}

						}
						else if(result.matches(RETRIEVE_USER_HISTORY))
						{
							log.debug("RETRIEVING USER ANSWERED Q'S");
							//NEXT LINE SHOULD BE USER OR GROUP
							result = stdIn.readLine();
							List<UserAnsweredQ> userAnswered = null;

							userAnswered = userAnswerRepo.getByJID(result);	
							if(userAnswered!=null)
							{
								String sendUserAnswered = objectToJSON(userAnswered);
								out.println(sendUserAnswered);
							}
							else
							{
								out.println("[]");
							}
						}
						else if(result.matches(RETRIEVE_QUESTIONS))
						{
							log.debug("RETRIEVING QUESTIONS!");
							List<Question> questionList = questionRepo.list();
							if(!questionList.isEmpty())
							{
								String sendQuestion = objectToJSON(questionList);
								out.println(sendQuestion);
							}
							else
							{
								out.println("[]");
							}

						}
						else if(result.matches(RETRIEVE_CATEGORIES))
						{
							log.debug("RETRIEVING CATEGORIES!");
							List<Category> categoryList = categoryRepo.list();

							if(!categoryList.isEmpty())
							{
								String sendCategory = objectToJSON(categoryList);
								out.println(sendCategory);
							}
							else
							{
								out.println("[]");
							}

						}
						else if(result.matches(UPLOAD_PROGRESS))
						{
							log.debug("Received progress update...");
							result = stdIn.readLine();
							if(result.matches(USER))
							{
								log.debug("UPDATING USER PROGRESS!");
								result = stdIn.readLine();
								UserScore updateUser = new Gson().fromJson(result, UserScore.class);
								log.debug("Received user update...");
								log.debug("USER: "+ updateUser.getUserJid() +"\n SCORE: "+String.valueOf(updateUser.getScore()));
								userScoreRepo.update(updateUser);
								log.debug("Updated user information in DB");
								log.debug("Now getting update on questions answered");
								List<UserAnsweredQ> alreadyAsked = userAnswerRepo.getByJID(updateUser.getUserJid());
								result = stdIn.readLine();
								if(result!=null && !result.isEmpty())
								{

									log.debug("Received questions from GUI");
									List<UserAnsweredQ> answeredQ = new Gson().fromJson(result, collectionType);
									answeredQ.removeAll(alreadyAsked);
									log.debug("Received following question IDs");
									for(UserAnsweredQ u : answeredQ)
									{
										log.debug(String.valueOf(u.getQuestionID()));
									}
									log.debug("Now updating the database");
									//List<UserAnsweredQ> answeredQ = Arrays.asList(new Gson().fromJson(result, UserAnsweredQ.class));
									userAnswerRepo.update(answeredQ);
								}
							}
							else if (result.matches(GROUP))
							{
								//ITS A GROUP
								log.debug("UPDATING GROUP PROGESS!");
								//GET THE GROUP
								result = stdIn.readLine();
								Groups group = new Gson().fromJson(result, Groups.class);
								groupsRepo.update(group);
								//GET THE ANSWERED QUESTIONS
								result = stdIn.readLine();
								List<UserAnsweredQ> alreadyAsked = userAnswerRepo.getByJID(group.getGroupName());
								if(result!=null && !result.isEmpty())
								{

									log.debug("Received questions from GUI");
									List<UserAnsweredQ> answeredQ = new Gson().fromJson(result, collectionType);
									answeredQ.removeAll(alreadyAsked);
									log.debug("Received following question IDs");
									for(UserAnsweredQ u : answeredQ)
									{
										log.debug(String.valueOf(u.getQuestionID()));
									}
									log.debug("Now updating the database");
									//List<UserAnsweredQ> answeredQ = Arrays.asList(new Gson().fromJson(result, UserAnsweredQ.class));
									userAnswerRepo.update(answeredQ);
								}

							}
							log.debug("Insertered correctly!");
						}
						else if (result.matches(RETRIEVE_USER_GROUP))
						{
							log.debug("Getting startup group info");
							//get user jid
							result = stdIn.readLine();
							UserGroups userGroup = userGroupsRepo.getByID(result);
							if(userGroup!=null)
							{
								//User is in a group - send group info back
								//GET GROUP INFO
								int groupId = userGroup.getGroupId();
								Groups group = groupsRepo.getByID(groupId);
								String sendGroup = objectToJSON(group);
								out.println(sendGroup);
							}
							else
							{
								out.println("NULL");
							}
						}
						else if (result.matches(RETRIEVE_GROUPS))
						{
							log.debug("Getting groups");
							//get user jid
							//result = stdIn.readLine();
							List<Groups> groups = groupsRepo.list();
							if(groups!=null)
							{
								String sendGroups = objectToJSON(groups);
								out.println(sendGroups);
							}
							else
							{
								out.println("[]");
							}
						}
						else if (result.matches(RETRIEVE_GROUP_PLAYERS))
						{
							//get group ID
							result = stdIn.readLine();

							List<UserGroups> users = userGroupsRepo.getListByID(Integer.parseInt(result));
							if(users.size()>0)
							{
								List<String> usersIDs = new ArrayList<String>();
								for(UserGroups u : users)
								{
									usersIDs.add(u.getUserJid());
								}
								String sendUsers = objectToJSON(usersIDs);
								out.println(sendUsers);
							}
							else//SHOULD HOPEFULLY NEVER ARRIVE HERE!!!
							{
								out.println("[]");
							}
						}
						else if(result.matches(RETRIEVE_INVITED_PLAYERS))
						{
							//Get group name
							result = stdIn.readLine();
							List<PendingJoins> invitedJoins = pendingJoinsRepo.getPlayersByGroup(result);
							if(invitedJoins.size()>0)
							{
								List<String> players = new ArrayList<String>();
								for(PendingJoins p : invitedJoins)
								{
									players.add(p.getToUser());
								}
								String sendPlayers = objectToJSON(players);
								out.println(sendPlayers);
							}
							else
							{
								out.println("[]");
							}

						}
						else if(result.matches(RETRIEVE_NOTIFICATIONS))
						{
							result = stdIn.readLine();
							List<PendingJoins> join = pendingJoinsRepo.getByID(result);
							if(join.size()>0)
							{
								//User has notifications - send them
								String sendPending = objectToJSON(join);
								out.println(sendPending);
							}
							else
							{
								//User has no notifcations either - sending NULL back
								out.println("[]");
							}
						}
						else if(result.matches(DELETE_NOTIFICATIONS))
						{
							result = stdIn.readLine();
							List<PendingJoins> join = new Gson().fromJson(result, collectionType2);
							for (PendingJoins p : join)
							{
								pendingJoinsRepo.physicalDelete(p);
							}
							out.println("TRUE");
						}
						else if (result.matches(CREATE_GROUP))
						{
							//GET JID
							result = stdIn.readLine();
							Groups newGroup = new Groups();
							newGroup.setGroupName(result.substring(0, result.indexOf('.')) + "'s Group");
							newGroup.setScore(0);
							newGroup.setAdmin(result);
							groupsRepo.insert(newGroup);
							//GET GROUP ID
							int groupId = groupsRepo.getByName(newGroup.getGroupName()).getGroupID();
							UserGroups newUserGroups = new UserGroups();
							newUserGroups.setGroupId(groupId);
							newUserGroups.setUserJid(result);
							userGroupsRepo.insert(newUserGroups);
							out.println("TRUE");
						}
						else if (result.matches(LEAVE_GROUP))
						{
							//GET JID
							result = stdIn.readLine();
							userGroupsRepo.deleteUser(result);
							out.println("TRUE");
						}
						else if (result.matches(RETRIEVE_ALL_USERS))
						{
							List<UserScore> allUsers = userScoreRepo.list();
							if(allUsers.size() > 0)
							{
								List<String> userJids = new ArrayList<String>();
								for (UserScore us : allUsers)
								{
									userJids.add(us.getUserJid());
								}
								String sendUsers = objectToJSON(userJids);
								out.println(sendUsers);		
							}
							else
							{
								out.println("[]");
							}
						}
						else if (result.matches(ADD_USER_TO_GROUP))
						{
							log.debug("Adding user to group!");
							//GET GROUP Name
							result = stdIn.readLine();
							//check that group exists
							Groups group = groupsRepo.getByName(result);
							if(group!=null)
							{
								log.debug("The group the user wants to join exists!");
								//GROUP EXISTS - CHECK IF USER IS IN ANOTHER GROUP, IF SO, REMOVE!
								result = stdIn.readLine();
								UserGroups usersGroup = userGroupsRepo.getByID(result);
								if(usersGroup!=null)
								{
									userGroupsRepo.physicalDelete(usersGroup);
								}
								//ADD USER TO NEW GROUP
								usersGroup = new UserGroups(); // MAY BE NULL
								usersGroup.setGroupId(group.getGroupID());
								usersGroup.setUserJid(result);
								userGroupsRepo.insert(usersGroup);
								//RETURN A LIST OF THE USERS AND THE GROUP INFO
								out.println("TRUE");

							}
							else
							{
								//GROUP DOESNT EXIST
								out.println("NULL");
							}							
						}
						else if (result.matches(INVITE_USER))
						{
							PendingJoins invite = new PendingJoins();
							//GET INVITE FROM
							result = stdIn.readLine();
							invite.setFromUser(result);
							//GET TO
							result = stdIn.readLine();
							invite.setToUser(result);
							//GET GROUP
							result = stdIn.readLine();
							invite.setGroupName(result);
							pendingJoinsRepo.insert(invite);
							out.println("TRUE");
						}
						else if (result.matches(REMOVE_USER_FROM_GROUP))
						{
							//GET USER ID
							result = stdIn.readLine();
							UserGroups group = userGroupsRepo.getByID(result);
							userGroupsRepo.physicalDelete(group);
							out.println("TRUE");
						}
						else if (result.matches(DELETE_GROUP))
						{
							log.debug("GOT DDELETE REQUEST!");
							result = stdIn.readLine();
							//GET GROUP NAME
							log.debug("DELETE groupID: " + result);
							Groups group = groupsRepo.getByID(Integer.parseInt(result));
							userGroupsRepo.deleteAll(result);
							pendingJoinsRepo.deleteGroup(group.getGroupName());
							userAnswerRepo.deleteAll(group.getGroupName());
							groupsRepo.deleteGroupByID(Integer.parseInt(result));
							out.println("TRUE");
						}
						else if (result.matches(RESET_USER))
						{
							//GET USER JID
							result = stdIn.readLine();
							userAnswerRepo.deleteAll(result);
							UserScore user = userScoreRepo.getByJID(result);
							user.setScore(0);
							userScoreRepo.update(user);
							out.println("TRUE");

						}
						else
						{
							log.debug("REQUEST DOESN'T MATCH: " + result);
						}
						reading = false;

					}

				}catch (Exception e) {
					log.debug("Other exception " + e);
					return;
				} finally {
					stdIn.close();
					out.close();
					serverSocket.close();
				}
			}
		}catch (IOException e) {
			log.debug("IO Exception - Socket is closed");
			return;
		}
*/

	}

	private String objectToJSON(Object data)
	{
		log.debug("Converting Object to JSON!");
		String jsonData = new Gson().toJson(data);
		if(jsonData!=null)
		{
			return jsonData;
		}
		else
		{
			return "NULL";
		}

	}

	public int getSocket(){
		return this.port;
	}

	public String getAddress(){
		return this.address.toString();
	}

}
