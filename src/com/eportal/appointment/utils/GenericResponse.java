package com.eportal.appointment.utils;

import java.util.HashMap;
import java.util.Map;

public class GenericResponse {
	private int code = 200;
	private Map<String,Object> data = new HashMap<String,Object>();
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(String key, Object object){
		this.data.put(key, object);
	}
	public GenericResponse group(String listOfObjects,Object...objects){
		this.data.put(listOfObjects,objects);
		return this;
	}
}
