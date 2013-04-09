
package org.societies.ext3p.nzone.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class is used to acquire trust evidence based on the CSS owner's direct
 * interactions with other CSSs, the CISs they're member of, and their
 * installed services. More specifically, it adds {@link DirectTrustEvidence}
 * to the Trust Evidence Repository by monitoring the following context change
 * events:
 * <ol>
 * <li>CSS owner uses a service</li>
 * <li>CSS owner (un)friends another CSS.</li>
 * <li>CSS owner joins/leaves a CIS.</li>
 * <li>Membership changes in a CIS the CSS owner is member of.</li>
 * </ol>
 * 
 * The generated pieces of Direct Trust Evidence are then processed by the
 * Direct Trust Engine in order to (re)evaluate the direct trust on the
 * referenced entities, i.e. CSS, CISs or services, on behalf of the CSS owner.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS) 
 * @since 0.4.1
 */
@Service
public class NZoneCxtChangeList implements CtxChangeEventListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(NZoneCxtChangeList.class);
	
	/** The time to wait between registration attempts for membership changes (in seconds) */
	private static final long WAIT = 60l;
	public NZoneClient client;
	
	@Autowired
	NZoneCxtChangeList() throws Exception {
		
	}

	/*
	 * @see org.societies.api.context.event.CtxChangeEventListener#onCreation(org.societies.api.context.event.CtxChangeEvent)
	 */
	@Override
	public void onCreation(CtxChangeEvent event) {
		LOG.info("Received CREATED event " + event);
	}

	/*
	 * @see org.societies.api.context.event.CtxChangeEventListener#onUpdate(org.societies.api.context.event.CtxChangeEvent)
	 */
	@Override
	public void onUpdate(CtxChangeEvent event) {
			LOG.info("Received UPDATED event start" + event);
			
			// now we want to change zone
			//client.locationChanged();
			
			new Thread(new LocationChangedHandler(client)).start();
			
			LOG.info("Received UPDATED event end " + event);
	}

	/*
	 * @see org.societies.api.context.event.CtxChangeEventListener#onModification(org.societies.api.context.event.CtxChangeEvent)
	 */
	@Override
	public void onModification(CtxChangeEvent event) {
		LOG.info("Received MODIFIED event " + event);
	}

	/*
	 * @see org.societies.api.context.event.CtxChangeEventListener#onRemoval(org.societies.api.context.event.CtxChangeEvent)
	 */
	@Override
	public void onRemoval(CtxChangeEvent event) {
		LOG.info("Received REMOVED event " + event);
	}
	
	
	private class LocationChangedHandler implements Runnable {
		
		public NZoneClient client;
		
		private LocationChangedHandler(NZoneClient client) {

			this.client = client;
		}
		
		@Override
		public void run() {
			client.locationChanged();

		}
	}
}	
	
