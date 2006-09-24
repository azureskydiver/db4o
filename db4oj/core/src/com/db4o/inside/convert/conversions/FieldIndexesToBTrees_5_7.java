/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert.conversions;

import com.db4o.*;
import com.db4o.header.*;
import com.db4o.inside.convert.Conversion;
import com.db4o.inside.convert.ConversionStage.*;


/**
 * @exclude
 */
public class FieldIndexesToBTrees_5_7 extends Conversion{
    
    public static final int VERSION = 6;

    public void convert(ClassCollectionAvailableStage stage) {
    	super.convert(stage);
    }
    
	public void convert(SystemUpStage stage) {
        stage.file().classCollection().writeAllClasses();        
		rebuildUUIDIndex(stage.file());
    	freeOldUUIDMetaIndex(stage.file());
    }
	
    private void rebuildUUIDIndex(YapFile file) {
		final YapFieldUUID uuid = file.getFieldUUID();
		final YapClassCollectionIterator i = file.classCollection().iterator();
		while (i.moveNext()) {
			final YapClass clazz = i.currentClass();
			if (clazz.generateUUIDs()) {
				uuid.rebuildIndexForClass(file, clazz);
			}
		}
	}

	private void freeOldUUIDMetaIndex(YapFile file) {
        FileHeader fh = file.getFileHeader();
        if(! (fh instanceof FileHeader0)){
            return;
        }
		final MetaIndex metaIndex = ((FileHeader0)fh).getUUIDMetaIndex();
        if(metaIndex == null){
            return;
        }
        file.free(metaIndex.indexAddress, metaIndex.indexLength);
	}
}
