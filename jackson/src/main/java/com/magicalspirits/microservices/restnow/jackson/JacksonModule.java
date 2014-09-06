package com.magicalspirits.microservices.restnow.jackson;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.google.inject.AbstractModule;

public class JacksonModule extends AbstractModule {

	@Override
	protected void configure() 
	{
		bind(JacksonJsonProvider.class);
	}
}
