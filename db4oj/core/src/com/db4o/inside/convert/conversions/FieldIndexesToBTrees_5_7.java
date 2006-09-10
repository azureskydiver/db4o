/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert.conversions;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.inside.convert.*;
import com.db4o.inside.convert.ConversionStage.*;


/**
 * @exclude
 */
public class FieldIndexesToBTrees_5_7 extends Conversion{
    
    public static final int VERSION = 6;

    public void convert(ClassCollectionAvailableStage stage) {
        stage.file().classCollection().writeAllClasses();
    }

}
