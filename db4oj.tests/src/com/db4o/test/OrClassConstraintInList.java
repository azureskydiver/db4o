/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.query.*;


/**
 * 
 */
/**
 * @decaf.ignore.jdk11
 */
public class OrClassConstraintInList {
    
    int cnt;
    List list;
    
    public void store(){
        OrClassConstraintInList occ = new OrClassConstraintInList();
        occ.list = Test.objectContainer().collections().newLinkedList();
        occ.cnt = 0;
        occ.list.add(new Atom());
        Test.store(occ);
        occ = new OrClassConstraintInList();
        occ.list = Test.objectContainer().collections().newLinkedList();
        occ.cnt = 1;
        occ.list.add(new Atom());
        Test.store(occ);
        occ = new OrClassConstraintInList();
        occ.cnt = 1;
        occ.list = Test.objectContainer().collections().newLinkedList();
        Test.store(occ);
        occ = new OrClassConstraintInList();
        occ.cnt = 2;
        occ.list = Test.objectContainer().collections().newLinkedList();
        occ.list.add(new OrClassConstraintInList());
        Test.store(occ);
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(OrClassConstraintInList.class);
        Constraint c1 = q.descend("list").constrain(Atom.class);
        Constraint c2 = q.descend("cnt").constrain(new Integer(1));
        c1.or(c2);
        Test.ensure(q.execute().size() == 3);
    }
}
