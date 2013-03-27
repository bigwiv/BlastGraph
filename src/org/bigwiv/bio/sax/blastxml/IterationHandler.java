/*
 *  BioJava development code This code may be freely distributed and modified
 *  under the terms of the GNU Lesser General Public Licence. This should be
 *  distributed with the code. If you do not have a copy, see:
 *  http://www.gnu.org/copyleft/lesser.html Copyright for this code is held
 *  jointly by the individual authors. These should be listed in
 *
 *@author    doc comments. For more information on the BioJava project and its
 *      aims, or to join the biojava-l mailing list, visit the home page at:
 *      http://www.biojava.org/
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
 * @author David Huen
 * @modified Ye Yanbo
 */
class IterationHandler
    extends StAXFeatureHandler
{
    // create static factory class that makes an instance
    // of this class.
    public final static StAXHandlerFactory ITERATION_HANDLER_FACTORY
             =
        new StAXHandlerFactory() {
            @Override
			public StAXContentHandler getHandler(StAXFeatureHandler staxenv) {
                return new IterationHandler(staxenv);
            }
        };
        


     // class variables
     AttributesImpl iterQueryIdAttrs = null;
     String iterQuery_def = null;

    // constructor
    public IterationHandler(StAXFeatureHandler staxenv)
    {
        super(staxenv);
//        System.out.println("IterationHandler staxenv " + staxenv);

//      // handle <Iteration_iter-num> internally.
//      super.addHandler(new ElementRecognizer.ByLocalName("Iteration_iter-num"),
//          HitPropertyHandler.HIT_PROPERTY_HANDLER_FACTORY);
        
        // delegate handling of <Iteration_query-ID>
        super.addHandler(new ElementRecognizer.ByLocalName("Iteration_query-ID"),
            new StAXHandlerFactory() {
                @Override
				public StAXContentHandler getHandler(StAXFeatureHandler staxenv) {
                    return new StringElementHandlerBase() {
                    	
                    	@Override
						public void startElement(
                                String nsURI,
                                String localName,
                                String qName,
                                Attributes attrs,
                                DelegationManager dm)
                                throws SAXException
                            {
                                // generate start of <biojava:QueryDescription>
                    			iterQueryIdAttrs = new AttributesImpl();

                                // must call superclass to keep track of levels
                                super.startElement(nsURI, localName, qName, attrs, dm);
                            }
                    	
                        @Override
						public void setStringValue(String s)  throws SAXException {
                        	iterQueryIdAttrs.addAttribute(biojavaUri, "id", "id", CDATA, s.trim());
                        }
                    };
                }
            }
        );

        // delegate handling of <Iteration_query-def>
        super.addHandler(new ElementRecognizer.ByLocalName("Iteration_query-def"),
            new StAXHandlerFactory() {
                @Override
				public StAXContentHandler getHandler(StAXFeatureHandler staxenv) {
                    return new StringElementHandlerBase() {
                        @Override
						public void setStringValue(String s)  throws SAXException {
                        	iterQuery_def = s.trim();
                        }
                    };
                }
            }
        );
        
     // delegate handling of <Iteration_query-len>
        super.addHandler(new ElementRecognizer.ByLocalName("Iteration_query-len"),
            new StAXHandlerFactory() {
                @Override
				public StAXContentHandler getHandler(StAXFeatureHandler staxenv) {
                    return new StringElementHandlerBase() {
                    	
                    	@Override
						public void startElement(
                                String nsURI,
                                String localName,
                                String qName,
                                Attributes attrs,
                                DelegationManager dm)
                                throws SAXException
                            {
                                if (iterQueryIdAttrs == null) iterQueryIdAttrs = new AttributesImpl();

                                // must call superclass to keep track of levels
                                super.startElement(nsURI, localName, qName, attrs, dm);                            
                            }

                            @Override
							public void setStringValue(String s)  throws SAXException {
                            	iterQueryIdAttrs.addAttribute(biojavaUri, "queryLength", "queryLength", CDATA, s.trim());
                            }
                            
                            @Override
							public void endElement(
                                    String nsURI,
                                    String localName,
                                    String qName,
                                    StAXContentHandler handler)
                                    throws SAXException
                                {
                                    // necessary as staxenv cannot be final and therefore
                                    // staxenv.listener cannot be accessed from inner class
                                    ContentHandler listener = getListener();

                                    // get superclass to process the PCDATA for this element
                                    super.endElement(nsURI, localName, qName, handler);

                                    // we now generate the Query element
                                    listener.startElement(biojavaUri, "Query", biojavaUri + ":Query", bldsAttrs);

                                    listener.startElement(biojavaUri, "Header", biojavaUri + ":Header", new AttributesImpl());
                                    // we don't have raw output but it is compulsory
                                    listener.startElement(biojavaUri, "RawOutput", biojavaUri + ":RawOutput", new AttributesImpl());
                                    listener.endElement(biojavaUri, "RawOutput", biojavaUri + ":RawOutput");
                                    
                                    // create <biojava:QueryId> element
                                    if (iterQueryIdAttrs != null) {
                                    	iterQueryIdAttrs.addAttribute(biojavaUri, "metaData", "metaData", CDATA, "none");
                                        
                                    	listener.startElement(biojavaUri, "QueryId", biojavaUri + ":QueryId", iterQueryIdAttrs);
                                        listener.endElement(biojavaUri, "QueryId", biojavaUri + ":QueryId");
                                    }

                                    // generate start of <biojava:QueryDescription>
                                    if (iterQuery_def != null) {
                                        listener.startElement(biojavaUri, "QueryDescription", biojavaUri + ":QueryDescription", new AttributesImpl());
                                        listener.characters(iterQuery_def.toCharArray(), 0, iterQuery_def.length());
                                        listener.endElement(biojavaUri, "QueryDescription", biojavaUri + ":QueryDescription");
                                        
                                    }

                                    listener.startElement(biojavaUri, "DatabaseId", biojavaUri + ":DatabaseId", dbAttrs);
                                    listener.endElement(biojavaUri, "DatabaseId", biojavaUri + ":DatabaseId");
                                    listener.endElement(biojavaUri, "Header", biojavaUri + ":Header");
                                }
                    };
                }
            }
        );
        
        // delegate handling of <Iteration_hits>
        super.addHandler(new ElementRecognizer.ByLocalName("Iteration_hits"),
            IterationHitsHandler.ITERATION_HITS_HANDLER_FACTORY);
        
     // delegate handling of <Iteration_stat>
        super.addHandler(new ElementRecognizer.ByLocalName("Iteration_stat"),
        		IterationStatHandler.ITERATION_STAT_HANDLER_FACTORY);

    }

    @Override
	public void endElementHandler(
            String nsURI,
            String localName,
            String qName,
            StAXContentHandler handler)
             throws SAXException
    {
        staxenv.listener.endElement(biojavaUri, "Query", biojavaUri + ":Query");
    }

}
