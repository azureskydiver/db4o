/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test.types {

   public class Callbacks: RTest {
      
      public Callbacks() : base() {
      }
      public String name;
      
      public void ObjectOnActivate(ObjectContainer container) {
         Console.WriteLine("onActivate");
      }
      
      public void ObjectOnDeactivate(ObjectContainer container) {
         Console.WriteLine("onDeactivate");
      }
      
      public void ObjectOnDelete(ObjectContainer container) {
         Console.WriteLine("onDelete");
      }
      
      public void ObjectOnNew(ObjectContainer container) {
         Console.WriteLine("onNew");
      }
      
      public void ObjectOnUpdate(ObjectContainer container) {
         Console.WriteLine("onUpdate");
      }
      
      public override void Set(int ver) {
         if (ver == 1) {
            name = "OneONEOneONEOneONEOneONEOneONEOneONE";
         } else {
            name = "TwoTWOTwoTWOTwoTWOTwoTWOTwoTWO";
         }
      }
   }
}