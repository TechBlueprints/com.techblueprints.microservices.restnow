package com.techblueprints.microservices.restnow.codahale;

import com.palominolabs.metrics.guice.MetricsInstrumentationModule;

public class CodahaleAnnotationsModule  extends MetricsInstrumentationModule 
{
	public CodahaleAnnotationsModule() 
	{
		super(CodahaleModule.getStaticMetricRegistry());
		//The MetricsInstrumentationModule stores the registry in a private
		// variable, and since it has to be passed in the constructor, I can't get a reference to it to return as 
		// a binding.
		// There should only be one of these app wide, so I am storing it in a static variable and using it in the provides
		// I've submitted a pull request against the current maintainer of metrics-guice to adapt it.
	}
}
