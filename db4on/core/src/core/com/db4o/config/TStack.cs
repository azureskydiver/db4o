/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;
using com.db4o;

namespace com.db4o.config {

	/// <exclude />
    public class TStack : ObjectTranslator {

        public void onActivate(ObjectContainer objectContainer, object obj, object members){
            Stack stack = (Stack)obj;
            if(members != null){
                object[] elements = (object[]) members;
                for(int i = elements.Length - 1; i >= 0 ; i--){
                    stack.Push(elements[i]);
                }
            }
        }

        public Object onStore(ObjectContainer objectContainer, object obj){
            Stack stack = (Stack)obj;
            int count = stack.Count;
            object[] elements = new object[count];
            IEnumerator e = stack.GetEnumerator();
            e.Reset();
            for(int i = 0; i < count; i++){
                e.MoveNext();
                elements[i] = e.Current;
            }
            return elements;
        }

        public Class storedClass(){
            return Class.getClassForType(typeof(object[]));
        }
    }
}
