/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class IntEntry: Entry {
      
      public IntEntry() : base() {
      }
      
      public override Entry firstElement() {
         return new Entry(System.Convert.ToInt32(101), "firstvalue");
      }
      
      public override Entry lastElement() {
         return new Entry(System.Convert.ToInt32(9999999), new ObjectSimplePublic("lastValue"));
      }
      
      public override Entry noElement() {
         return new Entry(System.Convert.ToInt32(-99999), "babe");
      }
      
      public override Entry[] test(int ver) {
         if (ver == 1) {
            return new Entry[]{firstElement(), new Entry(System.Convert.ToInt32(111), new ObjectSimplePublic("111")), new Entry(System.Convert.ToInt32(9999111), System.Convert.ToDouble((double)0.4566)), lastElement()};
         }
         return new Entry[]{new Entry(System.Convert.ToInt32(222), new ObjectSimplePublic("111")), new Entry(System.Convert.ToInt32(333), "TrippleThree"), new Entry(System.Convert.ToInt32(4444), new ObjectSimplePublic("4444"))};
      }
   }
}