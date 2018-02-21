package com.eportal.appointment.models.request;

import com.eportal.appointment.models.Employee;

public class EmployeeRequest {
	private String employeeId;
	private String employeeName;
	private String organisation;
	private String password;
	private String confirmPassword;
	private String role;
	
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role.toLowerCase();
	}
	
	public Employee toHandlerModel(){
		Employee employee = new Employee();
		employee.setEmployeeId(this.employeeId);
		employee.setEmployeeName(this.employeeName);
		employee.setOrganisation(this.organisation);
		employee.setPassword(this.password);
		employee.setRole(this.getRole());
		return employee;
	}
}
