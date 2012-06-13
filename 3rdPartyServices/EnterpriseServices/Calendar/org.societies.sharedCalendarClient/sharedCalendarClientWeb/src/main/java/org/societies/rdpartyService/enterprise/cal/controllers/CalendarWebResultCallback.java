package org.societies.rdpartyService.enterprise.cal.controllers;

import org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback;
import org.societies.rdpartyservice.enterprise.sharedcalendar.SharedCalendarResult;

/**
 * 
 * CallBack Class that handles ISharedCalendarClientRich invocations
 *
 * @author gspadotto
 *
 */
public class CalendarWebResultCallback implements IReturnedResultCallback {
	/** The Controller to pass results to */
	private CalendarWebController wc = null;
	
	public CalendarWebResultCallback(CalendarWebController controller) {
		this.wc = controller;
	}

	@Override
	public void receiveResult(Object result) {
		if (result instanceof SharedCalendarResult){
			this.wc.setResult((SharedCalendarResult) result);
		}else{
			//Log some info...
		}
	}

}
