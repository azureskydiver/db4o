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
using j4o.util;

namespace com.db4o {

    internal class P2HashMapKeySet : ICollection {

        protected P2HashMap i_map;
      
        internal P2HashMapKeySet(P2HashMap p2hashmap) : base() {
            i_map = p2hashmap;
        }

        public int Count{
            get{
                lock (i_map.streamLock()) {
                    i_map.checkActive();
                    return i_map.i_size;
                }
            }
        }

        public void CopyTo(Array arr, int pos){
            lock (i_map.streamLock()) {
                i_map.checkActive();
                P2HashMapKeyIterator i = new P2HashMapKeyIterator(i_map);
                while (i.hasNext()) {
                    arr.SetValue(i.next(), pos++);
                }
            }
        }

        public IEnumerator GetEnumerator(){
            i_map.checkActive();
            return new P2HashMapKeyIterator(i_map);
        }

        public bool IsSynchronized{
            get{
                return true;
            }
        }

        public Object SyncRoot{
            get{
                i_map.checkActive();
                return i_map.streamLock();
            }
        }
    }
}