package com.techblueprints.microservices.restnow.launcher;

import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.mycila.guice.ext.closeable.CloseableInjector;
import com.mycila.guice.ext.closeable.CloseableModule;
import com.mycila.guice.ext.jsr250.Jsr250Module;
import com.techblueprints.microservices.restnow.codahale.CodahaleAnnotationsModule;
import com.techblueprints.microservices.restnow.codahale.CodahaleExecutorModule;
import com.techblueprints.microservices.restnow.codahale.CodahaleModule;
import com.techblueprints.microservices.restnow.codahale.JettyInstrumentedModule;
import com.techblueprints.microservices.restnow.core.LoggingUncaughtExceptionHandlerModule;
import com.techblueprints.microservices.restnow.jackson.JacksonModule;
import com.techblueprints.microservices.restnow.jetty.JettyBasicModule;
import com.techblueprints.microservices.restnow.resteasy.ResteasyBasicModule;

@Slf4j
/**
 * This is a jsvc capable service. 
 */
public class Service 
{
	/**
	 * Defaults to false;
	 */
	public static final String SPECIFY_ALL_MODULES = "specify-all-modules";
	
	@Getter
	private CloseableInjector injector;
	
	private List<Module> modules = Lists.newArrayList();
	
	public void init(String[] arguments)
	{
		if(Strings.isNullOrEmpty(System.getProperty(SPECIFY_ALL_MODULES)) || !Boolean.valueOf(System.getProperty(SPECIFY_ALL_MODULES)))
		{	
			modules.add(new CloseableModule());
			modules.add(new Jsr250Module());
			
			modules.add(new LauncherBasicModule());
			modules.add(new CodahaleExecutorModule());
			modules.add(new CodahaleModule());
			modules.add(new CodahaleAnnotationsModule());
			modules.add(new JettyInstrumentedModule());
			modules.add(new JacksonModule());
			modules.add(new JettyBasicModule());
			modules.add(new ResteasyBasicModule());
			modules.add(new LoggingUncaughtExceptionHandlerModule());
		}
	
		for(String arg : arguments)
		{
			if(Strings.isNullOrEmpty(arg))
				continue;
			Module m;
			try 
			{
				m = (Module)Class.forName(arg).newInstance();
			} 
			catch (Exception e)
			{
				log.warn("Unable to load module {}", arg, e);
				continue;
			}
			modules.add(m);
		}
	}
	
	public void start()
	{
		Injector i = Guice.createInjector(modules);
		i.getAllBindings();
		i.createChildInjector().getAllBindings();
		this.injector = i.getInstance(CloseableInjector.class);
	}
	
	public void stop()
	{
		injector.close();
	}
	
	public void destroy()
	{
		injector = null;
		modules.clear();
	}
}
