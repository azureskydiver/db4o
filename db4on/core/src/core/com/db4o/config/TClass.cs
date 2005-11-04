/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;

namespace com.db4o.config {

	/// <exclude />
    public class TClass : ObjectConstructor {
		
		static readonly Class _stringClass = Class.getClassForType(typeof(String));
      
        public void onActivate(ObjectContainer objectContainer, object obj, object members) {
        }
      
        public Object onInstantiate(ObjectContainer objectContainer, object obj) {
            try { 
                return Class.forName((String)obj);
            }  catch (Exception exception) { 
                return null;
            }
        }
      
        public Object onStore(ObjectContainer objectContainer, object obj) {
            return ((Class)obj).getName();
        }
      
        public Class storedClass() {
            return _stringClass;
        }
    }
}