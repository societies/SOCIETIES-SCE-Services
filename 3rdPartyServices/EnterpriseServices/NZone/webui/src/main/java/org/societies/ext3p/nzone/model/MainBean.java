package org.societies.ext3p.nzone.model;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.faces.bean.ApplicationScoped;
import javax.faces.event.ComponentSystemEvent;

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
	
	final Semaphore checkinit = new Semaphore(1, true);
	

	private String zoneName;
	private int currentZone;
	private String zoneImage;
	private int refreshinterval;
	
	private String progresstext;
	
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
	}
	
	
	public void checkinit(ComponentSystemEvent ev)
	{
		try {
			checkinit.acquire();
			if (!initialisedClient)
			{
				
				this.getNzoneClient().delayedInit();
				
				initialisedClient = true;
			}
			
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
