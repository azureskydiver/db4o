/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;
import com.db4o.query.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.db4o.*;


public class FieldIndexTestCase extends Db4oTestCase{
    
    private static final int[] IDS = new int[]{3,7,9,4};

    
    protected void configure() {
        Db4o.configure()
        .objectClass(FieldIndexItem.class)
        .objectField("_id")
        .indexed(true);
    }

    public void store(){
        for (int i = 0; i < IDS.length; i++) {
            db().set(new FieldIndexItem(IDS[i]));
        }
        db().commit();
    }
    
    public void testAllThere() throws Exception{
        for (int i = 0; i < IDS.length; i++) {
            Query q = db().query();
            q.constrain(FieldIndexItem.class);
            q.descend("_id").constrain(new Integer(IDS[i]));
            ObjectSet objectSet = q.execute();
            Assert.areEqual(1, objectSet.size());
            FieldIndexItem fii = (FieldIndexItem) objectSet.next();
            Assert.areEqual(IDS[i], fii._id);
        }
    }
    
    public void testAccessingBTree() throws Exception{
        
        store();
        
        YapStream stream = (YapStream)db();
        ReflectClass claxx = stream.reflector().forObject(new FieldIndexItem());
        YapClass yc = stream.getYapClass(claxx, false);
        YapField yf = yc.getYapField("_id");
        BTree bTree = yf.getIndex();
        
        Assert.isNotNull(bTree);
        
        Transaction trans = stream.getTransaction();
        
        for (int i = 0; i < IDS.length; i++) {

            final int ix = i;

            BTreeRange range = bTree.search(trans, new Integer(IDS[i]));
            
            CountingVisitor visitor = new CountingVisitor(new Visitor4() {
                
                public void visit(Object obj) {
                    int expected = IDS[ix];
                    int actual = ((Integer)obj).intValue();
                    Assert.areEqual(expected, actual);
                }
            });
            
            
            range.traverseKeys(visitor);
            
            Assert.areEqual(1, visitor.count());
        }
        
        
    }
    
}
