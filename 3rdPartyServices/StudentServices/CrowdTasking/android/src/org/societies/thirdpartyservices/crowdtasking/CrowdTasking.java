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
import android.util.Log;
import android.webkit.CookieManager;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.societies.api.schema.cis.community.Community;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import si.setcce.societies.android.rest.RestTask;
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
	private final static String LOG_TAG = "Crowd Tasking";

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

	public void synchronizeCommunities(String response) {
		try {
			JSONArray sctComms = new JSONArray(response);
			Map<String, JSONObject> commMap = new HashMap<String, JSONObject>();
			for (int i = 0; i < sctComms.length(); i++) {
				commMap.put(sctComms.getJSONObject(i).getString("jid"), sctComms.getJSONObject(i));
			}
			for (CommunityJS cis:societiesCommunities) {
				JSONObject communitiyJSON = commMap.get(cis.jid);
				if (communitiyJSON == null) {
					addCisToGae(cis);
				} else {
					updateCis(cis, communitiyJSON);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}	}

	private void addCisToGae(CommunityJS cis) {
		// POST cis to /rest/community/create
		HttpPost postCommunity;
		try {
			postCommunity = new HttpPost(new URI(MainActivity.CREATE_COMMUNITY_API_URL));
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			// communityJid, ownerJid, name, description
			parameters.add(new BasicNameValuePair("communityJid", cis.jid));
			parameters.add(new BasicNameValuePair("communityId", ""));
			parameters.add(new BasicNameValuePair("ownerJid", cis.ownerJid));
			parameters.add(new BasicNameValuePair("owner", cis.owner == true ? "true":"no"));
			parameters.add(new BasicNameValuePair("name", cis.name));
			parameters.add(new BasicNameValuePair("description", cis.description));
			parameters.add(new BasicNameValuePair("action", "synchronize"));
			postCommunity.setEntity(new UrlEncodedFormEntity(parameters));
			RestTask task = new RestTask(getApplicationContext(), "", CookieManager.getInstance()
					.getCookie(MainActivity.DOMAIN), MainActivity.DOMAIN);
			task.execute(postCommunity);
		} catch (URISyntaxException e) {
			Log.e(LOG_TAG, "Can't log event: " + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			Log.e(LOG_TAG, "Can't log event: "+e.getMessage());
		}

	}

	private void updateCis(CommunityJS cis, JSONObject communityJSON) {
		// update CIS data
		String jid = null;
		try {
			jid = communityJSON.getString("jid");
			CommunityJS communityJS = cisMap.get(jid);
			if (communityJS != null) {
				// todo: test this
				communityJS.id = communityJSON.getLong("id");
				JSONArray spaces = new JSONArray(communityJSON.getString("spaces"));
				communityJS.setSpaces(spaces);
				cisMap.put(jid, communityJS);
				//comms.add(communityJS);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

/*
	public void setSpaces(String response) {
        //List<CommunityJS> comms = new ArrayList<CommunityJS>();
        try {
            JSONArray sctComms = new JSONArray(response);
            if (sctComms.length() != 0) {
                for (int i = 0; i < sctComms.length(); i++) {
                    String jid = sctComms.getJSONObject(i).getString("jid");
                    CommunityJS communityJS = cisMap.get(jid);
                    if (communityJS != null) {
	                    communityJS.id = sctComms.getJSONObject(i).getLong("id");
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
*/

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
