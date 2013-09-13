package org.societies.api.ext3p.nzone.server;

import java.util.List;

import org.societies.api.ext3p.schema.nzone.UserDetails;



/**
 * This empty interface is needed to register our service with the SLM component
 *
 */
public interface INZoneServer {
	
	public String getZoneName(int zone);
	public List<UserDetails> getZoneMembers(int zone);
	

}

