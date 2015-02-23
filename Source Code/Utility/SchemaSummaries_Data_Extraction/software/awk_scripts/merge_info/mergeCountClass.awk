BEGIN {
	dataForCompDir=dataForCompDirectory;
	destDir=destinatioDirectory;
	destFile=destinationFile;

	while (getline < (dataForCompDir"/countConcepts.txt"))
	{
		# Associo i conteggi ad ogni risorsa
		split("", cnt);
		split($0,cnt,"##");
			
		count[cnt[1]]=cnt[2];
	}

}

{ 
	split("", Conc);
	split($0,Conc,"##");

	toCheck=Conc[1]"##"Conc[2];
	
	if(toCheck in CountClass)
		CountClass[toCheck]=CountClass[toCheck]+Conc[3];
	else
		CountClass[toCheck]=Conc[3];
}

END { 
	
	system("touch "destDir"/"destFile); #Creo il file così da averlo, anche se vuoto

	print "Concept##Property##How complete is (%)##Number of resources of the minimum type class##Total Number of Concept Resources" >> destDir"/"destFile;

	for (conc in CountClass)
	{
		split("",clCount);
		split(conc,clCount,"##")

		percentuale = (CountClass[conc]/count[clCount[1]])*100;

		print conc "##" percentuale "##" CountClass[conc] "##" count[clCount[1]] >> destDir"/"destFile;
	}

}

