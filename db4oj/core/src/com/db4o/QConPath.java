/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.reflect.*;

/** 
 * Placeholder for a constraint, only necessary to attach children
 * to the query graph.
 * 
 * Added upon a call to Query#descend(), if there is no
 * other place to hook up a new constraint.
 * 
 * @exclude
 */
public class QConPath extends QConClass {
	
	public QConPath(){
		
	}

	QConPath(Transaction a_trans, QCon a_parent, QField a_field) {
		super(a_trans, a_parent, a_field, null);
		if(a_field != null){
			i_yapClass = a_field.getYapClass();
		}
	}
	
	public boolean canLoadByIndex() {
		return false;
	}
	
	boolean evaluate(QCandidate a_candidate) {
		if (a_candidate.classReflector() == null) {
			visitOnNull(a_candidate.getRoot());
		}
		return true;
	}
	
	void evaluateSelf() {
		// do nothing
	}

	boolean isNullConstraint() {
		return ! hasChildren();
	}

	QConClass shareParentForClass(ReflectClass a_class, boolean[] removeExisting) {
        if (i_parent == null) {
            return null;
        }
		if (! i_field.canHold(a_class)) {
            return null;
        }
		QConClass newConstraint = new QConClass(i_trans, i_parent, i_field, a_class);
		morph(removeExisting,newConstraint, a_class);
		return newConstraint;
	}


	QCon shareParent(Object a_object, boolean[] removeExisting) {
        if (i_parent == null) {
            return null;
        }
        Object obj = i_field.coerce(a_object);
        if(obj == No4.INSTANCE){
        	QConObject falseConstraint = new QConFalse(i_trans, i_parent, i_field);
            morph(removeExisting, falseConstraint, reflectClassForObject(obj));
    		return falseConstraint;
        }
        QConObject newConstraint = new QConObject(i_trans, i_parent, i_field, obj);
        morph(removeExisting, newConstraint, reflectClassForObject(obj));
		return newConstraint;
	}

	private ReflectClass reflectClassForObject(Object obj) {
		return i_trans.reflector().forObject(obj);
	}

	// Our QConPath objects are just placeholders to fields,
	// so the parents are reachable.
	// If we find a "real" constraint, we throw the QPath
	// out and replace it with the other constraint. 
    private void morph(boolean[] removeExisting, QConObject newConstraint, ReflectClass claxx) {
        boolean mayMorph = true;
        if (claxx != null) {
        	YapClass yc = i_trans.stream().getYapClass(claxx, true);
        	if (yc != null) {
        		Iterator4 i = iterateChildren();
        		while (i.moveNext()) {
        			QField qf = ((QCon) i.current()).getField();
        			if (!yc.hasField(i_trans.stream(), qf.i_name)) {
        				mayMorph = false;
        				break;
        			}
        		}
        	}
        }
        
        // }
        
        if (mayMorph) {
    		Iterator4 j = iterateChildren();
    		while (j.moveNext()) {
    			newConstraint.addConstraint((QCon) j.current());
    		}
        	if(hasJoins()){
        		Iterator4 k = iterateJoins();
        		while (k.moveNext()) {
        			QConJoin qcj = (QConJoin)k.current();
        			qcj.exchangeConstraint(this, newConstraint);
        			newConstraint.addJoin(qcj);
        		}
        	}
        	i_parent.exchangeConstraint(this, newConstraint);
        	removeExisting[0] = true;
        	
        } else {
        	i_parent.addConstraint(newConstraint);
        }
    }

	final boolean visitSelfOnNull() {
		return false;
	}
	
	public String toString(){
        if(! Debug4.prettyToStrings){
            return super.toString();
        }
        return "QConPath " + super.toString();
	}


}
