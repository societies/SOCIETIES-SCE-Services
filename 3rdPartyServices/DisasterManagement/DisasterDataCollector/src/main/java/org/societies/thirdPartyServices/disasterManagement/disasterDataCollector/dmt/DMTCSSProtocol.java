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
