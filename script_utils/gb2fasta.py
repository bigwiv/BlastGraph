#! /usr/bin/env python
#   
# Extract CDS information in Genbank file into fasta amino acid(faa) or nucleic acid(fna) file.
# Or just simply extract all nucleotide(fasta).
# Biopython is required to run this script.
# @data: 2012-11-7
# @author: yeyanbo

import re
import getopt
import sys
from Bio import SeqIO
from Bio.Seq import Seq
from Bio.SeqRecord import SeqRecord
from Bio.Alphabet import generic_protein

def usage():
    usage = """Usage: python gb2fasta.py -i [input file] -o [output file] -t [output type]
    
Option:
  -h,--help      Print this usage
  -v,--verbose   Print progress information
  -i,--input     The genbank file to be processed
  -o,--output    The fasta file to be output to
  -t,--type      Output type of fasta file [faa, fna, fasta]
                  faa(default)   Extract the amino acid of all CDS
                  fna            Extract the nucleotides of all CDS
                  fasta          Extract nucleotide sequence in this genbank file
"""

    print usage

def main():
    try:
        opts, args = getopt.getopt(sys.argv[1:], "i:o:t:hv", ["input=", "output=", "type=", "help"])
    except getopt.GetoptError, err:
        print str(err)
        usage()
        sys.exit(2)

    infile = None
    outfile = None
    outtype = "faa"
    verbose = False

    types = ["faa", "fna", "fasta"]

    for o, a in opts:
        if o == "-v":
            verbose = True
        elif o in ("-h", "--help"):
            usage()
            sys.exit()
        elif o in ("-i", "--input"):
            infile = a
        elif o in ("-o", "--output"):
            outfile = a
        elif o in ("-t", "--type"):
            outtype = a
        else:
            assert False, "unhandled option"

    if outtype not in types:
        usage()
        sys.exit(2)

    if infile == None or outfile == None:
        usage()
        sys.exit(2)


    input_handle = open(infile, "r")
    output_handle = open(outfile, "w")

    faa_record = [] #store faa created from CDS

    for seq_record in SeqIO.parse(input_handle, "genbank") :
        gb_id = seq_record.id
        gb_gi = seq_record.annotations['gi']
        gb_dec = seq_record.description

        if verbose:
            print "Record %s" % gb_id

        if outtype == "fasta":
            seq_fasta = SeqRecord(seq=seq_record.seq, id="gi|%s|gb|%s|" %(gb_gi, gb_id), description = gb_dec)
            faa_record.append(seq_fasta)
        else:
            organism = seq_record.annotations["organism"]
            taxonomy = seq_record.annotations["taxonomy"]
            
            cds_count = 0 # count CDS in current record
            for seq_feature in seq_record.features :
                if seq_feature.type == "CDS" :
                    assert len(seq_feature.qualifiers['translation']) == 1
                    
                    cds_count += 1 
                    
                    strand = seq_feature.strand
                    
                    location = str(seq_feature.location.start + 1) + "-" + str(seq_feature.location.end)
                    
                    location = re.sub(r"[^0-9\-]", "", location)

                    product = ('product' in seq_feature.qualifiers) and seq_feature.qualifiers['product'][0] or seq_feature.qualifiers['gene'][0]
                    
                    product = product.replace("|", "");

                    xref = seq_feature.qualifiers['db_xref']
                    
                    r = re.compile('GI.*')
                    
                    gi = filter(r.match, xref)[0].replace("GI:", "")

                    if outtype == "faa":
                        seq = Seq(seq_feature.qualifiers['translation'][0] , generic_protein)
                    else:
                        seq = seq_record.seq[seq_feature.location.start:seq_feature.location.end]
                        if strand == -1:
                            seq = seq.reverse_complement()

                    seq_fasta = SeqRecord(seq, id="gi|%s|gb|%s|or|%s|st|%s|lo|%s|gn|%s|tx|%s|" % (gi, seq_feature.qualifiers['protein_id'][0], cds_count, strand, location, seq_record.name, ";".join(taxonomy)), description="%s [%s]" % (product, organism))
                    
                    faa_record.append(seq_fasta)
           
    SeqIO.write(faa_record, output_handle, "fasta")
        
    output_handle.close()
    input_handle.close()


if __name__ == "__main__":
    main()

