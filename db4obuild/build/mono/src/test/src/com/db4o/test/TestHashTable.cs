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
using com.db4o;
namespace com.db4o.test {

   public class TestHashTable {
      
      public TestHashTable() : base() {
      }
      internal Hashtable ht;
      
      public void configure() {
         Db4o.configure().objectClass(this).cascadeOnUpdate(true);
         Db4o.configure().objectClass(this).cascadeOnDelete(true);
      }
      
      public void store() {
         Test.deleteAllInstances(this);
         Test.deleteAllInstances(new Atom());
         Test.deleteAllInstances(new com.db4o.config.Entry());
         TestHashTable tht1 = new TestHashTable();
         tht1.ht = new Hashtable();
         tht1.ht["t1"] = new Atom("t1");
         tht1.ht["t2"] = new Atom("t2");
         Test.store(tht1);
      }
      
      public void test() {
         com.db4o.config.Entry checkEntries1 = new com.db4o.config.Entry();
         TestHashTable tht1 = (TestHashTable)Test.getOne(this);
         Test.ensure(tht1.ht.Count == 2);
         Test.ensure(tht1.ht["t1"].Equals(new Atom("t1")));
         Test.ensure(tht1.ht["t2"].Equals(new Atom("t2")));
         tht1.ht["t2"] = new Atom("t3");
         Test.store(tht1);
         if (Test.COMPARE_INTERNAL_OK) {
            Test.ensureOccurrences(checkEntries1, 2);
            Test.commit();
            Test.ensureOccurrences(checkEntries1, 2);
            Test.deleteAllInstances(this);
            Test.ensureOccurrences(checkEntries1, 0);
            Test.rollBack();
            Test.ensureOccurrences(checkEntries1, 2);
            Test.deleteAllInstances(this);
            Test.ensureOccurrences(checkEntries1, 0);
            Test.commit();
            Test.ensureOccurrences(checkEntries1, 0);
         }
      }
   }
}