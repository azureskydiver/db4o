/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.inside.btree.*;

import db4ounit.db4o.*;


public class BTreeTestCaseBase extends Db4oTestCase{

    protected YapStream stream() {
        return (YapStream) db();
    }

    protected Transaction trans() {
        return stream().getTransaction();
    }

    private Transaction systemTrans() {
        return stream().getSystemTransaction();
    }

    protected BTree createIntKeyBTree(int id) {
        return new BTree(trans(), id, new YInt(stream()), null);
    }

    protected BTree createIntKeyValueBTree(int id) {
        return new BTree(trans(), id, new YInt(stream()), new YInt(stream()));
    }

}
