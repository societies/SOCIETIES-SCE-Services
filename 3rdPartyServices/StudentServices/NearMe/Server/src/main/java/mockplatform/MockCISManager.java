package mockplatform;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;

public class MockCISManager implements ICisManager{

	@Override
	public Future<ICisOwned> createCis(final String arg0, String arg1,
			Hashtable<String, MembershipCriteria> arg2, String arg3) {
		// TODO Auto-generated method stub
		
		return new Future<ICisOwned>(){

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public ICisOwned get() throws InterruptedException,
					ExecutionException {
				// TODO Auto-generated method stub
				return new MockCISOwned(arg0);
			}

			@Override
			public ICisOwned get(long timeout, TimeUnit unit)
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
	public Future<ICisOwned> createCis(String arg0, String arg1,
			Hashtable<String, MembershipCriteria> arg2, String arg3, String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteCis(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ICis getCis(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ICis> getCisList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ICisOwned> getListOfOwnedCis() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICisOwned getOwnedCis(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ICis> getRemoteCis() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void joinRemoteCIS(CisAdvertisementRecord arg0,
			ICisManagerCallback arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void leaveRemoteCIS(String arg0, ICisManagerCallback arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ICisOwned> searchCisByMember(IIdentity arg0)
			throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ICis> searchCisByName(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getListOfMembers(Requestor arg0, IIdentity arg1,
			ICisManagerCallback arg2) {
		// TODO Auto-generated method stub
		
	}

}
