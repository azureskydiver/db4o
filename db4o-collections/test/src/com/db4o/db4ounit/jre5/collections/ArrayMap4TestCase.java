/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import java.util.*;

import com.db4o.collections.*;

import db4ounit.*;

public class ArrayMap4TestCase implements TestLifeCycle {

    private ArrayMap4<String, Integer> map;

    public static void main(String[] args) {
        new TestRunner(ArrayMap4TestCase.class).run();
    }

    public void setUp() throws Exception {
        map = new ArrayMap4<String, Integer>();
        for (int i = 0; i < 10; i++) {
            map.put(String.valueOf(i), Integer.valueOf(i * 100));
        }
    }

    public void tearDown() throws Exception {
        map.clear();
    }

    // Function Test
    public void testConstructor() {
        ArrayMap4<String, String> m = new ArrayMap4<String, String>();
        Assert.isNotNull(m);
        Assert.areEqual(0, m.size());
        Assert.isTrue(m.isEmpty());
    }

    public void testClear() {
        Assert.areEqual(10, map.size());
        Assert.isFalse(map.isEmpty());

        map.clear();
        Assert.areEqual(0, map.size());
        Assert.isTrue(map.isEmpty());
    }

    @SuppressWarnings("unchecked")
    public void testClone() {
        Assert.areEqual(10, map.size());
        Assert.isFalse(map.isEmpty());

        ArrayMap4<String, Integer> clone = (ArrayMap4<String, Integer>) map
                .clone();
        Assert.areEqual(10, clone.size());

        for (int i = 0; i < 10; i++) {
            Assert.areEqual(Integer.valueOf(i * 100), clone.get(String
                    .valueOf(i)));
        }
    }

    public void testContainsKey() {
        for (int i = 0; i < 10; i++) {
            Assert.isTrue(map.containsKey(String.valueOf(i)));
        }

        Assert.isFalse(map.containsKey("10"));
    }

    public void testContainsValue() {
        for (int i = 0; i < 10; i++) {
            Assert.isTrue(map.containsValue(Integer.valueOf(i * 100)));
        }

        Assert.isFalse(map.containsValue("1"));
    }

    public void testEntrySet() {
        Set<Map.Entry<String, Integer>> set = map.entrySet();
        Assert.areEqual(10, set.size());

        for (int i = 0; i < 10; i++) {
            ArrayMap4.MapEntry4<String, Integer> entry = new ArrayMap4.MapEntry4<String, Integer>(
                    String.valueOf(i), Integer.valueOf(i * 100));
            Assert.isTrue(set.contains(entry));
        }
    }

    public void testGet() {
        for (int i = 0; i < 10; i++) {
            Integer value = map.get(String.valueOf(i));
            Assert.areEqual(Integer.valueOf(i * 100), value);
        }
    }

    public void testIsEmpty() {
        ArrayMap4<String, String> m = new ArrayMap4<String, String>();
        Assert.isTrue(m.isEmpty());
        m.put("key", "value");
        Assert.isFalse(m.isEmpty());
    }

    public void testKeySet() {
        Set<String> set = map.keySet();
        Assert.areEqual(10, set.size());
        for (int i = 0; i < 10; i++) {
            set.contains(String.valueOf(i));
        }
    }

    public void testPut() {
        ArrayMap4<String, String> m = new ArrayMap4<String, String>();
        m.put("one", "yi");
        m.put("two", "er");
        m.put("three", "san");
        Assert.areEqual(3, m.size());
        Assert.areEqual("yi", m.get("one"));
        Assert.areEqual("er", m.get("two"));
        Assert.areEqual("san", m.get("three"));
    }

    public void testPutAll() {
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

    public void testRemove() {
        ArrayMap4<String, String> m = new ArrayMap4<String, String>();
        m.put("one", "yi");
        m.put("two", "er");
        Assert.areEqual(2, m.size());
        String value = m.remove("one");
        Assert.areEqual("yi", value);
        Assert.areEqual(1, m.size());
        Assert.isNull(m.get("one"));

        value = m.remove("three");
        Assert.isNull(value);
        Assert.areEqual(1, m.size());

        value = m.remove("two");
        Assert.areEqual("er", value);
        Assert.areEqual(0, m.size());
        Assert.isNull(m.get("two"));

        m.put("three", "san");
        Assert.areEqual(1, m.size());

    }

    public void testSize() {
        Assert.areEqual(10, map.size());
        map.remove("1");
        Assert.areEqual(9, map.size());
        map.put("x", Integer.valueOf(1234));
        Assert.areEqual(10, map.size());
    }

    public void testValues() {
        Collection<Integer> values = map.values();
        for (int i = 0; i < 10; i++) {
            Assert.isTrue(values.contains(Integer.valueOf(i * 100)));
        }
    }

    // db4o - ta test

}
