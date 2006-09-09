/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert;

import com.db4o.foundation.*;
import com.db4o.header.*;
import com.db4o.inside.convert.conversions.*;

/**
 * @exclude
 */
public class Converter {
    
    public static final int VERSION = 5;
    
    public static boolean convert(ConversionStage stage) {
    	if(!needsConversion(stage.header())) {
    		return false;
    	}
    	if(CONVERTER == null){
    		CONVERTER = new Converter();
    	}
    	return CONVERTER.runConversions(stage);
    }

    private static Converter CONVERTER;

    private static boolean needsConversion(FileHeader0 fileHeader) {
        return fileHeader.converterVersion() >= VERSION;
    }

    private Hashtable4 _conversions;
    private Conversion updateVersionConv=new UpdateVersionConversion(VERSION);
    
    private Converter() {
        _conversions = new Hashtable4();
        
        // TODO: There probably will be Java and .NET conversions
        //       Create Platform4.registerConversions() method ann
        //       call from here when needed.
        CommonConversions.register(this);
        register(Integer.MAX_VALUE, new UpdateVersionConversion(VERSION));
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
        stage.accept(updateVersionConv);
        return true;
    }

}
