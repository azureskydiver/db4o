/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.inside.*;

/**
 * @exclude
 */
public abstract class QEStringCmp extends QEAbstract {
    
	public boolean caseSensitive;

	public QEStringCmp(boolean caseSensitive_) {
		caseSensitive = caseSensitive_;
	}

	boolean evaluate(QConObject a_constraint, QCandidate a_candidate, Object a_value){
		if(a_value != null){
		    if(a_value instanceof Buffer) {
                a_value = a_candidate._marshallerFamily._string.readFromOwnSlot(a_constraint.i_trans.stream(), ((Buffer)a_value));
		    }
		    String candidate=a_value.toString();
		    String constraint=a_constraint.i_object.toString();
		    if(!caseSensitive) {
		    	candidate=candidate.toLowerCase();
		    	constraint=constraint.toLowerCase();
		    }
			return compareStrings(candidate,constraint);
		}
		return a_constraint.i_object==null;
	}
	
	public boolean supportsIndex(){
	    return false;
	}
	
	protected abstract boolean compareStrings(String candidate,String constraint);
}
