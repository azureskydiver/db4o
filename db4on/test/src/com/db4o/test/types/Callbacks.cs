/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test.types {

   public class Callbacks: RTest {
      
      public Callbacks() : base() {
      }
      public String name;
      
      public void objectOnActivate(ObjectContainer container) {
         Console.WriteLine("onActivate");
      }
      
      public void objectOnDeactivate(ObjectContainer container) {
         Console.WriteLine("onDeactivate");
      }
      
      public void objectOnDelete(ObjectContainer container) {
         Console.WriteLine("onDelete");
      }
      
      public void objectOnNew(ObjectContainer container) {
         Console.WriteLine("onNew");
      }
      
      public void objectOnUpdate(ObjectContainer container) {
         Console.WriteLine("onUpdate");
      }
      
      public override void set(int ver) {
         if (ver == 1) {
            name = "OneONEOneONEOneONEOneONEOneONEOneONE";
         } else {
            name = "TwoTWOTwoTWOTwoTWOTwoTWOTwoTWO";
         }
      }
   }
}