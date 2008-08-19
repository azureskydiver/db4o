/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.query.processor;

import com.db4o.internal.*;


/**
 * @exclude
 */
public abstract class QEStringCmp extends QEAbstract {
    
	public boolean caseSensitive;

	/** for C/S messaging only */
	public QEStringCmp() {
	}
	
	public QEStringCmp(boolean caseSensitive_) {
		caseSensitive = caseSensitive_;
	}

	boolean evaluate(QConObject constraint, QCandidate candidate, Object obj){
		if(obj != null){
		    if(obj instanceof ByteArrayBuffer) {
                obj = candidate.readString((ByteArrayBuffer)obj);
		    }
		    String candidateStringValue = obj.toString();
		    String stringConstraint = constraint.i_object.toString();
		    if(!caseSensitive) {
		    	candidateStringValue=candidateStringValue.toLowerCase();
		    	stringConstraint=stringConstraint.toLowerCase();
		    }
			return compareStrings(candidateStringValue,stringConstraint);
		}
		return constraint.i_object==null;
	}
	
	public boolean supportsIndex(){
	    return false;
	}
	
	protected abstract boolean compareStrings(String candidate,String constraint);
}
