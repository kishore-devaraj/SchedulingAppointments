package com.eportal.appointment.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
	
	public long toepochSeconds(Instant instant){
		return instant.toEpochMilli();
	}
	
	
	
	public static void main(String[] args) {
		String startDate = "21-02-2018";
		String startTime = "13:00";
		String endDate = "21-02-2018";
		String endTime = "14:00";
		
		
		
//		TimeAPIWrapper wrapper = new TimeAPIWrapper();
//		Instant instant = wrapper.instantFromDateTime(wrapper.getLocalDate(wrapper.dateParser(startDate)),wrapper.getLocalTime(wrapper.timeParser(startTime)));
//		System.out.println(instant);
//		System.out.println(wrapper.toepochSeconds(instant));
//		
//
//		instant = wrapper.instantFromDateTime(wrapper.getLocalDate(wrapper.dateParser(endDate)),wrapper.getLocalTime(wrapper.timeParser(endTime)));
//		System.out.println(instant);
//		System.out.println(wrapper.toepochSeconds(instant));
	}
	
}
