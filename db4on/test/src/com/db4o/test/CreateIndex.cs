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
         Tester.deleteAllInstances(this);
         Tester.store(new CreateIndex("a"));
         Tester.store(new CreateIndex("c"));
         Tester.store(new CreateIndex("b"));
         Object obj11 = Db4o.configure().objectClass(Class.getClassForType(typeof(CreateIndex)));
         Object obj21 = Tester.objectContainer().ext().configure().objectClass(Class.getClassForType(typeof(CreateIndex)));
         int xxx1 = 1;
      }
   }
}