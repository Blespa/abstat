 Dataset Analysis Output:

 - The Awk output is in "MinTypes/Min_Type_Results" and "Patterns".
 - The column separator is ##. We can not use the , and the ; because they can be part of the RDF triple.

Input:

in addition to what reported on the technical report, we need also the following file of DBPedia: specific_mappingbased_properties_en.nt


MAPPING:  Dataset Analysis Output ==> Excel Report

Since the generation of the last Excel report we made some changes to the generated information. In the following I'll report a detailed description about what is changed and why. We made also minor changes to the structure of generated file reporting information to increase the understability of the data (e.g., column reporting the total respect to we computed the % in another column).

Each of the following files has now a header describing the columns.

- MinTypes/Min_Type_Results/newConcepts.txt ==> Conc - New Concepts
- Patterns/Obj_Patterns/newObjProperties.txt ==> New Object Properties
- Patterns/Obj_Patterns/countConcepts.txt ==> Conc - Number of Instances
- Patterns/Obj_Patterns/countClassSUBJ.txt ==> Obj Prop - Completeness (Subj)
- Patterns/Obj_Patterns/countClassOBJ.txt ==> Obj Prop - Completeness (Obj)
- Patterns/Obj_Patterns/countSUBJ.txt ==> Obj Prop - How Common is (Subj)
- Patterns/Obj_Patterns/countOBJ.txt ==> Obj Prop - How Common is (Obj)
- Patterns/Obj_Patterns/countProp.txt ==> Obj Prop - Usage
- Patterns/Obj_Patterns/relationCount.txt ==> R-Patterns
- Patterns/Obj_Patterns/concPropSUBJ.txt ==> NO (Report the number and % of concept that appear as SUBJ of the given property)
- Patterns/Obj_Patterns/concPropOBJ.txt ==> NO (Report the number and % of concept that appear as OBJ of the given property)

- Patterns/Dt_Patterns/newDtProperties.txt ==> New DataType Properties
- Patterns/Dt_Patterns/relationDTCount.txt ==> CI-Patterns
- Patterns/Dt_Patterns/countDTSUBJ.txt ==> Dt Prop - How Common is (Subj)
- Patterns/Dt_Patterns/countDTOBJ.txt ==> Dt Prop - How Common is (Obj) [Incoming in the DataType]
- Patterns/Dt_Patterns/countDTProp.txt ==> Dt Prop - Usage
- Patterns/Dt_Patterns/countClassDTSUBJ.txt ==> Dt Prop - Completeness (Subj)
- Patterns/Dt_Patterns/concPropDTSUBJ.txt ==> NO (Report the number and % of DataType that appear as SUBJ of the given property)
- Patterns/Dt_Patterns/countDataType.txt ==> EQUIVALENT TO Conc - Number of Instances BUT FOR DATATYPE

Not useful for the report txt:
- Patterns/Dt_Patterns/countClassDTOBJ.txt
- Patterns/Dt_Patterns/concPropDTOBJ.txt


Not computed because not useful:

- Obj Prop - Min,Max,Mean (Subj)
- Obj Prop - Min,Max,Mean (Obj)
- Conc - Report
- Dt Prop - Min,Max,Mean (Subj)
- Dt Prop - Min,Max,Mean (Obj)

Not computed because computationally intensive (and not part of the parallel and fast version)

- Obj - Extracted SomeValuesFrom
- Obj - Extracted MinCardinality
- Obj - Extracted Domain&Range
- Dt - Extracted SomeValuesFrom
- Dt - Extracted MinCardinality
- Dt - Extracted Domain&Range

Please note: the header of the Dt properties sometime refers to Concept instead of DataType. This is because the code does not generate the header according to the type of property. If you see Concept in a Dt Report, just read it as DataType.