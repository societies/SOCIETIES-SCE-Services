package org.societies.ext3p.nzone.model;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.ext3p.nzone.client.INZoneClient;
import org.societies.api.ext3p.schema.nzone.ZoneDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@SuppressWarnings("serial")
@ApplicationScoped
@Controller(value = "mainBean")
public class MainBean implements Serializable {

	@Autowired
	INZoneClient nzoneClient;
	
	private static Logger log = LoggerFactory.getLogger(MainBean.class);
	

	private String zoneName;
	private int currentZone;
	private String zoneImage;

	private List<ZoneDetails> dets;

	public MainBean() {
		currentZone = 0;

	}

	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
		setZoneName(getDets().get(getCurrentZone()).getZonename());
		return this.zoneName;
	}

	/**
	 * @param zoneName
	 *            the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * @return the zoneNo
	 */
	public int getCurrentZone() {

		this.currentZone = getNzoneClient().getCurrentZone();
		return this.currentZone;
	}

	/**
	 * @param zoneNo
	 *            the zoneNo to set
	 */
	public void setCurrentZone(int currentZone) {

		this.currentZone = currentZone;
	}

	public INZoneClient getNzoneClient() {
		return nzoneClient;
	}

	public void setNzoneClient(INZoneClient nzoneClient) {
		this.nzoneClient = nzoneClient;
	}

	public List<ZoneDetails> getDets() {

		if (dets == null)
			dets = getNzoneClient().getZoneDetails();
		return dets;
	}

	//NOTE : These images set up specifically for Intel Trial
	/**
	 * @return the zoneImage
	 */
	public String getZoneImage() {
		zoneImage = null;
		switch (getCurrentZone()) {
		case 0:
			zoneImage = new String("basiczone.png");
			break;
		case 1:
			zoneImage = new String("zone1.png");
			break;
		case 2:
			zoneImage = new String("zone2.png");
			break;
		case 3:
			zoneImage = new String("zone3.png");
			break;
		default:
			zoneImage = new String("basiczone.png");
			break;
		}

		return zoneImage;
	}

	/**
	 * @param zoneImage
	 *            the zoneImage to set
	 */
	public void setZoneImage(String zoneImage) {
		this.zoneImage = zoneImage;
	}
}
