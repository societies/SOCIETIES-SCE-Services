package laura.rest.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import laura.rest.server.model.Student;

@Path("/student")
public class StudentResource {
	
	
	Connection conn = null;
	Statement statement = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	
	
	List<Student> students = new ArrayList<Student>();

	Student student = new Student();

	
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Student getXML(@QueryParam("id") String id)
	{
		
		//get the student with the matching jid from the db
		//if the student does not exist return null
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String url ="jdbc:mysql://localhost:3306/sociallearninggame";
			conn = DriverManager.getConnection(url, "root", "123qwe");
			// Statements allow to issue SQL queries to the database
			statement = conn.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery("SELECT * FROM Students");
			
			while(resultSet.next())
			{
				if(resultSet.getString("jid").equals(id))
				{
					
					student.setId(id);
					student.setName(resultSet.getString("name"));
					student.setScore(resultSet.getInt("score"));
					student.setFirst(resultSet.getInt("first"));
					return student;
				}
			}
			resultSet.close();
			conn.close();
		} catch(Exception e){
			System.out.println("an error occurred while trying to connect to the database");
		}
		
		return null;
	}
	
	@GET 
	@Path("/all")
	@Produces( {MediaType.APPLICATION_XML} )
	public List<Student> getStudents(@QueryParam("id") String id)
	{
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String url ="jdbc:mysql://localhost:3306/sociallearninggame";
			conn = DriverManager.getConnection(url, "root", "123qwe");
			// Statements allow to issue SQL queries to the database
			statement = conn.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery("SELECT * FROM Students");

			while(resultSet.next())
			{
				if(!resultSet.getString("jid").equals(id))
				{
					Student studentToAdd = new Student();
					studentToAdd.setId(resultSet.getString("jid"));
					studentToAdd.setName(resultSet.getString("name"));
					studentToAdd.setScore(resultSet.getInt("score"));
					studentToAdd.setFirst(resultSet.getInt("first"));
					students.add(studentToAdd);
				} else {
				
				}
			}
			resultSet.close();
			conn.close();
		} catch(Exception e){
			System.out.println("an error occurred while trying to connect to the database");
		}
		
		return students;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String postStudent(JAXBElement<Student> savedstudent)
	{
		this.student = savedstudent.getValue();
		//if the student exists in the database then update the student otherwise add the 
		//student to the database
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String url ="jdbc:mysql://localhost:3306/sociallearninggame";
			conn = DriverManager.getConnection(url, "root", "123qwe");
			// Statements allow to issue SQL queries to the database
			statement = conn.createStatement();
			
			// Result set get the result of the SQL query
			String query = "SELECT * FROM Students"; 
			resultSet = statement.executeQuery(query);

			while(resultSet.next())
			{
				if(resultSet.getString("jid").equals(student.getId()))
				{
					try{
						preparedStatement = conn.prepareStatement("UPDATE Students SET score = (?), first = (?) WHERE jid = (?)");
						preparedStatement.setInt(1, student.getScore());
						preparedStatement.setString(3, student.getId());
						preparedStatement.setInt(2, student.getFirst());
						
						//return string after student has been successfully updated
						preparedStatement.executeUpdate();
						//close resultSet and connection
						resultSet.close();
						conn.close();
						return "Student score updated";
					} catch(Exception e){
						System.out.println("an error occurred while trying to update student");
						return "error updating student";
					}
				}
			}
			resultSet.close();
			//add the student to the database if the update statement did not return
			if(!student.getId().equals("") && !student.getName().equals("")){
				preparedStatement = conn.prepareStatement("INSERT INTO Students VALUES (?,?,?,?)");
				preparedStatement.setString(1, student.getId());
				preparedStatement.setString(2, student.getName());
				preparedStatement.setInt(3, student.getScore());
				preparedStatement.setInt(4, student.getFirst());
				preparedStatement.executeUpdate();
			}else{
				return "cannot add to database/update student";
			}
			
			resultSet.close();
			conn.close();
		} catch(Exception e){
			System.out.println("an error occurred while trying to connect to the database");
			return "an error occurred while trying to connect to the database";
		}
		
		return "student added to database";
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String registerStudent(@FormParam("jid") String id, @FormParam("name") String name)
	{
		//if the student exists in the database then update the student otherwise add the 
		//student to the database
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String url ="jdbc:mysql://localhost:3306/sociallearninggame";
			conn = DriverManager.getConnection(url, "root", "123qwe");
			// Statements allow to issue SQL queries to the database
			statement = conn.createStatement();
			
			// Result set get the result of the SQL query
			String query = "SELECT * FROM Students"; 
			resultSet = statement.executeQuery(query);
			
			while(resultSet.next())
			{
				
				if(resultSet.getString("jid").equals(id))
				{	
					return "Student already registered";
				}
			}
			resultSet.close();
			//add the student to the database if the update statement did not return
			if(!id.equals("") && !name.equals("")){
				
				preparedStatement = conn.prepareStatement("INSERT INTO Students (jid,name,first) VALUES (?,?,1)");
				preparedStatement.setString(1, id);
				preparedStatement.setString(2, name);
				preparedStatement.executeUpdate();
			}else{
				return "missing id and/or name";
			}
			
			resultSet.close();
			conn.close();
		} catch(Exception e){
			System.out.println("an error occurred while trying to connect to the database");
			return "an error occurred while trying to connect to the database";
		}
		
		return "registration complete";
	}
}
