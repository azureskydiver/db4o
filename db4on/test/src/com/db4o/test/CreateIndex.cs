/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

   public class CreateIndex {
      public String name;
      
      public CreateIndex() : base() {
      }
      
      public CreateIndex(String name) : base() {
         this.name = name;
      }
      
      public void configure() {
         Db4o.configure().objectClass(Class.getClassForType(typeof(CreateIndex))).objectField("name").indexed(true);
      }
      
      public void store() {
         Test.deleteAllInstances(this);
         Test.store(new CreateIndex("a"));
         Test.store(new CreateIndex("c"));
         Test.store(new CreateIndex("b"));
         Object obj11 = Db4o.configure().objectClass(Class.getClassForType(typeof(CreateIndex)));
         Object obj21 = Test.objectContainer().ext().configure().objectClass(Class.getClassForType(typeof(CreateIndex)));
         int xxx1 = 1;
      }
   }
}