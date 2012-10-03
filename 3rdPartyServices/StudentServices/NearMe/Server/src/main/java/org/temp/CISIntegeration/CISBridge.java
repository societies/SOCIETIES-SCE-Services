package org.temp.CISIntegeration;

import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CISBridge {
	
	public static boolean migrateCIS(CISData data){
		return migrateCIS(data.owner,data.participants,data.cisName,data.cisDescription);
	}
	
	private static boolean migrateCIS(String owner, Set<String> uids, String cisname,String cisdep) {
		try {
			Future<ICisOwned> cisf=ContextBinder.getCisMgm().createCis(cisname, "student-service", 
					new Hashtable<String, org.societies.api.cis.attributes.MembershipCriteria>(),cisdep);
			ICisOwned cis=cisf.get();
			if(cis==null)
				return false;
			for(String uid:uids)
				cis.addMember(uid, "participant");
			cis.addMember(owner, "owner");
			return true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
