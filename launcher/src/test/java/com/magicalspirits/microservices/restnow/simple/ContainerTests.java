package com.magicalspirits.microservices.restnow.simple;

import static com.magicalspirits.microservices.restnow.core.ModuleNamedVariables.JETTY_PORT_SUPPLIER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Supplier;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.magicalspirits.microservices.restnow.launcher.Service;
import com.magicalspirits.microservices.restnow.launcher.SimpleModule;

public class ContainerTests 
{
	private Service main;
	private int port;

	@Before
	public void setup()
	{
		System.setProperty(SimpleModule.SYSTEM_PORT_PROPERTY, "0");
		main = new Service();
		main.init(new String[]{TestModule.class.getName()});
		main.start();
		port = main.getInjector().getInstance(Key.get(new TypeLiteral<Supplier<Integer>>(){}, Names.named(JETTY_PORT_SUPPLIER))).get();
	}

	@After 
	public void tearDown()
	{
		main.stop();
	}

	@Test
	public void testHappyPath() 
	{
		Client client = ClientBuilder.newBuilder().register(JacksonJsonProvider.class).build();
		WebTarget target = client.target("http://localhost:" + port + "/test/resource").queryParam("value", "aTestValue");
		Response response = target.request().get();
		TestObject value = response.readEntity(TestObject.class);
		response.close();

		assertEquals("aTestValue", value.getTestValue());
	}
	
	@Test
	public void testException() 
	{
		Client client = ClientBuilder.newBuilder().register(JacksonJsonProvider.class).build();
		WebTarget target = client.target("http://localhost:" + port + "/test/exception").queryParam("value", "aTestValue");
		Response response = target.request().get();
		assertEquals(500, response.getStatus());
		String value = response.readEntity(String.class);
		assertTrue(value.contains("java.lang.RuntimeException: expected"));
		response.close();
	}
	
	@Test
	public void testNotFound() 
	{
		Client client = ClientBuilder.newBuilder().register(JacksonJsonProvider.class).build();
		WebTarget target = client.target("http://localhost:" + port + "/test/nothing").queryParam("value", "aTestValue");
		Response response = target.request().get();
		assertEquals(404, response.getStatus());
		response.close();
	}
	
	@Test
	public void testMonitoring() 
	{
		Client client = ClientBuilder.newBuilder().register(JacksonJsonProvider.class).build();
		WebTarget target = client.target("http://localhost:" + port + "/monitoring/ping");
		Response response = target.request().get();
		String value = response.readEntity(String.class);
		response.close();

		assertEquals("pong", value.trim());
	}

	@Test
	public void testHealth() 
	{
		Client client = ClientBuilder.newBuilder().register(JacksonJsonProvider.class).build();
		WebTarget target = client.target("http://localhost:" + port + "/monitoring/healthcheck");
		Response response = target.request().get();
		String value = response.readEntity(String.class);
		response.close();

		assertEquals("{\"deadlocks\":{\"healthy\":true}}", value);
	}

	@Test
	public void testThreads() 
	{
		Client client = ClientBuilder.newBuilder().register(JacksonJsonProvider.class).build();
		WebTarget target = client.target("http://localhost:" + port + "/monitoring/threads");
		Response response = target.request().get();
		String value = response.readEntity(String.class);
		response.close();

		assertTrue(value.contains("main "));
	}
	
	@Test
	public void testMetrics() 
	{
		Client client = ClientBuilder.newBuilder().register(JacksonJsonProvider.class).build();
		WebTarget target = client.target("http://localhost:" + port + "/monitoring/metrics");
		Response response = target.request().get();
		String value = response.readEntity(String.class);
		response.close();

		assertTrue(value.contains("get-requests"));
	}
}
