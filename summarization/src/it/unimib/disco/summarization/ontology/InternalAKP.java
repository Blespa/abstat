package it.unimib.disco.summarization.ontology;

public class InternalAKP{
	
	private String domain;

	public InternalAKP(String domain) {
		this.domain = domain;
	}

	public String typeOf(String subject, String object) {
		String type;
		if (((!(subject.contains("wikidata"))) && (subject.contains(domain))) && ((!(object.contains("wikidata"))) && (object.contains(domain))))
		{
			type = "internal";
		}
		else
		{
			type = "external";
		}
		return type;
	}
}