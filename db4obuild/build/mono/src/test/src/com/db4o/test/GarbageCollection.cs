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
using com.db4o;
using com.db4o.test.types;
namespace com.db4o.test {

   public class GarbageCollection {
      
      public GarbageCollection() : base() {
      }
      
      public static void Main(String[] args) {
         new j4o.io.File("tgc.yap").delete();
         int strSize1 = 1;
         int objectCount1 = 10000;
         ObjectContainer con = Db4o.openFile("tgc.yap");
         String longString1 = "String";
         ObjectSimplePublic osp1 = null;
         ArrayTypedPublic atp1 = null;
         for (int i1 = 0; i1 < strSize1; i1++) {
            longString1 = longString1 + longString1;
         }
         int toGetTen1 = objectCount1 / 10;
         for (int i1 = 0; i1 < objectCount1; i1++) {
            atp1 = new ArrayTypedPublic();
            atp1.set(1);
            con.set(atp1);
            if ((double)i1 / toGetTen1 - i1 / toGetTen1 < 1.0E-6) {
               con.commit();
               con.ext().purge();
               mem();
            }
         }
         con.commit();
         con.ext().purge();
         longString1 = null;
         osp1 = null;
         mem();
         mem();
         con.close();
      }
      
      static internal void mem() {

          // TODO: look for equivalen for .NET

//         j4o.lang.JavaSystem.runFinalization();
//         Runtime r1 = Runtime.getRuntime();
//         r1.gc();
//         r1.runFinalization();
//         r1.gc();
//         Console.WriteLine(r1.totalMemory() - r1.freeMemory());
      }
   }
}