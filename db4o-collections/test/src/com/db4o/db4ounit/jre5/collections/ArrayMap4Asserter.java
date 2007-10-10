package com.db4o.db4ounit.jre5.collections;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.db4o.collections.ArrayMap4;

import db4ounit.Assert;

public class ArrayMap4Asserter {

    public static void putData(Map<String, Integer> map) {
        for (int i = 0; i < 10; i++) {
            map.put(String.valueOf(i), Integer.valueOf(i * 100));
        }
    }

    public static void assertInitalStatus(Map<String, Integer> map) {
        Assert.isNotNull(map);
        Assert.areEqual(0, map.size());
        Assert.isTrue(map.isEmpty());
    }

    public static void assertClear(Map<String, Integer> map) {
        Assert.areEqual(10, map.size());
        Assert.isFalse(map.isEmpty());

        map.clear();

        checkClear(map);
    }

    public static void checkClear(Map<String, Integer> map) {
        Assert.areEqual(0, map.size());
        Assert.isTrue(map.isEmpty());
    }

    @SuppressWarnings("unchecked")
    public static void assertClone(ArrayMap4<String, Integer> map) {
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

    public static void assertContainsKey(ArrayMap4<String, Integer> map) {
        for (int i = 0; i < 10; i++) {
            Assert.isTrue(map.containsKey(String.valueOf(i)));
        }

        Assert.isFalse(map.containsKey("10"));
    }

    public static void assertContainsValue(ArrayMap4<String, Integer> map) {
        for (int i = 0; i < 10; i++) {
            Assert.isTrue(map.containsValue(Integer.valueOf(i * 100)));
        }

        Assert.isFalse(map.containsValue("1"));
    }

    public static void assertEntrySet(ArrayMap4<String, Integer> map) {
        Set<Map.Entry<String, Integer>> set = map.entrySet();
        Assert.areEqual(10, set.size());

        for (int i = 0; i < 10; i++) {
            ArrayMap4.MapEntry4<String, Integer> entry = new ArrayMap4.MapEntry4<String, Integer>(
                    String.valueOf(i), Integer.valueOf(i * 100));
            Assert.isTrue(set.contains(entry));
        }
    }

    public static void assertGet(ArrayMap4<String, Integer> map) {
        for (int i = 0; i < 10; i++) {
            Integer value = map.get(String.valueOf(i));
            Assert.areEqual(Integer.valueOf(i * 100), value);
        }
    }

    public static void assertIsEmpty(ArrayMap4<String, Integer> map) {
        Assert.isFalse(map.isEmpty());
        map.clear();
        Assert.isTrue(map.isEmpty());
    }

    public static void assertKeySet(ArrayMap4<String, Integer> map) {
        Set<String> set = map.keySet();
        Assert.areEqual(10, set.size());
        for (int i = 0; i < 10; i++) {
            set.contains(String.valueOf(i));
        }
    }

    public static void assertPut(ArrayMap4<String, Integer> map) {
        map.put("one", Integer.valueOf(1));
        map.put("two", Integer.valueOf(2));
        map.put("three", Integer.valueOf(3));
        Assert.areEqual(13, map.size());
        Assert.areEqual(Integer.valueOf(1), map.get("one"));
        Assert.areEqual(Integer.valueOf(2), map.get("two"));
        Assert.areEqual(Integer.valueOf(3), map.get("three"));

        map.put("two", Integer.valueOf(-2));
        checkPut(map);
    }
    
    public static void checkPut(ArrayMap4<String, Integer> map) {
        Assert.areEqual(Integer.valueOf(-2), map.get("two"));
    }

    public static void assertPutAll(ArrayMap4<String, Integer> map) {
        ArrayMap4<String, Integer> other = new ArrayMap4<String, Integer>();
        for (int i = 10; i < 20; i++) {
            other.put(String.valueOf(i), Integer.valueOf(i * 100));
        }

        map.putAll(other);

        checkMap(map, 0, 20);
    }
    
    public static void checkMap(ArrayMap4<String, Integer> map, int start, int end) {
        Assert.areEqual(end - start, map.size());
        for (int i = start; i < end; i++) {
            Assert.areEqual(Integer.valueOf(i * 100), map
                    .get(String.valueOf(i)));
        }
    }

    public static void assertRemove_FromHead(ArrayMap4<String, Integer> map) {
        Integer value = map.remove("0");
        Assert.areEqual(Integer.valueOf(0), value);

        checkRemove(map, 1, 10, "0");
    }
    
    public static void checkRemove(ArrayMap4<String, Integer> map, int start, int end, String removedKey) {
        checkMap(map, start, end);
        Assert.isNull(map.get(removedKey));
    }

    public static void assertRemove_FromEnd(ArrayMap4<String, Integer> map) {
        Integer value = map.remove("9");
        Assert.areEqual(Integer.valueOf(900), value);

        checkRemove(map, 0, 9, "9");
    }

    public static void assertRemove_FromMiddle(ArrayMap4<String, Integer> map) {
        Integer value = map.remove("5");
        Assert.areEqual(Integer.valueOf(500), value);

        checkRemove_FromMiddle(map);
    }
    
    public static void checkRemove_FromMiddle(ArrayMap4<String, Integer> map) {
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

    public static void assertSize(ArrayMap4<String, Integer> map) {
        Assert.areEqual(10, map.size());
        map.remove("1");
        Assert.areEqual(9, map.size());
        map.put("x", Integer.valueOf(1234));
        Assert.areEqual(10, map.size());
    }

    public static void assertValues(ArrayMap4<String, Integer> map) {
        Collection<Integer> values = map.values();
        Assert.areEqual(10, values.size());
        for (int i = 0; i < 10; i++) {
            Assert.isTrue(values.contains(Integer.valueOf(i * 100)));
        }
    }

    public static void assertEquals(ArrayMap4<String, Integer> map) {
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

    public static void assertIncreaseSize(ArrayMap4<String, Integer> map) {
        for (int i = 10; i < 50; i++) {
            map.put(String.valueOf(i), Integer.valueOf(i * 100));
        }

        checkMap(map, 0, 50);
    }
}
