package com.eportal.appointment.exceptions;

public class DuplicateEntry extends IllegalAccessException{
	public DuplicateEntry(String s){
		super(s);
	}
	
	public DuplicateEntry(){
		super();
	}
}
