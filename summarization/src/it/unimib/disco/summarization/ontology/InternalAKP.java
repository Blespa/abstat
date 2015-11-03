package it.unimib.disco.summarization.ontology;

public class InternalAKP{
	
	private InternalResources domain;

	public InternalAKP(String domain) {
		this.domain = new InternalResources(domain);
	}

	public String typeOf(String subject, String object) {
		if (isInternal(subject) && isInternal(object))
		{
			return "internal";
		}
		return "external";
	}

	private boolean isInternal(String subject) {
		return domain.typeOf(subject).equals("internal");
	}
}