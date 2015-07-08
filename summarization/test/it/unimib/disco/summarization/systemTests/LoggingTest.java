package it.unimib.disco.summarization.systemTests;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.output.Events;

import java.io.File;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class LoggingTest {
	
	@Test
	public void reverseProxyShouldLogAllTheRequests() throws Exception {
		
		new Events();
		
		double random = new Random().nextDouble();
		
		new ClientCommunication("http://localhost").httpGet("/?test=" + random);
		
		assertThat(FileUtils.readLines(new File("../data/logs/reverse-proxy/access.log")), hasItem(containsString(random + "")));
	}
	
	@Test
	public void sessionsShouldBeLogged() throws Exception {
		
	}
}
