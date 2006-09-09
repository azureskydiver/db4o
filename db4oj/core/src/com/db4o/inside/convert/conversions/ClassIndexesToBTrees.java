/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert.conversions;

import com.db4o.*;
import com.db4o.header.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.convert.*;
import com.db4o.inside.convert.ConversionStage.*;


/**
 * @exclude
 */
public class ClassIndexesToBTrees implements Conversion {

    public void convert(YapFile yapFile, int classIndexId, BTree bTree){
        Transaction trans = yapFile.getSystemTransaction();
        YapReader reader = yapFile.readReaderByID(trans, classIndexId);
        if(reader == null){
            return;
        }
        int entries = reader.readInt();
        for (int i = 0; i < entries; i++) {
            bTree.add(trans, new Integer(reader.readInt()));
        }
    }

	public void convert(ClassCollectionAvailableStage stage) {
//		YapFile yapFile = stage.file();
//		Transaction trans = yapFile.getSystemTransaction();
//		YapReader reader = yapFile.readReaderByID(trans, classIndexId);
//		if (reader == null) {
//			return;
//		}
//		int entries = reader.readInt();
//		for (int i = 0; i < entries; i++) {
//			bTree.add(trans, new Integer(reader.readInt()));
//		}
	}

	public void convert(SystemUpStage stage) {
        // calling #storedClasses forces reading all classes
        // That's fair enough to load them.
        stage.file().storedClasses();
	}
}
