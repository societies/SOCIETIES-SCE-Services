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
package org.societies.collabtools.interpretation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.societies.collabtools.api.IIncrementCtx;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Implementation of interface for increment context information using external NPL analysis
 *
 * @author Chris Lima
 *
 */
public class IncrementCtx implements IIncrementCtx {
	private String _apiKey;
	private static final String _requestUri = "http://access.alchemyapi.com/calls/";

	public static IIncrementCtx GetInstanceFromString(String apiKey) {
		IncrementCtx api = new IncrementCtx();
		api.SetAPIKey(apiKey);

		return api;
	}

	private void SetAPIKey(String apiKey) {
		_apiKey = apiKey;

		if (null == _apiKey || _apiKey.length() < 5) {
			throw new IllegalArgumentException("Too short API key.");
		}		
	}

	/* (non-Javadoc)
	 * @see org.societies.collabtools.api.IIncrementCtx#incrementString(java.lang.String)
	 */
	@Override
	public final String[] incrementString(final String text, EnrichmentTypes type) {
		Document doc = null;
		List<String> stringCollection = new ArrayList<String>();
		try {
			if (type.equals(EnrichmentTypes.CATEGORY)) {
				doc = this.TextGetCategory(text);
			}
			else if (type.equals(EnrichmentTypes.CONCEPT)) {
				doc = this.TextGetRankedConcepts(text);
			}
			else if (type.equals(EnrichmentTypes.TAXONOMY)) {
				doc = this.TextGetRankedTaxonomy(text);
				//TODO: Fix this to get taxonomies above the confidence score
				NodeList nodeResults = doc.getElementsByTagName("label");
				for (int i = 0; i < nodeResults.getLength(); i++) {
					stringCollection.add(nodeResults.item(i).getTextContent().toLowerCase());
				}
//				return stringCollection.toArray(new String[stringCollection.size()]);
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		if (null != doc) {
			NodeList nodeResults = doc.getElementsByTagName("text");
			for (int i = 0; i < nodeResults.getLength(); i++) {
				stringCollection.add(nodeResults.item(i).getTextContent().toLowerCase());
			}
		}
		return stringCollection.toArray(new String[stringCollection.size()]);
	}

	private Document TextGetRankedConcepts(String text) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
		return TextGetRankedConcepts(text, new AlchemyAPIConceptParams());

	}

	private Document TextGetRankedConcepts(String text,	AlchemyAPIConceptParams params) throws IOException, SAXException,
	ParserConfigurationException, XPathExpressionException {		
		CheckText(text);
		params.setText(text);		
		return POST("TextGetRankedConcepts", "text", params);
	}

	private Document TextGetCategory(String text) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException
	{
		return TextGetCategory(text, new AlchemyAPICategoryParams());
	}

	private Document TextGetCategory(String text, AlchemyAPICategoryParams params) throws IOException, SAXException,
	ParserConfigurationException, XPathExpressionException
	{
		CheckText(text);
		params.setText(text);
		return POST("TextGetCategory", "text", params);
	}
	
	private Document TextGetRankedTaxonomy(String text) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException
	{
		return TextGetRankedTaxonomy(text, new AlchemyAPITaxonomyParams());
	}

	private Document TextGetRankedTaxonomy(String text, AlchemyAPITaxonomyParams params) throws IOException, SAXException,
	ParserConfigurationException, XPathExpressionException
	{
		CheckText(text);
		params.setText(text);
		return POST("TextGetRankedTaxonomy", "text", params);
	}
	
	

	private Document POST(String callName, String callPrefix, AlchemyAPIParams params)
			throws IOException, SAXException,
			ParserConfigurationException, XPathExpressionException
			{
		URL url = new URL(_requestUri + callPrefix + "/" + callName);

		HttpURLConnection handle = (HttpURLConnection) url.openConnection();
		handle.setDoOutput(true);

		StringBuilder data = new StringBuilder();

		data.append("apikey=").append(this._apiKey);
		data.append(params.getParameterString());

		handle.addRequestProperty("Content-Length", Integer.toString(data.length()));

		DataOutputStream ostream = new DataOutputStream(handle.getOutputStream());
		ostream.write(data.toString().getBytes());
		ostream.close();

		return doRequest(handle, params.getOutputMode());
			}

	private Document doRequest(final HttpURLConnection handle, final String outputMode)
			throws IOException, SAXException,
			ParserConfigurationException, XPathExpressionException
			{
		DataInputStream istream = new DataInputStream(handle.getInputStream());
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(istream);

		istream.close();
		handle.disconnect();

		XPathFactory factory = XPathFactory.newInstance();

		if(AlchemyAPIParams.OUTPUT_XML.equals(outputMode)) {
			String statusStr = getNodeValue(factory, doc, "/results/status/text()");
			if (null == statusStr || !statusStr.equals("OK")) {
				String statusInfoStr = getNodeValue(factory, doc, "/results/statusInfo/text()");
				if (null != statusInfoStr && statusInfoStr.length() > 0) {
					return null;
					//					throw new IOException("Error making API call: " + statusInfoStr + '.');
				}

				throw new IOException("Error making API call: " + statusStr + '.');
			}
		}
		else if(AlchemyAPIParams.OUTPUT_RDF.equals(outputMode)) {
			String statusStr = getNodeValue(factory, doc, "//RDF/Description/ResultStatus/text()");
			if (null == statusStr || !statusStr.equals("OK")) {
				String statusInfoStr = getNodeValue(factory, doc, "//RDF/Description/ResultStatus/text()");
				if (null != statusInfoStr && statusInfoStr.length() > 0)
					throw new IOException("Error making API call: " + statusInfoStr + '.');

				throw new IOException("Error making API call: " + statusStr + '.');
			}
		}

		return doc;
			}

	private String getNodeValue(final XPathFactory factory, final Document doc, final String xpathStr)
			throws XPathExpressionException
			{
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile(xpathStr);
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList results = (NodeList) result;

		if (results.getLength() > 0 && null != results.item(0))
			return results.item(0).getNodeValue();

		return null;
			}


	private final void CheckText(final String text) {
		if (null == text || text.length() < 5) {
			throw new IllegalArgumentException("Enter some text to analyze. The text must have at least 5 words");
		}
	}

	private class AlchemyAPIParams {
		private static final String OUTPUT_XML = "xml";
		private static final String OUTPUT_RDF = "rdf";

		//		private String url;
		//		private String html;
		private String text;
		private String outputMode = OUTPUT_XML;
		//		private String customParameters;


		public void setText(final String text) {
			this.text = text;
		}
		public String getOutputMode() {
			return outputMode;
		}

		public String getParameterString(){
			String retString = "";
			try{
				//				if(url!=null) retString+="&url="+URLEncoder.encode(url,"UTF-8");
				//				if(html!=null) retString+="&html="+URLEncoder.encode(html,"UTF-8");
				if(null != text) retString+="&text="+URLEncoder.encode(text,"UTF-8");
				//				if(customParameters!=null) retString+=customParameters;
				if(null != outputMode) retString+="&outputMode="+outputMode;
			}
			catch(UnsupportedEncodingException e ){
				retString = "";
			}
			return retString;
		}
	}

	private class AlchemyAPICategoryParams extends AlchemyAPIParams{

		private Integer maxRetrieve;
		private String sourceText;
		private Boolean showSourceText;
		private String cQuery;
		private String xPath;
		private Boolean linkedData;

		public String getParameterString(){
			String retString = super.getParameterString();
			try{
				if(null != sourceText) retString+="&sourceText="+sourceText;
				if(null != showSourceText) retString+="&showSourceText="+(showSourceText?"1":"0");
				if(null != cQuery) retString+="&cquery="+URLEncoder.encode(cQuery,"UTF-8");
				if(null != xPath) retString+="&xpath="+URLEncoder.encode(xPath,"UTF-8");
				if(null != maxRetrieve) retString+="&maxRetrieve="+maxRetrieve.toString();
				if(null != linkedData) retString+="&linkedData="+(linkedData?"1":"0");
			}
			catch(UnsupportedEncodingException e ){
				retString = "";
			}
			return retString;
		}		
	}

	private class AlchemyAPIConceptParams extends AlchemyAPIParams{

		private Integer maxRetrieve;
		private String sourceText;
		private Boolean showSourceText;
		private String cQuery;
		private String xPath;
		private Boolean linkedData;		

		public String getParameterString(){
			String retString = super.getParameterString();
			try{
				if(null != sourceText) retString+="&sourceText="+sourceText;
				if(null != showSourceText) retString+="&showSourceText="+(showSourceText?"1":"0");
				if(null != cQuery) retString+="&cquery="+URLEncoder.encode(cQuery,"UTF-8");
				if(null != xPath) retString+="&xpath="+URLEncoder.encode(xPath,"UTF-8");
				if(null != maxRetrieve) retString+="&maxRetrieve="+maxRetrieve.toString();
				if(null != linkedData) retString+="&linkedData="+(linkedData?"1":"0");
			}
			catch(UnsupportedEncodingException e ){
				retString = "";
			}
			return retString;
		}		
	}
	
	private class AlchemyAPITaxonomyParams extends AlchemyAPIParams{

		private Boolean showSourceText;
		private String target;
		
		public String getParameterString(){
			String retString = super.getParameterString();
			try{
				if(showSourceText!=null) retString+="&showSourceText="+(showSourceText?"1":"0");
				if(target!=null) retString+="&target="+URLEncoder.encode(target, "UTF-8");
						
			}
			catch(UnsupportedEncodingException e ){
				retString = "";
			}
			
			return retString;
		}	
	}
}
