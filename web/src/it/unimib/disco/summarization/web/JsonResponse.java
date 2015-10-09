package it.unimib.disco.summarization.web;



public class JsonResponse implements Response {

	private Api api;

	public JsonResponse(Api api) {
		this.api = api;
	}

	@Override
	public void sendResponse(Communication communication) throws Exception {
		communication.setContentType("application/json");
		communication.setOutputStream(this.api.getAutocomplete(communication));
		communication.setHandled();
	}

}
