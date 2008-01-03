/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre11.defragment;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.defragment.*;
import com.db4o.db4ounit.jre11.defragment.SlotDefragmentVectorTestCase.*;
import com.db4o.defragment.*;
import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.*;

public class SlotDefragmentVectorUUIDTestCase implements TestCase {

    public static class Holder {
        public Vector _vector;

        public Holder(Vector vector) {
            this._vector = vector;
        }
    }
    
    // FIXME runs fine in db4oj suite, but fails in db4ojdk1.2 suite?!?
    public void _testVectorDefragment() throws Exception {
        store();
        defrag();
        query();
    }

    private void query() {
        ObjectContainer db=openDatabase();
        Query query=db.query();
        query.constrain(Holder.class);
        ObjectSet result=query.execute();
        Assert.areEqual(1,result.size());
        db.close();
    }

    private void defrag() throws IOException {
        DefragmentConfig config=new DefragmentConfig(SlotDefragmentTestConstants.FILENAME);
        config.forceBackupDelete(true);
        config.db4oConfig(configuration());
        Defragment.defrag(config);
    }

    private void store() {
        new File(SlotDefragmentTestConstants.FILENAME).delete();
        ObjectContainer db=openDatabase();
        db.store(new Holder(new Vector()));
        db.close();
    }

    private ObjectContainer openDatabase() {
        Configuration config = configuration();
        config.generateUUIDs(ConfigScope.GLOBALLY);
        return Db4o.openFile(config,SlotDefragmentTestConstants.FILENAME);
    }
    
    private Configuration configuration() {
        Configuration config = Db4o.newConfiguration();
        config.reflectWith(Platform4.reflectorForType(Data.class));
        return config;
    }

}