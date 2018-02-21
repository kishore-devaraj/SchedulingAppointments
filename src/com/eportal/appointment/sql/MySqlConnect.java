package com.eportal.appointment.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eportal.appointment.utils.Constants;


public class MySqlConnect {
	
	private Connection connect = null;
	private final static String username = "root";
	private final static String password = "toor";
	private final static Logger logger = Logger.getLogger(MySqlConnect.class.getName());
	
	static {
		logger.setLevel(Level.INFO);
	}
		
	public Connection getConnect() {
		return connect;
	}
	
	public void close() throws SQLException{
		this.connect.close();
	}


	public MySqlConnect(String databaseName) throws ClassNotFoundException, SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + databaseName ,username, password);		
			logger.info("Database connected successfully");
		} catch (SQLException e) {
	    	e.printStackTrace();
	    }
	
	}
	
	
	/*
	 	public void somefunction throws SQLException, Exception{
		try{
			MySqlConnect instance = new MySqlConnect(Constants.DATABASENAME);
		}catch(SQLException e){
			e.printStackTrace();
			throw new SQLException();
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception();
		}
	}
	*/
}
