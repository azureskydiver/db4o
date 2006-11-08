/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.classindex.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class LazyQueryResult extends AbstractQueryResult {
	
	private Iterable4 _iterable;
	
	public LazyQueryResult(Transaction trans) {
		super(trans);
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

	public void loadFromQuery(final QQuery query) {
		_iterable = new Iterable4(){
			public Iterator4 iterator() {
				return query.executeLazy();
			}
		};
	}

    public AbstractQueryResult supportSize(){
    	return toIdTree();
    }
    
    public AbstractQueryResult supportSort(){
    	return toIdList();
    }
    
    public AbstractQueryResult supportElementAccess(){
    	return toIdList();
    }
    
    protected int knownSize(){
    	return 0;
    }
    
    public AbstractQueryResult toIdList(){
    	return toIdTree().toIdList();
    }

	public AbstractQueryResult createIndexSnapshot() {
		return toIdTree();
	}

}
