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
      
      public void Store() {
         Tester.DeleteAllInstances(this);
         Tester.DeleteAllInstances(new Atom());
         Atom m11 = new Atom("One");
         Atom m21 = new Atom("Two");
         SodaNoDuplicates snd1 = new SodaNoDuplicates();
         snd1.atom = m11;
         Tester.Store(snd1);
         snd1 = new SodaNoDuplicates();
         snd1.atom = m11;
         Tester.Store(snd1);
         snd1 = new SodaNoDuplicates();
         snd1.atom = m21;
         Tester.Store(snd1);
         snd1 = new SodaNoDuplicates();
         snd1.atom = m21;
         Tester.Store(snd1);
      }
      
      public void Test() {
         Query q1 = Tester.Query();
         q1.Constrain(Class.GetClassForType(typeof(SodaNoDuplicates)));
         Query qAtoms1 = q1.Descend("atom");
         ObjectSet set1 = qAtoms1.Execute();
         Tester.Ensure(set1.Size() == 2);
      }
   }
}