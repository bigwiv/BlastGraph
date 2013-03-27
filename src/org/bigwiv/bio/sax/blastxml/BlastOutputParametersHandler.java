package org.bigwiv.bio.sax.blastxml;

import org.biojava.bio.seq.io.game.ElementRecognizer;
import org.biojava.utils.stax.StAXContentHandler;

public class BlastOutputParametersHandler extends StAXFeatureHandler {
	// create static factory class that makes an instance
    // of this class.
    public final static StAXHandlerFactory BLASTOUTPUT_PARAMETERS_HANDLER_FACTORY
             =
        new StAXHandlerFactory() {
            @Override
			public StAXContentHandler getHandler(StAXFeatureHandler staxenv) {
                return new BlastOutputParametersHandler(staxenv);
            }
        };

    // constructor
    public BlastOutputParametersHandler(StAXFeatureHandler staxenv)
    {
        super(staxenv);
//        System.out.println("BlastOutputParametersHandler staxenv " + staxenv);

        // delegate handling of <Parameters>
        super.addHandler(new ElementRecognizer.ByLocalName("Parameters"),
        	ParametersHandler.PARAMETERS_HANDLER_FACTORY);
    }
}
