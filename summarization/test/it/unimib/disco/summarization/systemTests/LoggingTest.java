package it.unimib.disco.summarization.systemTests;

import it.unimib.disco.summarization.output.Events;

import java.util.Random;

import org.junit.Test;

public class LoggingTest {
	
	@Test
	public void reverseProxyShouldLogAllTheRequests() throws Exception {
		
		new Events();
		
		Http.responseFrom("http://localhost?test=" + new Random().nextDouble());
	}
}
