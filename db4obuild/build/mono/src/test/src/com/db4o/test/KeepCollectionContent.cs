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
using System.Collections;
using j4o.lang;
using j4o.util;
namespace com.db4o.test {

   public class KeepCollectionContent {
      
      public KeepCollectionContent() : base() {
      }
      
      public void store() {
         Test.deleteAllInstances(new Atom());
         Test.deleteAllInstances(new System.Collections.Hashtable());
         Test.deleteAllInstances(new ArrayList());
         System.Collections.Hashtable ht1 = new System.Collections.Hashtable();
         ht1.Add(new Atom(), new Atom());
         Test.store(ht1);
         ArrayList al1 = new ArrayList();
         al1.Add(new Atom());
         Test.store(al1);
      }
      
      public void test() {
         Test.deleteAllInstances(new System.Collections.Hashtable());
         Test.deleteAllInstances(new ArrayList());
         Test.ensureOccurrences(new Atom(), 3);
      }
   }
}