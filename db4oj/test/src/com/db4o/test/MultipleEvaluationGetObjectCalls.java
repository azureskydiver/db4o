package com.db4o.test;

import com.db4o.*;
import com.db4o.query.*;


public class MultipleEvaluationGetObjectCalls {
	public String name;
	
	public void storeOne(){
	    name="hello";
	}
	
	public void test(){
		Query q = Test.query();
		q.constrain(getClass());
		q.descend("name").constrain(new Evaluation() {
            public void evaluate(Candidate candidate) {
            	boolean include = ((String)candidate.getObject()).startsWith("h")
            			&& ((String)candidate.getObject()).endsWith("o");
            	candidate.include(include);
            }
        });
		ObjectSet objectSet = q.execute();
		Test.ensure(objectSet.size() == 1);
	}
}
