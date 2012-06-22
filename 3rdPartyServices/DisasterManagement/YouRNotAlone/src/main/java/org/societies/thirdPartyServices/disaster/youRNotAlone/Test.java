package org.societies.thirdPartyServices.disaster.youRNotAlone;

import java.util.ArrayList;

import org.societies.thirdPartyServices.disaster.youRNotAlone.mockedData.MockedData;
import org.societies.thirdPartyServices.disaster.youRNotAlone.model.Volunteer;

class Test{

	public static void main(String[] args) {

		MockedData mData = new MockedData(); 
		VolunteerOrganizer organizer = new VolunteerOrganizer();
		organizer.loadVolunteers(mData.getMockedVolunteers());
		VolunteerCluster cluster = new VolunteerCluster(10,organizer.getAllLanguages(),organizer.getAllSkills());

		ArrayList<String> langs = new ArrayList<String>();
		langs.add("english");
		langs.add("japanese");
		ArrayList<Volunteer> langsGroup = organizer.getGroupByLanguages(langs);
//		System.out.println("language group test:");
//		displayGroup(langsGroup);

		ArrayList<String> skills = new ArrayList<String>();
		skills.add("java");
		skills.add("c++");
		skills.add("python");
		ArrayList<Volunteer> skillsGroup = organizer.getGroupBySkills(skills);
//		System.out.println("skill group test:");
//		displayGroup(skillsGroup);
		
		ArrayList<String> pros = new ArrayList<String>();
		pros.addAll(langs);
		pros.addAll(skills);
		ArrayList<Volunteer> propertiesGroup = organizer.getGroupByProperties(pros);
//		System.out.println("property group test:");
//		displayGroup(propertiesGroup);
		
		
		cluster.loadInstanceFromVolunteers(mData.getMockedVolunteers());
		cluster.update();
		
	}

	public static void displayGroup(ArrayList<Volunteer> group){
		for(int i=0;i<group.size();i++){
			System.out.println(group.get(i).toString());
		}
		System.out.println("");
	}

}