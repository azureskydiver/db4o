/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * @exclude
 */
class QPending extends Tree{
	
	final QConJoin			i_join;
	QCon 					i_constraint;
	
	int 					i_result;

	// Constants, so QConJoin.evaluatePending is made easy:
	static final int FALSE = -4;
	static final int BOTH = 1;
	static final int TRUE = 2;
	
	QPending(QConJoin a_join, QCon a_constraint, boolean a_firstResult){
		i_join = a_join;
		i_constraint = a_constraint;
		
		i_result = a_firstResult ? TRUE : FALSE;
	}
	
	public int compare(Tree a_to) {
		return i_constraint.i_id - ((QPending)a_to).i_constraint.i_id;
	}

	void changeConstraint(){
		i_constraint = i_join.getOtherConstraint(i_constraint);
	}
}

