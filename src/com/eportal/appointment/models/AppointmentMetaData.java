package com.eportal.appointment.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentMetaData {
	public LocalDate startDate;
	public LocalDate endDate;
	public LocalTime startTime;
	public LocalTime endTime;
	public String repetition;
	public long duration;
}
