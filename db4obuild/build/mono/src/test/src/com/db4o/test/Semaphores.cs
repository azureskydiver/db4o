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
using com.db4o.ext;
namespace com.db4o.test {

   public class Semaphores : AllTestsConfAll {
      
      public Semaphores() : base() {
      }
      
      public void test() {
         ExtObjectContainer eoc1 = Test.objectContainer();
         eoc1.setSemaphore("SEM", 0);
         Test.ensure(eoc1.setSemaphore("SEM", 0) == true);
         if (Test.clientServer) {
            ExtObjectContainer client21 = null;
            try {
               {
                  client21 = Db4o.openClient(SERVER_HOSTNAME, SERVER_PORT, DB4O_USER, DB4O_PASSWORD).ext();
                  Test.ensure(client21.setSemaphore("SEM", 0) == false);
                  eoc1.releaseSemaphore("SEM");
                  Test.ensure(client21.setSemaphore("SEM", 0) == true);
               }
            }  catch (Exception e) {
               {
                  j4o.lang.JavaSystem.printStackTrace(e);
                  return;
               }
            }
         } else {
            eoc1.releaseSemaphore("SEM");
         }
      }
   }
}