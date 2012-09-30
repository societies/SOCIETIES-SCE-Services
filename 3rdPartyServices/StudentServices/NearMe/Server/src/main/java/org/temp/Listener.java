package org.temp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantLock;

import org.temp.CtxBrokerIntegeration.CtxBrokerBridge;
import org.temp.CtxBrokerIntegeration.ProximityData;

public class Listener {

	public static ConcurrentHashMap<String, Set<LastUpdate>> lastUpdateLoc = new ConcurrentHashMap<String, Set<LastUpdate>>();
	public static ConcurrentHashMap<String, LastUpdate> lastUpdateUsr = new ConcurrentHashMap<String, LastUpdate>();

	public static ReentrantLock lock = new ReentrantLock(false);
	public static ConcurrentSkipListSet<String> removals = new ConcurrentSkipListSet<String>();

	public static class DemonThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					System.err.println("sleeping");
					Thread.sleep(1000 * 5);
					Listener.lock.lock();
					Date now = new Date();
					for (LastUpdate up : Listener.lastUpdateUsr.values()) {
						if (now.getTime() - up.lastUpdate.getTime() > 2 * 60 * 1000) {
							lastUpdateUsr.remove(up.uid);
							lastUpdateLoc.get(up.location).remove(up);
							Listener.removals.add(up.uid);
						}
					}
					Listener.lock.unlock();

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public static HashMap<String, Set<String>> getCopy() {
		HashMap<String, Set<String>> copies = new HashMap<String, Set<String>>();
		for (String loc : lastUpdateLoc.keySet()) {
			Set<LastUpdate> lups = lastUpdateLoc.get(loc);
			copies.put(loc, new HashSet<String>());
			for (LastUpdate lup : lups) {
				copies.get(loc).add(lup.uid);
			}
		}
		return copies;
	}

	public static class PushThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while (true) {
					System.err.println("sleeping2");
					Thread.sleep(1000);
					List<String> cpl = new ArrayList<String>(Listener.removals);
					Listener.removals.removeAll(cpl);
					for (String rm : cpl) {
						CtxBrokerBridge.getBridge(rm).cleanProximityData();
					}
					HashMap<String, Set<String>> cp = Listener.getCopy();
					for (String loc : cp.keySet()) {
						new ProximityData(loc, cp.get(loc)).store();
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static class LastUpdate implements Comparable {
		public String location;
		public String uid;
		public Date lastUpdate;

		private LastUpdate(String uid, String loc) {
			this.location = loc;
			this.uid = uid;
			this.lastUpdate = new Date();
			if (!Listener.lastUpdateLoc.containsKey(this.location)) {
				Listener.lastUpdateLoc.put(this.location,
						new ConcurrentSkipListSet<LastUpdate>());
			}
			Listener.lastUpdateLoc.get(this.location).add(this);
			Listener.lastUpdateUsr.put(this.uid, this);
		}

		public void moveTo(String loc) {
			Listener.lastUpdateLoc.get(this.location).remove(this);
			this.location = loc;
			if (!Listener.lastUpdateLoc.containsKey(this.location)) {
				Listener.lastUpdateLoc.put(this.location,
						new ConcurrentSkipListSet<LastUpdate>());
			}
			Listener.lastUpdateLoc.get(this.location).add(this);
		}

		public static LastUpdate getLastUpdate(String uid, String location) {
			try {
				lock.lock();
				if (Listener.lastUpdateUsr.containsKey(uid)) {
					LastUpdate lup = Listener.lastUpdateUsr.get(uid);
					if (!lup.location.equals(location)) {
						lup.moveTo(location);
					}
					lup.lastUpdate = new Date();
					return lup;
				} else {
					return new LastUpdate(uid, location);
				}
			} finally {
				lock.unlock();
			}
		}

		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			return this.hashCode() - arg0.hashCode();
		}
	}

}
