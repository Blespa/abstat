BEGIN {
	destDir=destinatioDirectory;
	destFile=destinationFile;

	unr_r_pattern_subj=0;
	unr_r_pattern_obj=0;
	unr_r_pattern_tot=0;
}

{ 
	if(match($0,"^##")){
	}
	else{
		split("", Rel);
		split($0,Rel,"##");

		#Variabili utili a identificare la tipologia di pattern
		s_res=0; #Soggetto risolto utilizzando le sole informazioni presenti nei dati
		s_unr=0; #Soggetto non risolto 
		s_dr=0; #Soggetto risolto utilizzando DR
		s_ukn=0; #Soggetto risolto utilizzando un tipo sconosciuto nell'intologia

		o_res=0; #Oggetto risolto utilizzando le sole informazioni presenti nei dati
		o_unr=0; #Oggetto non risolto 
		o_dr=0; #Oggetto risolto utilizzando DR
		o_ukn=0; #Oggetto risolto utilizzando un tipo sconosciuto nell'intologia

		toCheck=1;

		soggetto=Rel[toCheck];
		toCheck++;

		if(soggetto=="http://www.w3.org/2002/07/owl#Thing"){ #Non ho risolto il tipo
			s_unr=1;
		}
		else if(Rel[toCheck]=="DR" || Rel[toCheck]=="DR-T"){ #Ho risolto il soggetto utilizzando DR
			toCheck++;			
			s_dr=1;			
		}
		else if(Rel[toCheck]=="UKN"){ #Ho risolto il soggetto utilizzando un tipo sconosciuto nell'ontologia
			toCheck++;			
			s_ukn=1;	
		}
		else{
			s_res=1;
		}

		proprieta=Rel[toCheck];
		toCheck++;

		oggetto=Rel[toCheck];
		toCheck++;

		if(oggetto=="http://www.w3.org/2002/07/owl#Thing" || oggetto=="Ukn_Type"){ #Non ho risolto il tipo
			o_unr=1;
		}
		else if(Rel[toCheck]=="DR" || Rel[toCheck]=="DR-T"){ #Ho risolto il soggetto utilizzando DR
			toCheck++;			
			o_dr=1;			
		}
		else if(Rel[toCheck]=="UKN"){ #Ho risolto il soggetto utilizzando un tipo sconosciuto nell'ontologia
			toCheck++;			
			o_ukn=1;	
		}
		else{
			o_res=1;
		}

		#Recupero il conteggio di questo pattern
		numPattern=Rel[toCheck];
		
		#Aggiorno i conteggi dei pattern non risolti
		if(s_unr==1){
			unr_r_pattern_subj=unr_r_pattern_subj+numPattern; #Conteggio il pattern non risolto
			#Conteggio il pattern come non risolto se soggetto e/o oggetto nno sono risolti
			unr_r_pattern_tot=unr_r_pattern_tot+numPattern;
		}
		else if(o_unr==1){
			unr_r_pattern_obj=unr_r_pattern_obj+numPattern; #Conteggio il pattern non risolto
			#Conteggio il pattern come non risolto se soggetto e/o oggetto nno sono risolti
			unr_r_pattern_tot=unr_r_pattern_tot+numPattern;
		}
			
		#Associo il conteggio alle tipologie opportune
		if(s_res==1)
			s_res=numPattern;
		else if(s_unr==1)
			s_unr=numPattern;
		else if(s_dr==1)
			s_dr=numPattern;
		else if(s_ukn==1)
			s_ukn=numPattern;

		if(o_res==1)
			o_res=numPattern;
		else if(o_unr==1)
			o_unr=numPattern;
		else if(o_dr==1)
			o_dr=numPattern;
		else if(o_ukn==1)
			o_ukn=numPattern;
		
		pattern=soggetto"##"proprieta"##"oggetto;

		#print $0		
		#print pattern

		if(!(pattern in RelAttrPattern)){ #Prima volta che lo trovo nei dati
			
			RelAttrPattern[pattern]=numPattern"##"s_res"##"s_unr"##"s_dr"##"s_ukn"##"o_res"##"o_unr"##"o_dr"##"o_ukn;
		}
		else{
			
			conteggi=RelAttrPattern[pattern];

			split("",contSpl);
			split(conteggi,contSpl,"##");

			#Aggiorno i conteggi
			contSpl[1]=contSpl[1]+numPattern; #Totale
			contSpl[2]=contSpl[2]+s_res; #s_res
			contSpl[3]=contSpl[3]+s_unr; #s_unr
			contSpl[4]=contSpl[4]+s_dr; #s_dr
			contSpl[5]=contSpl[5]+s_ukn; #s_ukn
			contSpl[6]=contSpl[6]+o_res; #o_res
			contSpl[7]=contSpl[7]+o_unr; #o_unr
			contSpl[8]=contSpl[8]+o_dr; #o_dr
			contSpl[9]=contSpl[9]+o_ukn; #o_ukn

			#Salvo i conteggi aggiornati
			RelAttrPattern[pattern]=contSpl[1]"##"contSpl[2]"##"contSpl[3]"##"contSpl[4]"##"contSpl[5]"##"contSpl[6]"##"contSpl[7]"##"contSpl[8]"##"contSpl[9];
			
		}

		#print RelAttrPattern[pattern]


	}
	
}

END { 
	
	system("touch "destDir"/"destFile); #Creo il file così da averlo, anche se vuoto
	totale=0;

	print "URI Subject##URI Property##URI Object##Number of instances##.." >> destDir"/"destFile;

	for (patrn in RelAttrPattern)
	{
		split("",cntData);
		split(RelAttrPattern[patrn],cntData,"##");

		totale=totale+cntData[1]; #Il primo valore rappresenta il conteggio totale

		print patrn "##" RelAttrPattern[patrn] >> destDir"/"destFile;
	}
	
	print "Total##" totale >> destDir"/"destFile;
	print "Unresolved Subject##" unr_r_pattern_subj >> destDir"/"destFile;
	print "Unresolved Object##" unr_r_pattern_obj >> destDir"/"destFile;
	print "Unresolved Total##" unr_r_pattern_tot >> destDir"/"destFile;

}

