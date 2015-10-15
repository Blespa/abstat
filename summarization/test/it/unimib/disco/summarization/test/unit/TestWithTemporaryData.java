package it.unimib.disco.summarization.test.unit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class TestWithTemporaryData{
	
	public TemporaryFolder temporary;
	
	@Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	@Before
	public void createTemporaryFolder(){
		temporary = new TemporaryFolder();
		temporary.create();
	}
	
	@After
	public void deleteTemporaryFolder(){
		temporary.delete();
	}
}