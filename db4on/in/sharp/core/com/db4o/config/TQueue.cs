/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;
using com.db4o;

namespace com.db4o.config {

	/// <exclude />
    public class TQueue : ObjectTranslator {

        public void onActivate(ObjectContainer objectContainer, object obj, object members){
            Queue queue = (Queue)obj;
            queue.Clear();
            if(members != null){
                object[] elements = (object[]) members;
                for(int i = 0 ; i < elements.Length ; i++){
                    queue.Enqueue(elements[i]);
                }
            }
        }

        public Object onStore(ObjectContainer objectContainer, object obj){
            Queue queue = (Queue)obj;
            int count = queue.Count;
            object[] elements = new object[count];
            IEnumerator e = queue.GetEnumerator();
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
