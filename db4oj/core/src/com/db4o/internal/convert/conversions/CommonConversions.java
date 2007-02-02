/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.convert.conversions;

import com.db4o.internal.convert.*;

/**
 * @exclude
 */
public class CommonConversions {
    
    public static void register(Converter converter){
        converter.register(ClassIndexesToBTrees_5_5.VERSION, new ClassIndexesToBTrees_5_5());
        converter.register(FieldIndexesToBTrees_5_7.VERSION, new FieldIndexesToBTrees_5_7());
    }
    
    
    
}
