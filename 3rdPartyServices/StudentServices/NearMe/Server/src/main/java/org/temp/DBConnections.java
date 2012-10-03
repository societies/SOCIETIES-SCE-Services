package org.temp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.temp.CISIntegeration.ContextBinder;

public class DBConnections {
	public String dblink = "jdbc:mysql://localhost:3306/asocom";
	public String dbusr = "root";
	public String dbpwd = "xhyccc";
	public static String GET_UPDATE_SQL_BYDATE = "select * from main where Time>=? and SSID=?";
	public static String GET_USTATUS_SQL = "select * from ustatus where uid=?";
	public static String GET_LSTATUS_SQL = "select * from lstatus where SSID=?";
	public static String POST_EVENT_SQL = "insert into main(json,SSID,Time) values(?,?,?)";
	// get gen id here
	public static String UPDATE_USTATUS_SQL = "update ustatus set id=? and SSID=? where uid=?";
	public static String UPDATE_LSTATUS_SQL = "update lstatus set id=? where SSID=?";

	public static String CHECKIN_SQL = "insert into ucheckin(uid,SSID,Time) values(?,?,?)";
	public static String GET_LAST_CHECKIN_TIME = "select max(Time) from ucheckin where uid=? and SSID=?";
	
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Connection getConnection() {
		try {
		//	Class.forName("com.mysql.jdbc.Driver");
		//	Connection temp = DriverManager.getConnection(dblink, dbusr, dbpwd);
			Connection temp =ContextBinder.getDataSource().getConnection();
			return temp;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String[] getUpdatesFromTime(String ssid, Date time) {
		Connection conn = this.getConnection();
		try {
			List<String> result = new ArrayList<String>();
			PreparedStatement stmt = conn
					.prepareStatement(DBConnections.GET_UPDATE_SQL_BYDATE);
			if(time==null)
				time=new Date();
			stmt.setLong(1, time.getTime());
			stmt.setString(2, ssid);
			ResultSet res = stmt.executeQuery();
			while (res.next()) {
				result.add(res.getString("json"));
			}
			return result.toArray(new String[0]);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			this.close(conn);
		}
		return new String[0];
	}

	public boolean postEventTo(String uid, String content, String ssid) {
		Connection conn = this.getConnection();
		try {
			PreparedStatement events = conn.prepareStatement(
					DBConnections.POST_EVENT_SQL,
					PreparedStatement.RETURN_GENERATED_KEYS);
			events.setString(1, content);
			events.setString(2, ssid);
			events.setLong(3, new Date().getTime());
			PreparedStatement userupdate = conn
					.prepareStatement(DBConnections.UPDATE_USTATUS_SQL);
			PreparedStatement locationupdate = conn
					.prepareStatement(DBConnections.UPDATE_LSTATUS_SQL);
			int row = events.executeUpdate();
			userupdate.setInt(1, row);
			userupdate.setString(2, ssid);
			userupdate.setString(3, uid);
			userupdate.executeUpdate();
			locationupdate.setInt(1, row);
			locationupdate.setString(2, ssid);
			locationupdate.executeUpdate();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			this.close(conn);
		}
	}

	public Date getLastCheckInTime(String uid, String ssid) {
		Connection conn = this.getConnection();
		Date checkinDate = null;
		try {
			PreparedStatement checkin = conn
					.prepareStatement(DBConnections.GET_LAST_CHECKIN_TIME);
			checkin.setString(1, uid);
			checkin.setString(2, ssid);
			ResultSet rs = checkin.executeQuery();
			if (rs.next()) {
				checkinDate = new Date(rs.getLong(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			this.close(conn);
		}
		return checkinDate;
	}
	public void close(Connection conn){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean checkIn(String uid, String ssid) {
		Connection conn = this.getConnection();
		Date checkinDate = new Date();
		long sqlDate = checkinDate.getTime();
		try {
			PreparedStatement checkin = conn.prepareStatement(
					DBConnections.CHECKIN_SQL,
					PreparedStatement.RETURN_GENERATED_KEYS);
			checkin.setString(1, uid);
			checkin.setString(2, ssid);
			checkin.setLong(3, sqlDate);
			checkin.executeUpdate();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			this.close(conn);
		}
	}
}
