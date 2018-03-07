package com.eportal.appointment.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.eportal.appointment.handler.AppointmentHandler;
import com.eportal.appointment.models.request.AppointmentRequest;
import com.eportal.appointment.utils.GenericResponse;

@Path("/")
public class AppointmentService {
	
	@POST
	@Path("/appointment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse scheduleAppointment(AppointmentRequest request){
		GenericResponse response = new GenericResponse();
		try{
			if(request.getTitle() != null && 
				request.getOrganiser() != null &&
				(!request.getGuests().isEmpty()) &&
				request.getDuration().isAssigned())
				{
					AppointmentHandler appointmentHandler = new AppointmentHandler();
					response = appointmentHandler.create(request.toHandlerModel(),response); 
				
				}else{
					response.setCode(400);
					response.setData("error","Please fill up all the required fields");
				}
		}catch(Exception e){
			response.setCode(400);
			response.setData("error","Exception occured while processing your data");
		}
		return response;
	}
	
}
