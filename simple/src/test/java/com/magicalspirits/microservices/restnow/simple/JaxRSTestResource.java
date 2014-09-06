package com.magicalspirits.microservices.restnow.simple;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/test")
public class JaxRSTestResource
{
	@GET
	@Path("/resource")
	@Produces(MediaType.APPLICATION_JSON)
	public TestObject getJsonTestResource(@QueryParam("value") String value) 
	{
		return new TestObject(value);
	}

	@GET
	@Path("/exception")
	@Produces(MediaType.APPLICATION_JSON)
	public TestObject getJsonTestResource() 
	{
		throw new RuntimeException("expected");
	}
}