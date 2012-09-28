package org.temp;

import org.json.JSONException;
import org.json.JSONObject;
import org.temp.CISIntegeration.CISBridge;
import org.temp.CISIntegeration.CISData;

public class Interceptor {
	
	public static void after(String cmd){
		try {
			JSONObject jobj= new JSONObject(cmd);
			int opt=jobj.getInt("opetationType");
			if(opt==3){//creation
				JSONObject comm=jobj.getJSONObject("communitiesData");
				if(CISData.getCISData(comm.getString("name"))!=null){
					return;
				}
				new CISData(comm.getString("name"),
						comm.getString("nameAdministrator"),
						comm.getString("description"));
			}else if(opt==4){//join group
				JSONObject comm=jobj.getJSONObject("communitiesData");
				CISData.getCISData(comm.getString("name")).participants.add(
						jobj.getString("senderId"));
			}else if(opt==5){//leave group
				JSONObject comm=jobj.getJSONObject("communitiesData");
				CISData.getCISData(comm.getString("name")).participants.remove(
						jobj.getString("senderId"));
			}else if(opt==6){//chat in group
				JSONObject comm=jobj.getJSONObject("communitiesData");
				if(CISData.getCISData(comm.getString("name")).owner.equals(
						jobj.getString("senderId"))){
					String content=jobj.getString("chatData");
					if(content.equals("migrate"))
						CISBridge.migrateCIS(CISData.getCISData(comm.getString("name")));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
