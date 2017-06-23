# SNP
## Introduction
Single nucleotide polymorphisms (SNPs) are single base pair changes in the genome, each at a particular locus. While most SNPs have no affect on an individual, those which do can be used as biological markers for disease or drug uptake. Each SNP has an individual RSID associated with its effect on the genome which can be used to trace its phenotypic effects. 

## LDPair
SNPs in linkage disequilibrium (LD) are inherited together and therefore need not be analyzed separately. This software parses a list of SNPs and removes SNPs which are in LD with an existing SNP on the list using the [LDLink](https://analysistools.nci.nih.gov/LDlink/?tab=home) database. 
#### Parameters: 
`<SNP list>` a line-delimited list of SNPs to be analyzed 
#### Running: 
Executables: selenium-server-standalone-\*.jar, chromedriver.exe

The program can be compiled with `javac -classpath selenium-server-standalone-*.jar LDPair.java`. 

The program can be run with `java -classpath selenium-server-standalone-*.jar:. -Dwebdriver.chrome.driver=chromedriver.exe LDPair <SNP list>`. 
#### Output: 
list2.txt a line-delimited list of SNPs with all those in LD removed

## ReadFromBraineac
Because SNPs affect mRNA transcription, their effect on gene expression can be (and has been) documented. This software determines which SNPs affect a specified gene by using the [Braineac](braineac.org) database, returning a file containing all relevant SNPs that can be used in further concordance tests. 
#### Parameters: 
`<gene>` the gene in question
`<SNP list>`a line-delimited list of SNPs to be analyzed
#### Running: 
Executables: selenium-server-standalone-\*.jar, chromedriver.exe

The program can be compiled with `javac -classpath selenium-server-standalone-*.jar ReadFromBraineac.java`. 

The program can be run with `java -classpath selenium-server-standalone-*.jar:. -Dwebdriver.chrome.driver=chromedriver.exe ReadFromBraineac <gene> <SNP list>`. 
#### Output: 
`<gene>`-SNPsOfficial.txt a line-delimited list of SNPs which affect the expression of the specified gene

## ReadFromGTEx
Software to determine which SNPs significantly (p < .05) affect a specified gene as documented by the [GTEx Dataset](https://www.gtexportal.org/home/testyourown). Returns a file containing a command to input into GTEx with the significant SNPs to manually check direction of gene expression. 
#### Parameters: 
`<SNP list>` a line-delimited list of SNPs to be analyzed
`<gene>` the gene to be analyzed
#### Running: 
Executables: selenium-server-standalone-\*.jar, chromedriver.exe

The program can be compiled with `javac GTExData` and `javac -classpath selenium-server-standalone-*.jar ReadFromGTEx.java`. 

The program can be run with `java -classpath selenium-server-standalone-*.jar:. -Dwebdriver.chrome.driver=chromedriver.exe ReadFromGTEx <SNP list> <gene>`. 
#### Output: 
`<gene>`-GTEx.txt a GTEx command with significant SNPs to check directionality of concordance

