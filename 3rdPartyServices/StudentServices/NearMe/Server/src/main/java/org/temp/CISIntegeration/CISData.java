package org.temp.CISIntegeration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CISData {
	public String givenName;
	public String cisName;
	public String cisDescription;
	public String owner;
	public Set<String> participants = new HashSet<String>();
	public static ConcurrentHashMap<String, CISData> cisMap = new ConcurrentHashMap<String, CISData>();

	public CISData(String cisName, String owner, String cisDescription) {
		this.cisName = cisName;
		this.owner = owner;
		this.cisDescription = cisDescription;
		System.err.println("establish:\t"+cisName);
		cisMap.put(cisName, this);
	}

	public static CISData getCISData(String cisName) {
		if (cisMap.containsKey(cisName))
			return cisMap.get(cisName);
		return null;
	}
}
