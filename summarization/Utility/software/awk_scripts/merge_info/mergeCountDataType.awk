BEGIN {
	destDir=destinatioDirectory;
}

{ 
	split("", DaTy);
	split($0,DaTy,"##");
	
	if(DaTy[1] in CountDt)
		CountDt[DaTy[1]]=CountDt[DaTy[1]]+DaTy[2];
	else
		CountDt[DaTy[1]]=DaTy[2];
}

END { 
	
	system("touch "destDir"/countDataType.txt"); #Creo il file così da averlo, anche se vuoto
	
	totale=0;
	for (dt in CountDt)
	{
		totale=totale+CountDt[dt];
	}

	print "Property##Number of resources (that have|are object of) the property##% Respect to the total Number of Resources" >> destDir"/countDataType.txt";

	for (dt in CountDt)
	{
		percentuale = (CountDt[dt]/totale)*100;

		print dt "##" CountDt[dt] "##" percentuale >> destDir"/countDataType.txt";
	}
	print "##" totale >> destDir"/countDataType.txt";

}

