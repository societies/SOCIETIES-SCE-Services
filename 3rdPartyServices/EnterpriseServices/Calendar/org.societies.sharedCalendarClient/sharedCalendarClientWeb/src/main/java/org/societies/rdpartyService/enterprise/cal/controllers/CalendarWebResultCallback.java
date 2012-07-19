package org.societies.rdpartyService.enterprise.cal.controllers;

import java.util.concurrent.Semaphore;

import org.societies.api.ext3p.schema.sharedcalendar.SharedCalendarResult;
import org.societies.rdpartyService.enterprise.interfaces.IReturnedResultCallback;



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
	/** Semaphore for synchronous execution, ugly.*/
	private Semaphore sem = new Semaphore(1, true);
	
	public CalendarWebResultCallback(CalendarWebController controller) {
		this.wc = controller;
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void receiveResult(Object result) {
		if (result instanceof SharedCalendarResult){
			this.wc.setResult((SharedCalendarResult) result);
		}else{
			//Log some info...
		}
		//Work done, release the semaphore for Synchronous Execution
		sem.release();
	}

	public Semaphore getSem() {
		return sem;
	}

}
