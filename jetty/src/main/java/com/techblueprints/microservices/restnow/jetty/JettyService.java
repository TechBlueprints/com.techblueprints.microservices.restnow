package com.techblueprints.microservices.restnow.jetty;

import static com.techblueprints.microservices.restnow.core.ModuleNamedVariables.JETTY_LISTEN_ADDR;

import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Slf4j
@Singleton
public class JettyService 
{
	@Inject
	private Server server;
	
	@Inject
	private ServerConnector connector;
	
	@Inject
	private @Named(JETTY_LISTEN_ADDR) InetSocketAddress address;
	
	@Inject
	private ServletContextHandler handler;
	
	@Inject
	private HandlerWrapper hw;
	
	@PostConstruct
	public void setup() throws Exception
	{
		connector.setHost(address.getHostString());
		connector.setPort(address.getPort());
		connector.setIdleTimeout(20000); //TODO: should this come in as a bound variable? as a system property?
		connector.setSoLingerTime(-1);
		connector.setName("restnow");
		connector.setAcceptQueueSize(200); //TODO: Same here?
		server.setConnectors(new ServerConnector[]{connector});
		hw.setHandler(handler);
		server.setHandler(hw);

		server.start();
	}
	
	@PreDestroy
	public void shutdown()
	{
		try 
		{
			server.stop();
		} 
		catch (Exception e) 
		{
			log.error("Unable to stop", e);
		}
	}

}
