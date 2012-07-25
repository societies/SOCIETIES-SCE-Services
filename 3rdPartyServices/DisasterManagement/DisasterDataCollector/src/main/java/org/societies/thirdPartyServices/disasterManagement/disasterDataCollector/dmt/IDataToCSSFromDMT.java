package org.societies.thirdPartyServices.disasterManagement.disasterDataCollector.dmt;

public interface IDataToCSSFromDMT {

	public void setPosition(double latitude, double longitude, double elevation, int satNumber);

	public void setDirection(double roll, double pitch, double yaw);

	public void gpsConnected(boolean connected);

	public void compassConnected(boolean connected);

	public void viewLoaded(String viewXML);

	public void poisSent();
}
