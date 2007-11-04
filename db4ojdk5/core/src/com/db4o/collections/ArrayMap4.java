/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.collections;

import java.io.*;
import java.util.*;

import com.db4o.activation.*;
import com.db4o.ta.*;

/**
 * Transparent activatable Map implementation.
 * Implements Map interface using two arrays to store keys and values.<br><br>
 * When instantiated as a result of a query, all the internal members
 * are NOT activated at all. When internal members are required to 
 * perform an operation, the instance transparently activates all 
 * the members.   
 * 
 * @see java.util.Map
 * @see com.db4o.ta.Activatable
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

	/**
	 * activate basic implementation.
	 * 
	 * @see com.db4o.ta.Activatable
	 */
    public void activate() {
        if (_activator != null) {
            _activator.activate();
        }
    }

	/**
	 * bind basic implementation.
	 * 
	 * @see com.db4o.ta.Activatable
	 */
    public void bind(Activator activator) {
        if (_activator != null || activator == null) {
            throw new IllegalStateException();
        }
        _activator = activator;
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    public void clear() {
        activate();
        
        _startIndex = 0;
        _endIndex = 0;
        Arrays.fill(_keys, null);
        Arrays.fill(_values, null);
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    public boolean containsKey(Object key) {
        activate();
        
        return indexOf(_keys, key) != -1;
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    public boolean containsValue(Object value) {
        activate();
        
        return indexOf(_values, value) != -1;
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    public Set<Map.Entry<K, V>> entrySet() {
        activate();
        
        HashSet<Map.Entry<K, V>> set = new HashSet<Entry<K, V>>();
        for (int i = _startIndex; i < _endIndex; i++) {
            MapEntry4<K, V> entry = new MapEntry4<K, V>(_keys[i], _values[i]);
            set.add(entry);
        }
        return set;
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    public V get(Object key) {
        activate();
        
        int index = indexOf(_keys, key);
        return index == -1 ? null : _values[index];
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    public boolean isEmpty() {
        activate();
        
        return (_endIndex - _startIndex) == 0;
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    public Set<K> keySet() {
        activate();
        
        HashSet<K> set = new HashSet<K>();
        for (int i = _startIndex; i < _endIndex; i++) {
            set.add(_keys[i]);
        }
        return set;
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    public V put(K key, V value) {
        activate();
        
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

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    public void putAll(Map<? extends K, ? extends V> t) {
        for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        activate();
        
        int index = indexOf(_keys, key);
        if (index == -1) {
            return null;
        }
        return (V) delete(index);
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    public int size() {
        activate();
        
        return _endIndex - _startIndex;
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    public Collection<V> values() {
        activate();
        
        ArrayList<V> list = new ArrayList<V>();
        for (int i = _startIndex; i < _endIndex; i++) {
            list.add(_values[i]);
        }
        return list;
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    @SuppressWarnings("unchecked")
    public Object clone() {
        activate();
        try {
            ArrayMap4<K, V> mapClone = (ArrayMap4<K, V>) super.clone();
            mapClone._keys =  _keys.clone();
            mapClone._values = _values.clone();
            return mapClone;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
    
	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        Map<K, V> other = (Map<K, V>) obj;
        if (size() != other.size()) {
            return false;
        }
        
        Set<K> otherKeySet = other.keySet(); 
        for (Map.Entry<K, V> entry : entrySet()) {
            K key = entry.getKey();
            if (!otherKeySet.contains(key)) {
                return false;
            }
            
            V value = entry.getValue();
            if (!(value == null ? other.get(key) == null : value.equals(other.get(key)))) {
                return false;
            }
        }
        return true;
    }
    
	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    public int hashCode() {
        int hashCode = 0;
        for (Map.Entry<K, V> entry : entrySet()) {
            hashCode += entry.hashCode();
        }
        return hashCode;
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
        ensureCapacity();
        _keys[_endIndex] = key;
        _values[_endIndex] = value;
        
        _endIndex ++;
    }
    
    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (_endIndex == _keys.length) {
            Object[] newKeys = new Object[_keys.length * 2];
            Object[] newValues = new Object[_values.length * 2];
            System.arraycopy(_keys, _startIndex, newKeys, 0, _endIndex - _startIndex);
            System.arraycopy(_values, _startIndex, newValues, 0, _endIndex - _startIndex);
            Arrays.fill(_keys, null);
            Arrays.fill(_values, null);
            _keys = (K[]) newKeys;
            _values = (V[]) newValues;
        }
    }
    
    private Object delete(int index) {
        Object value = _values[index];
        for (int i = index; i < _endIndex -1; i++) {
            _keys[i] = _keys[i + 1];
            _values[i] = _values[i + 1];
        }
        _endIndex--;
        _keys[_endIndex] = null;
        _values[_endIndex] = null;
        return value;
    }
    
    /**
     * @exclude
     */
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
