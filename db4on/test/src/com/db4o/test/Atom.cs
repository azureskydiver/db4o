/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test {

   public class Atom {
      public Atom child;
      public String name;
      
      public Atom() : base() {
      }
      
      public Atom(Atom child) : base() {
         this.child = child;
      }
      
      public Atom(String name) : base() {
         this.name = name;
      }
      
      public Atom(Atom child, String name) : this(child) {
         this.name = name;
      }
      
      public int compareTo(Object obj) {
         return 0;
      }
      
      public override bool Equals(Object obj) {
         if (obj is Atom) {
            Atom other1 = (Atom)obj;
            if (name == null) {
               if (other1.name != null) {
                  return false;
               }
            } else {
               if (!name.Equals(other1.name)) {
                  return false;
               }
            }
            if (child != null) {
               return child.Equals(other1.child);
            }
            return other1.child == null;
         }
         return false;
      }
      
      public override String ToString() {
         String str1 = "Atom(" + name + ")";
         if (child != null) {
            return str1 + "." + child.ToString();
         }
         return str1;
      }

       public override int GetHashCode(){
           if(name != null){
               return name.GetHashCode();
           }
           return 0;
       }

   }
}