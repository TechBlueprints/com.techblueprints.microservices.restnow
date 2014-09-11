package com.techblueprints.microservices.restnow.codahale;

import java.lang.management.ManagementFactory;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

@Slf4j
public class CodahaleModule extends AbstractModule 
{
	public static final String METRIC_PREFIX = "metric-prefix";
	
	private static MetricRegistry registry;
	
	@Override
	protected void configure() 
	{
	}

	@Provides
	@Singleton
	public MetricRegistry getMetricRegistry()
	{
		return CodahaleModule.getStaticMetricRegistry();
	}
	
	/**
	 * You shouldn't call this directly. This utility method will dissapear in a future release.
	 * Presently I need it because the guice-annotations module doesn't expose the registry in a 
	 * usable way, and the MetricRegistry has to be obtained at construction time, not at injection time.
	 */
	synchronized static MetricRegistry getStaticMetricRegistry()
	{
		if(registry != null)
			return registry;

		registry = new MetricRegistry();

		//a bunch of the following code I learned from 
		// https://github.com/cloudera/cdk/blob/master/cdk-morphlines/cdk-morphlines-metrics-servlets/src/main/java/com/cloudera/cdk/morphline/metrics/servlets/RegisterJVMMetricsBuilder.java
		BufferPoolMetricSet bpms = new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer());
		registerAll("jvm.buffers", bpms, registry);
		registerAll("jvm.gc", new GarbageCollectorMetricSet(), registry);
		registerAll("jvm.memory", new MemoryUsageGaugeSet(), registry);
		registerAll("jvm.threads", new ThreadStatesGaugeSet(), registry);
		register("jvm.fileDescriptorCountRatio", new FileDescriptorRatioGauge(), registry);

		registry.registerAll(new JvmAttributeGaugeSet());

		return registry;
	}
	
	private static void registerAll(String prefix, MetricSet ms, MetricRegistry mr) 
	{
		for (Map.Entry<String, Metric> entry : ms.getMetrics().entrySet()) 
		{
			String name = MetricRegistry.name(prefix, entry.getKey());
			
			if (entry.getValue() instanceof MetricSet) 
			{
				registerAll(name, (MetricSet) entry.getValue(), mr);
			} 
			else 
			{
				register(name, entry.getValue(), mr);
			}
		} 
	}

	private static void register(String name, Metric m, MetricRegistry mr) 
	{
		if (!mr.getMetrics().containsKey(name)) 
		{ 
			try 
			{
				mr.register(name, m);
			} 
			catch (IllegalArgumentException e) 
			{
				log.warn("Unable to add metric {}", name, e);
			}
		}
	}

	@Provides
	@Singleton
	public HealthCheckRegistry getHealthCheckRegistry()
	{
		HealthCheckRegistry hr = new HealthCheckRegistry();
		//only add deadlock checker here
		hr.register("deadlocks", new ThreadDeadlockHealthCheck());
		return hr;
	}
}
