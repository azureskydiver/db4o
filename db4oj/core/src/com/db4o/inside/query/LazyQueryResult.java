/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.classindex.*;
import com.db4o.query.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class LazyQueryResult implements QueryResult {
	
	private final Transaction _transaction;
	
	private Iterable4 _iterable;
	
	public LazyQueryResult(Transaction trans) {
		_transaction = trans;
	}

	public Object get(int index) {
		throw new NotImplementedException();
	}

	public int indexOf(int id) {
		throw new NotImplementedException();
	}

	public IntIterator4 iterateIDs() {
		if(_iterable == null){
			throw new IllegalStateException();
		}
		return new IntIterator4Adaptor(_iterable.iterator());
	}
	
	public void loadFromClassIndex(final YapClass clazz) {
		_iterable = new Iterable4() {
			public Iterator4 iterator() {
				return classIndexIterator(clazz);
			}
		};
	}
	
	public Iterator4 classIndexIterator(YapClass clazz) {
		return BTreeClassIndexStrategy.iterate(clazz, transaction());
	}
	
	public Transaction transaction(){
		return _transaction;
	}

	public void loadFromClassIndexes(final YapClassCollectionIterator classCollectionIterator) {
		_iterable = new Iterable4() {
			public Iterator4 iterator() {
				return new CompositeIterator4(
					new MappingIterator(classCollectionIterator) {
						protected Object map(Object current) {
							final YapClass yapClass = (YapClass)current;
							if(skipClass(yapClass)){
								return MappingIterator.SKIP;
							}
							return classIndexIterator(yapClass);
						}
					}
				);
			}
		};
	}
	
	public boolean skipClass(YapClass yapClass){
		if (yapClass.getName() == null) {
			return true;
		}
		ReflectClass claxx = yapClass.classReflector();
		if (stream().i_handlers.ICLASS_INTERNAL.isAssignableFrom(claxx)){
			return true; 
		}
		return false;
	}

	public void loadFromIdReader(YapReader reader) {
		throw new NotImplementedException();
	}

	public void loadFromQuery(final QQuery query) {
		_iterable = new Iterable4(){
			public Iterator4 iterator() {
				return query.executeLazy();
			}
		};
	}

	public YapStream stream() {
		return _transaction.stream();
	}

    public ExtObjectContainer objectContainer() {
        return stream();
    }

	public int size() {
		throw new NotImplementedException();
	}

	public void sort(QueryComparator cmp) {
		throw new NotImplementedException();
	}

	public Iterator4 iterator() {
		throw new NotImplementedException();
	}

}
