/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.foundation;

namespace com.db4o.test
{

    public class TypedArrayInObject
	{
        Object obj;
	
        public void Store(){
            Tester.DeleteAllInstances(this);
            TypedArrayInObject taio = new TypedArrayInObject();
            Atom[] mols = new Atom[1];
            mols[0] = new Atom("TypedArrayInObject"); 
            taio.obj = mols;
            Tester.Store(taio);
        }

        class EnsureAtom : Visitor4{
            public void Visit(Object obj) {
                TypedArrayInObject taio = (TypedArrayInObject)obj;
                Tester.Ensure(taio.obj is Atom[]);
            }
        }
	
        public void Test(){
            Tester.ForEach(new TypedArrayInObject(), new EnsureAtom());
        }


}
}
