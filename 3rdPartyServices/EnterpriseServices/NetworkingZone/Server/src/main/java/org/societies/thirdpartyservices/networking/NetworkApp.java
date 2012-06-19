package org.societies.thirdpartyservices.networking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.thirdpartyservices.api.internal.networking.INetworkingDirectory;
import org.societies.thirdpartyservices.schema.networking.UserRecordResult;
import org.societies.thirdpartyservices.schema.networking.UserResult;


import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;


public class NetworkApp 
{

	
	// PRIVATE VARIABLES
	private ICommManager commManager;
	private INetworkingDirectory networkingDirectory;
	private ICtxBroker ctxBroker;
	

	
	private static Logger LOG = LoggerFactory
			.getLogger(NetworkApp.class);
	
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	
	public INetworkingDirectory getNetworkingDirectory() {
		return networkingDirectory;
	}

	public void setNetworkingDirectory(INetworkingDirectory networkingDirectory) {
		this.networkingDirectory = networkingDirectory;
	}

	

	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	// METHODS
	public NetworkApp() {
		
		//, g1, arg2).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC);
	//	List<CtxIdentifier> attrs = futureAttrs.get();
		
		
	}
	
	
	public void createNewAccount(String userName)
	{
		int userId = 0;
		
		Future<UserRecordResult> userRecordResult = this.getNetworkingDirectory()
				.getUserRecord(userId);
		try {
			//Make sure username not taken
			UserRecordResult result = userRecordResult.get();
			if (result == null || result.getResult() == UserResult.USER_NOT_FOUND)
			{
				// This is a new user,
				//We need to create a record for then
				
				//then call the privacy negotiation
				doPrivacyNegotiation();
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public void Login(String user)
	{
	
		int userId = 0;
		
		Future<UserRecordResult> userRecordResult = this.getNetworkingDirectory()
				.getUserRecord(userId);
		try {
			UserRecordResult result = userRecordResult.get();
			if (result == null || result.getResult() == UserResult.USER_NOT_FOUND)
			{
				//return user not found
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// We found the user
		// 
		
		
		
	// then log in
	// User agent or Cloud container log in ?
	
	//Next do stuff with the context data
	// Use the context data to figure out what zone they are oin and who is there
	// who then know , who then might want to know and feed that back to browser
		
		//TODO : Fix this up, not sure what this will do!
		Future<List<CtxIdentifier>> asynchCtxIdent;
		try {
			asynchCtxIdent = this.getCtxBroker().lookup(new Requestor(getCommManager().getIdManager().getThisNetworkNode()),
					getCommManager().getIdManager().fromJid("maria.societies.local"), CtxModelType.ATTRIBUTE,
					CtxAttributeTypes.LOCATION_SYMBOLIC.toString());
			List<CtxIdentifier> ctxIdent = asynchCtxIdent.get();
			this.getCtxBroker().registerForChanges(new Requestor(getCommManager().getIdManager().getThisNetworkNode()), new NetworkingContextListener(), ctxIdent.get(0));
			
		} catch (CtxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	}
	
	
	
	
	public boolean doPrivacyNegotiation()
	{
		/// seems like I should call 
		// org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyNegotiationManager.negotiateServicePolicy
		// here but the RequestorService will be the same for every user, 
		// how do we get the private negotiation when the cient is a browser??
		
		// For now we'll ignore all that are just always return true
		return true;
	}
	
	
	public void doContextStuff()
	{
		
		
	}
	
}