/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.ext;
namespace com.db4o.test {

   public class CascadeDeleteArray {
      
      public CascadeDeleteArray() : base() {
      }
      internal ArrayElem[] elements;
      
      public void configure() {
         Db4o.configure().objectClass(this).cascadeOnDelete(true);
      }
      
      public void storeOne() {
        elements = new ArrayElem[]{
            new ArrayElem("one"),
            new ArrayElem("two"),
            new ArrayElem("three")         
        };
      }
      
      public void testOne() {
         Test.ensureOccurrences(typeof(ArrayElem), 3);
         Test.delete(this);
         Test.ensureOccurrences(typeof(ArrayElem), 0);
         Test.rollBack();
         Test.ensureOccurrences(typeof(ArrayElem), 3);
         Test.delete(this);
         Test.ensureOccurrences(typeof(ArrayElem), 0);
         Test.commit();
         Test.ensureOccurrences(typeof(ArrayElem), 0);
      }
      
      public class ArrayElem {
         internal String name;
         
         public ArrayElem() : base() {
         }
         
         public ArrayElem(String name) : base() {
            this.name = name;
         }
      }
   }
}