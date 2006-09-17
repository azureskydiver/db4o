/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert;

import com.db4o.foundation.*;
import com.db4o.header.*;
import com.db4o.inside.convert.conversions.*;

/**
 * @exclude
 */
public class Converter {
    
    public static final int VERSION = FieldIndexesToBTrees_5_7.VERSION;
    
    private static Converter _converter;
    
    private Hashtable4 _conversions;
    
    private Converter() {
        _conversions = new Hashtable4();
        
        // TODO: There probably will be Java and .NET conversions
        //       Create Platform4.registerConversions() method ann
        //       call from here when needed.
        CommonConversions.register(this);
    }

    public static boolean convert(ConversionStage stage) {
    	if(!needsConversion(stage.header())) {
    		return false;
    	}
    	if(_converter == null){
    		_converter = new Converter();
    	}
    	return _converter.runConversions(stage);
    }

    private static boolean needsConversion(FileHeader0 fileHeader) {
        return fileHeader.converterVersion() < VERSION;
    }

    public void register(int idx, Conversion conversion) {
        if(_conversions.get(idx) != null){
            throw new IllegalStateException();
        }
        _conversions.put(idx, conversion);
    }
    
    public boolean runConversions(ConversionStage stage) {
    	FileHeader0 fileHeader=stage.header();
        if(!needsConversion(stage.header())){
            return false;
        }
        for (int i = fileHeader.converterVersion(); i <= VERSION; i++) {
            Conversion conversion = (Conversion)_conversions.get(i);
            if(conversion != null){
                stage.accept(conversion);
            }
        }
        return true;
    }
    
}
