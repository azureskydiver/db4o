/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

class QOrder extends Tree{
	
	final QConObject i_constraint;
	final QCandidate i_candidate;
	
	QOrder(QConObject a_constraint, QCandidate a_candidate){
		i_constraint = a_constraint;
		i_candidate = a_candidate;
	}

	int compare(Tree a_to) {
		if(i_constraint.i_comparator.isSmaller(i_candidate.value())){
			return i_constraint.i_orderID;	
		}
		if(i_constraint.i_comparator.isEqual(i_candidate.value())){
			return 0;	
		}
		return - i_constraint.i_orderID;	
	}

	
}

