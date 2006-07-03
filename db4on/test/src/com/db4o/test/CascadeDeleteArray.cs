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
      
      public void Configure() {
         Db4o.Configure().ObjectClass(this).CascadeOnDelete(true);
      }
      
      public void StoreOne() {
        elements = new ArrayElem[]{
            new ArrayElem("one"),
            new ArrayElem("two"),
            new ArrayElem("three")         
        };
      }
      
      public void TestOne() {
         Tester.EnsureOccurrences(typeof(ArrayElem), 3);
         Tester.Delete(this);
         Tester.EnsureOccurrences(typeof(ArrayElem), 0);
         Tester.RollBack();
         Tester.EnsureOccurrences(typeof(ArrayElem), 3);
         Tester.Delete(this);
         Tester.EnsureOccurrences(typeof(ArrayElem), 0);
         Tester.Commit();
         Tester.EnsureOccurrences(typeof(ArrayElem), 0);
      }
      
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