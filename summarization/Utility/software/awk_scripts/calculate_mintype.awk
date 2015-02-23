BEGIN {
	
	dirDataForComp=directoryDataForComp;
	destDir=destinatioDirectory;
	letter=lett;

	# Carico la struttura contenente tutti i percorsi della gerarchia di sottoclassi
	while (getline < (dirDataForComp"/path.txt"))
	{
		subclOf[$0];
	}
	
	
	# Carico la struttura contenente tutti i concetti
	
	while (getline < (dirDataForComp"/Concepts.txt"))
	{
		concepts[$0]=$0;
	}
	
	# Carico la struttura contenente tutti i concetti equivalenti
	
	while (getline < (dirDataForComp"/EquConcepts.txt"))
	{
		# Associo i concetti equivalenti ad ogni concetto indicato
		split("", equClass);
		split($0,equClass,"##"); # Splitto la relazione di sottoclasse
		
		# Prendo le sottoclassi
		equ=substr($0, length(equClass[1])+3);
		equConcepts[equClass[1]]=equ;
	}
	
	#triple_lette=0;
}

{ 
	# Elaboro ogni tripla del file
	
	# Stampa di controllo
	#if(triple_lette%10000==0 && triple_lette!=0)
		#print triple_lette " triple lette...";
	
	split("", statement);
	split($0,statement,"##"); # Splitto la tripla
	
	split("", URI_El);
	lengthArray=split(statement[3],URI_El,"/"); # Splitto lo statement
	
	entita=statement[1];
	risorsa=statement[3];
	risorsa_nome=URI_El[lengthArray];
	
	equivClass="";

	if(risorsa_nome!="owl#Thing"){
		
		if(!(risorsa in concepts)){
		
			if(!(risorsa in stored_equiv_class)){
				#print "Prendo l'equivalent class per: " risorsa;
				equivClass = getEqu(equConcepts,risorsa);
				stored_equiv_class[risorsa]=equivClass;
				#print "Presa: " equivClass;
			}
			else{
				#print "PRENDO EQUIVALENT DALLA MEMORIA PER: " risorsa;
				equivClass = stored_equiv_class[risorsa];
				#print "Ripresa dalla memoria: " equivClass;
			}
		}
		
		# Creo l'array contenente il concetto + le eventuali equivalent class, per analizzare la posizione nella gerarchia del tipo attuale della risorsa
		split("", conToAnalyze);
		conToAnalyze[1]=risorsa;
		count_to_analyze=0; # Se trovo classi equivalenti, rimuovo quella citata nei dati e associo le informazioni alle classi equivalenti
		
		if(equivClass!=""){
			
			split("", eCl);
			lengthArray=split(equivClass,eCl,"##"); # Separo le informazioni sulle classi equivalenti
			
			#print "LENG: " length(eCl);
			
			for (i = 1; i <= lengthArray; i++)
			{
				#print "STAMPO: " i " - " eCl[i];
				count_to_analyze++;
				conToAnalyze[count_to_analyze]=eCl[i];
				#print "Equiv - " i ": " conToAnalyze[count_to_analyze];
			}
		}
		
		if(count_to_analyze==0){
			count_to_analyze=1;
		}
		
		for (i = 1; i <= count_to_analyze; i++)
		{
			risorsa_da_analizzare = conToAnalyze[i];
			
			#print "ANALIZZO (" entita "): " risorsa_da_analizzare;
			
			# Se la classe non è nell'elenco dei concetti e non ho trovato equivalent class
			if(!(risorsa_da_analizzare in concepts) && equivClass==""){
				
				# TODO: Se non è nei concetti e non ha equivalent class non può neppure risultare nei percorsi
				#if(entita in minType){ # Se ho già associato un tipo a questa entità
					
					#regex = minType[entita]"(.*)"risorsa_da_analizzare; #Verifico se il concetto è sottoclasse di quello salvato
					
					#if(!(regex in stored_regex_result)){
						
						#if(match(equConcepts[var], regex))
						#	print "match";
						#print regex;
						#found=0;
						#for (var in subclOf)
						#{
							#print "Match: " var " con " regex;
							#if(match(var, regex)){
								#found=1;
								#break;
							#}
						#}
						
						#stored_regex_result[regex]=found;
						#print "Salvo risultato per: " regex " - " found;
					#}
					#else{
						#print "PRENDO EQUIVALENT DALLA MEMORIA PER: " risorsa;
						#found = stored_regex_result[regex];
						#print "Ripresa dalla memoria: " regex " - " found;
					#}
					
					#if(found==1){ # Se ho trovato almeno un path
						#count[minType[entita]]=count[minType[entita]]-1;
						#minType[entita]=risorsa_da_analizzare;
						#tipologia[entita]=0;
						#count[risorsa_da_analizzare]=count[risorsa_da_analizzare]+1;
					#}			
				#}
				#else{
					#minType[entita]=risorsa_da_analizzare;
					#tipologia[entita]=0;
					#count[risorsa_da_analizzare]=count[risorsa_da_analizzare]+1;
					#print "Count: " count[risorsa_da_analizzare];
				#}
				
				# Salvo il nuovo concetto, se non l'ho già salvato
				if(!(risorsa_da_analizzare in newConcepts)){
					split("", URI_El);
					lengthArray=split(risorsa_da_analizzare,URI_El,"/"); # Splitto lo statement
					risorsa_da_analizzare_nome=URI_El[lengthArray];
					newConcepts[risorsa_da_analizzare]=risorsa_da_analizzare_nome; # URI => LocalName
					obtainedBy[risorsa_da_analizzare]=entita;
					#print "Salvo: " risorsa_da_analizzare " - " risorsa_da_analizzare_nome;
				}
				
				#print "ENTITA: " entita;
				
				# Associo il concetto all'insieme dei tipi della risorsa
				if(!(entita in uknHierConcept)){ # Primo concetto sconosciuto per la risorsa
					
					uknHierConcept[entita] = risorsa_da_analizzare;
					# Conteggio la presenza di un'istanza per questo concetto
					if(!(entita in minType)){
						count[risorsa_da_analizzare]=count[risorsa_da_analizzare]+1;
					}
				}
				else{
					regex = "(^"risorsa_da_analizzare"$)|(^"risorsa_da_analizzare"##)|(##"risorsa_da_analizzare"##)|(##"risorsa_da_analizzare"$)"; #Verifico se ho già associato il concetto alla risorsa
						
					found=0;
					if(match(uknHierConcept[entita], regex)){
							found=1;
					}
						
					if(found==0){  #Se non ho ancora associato la risorsa
						uknHierConcept[entita] = uknHierConcept[entita]"##"risorsa_da_analizzare;
						# Conteggio la presenza di un'istanza per questo concetto
						if(!(entita in minType)){
							count[risorsa_da_analizzare]=count[risorsa_da_analizzare]+1;
						}
					}

				}

			}
			else{ #Il concetto è nell'elenco dei concetti, o ho trovato un equivalent class
			
				if(entita in minType){ # Se ho già associato un tipo a questa entità e non valuto uno dei tipi già associati
					#Diviso dal precedente per evitare l'"in" che è pesante
					if(!match(minType[entita],"(^"risorsa_da_analizzare"$)|(^"risorsa_da_analizzare"#-#)|(#-#"risorsa_da_analizzare"#-#)|(#-#"risorsa_da_analizzare"$)")){
						# Ottengo tutti i tipi minimi associati all'entità
						split("",allMinTypes);
						lengthallMinTypes=split(minType[entita],allMinTypes,"#-#");
						split("",minTypeToSost); #Salvo gli eventuali tipi minimi da sostituire con l'attuale
					
						# Verifico se il nuovo tipo è sottotipo di uno già presente
						isSubType=0;
						for (iamt = 1; iamt <= lengthallMinTypes; iamt++)
						{
						
							actMinType=allMinTypes[iamt];
					
		 					#Verifico se il nuovo tipo è sottoclasse di quello salvato e sono nella stessa gerarchia
							regex = "((\\[|, )"actMinType"(\\]|, ))(.*)((\\[|, |)"risorsa_da_analizzare"(\\]|, ))";
					
							if(!(regex in stored_regex_result)){
						
								
								#print regex;
								found=0;
								for (var in subclOf)
								{
									#if(match(var, regex))
										#print "match - con: " var;

									#print "Match: " var " con " regex;
									if(match(var, regex)){
										found=1;
										break;
									}
								}
						
								stored_regex_result[regex]=found;
								#print "Salvo risultato per: " regex " - " found;
							}
							else{
								#print "PRENDO EQUIVALENT DALLA MEMORIA PER: " risorsa;
								found = stored_regex_result[regex];
								#print "Ripresa dalla memoria: " regex " - " found;
							}
					
							if(found==1){ # Se ho trovato almeno un path

								minTypeToSost[actMinType]; #Salvo il tipo da sostituire

								isSubType=1; #Indico che il nuovo tipo è sottotipo di uno presente
							}
						}

						if(isSubType==0){ #Se il nuovo tipo non è sottotipo di nessuno di quelli attuali
						
							# Verifico se il nuovo tipo è sopraclasse di uno già presente
							isSupType=0;
							for (iamt = 1; iamt <= lengthallMinTypes; iamt++)
							{
								actMinType=allMinTypes[iamt];
								#Verifico se il nuovo tipo è nella stessa gerarchia del precedente, altrimenti si tratta di un nuovo tipo minimo in una diversa gerarchia

								regex_newmintype = "((\\[|, )"risorsa_da_analizzare"(\\]|, ))(.*)((\\[|, |)"actMinType"(\\]|, ))";
					
								if(!(regex_newmintype in stored_regex_result_newmintype)){
						
									#if(match(equConcepts[var], regex))
									#	print "match";
									#print regex;
									found=0;
									for (var in subclOf)
									{
										#print "Match: " var " con " regex;
										if(match(var, regex_newmintype)){
											found=1;
											break;
										}
									}
						
									stored_regex_result_newmintype[regex_newmintype]=found;
									#print "Salvo risultato per: " regex " - " found;
								}
								else{
									#print "PRENDO EQUIVALENT DALLA MEMORIA PER: " risorsa;
									found = stored_regex_result_newmintype[regex_newmintype];
									#print "Ripresa dalla memoria: " regex " - " found;
								}

								if(found==1){ # Se è sopraclasse

									isSupType=1; #Indico che il nuovo tipo è sopratipo di uno presente
									break;
								}
							}
						}

						#Sostituisco i tipi trovati con il nuovo sottotipo
						numMTSost=0;
						for( mTSost in minTypeToSost ){

							#print "Sostituisco: " mTSost
							if(numMTSost==0) #Se è sottotipo di un solo concetto
								gsub(mTSost,risorsa_da_analizzare,minType[entita]);
							else{#Se è sottotipo di più concetti, va tenuto solo questo (aggiunto nel passo precedente del ciclo)
								if(match(minType[entita],mTSost"#-#"))#Prima posizione
									gsub(mTSost"#-#","",minType[entita]);
								else if(match(minType[entita],"#-#"mTSost"#-#"))#Posizione centrale
									gsub("#-#"mTSost"#-#","",minType[entita]);
								else if(match(minType[entita],"#-#"mTSost))#Ultima posizione
									gsub("#-#"mTSost,"",minType[entita]);

							}

							count[mTSost]=count[mTSost]-1;

							tipologia[entita]=1;
								
							numMTSost++;
					
						}
						
						if( numMTSost!=0 )
							count[risorsa_da_analizzare]=count[risorsa_da_analizzare]+1;
						else if( numMTSost==0 && isSupType==0){ #Se il nuovo tipo non è sottoconcetto o sopraconcetto, allora è un nuovo minimo
							#print "Aggiungo: " risorsa_da_analizzare

							tmpMinType=minType[entita];
							minType[entita]=tmpMinType"#-#"risorsa_da_analizzare;
							#print "Risorsa: " entita " - Nuovo Tipo Minimo: " minType[entita];
							tipologia[entita]=1;
							count[risorsa_da_analizzare]=count[risorsa_da_analizzare]+1;

						}
					}

				}
				else{
					minType[entita]=risorsa_da_analizzare;
					tipologia[entita]=1;

					#print "Aggiungo primo: " risorsa_da_analizzare
					
					if(entita in uknHierConcept){
						split("", uknToSottr);
						lengthArray=split(uknHierConcept[entita],uknToSottr,"##");
						
						#print "Split UKN: " uknToSottr[1];

						for( i=1; i<=lengthArray; i++){
							count[uknToSottr[i]]=count[uknToSottr[i]]-1;
							#print "Scalo: " uknToSottr[i] " a: " count[uknToSottr[i]];
						}
						
					}
						
					count[risorsa_da_analizzare]=count[risorsa_da_analizzare]+1;
					#print "Count: " count[risorsa_da_analizzare];
				}
			}
		}	
	}

	#triple_lette++;
} 

END { 
	
	# Salvo tutte le informazioni su file
	
	#for (var in subclOf)
	#{
		#print var; #$var stampa il contenuto se è un array associativo
	#}
	
	#for (var in concepts)
	#{
		#print var " => " concepts[var]; #$var stampa il contenuto se è un array associativo
	#}
	
	#for (var in equConcepts)
	#{
		#print var " => " equConcepts[var]; #$var stampa il contenuto se è un array associativo
	#}
	system("touch "destDir"/"letter"_newConcepts.txt"); #Creo il file così da averlo, anche se vuoto

	for (var in newConcepts)
	{
		print var "##" newConcepts[var] "##" obtainedBy[var] >> destDir"/"letter"_newConcepts.txt";
	}

	system("touch "destDir"/"letter"_minType.txt"); #Creo il file così da averlo, anche se vuoto	

	for (var in minType) #TODO: Se posso avere tipologie diverse, trattare per ogni tipo minimo inserito
	{
		print tipologia[var] "##" var "##" minType[var] >> destDir"/"letter"_minType.txt";
	}

	system("touch "destDir"/"letter"_uknHierConcept.txt"); #Creo il file così da averlo, anche se vuoto
	
	for (var in uknHierConcept)
	{
		print var "##" uknHierConcept[var] >> destDir"/"letter"_uknHierConcept.txt";
	}

	system("touch "destDir"/"letter"_countConcepts.txt"); #Creo il file così da averlo, anche se vuoto
	
	for (var in count)
	{
		print var "##" count[var] >> destDir"/"letter"_countConcepts.txt";
	}
	
	# Aggiungo tutti i concetti che non hanno istanze e indico se sono tipi minimi
	for (concetto in concepts)
	{
		if(!(concetto in count)){
		
			#if(!(concetto in stored_isMinType_class)){
				#print "Prendo l'equivalent class per: " concetto;
				#isMinType = checkMinType(subclOf,concetto);
				#stored_isMinType_class[concetto]=isMinType;
				#print "Presa: " isMinType;
			#}
			#else{
				#print "PRENDO EQUIVALENT DALLA MEMORIA PER: " concetto;
				#isMinType = stored_isMinType_class[concetto];
				#print "Ripresa dalla memoria: " isMinType;
			#}
			
			count[concetto]=0; # Indico l'assenza di istanze
			
			#isMinType=0 (NON Tipo Minimo), isMinType=1 (Tipo Minimo)
			#print concetto "##0##"isMinType >> destDir"/"letter""countConcepts".txt";
			print concetto "##" count[concetto] >> destDir"/"letter"_countConcepts.txt";
		}
	}
	
	# Report contenente sulla prima colonna tutte le radici della gerarchia di concetti o eventuali nuovi concetti e per ognuno di questi i sottoconcetti (Ordinati per vicinanza alla radice) con relativo numero di istanze associato.
	
	# Creo il report a partire dalle radici della gerarchia
	#for (repCon in allSubConcept){
		
		 #split("", rootSubConc);
		 #lengthArray=split(repCon,rootSubConc,"##"); # Ottengo Radice e sottoconcetti
		 #toPrint = "";
		 #toPrintCount = "";
		 
		 # Imposto lo spazio per i sottoconcetti	
		 #for (i = 1; i <= lengthArray; i++)
		 #{
			#split("", URI_El);
			#lengthArray=split(rootSubConc[i],URI_El,"/"); # Splitto lo statement
			
			#risorsa_nome=substr(URI_El[lengthArray], 0, length(URI_El[lengthArray]));
			
			#toPrint = toPrint "##" risorsa_nome;
			
			#if(rootSubConc[i] in count){
				#toPrintCount = toPrintCount "##" count[rootSubConc[i]];
			#}
			#else{
				#toPrintCount = toPrintCount "##" "0";
			#}
			
		 #}
		 
		 #print toPrint >> "mintype_result/countConceptsReport"FILENAME".txt";
		 
		 # Salvo la radice
		 #split("", URI_El);
		 #lengthArray=split(rootSubConc[1],URI_El,"/"); # Splitto lo statement
			
		 #risorsa_nome=substr(URI_El[lengthArray], 0, length(URI_El[lengthArray]));
		 
		 #print risorsa_nome "" toPrintCount >> "mintype_result/countConceptsReport"FILENAME".txt";
		
	#}
	
	# - Se non c'è in count allora è 0
	
	# Aggiungo le informazioni relative ai nuovi concetti
	
}

# Determina se il concetto passato è un'equivalent class di una classe dell'ontologia
# NOTA: va scorso l'array perchè un concetto dell'ontologia può avere più equivalent class, e un concetto esterno può essere equivalent class di più concetti dell'ontologia
function testEqu(equConcepts,concept)
{
	
	for (var in equConcepts)
	{
		#gsub("\/", "\/", concept);
		#gsub("\.", "\.", concept);
		
		regex = "(^"concept"$)|(^"concept"##)|(##"concept"##)|(##"concept"$)";

		#if(match(equConcepts[var], regex))
		#	print "match";
		#print regex;	
		if(match(equConcepts[var], regex)){
			#print regex;
			#print equConcepts[var];
			return 1; # Se è un'equivalent class restituisco 1
		}
	}
	
	# Se non è un'equivalent class restituisco 0
    return 0;
}

# Determina se il concetto passato è un'equivalent class di una classe dell'ontologia e le restituisce se esiste
# NOTA: va scorso l'array perchè un concetto dell'ontologia può avere più equivalent class, e un concetto esterno può essere equivalent class di più concetti dell'ontologia
function getEqu(equConcepts,concept)
{
	
	equivalentClass="";
	
	for (var in equConcepts)
	{
		#gsub("\/", "\/", concept);
		#gsub("\.", "\.", concept);
		
		regex = "(^"concept"$)|(^"concept"##)|(##"concept"##)|(##"concept"$)";
		#if(match(equConcepts[var], regex))
		#	print "match";
		#print regex;	
		if(match(equConcepts[var], regex)){
			#print regex;
			#print equConcepts[var];
			if(equivalentClass=="")
				equivalentClass=var;
			else
				equivalentClass=equivalentClass"##"var;
		}
	}
	
	# Restituisco le classi equivalenti
    return equivalentClass;
}

# Determina se il concetto passato è minimo nella gerarchia dei concetti
function checkMinType(subclOf,concept)
{
	
	for (var in subclOf)
	{
		#gsub("\/", "\/", concept);
		#gsub("\.", "\.", concept);
		
		regex = "((\\[|, )"concept"(\\]$))";
		#if(match(equConcepts[var], regex))
		#	print "match";
		#print regex;	
		if(match(var, regex)){
			return "1"
		}
	}
	
	return "0"
}
