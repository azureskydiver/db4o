/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.ext.*;


/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class TransientClone {
    
    List list;
    Hashtable ht;
    String str;
    int myInt;
    Molecule[] molecules;

    public void storeOne(){
        list = new ArrayList();
        list.add(new Atom("listAtom"));
        list.add(this);
        ht = new Hashtable();
        ht.put("htc", new Molecule("htAtom"));
        ht.put("recurse", this);
        str = "str";
        myInt = 100;
        molecules = new Molecule[3];
        for (int i = 0; i < molecules.length; i++) {
            molecules[i] = new Molecule("arr" + i);
            molecules[i].child = new Atom("arr" + i);
            molecules[i].child.child = new Atom("arrc" + i);
        }
    }
    
    public void testOne(){
        ExtObjectContainer oc = Test.objectContainer();
        oc.activate(this, Integer.MAX_VALUE);
        TransientClone originalValues = peekPersisted(false);
        cmp(this, originalValues);
        oc.deactivate(this, Integer.MAX_VALUE);
        TransientClone modified = peekPersisted(false);
        cmp(originalValues, modified);
        oc.activate(this, Integer.MAX_VALUE);
        
        modified.str = "changed";
        modified.molecules[0].name = "changed";
        str = "changed";
        molecules[0].name = "changed";
        oc.store(molecules[0]);
        oc.store(this);

        TransientClone tc = peekPersisted(true);
        cmp(originalValues, tc);
        
        tc = peekPersisted(false);
        cmp(modified, tc);
        
        oc.commit();
        tc = peekPersisted(true);
        cmp(modified, tc);
    }
    
    private TransientClone cmp(TransientClone to, TransientClone tc){
        Test.ensure(tc != to);
        Test.ensure(tc.list != to);
        Test.ensure(tc.list.size() == to.list.size());
        Iterator i = tc.list.iterator();
        Iterator j = to.list.iterator();
        Atom tca = (Atom)i.next();
        Atom tct = (Atom)j.next();
        Test.ensure(tca != tct);
        Test.ensure(tca.name.equals(tct.name));
        Test.ensure(i.next() == tc);
        Test.ensure(j.next() == to);
        Test.ensure(tc.ht != to.ht);
        Molecule tcm = (Molecule)tc.ht.get("htc");
        Molecule tom = (Molecule)to.ht.get("htc");
        Test.ensure(tcm != tom);
        Test.ensure(tcm.name.equals(tom.name));
        Test.ensure(tc.ht.get("recurse") == tc);
        Test.ensure(to.ht.get("recurse") == to);
        Test.ensure(tc.str.equals(to.str));
        Test.ensure(tc.myInt == to.myInt);
        Test.ensure(tc.molecules.length == to.molecules.length);
        Test.ensure(tc.molecules.length == to.molecules.length);
        tcm = tc.molecules[0];
        tom = to.molecules[0];
        Test.ensure(tcm != tom);
        Test.ensure(tcm.name.equals(tom.name));
        Test.ensure(tcm.child != tom.child);
        Test.ensure(tcm.child.name.equals(tom.child.name));
        return tc;
    }

	private TransientClone peekPersisted(boolean committed) {
		ExtObjectContainer oc = Test.objectContainer();
        TransientClone tc = (TransientClone)oc.peekPersisted(this, Integer.MAX_VALUE, committed);
		return tc;
	}
}
