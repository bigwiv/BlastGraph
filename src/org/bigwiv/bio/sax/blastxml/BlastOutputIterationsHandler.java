/**
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
import org.biojava.utils.stax.StAXContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

class BlastOutputIterationsHandler
    extends StAXFeatureHandler
{
    // create static factory class that makes an instance
    // of this class.
    public final static StAXHandlerFactory BLASTOUTPUT_ITERATIONS_HANDLER_FACTORY
             =
        new StAXHandlerFactory() {
            @Override
			public StAXContentHandler getHandler(StAXFeatureHandler staxenv) {
                return new BlastOutputIterationsHandler(staxenv);
            }
        };

    // constructor
    public BlastOutputIterationsHandler(StAXFeatureHandler staxenv)
    {
        super(staxenv);
//        System.out.println("BlastOutputIterationsHandler staxenv " + staxenv);

        // delegate handling of <Iteration>
        super.addHandler(new ElementRecognizer.ByLocalName("Iteration"),
            IterationHandler.ITERATION_HANDLER_FACTORY);
    }
    
    @Override
	public void startElementHandler(
            String nsURI,
            String localName,
            String qName,
            Attributes attrs)
             throws SAXException
    {
    	//System.out.println("StaxFeaturehandler.startElementHandler starting. localName: " + localName + " " + level);
        // generate start of <biojava:QueryCollection>
        staxenv.listener.startElement(biojavaUri, "QueryCollection", biojavaUri + ":QueryCollection", new AttributesImpl());
    }

    @Override
	public void endElementHandler(
            String nsURI,
            String localName,
            String qName,
            StAXContentHandler handler)
             throws SAXException
    {
        // generate end of <biojava:QueryCollection>
        staxenv.listener.endElement(biojavaUri, "QueryCollection", biojavaUri + ":QueryCollection");
        //System.out.println("StAXFeatureHandler endElementHandler called, localName, level: " + localName + " " + level);
    }
}
