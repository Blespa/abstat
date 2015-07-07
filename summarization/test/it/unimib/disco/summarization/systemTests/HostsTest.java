package it.unimib.disco.summarization.systemTests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

public class HostsTest {
	
	@Test
	public void backendIsReachable() throws Exception {
		
		assertThat(addressOf("backend"), equalTo("193.204.59.21"));
	}

	@Test
	public void frontendIsReachable() throws Exception {
		
		assertThat(addressOf("frontend"), equalTo("10.109.149.57"));
	}
	
	private String addressOf(String host) throws UnknownHostException {
		return InetAddress.getByName(host).getHostAddress();
	}
}
