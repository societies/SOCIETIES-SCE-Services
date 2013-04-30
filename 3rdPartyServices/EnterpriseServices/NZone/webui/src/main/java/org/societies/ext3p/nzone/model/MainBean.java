package org.societies.ext3p.nzone.model;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.event.ComponentSystemEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.ext3p.nzone.client.INZoneClient;
import org.societies.api.ext3p.nzone.model.UserPreview;
import org.societies.api.ext3p.nzone.model.ZoneDisplayDetail;
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
	
	final Semaphore checkinit = new Semaphore(1, true);
	
	
	
	private String zoneName;
	private int currentZone;
	private String zoneImage;
	private int refreshinterval;
	
	private String progresstext;
	
	
	// Used to hold image offsets of all zones
	private int allZoneImageTop[];
	private int allZoneImageLeft[];
	private String allZoneNames[];
	
	private int zoneImageTop;
	private int zoneImageLeft;
	
	private int zoneImageTopOther;
	private int zoneImageLeftOther;
	
	private boolean showotheravatar;
	private String otherLocationMessage;
	private UserPreview otherToShow;
	
	
	private boolean initialisedClient;
	public boolean isInitialisedClient() {
		return initialisedClient;
	}

	public void setInitialisedClient(boolean initialisedClient) {
		this.initialisedClient = initialisedClient;
	}

	public boolean isInitialisingClient() {
		return initialisingClient;
	}

	public void setInitialisingClient(boolean initialisingClient) {
		this.initialisingClient = initialisingClient;
	}


	private boolean initialisingClient;

	private List<ZoneDetails> dets;

	public MainBean() {
		currentZone = 0;
		initialisedClient = false;
		initialisingClient = false;
		setProgresstext(new String("Initialising : 0%"));
		setRefreshinterval(3);
		//initialisingClient.getAndSet(false);
		
		
		//f or now we will hardcode the offsets
		allZoneImageTop = new int[3];
		allZoneImageLeft = new int[3];
		
		allZoneImageTop[0] = 45;
		allZoneImageTop[1] = 140;
		allZoneImageTop[2] = 75;

		allZoneImageLeft[0] = 60;
		allZoneImageLeft[1] = 100;
		allZoneImageLeft[2] = 230;
		
		allZoneNames = new String[3];
		allZoneNames[0] = "Breakout Zone";
		allZoneNames[1] = "Showcase Zone";
		allZoneNames[2] = "Presentation Zone";
		
		this.showotheravatar = false;
		otherToShow = new UserPreview();
		
		this.setOtherLocationMessage(new String(""));
		 
	}
	
	
	public void checkinit(ComponentSystemEvent ev)
	{
		
		if (initialisedClient)
			return;
		
		try {
			checkinit.acquire();
			if (!initialisedClient)
			{
				
				this.getNzoneClient().delayedInit();
				initialisedClient = true;
			}
			
			ZoneDisplayDetail zoneDet = getNzoneClient().getCurrentZone();
			
			this.currentZone = zoneDet.getZoneNo();
			this.zoneName = zoneDet.getZoneName();
			this.zoneImageTop = zoneDet.getImageOffsetTopProfile();
			this.zoneImageLeft = zoneDet.getImageOffsetLeftProfile();
			
			
			this.showotheravatar = false;
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			//TODO : check if we know that he have gotten this???
			checkinit.release();
		}
		
		
	}
	
		 
	public void startinit(ComponentSystemEvent ev)
	{

		if ((!initialisedClient) && (!initialisingClient))
		{
			initialisingClient = true;
			new Thread(new InitialiseZoneHandler(this)).start();
				
		}

	}

	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
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

	//	ZoneDisplayDetail zoneDet = getNzoneClient().getCurrentZone();
		
	//	this.currentZone = zoneDet.getZoneNo();
	//	this.zoneName = zoneDet.getZoneName();
	//	this.zoneImageTop = zoneDet.getImageOffsetTopProfile();
	//	this.zoneImageLeft = zoneDet.getImageOffsetLeftProfile();
		
		// While we are checking the zone,we'll update the offsets too
		//TODO : Do this better!
		
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
		
	/*	switch (getCurrentZone()) {
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
		*/
		if (zoneImage == null)
			zoneImage = new String("basiczone.png");

		updateOtherAvator();

		return zoneImage;
	}

	/**
	 * @param zoneImage
	 *            the zoneImage to set
	 */
	public void setZoneImage(String zoneImage) {
		this.zoneImage = zoneImage;
	}
	
	
	private Integer progress;

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	public void checkProgress() {
		if(progress == null)
			progress = 0;
	
		if (initialisedClient == true)
		{
			progress = 100;
			setProgresstext("Initialisation Complete");
			setRefreshinterval(30000);
		}
		else
		{
			if (progress < 50)
				progress += 10;
			else if (progress < 90)
			progress +=5;
			else
			{
				progress += 1;
				if (progress > 99)
					progress = 99;
			}
			
			setProgresstext("Initialisation : " + progress + "%");
		}
		
	}
	
	
	public String onInitComplete()
	{
	    if (initialisedClient == true)
	    {
	         return "gotomain";
	    }
	    return null;
	}

	
	
/**
	 * @return the progresstext
	 */
	public String getProgresstext() {
		return progresstext;
	}

	/**
	 * @param progresstext the progresstext to set
	 */
	public void setProgresstext(String progresstext) {
		this.progresstext = progresstext;
	}



/**
	 * @return the refreshinterval
	 */
	public int getRefreshinterval() {
		return refreshinterval;
	}

	/**
	 * @param refreshinterval the refreshinterval to set
	 */
	public void setRefreshinterval(int refreshinterval) {
		this.refreshinterval = refreshinterval;
	}


	/**
	 * @return the zoneImageTop
	 */
	public int getZoneImageTop() {
		return zoneImageTop;
	}


	/**
	 * @param zoneImageTop the zoneImageTop to set
	 */
	public void setZoneImageTop(int zoneImageTop) {
		this.zoneImageTop = zoneImageTop;
	}


	/**
	 * @return the zoneImageLeft
	 */
	public int getZoneImageLeft() {
		return zoneImageLeft;
	}


	/**
	 * @param zoneImageLeft the zoneImageLeft to set
	 */
	public void setZoneImageLeft(int zoneImageLeft) {
		this.zoneImageLeft = zoneImageLeft;
	}
	

/**
	 * @return the zoneImageTopOther
	 */
	public int getZoneImageTopOther() {
		return zoneImageTopOther;
	}

	/**
	 * @param zoneImageTopOther the zoneImageTopOther to set
	 */
	public void setZoneImageTopOther(int zoneImageTopOther) {
		this.zoneImageTopOther = zoneImageTopOther;
	}


public int getZoneImageLeftOther() {
		return zoneImageLeftOther;
	}

	public void setZoneImageLeftOther(int zoneImageLeftOther) {
		this.zoneImageLeftOther = zoneImageLeftOther;
	}

	

	public boolean isShowotheravatar() {
		return showotheravatar;
	}

	public void setShowotheravatar(boolean showotheravatar) {
		this.showotheravatar = showotheravatar;
	}

	

	public UserPreview getOtherToShow() {
		return otherToShow;
	}

	public void setOtherToShow(UserPreview otherToShow) {
		this.otherToShow = otherToShow;
	}

	

	

	
	/**
	 * @return the otherLocationMessage
	 */
	public String getOtherLocationMessage() {
		return otherLocationMessage;
	}

	/**
	 * @param otherLocationMessage the otherLocationMessage to set
	 */
	public void setOtherLocationMessage(String otherLocationMessage) {
		this.otherLocationMessage = otherLocationMessage;
	}

	
	public void updateOtherAvator()
			
	{
		this.setOtherLocationMessage("");
		
		if ((this.otherToShow == null) || (this.otherToShow.getImageSrc() == null) || (this.otherToShow.getImageSrc().contentEquals("")))
		{
			this.showotheravatar = false;
			return;
		}
		// Get zone
		int currentOtherZone = 0;
		for ( int i = 0; i < 3; i++)
		{
			if (allZoneNames[i].contains(this.otherToShow.getZone()))
			{
				currentOtherZone = i;
				i = 3;
			}
		}
	

		this.setOtherLocationMessage(allZoneNames[currentOtherZone]);
		
		this.zoneImageTopOther = allZoneImageTop[currentOtherZone];
		
		//if their are in the same zone as user, change location of user
		if (currentOtherZone == this.getCurrentZone()-1)
		{
			this.zoneImageLeft = allZoneImageLeft[currentOtherZone] - 20;
			this.zoneImageLeftOther = allZoneImageLeft[currentOtherZone] + 30;
		} else {
			// otherwise, can show them in efault position
			this.zoneImageLeftOther = allZoneImageLeft[currentOtherZone];
		};
		
		
		this.showotheravatar = true;
		
	}
	
private class InitialiseZoneHandler implements Runnable {
		
		public MainBean mainBean;
		
		private InitialiseZoneHandler(MainBean mainBean) {
			this.mainBean = mainBean;
		}
		
		@Override
		public void run() {
			
			if (mainBean.checkinit.tryAcquire())
			{
				mainBean.getNzoneClient().delayedInit();
				mainBean.initialisedClient = true;
				mainBean.initialisingClient = false;
				mainBean.checkinit.release();
			}
			
		}
	}


}
