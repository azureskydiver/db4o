/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.util;
namespace com.db4o.test.types {

   public class CSharpTypes: RTest {

       sbyte tsbyte;
       byte tbyte;
       char tchar;              
       short tshort;            
       ushort tushort;          
       int tint;              
       uint tuint; 
       long tlong;
       ulong tulong;
      
      public CSharpTypes() : base() {
      }
      
      public override void Set(int ver) {
         if (ver == 1) {
             tsbyte = 1;
             tbyte = 1;
             tchar = (char)1;
             tshort = 1;
             tushort = 1;
             tint = 1;
             tuint = 1;
             tlong = 1;
             tulong = 1;
         } else {
             tsbyte = 2;
             tbyte = 2;
             tchar = (char)2;
             tshort = 2;
             tushort = 2;
             tint = 2;
             tuint = 2;
             tlong = 2;
             tulong = 2;
         }
      }
      
   }
}