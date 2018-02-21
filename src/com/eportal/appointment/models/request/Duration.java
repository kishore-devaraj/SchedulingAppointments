package com.eportal.appointment.models.request;

public class Duration {
	private String startDate;
	private String endDate;
	private String startTime;
	private String endTime;
	
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
	
	public boolean isAssigned(){
		if((this.getStartDate() != null) && (this.getEndDate() != null) && (this.startTime != null) && (this.endTime != null)){
			return true;
		}else{
			return false;
		}
	}
}
