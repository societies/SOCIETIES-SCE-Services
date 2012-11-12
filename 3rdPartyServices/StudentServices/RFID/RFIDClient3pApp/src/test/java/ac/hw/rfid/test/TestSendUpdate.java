package ac.hw.rfid.test;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.context.source.ICtxSourceMgr;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.services.IServices;

import ac.hw.rfid.client.RfidClient;
import ac.hw.rfid.client.comm.CommsClient;


public class TestSendUpdate {

	

	
	@Before
	public void setup(){
		
	}
	
	@Ignore
	@Test
	public void testSendUpdate(){
		

		ICtxSourceMgr ctxSourceMgr = Mockito.mock(ICtxSourceMgr.class);
		IServices services = Mockito.mock(IServices.class);
		ICommManager commManager = Mockito.mock(ICommManager.class);
		IIdentityManager idm = Mockito.mock(IIdentityManager.class);
		ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
		Mockito.when(commManager.getIdManager()).thenReturn(idm);
		
		RfidClient rfidClient = new RfidClient();
		
		
		rfidClient.setCtxSourceMgr(ctxSourceMgr);
		rfidClient.setServices(services);
		rfidClient.setCommManager(commManager);
		rfidClient.setCtxBroker(ctxBroker);
		
		CommsClient commsClient = new CommsClient();
		commsClient.setCommManager(commManager);
		commsClient.InitService();
		
		rfidClient.setRfidServerRemote(commsClient);
		
		rfidClient.initialiseRFIDClient();
		
	}
	
}
