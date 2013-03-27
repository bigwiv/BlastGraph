package org.bigwiv.bio.sax.blastxml;

import org.biojava.bio.seq.io.game.ElementRecognizer;
import org.biojava.utils.stax.DelegationManager;
import org.biojava.utils.stax.StAXContentHandler;
import org.biojava.utils.stax.StringElementHandlerBase;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class StatHandler extends StAXFeatureHandler {

	// create static factory class that makes an instance
	// of this class.
	public final static StAXHandlerFactory STAT_HANDLER_FACTORY = new StAXHandlerFactory() {
		@Override
		public StAXContentHandler getHandler(StAXFeatureHandler staxenv) {
			return new StatHandler(staxenv);
		}
	};

	private AttributesImpl statAttrs = null;

	// constructor
	public StatHandler(StAXFeatureHandler staxenv) {
		super(staxenv);

		// delegate handling of <Statistics_db-num>
		super.addHandler(
				new ElementRecognizer.ByLocalName("Statistics_db-num"),
				new StAXHandlerFactory() {
					@Override
					public StAXContentHandler getHandler(
							StAXFeatureHandler staxenv) {
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
			                            // generate start of <biojava:HitDescription>
										statAttrs = new AttributesImpl();

			                            // must call superclass to keep track of levels
			                            super.startElement(nsURI, localName, qName, attrs, dm);
			                        }
							 
							@Override
							public void setStringValue(String s) {

								statAttrs.addAttribute(biojavaUri, "databaseNumber", "databaseNumber", CDATA, s);
							}
						};
					}
				});
		
		// delegate handling of <Statistics_db-len>
		super.addHandler(
				new ElementRecognizer.ByLocalName("Statistics_db-len"),
				new StAXHandlerFactory() {
					@Override
					public StAXContentHandler getHandler(
							StAXFeatureHandler staxenv) {
						return new StringElementHandlerBase() {
							@Override
							public void setStringValue(String s) {
								statAttrs.addAttribute(biojavaUri, "databaseLength", "databaseLength",
										CDATA, s);
							}
						};
					}
				});
		
		// delegate handling of <Statistics_hsp-len>
		super.addHandler(
				new ElementRecognizer.ByLocalName("Statistics_hsp-len"),
				new StAXHandlerFactory() {
					@Override
					public StAXContentHandler getHandler(
							StAXFeatureHandler staxenv) {
						return new StringElementHandlerBase() {
							@Override
							public void setStringValue(String s) {
								statAttrs.addAttribute(biojavaUri, "hspLength", "hspLength",
										CDATA, s);
							}
						};
					}
				});
		
		// delegate handling of <Statistics_eff-space>
		super.addHandler(
				new ElementRecognizer.ByLocalName("Statistics_eff-space"),
				new StAXHandlerFactory() {
					@Override
					public StAXContentHandler getHandler(
							StAXFeatureHandler staxenv) {
						return new StringElementHandlerBase() {
							@Override
							public void setStringValue(String s) {
								statAttrs.addAttribute(biojavaUri, "efficientSpace", "efficientSpace",
										CDATA, s);
							}
						};
					}
				});
		
		// delegate handling of <Statistics_kappa>
		super.addHandler(
				new ElementRecognizer.ByLocalName("Statistics_kappa"),
				new StAXHandlerFactory() {
					@Override
					public StAXContentHandler getHandler(
							StAXFeatureHandler staxenv) {
						return new StringElementHandlerBase() {
							@Override
							public void setStringValue(String s) {
								statAttrs.addAttribute(biojavaUri, "kappa", "kappa",
										CDATA, s);
							}
						};
					}
				});
		
		// delegate handling of <Statistics_lambda>
		super.addHandler(
				new ElementRecognizer.ByLocalName("Statistics_lambda"),
				new StAXHandlerFactory() {
					@Override
					public StAXContentHandler getHandler(
							StAXFeatureHandler staxenv) {
						return new StringElementHandlerBase() {
							@Override
							public void setStringValue(String s) {
								statAttrs.addAttribute(biojavaUri, "lambda", "lambda",
										CDATA, s);
							}
						};
					}
				});
		
		// delegate handling of <Statistics_entropy>
		super.addHandler(
				new ElementRecognizer.ByLocalName("Statistics_entropy"),
				new StAXHandlerFactory() {
					@Override
					public StAXContentHandler getHandler(
							StAXFeatureHandler staxenv) {
						return new StringElementHandlerBase() {
							@Override
							public void setStringValue(String s) {
								statAttrs.addAttribute(biojavaUri, "entropy", "entropy",
										CDATA, s);
							}
						};
					}
				});
	}
	
	 @Override
	public void endElementHandler(
	            String nsURI,
	            String localName,
	            String qName,
	            StAXContentHandler handler)
	             throws SAXException
	    {
	        staxenv.listener.startElement(biojavaUri, "Statistics", biojavaUri + ":Statistics", statAttrs);
	        staxenv.listener.endElement(biojavaUri, "Statistics", biojavaUri + ":Statistics");
	        //System.out.println("StAXFeatureHandler endElementHandler called, localName, level: " + localName + " " + level);
	    } 
}
