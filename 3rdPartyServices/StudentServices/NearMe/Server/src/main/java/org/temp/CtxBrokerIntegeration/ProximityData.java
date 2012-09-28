package org.temp.CtxBrokerIntegeration;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class ProximityData {
	private String loc = null;
	private Set<String> users = new ConcurrentSkipListSet<String>();

	public ProximityData(String loc, Set<String> users) {
		this.loc = loc;
		this.users = users;
	}

	public void store() {
		for (String uidd : users) {
			CtxBrokerBridge.getBridge(uidd).renewProximityData(this);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String id : this.users) {
			sb.append(id + "|");
		}
		sb.append(loc);
		return sb.toString();
	}
}
