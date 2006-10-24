/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.legacy.soda.experiments;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;
import com.db4o.test.soda.*;
import com.db4o.test.soda.experiments.*;

public class STIdentityEvaluation implements STClass1{
    
    public static transient SodaTest st;
    
    public Object[] store() {
        
        Helper helperA = new Helper("aaa");
        
        return new Object[] {
            new STIdentityEvaluation(null),
            new STIdentityEvaluation(helperA),
            new STIdentityEvaluation(helperA),
            new STIdentityEvaluation(helperA),
            new STIdentityEvaluation(new HelperDerivate("bbb")),
            new STIdentityEvaluation(new Helper("dod"))
            };
    }
    
    public Helper helper;
    
    public STIdentityEvaluation(){
    }
    
    public STIdentityEvaluation(Helper h){
        this.helper = h;
    }
    
    public void test(){
        Query q = st.query();
        Object[] r = store();
        q.constrain(new Helper("aaa"));
        ObjectSet os = q.execute();
        Helper helperA = (Helper)os.next();
        q = st.query();
        q.constrain(STIdentityEvaluation.class);
        q.descend("helper").constrain(helperA).identity();
        q.constrain(new Evaluation() {
            public void evaluate(Candidate candidate) {
                candidate.include(true);
            }
        });
        st.expect(q,new Object[]{r[1], r[2], r[3]});
    }
    
    public void testMemberClassConstraint(){
        Query q = st.query();
        Object[] r = store();
        q.constrain(STIdentityEvaluation.class);
        q.descend("helper").constrain(HelperDerivate.class);
        st.expect(q,new Object[]{r[4]});
    }
    
    public static class Helper{
        
        public String hString;
        
        public Helper(){
        }
        
        public Helper(String str){
            hString = str;
        }
    }
    
    public static class HelperDerivate extends Helper{
        public HelperDerivate(){
        }
        
        public HelperDerivate(String str){
            super(str);
        }
        
    }
    
}
