package mockplatform;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.societies.api.activity.IActivityFeed;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.identity.Requestor;
import org.societies.api.schema.cis.community.Community;

public class MockCISOwned implements ICisOwned{

	private String name=null;
	public MockCISOwned(String name){
		this.name=name;
	}
	@Override
	public IActivityFeed getActivityFeed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCisId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getInfo(ICisManagerCallback arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getListOfMembers(ICisManagerCallback arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getMembershipCriteria(ICisManagerCallback arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInfo(Community arg0, ICisManagerCallback arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addCriteria(String arg0, MembershipCriteria arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Future<Boolean> addMember(String arg0, String arg1)
			throws CommunicationException {
		// TODO Auto-generated method stub
		System.err.println("name:"+this.name+"\tuser:"+arg0+"\trole"+arg1);
		
		return new Future<Boolean>(){
			public Boolean get(){
				return true;
			}

			@Override
			public boolean cancel(boolean arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Boolean get(long arg0, TimeUnit arg1)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isCancelled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isDone() {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}

	@Override
	public boolean checkQualification(HashMap<String, String> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCisType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Set<ICisParticipant>> getMemberList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOwnerId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeCriteria(String arg0, MembershipCriteria arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Future<Boolean> removeMemberFromCIS(String arg0)
			throws CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setCisType(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDescription(String arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void getListOfMembers(Requestor arg0, ICisManagerCallback arg1) {
		// TODO Auto-generated method stub
		
	}

}
