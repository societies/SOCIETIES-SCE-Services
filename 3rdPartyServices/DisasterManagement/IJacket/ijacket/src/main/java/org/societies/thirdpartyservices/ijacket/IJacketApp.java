package org.societies.thirdpartyservices.ijacket;

import org.societies.android.api.cis.SocialContract;
import org.societies.thirdpartyservices.ijacket.com.BluetoothConnection;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class IJacketApp extends Application {
	
	BluetoothConnection con = null;
	
	//String macAddress = "";
	
	//int ledPin = -1;
	//int vibrationPin = -1;
	//int speakersPin = -1;
	
/*	String selectedCommunityLocalId = "";
	
	String selectCommunityJid = "";*/
	
	long iJacketSevId = -1;
	
	boolean testMode = false; // if true means that there is no jacket
	
	public static final String CIS_JID_PREFERENCE_TAG = "CisJID";
	public static final String MAC_PREFERENCE_TAG = "MacAddress";
	public static final String PREF_FILE_NAME = "ijackPref";
	
	
	
	

	
	
/*
	public String getSelectedCommunityLocalId() {
		return selectedCommunityLocalId;
	}

	public void setSelectedCommunityLocalId(String selectedCommunityLocalId) {
		this.selectedCommunityLocalId = selectedCommunityLocalId;
	}

	public String getSelectCommunityJid() {
		return selectCommunityJid;
	}

	public void setSelectCommunityJid(String selectCommunityJid) {
		this.selectCommunityJid = selectCommunityJid;
	}*/

	public long getiJacketSevId() {
		return iJacketSevId;
	}

	public void setiJacketSevId(long iJacketSevId) {
		this.iJacketSevId = iJacketSevId;
	}

	public boolean isTestMode() {
		return testMode;
	}

	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}

	public BluetoothConnection getCon() {
		return con;
	}

	public void setCon(BluetoothConnection con) {
		this.con = con;
	}

	/*	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public int getLedPin() {
		return ledPin;
	}

	public void setLedPin(int ledPin) {
		this.ledPin = ledPin;
	}

	public int getVibrationPin() {
		return vibrationPin;
	}

	public void setVibrationPin(int vibrationPin) {
		this.vibrationPin = vibrationPin;
	}

	public int getSpeakersPin() {
		return speakersPin;
	}

	public void setSpeakersPin(int speakersPin) {
		this.speakersPin = speakersPin;
	}*/
	


}
