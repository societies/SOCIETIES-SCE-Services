package org.societies.thirdpartyservices.networking.model;


public class UserPublicDetails {
   
	private String displayName;
	private String companyName;
	private String deptName;
	
	
	
	//TODO : Create list 
	private String emphistCompany;
	private String emphistDept;
	
	
	private String eduhistCollege;
	private String eduhistGradYear;
	private String eduhistCourse;
	
	private String showEduHist;
	private String showEmpHist;
	
	
	
	
	public UserPublicDetails(String displayName, String companyName,
			String deptName, String emphistCompany, String emphistDept,
			String eduhistCollege, String eduhistGradYear,
			String eduhistCourse, String showEduHist, String showEmpHist) {
		super();
		this.displayName = displayName;
		this.companyName = companyName;
		this.deptName = deptName;
		this.emphistCompany = emphistCompany;
		this.emphistDept = emphistDept;
		this.eduhistCollege = eduhistCollege;
		this.eduhistGradYear = eduhistGradYear;
		this.eduhistCourse = eduhistCourse;
		this.showEduHist = showEduHist;
		this.showEmpHist = showEmpHist;
	}
	
	public UserPublicDetails() {
		super();
		
	}
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getEmphistCompany() {
		return emphistCompany;
	}
	public void setEmphistCompany(String emphistCompany) {
		this.emphistCompany = emphistCompany;
	}
	public String getEmphistDept() {
		return emphistDept;
	}
	
	public void setEmphistDept(String emphistDept) {
		this.emphistDept = emphistDept;
	}
	public String getEduhistCollege() {
		return eduhistCollege;
	}
	public void setEduhistCollege(String eduhistCollege) {
		this.eduhistCollege = eduhistCollege;
	}
	public String getEduhistGradYear() {
		return eduhistGradYear;
	}
	public void setEduhistGradYear(String eduhistGradYear) {
		this.eduhistGradYear = eduhistGradYear;
	}
	public String getEduhistCourse() {
		return eduhistCourse;
	}
	public void setEduhistCourse(String eduhistCourse) {
		this.eduhistCourse = eduhistCourse;
	}
	public String getShowEduHist() {
		return showEduHist;
	}
	public void setShowEduHist(String showEduHist) {
		this.showEduHist = showEduHist;
	}
	public String getShowEmpHist() {
		return showEmpHist;
	}
	public void setShowEmpHist(String showEmpHist) {
		this.showEmpHist = showEmpHist;
	}
	
	
	
}
