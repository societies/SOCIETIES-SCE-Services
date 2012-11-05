/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
  package org.societies.thirdpartyservices.networking.directory.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Id;

/**
 * This is the Class accepted by the CssDiroectory when a css wants to register
 * and advertisment record. This Object contains attributes used to retrieve
 * services shared from/to a CSS/CIS and also information to retrieve
 * organization that has developed the service.
 * 
 * @author mmannion
 * @version 1.0
 */

@Entity
@Table(name = "NZZones")
public class NZZones implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7819484667842436359L;
	/**
	 * 
	 */

	public Integer id;
	public String zonename;
	public String zonelocation;
	public String zonelocdisplay;
	public String zonetopics;
	
	/**
	 * @return the userid
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO) 
	@Column(name = "id")
	public Integer getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "zonename")
	public String getZonename() {
		return zonename;
	}

	public void setZonename(String zonename) {
		this.zonename = zonename;
	}
	
	

	@Column(name = "zonelocation")
	public String getZonelocation() {
		return zonelocation;
	}

	public void setZonelocation(String zonelocation) {
		this.zonelocation = zonelocation;
	}
	
	@Column(name = "zonelocdisplay")
	public String getZonelocdisplay() {
		return zonelocdisplay;
	}

	public void setZonelocdisplay(String zonelocdisplay) {
		this.zonelocdisplay = zonelocdisplay;
	}

	@Column(name = "zonetopics")
	public String getZonetopics() {
		return zonetopics;
	}

	public void setZonetopics(String zonetopics) {
		this.zonetopics = zonetopics;
	}
	
	
	
}