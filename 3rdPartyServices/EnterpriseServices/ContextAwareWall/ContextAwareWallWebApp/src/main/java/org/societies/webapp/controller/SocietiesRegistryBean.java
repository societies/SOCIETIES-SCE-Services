package org.societies.webapp.controller;

import org.societies.webapp.ISocietiesRegistryBean;


public class SocietiesRegistryBean implements ISocietiesRegistryBean {

	private String serverURL; 
	private String mockEntity;
	private boolean mockEntityActive;
	private String pzQueriesURL;
	
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
	public String getPzQueriesURL() {
		return pzQueriesURL;
	}
	public void setPzQueriesURL(String pzQueriesURL) {
		this.pzQueriesURL = pzQueriesURL;
	}
	
}
