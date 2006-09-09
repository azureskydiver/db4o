/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert.conversions;

import com.db4o.inside.convert.*;

/**
 * @exclude
 */
public class CommonConversions {
    
    public static void register(Converter converter){
        converter.register(5, new ClassIndexesToBTrees());
    }
    
}
