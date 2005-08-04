/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import com.db4o.query.*;

public class PredicateEvaluation implements Evaluation {
    
    public Predicate _predicate;
    
    public PredicateEvaluation(){
        // CS
    }
    
    public PredicateEvaluation(Predicate predicate){
        _predicate = predicate;
    }

    public void evaluate(Candidate candidate) {
        candidate.include(_predicate.invoke(candidate.getObject()));
    }

}
