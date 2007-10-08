/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.config.*;
import com.db4o.reflect.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ArrayMap4TATestCase extends AbstractDb4oTestCase {

    public static void main(String[] args) {
        new ArrayMap4TATestCase().runSolo();
    }

    protected void store() throws Exception {
        ArrayMap4<String, Integer> map = new ArrayMap4<String, Integer>();
        for (int i = 0; i < 10; i++) {
            map.put(String.valueOf(i), Integer.valueOf(i * 100));
        }
        store(map);
    }

    protected void configure(Configuration config) throws Exception {
        config.add(new TransparentActivationSupport());
        config.activationDepth(0);
        super.configure(config);
    }
    
    @SuppressWarnings("unchecked")
    private ArrayMap4<String, Integer> retrieveOnlyInstance() {
        ArrayMap4<String, Integer> map = (ArrayMap4<String, Integer>) retrieveOnlyInstance(ArrayMap4.class);
        assertInitialStatus(map);
        return map;
    }

    private void assertInitialStatus(ArrayMap4<String, Integer> map) {
        Assert.isNull(getField(map, "_keys"));
        Assert.isNull(getField(map, "_values"));
        Assert.areEqual(0, getField(map, "_startIndex"));
        Assert.areEqual(0, getField(map, "_endIndex"));
    }

    private Object getField(Object obj, String fieldName) {
        ReflectClass parentClazz = reflector().forObject(obj);
        ReflectField field = parentClazz.getDeclaredField(fieldName);
        field.setAccessible();
        return field.get(obj);
    }
    
    public void testClear() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        map.clear();
        Assert.areEqual(0, map.size());
        Assert.isTrue(map.isEmpty());
    }

    @SuppressWarnings("unchecked")
    public void testClone() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4<String, Integer> clone = (ArrayMap4<String, Integer>) map
        .clone();
        Assert.areEqual(10, clone.size());

        for (int i = 0; i < 10; i++) {
            Assert.areEqual(Integer.valueOf(i * 100), clone.get(String
                    .valueOf(i)));
        }
    }
    
    public void testContainsKey() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        for (int i = 0; i < 10; i++) {
            Assert.isTrue(map.containsKey(String.valueOf(i)));
        }

        Assert.isFalse(map.containsKey("10"));
    }

    public void testContainsValue() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        for (int i = 0; i < 10; i++) {
            Assert.isTrue(map.containsValue(Integer.valueOf(i * 100)));
        }

        Assert.isFalse(map.containsValue("1"));
    }

    public void testEntrySet() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        Set<Map.Entry<String, Integer>> set = map.entrySet();
        Assert.areEqual(10, set.size());

        for (int i = 0; i < 10; i++) {
            ArrayMap4.MapEntry4<String, Integer> entry = new ArrayMap4.MapEntry4<String, Integer>(
                    String.valueOf(i), Integer.valueOf(i * 100));
            Assert.isTrue(set.contains(entry));
        }
    }

    public void testGet() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        for (int i = 0; i < 10; i++) {
            Integer value = map.get(String.valueOf(i));
            Assert.areEqual(Integer.valueOf(i * 100), value);
        }
    }

    public void testIsEmpty() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        Assert.isFalse(map.isEmpty());
        map.clear();
        Assert.isTrue(map.isEmpty());
    }

    public void testKeySet() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        Set<String> set = map.keySet();
        Assert.areEqual(10, set.size());
        for (int i = 0; i < 10; i++) {
            set.contains(String.valueOf(i));
        }
    }

    public void testPut() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        map.put("one", Integer.valueOf(1));
        map.put("two", Integer.valueOf(2));
        map.put("three", Integer.valueOf(3));
        Assert.areEqual(13, map.size());
        Assert.areEqual(Integer.valueOf(1), map.get("one"));
        Assert.areEqual(Integer.valueOf(2), map.get("two"));
        Assert.areEqual(Integer.valueOf(3), map.get("three"));
        
        map.put("two", Integer.valueOf(-2));
        Assert.areEqual(Integer.valueOf(-2), map.get("two"));
    }

    public void testPutAll() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4<String, Integer> other = new ArrayMap4<String, Integer>();
        for (int i = 10; i < 20; i++) {
            other.put(String.valueOf(i), Integer.valueOf(i * 100));
        }

        map.putAll(other);

        Assert.areEqual(20, map.size());
        for (int i = 0; i < 20; i++) {
            Assert.areEqual(Integer.valueOf(i * 100), map
                    .get(String.valueOf(i)));
        }
    }

    public void testRemove_FromHead() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        Integer value = map.remove("0");
        Assert.areEqual(Integer.valueOf(0), value);
        
        Assert.areEqual(9, map.size());
        for (int i = 1; i < 10; i++) {
            Assert.areEqual(Integer.valueOf(i * 100), map
                    .get(String.valueOf(i)));
        }
        Assert.isNull(map.get("0"));
    }

    public void testRemove_FromEnd() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        Integer value = map.remove("9");
        Assert.areEqual(Integer.valueOf(900), value);
        
        Assert.areEqual(9, map.size());
        for (int i = 0; i < 9; i++) {
            Assert.areEqual(Integer.valueOf(i * 100), map
                    .get(String.valueOf(i)));
        }
        Assert.isNull(map.get("9"));
    }

    public void testRemove_FromMiddle() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        Integer value = map.remove("5");
        Assert.areEqual(Integer.valueOf(500), value);
        
        Assert.areEqual(9, map.size());
        for (int i = 0; i < 5; i++) {
            Assert.areEqual(Integer.valueOf(i * 100), map
                    .get(String.valueOf(i)));
        }
        Assert.isNull(map.get("5"));
        
        for (int i = 6; i < 9; i++) {
            Assert.areEqual(Integer.valueOf(i * 100), map
                    .get(String.valueOf(i)));
        }
    }
    
    public void testSize() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        Assert.areEqual(10, map.size());
        map.remove("1");
        Assert.areEqual(9, map.size());
        map.put("x", Integer.valueOf(1234));
        Assert.areEqual(10, map.size());
    }

    public void testValues() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        Collection<Integer> values = map.values();
        Assert.areEqual(10, values.size());
        for (int i = 0; i < 10; i++) {
            Assert.isTrue(values.contains(Integer.valueOf(i * 100)));
        }
    }

    public void testEquals() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        ArrayMap4<String, Integer> other = new ArrayMap4<String, Integer>();
        for (int i = 0; i < 10; i++) {
            other.put(String.valueOf(i), Integer.valueOf(i * 100));
        }
        
        Assert.isTrue(map.equals(other));
        Assert.isTrue(other.equals(map));
        Assert.areEqual(map.hashCode(), other.hashCode());
        Assert.isFalse(map.equals(null));
        
        other.remove("5");
        Assert.isFalse(map.equals(other));
    }
    
    public void testIncreaseSize() {
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        
        for (int i = 10; i < 50; i++) {
            map.put(String.valueOf(i), Integer.valueOf(i * 100));
        }
        
        for (int i = 0; i < 50; i++) {
            Assert.areEqual(Integer.valueOf(i * 100), map.get(String.valueOf(i)));
        }
    }
}
