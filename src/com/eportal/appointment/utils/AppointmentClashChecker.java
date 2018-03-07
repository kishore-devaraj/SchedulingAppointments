package com.eportal.appointment.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import com.eportal.appointment.models.Appointment;
import com.eportal.appointment.models.AppointmentMetaData;
import com.eportal.appointment.sql.MySqlConnect;

public class AppointmentClashChecker {
	
	LocalDate dateUtcStart;
	LocalDate dateUtcEnd;
	LocalTime timeUtcStart;
	LocalTime timeUtcEnd;
	String utcRepetition;
	
	LocalDate dateStart;
	LocalDate dateEnd;
	LocalTime timeStart;
	LocalTime timeEnd;
	String repetition; 
	
	
	long daysUtcStart;
	long daysUtcEnd;
	long timeUtcStartSeconds;
	long timeUtcEndSeconds;
	long duration;
	
	long daysStart;
	long daysEnd;
	long timeStartSeconds;
	long timeEndSeconds;
	long utcDuration;
	long timeUltimateEndSeconds;
	
	long oldTimeUtcEndSeconds;
	long oldTimeUtcStartSeconds;
	
	TimeAPIWrapper timeWrapper;
	
	public AppointmentClashChecker(Map<String,String> existingAppointment, Map<String,String>currentAppointment){
		timeWrapper = new TimeAPIWrapper();
		
		// Assigning for the existing appointment
		this.dateUtcStart = this.timeWrapper.getLocalDate(this.timeWrapper.dateParser(existingAppointment.get("utcStartDate")));
		this.dateUtcEnd = this.timeWrapper.getLocalDate(this.timeWrapper.dateParser(existingAppointment.get("utcEndDate")));
		this.timeUtcStart =  this.timeWrapper.getLocalTime(this.timeWrapper.timeParser(existingAppointment.get("utcStartTime")));
		this.timeUtcEnd =  this.timeWrapper.getLocalTime(this.timeWrapper.timeParser(existingAppointment.get("utcEndTime")));	
		this.utcRepetition = existingAppointment.get("utcRepetition");
		
		this.dateStart = this.timeWrapper.getLocalDate(this.timeWrapper.dateParser(currentAppointment.get("startDate")));
		this.dateEnd = this.timeWrapper.getLocalDate(this.timeWrapper.dateParser(currentAppointment.get("endDate")));
		this.timeStart = this.timeWrapper.getLocalTime(this.timeWrapper.timeParser(currentAppointment.get("startTime")));
		this.timeEnd = this.timeWrapper.getLocalTime(this.timeWrapper.timeParser(currentAppointment.get("endTime")));
		this.repetition = currentAppointment.get("repetition");
		
		this.convertEverythingToUtc();
		System.out.println(this.checkForClashing());
	}
	
	public AppointmentClashChecker(AppointmentMetaData existingAppointment, AppointmentMetaData currentAppointment){
		timeWrapper = new TimeAPIWrapper();
		
		this.dateUtcStart = existingAppointment.startDate;
		this.dateUtcEnd = existingAppointment.endDate;
		this.timeUtcStart = existingAppointment.startTime;
		this.timeUtcEnd = existingAppointment.endTime;
		this.utcRepetition = existingAppointment.repetition;
		
		this.dateStart = currentAppointment.startDate;
		this.dateEnd = currentAppointment.endDate;
		this.timeStart = currentAppointment.startTime;
		this.timeEnd = currentAppointment.endTime;
		this.repetition = currentAppointment.repetition;
			
		this.convertEverythingToUtc();
	}
	
	public boolean checkForClashing(){
		// Check whether the existing appointment is recurring or not
			if(this.isEligibleForPrimaryCheck()){
				return this.primaryChecking();
			}else{
				return this.checkForRecurring();
			}
	}
	
	public boolean checkForRecurring(){
			boolean periodInstanceNotFound = true;
			this.oldTimeUtcEndSeconds = this.timeUtcEndSeconds;
			this.oldTimeUtcStartSeconds = this.timeUtcStartSeconds;
			
			// If the event happens before or after the periods of the existing event
			if(this.timeEndSeconds <= this.timeUtcStartSeconds){
				System.out.println("Event ends before the existing one");
				return false;
			}else{
				long tempTimeUtcEndSeconds = this.timeWrapper.instantFromDateTime(this.dateUtcEnd,this.timeUtcEnd).toEpochMilli();
				if(this.timeStartSeconds >= tempTimeUtcEndSeconds){
					System.out.println("Event starts after the existing one");
					return false;
				}
			}
			
			
			while(periodInstanceNotFound){
				
				// If it happens at the same time of existing event (COLLISION)
				if((this.timeStartSeconds == this.timeUtcStartSeconds) && (this.timeEndSeconds == this.timeUtcEndSeconds)){
					System.out.println("Events happens at the same start and end time of the existing one");
					periodInstanceNotFound = false;
					return true;
				}
				
				// If the start happens in between the existing event (COLLISION)
				if((this.timeStartSeconds > this.timeUtcStartSeconds) && (this.timeStartSeconds < this.timeUtcEndSeconds)){
					System.out.println("Event starts in between the existing one");
					periodInstanceNotFound = false;
					return true;
				}
				
				// If the end happens in between the existing event (COLLISION)
				if((this.timeEndSeconds > this.timeUtcStartSeconds) && (this.timeEndSeconds < this.timeUtcEndSeconds)){
					System.out.println("Event ends in between the existing one");
					periodInstanceNotFound = false;
					return true;
				}
				
				if(this.timeEndSeconds <= this.timeUtcStartSeconds){
					periodInstanceNotFound = false;
					if(this.timeStartSeconds >= this.oldTimeUtcEndSeconds){
						System.out.println("There is no 0 colliding");
						return false;
					}else{
						System.out.println("Yes there is a collsion");
						return true;
					}
				}
				
				if((this.timeStartSeconds <= this.timeUtcStartSeconds) && (this.timeEndSeconds >= this.timeUtcEndSeconds)){
					System.out.println("There is a collision");
					periodInstanceNotFound = false;
					return true;
				}
			
				
				if(this.timeStartSeconds >= this.timeUtcEndSeconds){
					if(this.utcRepetition.equals("dialy")){
						this.dateUtcStart = this.dateUtcStart.plusDays(1);
					}else if(this.utcRepetition.equals("weekly")){
						this.dateUtcStart = this.dateUtcStart.plusWeeks(1);
					}else if(this.utcRepetition.equals("monthly")){
						this.dateUtcStart= this.dateUtcStart.plusMonths(1);
					}else if(this.utcRepetition.equals("yearly")){
						this.dateUtcStart = this.dateUtcStart.plusYears(1);
					}
					
					Instant instant = this.timeWrapper.instantFromDateTime(this.dateUtcStart,this.timeUtcStart);
					System.out.println(instant);
					this.timeUtcStartSeconds = instant.toEpochMilli();
					this.timeUtcEndSeconds = this.timeUtcStartSeconds + this.utcDuration;
					
				}
			}
			System.out.println("No matching with any condition!!!!. Might be bug here ");
			return true;
	}
	
	
	public boolean primaryChecking(){
		if(this.timeEndSeconds <= this.timeUtcStartSeconds){
			return false;
		}else if((this.dateUtcEnd.isEqual(this.dateStart)) || (this.dateUtcEnd.isBefore(this.dateStart))){
			if(this.timeUtcEndSeconds <= this.timeStartSeconds){
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}
	
	public boolean isEligibleForPrimaryCheck(){
		if(this.utcRepetition.equals("none")){
			return true;
		}else{
			return false;
		}
	}

	
	
	public void convertEverythingToUtc(){
		this.convertExisting();
		this.convertCurrent();
		
		System.out.println("Existing: ");
		
		System.out.println(this.daysUtcStart);
		System.out.println(this.daysUtcEnd);
		System.out.println(this.timeUtcStartSeconds);
		System.out.println(this.timeUtcEndSeconds);
		System.out.println(this.utcDuration);
		System.out.println("---------");
		
		
		System.out.println("Current: ");
		
		System.out.println(this.daysStart);
		System.out.println(this.daysEnd);
		System.out.println(this.timeStartSeconds);
		System.out.println(this.timeEndSeconds);
		System.out.println(this.duration);
		System.out.println("---------");
		
	}
	
	public void convertCurrent(){
		Instant StartInstant = timeWrapper.instantFromDateTime(this.dateStart, this.timeStart);
		LocalDate localDateUtc = timeWrapper.convertToUtcDate(StartInstant);
		this.daysStart = localDateUtc.toEpochDay();
		this.timeStartSeconds = StartInstant.toEpochMilli();
	
	
		if(this.repetition.equals(null)){
			Instant EndInstant = timeWrapper.instantFromDateTime(this.dateEnd, this.timeEnd);
			localDateUtc = timeWrapper.convertToUtcDate(EndInstant);
			this.daysEnd = localDateUtc.toEpochDay();
			this.timeEndSeconds = EndInstant.toEpochMilli();
		}else{
			// Same day with a ending time
			Instant EndInstant = timeWrapper.instantFromDateTime(this.dateStart,this.timeEnd);
			this.timeEndSeconds = EndInstant.toEpochMilli();			
			
			// When it's actually being ended
			EndInstant = timeWrapper.instantFromDateTime(this.dateEnd,this.timeEnd);
			localDateUtc = timeWrapper.convertToUtcDate(EndInstant);
			this.daysEnd = localDateUtc.toEpochDay();
		}	
		this.duration = this.timeEndSeconds - this.timeStartSeconds;
		

	}
	
	public void convertExisting(){
			Instant utcStartInstant = timeWrapper.instantFromDateTime(this.dateUtcStart, this.timeUtcStart);
			LocalDate localDateUtc = timeWrapper.convertToUtcDate(utcStartInstant);
			this.daysUtcStart = localDateUtc.toEpochDay();
			this.timeUtcStartSeconds = utcStartInstant.toEpochMilli();
			
			//  Check whether the old appointment is recurring and its end date is mentioned
			if(this.utcRepetition.equals(null)){
				Instant utcEndInstant = timeWrapper.instantFromDateTime(this.dateUtcEnd, this.timeUtcEnd);
				localDateUtc = timeWrapper.convertToUtcDate(utcEndInstant);
				this.daysUtcEnd = localDateUtc.toEpochDay();
				this.timeUtcEndSeconds = utcEndInstant.toEpochMilli();
			}else{
				// Same day with a ending time
				Instant utcEndInstant = timeWrapper.instantFromDateTime(this.dateUtcStart,this.timeUtcEnd);
				this.timeUtcEndSeconds = utcEndInstant.toEpochMilli();
				// When it's actually being ended
				utcEndInstant = timeWrapper.instantFromDateTime(this.dateUtcEnd,this.timeUtcEnd);
				localDateUtc = timeWrapper.convertToUtcDate(utcEndInstant);
				this.timeUltimateEndSeconds = utcEndInstant.toEpochMilli();
				this.daysUtcEnd = localDateUtc.toEpochDay();
			}
		
		this.utcDuration = this.timeUtcEndSeconds - this.timeUtcStartSeconds;
	}

	
	public static void createPossibleDateTime(Timestamp existingStartTimestamp,Timestamp existingEndTimestamp,Instant currentEndInstant,String appointment){
		MySqlConnect sqlInstance;
		try {
			sqlInstance = new MySqlConnect(Constants.DATABASENAME);
			String sql = null;
			if(appointment.equals("none") || appointment.equals("daily")){
				sql = "{CALL createPossibleDays(?, ?, ?)}";
			}else if(appointment.equals("weekly")){
				sql = "{CALL createPossibleWeeks(?, ?, ?)}";
			}else if(appointment.equals("monthly")){
				sql= "{CALL createPossibleMonths(?, ?, ?)}";
			}else{
				sql = "{CALL createPossibleYears(?, ?, ?)}";
			}
				
			PreparedStatement pq = sqlInstance.getConnect().prepareCall(sql);
			pq.setTimestamp(1,existingStartTimestamp);
			pq.setTimestamp(2,existingEndTimestamp);
			pq.setTimestamp(3,Timestamp.from(currentEndInstant));
			int result = pq.executeUpdate();
			System.out.println(result + " rows created in PossibleWeeks");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void addToStagedAppointments(int appointmentId, Instant startInstant, Instant endInstant,String repetition, Timestamp existingEndTimeStamp){
		try {
			MySqlConnect sqlInstance = new MySqlConnect(Constants.DATABASENAME);
			Statement stmt = sqlInstance.getConnect().createStatement();
			String sql = "TRUNCATE TABLE StagedAppointments";
			stmt.execute(sql);
			if((repetition.equals("none")) || (repetition.equals("daily"))){
				sql = "{CALL createDailyStaged(?, ?, ?, ?)}";
			}else if(repetition.equals("weekly")){
				sql = "{CALL createWeeklyStaged(?, ?, ?, ?)}";
			}else if(repetition.equals("monthly")){
				sql = "{CALL createMonthlyStaged(?, ?, ?, ?)}";
			}else if(repetition.equals("yearly")){
				sql = "{CALL createYearlyStaged(?, ?, ?, ?)}";
			}
			
			PreparedStatement pq = sqlInstance.getConnect().prepareCall(sql);
			pq.setTimestamp(1,existingEndTimeStamp);
			pq.setTimestamp(2,Timestamp.from(startInstant));
			pq.setTimestamp(3,Timestamp.from(endInstant));
			pq.setInt(4,appointmentId);
			int result = pq.executeUpdate();
			System.out.println(result + " rows affected in Staged Appointments");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean getCollidingAppointmentIds(String repetition){
		String sql = "SELECT appointmentId FROM PossibleWeeks INNER JOIN "
					+ "StagedAppointments as Appointments "
					+ "ON ((((TIME(Appointments.startTime) BETWEEN TIME(PossibleWeeks.startTime) AND TIME(PossibleWeeks.endTime)) OR "
					+" (TIME(Appointments.endTime) BETWEEN TIME(PossibleWeeks.startTime) AND TIME(PossibleWeeks.endTime))) "
					+" OR "
					+"((TIME(PossibleWeeks.startTime) BETWEEN TIME(Appointments.startTime) AND TIME(Appointments.endTime)) OR "
					+"(TIME(PossibleWeeks.endTime) BETWEEN TIME(Appointments.startTime) AND TIME(Appointments.endTime)))) "     
					+" AND "
					+"(((DATE(Appointments.startTime) BETWEEN DATE(PossibleWeeks.startTime) AND DATE(PossibleWeeks.endTime)) OR "
					+"(DATE(Appointments.endTime) BETWEEN DATE(PossibleWeeks.startTime) AND DATE(PossibleWeeks.endTime))) "
					+" OR "
					+"((DATE(PossibleWeeks.startTime) BETWEEN DATE(Appointments.startTime) AND DATE(Appointments.endTime)) OR "
					+"(DATE(PossibleWeeks.endTime) BETWEEN DATE(Appointments.startTime) AND DATE(Appointments.endTime)))))";
		MySqlConnect sqlInstance;
		try {
			sqlInstance = new MySqlConnect(Constants.DATABASENAME);
			Statement stmt = sqlInstance.getConnect().createStatement();
			ResultSet finalRs = stmt.executeQuery(sql);
			while(finalRs.next()){
				System.out.println("Colliding appointment Id" + finalRs.getInt(1));
				return true;
			}
			System.out.println("No result found");
			return false;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return true;
		
		
	}
	
	public static void main(String[] args) {
		String startDate = "15-04-2018";
		String endDate = "15-04-2018";
		String startTime = "13:31";
		String endTime = "14:00";
		String repetition = "none";
		
//		String utcStartDate = "25-02-2018";
//		String utcEndDate = "28-3-2020";		
//		String utcStartTime = "8:00";
//		String utcEndTime = "9:00";
//		String utcRepetition = "weekly";
//		

		
		
//		Map<String,String> existingAppointment = new HashMap<String,String>();
		Map<String,String> currentAppointment = new HashMap<String,String>();
		
//		existingAppointment.put("utcStartDate", utcStartDate);
//		existingAppointment.put("utcEndDate", utcEndDate);
//		existingAppointment.put("utcStartTime", utcStartTime);
//		existingAppointment.put("utcEndTime", utcEndTime);
//		existingAppointment.put("utcRepetition", utcRepetition);
		
		currentAppointment.put("startDate", startDate);
		currentAppointment.put("endDate", endDate);
		currentAppointment.put("startTime", startTime);
		currentAppointment.put("endTime", endTime);
		currentAppointment.put("repetition", repetition);
		
		
//		AppointmentClashChecker appointment = new AppointmentClashChecker(existingAppointment, currentAppointment);
//		
		
	}
}
