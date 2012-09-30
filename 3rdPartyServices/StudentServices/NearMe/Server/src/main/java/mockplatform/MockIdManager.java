package mockplatform;

import java.util.Set;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityContextMapper;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;

public class MockIdManager implements IIdentityManager{

	@Override
	public INetworkNode fromFullJid(String arg0) throws InvalidFormatException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IIdentity fromJid(String arg0) throws InvalidFormatException {
		// TODO Auto-generated method stub
		MockIdentity mi=new MockIdentity(IdentityType.CSS,arg0.split("@")[0],arg0.split("@")[1]);
		return mi;
	}

	@Override
	public INetworkNode getCloudNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INetworkNode getDomainAuthorityNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IIdentityContextMapper getIdentityContextMapper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IIdentity> getPublicIdentities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INetworkNode getThisNetworkNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMine(IIdentity arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IIdentity newMemorableIdentity(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IIdentity newTransientIdentity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean releaseMemorableIdentity(IIdentity arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
