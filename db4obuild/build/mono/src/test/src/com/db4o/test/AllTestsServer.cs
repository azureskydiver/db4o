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
namespace com.db4o.test {

    /// <summary>
    /// This class will start a dedicated server for AllTests to run tests
    /// on different machines.
    /// </summary>
    ///
   public class AllTestsServer : AllTests, Runnable {
      
      public AllTestsServer() : base() {
      }
      
      public static void Main(String[] args) {
         new AllTestsServer().run();
      }
      
      public override void run() {
         Db4o.configure().messageLevel(-1);
         logConfiguration();
         Console.WriteLine("Waiting for tests to be run from different machine.");
         Console.WriteLine("\n\nThe server will need to be closed with CTRL + C.\n\n");
         Test.delete();
         configure();
         Test.runServer = true;
         Test.clientServer = true;
         Test.open();
      }
   }
}