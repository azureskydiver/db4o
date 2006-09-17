/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;

/**
 * database boot record. Responsible for ID generation, version generation and
 * holding a reference to the Db4oDatabase object of the ObjectContainer
 *
 * @exclude
 * @persistent
 */
public class PBootRecord extends P1Object implements Db4oTypeImpl, Internal4{

    public transient YapFile         i_stream;

    public Db4oDatabase       i_db;

    public long               i_versionGenerator;

    public int                i_generateVersionNumbers;

    public int                i_generateUUIDs;

    private transient boolean i_dirty;

    public MetaIndex          i_uuidMetaIndex;


    public PBootRecord(){
    }

    public int activationDepth() {
        return Integer.MAX_VALUE;
    }

    public void init() {
        i_uuidMetaIndex = new MetaIndex();
        i_dirty = true;
    }

    public MetaIndex getUUIDMetaIndex(){

        // TODO: This is legacy code for old database files.
        // Newer versions create i_uuidMetaIndex when PBootRecord
        // is created. Remove this code after June 2006.
        if (i_uuidMetaIndex == null) {
            i_uuidMetaIndex = new MetaIndex();
            Transaction systemTrans = i_stream.getSystemTransaction();
            i_stream.showInternalClasses(true);
            i_stream.setInternal(systemTrans, this, false);
            i_stream.showInternalClasses(false);
            systemTrans.commit();
        }

        return i_uuidMetaIndex;
    }

    public void setDirty(){
        i_dirty = true;
    }

    public void store(int a_depth) {
        if (i_dirty) {
            i_stream.showInternalClasses(true);
            super.store(a_depth);
            i_stream.showInternalClasses(false);
        }
        i_dirty = false;
    }

}