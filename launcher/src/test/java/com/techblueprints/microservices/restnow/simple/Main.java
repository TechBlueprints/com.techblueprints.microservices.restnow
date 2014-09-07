package com.techblueprints.microservices.restnow.simple;

import java.util.Scanner;

import lombok.Cleanup;

import org.eclipse.jetty.server.Server;

import com.google.common.base.Strings;
import com.techblueprints.microservices.restnow.launcher.Service;

public class Main 
{
	public static void main(String[] args) throws InterruptedException
	{
		if(Strings.isNullOrEmpty(System.getProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY)))
			System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
		
		Service main = new Service();
		main.init(args);
		main.start();
		
		@Cleanup Scanner s = new Scanner(System.in);
		while(true)
		{
			System.out.println("Type exit and press enter to end process");
			if(s.hasNext() && "exit".equalsIgnoreCase(s.next()))
			{
				Server server = main.getInjector().getInstance(Server.class);
				main.stop();
				server.join();
				System.exit(0);
			}
		}
	}
}
