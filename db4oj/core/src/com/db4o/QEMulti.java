/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

public class QEMulti extends QE{
	
	Collection4 i_evaluators = new Collection4();
	
	QE add(QE evaluator){
		i_evaluators.ensure(evaluator);
		return this;
	}
	
	boolean identity(){
		boolean ret = false;
		Iterator4 i = i_evaluators.iterator();
		while(i.hasNext()){
			if(((QE)i.next()).identity()){
				ret = true;
			}else{
				return false;
			}
		}
		return ret;
	}
	
	boolean evaluate(QConObject a_constraint, QCandidate a_candidate, Object a_value){
		Iterator4 i = i_evaluators.iterator();
		while(i.hasNext()){
			if(((QE)i.next()).evaluate(a_constraint, a_candidate, a_value)){
				return true;
			}
		}
		return false;
	}
	
	void indexBitMap(boolean[] bits){
	    Iterator4 i = i_evaluators.iterator();
	    while(i.hasNext()){
	        ((QE)i.next()).indexBitMap(bits);
	    }
	}
	
	boolean supportsIndex(){
	    Iterator4 i = i_evaluators.iterator();
	    while(i.hasNext()){
	        if(! ((QE)i.next()).supportsIndex()){
	            return false;
	        }
	    }
	    return true;
	}
	
	
	
}

