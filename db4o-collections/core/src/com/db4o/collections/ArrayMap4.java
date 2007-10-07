/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.collections;

import java.io.*;
import java.util.*;

import com.db4o.activation.*;
import com.db4o.ta.*;

/**
 * db4o <code>ArrayMap4</code> is an implementation of java.util.Map, which
 * supports Transparent Activate.
 * 
 * db4o <code>ArrayMap4</code> uses two separate arrays to store keys and
 * Values
 */

public class ArrayMap4<K, V> implements Map<K, V>, Serializable, Cloneable,
        Activatable {

    private static final long serialVersionUID = 1L;

    private K[] _keys;

    private V[] _values;

    private int _startIndex;

    private int _endIndex;

    private transient Activator _activator;

    public ArrayMap4() {
        this(16);
    }

    public ArrayMap4(int initialCapacity) {
        initializeBackingArray(initialCapacity);
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
        _startIndex = 0;
        _endIndex = 0;
        Arrays.fill(_keys, null);
        Arrays.fill(_values, null);
    }

    public boolean containsKey(Object key) {
        return indexOf(_keys, key) != -1;
    }

    public boolean containsValue(Object value) {
        return indexOf(_values, value) != -1;
    }

    public Set<Map.Entry<K, V>> entrySet() {
        HashSet<Map.Entry<K, V>> set = new HashSet<Entry<K, V>>();
        for (int i = _startIndex; i < _endIndex; i++) {
            MapEntry4<K, V> entry = new MapEntry4<K, V>(_keys[i], _values[i]);
            set.add(entry);
        }
        return set;
    }

    public V get(Object key) {
        int index = indexOf(_keys, key);
        return index == -1 ? null : _values[index];
    }

    public boolean isEmpty() {
        return (_endIndex - _startIndex) == 0;
    }

    public Set<K> keySet() {
        HashSet<K> set = new HashSet<K>();
        for (int i = _startIndex; i < _endIndex; i++) {
            set.add(_keys[i]);
        }
        return set;
    }

    public V put(K key, V value) {
        int index = indexOf(_keys, key);
        V oldValue = null;
        if (index == -1) {
            add(key, value);
        } else {
            oldValue = _values[index];
            _values[index] = value;
        }

        return oldValue;
    }

    public void putAll(Map<? extends K, ? extends V> t) {
        for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        int index = indexOf(_keys, key);
        if (index == -1) {
            return null;
        }
        return (V) delete(index);
    }

    public int size() {
        return _endIndex - _startIndex;
    }

    public Collection<V> values() {
        return Arrays.asList(_values);
    }

    @SuppressWarnings("unchecked")
    public Object clone() {
        try {
            ArrayMap4<K, V> mapClone = (ArrayMap4<K, V>) super.clone();
            mapClone._keys =  _keys.clone();
            mapClone._values = _values.clone();
            return mapClone;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeBackingArray(int length) {
        _keys = (K[]) new Object[length];
        _values = (V[]) new Object[length];
    }
    
    private int indexOf(Object[] array, Object obj) {
        int index = -1;
        for (int i = _startIndex; i < _endIndex; i++) {
            if (array[i] ==null ? obj == null : array[i].equals(obj)) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    private void add(K key, V value) {
        increase();
        _keys[_endIndex] = key;
        _values[_endIndex] = value;
        
        _endIndex ++;
    }
    
    @SuppressWarnings("unchecked")
    private void increase() {
        if (_endIndex == _keys.length) {
            Object[] newKeys = new Object[_keys.length * 2];
            Object[] newValues = new Object[_values.length * 2];
            System.arraycopy(_keys, _startIndex, newKeys, 0, _endIndex - _startIndex);
            System.arraycopy(_values, _startIndex, newValues, 0, _endIndex - _startIndex);
            _keys = (K[]) newKeys;
            _values = (V[]) newValues;
        }
    }
    
    private Object delete(int index) {
        Object value = _values[index];
        for (int i = index; i < _endIndex -1; i++) {
            _keys[i] = _keys[index + 1];
            _values[i] = _values[index + 1];
        }
        _endIndex--;
        return value;
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
