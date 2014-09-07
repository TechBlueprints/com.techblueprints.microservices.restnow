package com.techblueprints.microservices.restnow.simple;

import com.google.inject.AbstractModule;

public class TestModule extends AbstractModule
{
	@Override
	protected void configure() 
	{
		bind(JaxRSTestResource.class);
	}
}
