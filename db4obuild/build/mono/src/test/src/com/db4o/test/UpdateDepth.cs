/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

using System;
using j4o.lang;
using j4o.io;
using com.db4o;
using com.db4o.config;
using com.db4o.tools;
namespace com.db4o.test {

   public class UpdateDepth {
      
      public UpdateDepth() : base() {
      }
      internal String name;
      internal UpdateDepth child;
      internal UpdateDepth[] childArray;
      
      public static void Main(String[] args) {
         Configuration conf1 = Db4o.configure();
         conf1.objectClass("com.db4o.test.UpdateDepth").updateDepth(1);
         new File("updateDepth.yap").delete();
         ObjectContainer con1 = Db4o.openFile("updateDepth.yap");
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
         con1.set(ud1);
         ud1.name = "Update Level 0";
         ud1.child.name = "Update Level 1";
         ud1.child.child.name = "Update Level 2";
         ud1.childArray[0].name = "Update Array Level 1";
         ud1.child.childArray[0].name = "Update Array Level 2";
         con1.set(ud1);
         con1.close();
         con1 = Db4o.openFile("updateDepth.yap");
         set1 = con1.get(null);
         while (set1.hasNext()) {
            Logger.log(con1, set1.next());
         }
         con1.close();
      }
   }
}