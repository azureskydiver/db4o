/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

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
