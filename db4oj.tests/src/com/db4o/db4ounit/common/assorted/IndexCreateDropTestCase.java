/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import java.util.Date;

import com.db4o.config.ObjectClass;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class IndexCreateDropTestCase extends AbstractDb4oTestCase{
    
    public static class IndexCreateDropItem {
        
        public int _int;
        
        public String _string;
        
        public Date _date;

        public IndexCreateDropItem(int int_, String string_, Date date_) {
            _int = int_;
            _string = string_;
            _date = date_;
        }
        
        public IndexCreateDropItem(int int_) {
            this(int_, int_ == 0 ? null : "" + int_, int_ == 0 ? null : new Date(int_));
        }

    }

    
    private final int[] VALUES = new int[]{4, 7, 6, 6, 5, 4, 0, 0};
    
    public static void main(String[] arguments) {
        new IndexCreateDropTestCase().runSolo();
    }
    
    protected void store(){
        for (int i = 0; i < VALUES.length; i++) {
            db().set(new IndexCreateDropItem(VALUES[i]));
        }
    }
    
    public void test() throws Exception{
        assertQueryResults();
        assertQueryResults(true);
        assertQueryResults(false);
        assertQueryResults(true);
    }
    
    private void assertQueryResults(boolean indexed) throws Exception{
        indexed(indexed);
        reopen();
        assertQueryResults();
    }
    
    private void indexed(boolean flag){
        ObjectClass oc = fixture().config().objectClass(IndexCreateDropItem.class);
        oc.objectField("_int").indexed(flag);
        oc.objectField("_string").indexed(flag);
        oc.objectField("_date").indexed(flag);
    }
    
    protected Query newQuery(){
        Query q = super.newQuery();
        q.constrain(IndexCreateDropItem.class);
        return q;
    }
    
    private void assertQueryResults(){
        Query q = newQuery();
        q.descend("_int").constrain(new Integer(6));
        assertQuerySize(2, q);
        
        q = newQuery();
        q.descend("_int").constrain(new Integer(4)).greater();
        assertQuerySize(4, q);
        
        q = newQuery();
        q.descend("_int").constrain(new Integer(4)).greater().equal();
        assertQuerySize(6, q);
        
        q = newQuery();
        q.descend("_int").constrain(new Integer(7)).smaller().equal();
        assertQuerySize(8, q);
        
        q = newQuery();
        q.descend("_int").constrain(new Integer(7)).smaller();
        assertQuerySize(7, q);
        
        q = newQuery();
        q.descend("_string").constrain("6");
        assertQuerySize(2, q);
        
        q = newQuery();
        q.descend("_string").constrain("7");
        assertQuerySize(1, q);
        
        q = newQuery();
        q.descend("_string").constrain("4");
        assertQuerySize(2, q);
        
        q = newQuery();
        q.descend("_string").constrain(null);
        assertQuerySize(2, q);
        
        q = newQuery();
        q.descend("_date").constrain(new Date(4)).greater();
        assertQuerySize(4, q);
        
        q = newQuery();
        q.descend("_date").constrain(new Date(4)).greater().equal();
        assertQuerySize(6, q);
        
        q = newQuery();
        q.descend("_date").constrain(new Date(7)).smaller().equal();
        assertQuerySize(6, q);
        
        q = newQuery();
        q.descend("_date").constrain(new Date(7)).smaller();
        assertQuerySize(5, q);
        
        q = newQuery();
        q.descend("_date").constrain(null);
        assertQuerySize(2, q);
        
    }

    private void assertQuerySize(int size, Query q) {
        Assert.areEqual(size, q.execute().size());
    }

}
