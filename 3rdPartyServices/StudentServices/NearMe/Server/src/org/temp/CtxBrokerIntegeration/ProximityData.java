package org.temp.CtxBrokerIntegeration;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class ProximityData {
	private String loc = null;
	private ConcurrentSkipListSet<String> users = new ConcurrentSkipListSet<String>();
	private static ConcurrentHashMap<String, ProximityData> allProximities = new ConcurrentHashMap<String, ProximityData>();
	private static ConcurrentHashMap<String, Object> allLocks = new ConcurrentHashMap<String, Object>();
	private static Object sync = new Object();

	private ProximityData(String loc) {
		this.loc = loc;
		allLocks.put(loc, new Object());
	}

	private static ProximityData getProximity(String loc) {
		synchronized (sync) {
			if (!allProximities.containsKey(loc)) {
				allProximities.put(loc, new ProximityData(loc));
			}
			return allProximities.get(loc);
		}
	}

	public static void arrive(String uid, String loc) {
		Object lock = allLocks.get(loc);
		synchronized (lock) {
			ProximityData pd = getProximity(loc);
			Set<String> uids = pd.users;
			uids.add(uid);
			for (String uidd : uids) {
				CtxBrokerBridge.getBridge(uidd).renewProximityData(pd);
			}
		}
	}

	public static void leave(String uid, String loc) {
		Object lock = allLocks.get(loc);
		synchronized (lock) {
			ProximityData pd = getProximity(loc);
			Set<String> uids = pd.users;
			uids.add(uid);
			for (String uidd : uids) {
				CtxBrokerBridge.getBridge(uidd).renewProximityData(pd);
			}
			CtxBrokerBridge.getBridge(uid).cleanProximityData();
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
