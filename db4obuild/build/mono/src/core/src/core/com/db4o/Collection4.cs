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
using j4o.lang;
namespace com.db4o {

   public class Collection4 : DeepClone {
      
      public Collection4() : base() {
      }
      internal List4 i_first;
      private int i_size;
      private static EmptyIterator emptyIterator = new EmptyIterator();
      
      public void add(Object obj) {
         i_first = new List4(i_first, obj);
         i_size++;
      }
      
      internal void addAll(Object[] objs) {
         if (objs != null) {
            for (int i1 = 0; i1 < objs.Length; i1++) {
               if (objs[i1] != null) add(objs[i1]);
            }
         }
      }
      
      internal void clear() {
         i_first = null;
         i_size = 0;
      }
      
      public bool contains(Object obj) {
         return get(obj) != null;
      }
      
      public bool containsByIdentity(Object obj) {
         for (List4 list41 = i_first; list41 != null; list41 = list41.i_next) {
            if (list41.i_object != null && list41.i_object == obj) return true;
         }
         return false;
      }
      
      internal Object get(Object obj) {
         Iterator4 iterator41 = iterator();
         while (iterator41.hasNext()) {
            Object obj_0_1 = iterator41.next();
            if (obj_0_1.Equals(obj)) return obj_0_1;
         }
         return null;
      }
      
      public Object deepClone(Object obj) {
         Collection4 collection4_1_1 = new Collection4();
         Object obj_2_1 = null;
         Iterator4 iterator41 = iterator();
         while (iterator41.hasNext()) {
            obj_2_1 = iterator41.next();
            if (obj_2_1 is DeepClone) collection4_1_1.add(((DeepClone)obj_2_1).deepClone(obj)); else collection4_1_1.add(obj_2_1);
         }
         return collection4_1_1;
      }
      
      internal Object ensure(Object obj) {
         Object obj_3_1 = get(obj);
         if (obj_3_1 != null) return obj_3_1;
         add(obj);
         return obj;
      }
      
      public Iterator4 iterator() {
         if (i_first == null) return emptyIterator;
         return new Iterator4(i_first);
      }
      
      internal virtual Object remove(Object obj) {
         List4 list41 = null;
         for (List4 list4_4_1 = i_first; list4_4_1 != null; list4_4_1 = list4_4_1.i_next) {
            if (list4_4_1.i_object.Equals(obj)) {
               i_size--;
               if (list41 == null) i_first = list4_4_1.i_next; else list41.i_next = list4_4_1.i_next;
               return list4_4_1.i_object;
            }
            list41 = list4_4_1;
         }
         return null;
      }
      
      public int size() {
         return i_size;
      }
      
      internal void toArray(Object[] objs) {
         int i1 = i_size;
         Iterator4 iterator41 = iterator();
         while (iterator41.hasNext()) objs[--i1] = iterator41.next();
      }
   }
}