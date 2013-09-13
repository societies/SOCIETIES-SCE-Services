package org.societies.thirdpartyservices.networking.model;


public class EmpHistoryItem {
   
	private String company;
	private String department;
	
	

	public EmpHistoryItem() {
		super();
		
	}
	
	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}
	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}
	/**
	 * @return the department
	 */
	public String getDepartment() {
		return department;
	}
	/**
	 * @param department the department to set
	 */
	public void setDepartment(String department) {
		this.department = department;
	}
	
}
