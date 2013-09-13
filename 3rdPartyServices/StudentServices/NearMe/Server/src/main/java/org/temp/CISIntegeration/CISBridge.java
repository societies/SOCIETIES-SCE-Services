package org.temp.CISIntegeration;

import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.temp.CISIntegeration.*;


public class CISBridge {
	
	public static boolean migrateCIS(CISData data){
		if(data.cis!=null){
			return true;
		}
		ICisOwned ciso= migrateCIS(data.owner,data.participants,data.givenName,data.cisDescription);
		data.cis=ciso;
		if(ciso==null)
			return false;
		return true;
	}
	
	private static ICisOwned migrateCIS(String owner, Set<String> uids, String cisname,String cisdep) {
		try {
			Future<ICisOwned> cisf=ContextBinder.instance.getCisMgm().createCis(cisname, "student-service", 
					new Hashtable<String, org.societies.api.cis.attributes.MembershipCriteria>(),cisdep);
			ICisOwned cis=cisf.get();
			if(cis==null)
				return null;
			for(String uid:uids){
				if(!uid.equals(owner))
					cis.addMember(uid.replace('@','.'), "participant");
			}
			cis.addMember(owner.replace('@','.'), "owner");
			return cis;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
