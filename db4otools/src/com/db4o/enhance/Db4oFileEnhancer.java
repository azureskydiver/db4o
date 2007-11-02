/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.enhance;

import com.db4o.instrumentation.classfilter.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.main.*;
import com.db4o.nativequery.main.*;
import com.db4o.ta.instrumentation.*;


/**
 * FIXME: Write documentation 
 */
public class Db4oFileEnhancer {
    
    public void enhance(String sourceDir, String targetDir) throws Exception{
        Db4oFileInstrumentor instrument = new Db4oFileInstrumentor(new BloatClassEdit[]{
            new TranslateNQToSODAEdit(),
            new InjectTransparentActivationEdit(new AcceptAllClassesFilter()),
        });
        instrument.enhance(sourceDir, targetDir, new String[]{}, "");
    }

}
