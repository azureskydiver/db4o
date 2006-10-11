package com.db4o.objectmanager.api;

import com.db4o.ObjectContainer;
import com.db4o.ext.StoredClass;
import com.db4o.objectmanager.api.impl.DatabaseInspectorImpl;
import demo.objectmanager.model.Contact;
import com.db4o.reflect.ReflectClass;
import db4ounit.Assert;

import java.util.List;


/**
 * User: treeder
 * Date: Aug 9, 2006
 * Time: 10:54:10 AM
 */
public class DatabaseInspectorTest extends ObjectManagerTestCase {


    public void setUp() throws Exception {
        super.setUp();
        for (int i = 0; i < 10; i++) {
            Contact c = new Contact();
            c.setId(new Integer(i));
            c.setName("Contact " + i);
            c.setAge(i * 5);
            getDb().set(c);
        }
    }


    public void tearDown() throws Exception {
        super.tearDown();
    }


    public void testInspectorDump() {
        ObjectContainer oc = getDb();
        DatabaseInspector inspector = new DatabaseInspectorImpl(oc);
        print("Classes Stored:");
        print(inspector.getClassesStored());
        print("Number of classes: " + inspector.getNumberOfClasses());

        // checking stored classes
        StoredClass[] storedClasses = oc.ext().storedClasses();
        for (int i = 0; i < storedClasses.length; i++) {
            StoredClass storedClass = storedClasses[i];
            System.out.println("STORED: " + storedClass);
        }
    }

    private void print(List classesStored) {
        for (int i = 0; i < classesStored.size(); i++) {
            Object o = classesStored.get(i);
            print(o.toString());
        }
    }

    private void print(String s) {
        System.out.println(s);
    }

    public void testClassCounts() {
        ObjectContainer oc = getDb();
        DatabaseInspector inspector = new DatabaseInspectorImpl(oc);
        Assert.areEqual(1, inspector.getNumberOfClasses());

        Assert.areEqual(10, inspector.getNumberOfObjectsForClass(Contact.class.getName()));

        List storedClasses = inspector.getClassesStored();
        for (int i = 0; i < storedClasses.size(); i++) {
            ReflectClass reflectClass = (ReflectClass) storedClasses.get(i);
            ReflectClass reflectClassExpected = oc.ext().reflector().forName(Contact.class.getName());
            Assert.areEqual(reflectClassExpected, reflectClass);
        }
    }


}
