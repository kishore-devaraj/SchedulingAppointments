package com.eportal.appointment.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.eportal.appointment.sql.MySqlConnect;
import com.eportal.appointment.utils.AppointmentClashChecker;
import com.eportal.appointment.utils.Constants;
import com.eportal.appointment.utils.TimeAPIWrapper;

public class Participants {
	private String employeeId;
	private Appointment appointment;
	public static List<Participants> notAvailableParticipants = new ArrayList<Participants>();
	private TimeAPIWrapper timeWrapper = new TimeAPIWrapper();
	
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
		int appointmentId = rs.getInt("appointmentFK");
			try{
				MySqlConnect sqlInstance = new MySqlConnect(Constants.DATABASENAME);
				String sql = "SELECT `startTime`,`endTime`,`appoinmentId`,`repetition` FROM Appointments WHERE appoinmentId = ?";
				PreparedStatement pq = sqlInstance.getConnect().prepareStatement(sql);
				pq.setInt(1,appointmentId);
				ResultSet appointmentRs = pq.executeQuery();
				
				while(appointmentRs.next()){
					AppointmentClashChecker.addToStagedAppointments(this.appointment.getAppointmentId(),this.appointment.getStartInstant(),this.appointment.getEndInstant(),this.appointment.getRepetition(),appointmentRs.getTimestamp(2));
					AppointmentClashChecker.createPossibleDateTime(appointmentRs.getTimestamp(1),appointmentRs.getTimestamp(2),this.appointment.getEndInstant(),appointmentRs.getString(4));
					AppointmentClashChecker.getCollidingAppointmentIds(appointmentRs.getString(4));
					return true;
				}
				
				return true;
			}catch(SQLException e){
				e.printStackTrace();
				throw new SQLException();
			}
		}	
}
