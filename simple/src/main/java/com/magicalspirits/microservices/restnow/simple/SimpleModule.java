package com.magicalspirits.microservices.restnow.simple;

import static com.magicalspirits.microservices.restnow.core.ModuleNamedVariables.JETTY_LISTEN_ADDR;

import java.net.InetSocketAddress;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

public class SimpleModule extends AbstractModule {

	public static final String SYSTEM_PORT_PROPERTY = "system-jetty-port";
	
	@Override
	protected void configure() 
	{

	}
	@Provides
	@Named(JETTY_LISTEN_ADDR)
	public InetSocketAddress getListenAddress()
	{
		int iSystemPort;
		String sSystemPort = System.getProperty(SYSTEM_PORT_PROPERTY);
		if(Strings.isNullOrEmpty(sSystemPort))
			iSystemPort = 8080;
		else
			iSystemPort = Integer.parseInt(sSystemPort);
		
		return new InetSocketAddress(iSystemPort);
	}
}
