package com.eportal.appointment.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class Sha256Hex {
	private static MessageDigest digest;
	private final static String SALT = "mysalt";
	
	private Sha256Hex(){
		try {
			digest = MessageDigest.getInstance("Sha-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			digest = null;
			System.out.println("Cannot instantiate sha256Hex singleton class");
		}
	}

	public static MessageDigest getInstance(){
		if (digest == null){
			Sha256Hex obj = new Sha256Hex();
		}
		return digest;
	}
	
	public static String hashPassword(String password){
		String saltedPassword = SALT + password; 
		byte[] bytes;
		try {
			bytes = getInstance().digest(saltedPassword.getBytes("UTF-8"));
			return DatatypeConverter.printHexBinary(bytes);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}
