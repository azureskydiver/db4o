/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.query.*;

public class QConClass extends QConObject{
	
	private Class i_class;
	private boolean i_equal;
	
	public QConClass(){
		// C/S
	}
	
	QConClass(Transaction a_trans, QCon a_parent, QField a_field, Class a_class){
		super(a_trans, a_parent, a_field, null);
		if(a_class != null){
			i_yapClass = a_trans.i_stream.getYapClass(a_class, true);
			if(a_class == YapConst.CLASS_OBJECT){
				i_yapClass = (YapClass)((YapClassPrimitive)i_yapClass).i_handler;
			}
		}
		i_class = a_class;
	}
	
	boolean evaluate(QCandidate a_candidate){
		boolean res = true;
		Class clazz = a_candidate.getJavaClass();
		if(clazz == null){
			res = false;
		}else{
			res = i_equal ? i_class == clazz : i_class.isAssignableFrom(clazz);
		}
		return i_evaluator.not(res);
	}
	
	void evaluateSelf() {
		
		// optimization for simple class queries: 
		// No instantiation of objects, if not necessary.
		// Does not handle the special comparison of the
		// Compare interface.
		// 
		if(i_evaluator == QE.DEFAULT){
			if(i_orderID == 0 && i_joins == null){
				if(i_yapClass != null  && i_candidates.i_yapClass != null){
					if(i_yapClass.getHigherHierarchy(i_candidates.i_yapClass) == i_yapClass){
						return;
					}
				}
			}
		}
		i_candidates.filter(this);
	}
	
	public Constraint equal (){
		synchronized(streamLock()){
			i_equal = true;
			return this;
		}
	}
	
	boolean isNullConstraint() {
		return false;
	}
	
	public String toString(){
		if(Deploy.debugQueries){
			String str = "QConClass ";
			if(i_class != null){
				str += i_class.toString() + " ";
			}
			return str + super.toString();
		}
		return super.toString();
	}

	

}

