package com.magicalspirits.microservices.restnow.core;

import java.lang.Thread.UncaughtExceptionHandler;

import com.google.inject.AbstractModule;

public class LoggingUncaughtExceptionHandlerModule extends AbstractModule 
{
	@Override
	protected void configure() 
	{
		bind(UncaughtExceptionHandler.class).to(LoggingUncaughtExceptionHandler.class).asEagerSingleton();
	}
}
