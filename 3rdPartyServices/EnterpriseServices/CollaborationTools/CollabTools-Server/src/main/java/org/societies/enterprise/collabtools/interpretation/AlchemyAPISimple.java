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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AlchemyAPISimple {

	private String _apiKey;
	private static final String _requestUri = "http://access.alchemyapi.com/calls/";

	public static AlchemyAPISimple GetInstanceFromString(String apiKey) {
		AlchemyAPISimple api = new AlchemyAPISimple();
		api.SetAPIKey(apiKey);

		return api;
	}

	private void SetAPIKey(String apiKey) {
		_apiKey = apiKey;

		if (null == _apiKey || _apiKey.length() < 5)
			throw new IllegalArgumentException("Too short API key.");		
	}

	public Document TextGetRankedConcepts(String text) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
		return TextGetRankedConcepts(text, new AlchemyAPIConceptParams());

	}

	private Document TextGetRankedConcepts(String text,
			AlchemyAPIConceptParams params) throws IOException, SAXException,
			ParserConfigurationException, XPathExpressionException {		
		CheckText(text);		
		params.setText(text);		
		return POST("TextGetRankedConcepts", "text", params);
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

	private Document doRequest(HttpURLConnection handle, String outputMode)
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

	private String getNodeValue(XPathFactory factory, Document doc, String xpathStr)
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


	private void CheckText(String text) {
		if (null == text || text.length() < 5)
			throw new IllegalArgumentException("Enter some text to analyze. The text must have at least 5 words");		
	}

}
