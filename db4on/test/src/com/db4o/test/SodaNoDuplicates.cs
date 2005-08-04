/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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
         Tester.deleteAllInstances(this);
         Tester.deleteAllInstances(new Atom());
         Atom m11 = new Atom("One");
         Atom m21 = new Atom("Two");
         SodaNoDuplicates snd1 = new SodaNoDuplicates();
         snd1.atom = m11;
         Tester.store(snd1);
         snd1 = new SodaNoDuplicates();
         snd1.atom = m11;
         Tester.store(snd1);
         snd1 = new SodaNoDuplicates();
         snd1.atom = m21;
         Tester.store(snd1);
         snd1 = new SodaNoDuplicates();
         snd1.atom = m21;
         Tester.store(snd1);
      }
      
      public void test() {
         Query q1 = Tester.query();
         q1.constrain(Class.getClassForType(typeof(SodaNoDuplicates)));
         Query qAtoms1 = q1.descend("atom");
         ObjectSet set1 = qAtoms1.execute();
         Tester.ensure(set1.size() == 2);
      }
   }
}