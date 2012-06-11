package org.societies.thirdPartyServices.disaster.youRNotAlone;

import java.util.ArrayList;

import org.societies.thirdPartyServices.disaster.youRNotAlone.mockedData.MockedData;
import org.societies.thirdPartyServices.disaster.youRNotAlone.model.Volunteer;

class Test{
	
	public static void main(String[] args) {
		MockedData mData = new MockedData(); 
		ArrayList<Volunteer> volunteers = mData.getMockedVolunteers();
		
		for(int i=0;i<volunteers.size();i++){
			System.out.println(volunteers.get(i).toString());
			System.out.println();
		}
			
	}
	
}