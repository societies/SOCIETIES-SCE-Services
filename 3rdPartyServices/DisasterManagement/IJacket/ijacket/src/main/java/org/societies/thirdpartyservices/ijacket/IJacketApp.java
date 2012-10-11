package org.societies.thirdpartyservices.ijacket;

import org.societies.thirdpartyservices.ijacket.com.BluetoothConnection;

import android.app.Application;

public class IJacketApp extends Application {
	
	BluetoothConnection con;
	
	String macAddress = "";
	
	int ledPin = -1;
	int vibrationPin = -1;
	int speakersPin = -1;

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
