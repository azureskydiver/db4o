/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.cluster.inside;

import com.db4o.cluster.*;
import com.db4o.inside.*;
import com.db4o.query.*;


public class ClusterConstraint implements Constraint{
    
    private final Cluster _cluster;
    final Constraint[] _constraints;
    
    public ClusterConstraint(Cluster cluster, Constraint[] constraints){
        _cluster = cluster; 
        _constraints = constraints;
    }
    
    private ClusterConstraint compatible (Constraint with){
        if(! (with instanceof ClusterConstraint)){
            throw new IllegalArgumentException();
        }
        ClusterConstraint other = (ClusterConstraint)with;
        if(other._constraints.length != _constraints.length){
            throw new IllegalArgumentException();
        }
        return other;
    }

    public Constraint and(Constraint with) {
        return join(with, true);
    }

    public Constraint or(Constraint with) {
        return join(with, false);
    }
    
    private Constraint join(Constraint with, boolean isAnd){
        ClusterConstraint other = compatible(with);
        Constraint[] newConstraints = new Constraint[_constraints.length];
        for (int i = 0; i < _constraints.length; i++) {
            newConstraints[i] = isAnd ? _constraints[i].and(other._constraints[i]) : _constraints[i].or(other._constraints[i]);
        }
        return new ClusterConstraint(_cluster, newConstraints);
    }
    

    public Constraint equal() {
        for (int i = 0; i < _constraints.length; i++) {
            _constraints[i].equal();
        }
        return this;
    }

    public Constraint greater() {
        for (int i = 0; i < _constraints.length; i++) {
            _constraints[i].greater();
        }
        return this;
    }

    public Constraint smaller() {
        for (int i = 0; i < _constraints.length; i++) {
            _constraints[i].smaller();
        }
        return this;
    }

    public Constraint identity() {
        for (int i = 0; i < _constraints.length; i++) {
            _constraints[i].identity();
        }
        return this;
    }

    public Constraint like() {
        for (int i = 0; i < _constraints.length; i++) {
            _constraints[i].like();
        }
        return this;
    }

    public Constraint contains() {
        for (int i = 0; i < _constraints.length; i++) {
            _constraints[i].contains();
        }
        return this;
    }

    public Constraint not() {
        for (int i = 0; i < _constraints.length; i++) {
            _constraints[i].not();
        }
        return this;
    }

    public Object getObject() {
        Exceptions4.notSupported();
        return null;
    }

}
