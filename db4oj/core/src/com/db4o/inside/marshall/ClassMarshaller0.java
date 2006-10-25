/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.classindex.*;
import com.db4o.inside.convert.conversions.*;


/**
 * @exclude
 */
public class ClassMarshaller0 extends ClassMarshaller{
    
    protected void readIndex(YapStream stream, YapClass clazz, YapReader reader) {
        int indexID = reader.readInt();
        if(! stream.maintainsIndices() || ! (stream instanceof YapFile)){
            return;
        }
        if(btree(clazz) != null){
            return;
        }
        clazz.index().read(stream, validIndexId(indexID));
        if(isOldClassIndex(indexID)){
            new ClassIndexesToBTrees_5_5().convert((YapFile)stream, indexID, btree(clazz));
            stream.setDirtyInSystemTransaction(clazz);
        }
    }

    private BTree btree(YapClass clazz) {
        return BTreeClassIndexStrategy.btree(clazz);
    }

    private int validIndexId(int indexID) {
        return isOldClassIndex(indexID) ? 0 : -indexID;
    }

    private boolean isOldClassIndex(int indexID) {
        return indexID > 0;
    }
    
    protected int indexIDForWriting(int indexID){
        return indexID;
    }
}
