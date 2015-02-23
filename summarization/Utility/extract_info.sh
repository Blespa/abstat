#!/bin/bash

#Setto le variabili

JAVA_HOME="/usr" #Server: /usr/lib/jvm/java-6-sun
debug=1 #0: Disabled, 1:Enabled
#Setto opportunamente il comando di debug
if [ $debug -eq 1 ]
then
	dbgCmd="/usr/bin/time -f \"COMMAND: %C\nTIME: %E real\t%U user\t%S sys\nCPU: %P Percentage of the CPU that this job got\nMEMORY: %M maximum resident set size in kilobytes\n\n\" "
else
	dbgCmd=""
fi

DataDirectory=$1

#Variabili per il calcolo del report dell'ontologia
OntologyFile="$DataDirectory/ontology/"
ReportDirectory="Reports/"
TmpDatasetFileResult="Reports/Tmp_Data_For_Computation/"

#Variabili per il calcolo del report del dataset
DatasetFile="$DataDirectory/triples"
tmpDatasetFile="$DataDirectory/Organized_Splitted_Deduplicated_TmpFile"
orgDatasetFile="$DataDirectory/Organized_Splitted_Deduplicated"

#MinType
minTypeDataForComp="MinTypes/Data_For_Computation"
minTypeResult="MinTypes/Min_Type_Results"

#Pattern
patternDataForComp="Patterns/Data_For_Computation"
patternObjResult="Patterns/Obj_Patterns"
patternDtResult="Patterns/Dt_Patterns"
patternTmpFiles="Patterns/Tmp_Files"

#Variabili per la parallelizzazione
#Lettere con cui splitto i file per la parallelizzazione
IFS=',' read -a splitters <<< "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,%,_,others" #Allineare con quanto presente in organize:data, se modifico
NProc=4 #Numero di processi da parallelizzare [I passi successivi sono, per ora, vincolati all'uso di 4 processori]
NUM=0
QUEUE=""
#Utilizzate per calcolare il tempo di esecuzione totale
start=$SECONDS

#Funzioni utili alla gestione delle code di esecuzione di processi
function queue {
	QUEUE="$QUEUE $1"
	NUM=$(($NUM+1))
}

function regeneratequeue {
	OLDREQUEUE=$QUEUE
	QUEUE=""
	NUM=0
	for PID in $OLDREQUEUE
	do
		if [ -d /proc/$PID  ] ; then
			QUEUE="$QUEUE $PID"
			NUM=$(($NUM+1))
		fi
	done
}

function checkqueue {
	OLDCHQUEUE=$QUEUE
	for PID in $OLDCHQUEUE
	do
		if [ ! -d /proc/$PID ] ; then
			regeneratequeue # at least one PID has finished
			break
		fi
	done
}

#Rimuovo tutti i report
rm -f "Reports/ontology.xls"
rm -f "Reports/dt_dataset.xls"
rm -f "Reports/obj_dataset.xls"

rm -rf ${TmpDatasetFileResult}* #Rimuovo tutti i file Tmp_Data_For_Computation
mkdir -p $TmpDatasetFileResult

rm -f "log/log.txt" #Rimuovo tutti i file di log
mkdir -p log
touch "log/log.txt"

#TODO: Se diviene pesante, al cambiare delle ontologie, si può parallelizzar con l'organizzazione del dataset
{ 
#ONTOLOGY REPORT COMPUTATION
#Computo il report e le informazioni a partire dall'ontologia
echo "---Start: Ontology Report---"

	export OntologyFile
	export ReportDirectory
	export TmpDatasetFileResult

	eval ${dbgCmd}""$JAVA_HOME/bin/java -jar software/java_programs/ontology_summarization.jar "$OntologyFile" "$ReportDirectory" "$TmpDatasetFileResult"
	#Verifico eventuali errori nell'esecuzione in modo da verificare la possibilità di procedere
	if [ $? -ne 0 ]
	then
	    echo "App Failed during run"
	    exit 1
	fi

echo "---End: Ontology Report---"

echo ""

} &>> "log/log.txt"

{ 
	#ORGANIZATION, SPLITTING AND DEDUPLICATION OF FILE
	#Organizzo e splitto i file
	echo "---Start: Organize and Split files---"

	startBlock=$SECONDS

	#Divido i file da organizzare in NProc parti uguali l'uno così da parallelizzare l'organizzazione
	rm -rf $tmpDatasetFile #Rimuovo la directory che conterrà i file temporanei
	mkdir -p $tmpDatasetFile #Creo la directory che conterrà i file temporanei

	#Calcolo la dimensione dei file da splittare
	dataSize1=$(stat --printf="%s" $DatasetFile/instance_types_en.nt) 
 
	let "dataBlockSize1=($dataSize1/$NProc)+10000000" #Aggiungo 10000000 così da assicurarmi di salvare tutte le informazioni nel passo successivo

	#Processi da eseguire per lo splittaggio
	splitFile[0]="split -u -C $dataBlockSize1 $DatasetFile/instance_types_en.nt $tmpDatasetFile/1_lod_part_" #-u per scrittura diretta senza bufferizzazione

	#Splitto il file utilizzando dataBlockSize
	#Avvio l'esecuzione parallela dei processi
	for (( proc=0; proc<${#splitFile[@]}; proc++ )) # for the rest of the arguments
	do
		#echo ${splitFile[$proc]}
		eval ${dbgCmd}""${splitFile[$proc]} &
		PID=$!
		queue $PID

		while [ $NUM -ge $NProc ]; do
			checkqueue
			sleep 0.4
		done
	done
	wait # attendi il completamento di tutti i processi prima di procedere con il passo successivo
	sync #Mi assicuro che tutte le informazioni siano scritte su file

	#Creo le stringhe contenenti i file da organizzare
	filePartCount=0
	stringFile[0]="aa"
	stringFile[1]="ab"
	stringFile[2]="ac"
	stringFile[3]="ad"
	for (( i=0; i<${#stringFile[@]}; i++ ))
	do
		filePart=""
		if [ -f $tmpDatasetFile/1_lod_part_${stringFile[$i]} ];
		then
			if [ filePart == "" ]
			then
				filePart="$tmpDatasetFile/1_lod_part_${stringFile[$i]}"
			else
				filePart="${filePart} $tmpDatasetFile/1_lod_part_${stringFile[$i]}"
			fi
		fi

		if [ -f $tmpDatasetFile/2_lod_part_${stringFile[$i]} ];
		then
			if [ filePart == "" ]
			then
				filePart="$tmpDatasetFile/2_lod_part_${stringFile[$i]}"
			else
				filePart="${filePart} $tmpDatasetFile/2_lod_part_${stringFile[$i]}"
			fi
		fi

		if [ -f $tmpDatasetFile/3_lod_part_${stringFile[$i]} ];
		then
			if [ filePart == "" ]
			then
				filePart="$tmpDatasetFile/3_lod_part_${stringFile[$i]}"
			else
				filePart="${filePart} $tmpDatasetFile/3_lod_part_${stringFile[$i]}"
			fi
		fi

		if [ -f $tmpDatasetFile/4_lod_part_${stringFile[$i]} ];
		then
			if [ filePart == "" ]
			then
				filePart="$tmpDatasetFile/4_lod_part_${stringFile[$i]}"
			else
				filePart="${filePart} $tmpDatasetFile/4_lod_part_${stringFile[$i]}"
			fi
		fi
		
		filePartCom[$filePartCount]=${filePart}
		filePartCount=$(($filePartCount+1))	

	done


	rm -f $orgDatasetFile/*.nt  2>/dev/null #Rimuovo i file generati nell'esecuzione precedente
	mkdir -p $orgDatasetFile

	#TODO: Rendere flessibile la lettura per non essere vincolati a NProc

	#Processi da eseguire per l'organizzazione (Assumo che le stringhe di file abbiano almeno un file, TODO: Generalizzare a tutti i dataset)
	orgFile[0]="gawk -f software/awk_scripts/organize_data.awk -v prefix=1 -v destinatioDirectory=\"${orgDatasetFile}\" ${filePartCom[0]}"
	orgFile[1]="gawk -f software/awk_scripts/organize_data.awk -v prefix=2 -v destinatioDirectory=\"${orgDatasetFile}\" ${filePartCom[1]}"
	orgFile[2]="gawk -f software/awk_scripts/organize_data.awk -v prefix=3 -v destinatioDirectory=\"${orgDatasetFile}\" ${filePartCom[2]}"
	orgFile[3]="gawk -f software/awk_scripts/organize_data.awk -v prefix=4 -v destinatioDirectory=\"${orgDatasetFile}\" ${filePartCom[3]}"

	#Rinizializzo le variabili della parallelizzazione, per sicurezza
	NUM=0
	QUEUE=""

	#Avvio l'esecuzione parallela dei processi
	for (( proc=0; proc<${#orgFile[@]}; proc++ )) # for the rest of the arguments
	do
		#echo ${orgFile[$proc]}
		eval ${dbgCmd}""${orgFile[$proc]} &
		PID=$!
		queue $PID

		while [ $NUM -ge $NProc ]; do
			checkqueue
			sleep 0.4
		done
	done
	wait # attendi il completamento di tutti i processi prima di procedere con il passo successivo
	sync #Mi assicuro che tutte le informazioni siano scritte su file

	#Rimuovo le singole parti (Separato perchè in parallelo si crea dipendenza tra coppie di processi, ed essendo la rimozione veloce si può gestire senza problemi così)
	for i in 1 2 3 4
	do
		rm -f $tmpDatasetFile/${i}_lod_part_aa
		rm -f $tmpDatasetFile/${i}_lod_part_ab
		rm -f $tmpDatasetFile/${i}_lod_part_ac
		rm -f $tmpDatasetFile/${i}_lod_part_ad
	done

	rm -rf $tmpDatasetFile/ #Rimuovo la directory con i file temporanei, non più utili

	endBlock=$SECONDS
	if [ $debug -eq 1 ]
	then
		echo "Time: $((endBlock - startBlock)) secs."
		echo ""
	fi

	echo "---End: Organize and Split files---"
	echo ""
	echo "---Start: Deduplication of files---"
	
	startBlock=$SECONDS
	
	#Creo i processi che andranno ad unire i file (2>/dev/null per non scrivere errori di file non esistenti, perchè il cat funziona comunque su tutti quelli che ci sono)
	numMerge=0
	for element in "${splitters[@]}"
	do
	   #TODO: Per generalizzare, bisogna verificare se i file ci sono e creare dinamicamente il comando perchè cat crea il file vuoto anche se i file sorgente non vi sono
	   #Unisco i file types
	   mergeFile[$numMerge]="cat ${orgDatasetFile}/1${element}_types.nt ${orgDatasetFile}/2${element}_types.nt ${orgDatasetFile}/3${element}_types.nt ${orgDatasetFile}/4${element}_types.nt > ${orgDatasetFile}/${element}_types.nt 2>/dev/null"
	   numMerge=$(($numMerge+1))
	   #Unisco i file obj_properties
	   mergeFile[$numMerge]="cat ${orgDatasetFile}/1${element}_obj_properties.nt ${orgDatasetFile}/2${element}_obj_properties.nt ${orgDatasetFile}/3${element}_obj_properties.nt ${orgDatasetFile}/4${element}_obj_properties.nt > ${orgDatasetFile}/${element}_obj_properties.nt 2>/dev/null"
	   numMerge=$(($numMerge+1))
	   #Unisco i file dt_properties
	   mergeFile[$numMerge]="cat ${orgDatasetFile}/1${element}_dt_properties.nt ${orgDatasetFile}/2${element}_dt_properties.nt ${orgDatasetFile}/3${element}_dt_properties.nt ${orgDatasetFile}/4${element}_dt_properties.nt > ${orgDatasetFile}/${element}_dt_properties.nt 2>/dev/null"
	   numMerge=$(($numMerge+1))
	done

	#Unisco i file
	#Rinizializzo le variabili della parallelizzazione, per sicurezza
	NUM=0
	QUEUE=""

	#Avvio l'esecuzione parallela dei processi
	for (( proc=0; proc<${#mergeFile[@]}; proc++ )) # for the rest of the arguments
	do
		#echo ${mergeFile[$proc]}
		eval ${dbgCmd}""${mergeFile[$proc]} &
		PID=$!
		queue $PID

		while [ $NUM -ge $NProc ]; do
			checkqueue
			sleep 0.4
		done
	done
	wait # attendi il completamento di tutti i processi prima di procedere con il passo successivo
	sync #Mi assicuro che tutte le informazioni siano scritte su file

	#Rimuovo le singole parti (Separato perchè in parallelo si crea dipendenza tra coppie di processi, ed essendo la rimozione veloce si può gestire senza problemi così)
	for i in 1 2 3 4
	do
		for element in "${splitters[@]}"
		do
		   #Elimino i file types
		   rm -f ${orgDatasetFile}/${i}${element}"_types.nt"
		   #Elimino i file obj_properties
		   rm -f ${orgDatasetFile}/${i}${element}"_obj_properties.nt"
		   #Elimino i file dt_properties
		   rm -f ${orgDatasetFile}/${i}${element}"_dt_properties.nt"
		done
	done

	#Creo i processi che andranno a rimuovere i duplicati
	numDedupl=0
	for element in "${splitters[@]}"
	do
	   #Elimino i duplicati dai file types
	   if [ -f ${orgDatasetFile}/${element}_types.nt ];
	   then
		   deduplFile[$numDedupl]="sort -u ${orgDatasetFile}/${element}_types.nt -o ${orgDatasetFile}/${element}_types.nt"
		   numDedupl=$(($numDedupl+1))
	   fi
	   #Elimino i duplicati dai file obj_properties
	   if [ -f ${orgDatasetFile}/${element}_obj_properties.nt ];
	   then
		   deduplFile[$numDedupl]="sort -u ${orgDatasetFile}/${element}_obj_properties.nt -o ${orgDatasetFile}/${element}_obj_properties.nt"
		   numDedupl=$(($numDedupl+1))
	   fi
	   #Elimino i duplicati dai file dt_properties
	   if [ -f ${orgDatasetFile}/${element}_dt_properties.nt ];
	   then
		   deduplFile[$numDedupl]="sort -u ${orgDatasetFile}/${element}_dt_properties.nt -o ${orgDatasetFile}/${element}_dt_properties.nt"
		   numDedupl=$(($numDedupl+1))
	   fi
	done

	#Rimuovo i duplicati dai file (TODO: Si può prioritizzare la rimozione dai tipi per avviare il calcolo dei tipi minimi subito)

	#Rinizializzo le variabili della parallelizzazione, per sicurezza
	NUM=0
	QUEUE=""

	#Avvio l'esecuzione parallela dei processi
	for (( proc=0; proc<${#deduplFile[@]}; proc++ )) # for the rest of the arguments
	do
		#echo ${deduplFile[$proc]}
		eval ${dbgCmd}""${deduplFile[$proc]} &
		PID=$!
		queue $PID

		while [ $NUM -ge $NProc ]; do
			checkqueue
			sleep 0.4
		done
	done
	wait # attendi il completamento di tutti i processi prima di procedere con il passo successivo
	sync #Mi assicuro che tutte le informazioni siano scritte su file

	endBlock=$SECONDS
	if [ $debug -eq 1 ]
	then
		echo "Time: $((endBlock - startBlock)) secs."
		echo ""
	fi

	echo "---End: Deduplication of files---"
	echo ""

} &>> "log/log.txt"

{ 
#MINTYPE COMPUTATION
	echo "---Start: MinType---"
	
	startBlock=$SECONDS	

	#Rimuovo i file del calcolo precedente
	rm -rf $minTypeDataForComp/* $minTypeResult/*
	mkdir -p $minTypeDataForComp $minTypeResult

	#Sposto i file generati dall'ontologia, utili per il calcolo dei tipi minimi
	mv ${TmpDatasetFileResult}Concepts.txt $minTypeDataForComp/Concepts.txt
	mv ${TmpDatasetFileResult}EquConcepts.txt $minTypeDataForComp/EquConcepts.txt
	mv ${TmpDatasetFileResult}path.txt $minTypeDataForComp/path.txt

	sync #Mi assicuro che tutte le informazioni siano scritte su file

	#Creo i processi che andranno a calcolare i tipi minimi
	numMinType=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${orgDatasetFile}/${element}_types.nt ];
	   then
		   minTypeComp[$numMinType]="gawk -f software/awk_scripts/calculate_mintype.awk -v lett=${element} -v directoryDataForComp=\"${minTypeDataForComp}\" -v destinatioDirectory=\"${minTypeResult}\" ${orgDatasetFile}/${element}_types.nt"
		   numMinType=$(($numMinType+1))
	   fi
	done

	#Calcolo i Tipi minimi

	#Rinizializzo le variabili della parallelizzazione, per sicurezza
	NUM=0
	QUEUE=""

	#Avvio l'esecuzione parallela dei processi
	for (( proc=0; proc<${#minTypeComp[@]}; proc++ )) # for the rest of the arguments
	do
		#echo ${minTypeComp[$proc]}
		eval ${dbgCmd}""${minTypeComp[$proc]} &
		PID=$!
		queue $PID

		while [ $NUM -ge $NProc ]; do
			checkqueue
			sleep 0.4
		done
	done
	wait # attendi il completamento di tutti i processi prima di procedere con il passo successivo
	sync #Mi assicuro che tutte le informazioni siano scritte su file

	#Rimuovo i file dei tipi utilizzati per il calcolo, non più utili
	for element in "${splitters[@]}"
	do
	   rm -f ${orgDatasetFile}/${element}"_types.nt"
	done
	

	endBlock=$SECONDS
	if [ $debug -eq 1 ]
	then
		echo "Time: $((endBlock - startBlock)) secs."
		echo ""
	fi

echo "---End: MinType---"

echo ""

} &>> "log/log.txt"

{ 
	#PATTERNS COMPUTATION
	echo "---Start: Pattern---"
	
	startBlock=$SECONDS	

	#Rimuovo i file del calcolo precedente
	rm -rf $patternDataForComp/* $patternObjResult/* $patternDtResult/* $patternTmpFiles/*
	mkdir -p $patternDataForComp $patternObjResult $patternDtResult $patternTmpFiles

	#Sposto i file generati dall'ontologia, utili per il calcolo dei tipi minimi
	mv ${TmpDatasetFileResult}DR.txt $patternDataForComp/DR.txt
	mv ${TmpDatasetFileResult}DTProperties.txt $patternDataForComp/DTProperties.txt
	mv ${TmpDatasetFileResult}EquProperties.txt $patternDataForComp/EquProperties.txt
	mv ${TmpDatasetFileResult}Properties.txt $patternDataForComp/Properties.txt

	sync #Mi assicuro che tutte le informazioni siano scritte su file
	
	#Creo i processi che andranno a calcolare i pattern
	numPattern=0
	for element in "${splitters[@]}"
	do
	   if [[ -f ${orgDatasetFile}/${element}_obj_properties.nt && -f ${orgDatasetFile}/${element}_dt_properties.nt ]]
	   then
		   patternComp[$numPattern]="gawk -f software/awk_scripts/calculate_relation.awk -v lett=${element} -v minTypeResultsDirectory=\"${minTypeResult}\" -v directoryDataForComp=\"${patternDataForComp}\" -v directoryTempFile=\"${patternTmpFiles}\" -v destinatioDirectory=\"${patternObjResult}\" -v destinatioDirectoryDt=\"${patternDtResult}\" ${orgDatasetFile}/${element}_obj_properties.nt ${orgDatasetFile}/${element}_dt_properties.nt"
		   numPattern=$(($numPattern+1))
	   elif [ -f ${orgDatasetFile}/${element}_obj_properties.nt ]
	   then
		   patternComp[$numPattern]="gawk -f software/awk_scripts/calculate_relation.awk -v lett=${element} -v minTypeResultsDirectory=\"${minTypeResult}\" -v directoryDataForComp=\"${patternDataForComp}\" -v directoryTempFile=\"${patternTmpFiles}\" -v destinatioDirectory=\"${patternObjResult}\" -v destinatioDirectoryDt=\"${patternDtResult}\" ${orgDatasetFile}/${element}_obj_properties.nt"
		   numPattern=$(($numPattern+1))
	   elif [ -f ${orgDatasetFile}/${element}_dt_properties.nt ]
	   then
		   patternComp[$numPattern]="gawk -f software/awk_scripts/calculate_relation.awk -v lett=${element} -v minTypeResultsDirectory=\"${minTypeResult}\" -v directoryDataForComp=\"${patternDataForComp}\" -v directoryTempFile=\"${patternTmpFiles}\" -v destinatioDirectory=\"${patternObjResult}\" -v destinatioDirectoryDt=\"${patternDtResult}\" ${orgDatasetFile}/${element}_dt_properties.nt"
		   numPattern=$(($numPattern+1))
	   fi
	done

	#Calcolo i Pattern

	#Rinizializzo le variabili della parallelizzazione, per sicurezza
	NUM=0
	QUEUE=""

	#Avvio l'esecuzione parallela dei processi
	for (( proc=0; proc<${#patternComp[@]}; proc++ )) # for the rest of the arguments
	do
		#echo ${patternComp[$proc]}
		eval ${dbgCmd}""${patternComp[$proc]} &
		PID=$!
		queue $PID

		while [ $NUM -ge $NProc ]; do
			checkqueue
			sleep 0.4
		done
	done
	wait # attendi il completamento di tutti i processi prima di procedere con il passo successivo
	sync #Mi assicuro che tutte le informazioni siano scritte su file

	#Rimuovo i file delle proprietà utilizzati per il calcolo, non più utili
	for element in "${splitters[@]}"
	do
	   rm -f ${orgDatasetFile}/${element}"_obj_properties.nt"
	   rm -f ${orgDatasetFile}/${element}"_dt_properties.nt"
	done
	#}
	#End comment1

	endBlock=$SECONDS
	if [ $debug -eq 1 ]
	then
		echo "Time: $((endBlock - startBlock)) secs."
		echo ""
	fi

	echo "---End: Pattern---"
	echo ""

} &>> "log/log.txt"

{ 
	#MERGE DATA
	echo "---Start: Merge Data---"

	#Definisco gli script/comandi che si occupano di unire tutte le informazioni e i conteggi
	numMergeCmd=0
	numMergeCmd1=0
	numMergeCmd2=0
	#uknHierConcept
	uknHierConcept=""
	uknHierConceptCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${minTypeResult}/${element}_uknHierConcept.txt ];
	   then
		if [ $uknHierConceptCount -eq 0 ]
		then
			uknHierConcept=${minTypeResult}/${element}_uknHierConcept.txt
		else
			uknHierConcept="${uknHierConcept} ${minTypeResult}/${element}_uknHierConcept.txt"
		fi

		uknHierConceptCount=$(($uknHierConceptCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd[$numMergeCmd]="cat "${uknHierConcept}" > ${minTypeResult}/uknHierConcept.txt"
	numMergeCmd=$(($numMergeCmd+1))	

	#OBJECT PROPERTIES

	#NewObjProp
	NewObjProp=""
	NewObjPropFileCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternObjResult}/${element}_newObjProperties.txt ];
	   then
		if [ $NewObjPropFileCount -eq 0 ]
		then
			NewObjProp=${patternObjResult}/${element}_newObjProperties.txt
		else
			NewObjProp="${NewObjProp} ${patternObjResult}/${element}_newObjProperties.txt"
		fi

		NewObjPropFileCount=$(($NewObjPropFileCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd[$numMergeCmd]="sort -u "${NewObjProp}" -o ${patternObjResult}/newObjProperties.txt"
	numMergeCmd=$(($numMergeCmd+1))

	#DATATYPE PROPERTIES

	#NewODtProp
	NewODtProp=""
	NewODtPropFileCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternDtResult}/${element}_newDTProperties.txt ];
	   then
		if [ $NewODtPropFileCount -eq 0 ]
		then
			NewODtProp=${patternDtResult}/${element}_newDTProperties.txt
		else
			NewODtProp="${NewODtProp} ${patternDtResult}/${element}_newDTProperties.txt"
		fi

		NewODtPropFileCount=$(($NewODtPropFileCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd[$numMergeCmd]="sort -u "${NewODtProp}" -o ${patternDtResult}/newDTProperties.txt"
	numMergeCmd=$(($numMergeCmd+1))

	#uknHierConcept
	newConcepts=""
	newConceptsCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${minTypeResult}/${element}_newConcepts.txt ];
	   then
		if [ $newConceptsCount -eq 0 ]
		then
			newConcepts=${minTypeResult}/${element}_newConcepts.txt
		else
			newConcepts="${newConcepts} ${minTypeResult}/${element}_newConcepts.txt"
		fi

		newConceptsCount=$(($newConceptsCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd[$numMergeCmd]="gawk -f software/awk_scripts/merge_info/mergeNewConcepts.awk -v destinatioDirectory=\"${minTypeResult}\" ${newConcepts}"
	numMergeCmd=$(($numMergeCmd+1))

	#OBJECT PROPERTIES

	#countConcepts
	countConcepts=""
	countConceptsCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternObjResult}/${element}_countConcepts.txt ];
	   then
		if [ $countConceptsCount -eq 0 ]
		then
			countConcepts=${patternObjResult}/${element}_countConcepts.txt
		else
			countConcepts="${countConcepts} ${patternObjResult}/${element}_countConcepts.txt"
		fi

		countConceptsCount=$(($countConceptsCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd[$numMergeCmd]="gawk -f software/awk_scripts/merge_info/mergeCountConcepts.awk -v destinatioDirectory=\"${patternObjResult}\" ${countConcepts}"
	numMergeCmd=$(($numMergeCmd+1))	

	#countClassSubj
	countClassSubj=""
	countClassSubjCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternObjResult}/${element}_countClassSUBJ.txt ];
	   then
		if [ $countClassSubjCount -eq 0 ]
		then
			countClassSubj=${patternObjResult}/${element}_countClassSUBJ.txt
		else
			countClassSubj="${countClassSubj} ${patternObjResult}/${element}_countClassSUBJ.txt"
		fi

		countClassSubjCount=$(($countClassSubjCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd1[$numMergeCmd1]="gawk -f software/awk_scripts/merge_info/mergeCountClass.awk -v dataForCompDirectory=\"${patternObjResult}\" -v destinatioDirectory=\"${patternObjResult}\" -v destinationFile=\"countClassSUBJ.txt\" ${countClassSubj}"
	numMergeCmd1=$(($numMergeCmd1+1))

	#countClassObj
	countClassObj=""
	countClassObjCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternObjResult}/${element}_countClassOBJ.txt ];
	   then
		if [ $countClassObjCount -eq 0 ]
		then
			countClassObj=${patternObjResult}/${element}_countClassOBJ.txt
		else
			countClassObj="${countClassObj} ${patternObjResult}/${element}_countClassOBJ.txt"
		fi

		countClassObjCount=$(($countClassObjCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd1[$numMergeCmd1]="gawk -f software/awk_scripts/merge_info/mergeCountClass.awk -v dataForCompDirectory=\"${patternObjResult}\" -v destinatioDirectory=\"${patternObjResult}\" -v destinationFile=\"countClassOBJ.txt\" ${countClassObj}"
	numMergeCmd1=$(($numMergeCmd1+1))

	#countSubj
	countSubj=""
	countSubjCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternObjResult}/${element}_countSUBJ.txt ];
	   then
		if [ $countSubjCount -eq 0 ]
		then
			countSubj=${patternObjResult}/${element}_countSUBJ.txt
		else
			countSubj="${countSubj} ${patternObjResult}/${element}_countSUBJ.txt"
		fi

		countSubjCount=$(($countSubjCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd1[$numMergeCmd1]="gawk -f software/awk_scripts/merge_info/mergeCount.awk -v dataForCompDirectory=\"${patternObjResult}\" -v fileForComputation=\"countConcepts.txt\" -v destinatioDirectory=\"${patternObjResult}\" -v destinationFile=\"countSUBJ.txt\" ${countSubj}"
	numMergeCmd1=$(($numMergeCmd1+1))	

	#countObj
	countObj=""
	countObjCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternObjResult}/${element}_countOBJ.txt ];
	   then
		if [ $countObjCount -eq 0 ]
		then
			countObj=${patternObjResult}/${element}_countOBJ.txt
		else
			countObj="${countObj} ${patternObjResult}/${element}_countOBJ.txt"
		fi

		countObjCount=$(($countObjCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd1[$numMergeCmd1]="gawk -f software/awk_scripts/merge_info/mergeCount.awk -v dataForCompDirectory=\"${patternObjResult}\" -v fileForComputation=\"countConcepts.txt\" -v destinatioDirectory=\"${patternObjResult}\" -v destinationFile=\"countOBJ.txt\" ${countObj}"
	numMergeCmd1=$(($numMergeCmd1+1))	

	#countPropSubj
	countPropSubj=""
	countPropSubjCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternObjResult}/${element}_concPropSUBJ.txt ];
	   then
		if [ $countPropSubjCount -eq 0 ]
		then
			countPropSubj=${patternObjResult}/${element}_concPropSUBJ.txt
		else
			countPropSubj="${countPropSubj} ${patternObjResult}/${element}_concPropSUBJ.txt"
		fi

		countPropSubjCount=$(($countPropSubjCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd1[$numMergeCmd1]="gawk -f software/awk_scripts/merge_info/mergeCountConcProp.awk -v dataForCompDirectory=\"${patternObjResult}\" -v fileForComputation=\"countConcepts.txt\" -v destinatioDirectory=\"${patternObjResult}\" -v destinationFile=\"concPropSUBJ.txt\" ${countPropSubj}"
	numMergeCmd1=$(($numMergeCmd1+1))	

	#countPropObj
	countPropObj=""
	countPropObjCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternObjResult}/${element}_concPropOBJ.txt ];
	   then
		if [ $countPropObjCount -eq 0 ]
		then
			countPropObj=${patternObjResult}/${element}_concPropOBJ.txt
		else
			countPropObj="${countPropObj} ${patternObjResult}/${element}_concPropOBJ.txt"
		fi

		countPropObjCount=$(($countPropObjCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd1[$numMergeCmd1]="gawk -f software/awk_scripts/merge_info/mergeCountConcProp.awk -v dataForCompDirectory=\"${patternObjResult}\" -v fileForComputation=\"countConcepts.txt\" -v destinatioDirectory=\"${patternObjResult}\" -v destinationFile=\"concPropOBJ.txt\" ${countPropObj}"
	numMergeCmd1=$(($numMergeCmd1+1))	

	#countProp
	countProp=""
	countPropCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternObjResult}/${element}_countProp.txt ];
	   then
		if [ $countPropCount -eq 0 ]
		then
			countProp=${patternObjResult}/${element}_countProp.txt
		else
			countProp="${countProp} ${patternObjResult}/${element}_countProp.txt"
		fi

		countPropCount=$(($countPropCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd2[$numMergeCmd2]="gawk -f software/awk_scripts/merge_info/mergeCountProp.awk -v dataForCompDirectory=\"${patternObjResult}\" -v destinatioDirectory=\"${patternObjResult}\" -v destinationFile=\"countProp.txt\" ${countProp}"
	numMergeCmd2=$(($numMergeCmd2+1))	

	#Pattern
	countPattern=""
	countPatternCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternObjResult}/${element}_relationCount.txt ];
	   then
		if [ $countPatternCount -eq 0 ]
		then
			countPattern=${patternObjResult}/${element}_relationCount.txt
		else
			countPattern="${countPattern} ${patternObjResult}/${element}_relationCount.txt"
		fi

		countPatternCount=$(($countPatternCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd[$numMergeCmd]="gawk -f software/awk_scripts/merge_info/mergeRelation.awk -v destinatioDirectory=\"${patternObjResult}\" -v destinationFile=\"relationCount.txt\" ${countPattern}"
	numMergeCmd=$(($numMergeCmd+1))

	#DATATYPE PROPERTIES

	#countDataType
	countDataType=""
	countDataTypeCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternDtResult}/${element}_countDataType.txt ];
	   then
		if [ $countDataTypeCount -eq 0 ]
		then
			countDataType=${patternDtResult}/${element}_countDataType.txt
		else
			countDataType="${countDataType} ${patternDtResult}/${element}_countDataType.txt"
		fi

		countDataTypeCount=$(($countDataTypeCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd[$numMergeCmd]="gawk -f software/awk_scripts/merge_info/mergeCountDataType.awk -v destinatioDirectory=\"${patternDtResult}\" ${countDataType}"
	numMergeCmd=$(($numMergeCmd+1))	

	#countClassDtSubj
	countClassDtSubj=""
	countClassDtSubjCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternDtResult}/${element}_countClassDTSUBJ.txt ];
	   then
		if [ $countClassDtSubjCount -eq 0 ]
		then
			countClassDtSubj=${patternDtResult}/${element}_countClassDTSUBJ.txt
		else
			countClassDtSubj="${countClassDtSubj} ${patternDtResult}/${element}_countClassDTSUBJ.txt"
		fi

		countClassDtSubjCount=$(($countClassDtSubjCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd1[$numMergeCmd1]="gawk -f software/awk_scripts/merge_info/mergeCountClass.awk -v dataForCompDirectory=\"${patternObjResult}\" -v destinatioDirectory=\"${patternDtResult}\" -v destinationFile=\"countClassDTSUBJ.txt\" ${countClassDtSubj}"
	numMergeCmd1=$(($numMergeCmd1+1))

	#countDataTypeObj
	countDataTypeObj=""
	countDataTypeObjCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternDtResult}/${element}_countClassDTOBJ.txt ];
	   then
		if [ $countDataTypeObjCount -eq 0 ]
		then
			countDataTypeObj=${patternDtResult}/${element}_countClassDTOBJ.txt
		else
			countDataTypeObj="${countDataTypeObj} ${patternDtResult}/${element}_countClassDTOBJ.txt"
		fi

		countDataTypeObjCount=$(($countDataTypeObjCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd1[$numMergeCmd1]="gawk -f software/awk_scripts/merge_info/mergeCountDt.awk -v dataForCompDirectory=\"${patternDtResult}\" -v destinatioDirectory=\"${patternDtResult}\" -v destinationFile=\"countClassDTOBJ.txt\" ${countDataTypeObj}"
	numMergeCmd1=$(($numMergeCmd1+1))

	#countDtSubj
	countDtSubj=""
	countDtSubjCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternDtResult}/${element}_countDTSUBJ.txt ];
	   then
		if [ $countDtSubjCount -eq 0 ]
		then
			countDtSubj=${patternDtResult}/${element}_countDTSUBJ.txt
		else
			countDtSubj="${countDtSubj} ${patternDtResult}/${element}_countDTSUBJ.txt"
		fi

		countDtSubjCount=$(($countDtSubjCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd1[$numMergeCmd1]="gawk -f software/awk_scripts/merge_info/mergeCount.awk -v dataForCompDirectory=\"${patternObjResult}\" -v fileForComputation=\"countConcepts.txt\" -v destinatioDirectory=\"${patternDtResult}\" -v destinationFile=\"countDTSUBJ.txt\" ${countDtSubj}"
	numMergeCmd1=$(($numMergeCmd1+1))	

	#countDtObj
	countDtObj=""
	countDtObjCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternDtResult}/${element}_countDTOBJ.txt ];
	   then
		if [ $countDtObjCount -eq 0 ]
		then
			countDtObj=${patternDtResult}/${element}_countDTOBJ.txt
		else
			countDtObj="${countDtObj} ${patternDtResult}/${element}_countDTOBJ.txt"
		fi

		countDtObjCount=$(($countDtObjCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd1[$numMergeCmd1]="gawk -f software/awk_scripts/merge_info/mergeCount.awk -v dataForCompDirectory=\"${patternDtResult}\" -v fileForComputation=\"countDataType.txt\" -v destinatioDirectory=\"${patternDtResult}\" -v destinationFile=\"countDTOBJ.txt\" ${countDtObj}"
	numMergeCmd1=$(($numMergeCmd1+1))	

	#countPropDtSubj
	countPropDtSubj=""
	countPropDtSubjCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternDtResult}/${element}_concPropDTSUBJ.txt ];
	   then
		if [ $countPropDtSubjCount -eq 0 ]
		then
			countPropDtSubj=${patternDtResult}/${element}_concPropDTSUBJ.txt
		else
			countPropDtSubj="${countPropDtSubj} ${patternDtResult}/${element}_concPropDTSUBJ.txt"
		fi

		countPropDtSubjCount=$(($countPropDtSubjCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd1[$numMergeCmd1]="gawk -f software/awk_scripts/merge_info/mergeCountConcProp.awk -v dataForCompDirectory=\"${patternObjResult}\" -v fileForComputation=\"countConcepts.txt\" -v destinatioDirectory=\"${patternDtResult}\" -v destinationFile=\"concPropDTSUBJ.txt\" ${countPropDtSubj}"
	numMergeCmd1=$(($numMergeCmd1+1))	

	#countPropDtObj
	countPropDtObj=""
	countPropObjDtCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternDtResult}/${element}_concPropDTOBJ.txt ];
	   then
		if [ $countPropObjDtCount -eq 0 ]
		then
			countPropDtObj=${patternDtResult}/${element}_concPropDTOBJ.txt
		else
			countPropDtObj="${countPropDtObj} ${patternDtResult}/${element}_concPropDTOBJ.txt"
		fi

		countPropObjDtCount=$(($countPropObjDtCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd1[$numMergeCmd1]="gawk -f software/awk_scripts/merge_info/mergeCountConcProp.awk -v dataForCompDirectory=\"${patternDtResult}\" -v fileForComputation=\"countDataType.txt\" -v destinatioDirectory=\"${patternDtResult}\" -v destinationFile=\"concPropDTOBJ.txt\" ${countPropDtObj}"
	numMergeCmd1=$(($numMergeCmd1+1))	

	#countDtProp
	countDtProp=""
	countDtPropCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternDtResult}/${element}_countDTProp.txt ];
	   then
		if [ $countDtPropCount -eq 0 ]
		then
			countDtProp=${patternDtResult}/${element}_countDTProp.txt
		else
			countDtProp="${countDtProp} ${patternDtResult}/${element}_countDTProp.txt"
		fi

		countDtPropCount=$(($countDtPropCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd2[$numMergeCmd2]="gawk -f software/awk_scripts/merge_info/mergeCountDtProp.awk -v dataForCompDirectory=\"${patternDtResult}\" -v destinatioDirectory=\"${patternDtResult}\" -v destinationFile=\"countDTProp.txt\" ${countDtProp}"
	numMergeCmd2=$(($numMergeCmd2+1))	

	#Pattern
	countDtPattern=""
	countDtPatternCount=0
	for element in "${splitters[@]}"
	do
	   if [ -f ${patternDtResult}/${element}_relationDTCount.txt ];
	   then
		if [ $countDtPatternCount -eq 0 ]
		then
			countDtPattern=${patternDtResult}/${element}_relationDTCount.txt
		else
			countDtPattern="${countDtPattern} ${patternDtResult}/${element}_relationDTCount.txt"
		fi

		countDtPatternCount=$(($countDtPatternCount+1))	
	   fi
	done
	
	#Salvo il comando
	mergeCmd[$numMergeCmd]="gawk -f software/awk_scripts/merge_info/mergeRelation.awk -v destinatioDirectory=\"${patternDtResult}\" -v destinationFile=\"relationDTCount.txt\" ${countDtPattern}"
	numMergeCmd=$(($numMergeCmd+1))

	#Richiamo gli script/comandi che si occupano di unire tutte le informazioni e i conteggi, prestando attenzione a richiamare gli script dipendenti in blocchi paralleli di esecuzione sequenziali

	#Rinizializzo le variabili della parallelizzazione, per sicurezza
	NUM=0
	QUEUE=""

	#Avvio l'esecuzione parallela dei processi
	for (( proc=0; proc<${#mergeCmd[@]}; proc++ )) # for the rest of the arguments
	do
		#echo ${mergeCmd[$proc]}
		eval ${dbgCmd}""${mergeCmd[$proc]} &
		PID=$!
		queue $PID

		while [ $NUM -ge $NProc ]; do
			checkqueue
			sleep 0.4
		done
	done
	wait # attendi il completamento di tutti i processi prima di procedere con il passo successivo
	sync #Mi assicuro che tutte le informazioni siano scritte su file

	#Rinizializzo le variabili della parallelizzazione, per sicurezza
	NUM=0
	QUEUE=""

	#Avvio l'esecuzione parallela dei processi
	for (( proc=0; proc<${#mergeCmd1[@]}; proc++ )) # for the rest of the arguments
	do
		#echo ${mergeCmd1[$proc]}
		eval ${dbgCmd}""${mergeCmd1[$proc]} &
		PID=$!
		queue $PID

		while [ $NUM -ge $NProc ]; do
			checkqueue
			sleep 0.4
		done
	done
	wait # attendi il completamento di tutti i processi prima di procedere con il passo successivo
	sync #Mi assicuro che tutte le informazioni siano scritte su file

	#Rinizializzo le variabili della parallelizzazione, per sicurezza
	NUM=0
	QUEUE=""

	#Avvio l'esecuzione parallela dei processi
	for (( proc=0; proc<${#mergeCmd2[@]}; proc++ )) # for the rest of the arguments
	do
		#echo ${mergeCmd2[$proc]}
		eval ${dbgCmd}""${mergeCmd2[$proc]} &
		PID=$!
		queue $PID

		while [ $NUM -ge $NProc ]; do
			checkqueue
			sleep 0.4
		done
	done
	wait # attendi il completamento di tutti i processi prima di procedere con il passo successivo
	sync #Mi assicuro che tutte le informazioni siano scritte su file

	#Rimuovo i file parziali dei pattern utilizzati per il calcolo, non più utili
	for element in "${splitters[@]}"
	do
	  	rm -f ${minTypeResult}/${element}_uknHierConcept.txt
		rm -f ${patternObjResult}/${element}_newObjProperties.txt
		rm -f ${patternDtResult}/${element}_newDTProperties.txt
		rm -f ${minTypeResult}/${element}_newConcepts.txt
		rm -f ${patternObjResult}/${element}_countConcepts.txt
		rm -f ${patternObjResult}/${element}_countClassSUBJ.txt
		rm -f ${patternObjResult}/${element}_countClassOBJ.txt
		rm -f ${patternObjResult}/${element}_countSUBJ.txt
		rm -f ${patternObjResult}/${element}_countOBJ.txt
		rm -f ${patternObjResult}/${element}_concPropSUBJ.txt
		rm -f ${patternObjResult}/${element}_concPropOBJ.txt
		rm -f ${patternObjResult}/${element}_countProp.txt
		rm -f ${patternObjResult}/${element}_relationCount.txt
		rm -f ${patternDtResult}/${element}_countDataType.txt
		rm -f ${patternDtResult}/${element}_countClassDTSUBJ.txt
		rm -f ${patternDtResult}/${element}_countClassDTOBJ.txt
		rm -f ${patternDtResult}/${element}_countDTSUBJ.txt
		rm -f ${patternDtResult}/${element}_countDTOBJ.txt
		rm -f ${patternDtResult}/${element}_concPropDTSUBJ.txt
		rm -f ${patternDtResult}/${element}_concPropDTOBJ.txt
		rm -f ${patternDtResult}/${element}_countDTProp.txt
		rm -f ${patternDtResult}/${element}_relationDTCount.txt 
	done

	endBlock=$SECONDS
	if [ $debug -eq 1 ]
	then
		echo "Time: $((endBlock - startBlock)) secs."
		echo ""
	fi

	echo "---End: Merge Data---"
	echo ""

} &>> "log/log.txt"

{
	end=$SECONDS
	if [ $debug -eq 1 ]
	then
		echo "Total Time: $((end - start)) secs."
		echo ""
	fi

} &>> "log/log.txt"

exit #Termino l'esecuzione
