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
using com.db4o.tools;
namespace com.db4o.test {

   public class LogAll {
      
      public LogAll() : base() {
      }
      
      public static void Main(String[] args) {
         run(args[0]);
      }
      
      public static void run(String fileName) {
         Console.WriteLine("/** Logging database file: \'" + fileName + "\' **/");
         ObjectContainer con1 = Db4o.openFile(fileName);
         ObjectSet set1 = con1.get(null);
         while (set1.hasNext()) {
            Logger.log(con1, set1.next());
         }
         con1.close();
      }
   }
}