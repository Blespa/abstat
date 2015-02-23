BEGIN {
	destDir=destinatioDirectory;
}

{ 
	split("", Conc);
	split($0,Conc,"##");
	
	if(Conc[1] in CountConc)
		CountConc[Conc[1]]=CountConc[Conc[1]]+Conc[2];
	else
		CountConc[Conc[1]]=Conc[2];
}

END { 
	
	system("touch "destDir"/countConcepts.txt"); #Creo il file così da averlo, anche se vuoto
	
	totale=0;
	for (conc in CountConc)
	{
		totale=totale+CountConc[conc];
	}

	print "Concept##Number of Instances##% of Total Resources" >> destDir"/countConcepts.txt";

	for (conc in CountConc)
	{
		percentuale = (CountConc[conc]/totale)*100;

		print conc "##" CountConc[conc] "##" percentuale >> destDir"/countConcepts.txt";
	}
	print "##" totale >> destDir"/countConcepts.txt";

}

