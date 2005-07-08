/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * @exclude
 */
public class QEContains extends QEAbstract
{
	
	boolean evaluate(QConObject a_constraint, QCandidate a_candidate, Object a_value){
		if(a_value != null){
		    if(a_value instanceof YapReader) {
		        a_value = ((YapReader)a_value).toString(a_constraint.i_trans);
		    }
			return a_value.toString().indexOf(a_constraint.i_object.toString()) > -1;
		}
		return a_constraint.i_object.equals(null);
	}
	
	public boolean supportsIndex(){
	    return false;
	}
}
