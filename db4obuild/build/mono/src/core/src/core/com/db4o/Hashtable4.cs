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

   internal class Hashtable4 : Cloneable, DeepClone {
      private static float FILL = 0.5F;
      private int i_tableSize;
      private int i_mask;
      private int i_maximumSize;
      private int i_size;
      private HashtableIntEntry[] i_table;
      
      internal Hashtable4(int i) : base() {
         i = newSize(i);
         for (i_tableSize = 1; i_tableSize < i; i_tableSize = i_tableSize << 1) {
         }
         i_mask = i_tableSize - 1;
         i_maximumSize = (int)((float)i_tableSize * 0.5F);
         i_table = new HashtableIntEntry[i_tableSize];
      }
      
      private int newSize(int i) {
         return (int)((float)i / 0.5F);
      }
      
      public Object deepClone(Object obj) {
         Hashtable4 hashtable4_0_1 = (Hashtable4)j4o.lang.JavaSystem.clone(this);
         hashtable4_0_1.i_table = new HashtableIntEntry[i_tableSize];
         for (int i1 = 0; i1 < i_tableSize; i1++) {
            if (i_table[i1] != null) hashtable4_0_1.i_table[i1] = (HashtableIntEntry)i_table[i1].deepClone(obj);
         }
         return hashtable4_0_1;
      }
      
      public void forEachKey(Visitor4 visitor4) {
         for (int i1 = 0; i1 < i_table.Length; i1++) {
            for (HashtableIntEntry hashtableintentry1 = i_table[i1]; hashtableintentry1 != null; hashtableintentry1 = hashtableintentry1.i_next) {
               if (hashtableintentry1 is HashtableObjectEntry) visitor4.visit(((HashtableObjectEntry)hashtableintentry1).i_objectKey); else visitor4.visit(System.Convert.ToInt32(hashtableintentry1.i_key));
            }
         }
      }
      
      public void forEachValue(Visitor4 visitor4) {
         for (int i1 = 0; i1 < i_table.Length; i1++) {
            for (HashtableIntEntry hashtableintentry1 = i_table[i1]; hashtableintentry1 != null; hashtableintentry1 = hashtableintentry1.i_next) visitor4.visit(hashtableintentry1.i_object);
         }
      }
      
      public Object get(int i) {
         for (HashtableIntEntry hashtableintentry1 = i_table[i & i_mask]; hashtableintentry1 != null; hashtableintentry1 = hashtableintentry1.i_next) {
            if (hashtableintentry1.i_key == i) return hashtableintentry1.i_object;
         }
         return null;
      }
      
      public Object get(Object obj) {
         int i1 = obj.GetHashCode();
         for (HashtableObjectEntry hashtableobjectentry1 = (HashtableObjectEntry)i_table[i1 & i_mask]; hashtableobjectentry1 != null; hashtableobjectentry1 = (HashtableObjectEntry)hashtableobjectentry1.i_next) {
            if (hashtableobjectentry1.i_key == i1 && hashtableobjectentry1.i_objectKey.Equals(obj)) return hashtableobjectentry1.i_object;
         }
         return null;
      }
      
      public Object get(byte[] xis) {
         int i1 = hash(xis);
         for (HashtableObjectEntry hashtableobjectentry1 = (HashtableObjectEntry)i_table[i1 & i_mask]; hashtableobjectentry1 != null; hashtableobjectentry1 = (HashtableObjectEntry)hashtableobjectentry1.i_next) {
            if (hashtableobjectentry1.i_key == i1) {
               byte[] is_1_1 = (byte[])hashtableobjectentry1.i_objectKey;
               if (is_1_1.Length == xis.Length) {
                  bool xbool1 = true;
                  for (int i_2_1 = 0; i_2_1 < is_1_1.Length; i_2_1++) {
                     if (is_1_1[i_2_1] != xis[i_2_1]) xbool1 = false;
                  }
                  if (xbool1) return hashtableobjectentry1.i_object;
               }
            }
         }
         return null;
      }
      
      private int hash(byte[] xis) {
         int i1 = 0;
         for (int i_3_1 = 0; i_3_1 < xis.Length; i_3_1++) i1 = i1 * 31 + xis[i_3_1];
         return i1;
      }
      
      private void increaseSize() {
         i_tableSize = i_tableSize << 1;
         i_maximumSize = i_maximumSize << 1;
         i_mask = i_tableSize - 1;
         HashtableIntEntry[] hashtableintentrys1 = i_table;
         i_table = new HashtableIntEntry[i_tableSize];
         for (int i1 = 0; i1 < hashtableintentrys1.Length; i1++) reposition(hashtableintentrys1[i1]);
      }
      
      public void put(int i, Object obj) {
         put1(new HashtableIntEntry(i, obj));
      }
      
      public void put(Object obj, Object obj_4_) {
         put1(new HashtableObjectEntry(obj, obj_4_));
      }
      
      public void put(byte[] xis, Object obj) {
         int i1 = hash(xis);
         put1(new HashtableObjectEntry(i1, xis, obj));
      }
      
      private void put1(HashtableIntEntry hashtableintentry) {
         i_size++;
         if (i_size > i_maximumSize) increaseSize();
         int i1 = hashtableintentry.i_key & i_mask;
         hashtableintentry.i_next = i_table[i1];
         i_table[i1] = hashtableintentry;
      }
      
      public void remove(int i) {
         HashtableIntEntry hashtableintentry1 = i_table[i & i_mask];
         HashtableIntEntry hashtableintentry_5_1 = null;
         for (; hashtableintentry1 != null; hashtableintentry1 = hashtableintentry1.i_next) {
            if (hashtableintentry1.i_key == i) {
               if (hashtableintentry_5_1 != null) hashtableintentry_5_1.i_next = hashtableintentry1.i_next; else i_table[i & i_mask] = hashtableintentry1.i_next;
               i_size--;
               break;
            }
            hashtableintentry_5_1 = hashtableintentry1;
         }
      }
      
      public void remove(Object obj) {
         int i1 = obj.GetHashCode();
         HashtableObjectEntry hashtableobjectentry1 = (HashtableObjectEntry)i_table[i1 & i_mask];
         HashtableObjectEntry hashtableobjectentry_6_1 = null;
         for (; hashtableobjectentry1 != null; hashtableobjectentry1 = (HashtableObjectEntry)hashtableobjectentry1.i_next) {
            if (hashtableobjectentry1.i_key == i1 && hashtableobjectentry1.i_objectKey.Equals(obj)) {
               if (hashtableobjectentry_6_1 != null) hashtableobjectentry_6_1.i_next = hashtableobjectentry1.i_next; else i_table[i1 & i_mask] = hashtableobjectentry1.i_next;
               i_size--;
               break;
            }
            hashtableobjectentry_6_1 = hashtableobjectentry1;
         }
      }
      
      public Object remove(byte[] xis) {
         int i1 = hash(xis);
         HashtableObjectEntry hashtableobjectentry1 = (HashtableObjectEntry)i_table[i1 & i_mask];
         HashtableObjectEntry hashtableobjectentry_7_1 = null;
         for (; hashtableobjectentry1 != null; hashtableobjectentry1 = (HashtableObjectEntry)hashtableobjectentry1.i_next) {
            if (hashtableobjectentry1.i_key == i1) {
               byte[] is_8_1 = (byte[])hashtableobjectentry1.i_objectKey;
               if (is_8_1.Length == xis.Length) {
                  bool xbool1 = true;
                  for (int i_9_1 = 0; i_9_1 < is_8_1.Length; i_9_1++) {
                     if (is_8_1[i_9_1] != xis[i_9_1]) xbool1 = false;
                  }
                  if (xbool1) {
                     if (hashtableobjectentry_7_1 != null) hashtableobjectentry_7_1.i_next = hashtableobjectentry1.i_next; else i_table[i1 & i_mask] = hashtableobjectentry1.i_next;
                     i_size--;
                     return hashtableobjectentry1.i_object;
                  }
               }
            }
            hashtableobjectentry_7_1 = hashtableobjectentry1;
         }
         return null;
      }
      
      private void reposition(HashtableIntEntry hashtableintentry) {
         if (hashtableintentry != null) {
            reposition(hashtableintentry.i_next);
            hashtableintentry.i_next = i_table[hashtableintentry.i_key & i_mask];
            i_table[hashtableintentry.i_key & i_mask] = hashtableintentry;
         }
      }
   }
}