package org.societies.thirdPartyServices.disaster.youRNotAlone.mockedData;

import java.util.ArrayList;
import java.util.HashSet;

import org.societies.thirdPartyServices.disaster.youRNotAlone.model.*;

public class MockedData{	
	
	public ArrayList<Volunteer> volunteers;
	public ArrayList<String> allLanguages;
	public ArrayList<String> allSkills;
	
	public MockedData(){
		Volunteer v1 = new Volunteer("dingqi.societies@gmail.com","Dingqi","Yang","Telecom SudParis",
				"France","yangdingqi@gmail.com");
		v1.addSpokenLanguage("english");
		v1.addSpokenLanguage("french");
		v1.addSpokenLanguage("chinese");
		v1.addSkill("satellite_image_analysis");
		v1.addSkill("medical_support");
		v1.addSkill("insarag_mark_recognition");

		Volunteer v2 = new Volunteer("james.societies@gmail.com","James","Bande","Telecom SudParis",
				"France","james@gmail.com");
		v2.addSpokenLanguage("english");
		v2.addSkill("satellite_image_analysis");

		
		Volunteer v3 = new Volunteer("lucas.societies@gmail.com","Lucas","Leblanc","Telecom Italia",
				"Italia","luca@gmail.com");
		v3.addSpokenLanguage("english");
		v3.addSpokenLanguage("Italia");
		v3.addSkill("satellite_image_analysis");
		v3.addSkill("medical_support");
		
		Volunteer v4 = new Volunteer("zhiyong.societies@gmail.com","Zhiyong","Yu","Telecom SudParis",
				"france","zhiyong@gmail.com");
		v4.addSpokenLanguage("english");
		v4.addSpokenLanguage("japanese");
		v4.addSkill("satellite_image_analysis");
		
		Volunteer v5 = new Volunteer("haoyi.societies@gmail.com","haoyi","Xiong","Telecom SudParis",
				"france","haoyi@gmail.com");
		v5.addSpokenLanguage("english");
		v5.addSpokenLanguage("japanese");
		v5.addSpokenLanguage("chinese");
		v5.addSkill("satellite_image_analysis");
		v5.addSkill("insarag_mark_recognition");

		
		Volunteer v6 = new Volunteer("daqiang.societies@gmail.com","daqiang","Zhang","Telecom SudParis",
				"france","daqiang@gmail.com");
//		v6.addSpokenLanguage("english");
//		v6.addSpokenLanguage("japanese");
//		v6.addSpokenLanguage("chinese");
		v6.addSkill("satellite_image_analysis");
		
		volunteers = new ArrayList<Volunteer>();
		volunteers.add(v1);
		volunteers.add(v2);
		volunteers.add(v3);
		volunteers.add(v4);
		volunteers.add(v5);
		volunteers.add(v6);
		
	}
	
	public ArrayList<Volunteer> getMockedVolunteers(){
		return this.volunteers;
	}

	
}