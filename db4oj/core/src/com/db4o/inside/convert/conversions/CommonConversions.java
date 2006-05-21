/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert.conversions;

import com.db4o.inside.convert.*;

/**
 * @exclude
 *
 */
public class CommonConversions {
    
    public CommonConversions(Converter converter){
        
        // Start with '5' to allow adding further converters to fix
        // possible remaining old problems before class indexes are
        // updated
        converter.register(5, new ClassIndexesToBTrees());
    }

}
