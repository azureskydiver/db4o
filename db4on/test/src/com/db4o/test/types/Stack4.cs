/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class Stack4 {
      
      public Stack4() : base() {
      }
      internal Collection4 i_compare;
      internal Collection4 i_with;
      
      internal bool push(Object a_compare) {
         if (i_compare == null) {
            i_compare = new Collection4();
         } else {
            Iterator4 i = i_compare.iterator();
            while (i.hasNext()) {
               if (i.next() == a_compare) {
                  return false;
               }
            }
         }
         i_compare.add(a_compare);
         return true;
      }
   }
}