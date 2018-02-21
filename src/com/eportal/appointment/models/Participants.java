package com.eportal.appointment.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eportal.appointment.sql.MySqlConnect;
import com.eportal.appointment.utils.Constants;
import com.eportal.appointment.utils.TimeAPIWrapper;

public class Participants {
	private String employeeId;
	private Appointment appointment;
	public static List<Participants> notAvailableParticipants = new ArrayList<Participants>();
	
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public Appointment getAppointment() {
		return appointment;
	}
	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}
	
	public void toEntity(MySqlConnect instance){
		
		// To check whether the user already attending another appointment
		try{	
			String sql = "INSERT INTO Participants(`appointmentFK`,`employeeId`) VALUES(?,?)";
			PreparedStatement pq = instance.getConnect().prepareStatement(sql);
			
			pq.setInt(1,this.getAppointment().getAppointmentId());
			pq.setString(2,this.getEmployeeId());
			
			pq.executeUpdate();
			System.out.println(this.getEmployeeId() + " is added for Appointment " + this.getAppointment().getAppointmentId());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean isAvailable() throws SQLException, Exception{
		try{
			MySqlConnect instance = new MySqlConnect(Constants.DATABASENAME);
			PreparedStatement pq = instance.getConnect().prepareStatement("SELECT * FROM Participants WHERE employeeId = ?");
			pq.setString(1,this.getEmployeeId());
			ResultSet rs = pq.executeQuery();
			
			
			if(rs.next()){
				boolean isColliding = this.isColliding(rs);
				if(isColliding){
					notAvailableParticipants.add(this);
					instance.close();
					return false;
				}else{
					instance.close();
					return true;
				}
			}
			instance.close();
			return true;
			
			
		}catch(SQLException e){
			e.printStackTrace();
			throw new SQLException();
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception();
		}
	}
	
	public boolean isColliding(ResultSet rs) throws SQLException, Exception{
		String appointmentId = rs.getString("appointmentFK");
		System.out.println(appointmentId);
			try{
				MySqlConnect instance = new MySqlConnect(Constants.DATABASENAME);
				PreparedStatement pq = instance.getConnect().prepareStatement("SELECT * FROM Appointment WHERE appointmentId = ?");
				pq.setString(1,appointmentId);
				ResultSet appointmentRs = pq.executeQuery();
				if(appointmentRs.next()){
					TimeAPIWrapper wrapper = new TimeAPIWrapper();
					String startTime = appointmentRs.getString("startTime");
					String endTime = appointmentRs.getString("endTime");
					
					Map<String,Long> durationMap = new HashMap<String,Long>();
										
					durationMap.put("previousStart",wrapper.toepochSeconds(wrapper.instantFromString(startTime)));
					durationMap.put("previousEnd",wrapper.toepochSeconds(wrapper.instantFromString(endTime)));
					
					durationMap.put("currentStart",wrapper.toepochSeconds(wrapper.instantFromString(this.getAppointment().getStartUTC())));
					durationMap.put("currentEnd",wrapper.toepochSeconds(wrapper.instantFromString(this.getAppointment().getEndUTC())));
					return this.CollidingCheck(durationMap);	
				}else{
					return false;
				}
			}catch(SQLException e){
				e.printStackTrace();
				throw new SQLException();
			}
		}
	
	public boolean CollidingCheck(Map<String,Long> durationMap){
		if(((durationMap.get("currentStart") <= durationMap.get("previousStart")) && 
				(durationMap.get("currentEnd")) <= durationMap.get("previousStart"))){
			return false; 
		}else if((durationMap.get("currentStart") >= durationMap.get("previousEnd")) &&
				(durationMap.get("currentEnd")) >= durationMap.get("previousEnd")){
			return false;
		}
		return true;
	}
}
