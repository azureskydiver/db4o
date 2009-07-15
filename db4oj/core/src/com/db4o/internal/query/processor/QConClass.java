/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal.query.processor;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.*;
import com.db4o.reflect.*;


/**
 *
 * Class constraint on queries
 * 
 * @exclude
 */
public class QConClass extends QConObject{
	
	private transient ReflectClass _claxx;
	
	@decaf.Public
    private String _className;
	
	@decaf.Public
    private boolean i_equal;
	
	public QConClass(){
		// C/S
	}
	
	QConClass(Transaction a_trans, QCon a_parent, QField a_field, ReflectClass claxx){
		super(a_trans, a_parent, a_field, null);
		if(claxx != null){
			i_classMetadata = a_trans.container().produceClassMetadata(claxx);
			if(claxx.equals(a_trans.container()._handlers.ICLASS_OBJECT)){
				i_classMetadata = (ClassMetadata)i_classMetadata.typeHandler();
			}
		}
		_claxx = claxx;
	}
	
	QConClass(Transaction trans, ReflectClass claxx){
	    this(trans ,null, null, claxx);
	}
	
	public String getClassName() {
		return _claxx.getName();
	}
    
    public boolean canBeIndexLeaf(){
        return false;
    }
	
	boolean evaluate(QCandidate a_candidate){
		boolean res = true;
		ReflectClass claxx = a_candidate.classReflector();
		if(claxx == null){
			res = false;
		}else{
			res = i_equal ? _claxx.equals(claxx) : _claxx.isAssignableFrom(claxx);
		}
		return i_evaluator.not(res);
	}
	
	void evaluateSelf() {
		
		// optimization for simple class queries: 
		// No instantiation of objects, if not necessary.
		// Does not handle the special comparison of the
		// Compare interface.
		//
		if(i_candidates.wasLoadedFromClassIndex()){
			if(i_evaluator.isDefault()){
				if(! hasOrdering() && ! hasJoins()){
					if(i_classMetadata != null  && i_candidates.i_classMetadata != null){
						if(i_classMetadata.getHigherHierarchy(i_candidates.i_classMetadata) == i_classMetadata){
							return;
						}
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
    
    String logObject() {
        if (Debug4.queries) {
            if(_claxx != null){
                return _claxx.toString();
            }
        } 
        return "";
    }
    
    void marshall() {
        super.marshall();
        if(_claxx!=null) {
        	_className = container().config().resolveAliasRuntimeName(_claxx.getName());
        }
    }
	
	public String toString(){
		String str = "QConClass ";
		if(_claxx != null){
			str += _claxx.toString() + " ";
		}
		return str + super.toString();
	}
	
    void unmarshall(Transaction a_trans) {
        if (i_trans == null) {
            super.unmarshall(a_trans);
            if(_className!=null) {
            	_className = container().config().resolveAliasStoredName(_className);
            	_claxx = a_trans.reflector().forName(_className);
            }
        }
    }
    
    void setEvaluationMode() {
        Iterator4 children = iterateChildren();
        while (children.moveNext()) {
            Object child = children.current();
            if (child instanceof QConObject) {
                ((QConObject) child).setEvaluationMode();
            }
        }
    }
    
    @Override
    public void setProcessedByIndex() {
    	// do nothing, QConClass needs to stay in the evaluation graph.
    }
    
}

