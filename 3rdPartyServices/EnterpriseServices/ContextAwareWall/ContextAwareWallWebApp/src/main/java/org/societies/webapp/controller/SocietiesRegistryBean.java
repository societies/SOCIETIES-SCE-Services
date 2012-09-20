package org.societies.webapp.controller;

import org.societies.webapp.ISocietiesRegistryBean;


public class SocietiesRegistryBean implements ISocietiesRegistryBean {

	private String serverURL; 
	private String mockEntity;
	private boolean mockEntityActive;
	
	
	public String getServerURL() {
		return serverURL;
	}
	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}
	public String getMockEntity() {
		return mockEntity;
	}
	public void setMockEntity(String mockEntity) {
		this.mockEntity = mockEntity;
	}
	public boolean isMockEntityActive() {
		return mockEntityActive;
	}
	public void setMockEntityActive(boolean mockEntityActive) {
		this.mockEntityActive = mockEntityActive;
	}
}
