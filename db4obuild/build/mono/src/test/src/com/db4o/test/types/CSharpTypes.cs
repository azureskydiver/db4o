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
      
      public override void set(int ver) {
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