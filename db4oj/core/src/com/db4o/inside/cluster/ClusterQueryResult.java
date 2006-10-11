/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.cluster;

import com.db4o.*;
import com.db4o.cluster.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.query.*;
import com.db4o.query.*;

/**
 * 
 * @exclude
 */
public class ClusterQueryResult implements QueryResult{
    
    private final Cluster _cluster;
    private final ObjectSet[] _objectSets;
    private final int[] _sizes;
    private final int _size;
    
    public ClusterQueryResult(Cluster cluster, Query[] queries){
        _cluster = cluster;
        _objectSets = new ObjectSet[queries.length]; 
        _sizes = new int[queries.length];
        int size = 0;
        for (int i = 0; i < queries.length; i++) {
            _objectSets[i] = queries[i].execute();
            _sizes[i] = _objectSets[i].size();
            size += _sizes[i];
        }
        _size = size;
    }
    
    public IntIterator4 iterateIDs() {
		synchronized(_cluster) {
			final Iterator4[] iterators = new Iterator4[_objectSets.length];
			for (int i = 0; i < _objectSets.length; i++) {
				iterators[i] = ((ObjectSetFacade)_objectSets[i])._delegate.iterateIDs();
			}
			return new IntIterator4() {
				private final CompositeIterator4 _delegate = new CompositeIterator4(iterators);
				
				public boolean moveNext() {
					return _delegate.moveNext();
				}
			
				public Object current() {
					return _delegate.current();
				}
			
				public int currentInt() {
					return ((IntIterator4)_delegate.currentIterator()).currentInt();
				}
			}; 
		} 
	}

    
	public Iterator4 iterator() {
		synchronized(_cluster) {
			Iterator4[] iterators = new Iterator4[_objectSets.length];
			for (int i = 0; i < _objectSets.length; i++) {
				iterators[i] = ((ObjectSetFacade)_objectSets[i])._delegate.iterator();
			}
			return new CompositeIterator4(iterators);
		} 
	}

    public int size() {
        return _size;
    }
    
    public Object get(int index) {
        synchronized(_cluster){
            if (index < 0 || index >= size()) {
                throw new IndexOutOfBoundsException();
            }
            int i = 0;
            while(index >= _sizes[i]){
                index -= _sizes[i];
                i++;
            }
            return ((ObjectSetFacade)_objectSets[i]).get(index); 
        }
    }

    public Object streamLock() {
        return _cluster;
    }

    public ObjectContainer objectContainer() {
        throw new NotSupportedException();
    }

    public int indexOf(int id) {
        Exceptions4.notSupported();
        return 0;
    }

	public void sort(QueryComparator cmp) {
        Exceptions4.notSupported();
	}
}

