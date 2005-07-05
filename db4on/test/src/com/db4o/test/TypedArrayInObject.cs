/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.foundation;

namespace com.db4o.test
{

    public class TypedArrayInObject
	{
        Object obj;
	
        public void store(){
            Test.deleteAllInstances(this);
            TypedArrayInObject taio = new TypedArrayInObject();
            Atom[] mols = new Atom[1];
            mols[0] = new Atom("TypedArrayInObject"); 
            taio.obj = mols;
            Test.store(taio);
        }

        class EnsureAtom : Visitor4{
            public void visit(Object obj) {
                TypedArrayInObject taio = (TypedArrayInObject)obj;
                Test.ensure(taio.obj is Atom[]);
            }
        }
	
        public void test(){
            Test.forEach(new TypedArrayInObject(), new EnsureAtom());
        }


}
}
