/*
 *                    BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 *      http://www.biojava.org/
 *
 */

package org.bigwiv.bio.sax.blastxml;

import org.biojava.bio.seq.io.game.ElementRecognizer;
import org.biojava.utils.stax.DelegationManager;
import org.biojava.utils.stax.StAXContentHandler;
import org.biojava.utils.stax.StringElementHandlerBase;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Class to parse NCBI Blast-XML output.
 * 
 * @author David Huen
 * @author Yanbo Ye
 */
class BlastOutputHandler extends StAXFeatureHandler {
	// create static factory class that makes an instance
	// of this class.
	public final static StAXHandlerFactory BLASTOUTPUT_HANDLER_FACTORY = new StAXHandlerFactory() {
		@Override
		public StAXContentHandler getHandler(StAXFeatureHandler staxenv) {
			return new BlastOutputHandler(staxenv);
		}
	};

	// constructor when this is a element class in a document
	public BlastOutputHandler(StAXFeatureHandler staxenv) {
		super(staxenv);
		 //System.out.println("BlastOutputHandler staxenv " + staxenv);
		// initialise delegation
		initDelegation();
	}

	// constructor
	private void initDelegation() {
		// delegate handling of <BlastOutput_param>
		// super.addHandler(new
		// ElementRecognizer.ByLocalName("BlastOutput_param"),
		// BlastOutputParamHandler.BLAST_OUTPUT_PARAM_HANDLER_FACTORY);

		// delegate handling of <BlastOutput_program>
		super.addHandler(new ElementRecognizer.ByLocalName(
				"BlastOutput_program"), new StAXHandlerFactory() {
			@Override
			public StAXContentHandler getHandler(StAXFeatureHandler staxenv) {
				return new StringElementHandlerBase() {
					@Override
					public void setStringValue(String s) throws SAXException {
						program = s.trim();

						// at this point, I can set the sequence types
						// note that the sequence type here is the form
						// in which blast DISPLAYS its output, not the
						// sequence type of either query or target.
						if (program.equals("blastn")) {
							querySequenceType = "dna";
							hitSequenceType = "dna";
						} else if (program.equals("blastp")) {
							querySequenceType = "protein";
							hitSequenceType = "protein";
						} else if (program.equals("blastx")) {
							// nucleotide query translated in all frames
							// against protein database.
							querySequenceType = "protein";
							hitSequenceType = "protein";
						} else if (program.equals("tblastn")) {
							// protein query against dna database in all frames
							// hit frame is displayed only, no query frame
							// irrespective of frame, both sequences displayed
							// in increasing seq DNA coordinates (by from-to).
							querySequenceType = "protein";
							hitSequenceType = "protein";
						} else if (program.equals("tblastx")) {
							// dna query translated in all frames against
							// dna database in all frames
							// irrespective of frame, both sequences displayed
							// in increasing seq DNA coordinates.
							querySequenceType = "protein";
							hitSequenceType = "protein";
						}else if (program.equals("psiblast")) {
							querySequenceType = "protein";
							hitSequenceType = "protein";
						} else
							throw new SAXException("unknown BLAST program.");
					}
				};
			}
		});

		// delegate handling of <BlastOutput_version>
		super.addHandler(new ElementRecognizer.ByLocalName(
				"BlastOutput_version"), new StAXHandlerFactory() {
			@Override
			public StAXContentHandler getHandler(StAXFeatureHandler staxenv) {
				return new StringElementHandlerBase() {

					@Override
					public void setStringValue(String s) throws SAXException {
						version = s.trim();
					}

					@Override
					public void startElement(String nsURI, String localName,
							String qName, Attributes attrs, DelegationManager dm)
							throws SAXException {
						// now generate my own start element
						super.startElement(nsURI, localName, qName, attrs, dm);
					}

					@Override
					public void endElement(String nsURI, String localName,
							String qName, StAXContentHandler handler)
							throws SAXException {
						// necessary as staxenv cannot be final and therefore
						// staxenv.listener cannot be accessed from inner class
						ContentHandler listener = getListener();

						super.endElement(nsURI, localName, qName, handler);

						// generate attributes

						// System.out.println("program, version " + program +
						// " " + version);
						if(dbAttrs == null) bldsAttrs = new AttributesImpl();
						if ((program != null) && (version != null)) {
							bldsAttrs.addAttribute(biojavaUri, "program",
									"program", CDATA, program);
							bldsAttrs.addAttribute(biojavaUri, "version",
									"version", CDATA, version);
						}
					}
				};
			}
		});

		// delegate handling of <BlastOutput_reference>
		// super.addHandler(new
		// ElementRecognizer.ByLocalName("BlastOutput_reference"),
		// SearchPropertyHandler.SEARCH_PROPERTY_HANDLER_FACTORY);

		// delegate handling of <BlastOutput_db>
		super.addHandler(new ElementRecognizer.ByLocalName("BlastOutput_db"),
				new StAXHandlerFactory() {
					@Override
					public StAXContentHandler getHandler(
							StAXFeatureHandler staxenv) {
						return new StringElementHandlerBase() {
							@Override
							public void setStringValue(String s)
									throws SAXException {
								databaseId = s.trim();
							}

							@Override
							public void startElement(String nsURI,
									String localName, String qName,
									Attributes attrs, DelegationManager dm)
									throws SAXException {
								// now generate my own start element
								super.startElement(nsURI, localName, qName,
										attrs, dm);
							}

							@Override
							public void endElement(String nsURI,
									String localName, String qName,
									StAXContentHandler handler)
									throws SAXException {
								
								super.endElement(nsURI, localName, qName,
										handler);
								
								ContentHandler listener = getListener();
								
								if(dbAttrs == null) dbAttrs = new AttributesImpl();
								
								if (databaseId != null) {
									dbAttrs.addAttribute(biojavaUri, "id",
											"id", CDATA, databaseId);
									dbAttrs.addAttribute(biojavaUri,
											"metadata", "metadata", CDATA,
											"none");
								}
							}

						};
					}
				});

		/*
		 * // delegate handling of <BlastOutput_query-ID> super.addHandler(new
		 * ElementRecognizer.ByLocalName("BlastOutput_query-ID"), new
		 * StAXHandlerFactory() { public StAXContentHandler
		 * getHandler(StAXFeatureHandler staxenv) { return new
		 * StringElementHandlerBase() { public void setStringValue(String s)
		 * throws SAXException { queryId = s.trim(); } }; } } );
		 * 
		 * // delegate handling of <BlastOutput_query-def> super.addHandler(new
		 * ElementRecognizer.ByLocalName("BlastOutput_query-def"), new
		 * StAXHandlerFactory() { public StAXContentHandler
		 * getHandler(StAXFeatureHandler staxenv) { return new
		 * StringElementHandlerBase() { public void setStringValue(String s)
		 * throws SAXException { queryDef = s.trim(); } }; } } );
		 * 
		 * // delegate handling of <BlastOutput_query-len> super.addHandler(new
		 * ElementRecognizer.ByLocalName("BlastOutput_query-len"), new
		 * StAXHandlerFactory() { public StAXContentHandler
		 * getHandler(StAXFeatureHandler staxenv) { return new
		 * StringElementHandlerBase() { public void setStringValue(String s)
		 * throws SAXException { queryLen = s.trim(); } }; } } );
		 */

		// delegate handling of <BlastOutput_param>
		super
				.addHandler(
						new ElementRecognizer.ByLocalName("BlastOutput_param"),
						BlastOutputParametersHandler.BLASTOUTPUT_PARAMETERS_HANDLER_FACTORY);

		// delegate handling of <BlastOutput_iterations>
		super
				.addHandler(
						new ElementRecognizer.ByLocalName(
								"BlastOutput_iterations"),
						BlastOutputIterationsHandler.BLASTOUTPUT_ITERATIONS_HANDLER_FACTORY);
	}

	@Override
	public void startElementHandler(String nsURI, String localName,
			String qName, Attributes attrs) throws SAXException {
		//System.out.println("StaxFeaturehandler.startElementHandler starting. localName: " + localName + " " + level);
		
		// setup the root element of the output
		// generate start of <biojava:BlastLikeDataSet>
		bldsAttrs = new AttributesImpl();
		bldsAttrs.addAttribute("", "xmlns", "xmlns", CDATA, "");
		bldsAttrs.addAttribute(biojavaUri, "biojava", "xmlns:biojava", CDATA,
				"http://www.biojava.org");
		staxenv.listener.startElement(biojavaUri, "BlastLikeDataSet", biojavaUri + ":BlastLikeDataSet", bldsAttrs);
	}

	@Override
	void endElementHandler(String nsURI, String localName, String qName,
			StAXContentHandler handler) throws SAXException {
		// generate end of <biojava:BlastLikeDataSet>
		staxenv.listener.endElement(biojavaUri, "BlastLikeDataSet", biojavaUri
				+ ":BlastLikeDataSet");
		//System.out.println("StAXFeatureHandler endElementHandler called, localName, level: " + localName + " " + level);
	}
}
