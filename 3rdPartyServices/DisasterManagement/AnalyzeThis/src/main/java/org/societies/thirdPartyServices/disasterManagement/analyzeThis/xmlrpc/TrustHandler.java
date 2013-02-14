package org.societies.thirdPartyServices.disasterManagement.analyzeThis.xmlrpc;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.privacytrust.trust.ITrustBroker;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.springframework.beans.factory.annotation.Autowired;

public class TrustHandler {
	private static final String SOCIETIES_DOMAIN = ".societies.local";
	Logger logger = LoggerFactory.getLogger(TrustHandler.class);
	@Autowired(required = true)
	private ITrustEvidenceCollector trustEvidenceCollector;
	@Autowired(required = true)
	private ITrustBroker trustBroker;
	@Autowired(required = true)
	private ICommManager commMgr;
	private TrustedEntityId trustorId;

	public String recalculateTrust(String reporting_user_id,
			String recalculate_user_id, int value) {
		logger.debug("user_id " + reporting_user_id
				+ " reported: recalculate trust for user_id "
				+ recalculate_user_id + " --- value = " + value);

		TrustedEntityId trustedCssId1 = null;
		try {
			if (trustorId == null)
				this.trustorId = new TrustedEntityId(TrustedEntityType.CSS,
						commMgr.getIdManager()
								.fromJid(
								// "trustorCss@societies.local");
										commMgr.getIdManager()
												.getThisNetworkNode()
												.getBareJid()).toString());

			trustedCssId1 = new TrustedEntityId(
					TrustedEntityType.CSS, commMgr
							.getIdManager()
							.fromJid(
									recalculate_user_id.substring(0,
											recalculate_user_id.indexOf('@'))
											+ SOCIETIES_DOMAIN).toString());

		} catch (MalformedTrustedEntityIdException e) {
			logger.error(e.getMessage());
		} catch (InvalidFormatException e) {
			logger.error(e.getMessage());
		}
		
		if (trustEvidenceCollector != null)
			try {
				trustEvidenceCollector.addDirectEvidence(trustorId, trustedCssId1,
						TrustEvidenceType.RATED, new Date(), new Double(value/100));
			} catch (TrustException e) {
				logger.error(e.getMessage());
			}
		else
			logger.error("No connection to TrustEvidenceCollector");

		return getTrust(reporting_user_id, recalculate_user_id);
	}

	public String getTrust(String questioner_user_id, String required_user_id) {
		logger.debug("questioner_user_id " + questioner_user_id
				+ " | required_user_id " + required_user_id);

		if (trustorId == null)
			try {
				this.trustorId = new TrustedEntityId(TrustedEntityType.CSS,
						commMgr.getIdManager()
								.fromJid(
								// "trustorCss@societies.local");
										commMgr.getIdManager()
												.getThisNetworkNode()
												.getBareJid()).toString());
			} catch (MalformedTrustedEntityIdException e1) {
				logger.error(e1.getMessage());
			} catch (InvalidFormatException e1) {
				logger.error(e1.getMessage());
			}

		Double trustResult = Math.random();
		if (trustBroker != null)
			try {
				trustResult = trustBroker.retrieveTrust(
						this.trustorId,
						new TrustedEntityId(TrustedEntityType.CSS,
								required_user_id.substring(0,
										required_user_id.indexOf('.'))
										+ SOCIETIES_DOMAIN)).get();
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			} catch (ExecutionException e) {
				logger.error(e.getMessage());
			} catch (TrustException e) {
				logger.error(e.getMessage());
			}
		else
			logger.error("No connection to TrustBroker");

		return trustResult * 100 + "";
	}

	/**
	 * @return the trustEvidenceCollector
	 */
	public ITrustEvidenceCollector getTrustEvidenceCollector() {
		return trustEvidenceCollector;
	}

	/**
	 * @param trustEvidenceCollector
	 *            the trustEvidenceCollector to set
	 */
	public void setTrustEvidenceCollector(
			ITrustEvidenceCollector trustEvidenceCollector) {
		this.trustEvidenceCollector = trustEvidenceCollector;
	}

	/**
	 * @return the trustBroker
	 */
	public ITrustBroker getTrustBroker() {
		return trustBroker;
	}

	/**
	 * @param trustBroker
	 *            the trustBroker to set
	 */
	public void setTrustBroker(ITrustBroker trustBroker) {
		this.trustBroker = trustBroker;
	}
}