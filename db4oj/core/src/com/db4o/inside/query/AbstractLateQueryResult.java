/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.classindex.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public abstract class AbstractLateQueryResult extends AbstractQueryResult {
	
	protected Iterable4 _iterable;

	public AbstractLateQueryResult(Transaction transaction) {
		super(transaction);
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
    
	public IntIterator4 iterateIDs() {
		if(_iterable == null){
			throw new IllegalStateException();
		}
		return new IntIterator4Adaptor(_iterable);
	}
    
    public AbstractQueryResult toIdList(){
    	return toIdTree().toIdList();
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
	
	protected Iterable4 classIndexesIterable(final YapClassCollectionIterator classCollectionIterator) {
		return new Iterable4() {
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
	
	protected Iterable4 classIndexIterable(final YapClass clazz) {
		return new Iterable4() {
			public Iterator4 iterator() {
				return classIndexIterator(clazz);
			}
		};
	}
	
	public Iterator4 classIndexIterator(YapClass clazz) {
		return BTreeClassIndexStrategy.iterate(clazz, transaction());
	}

}
