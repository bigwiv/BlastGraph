package org.bigwiv.bio.sax.blastxml;

import org.biojava.bio.seq.io.game.ElementRecognizer;
import org.biojava.utils.stax.StAXContentHandler;

public class IterationStatHandler extends StAXFeatureHandler {
	

    // create static factory class that makes an instance
    // of this class.
    public final static StAXHandlerFactory ITERATION_STAT_HANDLER_FACTORY
             =
        new StAXHandlerFactory() {
            @Override
			public StAXContentHandler getHandler(StAXFeatureHandler staxenv) {
                return new IterationStatHandler(staxenv);
            }
        };

    // constructor
    public IterationStatHandler(StAXFeatureHandler staxenv)
    {
        super(staxenv);
        //System.out.println("IterationStatHandler staxenv " + staxenv);
        // delegate handling of <Statistics>
        super.addHandler(new ElementRecognizer.ByLocalName("Statistics"),
            StatHandler.STAT_HANDLER_FACTORY);
    }
}
