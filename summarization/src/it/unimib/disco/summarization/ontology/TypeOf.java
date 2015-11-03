package it.unimib.disco.summarization.ontology;

public class TypeOf{
	
	private String domain;

	public TypeOf(String domain) {
		this.domain = domain;
	}

	public String resource(String resource) {
		String typeOfConcept = "";
		if ((resource.contains("wikidata")) && (resource.contains(domain)))
		{
			typeOfConcept = "external";
		}
		else
		{
			if ((!(resource.contains("wikidata"))) && (resource.contains(domain)))
			{
				typeOfConcept = "internal";
			}
			else
			{
				if (!(resource.contains(domain)))
				{
					typeOfConcept = "external";
				}
			}
		}
		return typeOfConcept;
	}
	
	public String objectAKP(String subject, String object) {
		if (isInternal(subject) && isInternal(object))
		{
			return "internal";
		}
		return "external";
	}
	
	public String datatypeAKP(String subject) {
		return resource(subject);
	}

	private boolean isInternal(String subject) {
		return resource(subject).equals("internal");
	}
}