/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.query.model;

import com.db4o.query.Constraint;

public interface RelationOperator {
    public static abstract class RelationOperatorImpl implements RelationOperator {
        private String name;

        private RelationOperatorImpl(String name) {
            this.name = name;
        }
        
        public String name() {
            return name;
        }
    }
    
    public final static RelationOperator EQUALS=new RelationOperatorImpl("=") {
        public void apply(Constraint constraint) {
        }        
    };

    public final static RelationOperator IDENTITY=new RelationOperatorImpl("ID") {
        public void apply(Constraint constraint) {
            constraint.identity();
        }        
    };

    public final static RelationOperator SMALLER=new RelationOperatorImpl("<") {
        public void apply(Constraint constraint) {
            constraint.smaller();
        }        
    };

    public final static RelationOperator GREATER=new RelationOperatorImpl(">") {
        public void apply(Constraint constraint) {
            constraint.greater();
        }        
    };

    public final static RelationOperator LIKE=new RelationOperatorImpl("~") {
        public void apply(Constraint constraint) {
            constraint.like();
        }        
    };
    
    public static RelationOperator[] OPERATORS= {
        EQUALS,IDENTITY,GREATER,SMALLER,LIKE
    };
    String name();
    void apply(Constraint query);
}
