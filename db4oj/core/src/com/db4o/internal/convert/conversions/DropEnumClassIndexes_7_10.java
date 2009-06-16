/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.internal.convert.conversions;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.convert.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class DropEnumClassIndexes_7_10 extends Conversion{
	
	public static final int VERSION = 9;
	
    public void convert(ConversionStage.SystemUpStage stage)
    {
    	if(! Deploy.csharp){
    		return;
    	}
        LocalObjectContainer file = stage.file();
		Reflector reflector = file.reflector();
        ClassMetadataIterator i = file.classCollection().iterator();
        while (i.moveNext()){
            ClassMetadata classmetadata = i.currentClass();
            if(Platform4.isEnum(reflector, classmetadata.classReflector()))
            {
                classmetadata.dropClassIndex();
            }
        }
    }

}
