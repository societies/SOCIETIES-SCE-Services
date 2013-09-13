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
package org.societies.enterprise.collabtools.interpretation;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;



	public class AlchemyAPIConceptParams extends AlchemyAPIParams{
		public static final String CLEANED_OR_RAW = "cleaned_or_raw";
		public static final String CLEANED = "cleaned";
		public static final String RAW = "raw";
		public static final String CQUERY = "cquery";
		public static final String XPATH = "xpath";
		
		private Integer maxRetrieve;
		private String sourceText;
		private Boolean showSourceText;
		private String cQuery;
		private String xPath;
		private Boolean linkedData;
		
		public String getSourceText() {
			return sourceText;
		}
		
		public void setSourceText(String sourceText) {
			if( !sourceText.equals(AlchemyAPIConceptParams.CLEANED) && !sourceText.equals(AlchemyAPIConceptParams.CLEANED_OR_RAW) 
					&& !sourceText.equals(AlchemyAPIConceptParams.RAW) && !sourceText.equals(AlchemyAPIConceptParams.CQUERY)
					&& !sourceText.equals(AlchemyAPIConceptParams.XPATH))
			{
				throw new RuntimeException("Invalid setting " + sourceText + " for parameter sourceText");
			}
			this.sourceText = sourceText;
		}
		
		public boolean isShowSourceText() {
			return showSourceText;
		}
		
		public void setShowSourceText(boolean showSourceText) {
			this.showSourceText = showSourceText;
		}
		
		public String getCQuery() {
			return cQuery;
		}
		
		public void setCQuery(String cQuery) {
			this.cQuery = cQuery;
		}
		
		public String getXPath() {
			return xPath;
		}
		
		public void setXPath(String xPath) {
			this.xPath = xPath;
		}
		
		public int getMaxRetrieve() {
			return maxRetrieve;
		}
		
		public void setMaxRetrieve(int maxRetrieve) {
			this.maxRetrieve = maxRetrieve;
		}
		
		public boolean isLinkedData() {
			return linkedData;
		}
		
		public void setLinkedData(boolean linkedData) {
			this.linkedData = linkedData;
		}
		
		public String getParameterString(){
			String retString = super.getParameterString();
			try{
				if(sourceText!=null) retString+="&sourceText="+sourceText;
				if(showSourceText!=null) retString+="&showSourceText="+(showSourceText?"1":"0");
				if(cQuery!=null) retString+="&cquery="+URLEncoder.encode(cQuery,"UTF-8");
				if(xPath!=null) retString+="&xpath="+URLEncoder.encode(xPath,"UTF-8");
				if(maxRetrieve!=null) retString+="&maxRetrieve="+maxRetrieve.toString();
				if(linkedData!=null) retString+="&linkedData="+(linkedData?"1":"0");
			}
			catch(UnsupportedEncodingException e ){
				retString = "";
			}
			return retString;
		}
		
	
		
	}
	


