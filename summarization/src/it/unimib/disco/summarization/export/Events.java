package it.unimib.disco.summarization.export;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Events{
	
	static{
		PropertyConfigurator.configureAndWatch("log4j.properties");
	}
	
	public static Events summarization(){
		return new Events(Logger.getLogger("summarization"));
	}
	
	public static Events web(){
		return new Events(Logger.getLogger("web"));
	}
	
	private Logger logger;
	
	private Events(Logger logger){
		this.logger = logger;
	}
	
	public void error(Object message, Exception exception){
		logger.error(message, exception);
	}

	public void debug(Object message) {
		logger.debug(message);
	}

	public void info(Object message) {
		logger.info(message);
	}
}