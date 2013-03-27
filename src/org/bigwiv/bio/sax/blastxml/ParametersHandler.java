package org.bigwiv.bio.sax.blastxml;

import org.biojava.bio.seq.io.game.ElementRecognizer;
import org.biojava.utils.stax.StAXContentHandler;
import org.biojava.utils.stax.StringElementHandlerBase;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class ParametersHandler extends StAXFeatureHandler {

	// create static factory class that makes an instance
	// of this class.
	public final static StAXHandlerFactory PARAMETERS_HANDLER_FACTORY = new StAXHandlerFactory() {
		@Override
		public StAXContentHandler getHandler(StAXFeatureHandler staxenv) {
			return new ParametersHandler(staxenv);
		}
	};

	AttributesImpl paraAttrs;

	public ParametersHandler(StAXFeatureHandler staxenv) {
		super(staxenv);
		 //System.out.println("ParametersHandler staxenv " + staxenv);

		// delegate handling of <Parameters_matrix>
		super.addHandler(
				new ElementRecognizer.ByLocalName("Parameters_matrix"),
				new StAXHandlerFactory() {
					@Override
					public StAXContentHandler getHandler(
							StAXFeatureHandler staxenv) {
						return new StringElementHandlerBase() {
							@Override
							public void setStringValue(String s)
									throws SAXException {
								paraAttrs.addAttribute(biojavaUri, "matrix",
										"matrix", CDATA, s);
							}
						};
					}
				});

		// delegate handling of <Parameters_expect>
		super.addHandler(
				new ElementRecognizer.ByLocalName("Parameters_expect"),
				new StAXHandlerFactory() {
					@Override
					public StAXContentHandler getHandler(
							StAXFeatureHandler staxenv) {
						return new StringElementHandlerBase() {
							@Override
							public void setStringValue(String s)
									throws SAXException {
								paraAttrs.addAttribute(biojavaUri, "expect",
										"expect", CDATA, s);
							}
						};
					}
				});

		// delegate handling of <Parameters_gap-open>
		super.addHandler(new ElementRecognizer.ByLocalName(
				"Parameters_gap-open"), new StAXHandlerFactory() {
			@Override
			public StAXContentHandler getHandler(StAXFeatureHandler staxenv) {
				return new StringElementHandlerBase() {
					@Override
					public void setStringValue(String s) throws SAXException {
						paraAttrs.addAttribute(biojavaUri, "gapOpen",
								"gapOpen", CDATA, s);
					}
				};
			}
		});

		// delegate handling of <Parameters_gap-extend>
		super.addHandler(new ElementRecognizer.ByLocalName(
				"Parameters_gap-extend"), new StAXHandlerFactory() {
			@Override
			public StAXContentHandler getHandler(StAXFeatureHandler staxenv) {
				return new StringElementHandlerBase() {
					@Override
					public void setStringValue(String s) throws SAXException {
						paraAttrs.addAttribute(biojavaUri, "gapExtend",
								"gapExtend", CDATA, s);
					}
				};
			}
		});

		// delegate handling of <Parameters_filter>
		super.addHandler(
				new ElementRecognizer.ByLocalName("Parameters_filter"),
				new StAXHandlerFactory() {
					@Override
					public StAXContentHandler getHandler(
							StAXFeatureHandler staxenv) {
						return new StringElementHandlerBase() {
							@Override
							public void setStringValue(String s)
									throws SAXException {
								paraAttrs.addAttribute(biojavaUri, "filter",
										"filter", CDATA, s);
							}
						};
					}
				});
	}

	@Override
	public void startElementHandler(String nsURI, String localName,
			String qName, Attributes attrs) throws SAXException {
		
		//System.out.println("StaxFeaturehandler.startElementHandler starting. localName: " + localName + " " + level);
		// create an AttributesImpl to save the attributes to
		paraAttrs = new AttributesImpl();
	}

	@Override
	public void endElementHandler(String nsURI, String localName, String qName,
			StAXContentHandler handler) throws SAXException {
		if (paraAttrs != null) {
			paraAttrs.addAttribute(biojavaUri, "metaData", "metaData", CDATA,
					"none");
			staxenv.listener.startElement(biojavaUri, "Parameters", biojavaUri + ":Parameters",
					paraAttrs);
			staxenv.listener.endElement(biojavaUri, "Parameters", biojavaUri + ":Parameters");
		}
		
		//System.out.println("StAXFeatureHandler endElementHandler called, localName, level: " + localName + " " + level);

	}

}
