/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;

using com.db4o.foundation;

namespace com.db4o.test.types {

   public class Stack4 {
      
      internal Collection4 i_compare;
      internal Collection4 i_with;
      
      internal bool Push(Object a_compare) {
         if (i_compare == null) {
            i_compare = new Collection4();
         } else {
            IEnumerator i = i_compare.GetEnumerator();
            while (i.MoveNext()) {
               if (i.Current == a_compare) {
                  return false;
               }
            }
         }
         i_compare.Add(a_compare);
         return true;
      }
   }
}