/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.query.processor;

import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.types.Unversioned;


/**
 * Query Evaluator - Represents such things as &gt;, &gt;=, &lt;, &lt;=, EQUAL, LIKE, etc.
 * 
 * @exclude
 */
public class QE implements Unversioned {	
	
	static final QE DEFAULT = new QE();
	
	public static final int NULLS = 0;
	public static final int SMALLER = 1;
	public static final int EQUAL = 2;
	public static final int GREATER = 3;
	
	QE add(QE evaluator){
		return evaluator;
	}
    
	public boolean identity(){
		return false;
	}

    boolean isDefault(){
        return true;
    }

	boolean evaluate(QConObject constraint, QCandidate candidate, Object obj){
        Comparable4 comparator = constraint.getComparator(candidate);
		if(obj == null){
			return comparator instanceof Null;
		}
        if (comparator instanceof ArrayHandler) {
            return ((ArrayHandler) comparator).isEqual(obj);
        }
        return comparator.compareTo(obj) == 0;
	}
	
	public boolean equals(Object obj){
		return obj!=null&&obj.getClass() == this.getClass();
	}
	
	public int hashCode() {
		return getClass().hashCode();
	}
	
	// overridden in QENot 
	boolean not(boolean res){
		return res;
	}
	
	/**
	 * Specifies which part of the index to take.
	 * Array elements:
	 * [0] - smaller
	 * [1] - equal
	 * [2] - greater
	 * [3] - nulls
	 * 
	 * 
	 * @param bits
	 */
	public void indexBitMap(boolean[] bits){
	    bits[QE.EQUAL] = true;
	}
	
	public boolean supportsIndex(){
	    return true;
	}
	
}
