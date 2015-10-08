package it.unimib.disco.summarization.web;



public class JsonResponse implements Response {

	private String path;

	public JsonResponse(String path) {
		this.path = path;
	}

	@Override
	public void sendResponse(Communication communication) throws Exception {
		communication.setContentType("application/json");
		communication.setOutputStream(communication.getAutocomplete(this.path));
		communication.setHandled();
	}

}
