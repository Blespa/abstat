BEGIN {
	dataForCompDir=dataForCompDirectory;
	destDir=destinatioDirectory;
	destFile=destinationFile;

	while (getline < (dataForCompDir"/countDataType.txt"))
	{
		# Associo i conteggi ad ogni risorsa
		split("", cnt);
		split($0,cnt,"##");
			
		count[cnt[1]]=cnt[2];
	}

}

{ 
	split("", DaTy);
	split($0,DaTy,"##");

	toCheck=DaTy[1]"##"DaTy[2];
	
	if(toCheck in CountDt)
		CountDt[toCheck]=CountDt[toCheck]+DaTy[3];
	else
		CountDt[toCheck]=DaTy[3];
}

END { 
	
	system("touch "destDir"/"destFile); #Creo il file così da averlo, anche se vuoto

	print "Datatype##Property##How complete is (%)##Number of resources of the Datatype##Total Number of Datatype Resources" >> destDir"/"destFile;

	for (dt in CountDt)
	{
		split("",dtCount);
		split(dt,dtCount,"##")

		percentuale = (CountDt[dt]/count[dtCount[1]])*100;

		print dt "##" percentuale "##" CountDt[dt] "##" count[dtCount[1]] >> destDir"/"destFile;
	}

}

