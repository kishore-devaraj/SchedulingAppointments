package com.eportal.appointment.models.request;

import java.util.ArrayList;
import java.util.List;

import com.eportal.appointment.models.Appointment;
import com.eportal.appointment.models.Participants;

public class AppointmentRequest {
	private String organiser;
	private String title;
	private String description;
	private Duration duration;
	private ArrayList<String> guests;
	
	
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
	public Duration getDuration() {
		return duration;
	}
	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	public ArrayList<String> getGuests() {
		return guests;
	}
	public void setGuests(ArrayList<String> guests) {
		this.guests = guests;
	}
	
	public Appointment toHandlerModel(){
		Appointment appointment = new Appointment();
		appointment.setOrganiser(this.getOrganiser());
		appointment.setTitle(this.getTitle());
		appointment.setDescription(this.getDescription());
		appointment.setStartDate(this.getDuration().getStartDate());
		appointment.setEndDate(this.getDuration().getEndDate());
		appointment.setStartTime(this.getDuration().getStartTime());
		appointment.setEndTime(this.getDuration().getEndTime());
		appointment.setRepetition(this.getDuration().getRepetition().toLowerCase());
		appointment.setListOfParticipants(this.toParticipantsModel());
		
		return appointment;
	}
	
	public List<Participants> toParticipantsModel(){
		List<Participants> listOfParticipants = new ArrayList<Participants>();
		for(String employeeId: this.getGuests()){
			Participants participant = new Participants();
			participant.setEmployeeId(employeeId);
			listOfParticipants.add(participant);
		}
	
		return listOfParticipants;
	}
}
