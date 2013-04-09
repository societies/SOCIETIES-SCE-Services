package org.societies.api.ext3p.nzone.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class NZonePreferences {

	private HashMap<String, List<String>> list;
	
	String preferTypeDelim = ".";
	String preferDelim = ",";
	String shareDelim = ":";
	
	//Define perferenceTypes
	public static String company = "company"; 
	public static String interest = "interest";
	
	public NZonePreferences(String preferences)	
	{
		list = new HashMap<String, List<String>>();
		decode(preferences);
	};
	
	public NZonePreferences()	
	{
		list = new HashMap<String, List<String>>();
	};
	

	public String toString()
	{
		return encode();
	}
	
	public boolean isPreferred(String parameterType, String parameterValue)
	{
		boolean result = false;
		
		if (parameterValue != null && parameterValue.length() > 0)
		{
			List<String> tempList = list.get(parameterType); 
			
			if (tempList != null)
			{
				for ( int i = 0; i < tempList.size(); i++)
				{
					if (tempList.get(i).contentEquals(parameterValue.toLowerCase()))
						return true;
				}
			}
		}
		return result;
	}
	

	public void addPreferred(String parameterType, String parameterValue)
	{
				
		List<String> tempList = list.get(parameterType); 
		
		if (tempList != null)
		{
			// check if it contains it already
			for ( int i = 0; i < tempList.size(); i++)
			{
				if (tempList.get(i).contentEquals(parameterValue.toLowerCase()))
					return;
			}

			list.remove(parameterType);
			tempList.add(parameterValue.toLowerCase());
			list.put(parameterType, tempList);
		}
		else
		{
			// new entry
			tempList = new ArrayList<String>();
			tempList.add(parameterValue);
			list.put(parameterType, tempList);
		}	
	}
	
	public void removePreferred(String parameterType, String parameterValue)
	{
				
		List<String> tempList = list.get(parameterType); 
		
		if (tempList != null)
		{
			// check if it contains it already
			for ( int i = 0; i < tempList.size(); i++)
			{
				if (tempList.get(i).contentEquals(parameterValue.toLowerCase()))
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
				// 	remaining tokens are preferences values
				if (preferTokens.length > 1)
				{
					String key = preferTokens[0];
					List<String> values = new ArrayList<String>();
				
					for ( int j = 1; j < preferTokens.length; j++)
					{
						values.add(preferTokens[j]);
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
			
			encodedString = encodedString + key + preferDelim;
			List<String> tempList = list.get(key);
			
			for ( int i = 0; i < tempList.size(); i++)
			{
				encodedString = encodedString + tempList.get(i) + preferDelim;
			}
			
			encodedString = encodedString +preferTypeDelim;
		}	
		
		return encodedString;
		
	}
	
}

