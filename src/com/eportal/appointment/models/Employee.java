package com.eportal.appointment.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.eportal.appointment.sql.MySqlConnect;
import com.eportal.appointment.utils.Constants;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class Employee {
	private String employeeId;
	private String employeeName;
	private String organisation;
	private String password;
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
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	public void toEntity() throws SQLException, MySQLIntegrityConstraintViolationException, Exception {
		MySqlConnect sqlConnect;
		try {
			sqlConnect = new MySqlConnect(Constants.DATABASENAME);
			String sql = "INSERT INTO Employees VALUES(?,?,?,?,?)";
			PreparedStatement pq = sqlConnect.getConnect().prepareStatement("INSERT INTO Employee VALUES(?,?,?,?,?)");
			pq.setString(1,this.getEmployeeId());
			pq.setString(2,this.getEmployeeName());
			pq.setString(3,this.getOrganisation());
			pq.setString(4,this.getPassword());
			pq.setString(5,this.getRole());
			
			int result = pq.executeUpdate();
			System.out.println(result + " rows affected while updating employee Table");
			
		} catch (MySQLIntegrityConstraintViolationException e){
			throw new MySQLIntegrityConstraintViolationException();
		}catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException();
		} catch (ClassNotFoundException e){
			throw new ClassNotFoundException(); 
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
}