package com.magicalspirits.microservices.restnow.core;

import java.lang.Thread.UncaughtExceptionHandler;

import com.google.inject.Singleton;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class LoggingUncaughtExceptionHandler implements UncaughtExceptionHandler 
{
	@Override
	public void uncaughtException(Thread t, Throwable e) 
	{
		log.error("Thread {} encountered uncaught exception", t, e);
	}

}
