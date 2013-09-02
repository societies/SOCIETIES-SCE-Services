/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp.,
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.thirdpartyservices.crowdtasking;

import android.app.Application;

import com.google.gson.Gson;

import org.societies.api.schema.cis.community.Community;

import java.util.ArrayList;
import java.util.List;

import si.setcce.societies.crowdtasking.api.RESTful.json.CommunityJS;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 *
 */
public class CrowdTasking extends Application {
    private List<CommunityJS> societiesCommunities = new ArrayList();
    private String societiesCommunitiesJSON;
    Gson gson = new Gson();

	@Override
	public void onCreate() {
		super.onCreate();
	}

    public String getSocietiesCommunitiesJSON() {
        System.out.println(gson.toJson(societiesCommunities));
        return gson.toJson(societiesCommunities);
    }

    public String getSocietiesCommunitiesByJids(String[] jids) {
        List<CommunityJS> selectedCommunities = new ArrayList();
        for (CommunityJS commJS:societiesCommunities) {
            for (String jid:jids) {
                if (jid.equalsIgnoreCase(commJS.jid)) {
                    selectedCommunities.add(commJS);
                }
            }
        }
        return gson.toJson(selectedCommunities);
    }

    public List<CommunityJS> getSocietiesCommunities() {
        return societiesCommunities;
    }

    public void setSocietiesCommunities(List<CommunityJS> societiesCommunities) {
        this.societiesCommunities = societiesCommunities;
        societiesCommunitiesJSON = gson.toJson(societiesCommunities);
    }
}
