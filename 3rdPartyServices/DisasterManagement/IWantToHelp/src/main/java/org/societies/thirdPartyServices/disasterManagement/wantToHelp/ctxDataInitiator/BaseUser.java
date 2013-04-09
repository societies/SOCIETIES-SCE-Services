package org.societies.thirdPartyServices.disasterManagement.wantToHelp.ctxDataInitiator;

public abstract class BaseUser {
	
	 public abstract String getName();
	 
	 public abstract String getSex();
	 
	 public abstract String getAge();
	 
	 public abstract String getLanguages();
	 
	 public abstract String getInterests();
	 
	 public abstract String getMovies();
	 
	 public abstract String getOccupation();
	 
	 public abstract String getStatus();
	 
	 public abstract String getEmail();
	 
	 public abstract String getBirthday();
	 
	 public abstract String getPoliticalViews();
	 
	 public abstract String getLocationSymbolic();
	 
	 public abstract String getLocationCoordinates();
	 
	 public abstract String getSkills();
	 
	 public abstract String getFriends();
	 
	 public final static String[] SKILLS = new String[]{"computer", "infrastructure", "communication", "internet", "hospitals", "translation", "navigation", "chemical", "research", "management"};
	 
	 
}