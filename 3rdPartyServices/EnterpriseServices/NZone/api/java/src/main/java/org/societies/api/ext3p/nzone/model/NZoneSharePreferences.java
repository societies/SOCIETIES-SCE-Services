package org.societies.api.ext3p.nzone.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class NZoneSharePreferences {

	private HashMap<String, List<NZoneLearnShare>> list;
	
	String preferTypeDelim = ".";
	String preferDelim = ",";
	String shareDelim = ":";
	
	
	//Define perferenceTypes
	public static String company = "company"; 
	public static String interest = "interest";
	
	public NZoneSharePreferences(String preferences)	
	{
		list = new HashMap<String, List<NZoneLearnShare>>();
		decode(preferences);
	};
	
	public NZoneSharePreferences()	
	{
		list = new HashMap<String, List<NZoneLearnShare>>();
	};
	

	public String toString()
	{
		return encode();
	}
	
	public int getSharePreferred(String parameterType, String parameterValue)
	{
		
		if (parameterValue != null && parameterValue.length() > 0)
		{
			List<NZoneLearnShare> tempList = list.get(parameterType); 
			
			if (tempList != null)
			{
				for ( int i = 0; i < tempList.size(); i++)
				{
					if (tempList.get(i).value.contentEquals(parameterValue.toLowerCase()))
						return tempList.get(i).share;
				}
			}
		}
		return -1;
	}
	

	public void addSharePreferred(String parameterType, String parameterValue, int shareflag)
	{
				
		List<NZoneLearnShare> tempList = list.get(parameterType); 
		NZoneLearnShare learnShar = new NZoneLearnShare(parameterValue.toLowerCase(), shareflag);
		
		if (tempList != null)
		{
			// check if it contains it already
			for ( int i = 0; i < tempList.size(); i++)
			{
				if (tempList.get(i).value.contentEquals(parameterValue.toLowerCase()))
				{
					tempList.remove(i);
					i = tempList.size();
				}
			}
			list.remove(parameterType);	
			tempList.add(learnShar);
			list.put(parameterType, tempList);
		}
		else
		{
			// new entry
			tempList = new ArrayList<NZoneLearnShare>();
			tempList.add(learnShar);
			list.put(parameterType, tempList);
		}	
	}
	
	public void removePreferred(String parameterType, String parameterValue)
	{
				
		List<NZoneLearnShare> tempList = list.get(parameterType); 
		
		if (tempList != null)
		{
			// check if it contains it already
			for ( int i = 0; i < tempList.size(); i++)
			{
				if (tempList.get(i).value.contentEquals(parameterValue.toLowerCase()))
				{
					list.remove(parameterType);
					tempList.remove(i);
					if (tempList.size() > 0)
						list.put(parameterType, tempList);
					return;
				}
			}
		}
		
	}
	
	
	
	
	private void decode(String pref)
	{
		list.clear();
		
		if (pref != null && pref.length() > 0)
		{
			String[] preferTypeTokens =  pref.split(preferTypeDelim);
		
			for ( int i = 0; i < preferTypeTokens.length; i++)
			{
				// now split there
				String[] preferTokens =  preferTypeTokens[i].split(preferDelim);
			
				// 	first token is perference name
				// second is preference value;
				// thrid is sshare value
				if (preferTokens.length > 1)
				{
					String key = preferTokens[0];
					List<NZoneLearnShare> values = new ArrayList<NZoneLearnShare>();
				
					for ( int j = 1; j < preferTokens.length; j++)
					{
						String[] preferShare =  preferTypeTokens[i].split(shareDelim);
						NZoneLearnShare shareInfo = new NZoneLearnShare(preferShare[0],Integer.parseInt(preferShare[1]));
						values.add(shareInfo);
					}
					list.put(key, values);
				}
			
			}
		}
	}
	
	private String encode()
	{
		String encodedString = new String();
		
		Set<String> keys = list.keySet();
		Iterator<String> keysIter = keys.iterator();
		while (keysIter.hasNext())
		{
			String key = keysIter.next();
			
			encodedString += key + preferDelim;
			List<NZoneLearnShare> tempList = list.get(key);
			
			for ( int i = 0; i < tempList.size(); i++)
			{
				encodedString += tempList.get(i).value + shareDelim;
				encodedString += tempList.get(i).share;
				encodedString += preferDelim;
			}
			
			encodedString = encodedString +preferTypeDelim;
		}	
		
		return encodedString;
		
	}
	
}

