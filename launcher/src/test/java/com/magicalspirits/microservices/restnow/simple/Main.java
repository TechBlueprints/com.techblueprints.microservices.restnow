package com.magicalspirits.microservices.restnow.simple;

import java.util.Scanner;

import lombok.Cleanup;

import org.eclipse.jetty.server.Server;

import com.magicalspirits.microservices.restnow.launcher.Service;

public class Main 
{
	public static void main(String[] args) throws InterruptedException
	{
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
