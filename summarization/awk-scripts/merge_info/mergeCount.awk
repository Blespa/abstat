BEGIN {
	dataForCompDir=dataForCompDirectory;
	fileForComp=fileForComputation;
	destDir=destinatioDirectory;
	destFile=destinationFile;

	totRes=0;
	while (getline < (dataForCompDir"/"fileForComp))
	{
		if(match($0,"^##")){
			gsub("##","",$0);
			totRes=$0;
		}
	}
}

{ 
	split("", Prop);
	split($0,Prop,"##");
	
	if(Prop[1] in CountProp)
		CountProp[Prop[1]]=CountProp[Prop[1]]+Prop[2];
	else
		CountProp[Prop[1]]=Prop[2];
}

END { 
	
	system("touch "destDir"/"destFile); #Creo il file così da averlo, anche se vuoto

	print "Property##How Common is (%)##Number of resources (that have|are object of) the property" >> destDir"/"destFile; 

	for (prop in CountProp)
	{
		percentuale = (CountProp[prop]/totRes)*100;

		print prop "##" percentuale "##" CountProp[prop] >> destDir"/"destFile;
	}

}

