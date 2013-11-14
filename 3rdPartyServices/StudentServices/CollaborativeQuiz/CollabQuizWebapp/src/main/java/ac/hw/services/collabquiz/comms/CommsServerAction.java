package ac.hw.services.collabquiz.comms;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ac.hw.services.collabquiz.dao.ICategoryRepository;
import ac.hw.services.collabquiz.dao.IGroupsRepository;
import ac.hw.services.collabquiz.dao.IPendingJoinsRepository;
import ac.hw.services.collabquiz.dao.IQuestionRepository;
import ac.hw.services.collabquiz.dao.IUserAnsweredQRepository;
import ac.hw.services.collabquiz.dao.IUserGroupsRepository;
import ac.hw.services.collabquiz.dao.IUserScoreRepository;
import ac.hw.services.collabquiz.entities.Category;
import ac.hw.services.collabquiz.entities.Groups;
import ac.hw.services.collabquiz.entities.PendingJoins;
import ac.hw.services.collabquiz.entities.Question;
import ac.hw.services.collabquiz.entities.UserAnsweredQ;
import ac.hw.services.collabquiz.entities.UserGroups;
import ac.hw.services.collabquiz.entities.UserScore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CommsServerAction implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(CommsServerListener.class);

	private static final Type collectionType = new TypeToken<List<UserAnsweredQ>>(){}.getType();
	private static final Type collectionType2 = new TypeToken<List<PendingJoins>>(){}.getType();


	private IQuestionRepository questionRepo;
	private ICategoryRepository categoryRepo;
	private IUserAnsweredQRepository userAnswerRepo;
	private IUserScoreRepository userScoreRepo;
	private IUserGroupsRepository userGroupsRepo;
	private IGroupsRepository groupsRepo;
	private IPendingJoinsRepository pendingJoinsRepo;
	private Socket clientSocket;

	//MESSAGE TYPES
	private static final String RETRIEVE_SCORES = "RETRIEVE_SCORES";
	private static final String RETRIEVE_USER_HISTORY = "RETRIEVE_USER_HISTORY";
	private static final String RETRIEVE_QUESTIONS = "RETRIEVE_QUESTIONS";
	private static final String RETRIEVE_CATEGORIES = "RETRIEVE_CATEGORIES";
	private static final String UPLOAD_PROGRESS = "UPLOAD_PROGRESS";
	private static final String USER = "USER";
	private static final String GROUP = "GROUP";
	private static final String RETRIEVE_USER_GROUP = "RETRIEVE_USER_GROUP";
	private static final String RETRIEVE_GROUPS = "RETRIEVE_GROUPS";
	private static final String RETRIEVE_GROUP_PLAYERS = "RETRIEVE_GROUP_PLAYERS";
	private static final String RETRIEVE_INVITED_PLAYERS = "RETRIEVE_INVITED_PLAYERS";
	private static final String RETRIEVE_NOTIFICATIONS = "RETRIEVE_NOTIFICATIONS";
	private static final String DELETE_NOTIFICATIONS = "DELETE_NOTIFICATIONS";
	private static final String CREATE_GROUP = "CREATE_GROUP";
	private static final String LEAVE_GROUP = "LEAVE_GROUP";
	private static final String RETRIEVE_ALL_USERS = "RETRIEVE_ALL_USERS";
	private static final String ADD_USER_TO_GROUP = "ADD_USER_TO_GROUP";
	private static final String INVITE_USER = "INVITE_USER";
	private static final String REMOVE_USER_FROM_GROUP = "REMOVE_USER_FROM_GROUP";
	private static final String DELETE_GROUP = "DELETE_GROUP";
	private static final String RESET_USER = "RESET_USER";

	private static final String NULL_REPLY = "NULL";
	private static final String TRUE_REPLY = "TRUE";

	public CommsServerAction(Socket clientSocket, IUserAnsweredQRepository userAnserRepo, IUserScoreRepository userScoreRepo, IQuestionRepository questionRepo, ICategoryRepository categoryRepo, IUserGroupsRepository userGroupsRepo,  IGroupsRepository groupsRepo, IPendingJoinsRepository pendingJoinsRepo)
	{
		this.userAnswerRepo= userAnserRepo;
		this.userScoreRepo= userScoreRepo;
		this.questionRepo = questionRepo;
		this.categoryRepo= categoryRepo;
		this.userGroupsRepo = userGroupsRepo;
		this.groupsRepo = groupsRepo;
		this.pendingJoinsRepo = pendingJoinsRepo;
		this.clientSocket = clientSocket;
	}

	public void run() {

		String result = "";
		BufferedReader stdIn = null;
		PrintWriter out = null;
		try{
			stdIn = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(new BufferedOutputStream(clientSocket.getOutputStream()),true);
			result = stdIn.readLine();
			//result = stdIn.readLine();
			if(result.matches(RETRIEVE_SCORES))
			{
				log.debug("RETRIEVING SCORES!");
				//GET SCORES
				List<UserScore> userScoresList = userScoreRepo.list();
				if(userScoresList.size() > 0)
				{
					log.debug("REPLY ISNT EMPTY");
					String sendScores = objectToJSON(userScoresList);
					out.println(sendScores);
				}
				else
				{
					out.println(NULL_REPLY);
				}

			}
			else if(result.matches(RETRIEVE_USER_HISTORY))
			{
				log.debug("RETRIEVING USER ANSWERED Q'S");
				//NEXT LINE SHOULD BE USER OR GROUP
				result = stdIn.readLine();
				List<UserAnsweredQ> userAnswered = null;

				userAnswered = userAnswerRepo.getByJID(result);	
				if(userAnswered.size() > 0)
				{
					String sendUserAnswered = objectToJSON(userAnswered);
					out.println(sendUserAnswered);
				}
				else
				{
					out.println(NULL_REPLY);
				}
			}
			else if(result.matches(RETRIEVE_QUESTIONS))
			{
				log.debug("RETRIEVING QUESTIONS!");
				List<Question> questionList = questionRepo.list();
				if(questionList.size() > 0)
				{
					String sendQuestion = objectToJSON(questionList);
					out.println(sendQuestion);
				}
				else
				{
					out.println(NULL_REPLY);
				}

			}
			else if(result.matches(RETRIEVE_CATEGORIES))
			{
				log.debug("RETRIEVING CATEGORIES!");
				List<Category> categoryList = categoryRepo.list();
				if(categoryList.size() > 0)
				{
					String sendCategory = objectToJSON(categoryList);
					out.println(sendCategory);
				}
				else
				{
					out.println(NULL_REPLY);
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
					result = stdIn.readLine();
					if(result!=null && !result.isEmpty())
					{

						log.debug("Received questions from GUI");
						UserAnsweredQ answeredQ = new Gson().fromJson(result, UserAnsweredQ.class);
						log.debug("Received following question IDs");
						//List<UserAnsweredQ> answeredQ = Arrays.asList(new Gson().fromJson(result, UserAnsweredQ.class));
						userAnswerRepo.insert(answeredQ);
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
					if(result!=null && !result.isEmpty())
					{

						log.debug("Received questions from GUI");
						UserAnsweredQ answeredQ = new Gson().fromJson(result, UserAnsweredQ.class);
						log.debug("Received following question IDs");
						//List<UserAnsweredQ> answeredQ = Arrays.asList(new Gson().fromJson(result, UserAnsweredQ.class));
						userAnswerRepo.insert(answeredQ);
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
					out.println(NULL_REPLY);
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
					out.println(NULL_REPLY);
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
					out.println(NULL_REPLY);
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
					out.println(NULL_REPLY);
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
					out.println(NULL_REPLY);
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
				out.println(TRUE_REPLY);
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
				out.println(TRUE_REPLY);
			}
			else if (result.matches(LEAVE_GROUP))
			{
				//GET JID
				result = stdIn.readLine();
				userGroupsRepo.deleteUser(result);
				out.println(TRUE_REPLY);
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
					out.println(NULL_REPLY);
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

						//CHECK IF USER IS ADMIN OF THAT GROUP
						//IF SO - DELETE ALL PENDING JOINS & PLAYERS OF THAT GROUP
						Groups oldGroup = groupsRepo.getByID(usersGroup.getGroupId());
						if(oldGroup!=null)
						{
							if(oldGroup.getAdmin().equals(result))
							{
								pendingJoinsRepo.deleteGroup(oldGroup.getGroupName());
								userGroupsRepo.deleteAll(String.valueOf(oldGroup.getGroupID()));
								groupsRepo.deleteGroupByID(oldGroup.getGroupID());
							}
						}
					}
					//ADD USER TO NEW GROUP
					usersGroup = new UserGroups(); // MAY BE NULL
					usersGroup.setGroupId(group.getGroupID());
					usersGroup.setUserJid(result);
					userGroupsRepo.insert(usersGroup);
					//RETURN A LIST OF THE USERS AND THE GROUP INFO
					out.println(TRUE_REPLY);

				}
				else
				{
					//GROUP DOESNT EXIST
					out.println(NULL_REPLY);
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
				out.println(TRUE_REPLY);
			}
			else if (result.matches(REMOVE_USER_FROM_GROUP))
			{
				//GET USER ID
				result = stdIn.readLine();
				UserGroups group = userGroupsRepo.getByID(result);
				userGroupsRepo.physicalDelete(group);
				out.println(TRUE_REPLY);
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
				out.println(TRUE_REPLY);
			}
			else if (result.matches(RESET_USER))
			{
				//GET USER JID
				result = stdIn.readLine();
				userAnswerRepo.deleteAll(result);
				UserScore user = userScoreRepo.getByJID(result);
				user.setScore(0);
				userScoreRepo.update(user);
				out.println(TRUE_REPLY);

			}
			else
			{
				log.debug("REQUEST DOESN'T MATCH: " + result);
			}



		}catch (Exception e) {
			log.debug("Other exception " + e);
			StackTraceElement[] a = e.getStackTrace();
			for(int x = 0; x < a.length; x++)
			{
				log.debug(a[x].toString());
			}


		}
		try{
			out.close();
			stdIn.close();
			clientSocket.close();
		}catch(Exception e){}

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

}
