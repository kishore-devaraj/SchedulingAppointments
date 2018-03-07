package com.eportal.appointment.models.request;

public class Duration {
	private String startDate;
	private String endDate;
	private String startTime;
	private String endTime;
	private String repetition;
	
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
	
	public String getRepetition() {
		return repetition;
	}
	public void setRepetition(String repetition) {
		this.repetition = repetition;
	}
	
	public boolean isAssigned(){
		if((this.getStartDate() != null) && (this.getEndDate() != null) && (this.startTime != null) && (this.endTime != null) && (this.repetition != null)){
			return true;
		}else{
			return false;
		}
	}
}
