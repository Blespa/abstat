package it.unimib.disco.summarization.ontology;

public class TypeOf{
	
	private String domain;

	public TypeOf(String domain) {
		this.domain = domain;
	}

	public String resource(String resource) {
		String aaa = resource.toLowerCase();
		String type = "";
		if ((aaa.contains("wikidata")) && (aaa.contains(domain)))
		{
			type = "external";
		}
		else
		{
			if ((!(aaa.contains("wikidata"))) && (aaa.contains(domain)))
			{
				type = "internal";
			}
			else
			{
				if (!(aaa.contains(domain)))
				{
					type = "external";
				}
			}
		}
		return type;
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