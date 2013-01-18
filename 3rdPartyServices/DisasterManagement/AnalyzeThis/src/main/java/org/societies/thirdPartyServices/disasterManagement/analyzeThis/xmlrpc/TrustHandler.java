package org.societies.thirdPartyServices.disasterManagement.analyzeThis.xmlrpc;

import org.apache.log4j.Logger;

public class TrustHandler {
	Logger logger = Logger.getLogger(this.getClass());

	public String recalculateTrust(String reporting_user_id, String recalculate_user_id, int value) {
		logger.debug("user_id "+ reporting_user_id+" reported: recalculate trust for user_id " + recalculate_user_id + " --- value = " + value);
		return "new value " + value;
	}
	
	public String getTrust(String questioner_user_id, String required_user_id) {
		logger.debug("questioner_user_id " + questioner_user_id +" | required_user_id " + required_user_id);
		return ((int) (Math.random()*100)) + "";
	}
}