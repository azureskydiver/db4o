package com.db4o.test.jdk5;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;

public class Jdk5Tests implements TestSuite{
    
    public Class[] tests(){
        return new Class[] {
            Jdk5Tests.class,
        };
    }
    
    public void configure(){
        // Db4o.configure().objectClass(Jdk5Enum.class).persistStaticFieldValues();
    }
    
    
    public void testStoreRetrieve() {
        
        ObjectContainer db = Test.objectContainer();
        
        Jdk5Data<String> data=new Jdk5Data<String>("Test",Jdk5Enum.A);
        Jdk5Enum.A.reset();
        Test.ensure(Jdk5Enum.A.getCount() == 0);
        Jdk5Enum.A.incCount();
        Test.ensure(Jdk5Enum.A.getCount() == 1);
        Test.ensure(Jdk5Enum.B.getCount() == 0);
        Test.ensure(data.getType() == Jdk5Enum.A);
        Test.ensure(data.getSize() == 0);
        Test.ensure(data.getMax() == Integer.MIN_VALUE);
        data.add(2,4,6,1,3,5);
        
        Test.ensure(data.getSize() == 6);
        Test.ensure(data.getMax() == 6);
        
        db.set(data);
        Test.reOpen();
        db = Test.objectContainer();
        
        data=null;
        
        Query query=db.query();
        query.constrain(Jdk5Data.class);
        Query sub=query.descend("type");
        sub.constrain(Jdk5Enum.class);
        sub.constrain(Jdk5Enum.A);
        sub.descend("type").constrain("A");
        // sub.descend("count").constrain(Integer.valueOf(1));
        ObjectSet result=query.execute();
        Test.ensure(result.size() == 1);
        data=(Jdk5Data<String>)result.next();
        Test.ensure(data.getItem().equals("Test"));
        Test.ensure(Jdk5Enum.A == data.getType());
        Test.ensure(Jdk5Enum.A.name().equals(data.getType().name()));
        
        Test.ensure(data.getSize() == 6);
        Test.ensure(data.getMax() == 6);
        
        Test.ensure(Jdk5Data.class.isAnnotationPresent(Jdk5Annotation.class));
        
        db.close();
    }
}
