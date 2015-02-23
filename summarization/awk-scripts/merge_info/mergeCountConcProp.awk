BEGIN {
	dataForCompDir=dataForCompDirectory;
	fileForComp=fileForComputation;
	destDir=destinatioDirectory;
	destFile=destinationFile;

	totConcept=0; #Conto il numero di concetti
	while (getline < (dataForCompDir"/"fileForComp))
	{
		if(!match($0,"^##")){
			totConcept++;
		}
	}

}

{ 
	split("", concProp);
	numClassess=split($0,concProp,"##");
	
	if(concProp[1] in CountConcProp){
		
		#Analizzo le classi associate alla proprietà e determino eventuali nuove classi
		newClasses="";
		for(iNumCl=2; iNumCl<=numClassess; iNumCl++){
			
			#Verifico se ho già associato il concetto alla proprietà (in ogni file, per ogni risorsa, una classe compare al più una volta)
			regex = "(^"concProp[iNumCl]"$)|(^"concProp[iNumCl]"##)|(##"concProp[iNumCl]"##)|(##"concProp[iNumCl]"$)";
						 
			if(!match(CountConcProp[concProp[1]], regex)){
				newClasses=newClasses"##"concProp[iNumCl];
			}

		}
		
		#Aggiorno le classi associate alla proprietà
		CountConcProp[concProp[1]]=CountConcProp[concProp[1]]""newClasses;
		
	}
	else{
		
		classes="";
		for(iNumCl=2; iNumCl<=numClassess; iNumCl++){
			if(iNumCl==2)
				classes=concProp[iNumCl];
			else
				classes=classes"##"concProp[iNumCl];
		}
		
		CountConcProp[concProp[1]]=classes;
	}
}

END { 
	
	system("touch "destDir"/"destFile); #Creo il file così da averlo, anche se vuoto

	print "Property##Number of Minimum Type Concept (subject|object) of the property##% respect to the total number of Concepts" >> destDir"/"destFile;

	for (Concprop in CountConcProp)
	{
		split("",prCoCount);
		numEl=split(CountConcProp[Concprop],prCoCount,"##"); #Numero di classi associate alla proprietà

		percentuale = (numEl/totConcept)*100;

		print Concprop "##" numEl "##" percentuale >> destDir"/"destFile;
	}

}

