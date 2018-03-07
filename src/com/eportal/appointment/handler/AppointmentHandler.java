package com.eportal.appointment.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.eportal.appointment.models.Appointment;
import com.eportal.appointment.models.Participants;
import com.eportal.appointment.sql.MySqlConnect;
import com.eportal.appointment.utils.Constants;
import com.eportal.appointment.utils.GenericResponse;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class AppointmentHandler {
	
	private ResultSet results = null;
	private static List<String> noEmployeesExist;
	
	{
		noEmployeesExist = new ArrayList<String>();
	}
	
	public GenericResponse create(Appointment appointment,GenericResponse response){
		
		if(this.isRepetitionValid(appointment)){
			// Check whether the organiser is manager or not
			if(this.isEmployeeExists(appointment.getOrganiser())){
				System.out.println("yes someone exists with this id");
				if(this.isManager(appointment.getOrganiser())){
					try{
						// Creating appointment table
						appointment.toEntity();
						
						try{
							if(Appointment.Creationstatus == true){
								response.setCode(200);
								response.setData("message","Appointment fixed");
							}else{
								response.setCode(400);
								List<String> employeeIds = new ArrayList<String>();
								for(Participants p: Participants.notAvailableParticipants){
									employeeIds.add(p.getEmployeeId());
								}
								response.setData("error","Appointment is cancelled due to non availablity of employees"); 
								response.setData("notAvailableIds",employeeIds);	
							}
						}catch(NullPointerException e){
							response.setCode(400);
							response.setData("error","Error while creating your appointment. Please try again later");
						}
					}catch(MySQLIntegrityConstraintViolationException e){
						response.setCode(400);
						response.setData("error","You have already created this appointment");
					}catch(Exception e){
						e.printStackTrace();
						response.setCode(500);
						response.setData("error","Error happened while creating this processing your request");
					}
				}else{
					response.setCode(400);
					response.setData("error","Organiser is not valid manager");
				}			
			}else{
				response.setCode(400);
				response.setData("error","No Organiser exists!");
			}
		}else{
			response.setCode(400);
			response.setData("error","Repetition are allowed only for 'daily','weekly','monthly','yearly','None'");
		}
		return response;
	}
	
	
	public boolean isRepetitionValid(Appointment appointment){
		String repetitionStr = appointment.getRepetition();
		if(repetitionStr.equals("none") || repetitionStr.equals("daily") ||
				repetitionStr.equals("weekly") || repetitionStr.equals("monthly") ||
				repetitionStr.equals("yearly")){
			return true;
		}else{
			return false;
		}
	}
	
	
	public boolean isEmployeeExists(String employeeId){
		try{
			MySqlConnect sqlConnect = new MySqlConnect(Constants.DATABASENAME);
			PreparedStatement pq = sqlConnect.getConnect().prepareStatement("SELECT * FROM Employee WHERE employeeId = ?");
			pq.setString(1,employeeId);
			this.results = pq.executeQuery();
			while(this.results.next()){
				this.results.beforeFirst();
				return true;
			}
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean isManager(String employeeId){
			try {
				while(this.results.next()){
					if(this.results.getString(5).equals("manager")){
						return true;
					}else{
						return false;
					}
				}
				return false;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
	
}
