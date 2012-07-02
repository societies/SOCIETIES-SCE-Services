package org.societies.thirdPartyServices.disaster.youRNotAlone.mockedData;

import java.util.ArrayList;
import java.util.HashSet;

import org.societies.thirdPartyServices.disaster.youRNotAlone.model.*;

public class MockedData{	
	
	public ArrayList<Volunteer> volunteers;
	public ArrayList<String> allLanguages;
	public ArrayList<String> allSkills;
	
	public MockedData(){
		Volunteer v1 = new Volunteer("1","Dingqi","Yang","Telecom SudParis",
				"France","yangdingqi@gmail.com");
		v1.addSpokenLanguage("english");
		v1.addSpokenLanguage("french");
		v1.addSpokenLanguage("chinese");
		v1.addSkill("java");
		v1.addSkill("C++");
		v1.addSkill("C#");
		v1.addSkill("matlab");

		Volunteer v2 = new Volunteer("2","James","Bande","Telecom SudParis",
				"France","james@gmail.com");
		v2.addSpokenLanguage("english");
		v2.addSkill("java");
		v2.addSkill("C++");
		v2.addSkill("python");
		
		Volunteer v3 = new Volunteer("3","Luca","Lamorte","Telecom Italia",
				"Italia","luca@gmail.com");
		v3.addSpokenLanguage("english");
		v3.addSpokenLanguage("Italia");
		v3.addSkill("java");
		v3.addSkill("C++");
		v3.addSkill("php");
		
		Volunteer v4 = new Volunteer("4","Zhiyong","Yu","Telecom SudParis",
				"france","zhiyong@gmail.com");
		v4.addSpokenLanguage("english");
		v4.addSpokenLanguage("japanese");
		v4.addSkill("java");
		v4.addSkill("C++");
		v4.addSkill("ruby");
		
		Volunteer v5 = new Volunteer("5","haoyi","Xiong","Telecom SudParis",
				"france","haoyi@gmail.com");
		v5.addSpokenLanguage("english");
		v5.addSpokenLanguage("japanese");
		v5.addSpokenLanguage("chinese");
		v5.addSkill("java");
		v5.addSkill("C++");
		v5.addSkill("ruby");
		
		Volunteer v6 = new Volunteer("6","daqiang","Zhang","Telecom SudParis",
				"france","daqiang@gmail.com");
		v6.addSpokenLanguage("english");
		v6.addSpokenLanguage("japanese");
		v6.addSpokenLanguage("chinese");
		v6.addSkill("java");
		v6.addSkill("C++");
		v6.addSkill("python");
		
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