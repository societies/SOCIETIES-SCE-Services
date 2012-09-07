package laura.rest.server;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import laura.rest.server.model.Challenge;
import laura.rest.server.model.Student;

@Path("/challenge")
public class ChallengeResource {
	
	
	//TODO get challenge from database
	Connection conn = null;
	Statement statement = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	
	
	List<Challenge> challenges = new ArrayList<Challenge>();
	List<Challenge> challengesFrom = new ArrayList<Challenge>();
	
	
	@GET
	@Produces( { MediaType.APPLICATION_XML })
	public List<Challenge> getChallengesTo(@QueryParam("id") String id)
	{
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String url ="jdbc:mysql://localhost:3306/sociallearninggame";
			conn = DriverManager.getConnection(url, "root", "123qwe");

			preparedStatement = conn.prepareStatement("SELECT * FROM Challenges WHERE challenged = (?)");
			preparedStatement.setString(1, id);
			resultSet = preparedStatement.executeQuery();

			//if the player has no challenges return null and close the connection to the DB
			if(resultSet.next() == false)
			{
				conn.close();
				preparedStatement.close();
				resultSet.close();
				return null;
			} 
			//if there are challenges to the player loop through them and add them to the challenges list
			else{
				resultSet.beforeFirst();
				while(resultSet.next())
				{
					if(resultSet.getInt("challengedScore") == -1){
					Challenge challenge = new Challenge();
					challenge.setId(resultSet.getInt("id"));
					//find the challenger and challenged Students by jid
					try{
						//SELECT statement to find the student with the matching jid
						PreparedStatement findStudent = conn.prepareStatement("SELECT * FROM Students WHERE jid = (?)");
						findStudent.setString(1,resultSet.getString("challenger"));
						ResultSet challenger = findStudent.executeQuery();
						
						if(challenger.next()==false){
							return null;
						}
						//get the value of the Student who sent the challenge
						Student student1 = new Student();
						student1.setId(challenger.getString("jid"));
						student1.setName(challenger.getString("name"));
						student1.setScore(challenger.getInt("score"));
						challenge.setChallenger(student1);
						challenger.close();
						findStudent.close();
					}catch (Exception e){
						System.out.println("an error occurred while trying to retrieve student from DB");
					}
					try{
						//SELECT statement to find the student with the matching jid
						PreparedStatement findStudent = conn.prepareStatement("SELECT * FROM Students WHERE jid = (?)");
						findStudent.setString(1, resultSet.getString("challenged"));
						
						ResultSet challenged = findStudent.executeQuery();
						if(challenged.next()==false){
							return null;
						}
						//get the value of the Student who was challenged
						Student student = new Student();
						student.setId(challenged.getString("jid"));
						student.setName(challenged.getString("name"));
						student.setScore(challenged.getInt("score"));
						challenge.setChallenged(student);
						challenged.close();
						findStudent.close();
						
					}catch(Exception e){
						System.out.println("an error occurred while trying to retrieve students from DB");
					}

					challenge.setCategory(resultSet.getString("category"));
					challenge.setChallengerScore(resultSet.getInt("challengerScore"));
					challenge.setChallengedScore(resultSet.getInt("challengedScore"));
					
					challenges.add(challenge);
				}
				
				}
			}
			
			resultSet.close();
			conn.close();
		} catch(Exception e){
			System.out.println("an error occurred while trying to connect to the database");
		}
		if(challenges.isEmpty())
		{
			return null;
		}
		return challenges;
	}
	
	
	@GET @Path("from")
	@Produces( { MediaType.APPLICATION_XML })
	public List<Challenge> getChallengesFrom(@QueryParam("id") String id)
	{
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String url ="jdbc:mysql://localhost:3306/sociallearninggame";
			conn = DriverManager.getConnection(url, "root", "123qwe");
			preparedStatement = conn.prepareStatement("SELECT * FROM Challenges WHERE challenger = (?)");
			preparedStatement.setString(1, id);
			resultSet = preparedStatement.executeQuery();

			if(resultSet.next() == false)
			{
				//if there are no challenges returned close the connection, statement and result set and return null
				conn.close();
				preparedStatement.close();
				resultSet.close();
				return null;
			} else{
				//go back to before the first result so that the first result can be retrieved using the while statement
				resultSet.beforeFirst();
				while(resultSet.next())
				{
					//if the challenge score is the default value of -1 then the challenge has not been completed
					//ignore the challenge
					if(resultSet.getInt("challengedScore")==-1)
					{

					}
					//if the challenge has been completed add it to the list of challenges from the user
					else {
						Challenge challenge = new Challenge();
						challenge.setId(resultSet.getInt("id"));
						//find the challenger and challenged Students by jid
						try{
							//SELECT statement to find the student with the matching jid
							PreparedStatement findStudent = conn.prepareStatement("SELECT * FROM Students WHERE jid = (?)");
							findStudent.setString(1,resultSet.getString("challenger"));
							ResultSet challenger = findStudent.executeQuery();
						
							if(challenger.next()==false){
								return null;
							}
							//get the value of the Student who sent the challenge
							Student student1 = new Student();
							student1.setId(challenger.getString("jid"));
							student1.setName(challenger.getString("name"));
							student1.setScore(challenger.getInt("score"));
							challenge.setChallenger(student1);
							challenger.close();
							findStudent.close();
						}catch (Exception e){
							System.out.println("an error occurred while trying to retrieve student from DB");
						}
						try{
							//SELECT statement to find the student with the matching jid 
							PreparedStatement findStudent = conn.prepareStatement("SELECT * FROM Students WHERE jid = (?)");
							findStudent.setString(1, resultSet.getString("challenged"));
						
							ResultSet challenged = findStudent.executeQuery();
							if(challenged.next()==false){
								return null;
							}
							//get the value of the Student who was challenged
							Student student = new Student();
							student.setId(challenged.getString("jid"));
							student.setName(challenged.getString("name"));
							student.setScore(challenged.getInt("score"));
							challenge.setChallenged(student);
							challenged.close();
							findStudent.close();
						
						}catch(Exception e){
							System.out.println("an error occurred while trying to retrieve students from DB");
						}

						challenge.setCategory(resultSet.getString("category"));
						challenge.setChallengerScore(resultSet.getInt("challengerScore"));
						challenge.setChallengedScore(resultSet.getInt("challengedScore"));
					
						challengesFrom.add(challenge);
				
					}
				}
			}
			//close the connection and result set
			resultSet.close();
			conn.close();
		} catch(Exception e){
			System.out.println("an error occurred while trying to connect to the database");
		}
		if(challengesFrom.isEmpty()){
			return null;
		}
		else{
			return challengesFrom;
		}
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String postChallenge(JAXBElement<Challenge> savedChallenge)
	{		
		Challenge challenge = new Challenge();
		challenge = savedChallenge.getValue();
		
		//if the challenge exists in the database then update the challenge otherwise add the 
		//challenge to the database
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String url ="jdbc:mysql://localhost:3306/sociallearninggame";
			conn = DriverManager.getConnection(url, "root", "123qwe");
			// Statements allow to issue SQL queries to the database
			statement = conn.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery("SELECT * FROM Challenges");
			
			while(resultSet.next())
			{
				if(resultSet.getInt("id") == challenge.getId())
				{
					try{
						//UPDATE statement using variables
						preparedStatement = conn.prepareStatement("UPDATE Challenges SET challengerScore=(?),challengedScore=(?) WHERE id = (?)");
						preparedStatement.setInt(1, challenge.getChallengerScore());
						preparedStatement.setInt(2, challenge.getChallengedScore());
						preparedStatement.setInt(3, challenge.getId());
						
						//return string after student has been successfully updated
						preparedStatement.executeUpdate();
						//close resultSet and connection
						resultSet.close();
						conn.close();
						return "Challenge Updated";
					} catch(Exception e){
						System.out.println("an error occurred while trying to update student");
						return "error updating student";
					}
				}
			}
			//close the result set
			resultSet.close();
			//add the student to the database if the update statement did not return
			//INSERT statement using variables
			if(!challenge.getChallenger().getId().equals("")){
				preparedStatement = conn.prepareStatement("INSERT INTO Challenges(challenger,challenged,category,challengerScore) VALUES (?,?,?,?)");
				preparedStatement.setString(1, challenge.getChallenger().getId());
				preparedStatement.setString(2, challenge.getChallenged().getId());
				preparedStatement.setString(3, challenge.getCategory());
				preparedStatement.setInt(4, challenge.getChallengerScore());
				preparedStatement.executeUpdate();
			}
			
			//close the connection
			conn.close();
		} catch(Exception e){
			System.out.println("an error occurred while trying to connect to the database");
			return "an error occurred while trying to connect to the database";
		}
		
		return "challenge added to database";
	}
	
	@DELETE
	@Path("/delete")
	public String deleteChallenge(@QueryParam("id") int id)
	{
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String url ="jdbc:mysql://localhost:3306/sociallearninggame";
			conn = DriverManager.getConnection(url, "root", "123qwe");
			
			preparedStatement = conn.prepareStatement("DELETE FROM Challenges WHERE id = (?)");
			preparedStatement.setInt(1, id);
			preparedStatement.execute();
			
			preparedStatement.close();
			conn.close();
			return "challenge deleted";
		} catch(Exception e){
			System.out.println("an error occurred while trying to delete challenge");
			return "error deleting challenge";
		}
	}
	

}
