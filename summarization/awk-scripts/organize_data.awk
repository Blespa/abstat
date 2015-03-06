BEGIN {
	
	triple_lette=0;
	triple_scritte=0;
	pref=prefix;
	destDir=destinatioDirectory;
	lengthArray=split("0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,%,_",splitters,",");
	#Salvo le lettere nell'array associativo utilizzato per il salvataggio delle informazioni
	for(iSplit=1; iSplit<=lengthArray; iSplit++)
		letters[splitters[iSplit]];
}

{ 
	# Elaboro ogni tripla del file
	
	# Stampa di controllo
	#if(triple_lette%100000==0 && triple_lette!=0)
		#print triple_lette " triple lette...";
	isObjPropOrType=0;
	isDtProp=0;
	
	gsub(" .$","",$0);
	#Ottengo tutte le risorse o datatype dalla tripla, il match assume (come dovrebbe essere da standard) che negli URi non vi siano caratteri: <,>,"
	split("", start);
	split("", matches);
	split("", numMatch);
	split("", isnotCapuring);
	isnotCapuringGroup=FindAllMatches($0,"<|>","(\"(.*)\")", start, matches, numMatch, isnotCapuring); #Il gruppo da non catturare esclude già possibile presenza di \" all'interno dei "" che delimitano la stringa
	
	#Utilizzati per l'estrazione delle risorse	
	startString=-1;
	endString=-1;	
	soggetto="";
	proprieta="";
	oggetto="";
	
	#Prendo tutte le risorse o datatype
	for(imatch=1; imatch<=numMatch["match"]; imatch++){
		
		#< e > delimitano le risorse
		if(imatch%2!=0 && matches[imatch]=="<"){
			startString=start[imatch]+1;
		}
		else if(imatch%2==0 && matches[imatch]==">"){
			endString=start[imatch]-1;
			#Salvo la stringa
			if(imatch==2)
				soggetto=substr($0, startString, endString-startString+1);
			else if(imatch==4)
				proprieta=substr($0, startString, endString-startString+1);
			else if(imatch==6){
				oggetto=substr($0, startString, endString-startString+1);
				#Nota: Proprietà come homepage hanno una risorsa come oggetto, quindi risultano object property. Questa risorsa non è un uri ma un url di una pagina che potrebbe avere ##, quindi per evitare problemi in futuro sostituisco ## con il suo equivalente codificato esadecimale %23%23
				gsub("##","%23%23",oggetto);
			}
			else{ #I dati presenti possono essere al più 3, da standard, quindi 3 coppie di <>
             			next;
			}
		}
		else{
             		next;
		}
	}

	#Se ho trovato il gruppo da non catturare, ovvero un letterale, indico che si tratta di una Datatype Property
	literal="";
	if(isnotCapuringGroup==1){
		isDtProp=1;
		#Se ho anche un datatype, prendo la stringa escludendo il separatore prima del datatype (^^) che occupa due caratteri e le virgolette che delimitano il letterale
		if(oggetto!="")
			#Letterale con apici che delimitano tutto (compreso l'eventuale lingua) + Resto della stringa prima del datatype escludendo il separatore (^^)
			literal="\""substr($0, isnotCapuring["sNotCapt"]+1, isnotCapuring["eNotCapt"]-isnotCapuring["sNotCapt"]-2)""substr($0, isnotCapuring["eNotCapt"], (start[5]-2)-isnotCapuring["eNotCapt"])"\"";
		else #Se non ho un datatype prendo la stringa dall'inizio del matching del gruppo da non catturare alla fine, con apici che delimitano tutto (compreso l'eventuale lingua)
			literal="\""substr($0, isnotCapuring["sNotCapt"]+1, isnotCapuring["eNotCapt"]-isnotCapuring["sNotCapt"]-2)""substr($0, isnotCapuring["eNotCapt"])"\"";
	}
	else{
		isObjPropOrType=1
	}

	split(soggetto,URI_El,"/"); # Splitto lo statement
	risFistLett=substr(tolower(URI_El[length(URI_El)]), 0, 1); #Prima lettera in minuscolo

	if(proprieta=="http://www.w3.org/1999/02/22-rdf-syntax-ns#type"){
		
		if(risFistLett in letters) #Se la risorsa inizia con una lettera con cui splitto
			print soggetto"##"proprieta"##"oggetto >> destDir"/"pref""risFistLett"_types.nt";
		else
			print soggetto"##"proprieta"##"oggetto >> destDir"/"pref"others_types.nt";

		triple_scritte++;		
	}
	else{
		if(isDtProp==1){		
			if(oggetto!=""){
				if(risFistLett in letters) #Se la risorsa inizia con una lettera con cui splitto
					print soggetto"##"proprieta"##"literal"##"oggetto >> destDir"/"pref""risFistLett"_dt_properties.nt";
				else
					print soggetto"##"proprieta"##"literal"##"oggetto >> destDir"/"pref"others_dt_properties.nt";
			}
			else{
				if(risFistLett in letters) #Se la risorsa inizia con una lettera con cui splitto
					print soggetto"##"proprieta"##"literal >> destDir"/"pref""risFistLett"_dt_properties.nt";
				else
					print soggetto"##"proprieta"##"literal >> destDir"/"pref"others_dt_properties.nt";
			}
		}
		else{
			if(risFistLett in letters) #Se la risorsa inizia con una lettera con cui splitto
				print soggetto"##"proprieta"##"oggetto >> destDir"/"pref""risFistLett"_obj_properties.nt";
			else
				print soggetto"##"proprieta"##"oggetto >> destDir"/"pref"others_obj_properties.nt";
		}

		triple_scritte++;	
	}

	triple_lette++;
} 

END { 
	# Determino il nome del file di destinazione in base al file in input (La sua Directory)
	gsub(".nt", "", FILENAME);
	lengthArray=split(FILENAME,FILE,"/"); 
	FILENAME = FILE[lengthArray]; # Prendo il nome del file

	print "\n"FILENAME": "triple_lette " triple lette e " triple_scritte " triple scritte.\n" 
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
