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
namespace com.db4o.test.types {

   public class Entry {
      public Object key;
      public Object value;
      
      public Entry() : base() {
      }
      
      public Entry(Object key, Object value) : base() {
         this.key = key;
         this.value = value;
      }
      
      public virtual Entry firstElement() {
         return new Entry("first", "firstvalue");
      }
      
      public virtual Entry lastElement() {
         return new Entry(new ObjectSimplePublic("lastKey"), new ObjectSimplePublic("lastValue"));
      }
      
      public virtual Entry noElement() {
         return new Entry("NO", "babe");
      }
      
      public virtual Entry[] test(int ver) {
         if (ver == 1) {
            return new Entry[]{firstElement(), new Entry(System.Convert.ToInt32(111), new ObjectSimplePublic("111")), new Entry(System.Convert.ToInt64(9999111), System.Convert.ToDouble((double)0.4566)), lastElement()};
         }
         return new Entry[]{new Entry(System.Convert.ToInt32(222), new ObjectSimplePublic("111")), new Entry("222", "TrippleTwo"), new Entry(new ObjectSimplePublic("2222"), new ObjectSimplePublic("222"))};
      }
      
      public void compare(Entry[] a_cmp, int oneOrTwo, bool keysOnly) {
         Entry[] tests = test(oneOrTwo);
         Entry[] cmp = new Entry[a_cmp.Length];
         j4o.lang.JavaSystem.arraycopy(a_cmp, 0, cmp, 0, a_cmp.Length);
         if (cmp == null) {
            Regression.addError("Entry:argument is null");
            return;
         }
         if (cmp.Length != tests.Length) {
            Regression.addError("Entry:arrays of different length");
            return;
         }
         for (int i = 0; i < tests.Length; i++) {
            bool found = false;
            for (int j = 0; j < cmp.Length; j++) {
               if (cmp[j] != null) {
                  if (tests[i].key.Equals(cmp[j].key)) {
                     if (!keysOnly) {
                        if (!tests[i].value.Equals(cmp[j].value)) {
                           Regression.addError("Entry:inequality");
                           return;
                        }
                     }
                     cmp[j] = null;
                     found = true;
                     break;
                  }
               }
            }
            if (!found) {
               Regression.addError("element not found");
            }
         }
      }
   }
}