package org.societies.thirdpartyservices.ijacketlib;

public final class IJacketDefines {

	public static final String IJACKET_INTENT ="ijacketlib.intent.action";

	public static final class IjacketIntentExtras{
		
		// local id representing the community to be used
		public static final String CIS_ID = "CIS_ID";
	}

	
	public static final class AccountData{
		
		// local id representing the community to be used
		public static final String ACCOUNT_TYPE = "com.box";
		public static final String IJACKET_CLIENT_SERVICE_NAME = "org.societies.thirdpartyservices.ijacketclient";
		public static final String IJACKET_SERVICE_NAME = "org.societies.thirdpartyservices.ijacket";
	}
	
	public static final class Verbs{
		
		public static final String DISPLAY = "JACKET_DISPLAY";
		public static final String VIBRATE = "JACKET_VIBRATE";
		public static final String RING = "JACKET_RING";
	}

}
