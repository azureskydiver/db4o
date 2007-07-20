/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.query.processor;

import com.db4o.foundation.*;
import com.db4o.internal.*;


/** 
 * @exclude
 */
// FIXME This certainly shouldn't extend QConPath.
public class QConFalse extends QConPath {
	public QConFalse(Transaction trans){
		super(trans, null, new QField(trans, "", null, 0, 0));
	}
	
	void createCandidates(Collection4 a_candidateCollection) {
	}
	
	boolean evaluate(QCandidate a_candidate) {
		return false;
	}
}
