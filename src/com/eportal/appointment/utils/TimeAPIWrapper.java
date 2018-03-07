package com.eportal.appointment.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

public class TimeAPIWrapper {
	
	public List<String> dateParser(String date){
		return Arrays.asList(date.split("-"));
	}
	
	public List<String> timeParser(String time){
		return Arrays.asList(time.split(":"));
	}
	
	public LocalDate getLocalDate(List<String> date){
		return LocalDate.of(Integer.valueOf(date.get(2)).intValue(),Integer.valueOf(date.get(1)).intValue(),Integer.valueOf(date.get(0)).intValue());
	}
	
	public LocalTime getLocalTime(List<String> time){
		return LocalTime.of(Integer.valueOf(time.get(0)).intValue(),Integer.valueOf(time.get(1)).intValue());
	}
	
	public Instant instantFromDateTime(LocalDate localDate,LocalTime localTime){
		return LocalDateTime.of(localDate, localTime).toInstant(ZoneOffset.ofHoursMinutes(5,30));
	}
	
	public Instant instantFromString(String instantString){
		return Instant.parse(instantString);
	}
	
	public long toEpochMilli(Instant instant){
		return instant.toEpochMilli();
	}
	
	public LocalDate convertToUtcDate(Instant instant){
		return LocalDateTime.ofInstant(instant,ZoneId.of("UTC")).toLocalDate();
	}
	
	public LocalTime convertToUtcTime(Instant instant){
		return LocalDateTime.ofInstant(instant,ZoneId.of("UTC")).toLocalTime();
	}
	
	
}
