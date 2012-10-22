package org.societies.thirdpartyservices.ijacket;

import org.societies.thirdpartyservices.ijacket.com.BluetoothConnection;

import android.app.Application;

public class IJacketApp extends Application {
	
	BluetoothConnection con;
	
	String macAddress = "";
	
	int ledPin = -1;
	int vibrationPin = -1;
	int speakersPin = -1;
	
	String selectedCommunityLocalId = "";
	
	String selectCommunityJid = "";
	
	static final int MAX_RETRY = 5;
	
	int retrycounter = 0;
	boolean conectStatus = false;
	
	
	
	

	public boolean isConectStatus() {
		return conectStatus;
	}

	/*public void setConectStatus(boolean conectStatus) {
		this.conectStatus = conectStatus;
	}*/
	
	public void registerConnect(){
		conectStatus = true;
		retrycounter = 0;
	}

	public void registerDisconnect(){
		conectStatus = false;
	}


	public int getRetrycounter() {
		return retrycounter;
	}

	public void clearRetryCounter(){
		retrycounter = 0;
	}

	
	public void incRetryCounter(){
		retrycounter++;
	}
	
	public boolean testMaxRetryCounter(){
		if(retrycounter<MAX_RETRY) return false;
		else return true;
	}
	
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
	}

	public BluetoothConnection getCon() {
		return con;
	}

	public void setCon(BluetoothConnection con) {
		this.con = con;
	}

	public String getMacAddress() {
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
	}
	
	
	

}
