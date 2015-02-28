BEGIN {

	minTypeResDir=minTypeResultsDirectory;
	dirDataForComp=directoryDataForComp;
	dirTmpFile=directoryTempFile;
	destDir=destinatioDirectory;
	destDirDt=destinatioDirectoryDt;
	letter=lett;
	
	# Carico la struttura contenenete le informazioni sui tipi minimi
	
	#Pesa qualche secondo di overhead, quindi fare come scritto in testa, ovvero tutto nella stessa analisi
	while (getline < (minTypeResDir"/"letter"_minType.txt"))
	{
		# Associo i tipi minimi ad ogni risorsa
		split("", minTp);
		split($0,minTp,"##");

		minType[minTp[2]]=minTp[3];
	}
	
	while (getline < (minTypeResDir"/"letter"_uknHierConcept.txt"))
	{
		# Associo i tipi sconosciuti ai concetti
		split("", UK);
		split($0,UK,"##");

		Ukn[UK[1]]=UK[2];
	}
	
	while (getline < (dirDataForComp"/DR.txt"))
	{
		# Associo Domain e Range ad ogni proprietà
		split("", DR);
		split($0,DR,"##");

		DRRel[DR[1]]=DR[2]"##"DR[3];
	}	
	
	while (getline < (dirDataForComp"/EquProperties.txt"))
	{
		# Carico le proprietà equivalenti
		split("", equ);
		split($0,equ,"##");

		equProp[equ[1]]=equ[2];
	}
	
	# Carico la struttura contenente tutte le sottoclassi per le radici della gerarchia
	#while (getline < "r_pattern_cal_data/allSubConcept.txt")
	#{
		#allSubConcept[$0];
	#}
	
	# Carico la struttura contenente tutte le proprietà verso concetti
	while (getline < (dirDataForComp"/Properties.txt"))
	{
		currObjProperties[$0];
	}

	# Carico la struttura contenente tutte le proprietà verso datatype
	while (getline < (dirDataForComp"/DTProperties.txt"))
	{
		currDTProperties[$0];
	}
	
	while (getline < (minTypeResDir"/"letter"_countConcepts.txt"))
	{
		# Associo i conteggi ad ogni risorsa
		split("", cnt);
		split($0,cnt,"##");
			
		count[cnt[1]]=cnt[2];
	}
	
	triple_lette=0;

	#TODO:Decommentare se sono utili per la Summarization	
	#countSUBJREs=0;
	#countOBJREs=0;

	#Valori di quanto mantenere in memoria settati per: Max ~1.5Gb (compreso caricamento di informazioni iniziale, per eccesso di 500Mb), 2.550.000 circa triple velocemente. Compromesso tra memoria allocata (limitata per utilizzare più processi in parallelo), velocità ed efficienza sufficiente in parallelizzazione, anche valutando la probabilità di trovare risorse lontane tra loro.

	countSUBJTempSubj=0;
	countSUBJTempSubjSost=1;
	maxSUBJTempSubj=50000000; #1000000
	firstSaveSUBJTemp=0;

	countOBJTempObj=0;
	countOBJTempObjSost=1;
	maxOBJTempObj=50000000; #400000
	firstSaveOBJTemp=0;

	#TODO:Decommentare se sono utili per la Summarization		
	#countDTSUBJREs=0;

	countSUBJTempSubjDt=0;
	countSUBJTempSubjSostDt=1;
	maxSUBJTempSubjDt=50000000; #1100000
	firstSaveSUBJTempDt=0;

	countOBJTempObjDt=0;
	countOBJTempObjSostDt=1;
	maxOBJTempObjDt=50000000; #500000
	firstSaveOBJTempDt=0;

	#TODO:Decommentare se sono utili per la Summarization		
	#countDTSUBJREs=0;
	#countDTOBJREs=0;
}

{ 

# Determino le operazioni (Object Property o DataType Property) in base al file in analisi	
if(match(FILENAME,"obj_properties")){ #Elaboro Object Properties

	# Elaboro ogni tripla del file
	#startTime=systime();
	#Triple=$0;
	
	# Stampa di controllo
	#if(triple_lette%10000==0 && triple_lette!=0)
		#print triple_lette " triple lette..." #>> "log.txt";
	
	#fflush(""); # Avviando gawk, funziona sicuro
		
	split("", statementObjProp)
	split($0,statementObjProp,"##"); # Splitto la tripla
	

		entitaSUbj = statementObjProp[1];
		proprieta = statementObjProp[2];
		
		if(!(proprieta in currObjProperties)){ #Se la proprietà non è nell'elenco delle proprietà (quindi non presa dall'ontologia)
			if(!(proprieta in stored_equiv_obj_prop)){
				#print "Prendo l'equivalent property per: " proprieta;
				equivProp = getEquProp(equProp,proprieta);
				stored_equiv_obj_prop[proprieta]=equivProp;
				#print "Presa: " equivProp;
			}
			else{
				#print "PRENDO EQUIVALENT DALLA MEMORIA PER: " proprieta;
				equivProp = stored_equiv_obj_prop[proprieta];
				#print "Ripresa dalla memoria: " equivProp;
			}
		
			# Associo la proprietà equivalente alla proprietà
			proprieta = equivProp;
		
			# Salvo la proprietà se nuova
			if(!(proprieta in newObjProperties)){
				split("", URI);
				lengthArray=split(proprieta,URI,"/"); # Splitto lo statement
				proprieta_nome=URI[lengthArray];
				newObjProperties[proprieta]=proprieta_nome; # URI => LocalName
			}
		}
		
		
		entitaObj = statementObjProp[3];
		
		subjUnresolv="";
		objUnresolv="";
		ClasseSubj="";
		ClasseObj="";
		moreMinTypeSubj=0;
		moreMinTypeObj=0;
		
		# Risolvo le informazioni sui tipi minimi dei concetti e salvo il conteggio del path
		if( entitaSUbj in minType ){

			#Verifico se per questa risorsa vi sono più tipi minimi, nel qual caso costruisco r-pattern per ognuno di questi
			split("", minTpMultiSubj);
			lengthArrayMultiSubj=split(minType[entitaSUbj],minTpMultiSubj,"#-#");		

			if(lengthArrayMultiSubj>1){ #Vi sono più tipi minimi per questa risorsa
				
				moreMinTypeSubj=1;
			}
			else{
				relation=minType[entitaSUbj];
				ClasseSubj=minType[entitaSUbj];
			}

		}
		else # TODO: Vedere come gestire i tipi non risolti, probabilmente a parte
		{
			# Verifico se il tipo è risolvibile con DR
			if(proprieta in DRRel){
				split("", DRProp);
				split(DRRel[proprieta],DRProp,"##");
				
				relation=DRProp[1]"##DR"; # Soggetto della proprietà
				ClasseSubj=DRProp[1];

				countToSave=entitaSUbj"##"DRProp[1];
				
				if(!(countToSave in instCounted)){
					
					if(entitaSUbj in Ukn){
						split("", uknToSottr);
						lengthArray=split(Ukn[entitaSUbj],uknToSottr,"##");
						
						#print "Split UKN: " uknToSottr[1];

						for( i=1; i<=lengthArray; i++){
							count[uknToSottr[i]]=count[uknToSottr[i]]-1;
							#print "Scalo: " uknToSottr[i];
						}
						
					}
					
					count[DRProp[1]] = count[DRProp[1]] + 1;

					#Più proprietà possono avere una risoluzione ti tipo analoga, ma la stessa risorsa va contata una sola volta per ogni tipo, indipendentemente dal numero di proprietà che risolvono il tipo in quel concetto e se è oggetto o soggetto
					instCounted[countToSave];
					
				}
					
			}
			else if(entitaSUbj in Ukn)
			{
				relation=Ukn[entitaSUbj]"##UKN";
				ClasseSubj=Ukn[entitaSUbj];
			}
			else
			{
				relation="http://www.w3.org/2002/07/owl#Thing";
				
				subjUnresolv = entitaSUbj;
			}
		}
		
					
		if(moreMinTypeSubj==0){
			relation=relation"##"proprieta"##";
		}

		for(iMulSubj=1; iMulSubj<=lengthArrayMultiSubj; iMulSubj++){
			
			if(moreMinTypeSubj>=1){
				relation=minTpMultiSubj[iMulSubj];		
				ClasseSubj=minTpMultiSubj[iMulSubj];
				relation=relation"##"proprieta"##";
			}	

			if( entitaObj in minType ){

				#Verifico se per questa risorsa vi sono più tipi minimi, nel qual caso costruisco r-pattern per ognuno di questi
				split("", minTpMultiObj);
				lengthArrayMultiObj=split(minType[entitaObj],minTpMultiObj,"#-#");		

				if(lengthArrayMultiObj>1){ #Vi sono più tipi minimi per questa risorsa
				
					moreMinTypeObj=1;
				}
				else{
					relation=relation""minType[entitaObj];
					ClasseObj=minType[entitaObj];
				}
			}
			else # TODO: Vedere come gestire i tipi non risolti, probabilmente a parte
			{
				# Verifico se il tipo è risolvibile con DR
				if(proprieta in DRRel){
					split("", DRProp);
					split(DRRel[proprieta],DRProp,"##");
				
					relation=relation""DRProp[2]"##DR"; # Oggetto della proprietà
					ClasseObj=DRProp[2];

					countToSave=entitaObj"##"DRProp[2];
				
					if(!(countToSave in instCounted)){
				
						if(entitaObj in Ukn){
							split("", uknToSottr);
							lengthArray=split(Ukn[entitaObj],uknToSottr,"##");
						
							#print "Split UKN: " uknToSottr[1];

							for( i=1; i<=lengthArray; i++){
								count[uknToSottr[i]]=count[uknToSottr[i]]-1;
								#print "Scalo: " uknToSottr[i];
							}
						
						}
					
						count[DRProp[2]] = count[DRProp[2]] + 1;
						
						#Più proprietà possono avere una risoluzione di tipo analoga, ma la stessa risorsa va contata una sola volta per ogni tipo, indipendentemente dal numero di proprietà che risolvono il tipo in quel concetto e se è oggetto o soggetto
						instCounted[countToSave];
					
					}
					
				}
				else if(entitaObj in Ukn)
				{
					relation=relation""Ukn[entitaObj]"##UKN";
					ClasseObj=Ukn[entitaObj];
				}
				else
				{
					relation=relation"""http://www.w3.org/2002/07/owl#Thing";
				
					objUnresolv = entitaObj;
				}
			
			}

			for(iMulObj=1; iMulObj<=lengthArrayMultiObj; iMulObj++){
			
				if(moreMinTypeObj>=1){
					relation=relation""minTpMultiObj[iMulObj];		
					ClasseObj=minTpMultiObj[iMulObj];
				}		

		
				if(!(relation in relationCount))
				{
					relationCount[relation]=1;
				}
				else
				{
					relationCount[relation]=relationCount[relation]+1;
				}			
		
				# Salvo eventuali informazioni sulle risorse non risolvibili, da mostrare come esempio
				#if(relation in egUnresolved){
		
					#if(subjUnresolv!=""){
						#split("", UrnRelation);
						#lengthArray=split(egUnresolved[relation],UrnRelation,"###"); # Splitto lo statement
						#to_check = subjUnresolv" (SUBJ)";
				
						#for(i=1; i<=lengthArray; i++){
							#UrnRelationCheck[UrnRelation[i]];
						#}
				
						#if(!(to_check in UrnRelationCheck))
							#egUnresolved[relation] = egUnresolved[relation]"###"subjUnresolv" (SUBJ)";
					#}
			
					#if(objUnresolv!=""){
						#split("", UrnRelation);
						#lengthArray=split(egUnresolved[relation],UrnRelation,"###"); # Splitto lo statement
						#to_check = objUnresolv" (OBJ)";
				
						#for(i=1; i<=lengthArray; i++){
							#UrnRelationCheck[UrnRelation[i]];
						#}
				
						#if(!(to_check in UrnRelationCheck))
							#egUnresolved[relation] = egUnresolved[relation]"###"objUnresolv" (OBJ)";
					#}
				
				#}
				#else{
			
					#if(subjUnresolv!="")
						#egUnresolved[relation] = subjUnresolv" (SUBJ)";
			
					#if(objUnresolv!="")
						#egUnresolved[relation] = objUnresolv" (OBJ)";	
			
				#}
		
				# Conto quante istanze hanno una determinata proprietà
				# Verifico il soggetto
				subj_to_check=entitaSUbj"##"proprieta;
				gsub("-","(m#i#n#u#s)",subj_to_check); #Necessario per il successivo grep per risorse con -string-
				subj_to_check_found=0;
				
				if(countSUBJTempSubj>0 && subj_to_check in tempCountedSUBJProp){
					subj_to_check_found=1;
					#print ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>TROVATO IN ARRAY!!!";
					break;
				}
				#system("sleep 2");
									
				#system("stat -c %s tmpFile/countedSUBJProp.txt");
				if( subj_to_check_found!=1 && countSUBJTempSubj>maxSUBJTempSubj ){
					#startTime=systime();
					#print "Cerco in File"
					#cmd="cat tmpFile/countedSUBJProp.txt";
					#if(substr(subj_to_check,1,1)=="-")
						#subj_to_check="\\"subj_to_check;		
					#gensub("/-/","\\-",1,subj_to_check);
					#print "Cerco: "subj_to_check;
					cmd="grep -m 1 -e \""subj_to_check"\" "dirTmpFile"/"letter"_countedSUBJProp.txt";
					#print "Comando: " cmd;
					while (cmd | getline line > 0){ 
						#print "Cerco: "subj_to_check;
						#print "    In: "line;
						#if(match(line,subj_to_check)){
							subj_to_check_found=1;
							#print ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>TROVATO!!!";
							break;
						#}
						#system("sleep 2");
					}
					close(cmd);
					
					#while ((getline < "tmpFile/countedSUBJProp.txt") > 0)
					#{
						#print "Cerco: "subj_to_check;
						#print "    In: "$0;
						#if(match($0,subj_to_check)){
							#subj_to_check_found=1;
							#print ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>TROVATO!!!";
							#break;
						#}
						#system("sleep 2");
					#}
					#endTime=systime();
					#diffTime=endTime-startTime;
					#if(diffTime>=1)
						#print "Time Subj Check: " diffTime " --- " subj_to_check;

					#cl=close("tmpFile/countedSUBJProp.txt");
				}
				#print "Close: " cl;

				#print "\nsubj_to_check_found: " subj_to_check_found;

				#print "\n";

				if(subj_to_check_found!=1){

					# Conto quante istanze di una data classe hanno una determinata proprietà, solo se la classe di appartenenenza dell'istanza è nota
					if(subjUnresolv==""){
						subj_to_check_classe=ClasseSubj"##"proprieta;
				
						if(!(subj_to_check_classe in countClassSUBJ)){
							# Conto il numero di concetti che hanno una data proprietà e che sono soggetto della proprietà
							if(!(proprieta in concPropSUBJ)){
								concPropSUBJ[proprieta]=ClasseSubj
								#TODO: Riattivare per Domain & Range
								#concPropSUBJOneClass[proprieta] = ClasseSubj;
							}
							else
								concPropSUBJ[proprieta]=concPropSUBJ[proprieta]"##"ClasseSubj
						
							countClassSUBJ[subj_to_check_classe]=1
						}
						else
							countClassSUBJ[subj_to_check_classe]=countClassSUBJ[subj_to_check_classe]+1				
					}
			
					if(!(proprieta in countSUBJ))
						countSUBJ[proprieta]=1;
					else
						countSUBJ[proprieta]=countSUBJ[proprieta]+1;

					#Mantengo un array temporaneo degli ultimi maxSUBJTempSubj elementi analizzati
					if( countSUBJTempSubj>=maxSUBJTempSubj ){
						#print "Replace element: " countSUBJTempSubjSost;
						#print "Previous: " tempCountedSUBJProp[countSUBJTempSubjSost];
						
						#Ottengo l'indice dell'elemeno da sostituire
						indSostSubj=countSUBJTempSubjSostNumber[countSUBJTempSubjSost];

						#Salvo sul file temporaneo l'informazioni, se dovesse servire in seguito
						#print "Salvo: "	subj_to_check;				
						#print subj_to_check >> "tmpFile/countedSUBJProp.txt";
						subj_to_save=indSostSubj;

						if(firstSaveSUBJTemp==0){ #Necessario per la modalità ottimizzata di salvataggio
							system("echo \""subj_to_save"\" >> "dirTmpFile"/"letter"_countedSUBJProp.txt; sync;");  #sync; sync;
							firstSaveSUBJTemp=1;
						}
						else{
							system("sed -i -e \"1i "subj_to_save"\" "dirTmpFile"/"letter"_countedSUBJProp.txt");
							#system("sync");
						}
						
						delete tempCountedSUBJProp[indSostSubj];
						tempCountedSUBJProp[subj_to_check];
						countSUBJTempSubjSostNumber[countSUBJTempSubjSost]=subj_to_check;

						#print "Now: " tempCountedSUBJProp[countSUBJTempSubjSost];
						countSUBJTempSubjSost=((countSUBJTempSubjSost)%maxSUBJTempSubj)+1;

						countSUBJTempSubj++;						
					}
					else{
						countSUBJTempSubj++;
						tempCountedSUBJProp[subj_to_check];
						countSUBJTempSubjSostNumber[countSUBJTempSubj]=subj_to_check;
					}

				}
		
				# TODO: Rimettere per Min, Max, Mean
				#if(subjUnresolv==""){
					#to_check_prop_conc=entitaSUbj"##"ClasseSubj"##"proprieta;
					
					#if(to_check_prop_conc in countedSUBJPropClass)
						#countedSUBJPropClass[to_check_prop_conc] = countedSUBJPropClass[to_check_prop_conc] + 1;
					#else
						#countedSUBJPropClass[to_check_prop_conc] = 1;
				#}
				
				#TODO:Decommentare se sono utili per la Summarization
				#if(!(entitaSUbj in countedSUBJResource)){
			
					#countSUBJREs = countSUBJREs + 1;
			
					#if(subjUnresolv==""){
				
						#if(ClasseSubj in countNumberOfClassAsSUBJ){
							#countNumberOfClassAsSUBJ[ClasseSubj] = countNumberOfClassAsSUBJ[ClasseSubj] + 1;
						#}
						#else
							#countNumberOfClassAsSUBJ[ClasseSubj] = 1;
				
					#}
			
					#countedSUBJResource[entitaSUbj];
				#}
		
				# Verifico l'oggetto
				obj_to_check=entitaObj"##"proprieta;
				gsub("-","(m#i#n#u#s)",obj_to_check); #Necessario per il successivo grep per risorse con -string-
				obj_to_check_found=0;

				if(countOBJTempObj>0 && obj_to_check in tempCountedOBJProp){
					obj_to_check_found=1;
					#print ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>TROVATO IN ARRAY!!!";
					break;
				}
				#system("sleep 2");
									
				#system("stat -c %s tmpFile/countedOBJProp.txt");
				if( obj_to_check_found!=1 && countOBJTempObj>maxOBJTempObj ){
					#startTime=systime();
					#print "Cerco in File"
					#cmd="cat tmpFile/countedOBJProp.txt";
					#if(substr(obj_to_check,1,1)=="-")
						#obj_to_check="\\"obj_to_check;					
					#gensub("/-/","\\-",1,obj_to_check);
					#print "Cerco: "obj_to_check;
					cmd="grep -m 1 -e \""obj_to_check"\" "dirTmpFile"/"letter"_countedOBJProp.txt";
					#print "Comando: " cmd;
					while (cmd | getline line > 0){ 
						#print "Cerco: "obj_to_check;
						#print "    In: "line;
						#if(match(line,obj_to_check)){
							obj_to_check_found=1;
							#print ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>TROVATO!!!";
							break;
						#}
						#system("sleep 2");
					}
					close(cmd);

					#while ((getline < "tmpFile/countedOBJProp.txt") > 0)
					#{
						#print "Cerco: "obj_to_check;
						#print "    In: "$0;
						#if(match($0,obj_to_check)){
							#obj_to_check_found=1;
							#print ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>TROVATO!!!";
							#break;
						#}
						#system("sleep 2");
					#}
					#endTime=systime();
					#diffTime=endTime-startTime;
					#if(diffTime>=1)
						#print "Time Obj Check: " diffTime " --- " obj_to_check;

					#cl=close("tmpFile/countedOBJProp.txt");
				}
				#print "Close OBJ: " cl;

				#print "\nobj_to_check_found: " obj_to_check_found;

				#print "\n";

				if( obj_to_check_found!=1 ){
		
					# Conto quante istanze di una data classe hanno una determinata proprietà, solo se la classe di appartenenenza dell'istanza è nota
					if(objUnresolv==""){
						obj_to_check_classe=ClasseObj"##"proprieta;

						#if(length(ClasseObj)==0){
							#print "moreMinTypeObj: " moreMinTypeObj;
						#}
				
						if(!(obj_to_check_classe in countClassOBJ)){
							# Conto il numero di concetti che hanno una data proprietà e che sono oggetto della proprietà
							if(!(proprieta in concPropOBJ)){
								concPropOBJ[proprieta]=ClasseObj
								#TODO: Riattivare per Domain & Range
								#concPropOBJOneClass[proprieta] = ClasseObj;
							}
							else
								concPropOBJ[proprieta]=concPropOBJ[proprieta]"##"ClasseObj
						
							countClassOBJ[obj_to_check_classe]=1
						}
						else
							countClassOBJ[obj_to_check_classe]=countClassOBJ[obj_to_check_classe]+1				
					}
			
					if(!(proprieta in countOBJ))
						countOBJ[proprieta]=1;
					else
						countOBJ[proprieta]=countOBJ[proprieta]+1;

					#Mantengo un array temporaneo degli ultimi 200 elementi analizzati
					if( countOBJTempObj>=maxOBJTempObj ){
						#print "Replace element: " countOBJTempObjSost;
						#print "Previous: " tempCountedOBJProp[countOBJTempObjSost];
						
						#Ottengo l'indice dell'elemeno da sostituire
						indSostObj=countOBJTempObjSostNumber[countOBJTempObjSost];
						#Salvo sul file temporaneo l'informazioni, se dovesse servire in seguito
						#print "Salvo: "	obj_to_check;				
						#print obj_to_check >> "tmpFile/countedOBJProp.txt";
						obj_to_save=indSostObj;

						if(firstSaveOBJTemp==0){ #Necessario per la modalità ottimizzata di salvataggio
							system("echo \""obj_to_save"\" >> "dirTmpFile"/"letter"_countedOBJProp.txt; sync;");  #sync; sync;
							firstSaveOBJTemp=1;
						}
						else{
							system("sed -i -e \"1i "obj_to_save"\" "dirTmpFile"/"letter"_countedOBJProp.txt");
							#system("sync");
						}

						delete tempCountedOBJProp[indSostObj];
						tempCountedOBJProp[obj_to_check];
						countOBJTempObjSostNumber[countOBJTempObjSost]=obj_to_check;

						#print "Now: " tempCountedOBJProp[countOBJTempObjSost];
						countOBJTempObjSost=((countOBJTempObjSost)%maxOBJTempObj)+1;

						countOBJTempObj++;
						
					}
					else{
						countOBJTempObj++;
						tempCountedOBJProp[obj_to_check];
						countOBJTempObjSostNumber[countOBJTempObj]=obj_to_check;
					}

				}
				
				# TODO: Rimettere per Min, Max, Mean
				#if(objUnresolv==""){
					#to_check_prop_conc=entitaObj"##"ClasseObj"##"proprieta;
					#if(to_check_prop_conc in countedOBJPropClass)
						#countedOBJPropClass[to_check_prop_conc] = countedOBJPropClass[to_check_prop_conc] + 1;
					#else
						#countedOBJPropClass[to_check_prop_conc] = 1;
				#}
				
				#TODO:Decommentare se sono utili per la Summarization
				#if(!(entitaObj in countedOBJResource)){
			
					#countOBJREs = countOBJREs + 1;
			
					#if(objUnresolv==""){
			
						#if(ClasseObj in countNumberOfClassAsOBJ){
							#countNumberOfClassAsOBJ[ClasseObj] = countNumberOfClassAsOBJ[ClasseObj] + 1;
						#}
						#else
							#countNumberOfClassAsOBJ[ClasseObj] = 1;
					#}
			
					#countedOBJResource[entitaObj];
				#}
			}

		}
		
		if(!(proprieta in countProp)){
			# Conteggio la presenza della proprietà
			countProp[proprieta]=1;
		}
		else{
			countProp[proprieta]=countProp[proprieta]+1;
		}
		
		#print "Object: " entitaSUbj " - " proprieta " - " entitaObj
		#print relation " => " relationCount[relation];
		
	triple_lette++;

	#endTime=systime();
	#diffTime=endTime-startTime;
	#if(diffTime>=1){
		#print "Time: " diffTime " ( Start: " startTime ",End: " endTime ")";
		#print "(" triple_lette ") - Tripla: " Triple "\n";
	#}
}
else{ #Elaboro Datatype Properties
	
	# Elaboro ogni tripla del file
	#startTime=systime();
	#Triple=$0;
	
	# Stampa di controllo
	#if(triple_lette%10000==0 && triple_lette!=0)
		#print triple_lette " triple lette..." #>> "log.txt";
	
	#fflush(""); # Avviando gawk, funziona sicuro
		
	#Splitto utilizzando una funzione che mi garantisce che i ## su cui avviene lo split non siano parte di un eventuale letterale, ma sono a tutti gli effetti separatori da me inseriti
	#TODO: Vedere come usare delete con gawk e farlo andare, perchè dovrebbe essere 3 volte più veloce di questa operazione		
	split("", start); 
	split("", matches);
	split("", numMatch);
	split("", isnotCapuring);
	thereisnotCapuring=FindAllMatches($0,"##","(\"(.*)\")", start, matches, numMatch, isnotCapuring);

	#Prendo tutte le risorse o datatype
	entityType = "";

	if(numMatch["match"]==2){ #Non ho il datatype
		#La prima parte della stringa è il soggetto
		entitaSUbj=substr($0, 1, start[1]-1);
		#La seconda parte della stringa è la proprietà
		proprieta=substr($0, start[1]+2, start[2]-(start[1]+2));
		#La terza parte della stringa è l'oggetto o letterale		
		entitaObj = substr($0, start[2]+2);
	}
	else if(numMatch["match"]==3){#Ho il datatype
		#La prima parte della stringa è il soggetto
		entitaSUbj=substr($0, 1, start[1]-1);
		#La seconda parte della stringa è la proprietà
		proprieta=substr($0, start[1]+2, start[2]-(start[1]+2));
		#La terza parte della stringa è l'oggetto o letterale		
		entitaObj = substr($0, start[2]+2, start[3]-(start[2]+2));
		#La quarta parte della stringa, se presente, è il datatype
		entityType = substr($0, start[3]+2);
	}

	if(thereisnotCapuring==1){ #E' un letterale, rimuovo l'apice iniziale e finale
		gsub("^\"","",entitaObj);
		gsub("\"$","",entitaObj);
	}
		
		if(!(proprieta in currDTProperties)){ #Se la proprietà non è nell'elenco delle proprietà (quindi non presa dall'ontologia)
			if(!(proprieta in stored_equiv_dt_prop)){
				#print "Prendo l'equivalent property per: " proprieta;
				equivProp = getEquProp(equProp,proprieta);
				stored_equiv_dt_prop[proprieta]=equivProp;
				#print "Presa: " equivProp;
			}
			else{
				#print "PRENDO EQUIVALENT DALLA MEMORIA PER: " proprieta;
				equivProp = stored_equiv_dt_prop[proprieta];
				#print "Ripresa dalla memoria: " equivProp;
			}
		
			# Associo la proprietà equivalente alla proprietà
			proprieta = equivProp;
		
			# Salvo la proprietà se nuova
			if(!(proprieta in newDTProperties)){
				split("", URI_El);
				lengthArray=split(proprieta,URI_El,"/"); # Splitto lo statement
				proprieta_nome=URI_El[lengthArray];
				newDTProperties[proprieta]=proprieta_nome; # URI => LocalName
			}
		}
		
		#TODO: Vedere come usare delete con gawk e farlo andare, perchè dovrebbe essere 3 volte più veloce di questa operazione
		
		subjUnresolv="";
		objUnresolv="";
		ClasseSubj="";
		DataTypeObj="";
		moreMinTypeSubj=0;
		
		# Risolvo le informazioni sui tipi minimi dei concetti e salvo il conteggio del path
		if( entitaSUbj in minType ){
			#Verifico se per questa risorsa vi sono più tipi minimi, nel qual caso costruisco r-pattern per ognuno di questi
			split("", minTpMultiSubj);
			lengthArrayMultiSubj=split(minType[entitaSUbj],minTpMultiSubj,"#-#");		

			if(lengthArrayMultiSubj>1){ #Vi sono più tipi minimi per questa risorsa
				
				moreMinTypeSubj=1;
			}
			else{
				relation=minType[entitaSUbj];
				ClasseSubj=minType[entitaSUbj];
			}
		}
		else # TODO: Vedere come gestire i tipi non risolti, probabilmente a parte
		{
			# Verifico se il tipo è risolvibile con DR
			if(proprieta in DRRel){
				split("", DRProp);
				split(DRRel[proprieta],DRProp,"##");
				
				relation=DRProp[1]"##DR"; # Soggetto della proprietà
				ClasseSubj=DRProp[1];

				countToSave=entitaSUbj"##"DRProp[1];
				
				if(!(countToSave in instCounted)){
				
					if(entitaSUbj in Ukn){
						split("", uknToSottr);
						lengthArray=split(Ukn[entitaSUbj],uknToSottr,"##");
						
						#print "Split UKN: " uknToSottr[1];

						for( i=1; i<=lengthArray; i++){
							count[uknToSottr[i]]=count[uknToSottr[i]]-1;
							#print "Scalo: " uknToSottr[i];
						}
						
					}
					
					count[DRProp[1]] = count[DRProp[1]] + 1;

					#Più proprietà possono avere una risoluzione di tipo analoga, ma la stessa risorsa va contata una sola volta per ogni tipo, indipendentemente dal numero di proprietà che risolvono il tipo in quel concetto e se è oggetto o soggetto
					instCounted[countToSave];
				}
				
			}
			else if(entitaSUbj in Ukn)
			{
				relation=Ukn[entitaSUbj]"##UKN";
				ClasseSubj=Ukn[entitaSUbj];
			}
			else
			{
				relation="http://www.w3.org/2002/07/owl#Thing";
				
				subjUnresolv = entitaSUbj;
			}
		}
		
		if(moreMinTypeSubj==0){
			relation=relation"##"proprieta"##";
		}

		for(iMulSubj=1; iMulSubj<=lengthArrayMultiSubj; iMulSubj++){
			
			if(moreMinTypeSubj>=1){
				relation=minTpMultiSubj[iMulSubj];		
				ClasseSubj=minTpMultiSubj[iMulSubj];
				relation=relation"##"proprieta"##";
			}	

			if(entityType != ""){ # Se è indicato il tipo
			
				relation=relation""entityType;
				DataTypeObj=entityType;

				countToSave=entitaObj"##"DataTypeObj;

				if(!(countToSave in InstDtCounted)){
					countDt[DataTypeObj] = countDt[DataTypeObj] + 1;
				
					#Più proprietà possono avere una risoluzione di datatype analoga, ma la stessa risorsa va contata una sola volta per ogni datatype, indipendentemente dal numero di proprietà che risolvono il tipo in quel datatype
					InstDtCounted[countToSave];
				}

			}
			else{
		
				# Verifico se il tipo è risolvibile con DR
				if(proprieta in DRRel){
					split("", DRProp);
					split(DRRel[proprieta],DRProp,"##");
				
					relation=relation""DRProp[2]"##DR-T"; # Oggetto della proprietà
					DataTypeObj=DRProp[2];

					countToSave=entitaObj"##"DataTypeObj;

					if(!(countToSave in InstDtCounted)){
						countDt[DataTypeObj] = countDt[DataTypeObj] + 1;
				
						#Più proprietà possono avere una risoluzione di datatype analoga, ma la stessa risorsa va contata una sola volta per ogni datatype, indipendentemente dal numero di proprietà che risolvono il tipo in quel datatype
						InstDtCounted[countToSave];
					}
				}
				else
				{
					relation=relation"Ukn_Type";
				
					objUnresolv = entitaObj;
				}
			}
		
			if(!(relation in relationDTCount))
			{
				relationDTCount[relation]=1;
			}
			else
			{
				relationDTCount[relation]=relationDTCount[relation]+1;
			}
		
			# Salvo eventuali informazioni sulle risorse non risolvibili, da mostrare come esempio
			#if(relation in egDTUnresolved){

				#if(subjUnresolv!=""){
					#split("", UrnRelation);
					#lengthArray=split(egDTUnresolved[relation],UrnRelation,"###"); # Splitto lo statement
					#to_check = subjUnresolv" (SUBJ)";
				
					#for(i=1; i<=lengthArray; i++){
						#UrnRelationCheck[UrnRelation[i]];
					#}
				
					#if(!(to_check in UrnRelationCheck))
						#egDTUnresolved[relation] = egDTUnresolved[relation]"###"subjUnresolv" (SUBJ)";
				#}
			
				#if(objUnresolv!=""){
					#split("", UrnRelation);
					#lengthArray=split(egDTUnresolved[relation],UrnRelation,"###"); # Splitto lo statement
					#to_check = objUnresolv" (OBJ)";
				
					#for(i=1; i<=lengthArray; i++){
						U#rnRelationCheck[UrnRelation[i]];
					#}
				
					#if(!(to_check in UrnRelationCheck))
						#egDTUnresolved[relation] = egDTUnresolved[relation]"###"objUnresolv" (OBJ)";
				#}
			
			#}
			#else{
			
				#if(subjUnresolv!="")
					#egDTUnresolved[relation] = subjUnresolv" (SUBJ)";
			
				#if(objUnresolv!="")
					#egDTUnresolved[relation] = objUnresolv" (OBJ)";
			
			#}
		
			# Conto quante istanze hanno una determinata proprietà
			# Verifico il soggetto
			subj_to_check=entitaSUbj"##"proprieta;
			gsub("-","(m#i#n#u#s)",subj_to_check); #Necessario per il successivo grep per risorse con -string-
			subj_to_check_found=0;

			if(countSUBJTempSubjDt>0 && subj_to_check in tempCountedSUBJPropDt){
				subj_to_check_found=1;
				#print ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>TROVATO IN ARRAY!!!";
				break;
			}
			#system("sleep 2");
									
			#system("stat -c %s tmpFile/countedSUBJPropDt.txt");
			if( subj_to_check_found!=1 && countSUBJTempSubjDt>maxSUBJTempSubjDt ){
					#startTime=systime();
					#print "Cerco in File"
					#cmd="cat tmpFile/countedSUBJPropDt.txt";
					#if(substr(subj_to_check,1,1)=="-")
						#subj_to_check="\\"subj_to_check;					
					#gensub("/-/","\\-",1,subj_to_check);
					#print "Cerco: "subj_to_check;
					cmd="grep -m 1 -e \""subj_to_check"\" "dirTmpFile"/"letter"_countedSUBJPropDt.txt";
					#print "Comando: " cmd;
					while (cmd | getline line > 0){ 
						#print "Cerco: "subj_to_check;
						#print "    In: "line;
						#if(match(line,subj_to_check)){
							subj_to_check_found=1;
							#print ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>TROVATO!!!";
							break;
						#}
						#system("sleep 2");
					}
					close(cmd);
					
					#while ((getline < "tmpFile/countedSUBJPropDt.txt") > 0)
					#{
						#print "Cerco: "subj_to_check;
						#print "    In: "$0;
						#if(match($0,subj_to_check)){
							#subj_to_check_found=1;
							#print ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>TROVATO!!!";
							#break;
					#}
					#system("sleep 2");
				#}
				#endTime=systime();
				#diffTime=endTime-startTime;
				#if(diffTime>=1)
					#print "Time Subj Check Dt: " diffTime " --- " subj_to_check;

				#cl=close("tmpFile/countedSUBJPropDt.txt");
			}
			#print "Close: " cl;

			#print "\nsubj_to_check_found: " subj_to_check_found;

			#print "\n";
			if(subj_to_check_found!=1){
			
				# Conto quante istanze di una data classe hanno una determinata proprietà, solo se la classe di appartenenenza dell'istanza è nota
				if(subjUnresolv==""){
					subj_to_check_classe=ClasseSubj"##"proprieta;
				
					if(!(subj_to_check_classe in countClassDTSUBJ)){
						# Conto il numero di concetti che hanno una data proprietà e che sono soggetto della proprietà
						if(!(proprieta in concPropDTSUBJ)){
							concPropDTSUBJ[proprieta]=ClasseSubj
							#TODO: Riattivare per Domain & Range
							#concPropDTSUBJOneClass[proprieta] = ClasseSubj;
						}
						else
							concPropDTSUBJ[proprieta]=concPropDTSUBJ[proprieta]"##"ClasseSubj
						
						countClassDTSUBJ[subj_to_check_classe]=1
					}
					else
						countClassDTSUBJ[subj_to_check_classe]=countClassDTSUBJ[subj_to_check_classe]+1				
				}
			
				if(!(proprieta in countDTSUBJ))
					countDTSUBJ[proprieta]=1
				else
					countDTSUBJ[proprieta]=countDTSUBJ[proprieta]+1
				
				#Mantengo un array temporaneo degli ultimi maxSUBJTempSubjDt elementi analizzati
				if( countSUBJTempSubjDt>=maxSUBJTempSubjDt ){
					#print "Replace element: " countSUBJTempSubjSostDt;
					#print "Previous: " tempCountedSUBJPropDt[countSUBJTempSubjSostDt];
					
					#Ottengo l'indice dell'elemeno da sostituire
					indSostSubjDt=countSUBJTempSubjSostNumberDt[countSUBJTempSubjSostDt];
					#Salvo sul file temporaneo l'informazioni, se dovesse servire in seguito
					#print "Salvo: "	subj_to_check;				
					#print subj_to_check >> "tmpFile/countedSUBJPropDt.txt";
					subj_to_save=indSostSubjDt;

					if(firstSaveSUBJTempDt==0){ #Necessario per la modalità ottimizzata di salvataggio
						system("echo \""subj_to_save"\" >> "dirTmpFile"/"letter"_countedSUBJPropDt.txt; sync;");  #sync; sync;
						firstSaveSUBJTempDt=1;
					}
					else{
						system("sed -i -e \"1i "subj_to_save"\" "dirTmpFile"/"letter"_countedSUBJPropDt.txt");
						#system("sync");
					}
				
					delete tempCountedSUBJPropDt[indSostSubjDt];
					tempCountedSUBJPropDt[subj_to_check];
					countSUBJTempSubjSostNumberDt[countSUBJTempSubjSostDt]=subj_to_check;

					#print "Now: " tempCountedSUBJPropDt[countSUBJTempSubjSostDt];
					countSUBJTempSubjSostDt=((countSUBJTempSubjSostDt)%maxSUBJTempSubjDt)+1;

					countSUBJTempSubjDt++;
					
				}
				else{
					countSUBJTempSubjDt++;
					tempCountedSUBJPropDt[subj_to_check];
					countSUBJTempSubjSostNumberDt[countSUBJTempSubjDt]=subj_to_check;
				}
			}
			
			# TODO: Rimettere per Min, Max, Mean
			#if(subjUnresolv==""){
				#to_check_prop_conc=entitaSUbj"##"ClasseSubj"##"proprieta;
				#if(to_check_prop_conc in countedDTSUBJPropClass)
					#countedDTSUBJPropClass[to_check_prop_conc] = countedDTSUBJPropClass[to_check_prop_conc] + 1;
				#else
					#countedDTSUBJPropClass[to_check_prop_conc] = 1;
			#}
		
			#if(!(entitaSUbj in countedDTSUBJResource)){
			
				#countDTSUBJREs = countDTSUBJREs + 1;
			
				#if(subjUnresolv==""){
				
					#if(ClasseSubj in countNumberOfClassAsDTSUBJ){
						#countNumberOfClassAsDTSUBJ[ClasseSubj] = countNumberOfClassAsDTSUBJ[ClasseSubj] + 1;
					#}
					#else
						#countNumberOfClassAsDTSUBJ[ClasseSubj] = 1;
				
				#}
			
				#countedDTSUBJResource[entitaSUbj];
			#}
		
			# Verifico l'oggetto
			if(objUnresolv==""){ #Solo se il DataType è noto, altrimenti non è rilevante
				obj_to_check=entitaObj"##"proprieta; #Stringhe di lingue (@ln) diverse sono differenziate
				gsub("-","(m#i#n#u#s)",obj_to_check); #Necessario per il successivo grep per risorse con -string-
				#gsub("\\.","(p#o#i#n#t)",obj_to_check); #Necessario per il successivo grep per risorse con -string-	
				obj_to_check_found=0;

				if(countOBJTempObjDt>0 && obj_to_check in tempCountedOBJPropDt){
					obj_to_check_found=1;
					#print ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>TROVATO IN ARRAY!!!";
					break;
				}
				#system("sleep 2");
								
				#system("stat -c %s tmpFile/countedOBJPropDt.txt");
				if( obj_to_check_found!=1 && countOBJTempObjDt>maxOBJTempObjDt ){
					#startTime=systime();
					
					#cmd="cat tmpFile/countedOBJPropDt.txt";
					#gsub("\\.","\\\.",obj_to_check);
					#if(substr(obj_to_check,1,1)=="-")
						#obj_to_check="\\"obj_to_check;
					#gensub("-","/\\-/",1,obj_to_check);				
					#print "Cerco: "obj_to_check;
					cmd="grep -m 1 -e \""obj_to_check"\" "dirTmpFile"/"letter"_countedOBJPropDt.txt";
					#print "Comando: " cmd;
					while (cmd | getline line > 0){ 
						#print "Cerco: "obj_to_check;
						#print "    In: "line;
						#if(match(line,obj_to_check)){
							obj_to_check_found=1;
							#print ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>TROVATO IN FILE!!!";
							break;
						#}
						#system("sleep 2");
					}
					close(cmd);

					#while ((getline < "tmpFile/countedOBJPropDt.txt") > 0)
					#{
						#print "Cerco: "obj_to_check;
						#print "    In: "$0;
						#if(match($0,obj_to_check)){
							#obj_to_check_found=1;
							#print ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>TROVATO!!!";
							#break;
						#}
						#system("sleep 2");
					#}
					#endTime=systime();
					#diffTime=endTime-startTime;
					#if(diffTime>=1)
						#print "Time Obj Check Dt: " diffTime " --- " obj_to_check;

					#cl=close("tmpFile/countedOBJPropDt.txt");
				}
				#print "Close OBJ: " cl;

				#print "\nobj_to_check_found: " obj_to_check_found;

				#print "\n";

				if( obj_to_check_found!=1 ){
		
					# Conto quanti datatype hanno una determinata proprietà, solo se il datatype dell'istanza è noto (Verificato dall'if di accesso al macroblocco sulla verifica dell'oggetto)
					obj_to_check_classe=DataTypeObj"##"proprieta;
				
					if(!(obj_to_check_classe in countClassDTOBJ)){
						# Conto il numero di datatype che hanno una data proprietà e che sono oggetto della proprietà
						if(!(proprieta in concPropDTOBJ)){
							concPropDTOBJ[proprieta]=DataTypeObj
							#TODO: Riattivare per Domain & Range
							#concPropDTOBJOneClass[proprieta] = DataTypeObj;
						}
						else
							concPropDTOBJ[proprieta]=concPropDTOBJ[proprieta]"##"DataTypeObj
						
						countClassDTOBJ[obj_to_check_classe]=1
					}
					else
						countClassDTOBJ[obj_to_check_classe]=countClassDTOBJ[obj_to_check_classe]+1				
			
					if(!(proprieta in countDTOBJ))
						countDTOBJ[proprieta]=1
					else
						countDTOBJ[proprieta]=countDTOBJ[proprieta]+1
				
					#Mantengo un array temporaneo degli ultimi maxOBJTempObj elementi analizzati
					if( countOBJTempObjDt>=maxOBJTempObjDt ){
						#print "Replace element: " countOBJTempObjSostDt;
						#print "Previous: " tempCountedOBJPropDt[countOBJTempObjSostDt];
					
						#Ottengo l'indice dell'elemeno da sostituire
						indSostObjDt=countOBJTempObjSostNumberDt[countOBJTempObjSostDt];
						#Salvo sul file temporaneo l'informazioni, se dovesse servire in seguito
						#print "Salvo: "	obj_to_check;				
						#print obj_to_check >> "tmpFile/countedOBJPropDt.txt";
						obj_to_save=indSostObjDt;

						if(firstSaveOBJTempDt==0){ #Necessario per la modalità ottimizzata di salvataggio
							system("echo \""obj_to_save"\" >> "dirTmpFile"/"letter"_countedOBJPropDt.txt; sync;");  #sync; sync;
							firstSaveOBJTempDt=1;
						}
						else{
							system("sed -i -e \"1i "obj_to_save"\" "dirTmpFile"/"letter"_countedOBJPropDt.txt;");
							#system("sync");

						}

						delete tempCountedOBJPropDt[indSostObjDt];
						tempCountedOBJPropDt[obj_to_check];
						countOBJTempObjSostNumberDt[countOBJTempObjSostDt]=obj_to_check;

						#print "Now: " tempCountedOBJPropDt[countOBJTempObjSostDt];
						countOBJTempObjSostDt=((countOBJTempObjSostDt)%maxOBJTempObjDt)+1;

						countOBJTempObjDt++;
					
					}
					else{
						countOBJTempObjDt++;
						tempCountedOBJPropDt[obj_to_check];
						countOBJTempObjSostNumberDt[countOBJTempObjDt]=obj_to_check;
					}
				}
			}
			
			# TODO: Rimettere per Min, Max, Mean
			#if(objUnresolv==""){
				#to_check_prop_conc=entitaObj"##"DataTypeObj"##"proprieta;
				#if(to_check_prop_conc in countedDTOBJPropClass)
					#countedDTOBJPropClass[to_check_prop_conc] = countedDTOBJPropClass[to_check_prop_conc] + 1;
				#else
					#countedDTOBJPropClass[to_check_prop_conc] = 1;
			#}
		
			#TODO:Decommentare se sono utili per la Summarization
			#if(!(entitaObj in countedDTOBJResource)){
			
				#countDTOBJREs = countDTOBJREs + 1;
			
				#if(objUnresolv==""){
			
					#if(DataTypeObj in countNumberOfClassAsDTOBJ){
						#countNumberOfClassAsDTOBJ[DataTypeObj] = countNumberOfClassAsDTOBJ[DataTypeObj] + 1;
					#}
					#else
						#countNumberOfClassAsDTOBJ[DataTypeObj] = 1;
				#}
			
				#countedDTOBJResource[entitaObj];
			#}
		}

		if(!(proprieta in countDTProp)){
			# Conteggio la presenza della proprietà
			countDTProp[proprieta]=1;
		}
		else{
			countDTProp[proprieta]=countDTProp[proprieta]+1;
		}
		
		#print "DataType: " entitaSUbj " - " proprieta " - " entitaObj " >> " DataTypeObj
		
	triple_lette++;

	#endTime=systime();
	#diffTime=endTime-startTime;
	#if(diffTime>=1){
		#print "Time: " diffTime " ( Start: " startTime ",End: " endTime ")";
		#print "(" triple_lette ") - Tripla: " Triple "\n";
	#}	

}

}

END { 

	#OBJECT PROPERTIES
	
	# Salvo tutte le informazioni su file

	system("touch "destDir"/"letter"_countProp.txt"); #Creo il file così da averlo, anche se vuoto	
	for (var in countProp)
	{	
		print var "##" countProp[var] >> destDir"/"letter"_countProp.txt";
	}

	system("touch "destDir"/"letter"_concPropSUBJ.txt"); #Creo il file così da averlo, anche se vuoto	
	for (var in concPropSUBJ)
	{
		print var "##" concPropSUBJ[var] >> destDir"/"letter"_concPropSUBJ.txt";
	}

	system("touch "destDir"/"letter"_concPropOBJ.txt"); #Creo il file così da averlo, anche se vuoto	
	for (var in concPropOBJ)
	{
		print var "##" concPropOBJ[var] >> destDir"/"letter"_concPropOBJ.txt";
	}
	
	# Salvo le informazioni su conteggi
	#Object Properties Count

	#Conteggio anche le risorse di ogni proprietà, così da valutare la "Commoness"
	system("touch "destDir"/"letter"_countClassSUBJ.txt"); #Creo il file così da averlo, anche se vuoto	
	for (varCountClassSUBJ in countClassSUBJ)
	{
		split("",clCount);
		split(varCountClassSUBJ,clCount,"##")		
		
		print varCountClassSUBJ "##" countClassSUBJ[varCountClassSUBJ] >> destDir"/"letter"_countClassSUBJ.txt";
	}
	
	#Conteggio anche le risorse di ogni proprietà, così da valutare la "Commoness"
	system("touch "destDir"/"letter"_countClassOBJ.txt"); #Creo il file così da averlo, anche se vuoto	
	for (varCountClassOBJ in countClassOBJ)
	{
		split("",clCount);
		split(varCountClassOBJ,clCount,"##")
		
		print varCountClassOBJ "##" countClassOBJ[varCountClassOBJ] >> destDir"/"letter"_countClassOBJ.txt";
	}
	
	system("touch "destDir"/"letter"_countSUBJ.txt"); #Creo il file così da averlo, anche se vuoto	
	for (varCountSUBJ in countSUBJ)
	{
		print varCountSUBJ "##" countSUBJ[varCountSUBJ] >> destDir"/"letter"_countSUBJ.txt";
	}

	system("touch "destDir"/"letter"_countOBJ.txt"); #Creo il file così da averlo, anche se vuoto		
	for (varCountOBJ in countOBJ)
	{
		print varCountOBJ "##" countOBJ[varCountOBJ] >> destDir"/"letter"_countOBJ.txt";
	}

	system("touch "destDir"/"letter"_newObjProperties.txt"); #Creo il file così da averlo, anche se vuoto		
	for (varNObj in newObjProperties)
	{
		print varNObj "##" newObjProperties[varNObj] >> destDir"/"letter"_newObjProperties.txt";
	}

	system("touch "destDir"/"letter"_relationCount.txt"); #Creo il file così da averlo, anche se vuoto		
	for (var in relationCount)
	{
		print var "##" relationCount[var] >> destDir"/"letter"_relationCount.txt";
	}
	
	
	#TODO:Decommentare se sono utili per la Summarization
	#for (varUnr in egUnresolved)
	#{
		#print varUnr " => " egUnresolved[varUnr] >> "relation_info/relationEgUnresolved"FILENAME".txt";
	#}

	system("touch "destDir"/"letter"_countConcepts.txt"); #Creo il file così da averlo, anche se vuoto		
	for (var in count)
	{
		to_Print = var "##" count[var]
			
		print  to_Print >> destDir"/"letter"_countConcepts.txt";
	}
	
	# TODO: Rimettere per Min, Max, Mean
	#print "NUMERO DI VOLTE MINIMO, MASSIMO E MEDIO IN CUI UNA PROPRIETA E ASSOCIATA AD UN CONCETTO (SE NOTO) COME SOGGETTO (OVVERO IN USCITA)" >> "relation_info/Conteggi"FILENAME".txt";
	
	# Inizializzo gli array per la valutazione
	#for(countedConcPropSubj in countClassSUBJ){
		#min_count_class_subj[countedConcPropSubj] = totale;
		#max_count_class_subj[countedConcPropSubj] = -1;
	#}
	
	#for(countedSubjPropVar in countedSUBJPropClass){
		
		# Recupero il concetto associato alla risorsa
		#split("",propClassTot);
		#split(countedSubjPropVar,propClassTot,"##");
		#classe = propClassTot[2];
		#proprieta = propClassTot[3];
		#to_check_prop_classe = classe"##"proprieta;
		
		#if(min_count_class_subj[to_check_prop_classe]>countedSUBJPropClass[countedSubjPropVar])
			#min_count_class_subj[to_check_prop_classe]=countedSUBJPropClass[countedSubjPropVar];
		
		#if(max_count_class_subj[to_check_prop_classe]<countedSUBJPropClass[countedSubjPropVar])
			#max_count_class_subj[to_check_prop_classe]=countedSUBJPropClass[countedSubjPropVar];
		
	#}
	
	#for (min_max_mean_var in min_count_class_subj){
		
		#media = (min_count_class_subj[min_max_mean_var]+max_count_class_subj[min_max_mean_var])/2;
		
		#print min_max_mean_var " => " "MINIMO: " min_count_class_subj[min_max_mean_var] ", MASSIMO: " max_count_class_subj[min_max_mean_var] ", MEDIA: " media >> "relation_info/Conteggi"FILENAME".txt";
	
	#}
	
	#print "\n\n" >> "relation_info/Conteggi"FILENAME".txt";

	#print "NUMERO DI VOLTE MINIMO, MASSIMO E MEDIO IN CUI UNA PROPRIETA E ASSOCIATA AD UN DATATYPE (SE NOTO) COME OGGETTO (OVVERO IN ENTRATA)" >> "relation_info/Conteggi"FILENAME".txt";
	
	# Inizializzo gli array per la valutazione
	#for(countedConcPropObj in countClassOBJ){
		#min_count_class_obj[countedConcPropObj] = totale;
		#max_count_class_obj[countedConcPropObj] = -1;
	#}
	
	#for(countedObjPropVar in countedOBJPropClass){
		
		# Recupero il concetto associato alla risorsa
		#split("",propClassTot);
		#split(countedObjPropVar,propClassTot,"##");
		#classe = propClassTot[2];
		#proprieta = propClassTot[3];
		#to_check_prop_classe = classe"##"proprieta;
		
		#if(min_count_class_obj[to_check_prop_classe]>countedOBJPropClass[countedObjPropVar])
			#min_count_class_obj[to_check_prop_classe]=countedOBJPropClass[countedObjPropVar];
		
		#if(max_count_class_obj[to_check_prop_classe]<countedOBJPropClass[countedObjPropVar])
			#max_count_class_obj[to_check_prop_classe]=countedOBJPropClass[countedObjPropVar];
		
	#}

	#for (min_max_mean_var_obj in min_count_class_obj){
		
		#media = (min_count_class_obj[min_max_mean_var_obj]+max_count_class_obj[min_max_mean_var_obj])/2;
		
		#print min_max_mean_var_obj " => " "MINIMO: " min_count_class_obj[min_max_mean_var_obj] ", MASSIMO: " max_count_class_obj[min_max_mean_var_obj] ", MEDIA: " media >> "relation_info/Conteggi"FILENAME".txt";
	
	#}
	
	#TODO:Decommentare se sono utili per la Summarization
	# "Inferisco" le relazioni in base all'analisi
	# Esistenziale
	#print "\n\n" >> "relation_info/Conteggi"FILENAME".txt";
	#print "ESISTENZIALE" >> "relation_info/Conteggi"FILENAME".txt";
	#minCardin = "";
	#for (varCountClassSUBJ in countClassSUBJ)
	#{
		#split("",clCountSubj);
		#split(varCountClassSUBJ,clCountSubj,"##")
		
		#percentuale = (countClassSUBJ[varCountClassSUBJ]/countNumberOfClassAsSUBJ[clCountSubj[1]])*100;
		
		#print "VALUTO - SUBJ: " varCountClassSUBJ " Valori: countClassSUBJ[varCountClassSUBJ] - "  countClassSUBJ[varCountClassSUBJ] " - countNumberOfClassAsSUBJ[clCountSubj[1]] " countNumberOfClassAsSUBJ[clCountSubj[1]]
		
		#if( percentuale==100 ){ #Tutte le risorse di questa classe hanno questa proprietà
		
			#for (varCountClassOBJ in countClassOBJ)
			#{
				#split("",clCountObj);
				#split(varCountClassOBJ,clCountObj,"##")
				
				#if( clCountObj[2] == clCountSubj[2] ){
				
					#percentuale = (countClassOBJ[varCountClassOBJ]/countNumberOfClassAsOBJ[clCountObj[1]])*100;
					
					#print "VALUTO - OBJ: " varCountClassOBJ" Valori: countClassOBJ[varCountClassOBJ] - "  countClassOBJ[varCountClassOBJ] " - countNumberOfClassAsSUBJ[clCountObj[1]] " countNumberOfClassAsOBJ[clCountObj[1]]
					
					#if( percentuale==100 ){ #Tutte le risorse di questa classe hanno questa proprietà
						
						#print clCountSubj[1] " someValuesFrom [" clCountObj[2] "] " clCountObj[1] >> "relation_info/Conteggi"FILENAME".txt";
						
						# MinCardinality
						#minCardin = minCardin"\n"clCountSubj[1] " minCardinality [" clCountObj[2] "] (" min_count_class_subj[varCountClassSUBJ] ") "clCountObj[1]
					#}
				#}
			#}
		#}
		
	#}
	
	# MinCardinality
	#if(minCardin!=""){
	
		#print "\n\n" >> "relation_info/Conteggi"FILENAME".txt";
		#print "MINCARDINALITY" >> "relation_info/Conteggi"FILENAME".txt";
		
		#print minCardin >> "relation_info/Conteggi"FILENAME".txt";
	#}
	
	# Domain & Range
	#print "\n\n" >> "relation_info/Conteggi"FILENAME".txt";
	#print "DOMAIN & RANGE" >> "relation_info/Conteggi"FILENAME".txt";
	#for(countPropCOncSUBJ in concPropSUBJ){
		
		# La proprietà ha solo un concetto come dominio
		#if( concPropSUBJ[countPropCOncSUBJ]==1 ){
			
			#if( countPropCOncSUBJ in concPropOBJ ){
				
				# La proprietà ha solo un concetto come range
				#if( concPropOBJ[countPropCOncSUBJ]==1 ){
					
					#print concPropSUBJOneClass[countPropCOncSUBJ] " " countPropCOncSUBJ " " concPropOBJOneClass[countPropCOncSUBJ]  >> "relation_info/Conteggi"FILENAME".txt";
				#}
			
			#}
		
		#}
	
	#}
	
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
		 
		 #print toPrint >> "relation_info/countConceptsReportDRResolution"FILENAME".txt";
		 
		 # Salvo la radice
		 #split("", URI_El);
		 #lengthArray=split(rootSubConc[1],URI_El,"/"); # Splitto lo statement
			
		 #risorsa_nome=substr(URI_El[lengthArray], 0, length(URI_El[lengthArray]));
		 
		 #print risorsa_nome "" toPrintCount >> "relation_info/countConceptsReportDRResolution"FILENAME".txt";
		
	#}

	#DATATYPE PROPERTIES
	# Salvo tutte le informazioni su file

	system("touch "destDirDt"/"letter"_countDTProp.txt"); #Creo il file così da averlo, anche se vuoto		
	for (varCDT in countDTProp)
	{
		print varCDT "##" countDTProp[varCDT] >> destDirDt"/"letter"_countDTProp.txt";
	}

	system("touch "destDirDt"/"letter"_concPropDTSUBJ.txt"); #Creo il file così da averlo, anche se vuoto	
	for (varCDT in concPropDTSUBJ)
	{
		print varCDT "##" concPropDTSUBJ[varCDT] >> destDirDt"/"letter"_concPropDTSUBJ.txt";
	}

	system("touch "destDirDt"/"letter"_concPropDTOBJ.txt"); #Creo il file così da averlo, anche se vuoto	
	for (varCDT in concPropDTOBJ)
	{
		print varCDT "##" concPropDTOBJ[varCDT] >> destDirDt"/"letter"_concPropDTOBJ.txt";
	}
		
	# Salvo le informazioni su conteggi - DataType Properties

	#Conteggio anche le risorse di ogni proprietà, così da valutare la "Commoness"
	system("touch "destDirDt"/"letter"_countClassDTSUBJ.txt"); #Creo il file così da averlo, anche se vuoto	
	for (varCountClassDTSUBJ in countClassDTSUBJ)
	{
		split("",clCount);
		split(varCountClassDTSUBJ,clCount,"##")

		print varCountClassDTSUBJ "##" countClassDTSUBJ[varCountClassDTSUBJ] >> destDirDt"/"letter"_countClassDTSUBJ.txt";
	}	
	
	#Conteggio anche le risorse di ogni proprietà, così da valutare la "Commoness"
	system("touch "destDirDt"/"letter"_countClassDTOBJ.txt"); #Creo il file così da averlo, anche se vuoto	
	for (varCountClassDtOBJ in countClassDTOBJ)
	{
		split("",clCount);
		split(varCountClassDtOBJ,clCount,"##")

		print varCountClassDtOBJ "##" countClassDTOBJ[varCountClassDtOBJ] >> destDirDt"/"letter"_countClassDTOBJ.txt";
	}

	system("touch "destDirDt"/"letter"_countDTSUBJ.txt"); #Creo il file così da averlo, anche se vuoto	
	for (varCountDTSUBJ in countDTSUBJ)
	{	
		print varCountDTSUBJ "##" countDTSUBJ[varCountDTSUBJ] >> destDirDt"/"letter"_countDTSUBJ.txt";
	}

	system("touch "destDirDt"/"letter"_countDTOBJ.txt"); #Creo il file così da averlo, anche se vuoto		
	for (varCountDTOBJ in countDTOBJ)
	{
		print varCountDTOBJ "##" countDTOBJ[varCountDTOBJ] >> destDirDt"/"letter"_countDTOBJ.txt";
	}
	
	system("touch "destDirDt"/"letter"_newDTProperties.txt"); #Creo il file così da averlo, anche se vuoto	
	for (varNDT in newDTProperties)
	{
		print varNDT "##" newDTProperties[varNDT] >> destDirDt"/"letter"_newDTProperties.txt";
	}
	
	system("touch "destDirDt"/"letter"_relationDTCount.txt"); #Creo il file così da averlo, anche se vuoto	
	for (varDT in relationDTCount)
	{
		print varDT "##" relationDTCount[varDT] >> destDirDt"/"letter"_relationDTCount.txt";
	}
	
	#TODO:Decommentare se sono utili per la Summarization	
	#for (varDTUnr in egDTUnresolved)
	#{
		#print varDTUnr " => " egDTUnresolved[varDTUnr] >> "relation_info/relationEgDTUnresolved"FILENAME".txt";
	#}

	system("touch "destDirDt"/"letter"_countDataType.txt"); #Creo il file così da averlo, anche se vuoto	
	for (varCDT in countDt)
	{
		to_Print = varCDT "##" countDt[varCDT]

		print  to_Print >> destDirDt"/"letter"_countDataType.txt";
	}
	
	
	# TODO: Rimettere per Min, Max, Mean
	#print "NUMERO DI VOLTE MINIMO, MASSIMO E MEDIO IN CUI UNA PROPRIETA E ASSOCIATA AD UN CONCETTO (SE NOTO) COME SOGGETTO (OVVERO IN USCITA)" >> "relation_info/Conteggi"FILENAME".txt";
	
	# Inizializzo gli array per la valutazione
	#for(countedConcPropSubj in countClassSUBJ){
		#min_count_class_subj[countedConcPropSubj] = totale;
		#max_count_class_subj[countedConcPropSubj] = -1;
	#}
	
	#for(countedSubjPropVar in countedSUBJPropClass){
		
		# Recupero il concetto associato alla risorsa
		#split("",propClassTot);
		#split(countedSubjPropVar,propClassTot,"##");
		#classe = propClassTot[2];
		#proprieta = propClassTot[3];
		#to_check_prop_classe = classe"##"proprieta;
		
		#if(min_count_class_subj[to_check_prop_classe]>countedSUBJPropClass[countedSubjPropVar])
			#min_count_class_subj[to_check_prop_classe]=countedSUBJPropClass[countedSubjPropVar];
		
		#if(max_count_class_subj[to_check_prop_classe]<countedSUBJPropClass[countedSubjPropVar])
			#max_count_class_subj[to_check_prop_classe]=countedSUBJPropClass[countedSubjPropVar];
		
	#}
	
	#for (min_max_mean_var in min_count_class_subj){
		
		#media = (min_count_class_subj[min_max_mean_var]+max_count_class_subj[min_max_mean_var])/2;
		
		#print min_max_mean_var " => " "MINIMO: " min_count_class_subj[min_max_mean_var] ", MASSIMO: " max_count_class_subj[min_max_mean_var] ", MEDIA: " media >> "relation_info/Conteggi"FILENAME".txt";
	
	#}
	
	#print "\n\n" >> "relation_info/Conteggi"FILENAME".txt";

	#print "NUMERO DI VOLTE MINIMO, MASSIMO E MEDIO IN CUI UNA PROPRIETA E ASSOCIATA AD UN DATATYPE (SE NOTO) COME OGGETTO (OVVERO IN ENTRATA)" >> "relation_info/Conteggi"FILENAME".txt";
	
	# Inizializzo gli array per la valutazione
	#for(countedConcPropObj in countClassOBJ){
		#min_count_class_obj[countedConcPropObj] = totale;
		#max_count_class_obj[countedConcPropObj] = -1;
	#}
	
	#for(countedObjPropVar in countedOBJPropClass){
		
		# Recupero il concetto associato alla risorsa
		#split("",propClassTot);
		#split(countedObjPropVar,propClassTot,"##");
		#classe = propClassTot[2];
		#proprieta = propClassTot[3];
		#to_check_prop_classe = classe"##"proprieta;
		
		#if(min_count_class_obj[to_check_prop_classe]>countedOBJPropClass[countedObjPropVar])
			#min_count_class_obj[to_check_prop_classe]=countedOBJPropClass[countedObjPropVar];
		
		#if(max_count_class_obj[to_check_prop_classe]<countedOBJPropClass[countedObjPropVar])
			#max_count_class_obj[to_check_prop_classe]=countedOBJPropClass[countedObjPropVar];
		
	#}

	#for (min_max_mean_var_obj in min_count_class_obj){
		
		#media = (min_count_class_obj[min_max_mean_var_obj]+max_count_class_obj[min_max_mean_var_obj])/2;
		
		#print min_max_mean_var_obj " => " "MINIMO: " min_count_class_obj[min_max_mean_var_obj] ", MASSIMO: " max_count_class_obj[min_max_mean_var_obj] ", MEDIA: " media >> "relation_info/Conteggi"FILENAME".txt";
	
	#}
	
	#TODO:Decommentare se sono utili per la Summarization
	# DataType Property
	# TODO: Rimettere per Min, Max, Mean
	#print "\n\n" >> "relation_info/ConteggiDT"FILENAME".txt";
	#print "NUMERO DI VOLTE MINIMO, MASSIMO E MEDIO IN CUI UNA PROPRIETA E ASSOCIATA AD UN CONCETTO (SE NOTO) COME SOGGETTO (OVVERO IN USCITA)" >> "relation_info/ConteggiDT"FILENAME".txt";
	
	# Inizializzo gli array per la valutazione
	#for(countedConcPropDTSubj in countClassDTSUBJ){
		#min_count_class_dt_subj[countedConcPropDTSubj] = totale;
		#max_count_class_dt_subj[countedConcPropDTSubj] = -1;
	#}
	
	#for(countedDTSubjPropVar in countedDTSUBJPropClass){
		
		# Recupero il concetto associato alla risorsa
		#split("",propClassTot);
		#split(countedDTSubjPropVar,propClassTot,"##");
		#classe = propClassTot[2];
		#proprieta = propClassTot[3];
		#to_check_prop_classe = classe"##"proprieta;
		
		#if(min_count_class_dt_subj[to_check_prop_classe]>countedDTSUBJPropClass[countedDTSubjPropVar])
			#min_count_class_dt_subj[to_check_prop_classe]=countedDTSUBJPropClass[countedDTSubjPropVar];
		
		#if(max_count_class_dt_subj[to_check_prop_classe]<countedDTSUBJPropClass[countedDTSubjPropVar])
			#max_count_class_dt_subj[to_check_prop_classe]=countedDTSUBJPropClass[countedDTSubjPropVar];
		
	#}
	
	#for (min_max_mean_var in min_count_class_dt_subj){
		
		#media = (min_count_class_dt_subj[min_max_mean_var]+max_count_class_dt_subj[min_max_mean_var])/2;
		
		#print min_max_mean_var " => " "MINIMO: " min_count_class_dt_subj[min_max_mean_var] ", MASSIMO: " max_count_class_dt_subj[min_max_mean_var] ", MEDIA: " media >> "relation_info/ConteggiDT"FILENAME".txt";
	
	#}
	
	#print "\n\n" >> "relation_info/ConteggiDT"FILENAME".txt";
	#print "NUMERO DI VOLTE MINIMO, MASSIMO E MEDIO IN CUI UNA PROPRIETA E ASSOCIATA AD UN CONCETTO (SE NOTO) COME OGGETTO (OVVERO IN ENTRATA)" >> "relation_info/ConteggiDT"FILENAME".txt";
	
	# Inizializzo gli array per la valutazione
	#for(countedDTConcPropObj in countClassDTOBJ){
		#min_count_class_dt_obj[countedDTConcPropObj] = totale;
		#max_count_class_dt_obj[countedDTConcPropObj] = -1;
	#}
	
	#for(countedDTObjPropVar in countedDTOBJPropClass){
		
		# Recupero il datatype associato alla risorsa
		#split("",propClassTot);
		#split(countedDTObjPropVar,propClassTot,"##");
		#datatype = propClassTot[2];
		#proprieta = propClassTot[3];
		#to_check_prop_classe = datatype"##"proprieta;
		
		#if(min_count_class_dt_obj[to_check_prop_classe]>countedDTOBJPropClass[countedDTObjPropVar])
			#min_count_class_dt_obj[to_check_prop_classe]=countedDTOBJPropClass[countedDTObjPropVar];
		
		#if(max_count_class_dt_obj[to_check_prop_classe]<countedDTOBJPropClass[countedDTObjPropVar])
			#max_count_class_dt_obj[to_check_prop_classe]=countedDTOBJPropClass[countedDTObjPropVar];
		
	#}
	
	#for (min_max_mean_var_obj in min_count_class_dt_obj){
		
		#media = (min_count_class_dt_obj[min_max_mean_var_obj]+max_count_class_dt_obj[min_max_mean_var_obj])/2;
		
		#print min_max_mean_var_obj " => " "MINIMO: " min_count_class_dt_obj[min_max_mean_var_obj] ", MASSIMO: " max_count_class_dt_obj[min_max_mean_var_obj] ", MEDIA: " media >> "relation_info/ConteggiDT"FILENAME".txt";
	
	#}
	
	#TODO:Decommentare se sono utili per la Summarization
	# "Inferisco" le relazioni in base all'analisi
	# Esistenziale
	#print "\n\n" >> "relation_info/ConteggiDT"FILENAME".txt";
	#print "ESISTENZIALE" >> "relation_info/ConteggiDT"FILENAME".txt";
	#minCardin = "";
	#for (varCountClassDTSUBJ in countClassDTSUBJ)
	#{
		#split("",clCountSubj);
		#split(varCountClassDTSUBJ,clCountSubj,"##")
		
		#percentuale = (countClassDTSUBJ[varCountClassDTSUBJ]/countNumberOfClassAsDTSUBJ[clCountSubj[1]])*100;
		
		#print "VALUTO - SUBJ: " varCountClassSUBJ " Valori: countClassSUBJ[varCountClassSUBJ] - "  countClassSUBJ[varCountClassSUBJ] " - countNumberOfClassAsSUBJ[clCountSubj[1]] " countNumberOfClassAsSUBJ[clCountSubj[1]]
		
		#if( percentuale==100 ){ #Tutte le risorse di questa classe hanno questa proprietà
		
			#for (varCountClassDTOBJ in countClassDTOBJ)
			#{
				#split("",clCountObj);
				#split(varCountClassDTOBJ,clCountObj,"##")
				
				#if( clCountObj[2] == clCountSubj[2] ){
				
					#percentuale = (countClassDTOBJ[varCountClassDTOBJ]/countNumberOfClassAsDTOBJ[clCountObj[1]])*100;
					
					#print "VALUTO - OBJ: " varCountClassOBJ" Valori: countClassOBJ[varCountClassOBJ] - "  countClassOBJ[varCountClassOBJ] " - countNumberOfClassAsSUBJ[clCountObj[1]] " countNumberOfClassAsOBJ[clCountObj[1]]
					
					#if( percentuale==100 ){ #Tutte le risorse di questa classe hanno questa proprietà
						
						#print clCountSubj[1] " someValuesFrom [" clCountObj[2] "] " clCountObj[1] >> "relation_info/ConteggiDT"FILENAME".txt";
						
						# MinCardinality
						#minCardin = minCardin"\n"clCountSubj[1] " minCardinality [" clCountObj[2] "] (" min_count_class_dt_subj[varCountClassDTSUBJ] ") "clCountObj[1]
					#}
				#}
			#}
		#}
		
	#}
	
	# MinCardinality
	#if(minCardin!=""){
	
		#print "\n\n" >> "relation_info/ConteggiDT"FILENAME".txt";
		#print "MINCARDINALITY" >> "relation_info/ConteggiDT"FILENAME".txt";
		
		#print minCardin >> "relation_info/ConteggiDT"FILENAME".txt";
	#}
	
	# Domain & Range
	#print "\n\n" >> "relation_info/ConteggiDT"FILENAME".txt";
	#print "DOMAIN & RANGE" >> "relation_info/ConteggiDT"FILENAME".txt";
	#for(countPropCOncDTSUBJ in concPropDTSUBJ){
		
		# La proprietà ha solo un concetto come dominio
		#if( concPropDTSUBJ[countPropCOncDTSUBJ]==1 ){
			
			#if( countPropCOncDTSUBJ in concPropDTOBJ ){
				
				# La proprietà ha solo un concetto come range
				#if( concPropDTOBJ[countPropCOncDTSUBJ]==1 ){
					
					#print concPropDTSUBJOneClass[countPropCOncDTSUBJ] " " countPropCOncDTSUBJ " " concPropDTOBJOneClass[countPropCOncDTSUBJ]  >> "relation_info/ConteggiDT"FILENAME".txt";
				#}
			
			#}
		
		#}
	
	#}
	
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
		 
		 #print toPrint >> "relation_info/countConceptsReportDRResolution"FILENAME".txt";
		 
		 # Salvo la radice
		 #split("", URI_El);
		 #lengthArray=split(rootSubConc[1],URI_El,"/"); # Splitto lo statement
			
		 #risorsa_nome=substr(URI_El[lengthArray], 0, length(URI_El[lengthArray]));
		 
		 #print risorsa_nome "" toPrintCount >> "relation_info/countConceptsReportDRResolution"FILENAME".txt";
		
	#}

	# Elimino i file temporanei
	system("rm -f "dirTmpFile"/"letter"_countedSUBJProp.txt");
	system("rm -f "dirTmpFile"/"letter"_countedSUBJPropDt.txt");
	system("rm -f "dirTmpFile"/"letter"_countedOBJProp.txt");
	system("rm -f "dirTmpFile"/"letter"_countedOBJPropDt.txt");
}

# Determina se la proprietà passata è un'equivalent property di una proprietà dell'ontologia e le restituisce se esiste
# NOTA: va scorso l'array perchè una proprietà dell'ontologia può avere più equivalent property, e una proprietà esterna può essere equivalent property di più concetti dell'ontologia
function getEquProp(equProperty,property)
{
	
	equivalentProperty="";
	
	for (var in equProperty)
	{
		#gsub("\/", "\/", property);
		#gsub("\.", "\.", property);
		
		regex = "(^"property"$)|(^"property"##)|(##"property"##)|(##"property"$)";
		#if(match(equProperty[var], regex))
		#	print "match";
		#print regex;	
		if(match(equProperty[var], regex)){
			#print regex;
			#print equProperty[var];
			if(equivalentProperty=="")
				equivalentProperty=var;
			else
				equivalentProperty=equivalentProperty"#"var;
		}
	}
	
	# Se non ha equivalent property restituisco la proprietà stessa
	if(equivalentProperty=="")
		equivalentProperty = property;
	
	# Restituisco le proprietà equivalenti
    return equivalentProperty;
}

#Source: http://awk.freeshell.org/FindAllMatches - Riadattato
#TODO al momento in cui l'ho presa (non influiscono sull'utilizzo):
#- correctly handle 0-length matches.
#- handle anchored REs (eg, if RE is "^foo" and str is "foofoo", return only one "foo") - HARD
function FindAllMatches(str, re, notCapuringGroup, start, matches, numMatch, isnotCapuring) {
        
        j=0;
        eaten = 0;     # optional: used if start[] is needed
        a = RSTART; b = RLENGTH;   # to avoid unexpected side effects
	sNotCapt = -1; eNotCapt = -1; # inizializzo gli indici della parte di stringa in cui non catturare re
	isnotCapuringGroup = 0;
	inNumMatch=0;

	# Match notCapturingGroup
	if (match(str, notCapuringGroup) > 0) {
		
		sNotCapt=RSTART;
		eNotCapt=RSTART+RLENGTH;
		isnotCapuringGroup=1;
		isnotCapuring["sNotCapt"]=sNotCapt;
		isnotCapuring["eNotCapt"]=eNotCapt;
        }

	RSTART = a; RLENGTH = b;
	
	# Match re
        while (match(str, re) > 0) {	

		if((RSTART+eaten)<sNotCapt || (RSTART+eaten)>=eNotCapt){
		        start[++j]=RSTART+eaten;    # optional: save position of match in the string
			matches[j]=substr(str, RSTART, RLENGTH);
			inNumMatch++;
		}
		eaten+=(RSTART+RLENGTH-1);
		        
		str = substr(str, RSTART+RLENGTH);
	}

        RSTART = a; RLENGTH = b;

	numMatch["match"]=inNumMatch;

	return isnotCapuringGroup;
}