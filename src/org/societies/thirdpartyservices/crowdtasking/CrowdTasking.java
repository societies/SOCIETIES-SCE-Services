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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.societies.api.schema.cis.community.Community;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import si.setcce.societies.crowdtasking.api.RESTful.json.CommunityJS;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 *
 */
public class CrowdTasking extends Application {
    //private List<CommunityJS> societiesCommunities = new ArrayList();
    private Map<String, CommunityJS> cisMap = new HashMap<String, CommunityJS>();
    private List<CommunityJS> societiesCommunities;
    private String societiesCommunitiesJSON;
    Gson gson = new Gson();
    public String symbolicLocation="";

	@Override
	public void onCreate() {
		super.onCreate();
	}

    public String getSocietiesCommunitiesJSON() {
        //System.out.println(gson.toJson(societiesCommunities));
        //return gson.toJson(societiesCommunities);
        societiesCommunitiesJSON = gson.toJson(new ArrayList<CommunityJS>(cisMap.values()));
        return societiesCommunitiesJSON;
    }

    public String getSocietiesCommunitiesByJids(String[] jids) {
        if (cisMap == null) return null;
        List<CommunityJS> selectedCommunities = new ArrayList();

        for (String jid:jids) {
            selectedCommunities.add(cisMap.get(jid));
        }
        return gson.toJson(selectedCommunities);
    }

    public void setSpaces(String response) {
        //List<CommunityJS> comms = new ArrayList<CommunityJS>();
        try {
            JSONArray sctComms = new JSONArray(response);
            if (sctComms.length() != 0) {
                for (int i = 0; i < sctComms.length(); i++) {
                    String jid = sctComms.getJSONObject(i).getString("jid");
                    CommunityJS communityJS = cisMap.get(jid);
                    if (communityJS != null) {
                        JSONArray spaces = new JSONArray(sctComms.getJSONObject(i).getString("spaces"));
                        communityJS.setSpaces(spaces);
                        cisMap.put(jid, communityJS);
                        //comms.add(communityJS);
                    }
                }
                //setSocietiesCommunities(comms);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setCommunitySpaces(String communityJSON) {
        // todo implement this
        try {
            JSONObject community = new JSONObject(communityJSON);
            JSONArray spaces = new JSONArray(community.getString("spaces"));
            CommunityJS communityJS = cisMap.get(community.getString("jid"));
            communityJS.setSpaces(spaces);
            cisMap.put(communityJS.jid, communityJS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
/*
    public List<CommunityJS> getSocietiesCommunities() {
        return societiesCommunities;
    }
*/

    public void setSocietiesCommunities(List<CommunityJS> societiesCommunities) {
        // todo fix this later - periodicaly update from SOCIETIES and SCT
        if (!cisMap.isEmpty()) {
            return;
        }
        this.societiesCommunities = societiesCommunities;
        societiesCommunitiesJSON = gson.toJson(societiesCommunities);
        cisMap = new HashMap<String, CommunityJS>();
        for (CommunityJS communityJS : societiesCommunities) {
            cisMap.put(communityJS.jid, communityJS);
        }
    }

    public Map<String, CommunityJS> getCisMap() {
        return cisMap;
    }
}
