/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;
using com.db4o;

namespace com.db4o.config {

	/// <exclude />
    public class TList : ObjectTranslator {

        public void OnActivate(ObjectContainer objectContainer, object obj, object members){
            IList list = (IList)obj;
            list.Clear();
            if(members != null){
                object[] elements = (object[]) members;
                for(int i = 0; i < elements.Length; i++){
                    list.Add(elements[i]);
                }
            }
        }

        public Object OnStore(ObjectContainer objectContainer, object obj){
            IList list = (IList)obj;
            object[] elements = new object[list.Count];
            for(int i = 0; i < list.Count; i++){
                elements[i] = list[i];
            }
            return elements;
        }

        public Class StoredClass(){
            return Class.GetClassForType(typeof(object[]));
        }
    }
}
