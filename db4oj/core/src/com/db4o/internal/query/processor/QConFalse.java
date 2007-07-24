/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.query.processor;

import com.db4o.internal.*;


/** 
 * @exclude
 */
public class QConFalse extends QCon {

    public QConFalse() {
        // C/S only
    }

    public QConFalse(Transaction trans) {
        super(trans);
    }
    
    void evaluateSimpleExec(QCandidates a_candidates) {
    	a_candidates.filter(this);
    }

	boolean evaluate(QCandidate a_candidate) {
		return false;
	}

}