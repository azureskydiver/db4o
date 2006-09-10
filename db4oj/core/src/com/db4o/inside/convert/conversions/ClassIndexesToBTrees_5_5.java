/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert.conversions;

import com.db4o.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.convert.*;
import com.db4o.inside.convert.ConversionStage.*;


/**
 * @exclude
 */
public class ClassIndexesToBTrees_5_5 extends Conversion {
    
    public static final int VERSION = 5;

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

	public void convert(SystemUpStage stage) {
        
        // calling #storedClasses forces reading all classes
        // That's good enough to load them all and to call the
        // above convert method.
        
        stage.file().storedClasses();
	}
}
