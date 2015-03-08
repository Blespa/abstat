package it.unimib.disco.summarization.web;

public class WebApplication {

	public static void main(String[] args) throws Exception {
		try{
			new SummarizationInspection().on(Integer.parseInt(args[0])).start();
		}
		catch(Exception e){
			new Events().error("application didn't start", e);
			System.exit(1);
		}
	}
}
