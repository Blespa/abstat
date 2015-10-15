package it.unimib.disco.summarization.test.system;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.export.Events;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.http.cookie.Cookie;
import org.junit.Test;

public class LoggingTest {
	
	@Test
	public void reverseProxyShouldLogAllTheRequests() throws Exception {
		
		Events.web();
		
		double random = new Random().nextDouble();
		
		new ClientCommunication("http://localhost").httpGet("/?test=" + random);
		
		assertThat(accessLog(), hasItem(containsString(random + "")));
	}

	@Test
	public void sessionsShouldBeLogged() throws Exception {
		
		ClientCommunication browser = new ClientCommunication("http://localhost");
		browser.getCookie("/", "JSESSIONID");
		
		Cookie session = browser.getCookie("/", "JSESSIONID");
		
		assertThat(accessLog(), hasItem(containsString(session.getValue())));
	}
	
	private List<String> accessLog() throws IOException {
		return FileUtils.readLines(new File("../data/logs/reverse-proxy/access.log"));
	}
}
