package mockplatform;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;

public class MockCommManager implements ICommManager{

	@Override
	public boolean UnRegisterCommManager() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addRootNode(XMPPNode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IIdentityManager getIdManager() {
		// TODO Auto-generated method stub
		return new MockIdManager();
	}

	@Override
	public String getInfo(IIdentity arg0, String arg1, ICommCallback arg2)
			throws CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getItems(IIdentity arg0, String arg1, ICommCallback arg2)
			throws CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void register(IFeatureServer arg0) throws CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void register(ICommCallback arg0) throws CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeRootNode(XMPPNode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendIQGet(Stanza arg0, Object arg1, ICommCallback arg2)
			throws CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendIQSet(Stanza arg0, Object arg1, ICommCallback arg2)
			throws CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessage(Stanza arg0, Object arg1)
			throws CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessage(Stanza arg0, String arg1, Object arg2)
			throws CommunicationException {
		// TODO Auto-generated method stub
		
	}

}
