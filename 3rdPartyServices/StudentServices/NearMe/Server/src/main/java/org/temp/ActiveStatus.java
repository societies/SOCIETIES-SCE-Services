package org.temp;

import java.util.Date;
import java.util.HashMap;

public class ActiveStatus {
	public Date start_active;
	public Date last_update;
	public String loc;
	public static HashMap<String, ActiveStatus> status = new HashMap<String, ActiveStatus>();
	public static Object sync = new Object();

	public boolean isActive() {
		return start_active != null
				&& ((new Date()).getTime() - this.start_active.getTime() < 1000 * 60 * 60);
	}

	public void update() {
		Date now = new Date();
		if (!this.isActive()) {
			this.start_active = now;
		}
		this.last_update = now;
	}

	private ActiveStatus(String loc) {
		this.loc = loc;
	}

	public static ActiveStatus getCurrentStatus(String loc) {
		synchronized (sync) {
			if (!status.containsKey(loc)) {
				status.put(loc, new ActiveStatus(loc));
			}
			return status.get(loc);
		}
	}
}
