package it.unimib.disco.summarization.systemTests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.InetAddress;

import org.junit.Test;

public class HostsTest {
	
	@Test
	public void backendIsReachable() throws Exception {
		
		assertThat(InetAddress.getByName("backend").getHostAddress(), equalTo("193.204.59.21"));
	}
	
	@Test
	public void frontendIsReachable() throws Exception {
		
		assertThat(InetAddress.getByName("frontend").getHostAddress(), equalTo("10.109.149.57"));
	}
}
