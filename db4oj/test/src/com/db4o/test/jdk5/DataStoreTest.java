package com.db4o.test.jdk5;

import java.io.*;
import junit.framework.*;
import com.db4o.*;
import com.db4o.query.*;


public class DataStoreTest extends TestCase {
    public final static String YAPFILE="jdk15test.yap";
    
    public void testStoreRetrieve() {
        Db4o.configure().exceptionsOnNotStorable(true);
        new File(YAPFILE).delete();
        Data<String> data=new Data<String>("Test",DataType.A);
        assertEquals(0,DataType.A.getCount());
        DataType.A.incCount();
        assertEquals(1,DataType.A.getCount());
        assertEquals(0,DataType.B.getCount());
        assertSame(DataType.A,data.getType());
        assertEquals(0,data.getSize());
        assertEquals(Integer.MIN_VALUE,data.getMax());
        data.add(2,4,6,1,3,5);
        assertEquals(6,data.getSize());
        assertEquals(6,data.getMax());
        ObjectContainer db=Db4o.openFile(YAPFILE);
        db.set(data);
        db.close();
        data=null;
        db=null;
        db=Db4o.openFile(YAPFILE);
        Query query=db.query();
        query.constrain(Data.class);
        Query sub=query.descend("type");
        sub.constrain(DataType.class);
//        sub.constrain(DataType.A);
//        sub.descend("type").constrain("A");
        sub.descend("count").constrain(Integer.valueOf(1));
        ObjectSet result=query.execute();
        assertEquals(1,result.size());
        data=(Data<String>)result.next();
        assertEquals("Test",data.getItem());
        //assertSame(DataType.A,data.getType());
        assertEquals(DataType.A.name(),data.getType().name());
        assertEquals(6,data.getSize());
        assertEquals(6,data.getMax());
        assertTrue(Data.class.isAnnotationPresent(Db4oObjectClass.class));
        db.close();
    }
}
