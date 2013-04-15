package org.societies.ext3p.nzone.test;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.ext3p.nzone.client.INZoneClient;
import org.societies.api.ext3p.nzone.model.UserPreview;
import org.societies.api.ext3p.schema.nzone.ZoneDetails;


public class NZoneTest {

	private static Logger log = LoggerFactory.getLogger(NZoneTest.class);
	
	
	private INZoneClient nzoneClient;
	
	
	public INZoneClient getNzoneClient() {
		return nzoneClient;
	}

	public void setNzoneClient(INZoneClient nzoneClient) {
		this.nzoneClient = nzoneClient;
	}
	



	public NZoneTest(INZoneClient nzoneClient)	
	{
		setNzoneClient(nzoneClient);
	};
	

	

	public void runTest() 
	{
		log.info("NZoneTest bundle initialized.");
		
	
				
		List<ZoneDetails> zoneDets = getNzoneClient().getZoneDetails();
		
		for ( int i = 0; i < zoneDets.size(); i++)
		{
			log.info("Index [" + i + "], Zone Name [" + zoneDets.get(i).getZonename() +"]");
			log.info("Index [" + i + "], Zone Location [" + zoneDets.get(i).getZonelocation() +"]");
			log.info("Index [" + i + "], Zone Location Display[" + zoneDets.get(i).getZonelocationdisplay() +"]");
			log.info("Index [" + i + "], Zone topics [" + zoneDets.get(i).getZonetopics() +"]");
			log.info("Index [" + i + "], Is main zone [" + zoneDets.get(i).getMainzone() +"]");
			log.info("Index [" + i + "], Member Count [" + zoneDets.get(i).getZonemembercount() +"]");
			log.info("Index [" + i + "], Cis ID [" + zoneDets.get(i).getCisid() +"]");
			
		}
	
		
		
		// Now do tests for each of the zones
		for ( int k = 0; k < zoneDets.size(); k++)
		{
			if (zoneDets.get(k).getMainzone() == 0) // don't test for main zone, we are already there
			{
				log.info("Attempting to join Zone " + zoneDets.get(k).getZonename());
				//try to join the zone
				if (getNzoneClient().bJoinZone(zoneDets.get(k).getCisid()) == false)
					log.error("Problem joined zone " + zoneDets.get(k).getZonename());
				else
					log.info("Sucessfully joined zone " + zoneDets.get(k).getZonename());
			}
			
			log.info("Getting suggestions for this zone");
			List<UserPreview> list = getNzoneClient().getSuggestedList(false);
			
			for ( int j = 0; j < list.size(); j++)
			{
				log.info("Suggestion is " + list.get(j).getDisplayName());
				if ((list.get(j).getTags() != null) && (list.get(j).getTags().size() > 0))
					log.info("Suggested because of " + list.get(j).getTags().get(0));
			}
				
			log.info("Setting preferences for this zone");
			// Do some prefereces stuff
			getNzoneClient().setAsPreferred("company", "intel");
			getNzoneClient().setAsPreferred("company", "Intel"); // making sure that is not case sensitive
		//	getNzoneClient().setAsPreferred("company", "ibm"); // making sure that is not case sensitive
		}
		
		
	};
	
	
	
	


			
}

