/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.Serializable;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Candidate;
import com.db4o.query.Evaluation;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

/**
 * 
 */
public class ParameterizedEvaluation extends ClientServerTestCase {
    
    public String str;
    
    public void store(ExtObjectContainer oc){
        store(oc, "one");
        store(oc, "fun");
        store(oc, "ton");
        store(oc, "sun");
    }
    
    private void store(ExtObjectContainer oc, String str){
        ParameterizedEvaluation pe = new ParameterizedEvaluation();
        pe.str = str;
        oc.set(pe);
    }
    
    public void conc(ExtObjectContainer oc){
        Assert.areEqual(2, queryContains(oc, "un").size());
    }
    
    private ObjectSet queryContains(ExtObjectContainer oc, final String str){
        Query q = oc.query();
        q.constrain(ParameterizedEvaluation.class);
        q.constrain(new Evaluation() {
            public void evaluate(Candidate candidate) {
                ParameterizedEvaluation pe = (ParameterizedEvaluation)candidate.getObject();
                boolean inc = pe.str.indexOf(str) != -1;
                candidate.include(inc);
            }
        });
        return q.execute();
    }

}
