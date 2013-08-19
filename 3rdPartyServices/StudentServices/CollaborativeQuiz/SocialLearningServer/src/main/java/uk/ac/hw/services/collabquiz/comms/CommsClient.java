package uk.ac.hw.services.collabquiz.comms;

import java.util.List;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;

import org.societies.api.identity.IIdentity;

public class CommsClient implements ICommCallback {
	
	private ICommManager commManager;
	
	public CommsClient(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	public void sendToClient(IIdentity toIdentity, int serverPort, String serverAddress){
	//	SocialLearningServerBean serverBean = new SocialLearningServerBean();
	//	serverBean.setMethod(SocialLearningMethodType.SERVER_SOCKET_INFO);
	//	serverBean.setPort(serverPort);
	//	serverBean.setAddress(serverAddress);
	//	Stanza stanza = new Stanza(toIdentity);
	//	
	//	try {
	//		commManager.sendMessage(stanza, serverBean);
	//	} catch (CommunicationException e) {
	//		// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	}
	}

	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getJavaPackages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void receiveResult(Stanza stanza, Object payload) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveError(Stanza stanza, XMPPError error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveItems(Stanza stanza, String node, List<String> items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		// TODO Auto-generated method stub
		
	}

}
