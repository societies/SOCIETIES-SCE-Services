package org.societies.api.ext3p.nzone.client;

import java.util.List;

import org.societies.api.ext3p.nzone.model.UserPreview;
import org.societies.api.ext3p.schema.nzone.ZoneDetails;



/**
 * This interface s user by UI clients to retrive information from the Networking Zone User client
 *
 */
public interface INZoneClient {
	
	public List<ZoneDetails> getZoneDetails();
	public boolean bJoinZone(String zoneID);
	public List<UserPreview> getSuggestedList();
	public void getUserProfile();
	public void saveShareInfo();
	public void getMyProfile();
	public void getActivityFeed();
	public void sendSocFR();
	public void getShareInfo();
	public void saveMyProfile();
	public void saveExtraInfo();
	public void posttoSN();
	public void setAsPreferred(String type, String value);
	public void removeAsPreferred(String type, String value);
	

	public void recordActionShowProfile();
	public void recordActionEnterZone();
	
}

