/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.reflect.*;

/** 
 * non-constraint, only necessary to attach children.
 * Added upon call to Query#descendant, if there is no
 * other place to hook in.
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
	
	int candidateCountByIndex(int depth) {
	    return -1;
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
		return i_subConstraints == null;
	}

	QConClass shareParentForClass(IClass a_class, boolean[] removeExisting) {
		if (i_parent != null) {
			if (i_field.canHold(a_class)) {
				QConClass newConstraint = new QConClass(i_trans, i_parent, i_field, a_class);
				morph(removeExisting,newConstraint, a_class);
				return newConstraint;
			}
		}
		return null;
	}


	QCon shareParent(Object a_object, boolean[] removeExisting) {
		if (i_parent != null) {
			if (i_field.canHold(a_object)) {
				QConObject newConstraint =
					new QConObject(i_trans, i_parent, i_field, a_object);
				IClass claxx = i_trans.reflector().forObject(a_object);
                morph(removeExisting, newConstraint, claxx);
				return newConstraint;
			}
		}
		return null;
	}

	// Our QConPath objects are just placeholders to fields,
	// so the parents are reachable.
	// If we find a "real" constraint, we throw the QPath
	// out and replace it with the other constraint. 
    private void morph(boolean[] removeExisting, QConObject newConstraint, IClass claxx) {
        boolean mayMorph = true;
        if (claxx != null) {
        	YapClass yc = i_trans.i_stream.getYapClass(claxx, true);
        	if (yc != null && i_subConstraints != null) {
        		Iterator4 i = new Iterator4(i_subConstraints);
        		while (i.hasNext()) {
        			QField qf = ((QCon) i.next()).getField();
        			if (!yc.hasField(i_trans.i_stream, qf.i_name)) {
        				mayMorph = false;
        				break;
        			}
        		}
        	}
        }
        
        // }
        
        if (mayMorph) {
        	if(i_subConstraints != null){
        		Iterator4 i = new Iterator4(i_subConstraints);
        		while (i.hasNext()) {
        			newConstraint.addConstraint((QCon) i.next());
        		}
        	}
        	if(i_joins != null){
        		Iterator4 i = i_joins.iterator();
        		while (i.hasNext()) {
        			QConJoin qcj = (QConJoin)i.next();
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
		if(Deploy.debugQueries){
			return "QConPath " + super.toString();
		}
		return super.toString();
	}


}
