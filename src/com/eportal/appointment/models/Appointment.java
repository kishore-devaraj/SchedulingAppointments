package com.eportal.appointment.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
	private String repetition;
	
	private long duration;
	
	private long startUTC;
	private long endUTC;
	
	private long startDateUTC;
	private long endDateUTC;
	
	private int appointmentId;
	private List<Participants> listOfParticipants;
	
	public static boolean Creationstatus;
	
	private Instant startInstant;
	private Instant endInstant;
	
	
	
	public Instant getStartInstant() {
		return startInstant;
	}
	public void setStartInstant(Instant startInstant) {
		this.startInstant = startInstant;
	}
	
	public Instant getEndInstant() {
		return endInstant;
	}
	public void setEndInstant(Instant endInstant) {
		this.endInstant = endInstant;
	}
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
	public String getRepetition() {
		return repetition;
	}
	public void setRepetition(String repetition) {
		this.repetition = repetition;
	}
	public long getStartDateUTC() {
		return startDateUTC;
	}
	public void setStartDateUTC(long startDateUTC) {
		this.startDateUTC = startDateUTC;
	}
	public long getEndDateUTC() {
		return endDateUTC;
	}
	public void setEndDateUTC(long endDateUTC) {
		this.endDateUTC = endDateUTC;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration() {
		this.duration = (this.endUTC - this.startUTC);
	}
		
	
	public long getStartUTC() {
		return startUTC;
	}
	public void setStartUTC(long startUTC) {
		this.startUTC = startUTC;
	}
	public long getEndUTC() {
		return endUTC;
	}
	public void setEndUTC(long endUTC) {
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
			String sql = "INSERT INTO Appointments(`startTime`,`endTime`,`repetition`)"
					+ "VALUES(?,?,?)";
			PreparedStatement pq = sqlConnect.getConnect().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			
			TimeAPIWrapper timeWrapper = new TimeAPIWrapper();
			
			Instant startInstant = timeWrapper.instantFromDateTime(timeWrapper.getLocalDate(timeWrapper.dateParser(this.getStartDate())),timeWrapper.getLocalTime(timeWrapper.timeParser(this.getStartTime())));
			this.setStartInstant(startInstant);
			pq.setTimestamp(1,Timestamp.from(startInstant));
			
			Instant endInstant = timeWrapper.instantFromDateTime(timeWrapper.getLocalDate(timeWrapper.dateParser(this.getEndDate())),timeWrapper.getLocalTime(timeWrapper.timeParser(this.getEndTime())));
			this.setEndInstant(endInstant);
			
			System.out.println(this.getStartInstant());
			System.out.println(this.getEndInstant());
			
			pq.setTimestamp(2,Timestamp.from(endInstant));
			pq.setString(3,this.getRepetition());
			
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
					sqlConnect.close();
					System.out.println("Appointment is Created");
					Creationstatus = true;
				}else{
					sqlConnect.getConnect().rollback();
					sqlConnect.close();
					System.out.println("Appointment is Cancelled");
					Creationstatus = false;
				}
			
			}catch(Exception e){
				e.printStackTrace();
				sqlConnect.getConnect().rollback();
				sqlConnect.close();
			}
			
		
		}catch(MySQLIntegrityConstraintViolationException e){
			throw new MySQLIntegrityConstraintViolationException();
		}catch(SQLException e){
			e.printStackTrace();
			throw new SQLException();
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception();
		}
	}
}
