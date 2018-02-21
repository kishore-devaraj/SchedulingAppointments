package com.eportal.appointment.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.eportal.appointment.sql.MySqlConnect;
import com.eportal.appointment.utils.Constants;
import com.eportal.appointment.utils.TimeAPIWrapper;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class Appointment {
	private String organiser;
	private String title;
	private String description;
	private String startTime;
	private String endTime;
	private String startDate;
	private String endDate;
	
	private String startUTC;
	private String endUTC;
	
	private int appointmentId;
	private List<Participants> listOfParticipants;
	
	public static boolean Creationstatus;
	
	public String getOrganiser() {
		return organiser;
	}
	public void setOrganiser(String organiser) {
		this.organiser = organiser;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	
	
	
	public String getStartUTC() {
		return startUTC;
	}
	public void setStartUTC(String startUTC) {
		this.startUTC = startUTC;
	}
	public String getEndUTC() {
		return endUTC;
	}
	public void setEndUTC(String endUTC) {
		this.endUTC = endUTC;
	}
	public int getAppointmentId() {
		return appointmentId;
	}
	public void setAppointmentId(int appointmentId) {
		this.appointmentId = appointmentId;
	}
	public List<Participants> getListOfParticipants() {
		return listOfParticipants;
	}
	public void setListOfParticipants(List<Participants> listOfParticipants) {
		this.listOfParticipants = listOfParticipants;
	}
	
	public void toEntity() throws MySQLIntegrityConstraintViolationException, SQLException, Exception{
		Creationstatus = false;
		Participants.notAvailableParticipants = new ArrayList<Participants>();
		MySqlConnect sqlConnect = null ;
		try{
			sqlConnect = new MySqlConnect(Constants.DATABASENAME);
			sqlConnect.getConnect().setAutoCommit(false);
			PreparedStatement pq = sqlConnect.getConnect().prepareStatement("INSERT INTO Appointment(`title`,`description`,`organiser`,`startTime`,`endTime`) VALUES(?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			
			TimeAPIWrapper wrapper = new TimeAPIWrapper();
			
			Instant instant = wrapper.instantFromDateTime(wrapper.getLocalDate(wrapper.dateParser(this.getStartDate())),wrapper.getLocalTime(wrapper.timeParser(this.getStartTime())));
			this.setStartUTC(instant.toString());
			
			instant = wrapper.instantFromDateTime(wrapper.getLocalDate(wrapper.dateParser(this.getEndDate())),wrapper.getLocalTime(wrapper.timeParser(this.getEndTime())));
			this.setEndUTC(instant.toString());
			
			pq.setString(1,this.getTitle());
			pq.setString(2,this.getDescription());
			pq.setString(3,this.getOrganiser());
			pq.setString(4,this.getStartUTC());
			pq.setString(5,this.getEndUTC());
			
			int results = pq.executeUpdate();
			ResultSet rs = pq.getGeneratedKeys();
			if(rs.next()){
				this.setAppointmentId(rs.getInt(1));
			}
			
			System.out.println(results + " rows affected in appointment tables");
			System.out.println("AppointmentId is " + this.getAppointmentId());
			
			// Check whether all the employees are valid
			
			try{
				boolean isAppointmentEligible = true;
				
				// Update the Guest Table with the appointment Id
				for(Participants participant: this.getListOfParticipants()){
					participant.setAppointment(this);
					boolean isAvailable = participant.isAvailable();
					System.out.println("Available " + isAvailable);
					
					
					if(isAvailable){
						participant.toEntity(sqlConnect);						
					}else{
						isAppointmentEligible = false;
						System.out.println(participant.getEmployeeId() + " is not eligible");
					}
				}
				
				
				if(isAppointmentEligible){
					sqlConnect.getConnect().commit();
					System.out.println("Appointment is Created");
					Creationstatus = true;
				}else{
					sqlConnect.getConnect().rollback();
					System.out.println("Appointment is Cancelled");
					Creationstatus = false;
				}
			
			}catch(Exception e){
				e.printStackTrace();
				sqlConnect.getConnect().rollback();
			}
			
			sqlConnect.close();
			sqlConnect = null;
		
		}catch(MySQLIntegrityConstraintViolationException e){
			throw new MySQLIntegrityConstraintViolationException();
		}catch(SQLException e){
			e.printStackTrace();
			throw new SQLException();
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception();
		}finally{
			if(sqlConnect != null){
				sqlConnect.getConnect().rollback();
				sqlConnect.close();
			}
		}
	}
	
}
