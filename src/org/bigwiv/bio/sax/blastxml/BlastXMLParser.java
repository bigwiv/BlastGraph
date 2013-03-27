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
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class parses NCBI Blast XML output.
 * @author David Huen
 * @modified Ye Yanbo
 */
public class BlastXMLParser
    extends StAXFeatureHandler
{
    boolean firstTime = true;

    // constructor
    public BlastXMLParser()
    {
        // this is the base element class
        this.staxenv = this;
//        System.out.println("staxenv " + staxenv);
        // just set a DefaultHandler: does nothing worthwhile.
        this.listener = new DefaultHandler();
    }

    /**
     * sets the ContentHandler for this object
     */
    public void setContentHandler(org.xml.sax.ContentHandler listener)
    {
        this.listener = listener;
    }

    /**
     * we override the superclass startElement method so we can determine the
     * the start tag type and use it to set up delegation for the superclass.
     */
    @Override
	public void startElement(
            String nsURI,
            String localName,
            String qName,
            Attributes attrs,
            DelegationManager dm)
        throws SAXException
    {
        //System.out.println("localName is " + localName);
        if (firstTime) {
            // what kind of tag do we have?
            if (localName.equals("BlastOutput")) {
                // this is a well-formed XML document from NCBI BLAST
                // pertaining to one search result
                super.addHandler(
                    new ElementRecognizer.ByLocalName("BlastOutput"),
                    new StAXHandlerFactory() {
                        @Override
						public StAXContentHandler getHandler(StAXFeatureHandler staxenv) {
                            return new BlastOutputHandler(staxenv);
                        }
                    }
                );
            }
            else {
                throw new SAXException("illegal element " + localName);
            }

            firstTime = false;
        }

        // now invoke delegation
//        super.startElement(nsURI, localName, qName, attrs, dm);

        level++;

        // perform delegation
        // we must delegate only on features that are directly attached.
        // if I do not check that that's so, any element of a kind I delegate
        // on will be detected any depth within unrecognized tags.
        if (level == 1) {
        //System.out.println("StaxFeaturehandler.startElement starting. localName: " + localName + " " + level);
            for (int i = handlers.size() - 1; i >= 0; --i) {
            	//System.out.println(handlers.size());
                Binding b = (Binding) handlers.get(i);
                if (b.recognizer.filterStartElement(nsURI, localName, qName, attrs)) {
                    dm.delegate(b.handlerFactory.getHandler(this));
                    return;
                }
            }
        }

        // call the element specific handler now.
        // remember that if we we have a delegation failure we pass here too!
        if (level == 1) {
            startElementHandler(nsURI, localName, qName, attrs);
        }
    }
}
