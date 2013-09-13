
package org.societies.ext3p.nzone.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NZoneCxtChangeList implements CtxChangeEventListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(NZoneCxtChangeList.class);
	
	
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
			
			if (client.waitingToStart.tryAcquire())
			{
				// Only one thread needs to wait, all others can ignore the location change, 
				// if mutiple events are generated while we are waiting, we will alway read the latest value
				// of symbolic location, so only need to act on these multiple changes once
				
				// We have indicated we are waiting to start, now we need to wait until any running locationChanged are running
				try {
					client.busy.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//ready to start, release waitingToStart so another thread can wait
				client.waitingToStart.release();
				client.locationChanged();
				client.busy.release();
			}
		}
	}
	
}	
	
