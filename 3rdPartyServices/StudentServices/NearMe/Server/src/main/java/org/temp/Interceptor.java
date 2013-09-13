package org.temp;

import org.json.JSONException;
import org.json.JSONObject;
import org.temp.CISIntegeration.CISBridge;
import org.temp.CISIntegeration.CISData;
import org.societies.api.activity.IActivity;


public class Interceptor {

	public static void after(String cmd) {
		try {
			JSONObject jobj = new JSONObject(cmd);
			int opt = jobj.getInt("opetationType");
			if (opt == 3) {// creation
				JSONObject comm = jobj.getJSONObject("communitiesData");
				comm = comm.getJSONArray("allCommunitiesArray")
						.getJSONObject(0);
				if (CISData.getCISData(comm.getString("id")) != null) {
					return;
				}
				new CISData(comm.getString("id"), jobj.getString("senderId"),
						comm.getString("description"));
			} else if (opt == 4) {// join group
				JSONObject comm = jobj.getJSONObject("communitiesData");
				CISData.getCISData(comm.getString("id")).participants.add(jobj
						.getString("senderId"));
			} else if (opt == 5) {// leave group
				JSONObject comm = jobj.getJSONObject("communitiesData");
				CISData.getCISData(comm.getString("id")).participants
						.remove(jobj.getString("senderId"));
			} else if (opt == 6) {// chat in group
				JSONObject comm = jobj;
				if (CISData.getCISData(comm.getString("receiver")) == null)
					return;
				System.err.println(comm.getString("receiver") + "\t"
						+ jobj.getString("senderId"));
				if (CISData.getCISData(comm.getString("receiver")).owner
						.equals(jobj.getString("senderId"))) {
					String content = jobj.getString("chatData");

					if (content.startsWith("migrate ")) {
						String[] givenName = content.split(" ");
						if (givenName.length > 1 && !givenName[1].equals("")) {
							System.out.println("migration:\t" + content);
							CISData inst = CISData.getCISData(comm
									.getString("receiver"));
							inst.givenName = givenName[1];
							CISBridge.migrateCIS(inst);
						}
					}else {
						CISData inst = CISData.getCISData(comm
								.getString("receiver"));
						if(inst.cis!=null){
							IActivity ret = new NearMeActivity();
							ret.setActor(comm.getString("senderId").replace('@','.'));
							ret.setObject(content);
							ret.setTarget(inst.cis.getCisId());
							ret.setPublished("0");
						    inst.cis.getActivityFeed().addActivity(ret); 
						}
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
