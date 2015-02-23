BEGIN {
	destDir=destinatioDirectory;
}

{ 
	split("", newConc);
	split($0,newConc,"##");

	newConcepts[newConc[1]"##"newConc[2]]=newConc[3];
}

END { 
	
	system("touch "destDir"/newConcepts.txt"); #Creo il file così da averlo, anche se vuoto
	
	#Header
	print "Concept##Instance Example" >> destDir"/newConcepts.txt";	
	
	for (conc in newConcepts)
	{
		print conc "##" newConcepts[conc] >> destDir"/newConcepts.txt";
	}

}

