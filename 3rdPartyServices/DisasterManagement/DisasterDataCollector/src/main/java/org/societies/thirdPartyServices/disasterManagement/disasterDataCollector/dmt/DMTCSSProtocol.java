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

package org.societies.thirdPartyServices.disasterManagement.disasterDataCollector.dmt;

public class DMTCSSProtocol {
	
	// CSS2DMT
	public static String TRIGGER_SENDPOIS = "TSP";
	public static String SET_VIEW = "STV";
	
	// DMT2CSS
	public static String SENDPOIS_MANUALLY_TRIGGERED = "SPT";
	public static String ADD_POI ="ADP";
	public static String SEND_POI = "SDP";
	public static String SEND_VIEW = "SDV";
	public static String VIEW_LOADED = "VL";
	public static String GPS_STATUS = "GPS";
	public static String POSITION_UPDATE = "PU";
	public static String COMPASS_STATUS = "COP";
	public static String DIRECTION_UPDATE = "DU";
	
	
	public static final String DELIMITER = ";";
	
	public String protocolType;
	public int payloadSize;
	
	DMTCSSProtocol (String protocolType, int payloadSize){
		
		this.protocolType = protocolType;
		this.payloadSize = payloadSize;
		
	}
	
	public DMTCSSProtocol (byte[] bytearray){
		String header = new String(bytearray);
//		System.out.println("[DMTPHeader] Header: " + header);
		
		String[] splittedHeader = header.split(DELIMITER);
		
		protocolType = splittedHeader[0];
//		System.out.println("Messagetype: " + protocolType);
		payloadSize = new Integer(splittedHeader[1]);
//		System.out.println("PayloadSize[byte]: " + payloadSize);
	}
	
	private byte[] calculateHeader(){
		
		StringBuilder headerBuilder = new StringBuilder();
		headerBuilder.append(protocolType);
		headerBuilder.append(DELIMITER);
		headerBuilder.append(payloadSize);
		
		int headerLength = headerBuilder.length();
		
		byte[] output = new byte[headerLength];
		System.arraycopy(headerBuilder.toString().getBytes(), 0, output, 0, headerLength);
		
		return output;
	}
	
	public byte[] calculateHeaderAndSize(){
		byte[] headerBytes = calculateHeader();
		byte[] headerSize = Tools.intToByte(headerBytes.length);
		
		byte[] totalHeader = new byte[headerBytes.length + headerSize.length];
		
		System.arraycopy(headerSize, 0, totalHeader, 0, headerSize.length);
		System.arraycopy(headerBytes, 0, totalHeader, headerSize.length, headerBytes.length);
		
		return totalHeader;
	}
	
	
	

}
