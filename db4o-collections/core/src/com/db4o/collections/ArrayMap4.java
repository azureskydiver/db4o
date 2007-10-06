/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.collections;

import java.io.*;
import java.util.*;

import com.db4o.activation.*;

public class ArrayMap4<K, V> implements Map<K, V>, Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private ArrayList4<K> keys;

    private ArrayList4<V> values;

    private transient Activator _activator;

    public ArrayMap4() {
        this(16);
    }

    public ArrayMap4(int initialCapacity) {
        keys = new ArrayList4<K>(initialCapacity);
        values = new ArrayList4<V>(initialCapacity);
    }

    public void activate() {
        if (_activator != null) {
            _activator.activate();
        }
    }

    public void bind(Activator activator) {
        if (_activator != null) {
            throw new IllegalStateException();
        }
        _activator = activator;
    }

    public void clear() {
        keys.clear();
        values.clear();
    }

    public boolean containsKey(Object key) {
        return keys.contains(key);
    }

    public boolean containsValue(Object value) {
        return values.contains(value);
    }

    public Set<Map.Entry<K, V>> entrySet() {
        HashSet<Map.Entry<K, V>> set = new HashSet<Entry<K, V>>();
        for (int i = 0; i < keys.size(); i++) {
            MapEntry4<K, V> entry = new MapEntry4<K, V>(keys.get(i), values
                    .get(i));
            set.add(entry);
        }
        return set;
    }

    public V get(Object key) {
        int index = keys.indexOf(key);
        return index == -1 ? null : values.get(index);
    }

    public boolean isEmpty() {
        return keys.size() == 0;
    }

    public Set<K> keySet() {
        return new HashSet<K>(keys);
    }

    public V put(K key, V value) {
        int index = keys.indexOf(key);
        V oldValue = null;
        if (index == -1) {
            keys.add(key);
            values.add(value);
        } else {
            oldValue = values.get(index);
            values.set(index, value);
        }

        return oldValue;
    }

    public void putAll(Map<? extends K, ? extends V> t) {
        for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public V remove(Object key) {
        int index = keys.indexOf(key);
        if (index == -1) {
            return null;
        }

        keys.remove(index);
        return values.remove(index);
    }

    public int size() {
        return keys.size();
    }

    public Collection<V> values() {
        return values;
    }

    @SuppressWarnings("unchecked")
    public Object clone() {
        try {
            ArrayMap4<K, V> mapClone = (ArrayMap4<K, V>) super.clone();
            mapClone.keys = (ArrayList4<K>) keys.clone();
            mapClone.values = (ArrayList4<V>) values.clone();
            return mapClone;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    public static class MapEntry4<K, V> implements Map.Entry<K, V> {

        private K key;

        private V value;

        public MapEntry4(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V oldValue = value;
            this.value = value;
            return oldValue;
        }

        @SuppressWarnings("unchecked")
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }

            MapEntry4<K, V> other = (MapEntry4<K, V>) o;

            return (key == null ? other.getKey() == null : key.equals(other
                    .getKey())
                    && value == null ? other.getValue() == null : value
                            .equals(other.getValue()));

        }

        public int hashCode() {
            return (key == null ? 0 : key.hashCode())
                    ^ (value == null ? 0 : value.hashCode());
        }
    }
}
