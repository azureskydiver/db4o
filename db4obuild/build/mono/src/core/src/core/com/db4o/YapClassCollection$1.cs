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
namespace com.db4o {

   internal class YapClassCollection__1 : Visitor4 {
      private String val__a_field;
      private Visitor4 val__a_visitor;
      private YapClass val__yc;
      private YapClassCollection stathis0;
      
      internal YapClassCollection__1(YapClassCollection yapclasscollection, String xstring, Visitor4 visitor4, YapClass yapclass) : base() {
         stathis0 = yapclasscollection;
         val__a_field = xstring;
         val__a_visitor = visitor4;
         val__yc = yapclass;
      }
      
      public void visit(Object obj) {
         YapField yapfield1 = (YapField)obj;
         if (yapfield1.alive() && val__a_field.Equals(yapfield1.getName())) val__a_visitor.visit(new Object[]{
            val__yc,
yapfield1         });
      }
   }
}