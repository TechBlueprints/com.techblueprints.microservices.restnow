package com.magicalspirits.microservices.restnow.simple;

import static com.magicalspirits.microservices.restnow.core.ModuleNamedVariables.JETTY_PORT_SUPPLIER;
import static org.junit.Assert.assertFalse;
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
import com.magicalspirits.microservices.restnow.codahale.CodahaleModule;
import com.magicalspirits.microservices.restnow.launcher.Service;
import com.magicalspirits.microservices.restnow.launcher.SimpleModule;

public class MetricPrefixTest {
	private Service main;
	private int port;

	@Before
	public void setup()
	{
		System.setProperty(SimpleModule.SYSTEM_PORT_PROPERTY, "0");
		System.setProperty(CodahaleModule.METRIC_PREFIX, "aprefix");
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
	public void testMetrics() 
	{
		Client client = ClientBuilder.newBuilder().register(JacksonJsonProvider.class).build();
		WebTarget target = client.target("http://localhost:" + port + "/monitoring/metrics");
		Response response = target.request().get();
		String value = response.readEntity(String.class);
		response.close();

		assertTrue(value.contains("aprefix."));
		assertFalse(value.contains("aprefix.aprefix"));
	}
}
