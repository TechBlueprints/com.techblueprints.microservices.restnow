package com.techblueprints.microservices.restnow.resteasy;

import static com.techblueprints.microservices.restnow.core.ModuleNamedVariables.JETTY_DEFAULT_EVENT_LISTENER;
import static com.techblueprints.microservices.restnow.core.ModuleNamedVariables.JETTY_DEFAULT_SERVLET;
import static com.techblueprints.microservices.restnow.core.ModuleNamedVariables.JETTY_INITIALIZATION_MAP;

import java.util.EventListener;
import java.util.Map;

import javax.servlet.Servlet;

import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;


public class ResteasyBasicModule extends AbstractModule {

	@Override
	protected void configure() 
	{
		bind(EventListener.class).annotatedWith(Names.named(JETTY_DEFAULT_EVENT_LISTENER)).to(GuiceResteasyBootstrapServletContextListener.class);
	}

	@Provides
	@Named(JETTY_DEFAULT_SERVLET)
	public Servlet getDefaultServlet(HttpServletDispatcher defaultServlet)
	{
		return defaultServlet;
	}
	
	@Provides
	@Named(JETTY_INITIALIZATION_MAP)
	public Map<String, String> getInitializationMap()
	{
		Map<String, String> initMap = Maps.newHashMap();
		initMap.put("resteasy.role.based.security", "false");
		return initMap;
	}
}
