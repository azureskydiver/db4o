/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;
using com.db4o;

namespace com.db4o.config {

	/// <exclude />
    public class TDictionary : ObjectTranslator {

        public void onActivate(ObjectContainer objectContainer, object obj, object members){
            IDictionary dict = (IDictionary)obj;
            dict.Clear();
            if(members != null){
                Entry[] entries = (Entry[]) members;
                for(int i = 0; i < entries.Length; i++){
                    if(entries[i].key != null && entries[i].value != null){
                        dict[entries[i].key] =  entries[i].value;
                    }
           }
            }
        }

        public Object onStore(ObjectContainer objectContainer, object obj){
            IDictionary dict = (IDictionary)obj;
            Entry[] entries = new Entry[dict.Count];
            IDictionaryEnumerator e = dict.GetEnumerator();
            e.Reset();
            for(int i = 0; i < dict.Count; i++){
                e.MoveNext();
                entries[i] = new Entry();
                entries[i].key = e.Key;
                entries[i].value = e.Value;
            }
            return entries;
        }

        public Class storedClass(){
            return Class.getClassForType(typeof(Entry[]));
        }
    }

}
