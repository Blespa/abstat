BEGIN {
	dataForCompDir=dataForCompDirectory;
	destDir=destinatioDirectory;
	destFile=destinationFile;

	while (getline < (dataForCompDir"/concPropDTSUBJ.txt"))
	{
		split("",subjConcDataSpl);
		split($0,subjConcDataSpl,"##");

		subjConcData[subjConcDataSpl[1]]=subjConcDataSpl[2]"##"subjConcDataSpl[3]
	}

	while (getline < (dataForCompDir"/concPropDTOBJ.txt"))
	{
		split("",objConcDataSpl);
		split($0,objConcDataSpl,"##");

		objConcData[objConcDataSpl[1]]=objConcDataSpl[2]"##"objConcDataSpl[3]
	}

	while (getline < (dataForCompDir"/countDTSUBJ.txt"))
	{
		split("",resSubjSpl);
		split($0,resSubjSpl,"##");

		resSubj[resSubjSpl[1]]=resSubjSpl[3];
	}

	while (getline < (dataForCompDir"/countDTOBJ.txt"))
	{
		split("",resObjSpl);
		split($0,resObjSpl,"##");

		resObj[resObjSpl[1]]=resObjSpl[3];
	}

}

{ 
	split("", ResProp);
	split($0,ResProp,"##");
	
	if(ResProp[1] in CountResProp)
		CountResProp[ResProp[1]]=CountResProp[ResProp[1]]+ResProp[2];
	else
		CountResProp[ResProp[1]]=ResProp[2];
}

END { 
	
	system("touch "destDir"/"destFile); #Creo il file così da averlo, anche se vuoto

	print "Property##How many times is used##Number of Subject Resources##Number of Object Resources##Number of minimum type class that are subject of the property##Num$

	for (prop in CountResProp)
	{
		if( prop in subjConcData )
			ClSubj = "##"subjConcData[prop]
		else
			ClSubj = "##"0

		if( prop in objConcData )
			ClObj = "##"objConcData[prop]
		else
			ClObj = "##"0

		if( prop in resSubj )
			ResSubjNum = "##"resSubj[prop]
		else
			ResSubjNum = "##"0

		if( prop in resObj )
			ResObjNum = "##"resObj[prop]
		else
			ResObjNum = "##"0


		print prop "##" CountResProp[prop] "" ResSubjNum "" ResObjNum "" ClSubj "" ClObj >> destDir"/"destFile;
	}
}

