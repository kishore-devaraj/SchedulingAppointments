package com.eportal.appointment.models;

import java.util.ArrayList;

public class EmployeeResponse {
	private String employeeId;
	private String employeeName;
	private String organisation;
	private String address;
	private ArrayList<Object> skillsets = new ArrayList<Object>();
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getOrganisation() {
		return organisation;
	}
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public ArrayList<Object> getSkillsets() {
		return skillsets;
	}
	public void setSkillsets(ArrayList<Object> skillsets) {
		this.skillsets = skillsets;
	}
	
	
	
}
