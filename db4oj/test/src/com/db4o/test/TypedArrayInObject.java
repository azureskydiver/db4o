/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;

public class TypedArrayInObject {
	
	Object obj;
	Object[] obj2;
	
	public void store(){
		Test.deleteAllInstances(this);
		TypedArrayInObject taio = new TypedArrayInObject();
		Atom[] mols = new Atom[1];
		mols[0] = new Atom("TypedArrayInObject"); 
		taio.obj = mols;
		taio.obj2 = mols;
		Test.store(taio);
	}
	
	public void test(){
		Test.forEach(new TypedArrayInObject(), new Visitor4() {
            public void visit(Object a_obj) {
            	TypedArrayInObject taio = (TypedArrayInObject)a_obj;
            	Test.ensure(taio.obj instanceof Atom[]);
            	Test.ensure(taio.obj2 instanceof Atom[]);
            	
            }
        });
	}
}
