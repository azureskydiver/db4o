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
using com.db4o.query;
namespace com.db4o.test {

   public class SodaNoDuplicates {
      
      public SodaNoDuplicates() : base() {
      }
      internal Atom atom;
      
      public void store() {
         Test.deleteAllInstances(this);
         Test.deleteAllInstances(new Atom());
         Atom m11 = new Atom("One");
         Atom m21 = new Atom("Two");
         SodaNoDuplicates snd1 = new SodaNoDuplicates();
         snd1.atom = m11;
         Test.store(snd1);
         snd1 = new SodaNoDuplicates();
         snd1.atom = m11;
         Test.store(snd1);
         snd1 = new SodaNoDuplicates();
         snd1.atom = m21;
         Test.store(snd1);
         snd1 = new SodaNoDuplicates();
         snd1.atom = m21;
         Test.store(snd1);
      }
      
      public void test() {
         Query q1 = Test.query();
         q1.constrain(Class.getClassForType(typeof(SodaNoDuplicates)));
         Query qAtoms1 = q1.descend("atom");
         ObjectSet set1 = qAtoms1.execute();
         Test.ensure(set1.size() == 2);
      }
   }
}