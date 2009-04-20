/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;
import com.db4o.internal.*;

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
public class PBootRecord extends P1Object implements Internal4{

    public Db4oDatabase       i_db;

    public long               i_versionGenerator;

    public MetaIndex          i_uuidMetaIndex;

    public MetaIndex getUUIDMetaIndex(){
        return i_uuidMetaIndex;
    }

    public void write(LocalObjectContainer file) {
    	// write is still called when storing objects to old
    	// database files (CLI1.ObjectInfoMigration52TestCase
    	// and CLI1.ObjectInfoMigration57)
    
    }

}