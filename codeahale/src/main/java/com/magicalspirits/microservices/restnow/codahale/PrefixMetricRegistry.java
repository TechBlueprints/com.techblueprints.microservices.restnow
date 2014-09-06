package com.magicalspirits.microservices.restnow.codahale;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;

@AllArgsConstructor
public class PrefixMetricRegistry extends MetricRegistry 
{
	@NonNull
	private String prefix;
	
	@Override
    public <T extends Metric> T register(String name, T metric) throws IllegalArgumentException 
    {
    	return super.register(MetricRegistry.name(prefix, name), metric);
    }
	
	@Override
    public void registerAll(MetricSet metrics) throws IllegalArgumentException 
	{
		registerAll(prefix, metrics);
    }
	
	private void registerAll(String passedPrefix, MetricSet ms) 
	{
		for (Map.Entry<String, Metric> entry : ms.getMetrics().entrySet()) 
		{
			String name = MetricRegistry.name(passedPrefix, entry.getKey());
			
			if (entry.getValue() instanceof MetricSet) 
			{
				registerAll(MetricRegistry.name(passedPrefix, entry.getKey()), (MetricSet) entry.getValue());
			} 
			else 
			{
				super.register(name, entry.getValue());
			}
		} 
	}
}
