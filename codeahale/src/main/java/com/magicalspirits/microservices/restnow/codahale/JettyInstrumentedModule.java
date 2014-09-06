package com.magicalspirits.microservices.restnow.codahale;

import static com.magicalspirits.microservices.restnow.core.ModuleNamedVariables.JETTY_LISTEN_ADDR;
import static com.magicalspirits.microservices.restnow.core.ModuleNamedVariables.JETTY_MONITORING_SERVLET;

import java.net.InetSocketAddress;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedConnectionFactory;
import com.codahale.metrics.jetty9.InstrumentedHandler;
import com.codahale.metrics.jetty9.InstrumentedQueuedThreadPool;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

public class JettyInstrumentedModule extends AbstractModule 
{
	@Override
	protected void configure() 
	{
		bind(InjectedAdminServlet.class);
	}

	@Provides
	@Named(JETTY_MONITORING_SERVLET)
	@Singleton
	public Servlet getAdminServlet(InjectedAdminServlet servlet)
	{
		//make sure hr and mr are pre-initialized.
		return servlet;
	}

	@Provides
	@Singleton
	public QueuedThreadPool getQueuedThreadPool(MetricRegistry registry)
	{
		InstrumentedQueuedThreadPool rv = new InstrumentedQueuedThreadPool(registry);
		rv.setName("jetty-thread-pool");
		rv.setDaemon(true);
		rv.setStopTimeout(30000);
		return rv;
	}
	
	@Provides
	@Singleton
	public HandlerWrapper getHandlerWrapper(MetricRegistry registry)
	{
		return new InstrumentedHandler(registry);
	}
	
	@Provides
	@Singleton
	public ServerConnector getServerConnector(MetricRegistry registry, Server server, @Named(JETTY_LISTEN_ADDR) InetSocketAddress address)
	{
		return new InstrumentedServerConnector("jetty-" + address.getHostString(), address.getPort(), server, registry);
	}
	
	@Provides
	@Singleton
	public ConnectionFactory getConnectionFactory(MetricRegistry registry, HttpConnectionFactory toWrap)
	{
		return new InstrumentedConnectionFactory(toWrap, registry.timer("jetty-connection-factory"));
	}
}
