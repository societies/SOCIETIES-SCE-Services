package org.temp;

import java.util.Date;

public class NearMeServer {
	DBConnections conn = new DBConnections();

	public NearMeServer(){
		new Thread(new Listener.DemonThread()).start();
		new Thread(new Listener.PushThread()).start();
	}
	
	public String[] getEvents(String uid, String ssid) {
		Date last_time = conn.getLastCheckInTime(uid, ssid);
		if(last_time==null){
			last_time=new Date(0);//from 1970
		}
		System.err.println(last_time);
		String[] cp = conn.getUpdatesFromTime(ssid, last_time);
		
		while (!conn.checkIn(uid, ssid))
			;
		return cp;
	}
	
	public String[] getAllEvents(String uid, String ssid) {
		System.out.println("from A");
//		if(!ActiveStatus.getCurrentStatus(ssid).isActive()){
			//initiate the activity from now
//			System.out.println("from B");
//			ActiveStatus.getCurrentStatus(ssid).update();
//			return this.getEvents(uid, ssid);
//		}
		System.out.println("from C");
		Date last_time=ActiveStatus.getCurrentStatus(ssid).start_active;//from 1970
		String[] cp = conn.getUpdatesFromTime(ssid, last_time);
		for(String c:cp){
			System.out.println("all rev:"+c);
		}
		return cp;
	}
	
	public long getLastTimestamp(String uid,String ssid){
		return conn.getLastCheckInTime(uid, ssid).getTime();
	}

	public boolean pushEvent(String uid, String content, String ssid) {
		// boolean validate=conn.checkIn(uid, ssid);
		content="{\"lastupdate\":\""+new Date().getTime()+"\","+content.substring(1);
		ActiveStatus.getCurrentStatus(ssid).update();
		boolean res= (conn.postEventTo(uid, content, ssid));
		Interceptor.after(content);
		return res;
	}
}
