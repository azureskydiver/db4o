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
    
    public static final int VERSION = 5;
    
    private static Converter _converter;
    
    private Hashtable4 _conversions;
    
    private Converter(){
        _conversions = new Hashtable4();
        
        // TODO: There probably will be Java and .NET conversions
        //       Create Platform4.registerConversions() method ann
        //       call from here when needed.
        CommonConversions.register(this);

    }
    
    public void register(int idx, Conversion conversion){
        if(_conversions.get(idx) != null){
            throw new IllegalStateException();
        }
        _conversions.put(idx, conversion);
    }
    
    
    private static boolean needsConversion(FileHeader0 fileHeader){
        if(fileHeader.converterVersion() >= VERSION){
            return false;
        }
        if(_converter == null){
            _converter = new Converter();
        }
        return true;
    }
    
    
    public static void convertWhenClassCollectionAvailable(YapFile file, FileHeader0 fileHeader) {
        if(!needsConversion(fileHeader)){
            return;
        }
        _converter.convertWhenClassCollectionAvailable1(file, fileHeader);
    }
    
    public static final boolean convertWhenSystemIsUp(YapFile file, FileHeader0 fileHeader){
        if(!needsConversion(fileHeader)){
            return false;
        }
        _converter.convertWhenSystemIsUp1(file, fileHeader);
        return true;
    }
    
    private void convertWhenSystemIsUp1(YapFile file, FileHeader0 fileHeader){
        for (int i = fileHeader.converterVersion(); i <= VERSION; i++) {
            Conversion conversion = (Conversion)_conversions.get(i);
            if(conversion != null){
                conversion.setFile(file);
                conversion.convertWhenSystemIsUp();
            }
        }
        fileHeader.converterVersion(VERSION);
        fileHeader.writeVariablePart1();
    }

    private void convertWhenClassCollectionAvailable1(YapFile file, FileHeader0 fileHeader){
        for (int i = fileHeader.converterVersion(); i <= VERSION; i++) {
            Conversion conversion = (Conversion)_conversions.get(i);
            if(conversion != null){
                conversion.setFile(file);
                conversion.convertWhenClassCollectionAvailable();
            }
        }
    }

}
