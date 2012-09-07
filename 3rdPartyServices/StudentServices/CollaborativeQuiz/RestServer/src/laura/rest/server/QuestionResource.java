package laura.rest.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import laura.rest.server.model.Question;

@Path("/question")
public class QuestionResource {
	
	Connection conn = null;
	Statement statement = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<Question> getQuestions() {
		List<Question> questionset = new ArrayList<Question>();
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String url ="jdbc:mysql://localhost:3306/sociallearninggame";
			conn = DriverManager.getConnection(url, "root", "123qwe");
			// Statements allow to issue SQL queries to the database
			statement = conn.createStatement();
		
			resultSet = statement.executeQuery("SELECT * FROM Questions;");
			
			while(resultSet.next()){
				Question question = new Question();
				
				question.setQuestion(resultSet.getString("question"));
				question.setAnswer1(resultSet.getString("answer1"));
				question.setAnswer2(resultSet.getString("answer2"));
				question.setAnswer3(resultSet.getString("answer3"));
				question.setAnswer4(resultSet.getString("answer4"));
				question.setCorrectAnswer(resultSet.getString("correctAnswer"));
				question.setCategory(resultSet.getString("category"));
				
				questionset.add(question);
			}
		}
		catch(SQLException e){
			System.out.println("error connecting to database");
		}catch (ClassNotFoundException e){
			System.out.println("missing class");
		}

		return questionset;
	}
	
	@GET
	@Path("unverified")
	@Produces(MediaType.APPLICATION_XML)
	public List<Question> geUnverified() {
		List<Question> questionset = new ArrayList<Question>();
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String url ="jdbc:mysql://localhost:3306/sociallearninggame";
			conn = DriverManager.getConnection(url, "root", "123qwe");
			// Statements allow to issue SQL queries to the database
			statement = conn.createStatement();
		
			resultSet = statement.executeQuery("SELECT * FROM QuestionsToCheck;");
			
			while(resultSet.next()){
				Question question = new Question();
				
				question.setQuestion(resultSet.getString("question"));
				question.setAnswer1(resultSet.getString("answer1"));
				question.setAnswer2(resultSet.getString("answer2"));
				question.setAnswer3(resultSet.getString("answer3"));
				question.setAnswer4(resultSet.getString("answer4"));
				question.setCorrectAnswer(resultSet.getString("correctAnswer"));
				question.setCategory(resultSet.getString("category"));
				
				questionset.add(question);
			}
		}
		catch(SQLException e){
			System.out.println("error connecting to database");
		}catch (ClassNotFoundException e){
			System.out.println("missing class");
		}

		return questionset;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String addQuestion(@FormParam("question") String question, @FormParam("answer1") String answer1, 
			@FormParam("answer2") String answer2, @FormParam("answer3") String answer3,
			@FormParam("answer4") String answer4, @FormParam("correctAnswer") String correctAnswer, 
			@FormParam("category") String category)
	{
		
		if(correctAnswer.equals("Answer 1")){
			correctAnswer = answer1;
		}else if(correctAnswer.equals("Answer 2")){
			correctAnswer = answer2;
		}else if(correctAnswer.equals("Answer 3")){
			correctAnswer = answer3;
		}else if(correctAnswer.equals("Answer 4")){
			correctAnswer = answer4;
		}
		//INSERT a question into the questionsToCheck Table, so that questions and answers can be confirmed before added to the questions
		//database
		
		
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String url ="jdbc:mysql://localhost:3306/sociallearninggame";
			conn = DriverManager.getConnection(url, "root", "123qwe");
			
			try{
				preparedStatement = conn.prepareStatement("SELECT * FROM QuestionsToCheck WHERE question = (?)");
				preparedStatement.setString(1,question);
				resultSet = preparedStatement.executeQuery();
				
				if(resultSet.next()){
					conn.close();
					return "question already added";
				}
				preparedStatement.close();
				
				preparedStatement = conn.prepareStatement("SELECT * FROM Questions WHERE question = (?)");
				preparedStatement.setString(1,question);
				resultSet = preparedStatement.executeQuery();
				
				if(resultSet.next()){
					conn.close();
					return "question already added";
				}
				preparedStatement.close();
				
			}catch(Exception e){
				System.out.println("error connecting to database");
			}
			
			// Statements allow to issue SQL queries to the database
			preparedStatement = conn.prepareStatement("INSERT INTO QuestionsToCheck (question, answer1, answer2, answer3, answer4, correctAnswer, category) VALUES (?,?,?,?,?,?,?)");
			preparedStatement.setString(1, question);
			preparedStatement.setString(2, answer1);
			preparedStatement.setString(3, answer2);
			preparedStatement.setString(4, answer3);
			preparedStatement.setString(5, answer4);
			preparedStatement.setString(6, correctAnswer);
			preparedStatement.setString(7, category);
			
			preparedStatement.executeUpdate();
			

		}
		catch(SQLException e){
			System.out.println("error connecting to database");
			return "error connecting to database";
		}catch (ClassNotFoundException e){
			System.out.println("missing class");
			return "error connecting to database";
		}
		
		return "Question added";
	}
	
} 