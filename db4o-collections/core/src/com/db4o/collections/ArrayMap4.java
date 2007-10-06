/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.collections;

import java.io.*;
import java.util.*;

import com.db4o.activation.*;
import com.db4o.ta.*;

public class ArrayMap4<K, V> implements Map<K, V>, Serializable, Cloneable, Activatable {

    private static final long serialVersionUID = 1L;

    private ArrayList4<K> _keys;

    private ArrayList4<V> _values;

    private transient Activator _activator;

    public ArrayMap4() {
        this(16);
    }

    public ArrayMap4(int initialCapacity) {
        _keys = new ArrayList4<K>(initialCapacity);
        _values = new ArrayList4<V>(initialCapacity);
    }

    public void activate() {
        if (_activator != null) {
            _activator.activate();
        }
    }

    public void bind(Activator activator) {
        if (_activator != null || activator == null) {
            throw new IllegalStateException();
        }
        _activator = activator;
    }

    public void clear() {
        _keys.clear();
        _values.clear();
    }

    public boolean containsKey(Object key) {
        return _keys.contains(key);
    }

    public boolean containsValue(Object value) {
        return _values.contains(value);
    }

    public Set<Map.Entry<K, V>> entrySet() {
        HashSet<Map.Entry<K, V>> set = new HashSet<Entry<K, V>>();
        for (int i = 0; i < _keys.size(); i++) {
            MapEntry4<K, V> entry = new MapEntry4<K, V>(_keys.get(i), _values
                    .get(i));
            set.add(entry);
        }
        return set;
    }

    public V get(Object key) {
        int index = _keys.indexOf(key);
        return index == -1 ? null : _values.get(index);
    }

    public boolean isEmpty() {
        return _keys.size() == 0;
    }

    public Set<K> keySet() {
        return new HashSet<K>(_keys);
    }

    public V put(K key, V value) {
        int index = _keys.indexOf(key);
        V oldValue = null;
        if (index == -1) {
            _keys.add(key);
            _values.add(value);
        } else {
            oldValue = _values.get(index);
            _values.set(index, value);
        }

        return oldValue;
    }

    public void putAll(Map<? extends K, ? extends V> t) {
        for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public V remove(Object key) {
        int index = _keys.indexOf(key);
        if (index == -1) {
            return null;
        }

        _keys.remove(index);
        return _values.remove(index);
    }

    public int size() {
        return _keys.size();
    }

    public Collection<V> values() {
        return _values;
    }

    @SuppressWarnings("unchecked")
    public Object clone() {
        try {
            ArrayMap4<K, V> mapClone = (ArrayMap4<K, V>) super.clone();
            mapClone._keys = (ArrayList4<K>) _keys.clone();
            mapClone._values = (ArrayList4<V>) _values.clone();
            return mapClone;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    public static class MapEntry4<K, V> implements Map.Entry<K, V> {

        private K _key;

        private V _value;

        public MapEntry4(K key, V value) {
            _key = key;
            _value = value;
        }

        public K getKey() {
            return _key;
        }

        public V getValue() {
            return _value;
        }

        public V setValue(V value) {
            V oldValue = value;
            this._value = value;
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

            return (_key == null ? other.getKey() == null : _key.equals(other
                    .getKey())
                    && _value == null ? other.getValue() == null : _value
                            .equals(other.getValue()));

        }

        public int hashCode() {
            return (_key == null ? 0 : _key.hashCode())
                    ^ (_value == null ? 0 : _value.hashCode());
        }
    }
}
