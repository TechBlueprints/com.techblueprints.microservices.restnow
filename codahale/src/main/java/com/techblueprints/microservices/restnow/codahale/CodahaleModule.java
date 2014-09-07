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
import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

@Slf4j
public class CodahaleModule extends AbstractModule 
{
	public static final String METRIC_PREFIX = "metric-prefix";
	
	@Override
	protected void configure() 
	{
	}

	@Provides
	@Singleton
	public MetricRegistry getMetricRegistry()
	{
		String metricPrefix = System.getProperty(METRIC_PREFIX);
		MetricRegistry mr;
		if(Strings.isNullOrEmpty(metricPrefix))
		{
			mr = new MetricRegistry();
		}
		else
		{
			mr = new PrefixMetricRegistry(metricPrefix);
		}
			
		//a bunch of the following code I learned from 
		// https://github.com/cloudera/cdk/blob/master/cdk-morphlines/cdk-morphlines-metrics-servlets/src/main/java/com/cloudera/cdk/morphline/metrics/servlets/RegisterJVMMetricsBuilder.java
		BufferPoolMetricSet bpms = new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer());
		registerAll("jvm.buffers", bpms, mr);
		registerAll("jvm.gc", new GarbageCollectorMetricSet(), mr);
		registerAll("jvm.memory", new MemoryUsageGaugeSet(), mr);
		registerAll("jvm.threads", new ThreadStatesGaugeSet(), mr);
		register("jvm.fileDescriptorCountRatio", new FileDescriptorRatioGauge(), mr);
		mr.registerAll(new JvmAttributeGaugeSet());
		return mr;
	}
	
	private void registerAll(String prefix, MetricSet ms, MetricRegistry mr) 
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

	private void register(String name, Metric m, MetricRegistry mr) 
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
