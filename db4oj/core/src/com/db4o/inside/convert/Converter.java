/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.convert.conversions.*;

/**
 * @exclude
 */
public class Converter {
    
    /**
     * redundant count to prevent loading all the Conversions classes
     * into the ClassLoader if not needed.
     */
    private static final int VERSION = 1;
    
    private Hashtable4 _conversions;
    
    public static final void convert(YapConfigBlock config){
        if(config.converterVersion() >= VERSION){
            return;
        }
        new Converter().run(config);
    }
    
    private Converter(){
        _conversions = new Hashtable4(1);
        new CommonConversions(this);
        // TODO: There probably will be Java and .NET conversions
        //       Create Platform4.registerConversions() method ann
        //       call from here when needed.
    }
    
    public void register(int idx, Conversion conversion){
        if(_conversions.get(idx) != null){
            Exceptions4.shouldNeverHappen();
        }
        _conversions.put(idx, conversion);
    }
    
    private void run(YapConfigBlock config){
        int start = config.converterVersion();
        for (int i = start; i <= VERSION; i++) {
            Conversion conversion = (Conversion)_conversions.get(i);
            if(conversion != null){
                conversion.run();
            }
        }
        config.converterVersion(VERSION);
        config.write();
    }

}
