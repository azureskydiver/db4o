/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;
import com.db4o.inside.*;

/**
 * Old database boot record class. 
 * 
 * This class was responsible for storing the last timestamp id,
 * for holding a reference to the Db4oDatabase object of the 
 * ObjectContainer and for holding on to the UUID index.
 * 
 * This class is no longer needed with the change to the new
 * fileheader. It still has to stay here to be able to read
 * old databases.
 *
 * @exclude
 * @persistent
 */
public class PBootRecord extends P1Object implements Db4oTypeImpl, Internal4{

    public Db4oDatabase       i_db;

    public long               i_versionGenerator;

    public MetaIndex          i_uuidMetaIndex;

    public int activationDepth() {
        return Integer.MAX_VALUE;
    }

    public MetaIndex getUUIDMetaIndex(){
        return i_uuidMetaIndex;
    }

    public void write(YapFile file) {
        SystemData systemData = file.systemData();
        i_versionGenerator = systemData.lastTimeStampID();
        i_db = systemData.identity();
        file.showInternalClasses(true);
        store(2);
        file.showInternalClasses(false);
    }

}