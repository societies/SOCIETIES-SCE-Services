/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp.,
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
package si.stecce.societies.crowdtasking.model;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 *
 */
public enum AuthenticationPrvider {
	SOCIETIES("SOCIETIES"),
	GOOGLE("https://www.google.com/accounts/o8/id"),
	YAHOO("yahoo.com"),
	MYOPENID("myopenid.com"),
	MYSPACE("myspace.com"),
	LOCAL("development"),
	UNKNOWN("");
	
	private String federatedIdenty;
	
	AuthenticationPrvider(String federatedIdenty) {
		this.federatedIdenty = federatedIdenty;
	}
	
	String getFederatedIdenty() {
		return federatedIdenty;
	}
	
	public static AuthenticationPrvider getAuthenticationPrvider(String federatedIdenty) {
		if (federatedIdenty.startsWith("SOCIETIES")) {
			return AuthenticationPrvider.SOCIETIES;
		}
		if (federatedIdenty.startsWith("https://www.google.com")) {
			return AuthenticationPrvider.GOOGLE;
		}
		if (federatedIdenty.contains("yahoo.com")) {
			return AuthenticationPrvider.YAHOO;
		}
		if (federatedIdenty.contains("myopenid.com")) {
			return AuthenticationPrvider.MYOPENID;
		}
		if (federatedIdenty.contains("myspace.com")) {
			return AuthenticationPrvider.MYSPACE;
		}
		if (federatedIdenty.startsWith("gugl")) {
			return AuthenticationPrvider.LOCAL;
		}
		return AuthenticationPrvider.UNKNOWN;
	}
}
