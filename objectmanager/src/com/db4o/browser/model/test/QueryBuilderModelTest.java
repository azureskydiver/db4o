/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model.test;

import java.io.File;

import junit.framework.TestCase;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.browser.model.Database;
import com.db4o.browser.model.Db4oDatabase;
import com.db4o.browser.query.model.FieldConstraint;
import com.db4o.browser.query.model.QueryBuilderModel;
import com.db4o.browser.query.model.QueryPrototypeInstance;
import com.db4o.browser.query.model.RelationOperator;
import com.db4o.reflect.ReflectClass;

public class QueryBuilderModelTest extends TestCase {
    private static class Data {
        int id;
        String name;

        public Data(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    
    private static class Container {
        Data data;
        String value;
        Container cyclicalReference = this;
        
        public Container(int id, String name, String value) {
            data = new Data(id, name);
            this.value = value;
        }
    }
    
    private final static String YAPFILENAME="querymodel.yap";
    
    private Database database;
    private ReflectClass clazz;
    private QueryBuilderModel model;
    private QueryPrototypeInstance proto;
    
    protected void setUp() {
        new File(YAPFILENAME).delete();
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
        db.set(new Data(1,"A"));
        db.set(new Data(2,"B"));
        db.set(new Data(3,"C"));
        
        db.set(new Container(9, "z", "q"));
        db.set(new Container(8, "y", "r"));
        db.set(new Container(7, "x", "s"));
        db.close();
        database=new Db4oDatabase();
        database.open(YAPFILENAME);
        clazz=database.reflector().forName(Data.class.getName());
        model=new QueryBuilderModel(clazz,database);
        proto=model.getRootInstance();
    }
    
    protected void tearDown() {
        database.close();
    }
    
    public void testClassOnly() {
        ObjectSet result=model.getQuery().execute();
        assertEquals(6,result.size());
        while(result.hasNext()) {
            Data data=(Data)result.next();
        }
    }
    
    public void testRootPrototype() {
        String[] fieldNames=proto.getFieldNames();
        assertEquals(2,fieldNames.length);
        FieldConstraint constraint=proto.getConstraint("id");
        assertEquals(constraint.field.getName(),"id");
        assertEquals(RelationOperator.EQUALS,constraint.relation);
        assertNull(constraint.value);
    }

    public void testSingleConstraint() {
        FieldConstraint constraint=proto.getConstraint("id");
        constraint.value=new Integer(1);
        ObjectSet result=model.getQuery().execute();
        assertEquals(1,result.size());
        Data data=(Data)result.next();
        assertEquals(1,data.id);
        assertEquals("A",data.name);
        
        constraint.value=new Integer(3);
        constraint.relation=RelationOperator.SMALLER;
        result=model.getQuery().execute();
        assertEquals(2,result.size());
    }
    
    public void testCombinedConstraints() {
        FieldConstraint nameConstraint=proto.getConstraint("name");
        nameConstraint.value="A";
        nameConstraint.relation=RelationOperator.GREATER;
        ObjectSet result=model.getQuery().execute();
        assertEquals(5,result.size());
        FieldConstraint idConstraint=proto.getConstraint("id");
        idConstraint.value=new Integer(2);
        result=model.getQuery().execute();
        assertEquals(1,result.size());
        
    }
    
    public void testNestedClasses() {
        ReflectClass clazz=database.reflector().forName(Container.class.getName());
        QueryBuilderModel model=new QueryBuilderModel(clazz,database);
        QueryPrototypeInstance proto=model.getRootInstance();
        
        FieldConstraint nameConstraint = proto.getConstraint("data").valueProto().getConstraint("name");
        nameConstraint.value = "y";
        nameConstraint.relation = RelationOperator.GREATER;
        ObjectSet result = model.getQuery().execute();
        assertEquals(1, result.size());
        Container resultObj = (Container) result.next();
        assertEquals("q", resultObj.value);
        assertEquals("z", resultObj.data.name);
    }
}
