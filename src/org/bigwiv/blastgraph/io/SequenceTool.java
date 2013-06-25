/*
 * BlastGraph: a comparative genomics tool
 * Copyright (C) 2013  Yanbo Ye (yeyanbo289@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.bigwiv.blastgraph.io;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class SequenceTool {
	public static final String EUTILS_BASE_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/";

	/**
	 * Find fasta sequences from sourceFile by IDs in list and save in
	 * targetFile, return IDs unfound.
	 * 
	 * @param list
	 * @param targetFile
	 * @param sourceFile
	 * @return
	 * @throws NoSuchElementException
	 * @throws BioException
	 * @throws IOException
	 */
//	public static Set<String> getFastaFromFile(Set<String> list,
//			File targetFile, File sourceFile) throws NoSuchElementException,
//			BioException, IOException {
//		OutputStream os = new FileOutputStream(targetFile);
//		BufferedReader br = new BufferedReader(new FileReader(sourceFile));
//		RichSequenceIterator rsIterator = RichSequence.IOTools
//				.readFastaProtein(br, null);
//		SequenceDB targetDB = new HashSequenceDB();
//		int count = 0;
//		int size = list.size();
//		while (rsIterator.hasNext()) {
//			RichSequence sequence = rsIterator.nextRichSequence();
//			// System.out.println(sequence.getName());
//
//			if (list.contains(sequence.getIdentifier())) {
//				targetDB.addSequence(sequence);
//				list.remove(sequence.getIdentifier());
//				count++;
//			}
//
//			if (count == size)
//				break;
//		}
//
//		SequenceIterator sequenceIterator = targetDB.sequenceIterator();
//		RichSequence.IOTools.writeFasta(os, sequenceIterator, null);
//
//		return list;
//	}

	/**
	 * @param list
	 * @param db "nuccore" for nuceleotide, "protein" for protein
	 * @param type "fasta" for FASTA, "gb" or "gp" for GenBank file
	 * @param targetFile
	 * @throws IOException
	 */
	public static void getSequenceFromNCBI(Set<String> list, String db, String type, File targetFile)
			throws IOException {
		String efetch = EUTILS_BASE_URL + "efetch.fcgi?"
				+ "db=" + db + "&rettype=" + type + "&retmode=text&id=";

		for (String string : list) {
			efetch += string + ",";
		}
		UrlDownloader.saveUrlAs(efetch, targetFile);
	}
}
