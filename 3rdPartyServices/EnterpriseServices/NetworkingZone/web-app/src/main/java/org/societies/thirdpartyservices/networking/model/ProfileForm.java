
package org.societies.thirdpartyservices.networking.model;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.ext3p.networking.Education;
import org.societies.api.ext3p.networking.Employment;


public class ProfileForm {
	
	String name;
	String email;
	String homelocation;
	String sex;
	String company;
	String department;
	String position;
	String about;
	List<Education> educationHistory;
	List<Employment> employmentHistory;
	Integer personalvisible;
	Integer aboutvisible;
	Integer employvisible;
	Integer emphistvisible;
	Integer eduhistvisible;
	String newnote;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	
	public List<Education> getEducationHistory() {
		if (educationHistory == null)
			educationHistory = new ArrayList<Education>();	
		return educationHistory;
	}
	public void setEducationHistory(List<Education> educationHistory) {
		this.educationHistory = educationHistory;
	}
	
	public List<Employment> getEmploymentHistory() {
		if (employmentHistory == null)
			employmentHistory = new ArrayList<Employment>();	
		return employmentHistory;
	}
	public void setEmploymentHistory(List<Employment> employmentHistory) {
		this.employmentHistory = employmentHistory;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getHomelocation() {
		return homelocation;
	}
	public void setHomelocation(String homelocation) {
		this.homelocation = homelocation;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Integer getPersonalvisible() {
		return personalvisible;
	}
	public void setPersonalvisible(Integer personalvisible) {
		this.personalvisible = personalvisible;
	}
	public Integer getAboutvisible() {
		return aboutvisible;
	}
	public void setAboutvisible(Integer aboutvisible) {
		this.aboutvisible = aboutvisible;
	}
	public Integer getEmphistvisible() {
		return emphistvisible;
	}
	public void setEmphistvisible(Integer emphistvisible) {
		this.emphistvisible = emphistvisible;
	}
	public Integer getEduhistvisible() {
		return eduhistvisible;
	}
	public void setEduhistvisible(Integer eduhistvisible) {
		this.eduhistvisible = eduhistvisible;
	}
	public Integer getEmployvisible() {
		return employvisible;
	}
	public void setEmployvisible(Integer employvisible) {
		this.employvisible = employvisible;
	}
	public String getNewnote() {
		return newnote;
	}
	public void setNewnote(String newnote) {
		this.newnote = newnote;
	}
	
	
}
