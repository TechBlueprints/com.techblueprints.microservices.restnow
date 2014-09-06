package com.magicalspirits.microservices.restnow.codahale;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class CodahaleExecutorModule extends AbstractModule {

	@Override
	protected void configure() 
	{
		bind(Executor.class).to(ExecutorService.class);
	}
	
	@Provides
	@Singleton
	public ExecutorService getExecutorService(UncaughtExceptionHandler uncaughtExceptionHandler, MetricRegistry registry)
	{
		//we're going to provide this using the codahale module so that we can instrument it. 
		//this is unbounded, so you might want to provide your own implementation.
		ExecutorService es = Executors.newCachedThreadPool(
				new ThreadFactoryBuilder().setNameFormat("instrumented-%d").setDaemon(true)
					.setUncaughtExceptionHandler(uncaughtExceptionHandler).build());
		MoreExecutors.addDelayedShutdownHook(es, 30, TimeUnit.SECONDS);
		return new InstrumentedExecutorService(es, registry);
	}
	
	@Provides
	@Singleton
	public ListeningExecutorService getListeningExecutorService(ExecutorService exec)
	{
		return MoreExecutors.listeningDecorator(exec);
	}
}
