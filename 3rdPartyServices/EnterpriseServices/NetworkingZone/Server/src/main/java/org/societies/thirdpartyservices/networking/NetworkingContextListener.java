package org.societies.thirdpartyservices.networking;

import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkingContextListener  implements CtxChangeEventListener {

	private static Logger LOG = LoggerFactory.getLogger(NetworkingContextListener.class);

	  public void onCreation(CtxChangeEvent event) {
		      LOG.info(event.getId() + ": *** CREATED event ***");
		      }
		  public void onModification(CtxChangeEvent event) {
		      LOG.info(event.getId() + ": *** MODIFIED event ***");
		      }
		  public void onRemoval(CtxChangeEvent event) {
		      LOG.info(event.getId() + ": *** REMOVED event ***");
		      }
		 public void onUpdate(CtxChangeEvent event) {
		     LOG.info(event.getId() + ": *** UPDATED event ***");
		     }
		 }
		 

		

