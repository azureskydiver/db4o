/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert.conversions;

import com.db4o.*;
import com.db4o.header.*;
import com.db4o.inside.*;
import com.db4o.inside.convert.Conversion;
import com.db4o.inside.convert.ConversionStage.*;


/**
 * @exclude
 */
public class FieldIndexesToBTrees_5_7 extends Conversion{
    
    public static final int VERSION = 6;

	public void convert(SystemUpStage stage) {
        stage.file().classCollection().writeAllClasses();        
		rebuildUUIDIndex(stage.file());
    	freeOldUUIDMetaIndex(stage.file());
    }
	
    private void rebuildUUIDIndex(LocalObjectContainer file) {
		final YapFieldUUID uuid = file.getUUIDIndex();
		final ClassMetadataIterator i = file.classCollection().iterator();
		while (i.moveNext()) {
			final ClassMetadata clazz = i.currentClass();
			if (clazz.generateUUIDs()) {
				uuid.rebuildIndexForClass(file, clazz);
			}
		}
	}

	private void freeOldUUIDMetaIndex(LocalObjectContainer file) {
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
