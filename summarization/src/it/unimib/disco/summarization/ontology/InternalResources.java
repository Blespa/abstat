package it.unimib.disco.summarization.ontology;

public class InternalResources{
	
	private String domain;

	public InternalResources(String domain) {
		this.domain = domain;
	}

	public String typeOf(String resource) {
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
}