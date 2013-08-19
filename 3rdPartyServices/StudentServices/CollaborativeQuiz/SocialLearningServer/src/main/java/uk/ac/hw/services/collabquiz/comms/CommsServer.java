package uk.ac.hw.services.collabquiz.comms;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.sociallearning.schema.serverbean.SocialLearningServerBean;
import org.societies.api.sociallearning.schema.serverbean.SocialLearningMethodType;


import uk.ac.hw.services.collabquiz.ICollabQuizServer;

public class CommsServer implements IFeatureServer {
	
	private ICollabQuizServer collabQuizServer;
	private ICommManager commsManager;
	private static final Logger log = LoggerFactory.getLogger(CommsServer.class);
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/sociallearning/schema/serverbean"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.sociallearning.schema.serverbean"));



	/*Injection*/
	public ICollabQuizServer getCollabQuizServer() {
		return collabQuizServer;
	}

	public void setCollabQuizServer(ICollabQuizServer collabQuizServer) {
		this.collabQuizServer = collabQuizServer;
	}
	public ICommManager getCommsManager() {
		return commsManager;
	}

	public void setCommsManager(ICommManager commsManager) {
		this.commsManager = commsManager;
	}
	

	public void init() {
		log.debug("COMMS SERVER STARTED");
	
		if(getCommsManager()!=null) 
		{
			log.debug("YAY COMMMGR ISNT NULL!");
		}
		else
		{
			log.debug("BOOO COMMMGR IS NULL");
		}
		if(getCollabQuizServer()!=null)
		{
			log.debug("YAY COLLABQUIZ ISNT NULL!");
		}
		else
		{
			log.debug("BOOO COLLABQUIZ IS NULL");
		}
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			getCommsManager().register(this);
			log.debug("SUCCESSFULLY REGISTERED COMMS");
		} catch (CommunicationException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
		return NAMESPACES;
	}

	@Override
	public List<String> getJavaPackages() {
		// TODO Auto-generated method stub
		return PACKAGES;
	}

	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		log.debug("I have receieved a message:" + (String) payload);
		
	}

	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		
		log.debug("I have picked up a message!");
		if(payload instanceof SocialLearningServerBean) {
			SocialLearningServerBean bean = (SocialLearningServerBean) payload;
			if(bean.getMethod().equals(SocialLearningMethodType.SERVER_SOCKET_INFO_REQUEST)) {
				log.debug("I have recieved a message to send my socket listener info back!");
				bean.setMethod(SocialLearningMethodType.SERVER_SOCKET_INFO);
				bean.setAddress(collabQuizServer.getAddress());
				bean.setPort(collabQuizServer.getPort());
				return bean;
			}
		}
		return null;
	}
	

	@Override
	public Object setQuery(Stanza stanza, Object payload) throws XMPPError {

		return null;
	}
}
