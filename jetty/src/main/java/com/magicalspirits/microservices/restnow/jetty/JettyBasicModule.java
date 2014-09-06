package com.magicalspirits.microservices.restnow.jetty;

import static com.magicalspirits.microservices.restnow.core.ModuleNamedVariables.JETTY_DEFAULT_EVENT_LISTENER;
import static com.magicalspirits.microservices.restnow.core.ModuleNamedVariables.JETTY_DEFAULT_FILTER;
import static com.magicalspirits.microservices.restnow.core.ModuleNamedVariables.JETTY_DEFAULT_SERVLET;
import static com.magicalspirits.microservices.restnow.core.ModuleNamedVariables.JETTY_INITIALIZATION_MAP;
import static com.magicalspirits.microservices.restnow.core.ModuleNamedVariables.JETTY_MONITORING_SERVLET;
import static com.magicalspirits.microservices.restnow.core.ModuleNamedVariables.JETTY_PORT_SUPPLIER;

import java.util.EventListener;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class JettyBasicModule extends AbstractModule
{
	@Override
	protected void configure() 
	{
		bind(JettyService.class).asEagerSingleton();
	}
	
	@Provides 
	@Singleton
	public ServletContextHandler getServletContextHandlerAndInitializeServer(Injector i, @Named(JETTY_DEFAULT_SERVLET) Servlet servlet, 
			@Named(JETTY_INITIALIZATION_MAP) Map<String, String> initializationMap,
			Server server, ErrorHandler eh)
	{
        ServletContextHandler servletHandler = new ServletContextHandler();
        servletHandler.setContextPath("/");
	    Key<EventListener> eventListenerKey = Key.get(EventListener.class, Names.named(JETTY_DEFAULT_EVENT_LISTENER));
	    
	    //Much to my consternation, Guice doesn't support optional bindings on provides methods, so we have to do a tap dance to see if something is bound.
	    if(i.getExistingBinding(eventListenerKey) != null)
	    	servletHandler.addEventListener(i.getInstance(eventListenerKey));

	    Key<Filter> filterKey= Key.get(Filter.class, Names.named(JETTY_DEFAULT_FILTER));
	    if(i.getExistingBinding(filterKey) != null)
	    	servletHandler.addFilter(new FilterHolder(i.getInstance(filterKey)), "/*", null);
	    
        ServletHolder sh = new ServletHolder(servlet);

        for(Map.Entry<String, String> entry : initializationMap.entrySet())
        	servletHandler.setInitParameter(entry.getKey(), entry.getValue());
        servletHandler.addServlet(sh, "/*");
 
	    Key<Servlet> monitoringServletKey = Key.get(Servlet.class, Names.named(JETTY_MONITORING_SERVLET));
	    if(i.getExistingBinding(monitoringServletKey) != null)
	    	servletHandler.addServlet(new ServletHolder(i.getInstance(monitoringServletKey)), "/monitoring/*");
	    
	    servletHandler.setErrorHandler(eh);
	    
	    return servletHandler;
	}
	
	@Provides
	@Singleton
	public Server getServer(QueuedThreadPool threadPool)
	{
		//TODO: What if you wanted to listen on more than one address, but not use 0.0.0.0?
		Server server = new Server(threadPool);
		return server;
	}
	
	@Provides
	@Singleton
	public HttpConnectionFactory getHttpConnectionFactory()
	{
		HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setOutputBufferSize(4096);
		httpConfig.setSendServerVersion(false);
		return new HttpConnectionFactory(httpConfig);
	}
	
	@Provides
	@Named(JETTY_PORT_SUPPLIER)
	@Singleton
	public Supplier<Integer> getJettyPort(final Server server) //using google supplier so java 8 isn't required
	{
		//good spot for a lambda, but I don't want to require java 8
		return new Supplier<Integer>() 
		{
			@Override
			public Integer get() 
			{
				return ((ServerConnector)server.getConnectors()[0]).getLocalPort();
			}
		};
	}
	
	@Provides
	@Singleton
	public ErrorHandler getErrorHandler()
	{
        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setShowStacks(true);
        return errorHandler;
	}
}