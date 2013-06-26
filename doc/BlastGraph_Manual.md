# BlastGraph Manual v0.1

------------------------


## 1 Installing

---------------------------

### 1.1 Requirements
- BlastGraph main program is developed using Java. So it should work on any system with JRE(Java Runtime Environment) installed.
- NCBI BLASTALL package is required for similarity search. 
- [MCL][MCL](http://micans.org/mcl) program is recommended for clustering. 
- Python environment and Biopython package are required to run script tools.

Computer requirements:

- JRE: Java SE Runtime Environment 1.6 or later;
- Free memory: 2G;
- Free space: 10G;

### 1.2 Installing steps

- Install Java Runtime environment;
- Install blast package;
- Install the BlastGraph package;
- Install MCL for clustering.

### 1.3 Install JRE

Download and install Java SE Runtime Environment 6 from [http://www.java.com](http://www.java.com). The latest JRE 7 or higher versions are not supported. While the default JRE(Open JRE) on most Linux platforms can be used, it's better to use Java SE Runtime Environment instead for better performance. 

### 1.4 Install BLAST

Download a version of BLASTALL package for your platform from NCBI([ftp://ftp.ncbi.nih.gov/blast/executables/](ftp://ftp.ncbi.nih.gov/blast/executables/)) and simply decompress it. Put the path into your system environment. Here we use blastall 2.2.21. It should work on other versions.

### 1.5 Install BlastGraph

Download BlastGraph package from [https://github.com/bigwiv/BlastGraph](https://github.com/bigwiv/BlastGraph) and simply decompress it. Then the main jar file and script tools are available to run. Before perform

### 1.6 Install MCL

MCL can be compiled on UNIX/Linux systems including Mac OS X. For Windows, cygwin should be installed first. Please refer to MCL official website for more information.

#### 1.6.1 Install MCL on UNIX/Linux

Download the latest version of MCL from it's website([http://micans.org/mcl/src/](http://micans.org/mcl/src/))  
Install as follows:

	tar xzf mcl-latest.tar.gz
	cd mcl-latest
	./configure --prefix=$HOME/local
	make install

#### 1.6.2 Install MCL on windows

- Install [Cygwin][Cygwin] first following the installation instructions on Cygwin official website;
- To make Cygwin work in the windows command prompt(`cmd`), you need to add Cygwin bin path to the windows environment(Path). For example, if your Cygwin path is `C:\cygwin`, then append `;C:\cygwin\bin\` to the Path variable without quote;
- Install MCL the same way as on UNIX/Linux systems.

### 1.7 Set MCL path

Open BlastGraph main program, select **Edit>Setting** menu, and then choose the MCL path on the **Program** tab of the prompt dialog.

-----------------------------------

## 2 Usage

-----------------------------------

### 2.1 Common Workflow

- Convert Gnome Genbank file to Fasta file;
- Do all-to-all blast;
- Create Graph from Blast XML result in BlastGraph main program;
- Clustering and filtering;
- Gene content and tree building;

### 2.2 Data preprocessing

#### 2.2.1 Genbank2Fasta Conversion

Before performing an all-to-all BLAST, all coding DNA sequences (CDSs) in each supplied genome GenBank file must be extracted and converted into standard FASTA file format. This can be performed using a Python script(`gb2fasta.py`) conversion tool provided along with the main BlastGraph program. The required header line format and abbreviations are listed in Table 1.

Table: FASTA header format and recognized abbreviations.

| Shortcut | Meaning           | Comment                    |
|:---------|:------------------|:---------------------------|
| gi       | GI number         | Same as standard Fasta.    |
| gb       | Genbank Accession | Same as standard Fasta.    |
| or       | CDS order         | CDS order in genbank file. |
|          |                   | May not be the real order  |
|          |                   | on genome.                 |
| st       | Strand            | Positive: 1; Negative: -1; |
|          |                   | Unknown: 0;                |
| lo       | Location          | Start to stop(1-747)       |
| gn       | Genome Accession  | Genome of this sequence    |
| tx       | Taxonomy          | Taxonomy of this organism  |


`gb2fasta.py` usage:

	# common usage: this will extract amino acid of all CDS(faa)
	gb2fasta.py -i sample.gb -o sample.faa
	
	# type help option for more information
	gb2fasta.py -h


Sample of converted result: 

    >gi|410493571|gb|YP_006908509.1|or|1|st|1|lo|1-747|gn|NC_018875|tx|Viruses;dsDNA viruses, no RNA stage;Baculoviridae;Betabaculovirus;unclassified Betabaculovirus| granulin [Epinotia aporema granulovirus]  
	MGYNKSLRYSRHEGTTCVIDNHHLKSLGSVLNDVRRKKDHIRDEEFGPIKDIANQYMVTE  
	DPFRGPGKNVRITLFKEIRRVHPDTMKLVCNWSGKEFLRETWTRFICEEFPITTDQEIMD  
	LWFELQLRPMQPNRCYKFTMQYALCAHSDYVAHDVIRQQDPYYVGPNNIERINLTKKGFA  
	FPLTCLQSVYNENFEEFFDDVLWPYFHRPLVYVGTTSAETEEIMIEVSLLFKIKEFAPDV  
	PLFTGPAY


#### 2.2.2 All to All blast

Usually, the generated FASTA files can then be used to build a local database using formatdb program from the BLASTALL/BLAST+ package.  These files can then be used as BLAST queries against the generated database. However, as BlastGraph can merge multiple BLAST results into a single graph, there is a second approach that can be adopted.  If previous studies or analyses indicate the genomes can be classified into two distantly related sets, then an all-to-all BLAST analysis can be performed individually within both sets followed by a final BLAST between sets with different parameters, such as different scoring matrices.  In this way, remote similarities or connections can be identified.

The BlastGraph only accept XML format of Blast result as input. Here a simple instruction is given just in case. 

Create a sequence datebase from the generated Fasta file(s)

	formatdb -i sample.faa (BLASTALL 2.2.25)
or
	makeblastdb -dbtype 'prot' -in sample.faa (BLAST+ 2.2.28)

Blast using it again as query to the database and generate the XML result.

	blastall -p blastp -d sample.faa -i sample.faa -e 1 -m 7 -o sample.xml
or
	blastp -query sample.faa -db sample.faa -evalue 1 -outfmt 5 -out sample.xml

We can set a large E-value(-e 1) at the beginning as BlastGraph can flexibly filter the results.


### 2.3 BlastGraph main program 

![Screen shot of BlastGrahp main program.][MainProgram]

#### 2.3.1 Create graph from Blast result

Click **File>New From Blast** from menu bar, choose the XML result. 

BlastGraph will convert it into an undirected graph where the vertices represent the genes/proteins and the edges represent the similarity relationship. If there are reciprocal hits between two vertices, the better one will be chosen. All related information(as shown in 2.2.1) and blast value are included in this graph.

Click **File>Save As** to save the generated graph in GML format(Graph XML file).

If there are other related graphs already converted, as described in 2.2.2, BlastGraph can merge them into the current one if there are overlapping vertices.

Click **File>Append Graph** to choose the graph to be appended.

#### 2.3.2 Create smaller subgraphs

The graph crated by the previous step may contain one or more subgraphs ordered from large to small. While those subgraphs are normally too large to be displayed and to show meaningful informations, we need to filter simplify them by reducing their size and create more smaller clusters. There are two ways for this task: Markov Clustering Algorithm(MCL) and Edge filtering.

##### 2.3.2.1 Markov Clustering

In BlastGraph, there are three steps for MCL clustering: (i) generation of a weighted graph in “abc” format from the raw graph based on BLAST E-value or Score; (ii) MCL clustering; (iii) creation of subgraphs from the original graph according to clustering results. At the first step, we implement a manageable method to create the weighted graph, which affects the granularity of the MCL results along with the inflation parameter in the MCL algorithm.

Click **Edit>Markov Cluster** to input the weighted graph generate parameters and the MCL inflation parameter. Click run when finished input and then wait until new subgraphs generated. 

Here, the -log(*E-value*), bit score or score density can be used for edge weighting. A score window can be selected by defining low and high cutoff. The low cutoff filters weaker edges, which also solves the negative weight problem that occurs when the *E-value* is greater than 1. The high cutoff tells BlastGraph to treat edges stronger than this cutoff as equal and assign them this maximum value. This is comparable to setting an *E-value* cutoff in a standard BLAST analysis in order to identify equivalent gene sequences with similarities greater than this threshold. The high cutoff also solve the log*0* problem for E-value. 

##### 2.3.2.2 Edge filtering

Click **Edit>Filtering** to select the filtering criterion and set the cutoff to be used.

The filtering criteria are all based on values generated from the BLAST analysis, as either directly generated values or calculated quantities (Table 2.).

Table: Calculated measures for comparison of sequence similarity.
*N~I~*(Number of Identities); *N~P~*(Number of Positives); *S*(Blast Bit Score);
*L~Q~*(Length of Query Sequence); *L~S~*(Length of Subject Sequence);
*L~QC~*(Length of Query Coverage); *L~SC~*(Length of Subject Coverage). 
Except *L~Q~* and *L~S~*, all values above are for each HSP(High-scoring Segment Pair) instead of whole sequence. *Sum(L~QC~)* and *Sum(L~SC~)* are calculated such that the overlapping regions of different HSPs are only counted once.

| Filtering Criterion | Description                                       |
| :-------------------| :-------------------------------------------------|
| E-value             | Same as Blast result                              |
| Score Density       | *Sum(S)* / *Sum(Max(L~QC~, L~SC~))*               |
| Percentage Identity | *Sum(N~I~)*/ *Sum(Max(L~QC~, L~SC~))*             |
| Percentage Positive | *Sum(N~P~)*/ *Sum(Max(L~QC~, L~SC~))*             |
| Coverage            | *Sum(L~QC~)* * *Sum(L~SC~)* / *(L~Q~* * *L~S~)*   |
| Coverage2           | *Min(Sum(L~QC~), Sum(L~SC~))* / *Max(L~Q~, L~S~)* |
| Coverage Length     | *(Sum(L~QC~) + Sum(L~SC~))/2*                     |


After filtering, only edges are removed, while the vertices in each subgraph will stay the same. You need to click **Graph>Resort graphs** to sort separate clusters into their independent subgraphs.

#### 2.3.3 Graph Visualization and Control

##### 2.3.3.1 Visualize subgraphs
BlastGraph visualizes subgraphs in separate slides using [JUNG][JUNG] graph library. User can navigate to any subgraph
of interest using the arrows on the toolbar.

##### 2.3.3.2 Control Mode
To control an subgraph, there are three modes provided by the JUNG library: Transforming, Picking and Annotating.

- *Transforming*: This mode allow you to move and rotate the graph. `LeftMouse`+`drag` to move the graph and `LeftMouse`+`Shift`+`Drag` to rotate the graph.
- *Picking*: This mode allow you to select vertices and edges to view detail information on the "Element Info Panel" below and edit the graph by removing selected edges. You can click on an element to select one or drag to select all elements in a area. Use `Shift` when selecting to add elements to your selection. Click `RightMouse` on an edge or slected edges and select "Remove Edge" or "Remove Selected Edge" to remove the edges you want.
- *Annotation*: This mode allow you to add annotation to your current plot. `LeftMouse`+`drag` to add a shape annotation and `RightMouse` to add a text annotation.

##### 2.3.3.3 Graph Layout
Beside the mode selection tool, you can select the graph layout type to change the plot view. The default one is **FRLayout**, which is the implement of Fruchterman-Reingold force-directed algorithm provided by JUNG. This layout treat all edges equally when calculating the vertices distance. Another option is **EWLayout**, which is a modified version of **FRLayout** and takes edge weights into consideration. This allows similar genes/proteins to be laid together.

##### 2.3.3.4 Vertex Color
Vertices contain several basic attributes(see Fasta header information in 2.2.1). For a given attribute, different colors can be assigned to vertices with different attribute values. You can select different attributes on the toolbar, including Custom attribute, for different coloring modes. 

##### 2.3.3.5 Edit Subgraph
As described before, in the Picking mode, you can remove the edges which might not be strong enough on your criterion. You can also remove those independent vertices by selecting **Edit>Remove Single Vertices" if you like, as they are meaningless when computing the gene content tree. Be sure to resort subgraphs after editing.

##### 2.3.3.6 General and Statistics Information 
On the right of the graph plot is the graph general and statistics information. From top to bottom, you can see four information sections, not including progress section.

- *Main Info*: Information about the current open graph file(vertix, edge and subgraph count).
- *Current Graph*: Information about current viewing subgraph(vertix, edge count and the degree percentage). Degree percentage is defined by the number of edges (*Ne*) divide by the max number of edges (*Nv(Nv-1)/2*) it can contain.
- *Selected*: The number of vertices and edges currently selected.
- *Statistics*: The histogram and line plot of selected values(vertex Length, LogEvalue, Coverage, Coverage2, ScoreDensity, PercentageIdentity, PercentagePositive).

These information will assist your edge filtering and graph editing.

#### 2.3.4 Gene Content
If you are satisfied with your result after clustering and thorough pruning, you can build your gene content table and view it in a separate window by clicking **Tools>Gene Content Table**.

![The screen shot of gene content window.][GeneContent]

In this table, the rows correspond to genomes and the columns correspond to gene families. The element *N~ij~* of row *i* and column *j* represent the number of presence of gene j in genome i. This table shows all the gene presence, absence
and gene duplication information among all given genomes. It will serve as the raw data of phyletic pattern matrix, gene content phylogenetics and gene gain and loss analysis.

#### 2.3.5 Gene Content phylogeny
BlastGraph can estimate gene content tree based on gene content distance. The gene content table will be converted into a phyletic pattern matrix, where the gene number equal and greater than 1 are considered to be the same(presence).

In the Gene Content Table, Click **Phylogeny>Gene Content Cluster** to built a simple hierarchical cluster base on the number of genes shared by each two clades.

To built gene content tree, Click **Phylogeny>Gene Content Tree**, set the parameters as described below and run until the Tree Window(See 2.3.6) pop up.

Three methods are provided for estimation of genome content distance (Table 3.). The first two methods use both presence (1) and absence (0) information while the last one only considers presence (1) information. Thus, bootstrapping which randomly samples columns representing gene families can be used as an estimate of branch support method for the first two measures and the Delete-Half Jackknife method, which randomly samples half of the present genes, is used for the estimating support for the last method.

Table: Calculated metrics used for estimation of genome content distance.
*G~1~*(Genome 1); *G~2~*(Genome 2); *N~o~*(Number of one); *N~z~*(Number of zero);
*N~gf~*(Number of gene families); *N~mm~*(Number of mismatch, 10 or 01); 
*N~om~*(Number of one match, 11); *N~zm~*(Number of zero match, 00).

| **Method**       | **Distance Calculation**                                              | **Branch Supprot**    |
| :-----------     | :-------------------------                                            | :------------         |
| Simple Matching  | *N~mm~*(*G~1~*, *G~2~*) / *N~gf~*                                     | Bootstrap             |
| Jeccard Distance | *N~mm~*(*G~1~*, *G~2~*) / [*N~gf~* - *N~zm~*(*G~1~*, *G~2~*)]         | Bootstrap             |
| Berend Snel      | *1* - *N~om~*(*G~1~*, *G~2~*) / *Max*[*N~o~*(*G~1~*), *N~o~*(*G~2~*)] | Delete-Half Jackknife |


#### 2.3.6 Tree Window
Either gene content cluster or tree can be viewed and edited instantly in the tree window.

![Screen shot of tree window][Tree]

You can set the parameters on the tool bar to edit the tree, change the tree type and plot data on the tree.

#### 2.3.7 Gene Gain and Loss
Check the *GainLoss* option on the tool bar to show the gene gain and loss data. Pie chart and data can be plotted on to the tree.

Two Methods used for gene gain and loss estimation

- *Simple Mapping*: Given a tree and gene states (0: absent; 1: present) of terminal nodes, the internal node gene state is estimated 1 only if it's direct children are all 1; 
- *Maximum Parsimony*: Given a tree and gene states of terminal nodes, the internal node gene states is estimated by minimizing the gene state changes throughout the tree. The gene gain and loss events are then counted based on the state results and the contributions to both events from unresolved states are calculated based on state probability.

[MCL]: http://micans.org/mcl "MCL"
[Cygwin]: http://cygwin.com/ "Cygwin" 
[JUNG]: http://jung.sourceforge.net/ "JUNG"


[MainProgram]: ./images/main_program.png?raw=true "MainProgram"
[GeneContent]: ./images/gene_content.png?raw=true "GeneContent"
[Tree]: ./images/tree.png?raw=true "Tree"