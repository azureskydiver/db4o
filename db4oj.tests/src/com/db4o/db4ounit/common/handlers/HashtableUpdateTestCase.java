/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import java.util.*;

import com.db4o.*;
import com.db4o.db4ounit.util.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;

/**
 * @sharpen.ignore
 */
public class HashtableUpdateTestCase extends HandlerUpdateTestCaseBase {
	
    private static final Object[] DATA = new Object[] { 
        "one",
        "aAzZ|!§$%&/()=?ßöäüÄÖÜYZ;:-_+*~#^°'@",
        "",
        createNestedList(1),
    };
    
    private static List createNestedList(int depth){
        List list = new ArrayList();
        list.add("nested1");
        list.add("nested2");
        if(depth > 0){
            list.add(createNestedList(depth - 1));
        }
        return list;
    }

    protected String typeName() {
        return "ArrayList";
    }
    
    public static class Item {
        
        public String _mapClassName;
        
        public Hashtable _typed;
        
        public Object _untyped;
        
        public Hashtable _emptyTyped;
        
        public Object _emptyUntyped;
        
        public Map _interface;
        
        public Map _emptyInterface;
        
    }
    
    /** Todo: add as type to Item **/
    public static class HashtableExtensionWithField extends Hashtable{
        
        public static final String STORED_NAME = "outListsName";
        
        public String name;
        
        public boolean equals(Object obj) {
            if(! super.equals(obj)){
                return false;
            }
            HashtableExtensionWithField other = (HashtableExtensionWithField) obj;
            if(name == null){
                return other.name == null;
            }
            return name.equals(other.name);
        }
    }
    
    /** Todo: add as type to Item **/
    public static class HashtableExtensionWithoutField extends Hashtable{
        
    }
    
    
    protected Object[] createValues() {
        if(testNotCompatibleToOldVersion()){
            return new Item[0];
        }
        Item[] values = new Item[3];
        values[0] = createItem(Hashtable.class);
        values[1] = createItem(HashtableExtensionWithField.class);
        values[2] = createItem(HashtableExtensionWithoutField.class);
        return values;
    }
    
    private Item createItem(Class clazz){
        Item item = new Item();
        item._mapClassName = clazz.getName();
        createMaps(item, clazz);
        return item;
    }

    private void createMaps(Item item, Class clazz) {
        item._typed = (Hashtable) createFilledMap(clazz);
        item._untyped = createFilledMap(clazz);
        item._interface = createFilledMap(clazz);
        item._emptyTyped = (Hashtable)createMap(clazz);
        item._emptyUntyped = createMap(clazz);
        item._emptyInterface = createMap(clazz);
    }
    
    private Map createFilledMap(Class clazz){
        Map map = createMap(clazz); 
        fillMap(map);
        if( map instanceof HashtableExtensionWithField){
        	HashtableExtensionWithField typedList = (HashtableExtensionWithField) map;
            typedList.name = HashtableExtensionWithField.STORED_NAME;
        }
        return map;
    }

    private Map createMap(Class clazz) {
        Map map = null;
        try {
            map = (Map) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
    
    private void fillMap(Object map){
        for (int i = 0; i < DATA.length; i++) {
            ((Map)map).put(DATA[i], DATA[i]);
        }
    }
    
    protected Object createArrays() {
        return null;
    }
    
    protected void assertValues(ExtObjectContainer objectContainer, Object[] values) {
        if(testNotCompatibleToOldVersion()){
            return;
        }
        assertItem(values[0], Hashtable.class);
        assertItem(values[1], HashtableExtensionWithField.class);
        assertItem(values[2], HashtableExtensionWithoutField.class);
    }
    
    protected void assertQueries(ExtObjectContainer objectContainer) {
        if(testNotCompatibleToOldVersion()){
            return;
        }
        assertQueries(objectContainer, Hashtable.class);
        assertQueries(objectContainer, HashtableExtensionWithField.class);
        assertQueries(objectContainer, HashtableExtensionWithoutField.class);
    }
    
    private void assertQueries(ExtObjectContainer objectContainer, Class clazz){
        assertQuery(objectContainer, clazz, "_typed");
//        assertQuery(objectContainer, clazz, "_untyped");
//        assertQuery(objectContainer, clazz, "_interface");
    }

    
    private void assertQuery(ExtObjectContainer objectContainer, Class clazz, String fieldName ){
        Query q = objectContainer.query();
        q.constrain(Item.class);
        q.descend("_mapClassName").constrain(clazz.getName());
        q.descend(fieldName).constrain("one");
        ObjectSet objectSet = q.execute();
        Assert.areEqual(1, objectSet.size());
        Item item = (Item) objectSet.next();
        assertItem(item, clazz);
    }
    

    private void assertItem(Object obj, Class clazz) {
        Item item = (Item) obj;
        assertMap(item._typed, clazz);
        assertMap(item._untyped, clazz);
        assertMap(item._interface, clazz);
        assertEmptyMap(item._emptyTyped);
        assertEmptyMap(item._emptyUntyped);
        assertEmptyMap(item._emptyInterface);
    }
    
    private void assertEmptyMap(Object obj) {
        Map map = (Map) obj;
        Assert.isTrue(map.isEmpty());
        Assert.areEqual(0, map.size());
        Assert.areEqual(0, map.keySet().size());
    }

    private void assertMap(Object obj, Class clazz) {
        Map map = (Map) obj;
        Object[] array = new Object[map.size()];
        int idx = 0;
        Iterator i = map.keySet().iterator();
        while(i.hasNext()){
            array[idx++] = i.next();
        }
        ArrayAssert.contains(DATA, array);
        Assert.areEqual(DATA.length, array.length);
        for (int j = 0; j < DATA.length; j++) {
        	Object mapValue = map.get(DATA[j]);
        	if(! (mapValue instanceof ArrayList)){
        		// For the ArrayList we have an issue with activation: Don't compare
    			Assert.areEqual(DATA[j], mapValue);
        	}
		}
        
        Assert.isInstanceOf(clazz, map);
        if( map instanceof HashtableExtensionWithField){
        	HashtableExtensionWithField typedList = (HashtableExtensionWithField) map;
            Assert.areEqual(HashtableExtensionWithField.STORED_NAME, typedList.name);
        }

    }

    protected void assertArrays(ExtObjectContainer objectContainer, Object obj) {
        // do nothing
    }
    
    private boolean testNotCompatibleToOldVersion() {
        // This test fails for 3.0 and 4.0 versions, probably
        // because translators are incompatible.
        
        if(db4oMajorVersion() < 5) {
            return true;
        }
        return db4oHeaderVersion() == VersionServices.HEADER_30_40;
    }




}
