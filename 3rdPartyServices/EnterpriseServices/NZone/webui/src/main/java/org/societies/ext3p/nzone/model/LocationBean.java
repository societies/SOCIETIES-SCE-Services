package org.societies.ext3p.nzone.model;  
  
import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ApplicationScoped;

import org.societies.api.ext3p.nzone.client.INZoneClient;
import org.societies.api.ext3p.schema.nzone.ZoneDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
  
@SuppressWarnings("serial")
@ApplicationScoped
@Controller(value = "locationBean") 
public class LocationBean implements Serializable {   
  
	
	@Autowired
	INZoneClient nzoneClient; 
	
    private String zoneName;   
    private int zoneNo;
    
    private List<ZoneDetails> dets;
    
    
    public LocationBean() 
    {
    	zoneNo = 0;
    	
    }
    
	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
		if (this.zoneName == null || this.zoneName.length() == 0)
		{
			setZoneName(getDets().get(this.zoneNo).getZonename());
		}
		return this.zoneName;
	}
	/**
	 * @param zoneName the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	/**
	 * @return the zoneNo
	 */
	public int getZoneNo() {
		return zoneNo;
	}
	/**
	 * @param zoneNo the zoneNo to set
	 */
	public void setZoneNo(int zoneNo) {
		getNzoneClient().updateLocationManual(getDets().get(zoneNo).getZonelocation());
		this.zoneNo = zoneNo;
		this.setZoneName(getDets().get(zoneNo).getZonename());
	}
	
	
	public INZoneClient getNzoneClient() {
		return nzoneClient;
	}
	public void setNzoneClient(INZoneClient nzoneClient) {
		this.nzoneClient = nzoneClient;
	}
    
    
	public List<ZoneDetails> getDets() {
		
		if (dets == null)
		{
			dets = getNzoneClient().getZoneDetails();
			// we don;t want the main zone in this so remove
			/*if ((dets != null) && (dets.size() > 0))
			{
				for ( int i = 0; i < dets.size(); i++)
				{
					if (dets.get(i).getMainzone() == 1)
					{
						dets.remove(i);
						i = dets.size(); 
					}
					
				}
			}	*/	
		}		
		
		return dets;
	}  
}
