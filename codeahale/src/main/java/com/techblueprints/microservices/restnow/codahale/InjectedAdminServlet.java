package com.techblueprints.microservices.restnow.codahale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.AdminServlet;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class InjectedAdminServlet extends AdminServlet 
{
	private static final long serialVersionUID = 6179559873306580966L;

	@Inject
	private HealthCheckRegistry hr;
	@Inject 
	private MetricRegistry mr;
	
    public void init(ServletConfig config) throws ServletException 
    {
    	//get ready to do something really silly
    	config.getServletContext().setAttribute(HealthCheckServlet.HEALTH_CHECK_REGISTRY, hr);    	
    	config.getServletContext().setAttribute(MetricsServlet.METRICS_REGISTRY, mr);
        super.init(config);
    }
}
