/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test {

   public class Molecule : Atom {
      
      public Molecule() : base() {
      }
      
      public Molecule(Atom child) : base(child) {
      }
      
      public Molecule(String name) : base(name) {
      }
      
      public Molecule(Atom child, String name) : base(child, name) {
      }
      
      public override bool Equals(Object obj) {
         if (obj is Molecule) {
            return base.Equals(obj);
         }
         return false;
      }
      
      public override String ToString() {
         String str1 = "Molecule(" + name + ")";
         if (child != null) {
            return str1 + "." + child.ToString();
         }
         return str1;
      }
   }
}