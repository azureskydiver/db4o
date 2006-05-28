/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

using Db4oTools;

using j4o.lang;
using j4o.io;
using com.db4o;
using com.db4o.config;

namespace com.db4o.test {

   public class UpdateDepth {
      
      public UpdateDepth() : base() {
      }
      internal String name;
      internal UpdateDepth child;
      internal UpdateDepth[] childArray;
      
		
      public static void Main(String[] args) {
         Configuration conf1 = Db4o.Configure();
         conf1.ObjectClass("com.db4o.test.UpdateDepth").UpdateDepth(1);
         new File("updateDepth.yap").Delete();
         ObjectContainer con1 = Db4o.OpenFile("updateDepth.yap");
         ObjectSet set1 = null;
         UpdateDepth ud1 = new UpdateDepth();
         ud1.name = "Level 0";
         ud1.child = new UpdateDepth();
         ud1.child.name = "Level 1";
         ud1.child.child = new UpdateDepth();
         ud1.child.child.name = "Level 2";
         ud1.childArray = new UpdateDepth[]{
            new UpdateDepth()         };
         ud1.childArray[0].name = "Array Level 1";
         ud1.child.childArray = new UpdateDepth[]{
            new UpdateDepth()         };
         ud1.child.childArray[0].name = "Array Level 2";
         con1.Set(ud1);
         ud1.name = "Update Level 0";
         ud1.child.name = "Update Level 1";
         ud1.child.child.name = "Update Level 2";
         ud1.childArray[0].name = "Update Array Level 1";
         ud1.child.childArray[0].name = "Update Array Level 2";
         con1.Set(ud1);
         con1.Close();
         con1 = Db4o.OpenFile("updateDepth.yap");
         set1 = con1.Get(null);
         while (set1.HasNext()) {
            Logger.Log(con1, set1.Next());
         }
         con1.Close();
      }
   }
}
