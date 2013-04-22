/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.thirdPartyServices.disasterManagement.analyzeThis.xmlrpc;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.thirdPartyServices.disasterManagement.analyzeThis.AnalyzeThis;

@SuppressWarnings("unused")
public class TrustHandler {
	private static final String SOCIETIES_DOMAIN = ".societies.local";
	private static final Logger LOG = LoggerFactory.getLogger(TrustHandler.class);

	public String recalculateTrust(String reporting_user_id, String required_user_id, int value) {
//		LOG.debug("user_id " + reporting_user_id + " reported: recalculate trust for user_id " + recalculate_user_id + " --- value = " + value);
		
		String feedback = "<br>";
		
		TrustedEntityId questionerUserTEID = null;
		TrustedEntityId requiredUserTEID = null;
		try {
//			feedback += "CommMgr "+AnalyzeThis.getInstance().getCommMgr().toString()+ " <br>";
			questionerUserTEID = new TrustedEntityId(TrustedEntityType.CSS, AnalyzeThis.getInstance().getCommMgr().getIdManager() .fromJid(
					AnalyzeThis.getInstance().getCommMgr().getIdManager().getThisNetworkNode().getBareJid()).toString());
			requiredUserTEID = new TrustedEntityId(TrustedEntityType.CSS, required_user_id.substring(0,required_user_id.indexOf('@')) + SOCIETIES_DOMAIN);
		} catch (MalformedTrustedEntityIdException e1) {
			LOG.error(e1.getMessage());
		} catch (InvalidFormatException e1) {
			LOG.error(e1.getMessage());
		}
		feedback += "questioner_user trustId "+questionerUserTEID.getEntityId()+ " <br>";
		feedback += "required_user trustId "+requiredUserTEID.getEntityId()+ " <br>";
		
		feedback += "TrustEvidenceCollector "+AnalyzeThis.getInstance().getTrustEvidenceCollector()+" <br>";
		if (AnalyzeThis.getInstance().getTrustEvidenceCollector() != null)
			try {
				AnalyzeThis.getInstance().getTrustEvidenceCollector().addDirectEvidence(questionerUserTEID, requiredUserTEID, TrustEvidenceType.RATED, new Date(), new Double(value/100));
			} catch (TrustException e) {
				LOG.error(e.getMessage());
			}
		else
			LOG.error("No connection to TrustEvidenceCollector");

//		return feedback;
		return getTrust(reporting_user_id, required_user_id);
	}

	public String getTrust(String questioner_user_id, String required_user_id) {
//		LOG.debug("questioner_user_id " + questioner_user_id + " | required_user_id " + required_user_id);
		IIdentityManager idMgr = AnalyzeThis.getInstance().getCommMgr().getIdManager();
		
		TrustedEntityId questionerUserTEID = null;
		TrustedEntityId requiredUserTEID = null;
		Double trustResult = 0.0;
		
		String feedback = "<br>";
		
		try {
//			feedback += "CommMgr "+AnalyzeThis.getInstance().getCommMgr().toString()+ " <br>";
			questionerUserTEID = new TrustedEntityId(TrustedEntityType.CSS, idMgr.fromJid(idMgr.getThisNetworkNode().getBareJid()).toString());
			requiredUserTEID = new TrustedEntityId(TrustedEntityType.CSS, required_user_id.substring(0,required_user_id.indexOf('@')) + SOCIETIES_DOMAIN);
			feedback += "questioner_user trustId "+questionerUserTEID.getEntityId()+ " <br>";
			feedback += "required_user trustId "+requiredUserTEID.getEntityId()+ " <br>";
			
	//		feedback += "TrustBroker "+AnalyzeThis.getInstance().getTrustBroker().toString()+ " <br>";

			Requestor requestor = new Requestor(idMgr.fromJid(idMgr.getThisNetworkNode().getBareJid()));

			//trustResult = AnalyzeThis.getInstance().getTrustBroker().retrieveTrust(questionerUserTEID, requiredUserTEID).get();
			trustResult = AnalyzeThis.getInstance().getTrustBroker().retrieveTrustValue(requestor, questionerUserTEID, requiredUserTEID, TrustValueType.USER_PERCEIVED).get();
			feedback += " ........ TRUST RESULT "+trustResult+"<br>";
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		} catch (ExecutionException e) {
			LOG.error(e.getMessage());
		} catch (TrustException e) {
			LOG.error(e.getMessage());
		} catch (InvalidFormatException e1) {
			LOG.error(e1.getMessage());
		}

//		return feedback;
		return trustResult * 100 + "";
	}
}