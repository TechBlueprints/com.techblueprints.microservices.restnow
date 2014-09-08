com.techblueprints.microservices.restnow
========================================

Standing up Rest services should be as simple. This project has been created to make it simple.

RestNow is a container that provides you with a very easy framework for standing up micro (or larger) rest services.


This container provides you with a JaxRS 2.0 framework, a Servlet 3.1 container, Guice dependency injection, JSR-250 (@PostConstruct, @PreDestroy), codahale metrics, and codahale health checks for monitoring.


Quick Start
-----------

This quick start should have you a functional rest service in 10 minutes or less.

1) Add this dependency to your Maven project:

```
  	<dependency>
  		<groupId>com.techblueprints.microservices.restnow</groupId>
  		<artifactId>launcher</artifactId>
  		<version>0.0.1-SNAPSHOT</version>
  	</dependency>
```

2) Make a rest service:

Here is a brief example:

ResourceEndpoint.java
```
@Slf4j
@Path("/resource")
public class ResourceEndpoint
{
	@Inject 
	private Executor executor;

	@GET
	@Path("/sample")
	@Produces(MediaType.APPLICATION_JSON)
	public SampleResource getSampleResource(@QueryParam("value") String value) 
	{
		return new SampleResource(value);
	}
}
```

SampleResource.java
```
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SampleResource
{
	private String testValue;
}
```
3) Bind your jaxrs endpoint in a Guice module:
```
public class SimpleModule extends AbstractModule
{
	@Override
	protected void configure() 
	{
		bind(ResourceEndpoint.class);
	}
}
```
4) Execute the launcher:

com.techblueprints.microservices.restnow.launcher.Service is an apache jsvc capable launcher.
Unless you are configured to run jsvc, you will want to create a run configuration to run the Main in the test scope:

com.techblueprints.microservices.restnow.launcher.Main SimpleModule

Note: You will need to pass your module name(s) as arguments to the Main launcher and the jsvc launcher.

5) Enjoy!

Open up a browser to http://localhost:8080/resource/sample?value=test-value and note the result.

Also, browse to http://localhost:8080/monitoring and note the metrics and healthchecks available.

You can add to these by injecting the MetricRegistry or the HealthCheckRegistry and adding to them.


Additional examples of how create rest endpoints and execute calls asynchronously, can be found in the test scope of the launcher package. 

Additional documentation, sub module documentation, system properties, and Maven Central published artifacts are coming soon.



Apache Software License 2.0:
-----------------------------------------------------------------------------
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
