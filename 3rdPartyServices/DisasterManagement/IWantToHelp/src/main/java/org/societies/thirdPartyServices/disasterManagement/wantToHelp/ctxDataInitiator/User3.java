package org.societies.thirdPartyServices.disasterManagement.wantToHelp.ctxDataInitiator;

public class User3 extends BaseUser{

	@Override
	public String getName() {
		
		return "User3";
	}

	@Override
	public String getSex() {
		
		return "male";
	}

	@Override
	public String getAge() {
		
		return "18";
	}

	@Override
	public String getLanguages() {

		return null;
	}

	@Override
	public String getInterests() {

		return null;
	}

	@Override
	public String getMovies() {

		return ",ironman,superman";
	}

	@Override
	public String getOccupation() {

		return "unemployed";
	}

	@Override
	public String getStatus() {
		
		return "available";
	}

	@Override
	public String getEmail() {
		
		return "";
	}

	@Override
	public String getBirthday() {
		
		return "25/12/1979";
	}

	@Override
	public String getPoliticalViews() {
		
		return null;
	}

	@Override
	public String getLocationSymbolic() {

		return null;
	}

	@Override
	public String getLocationCoordinates() {

		return null;
	}

	@Override
	public String getFriends() {
		
		return "";
	}

	/* (non-Javadoc)
	 * @see org.societies.context.dataInit.impl.BaseUser#getSkills()
	 */
	@Override
	public String getSkills() {
		return BaseUser.SKILLS[9]+","+BaseUser.SKILLS[2]+","+BaseUser.SKILLS[3];
	}
	
}
