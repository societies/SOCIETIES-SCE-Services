
package org.societies.thirdpartyservices.networking.model;

public class PublicDetails {

	
	static int xShareCompanyName = 0x0000000001; 
	static int xShareCompanyDepart = 0x0000000010; 
	static int xShareInterests = 0x0000000100;
	
	
	
	int sharedInfo = 0;
	
	public boolean isSharingCompany()
	{
		return ((sharedInfo & xShareCompanyName) == xShareCompanyName);
	}
	
	public boolean isSharingDepart()
	{
		return ((sharedInfo & xShareCompanyDepart) == xShareCompanyDepart);
	}
	
	public boolean isSharingInterests()
	{
		return ((sharedInfo & xShareInterests) == xShareInterests);
	}
	
	
	public void setSharingCompany(boolean share)
	{
		if (share)
			sharedInfo |= xShareCompanyName; // turn on
		else
			sharedInfo &= -xShareCompanyName; // turn off
	}
	
	public void setSharingDepartment(boolean share)
	{
		if (share)
			sharedInfo |= xShareCompanyDepart; // turn on
		else
			sharedInfo &= -xShareCompanyDepart; // turn off
	}
	
	public void setSharingInterests(boolean share)
	{
		if (share)
			sharedInfo |= xShareInterests; // turn on
		else
			sharedInfo &= -xShareInterests; // turn off
	}
	
}
