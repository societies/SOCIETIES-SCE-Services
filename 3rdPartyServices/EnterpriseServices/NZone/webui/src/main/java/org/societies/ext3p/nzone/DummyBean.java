package org.societies.ext3p.nzone;  

import org.societies.api.ext3p.nzone.client.INZoneClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DummyBean {
	
	@Autowired
	INZoneClient nzoneClient;
	
	public INZoneClient getNzoneClient() {
		return nzoneClient;
	}

	public void setNzoneClient(INZoneClient nzoneClient) {
		this.nzoneClient = nzoneClient;
	}

}
