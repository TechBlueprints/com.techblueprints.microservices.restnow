package com.techblueprints.microservices.restnow.simple;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;

import com.google.inject.Inject;

@Slf4j
@Path("/test")
public class JaxRSTestResource
{
	@Inject 
	private Executor executor;

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

	@GET
	@Path("/asyncresource")
	@Produces(MediaType.APPLICATION_JSON)
	public void getAsyncJsonResource(@Suspended final AsyncResponse response, @QueryParam("value") final String value)
	{
		response.setTimeout(3, TimeUnit.SECONDS);
		//another good spot for a lambda. Not using it here to keep it working with java 1.7 and 1.6
		Runnable r = new Runnable()
		{
			@Override
			public void run() 
			{
				if (!response.isSuspended()) 
				{
					log.debug("AsyncResponse not suspended. Canceled: {} Done: {}", response.isCancelled(), response.isDone());
					return;
				}

				boolean resumed = response.resume(Response.ok(new TestObject(value)).build());
				if (!resumed)
				{
					log.debug("AsyncResponse not resumed");
				}
			}
		};
		executor.execute(r);
	}
}