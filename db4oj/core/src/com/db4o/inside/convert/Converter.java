/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.header.*;
import com.db4o.inside.*;
import com.db4o.inside.convert.conversions.*;
import com.db4o.inside.marshall.*;

/**
 * @exclude
 */
public class Converter {
    
    public static final int VERSION = MarshallerFamily.LEGACY ? 0 : 5;
    
    private Hashtable4 _conversions;
    
    public static final boolean convert(YapFile file, FileHeader0 fileHeader){
        if(fileHeader.converterVersion() >= VERSION){
            return false;
        }
        Converter converter = new Converter();
        converter.run(file, fileHeader);
        return true;
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
    
    private void run(YapFile file, FileHeader0 fileHeader){
        int start = fileHeader.converterVersion();
        for (int i = start; i <= VERSION; i++) {
            Conversion conversion = (Conversion)_conversions.get(i);
            if(conversion != null){
                conversion.setFile(file);
                conversion.run();
            }
        }
        fileHeader.converterVersion(VERSION);
        fileHeader.writeVariablePart1();
    }

}
