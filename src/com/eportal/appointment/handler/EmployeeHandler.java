package com.eportal.appointment.handler;

import com.eportal.appointment.models.Employee;
import com.eportal.appointment.utils.GenericResponse;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class EmployeeHandler {
	public GenericResponse create(Employee employee, GenericResponse response){
		try{
			employee.toEntity();
			response.setData("message","Employee Successfully created");
		}catch(MySQLIntegrityConstraintViolationException e){
			response.setCode(400);
			response.setData("error","EmployeeId already exists!");
		}catch(Exception e){
			e.printStackTrace();
			response.setCode(500);
			response.setData("error","Exception occured while creating new employee");
		}
		return response;
	}
	
}
