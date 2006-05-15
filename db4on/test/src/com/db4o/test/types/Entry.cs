/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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
		 System.Array.Copy(a_cmp, 0, cmp, 0, a_cmp.Length);
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