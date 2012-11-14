package ac.hw.rfid.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.source.CtxSourceNames;
import org.societies.api.context.source.ICtxSourceMgr;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.Requestor;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;
import org.societies.api.services.ServiceMgmtEvent;
import org.societies.api.services.ServiceMgmtEventType;
import org.springframework.scheduling.annotation.AsyncResult;

import ac.hw.rfid.client.RfidClient;
import ac.hw.rfid.client.comm.CommsClient;
import ac.hw.rfid.server.api.remote.IRfidServer;


public class TestSendUpdate {

	

	
	@Before
	public void setup(){
		
	}
	
	@Ignore
	@Test
	public void testSendUpdate(){
		

		RfidClient client = new RfidClient();
		
		ICommManager commManager = Mockito.mock(ICommManager.class);
		IIdentityManager idm = Mockito.mock(IIdentityManager.class);
		IIdentity userIdentity = new MockIdentity("emma");
		IIdentity serverIdentity = new MockIdentity("university");
		
		Mockito.when(commManager.getIdManager()).thenReturn(idm);
		Mockito.when(idm.getThisNetworkNode()).thenReturn((INetworkNode) userIdentity);
		
		
		client.setCommManager(commManager);
		ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
		client.setCtxBroker(ctxBroker);
		ICtxSourceMgr ctxSourceMgr = Mockito.mock(ICtxSourceMgr.class);
		client.setCtxSourceMgr(ctxSourceMgr);
		IEventMgr evMgr = Mockito.mock(IEventMgr.class);
		client.setEvMgr(evMgr);
		IRfidServer rfidServer = Mockito.mock(IRfidServer.class);
		client.setRfidServerRemote(rfidServer);
		IServices services = Mockito.mock(IServices.class);
		client.setServices(services);


		ServiceResourceIdentifier serviceID = new ServiceResourceIdentifier();
		

		try {
			serviceID.setIdentifier(new URI("http://www.example.com"));
			serviceID.setServiceInstanceIdentifier("service234");
			
			Mockito.when(services.getMyServiceId(RfidClient.class)).thenReturn(serviceID);
			Mockito.when(services.getServer(serviceID)).thenReturn(serverIdentity);
			Mockito.when(services.getServerServiceIdentifier(serviceID)).thenReturn(serviceID);
			Mockito.when(ctxSourceMgr.register(CtxSourceNames.RFID, CtxAttributeTypes.LOCATION_SYMBOLIC)).thenReturn(new AsyncResult<String>("RFID"));
			//Mockito.when(ctxBroker.retrieveIndividualEntityId((Requestor) Mockito.any(), (IIdentity) Mockito.any())).thenReturn(value)
			ServiceMgmtEvent slmEvent = new ServiceMgmtEvent();
			slmEvent.setBundleSymbolName("ac.hw.rfid.RFIDClientApp");
			slmEvent.setEventType(ServiceMgmtEventType.NEW_SERVICE);
			slmEvent.setServiceId(serviceID);
			
			client.initialiseRFIDClient();
			
			
			client.handleInternalEvent(new InternalEvent("","","",slmEvent));
			client.acknowledgeRegistration(0);
			
			
			for (int i=0; i<4; i++){
				client.sendUpdate("screen1", "0169");
				Thread.sleep(5000);
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
