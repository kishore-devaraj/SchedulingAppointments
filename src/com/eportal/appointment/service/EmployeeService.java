package com.eportal.appointment.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
//
//import com.eportal.handler.EmployeeHandler;
//import com.eportal.models.request.EmployeeRequest;
//import com.eportal.utils.GenericResponse;
import javax.ws.rs.core.MediaType;

import com.eportal.appointment.handler.AppointmentHandler;
import com.eportal.appointment.handler.EmployeeHandler;
import com.eportal.appointment.models.request.AppointmentRequest;
import com.eportal.appointment.models.request.EmployeeRequest;
import com.eportal.appointment.utils.GenericResponse;

@Path("/")
public class EmployeeService {
	private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());
	
	
	{
		LOGGER.setLevel(Level.INFO);
	}
	
	@POST
	@Path("/employee")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse createEmployee(EmployeeRequest employee){
		
		GenericResponse response = new GenericResponse();
		try{
			// Checking the mandatory field to create a employee
			if (employee.getEmployeeName() != null &&
				employee.getOrganisation() != null &&
				employee.getEmployeeId()!= null &&
				employee.getPassword() != null &&
				employee.getConfirmPassword() != null && 
				employee.getRole() != null)
			{		
				if ((employee.getRole().toLowerCase().equals("manager")) || (employee.getRole().toLowerCase().equals("staff"))){
					if(employee.getPassword().equals(employee.getConfirmPassword())){
						EmployeeHandler employeeHandler = new EmployeeHandler();
						response = employeeHandler.create(employee.toHandlerModel(), response);
					}else{
						response.setCode(400);
						response.setData("error","Password doesn't match");;
					}
				}else{
					response.setCode(400);
					response.setData("error","Role of the employee should be either 'manager' or 'staff'!");
				}				
			} else {
				response.setCode(400);
				response.setData("error","Please fill up all the required details");
			}
		}catch(Exception e){
			e.printStackTrace();
			response.setCode(400);
			response.setData("error", "Error happened in processing the data");
		}
		return response;
	}
	

	/*
	@DELETE
	@Path("/{org}/employee/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse deleteEmployee(
			@PathParam("id") String id,
			@PathParam("org") String org
			){
		GenericResponse response = new GenericResponse();
		if (id != null){
			EmployeeHandler employeeHandler = new EmployeeHandler();
			// response = employeeHandler.delete(org,id,response);
			
		}else{
			response.setCode(400);
			response.setData("error", "Specify in the Employee Id in the path param");
		}
		return response;	
	}
	
	
	
	@PUT
	@Path("/employee")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse updateEmployee(EmployeeRequest employee){
		GenericResponse response = new GenericResponse();
		
		if ((employee.getOrganisation() !=  null) && 
			(employee.getEmployeeId() != null)){
			
			EmployeeHandler employeeHandler = new EmployeeHandler();
			response = employeeHandler.update(employee.mapToEmployee(),response);
		}else{
			response.setCode(400);
			response.setData("error","EmployeeId and Organisation is must");
		}
		return response;
	}
	
	@GET
	@Path("/{orgid}/employee")
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse getAllEmployees(@PathParam("orgid") String orgid){
		GenericResponse response = new GenericResponse();
		try{
			if (orgid != null){
				EmployeeHandler employeeHandler = new EmployeeHandler();
				response = employeeHandler.getAll(orgid,response);
			}else{
				response.setCode(400);
				response.setData("error","Organisation Id is missing");
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e);
			response.setCode(400);
			response.setData("error","Error while processing the request");
		}
		return response;
	}
	
	@GET
	@Path("/{org}/employee/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse getEmployeeById(
			@PathParam("org") String org,
			@PathParam("id") String id){
		GenericResponse response = new GenericResponse();
		
		try{
			EmployeeHandler employeeHandler = new EmployeeHandler();
			response = employeeHandler.getById(org,id,response);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
			response.setCode(400);
			response.setData("error","Error happened while retriving the Employee");
		}
		return response;
	}
	
	
	
	@GET
	@Path("{org}/employees")
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResponse getEmployeeBySkillsetsAndExperience(
			@PathParam("org") String organisation,
			@QueryParam("skillsets") String skillsets,
			@QueryParam("experience") int experience
			){
		GenericResponse response = new GenericResponse();
		EmployeeHandler employeeHandler = new EmployeeHandler();
		
		try{
			if ((skillsets != null) && (experience == 0)){
				System.out.println("Yet to implement it");
				response.setCode(400);
				response.setData("error","This functionality not yet implemented");
			}else if ((skillsets != null) && (experience != 0)){
				response = employeeHandler.getEmployeeBySkillsetsAndExperience(organisation,skillsets,experience,response);
			}
		}catch(Exception e){
			e.printStackTrace();
			response.setCode(400);
			response.setData("error", "Exception occured while retrieving employees");
		}
		
		return response;
	}
*/
	
}

