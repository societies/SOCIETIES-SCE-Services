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


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;



public class AlchemyAPIParams {
	public static final String OUTPUT_XML = "xml";
	public static final String OUTPUT_RDF = "rdf";
	
	private String url;
	private String html;
	private String text;
	private String outputMode = OUTPUT_XML;
	private String customParameters;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getOutputMode() {
		return outputMode;
	}
	public void setOutputMode(String outputMode) {
		if( !outputMode.equals(AlchemyAPIParams.OUTPUT_XML) && !outputMode.equals(OUTPUT_RDF) ) 
		{
			throw new RuntimeException("Invalid setting " + outputMode + " for parameter outputMode");
		}
		this.outputMode = outputMode;
	}
	public String getCustomParameters() {
		return customParameters;
	}
	
	public void setCustomParameters(String... customParameters) {
		StringBuilder data = new StringBuilder();
		try{
			for (int i = 0; i < customParameters.length; ++i) {
				data.append('&').append(customParameters[i]);
				if (++i < customParameters.length)
					data.append('=').append(URLEncoder.encode(customParameters[i], "UTF8"));
			}
		}
		catch(UnsupportedEncodingException e){
			this.customParameters = "";
			return;
		}
		this.customParameters = data.toString();
	}
	
	public String getParameterString(){
		String retString = "";
		try{
			if(url!=null) retString+="&url="+URLEncoder.encode(url,"UTF-8");
			if(html!=null) retString+="&html="+URLEncoder.encode(html,"UTF-8");
			if(text!=null) retString+="&text="+URLEncoder.encode(text,"UTF-8");
			if(customParameters!=null) retString+=customParameters;
			if(outputMode!=null) retString+="&outputMode="+outputMode;
		}
		catch(UnsupportedEncodingException e ){
			retString = "";
		}
		return retString;
	}
}
