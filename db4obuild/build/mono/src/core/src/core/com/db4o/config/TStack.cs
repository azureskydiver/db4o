/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
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
