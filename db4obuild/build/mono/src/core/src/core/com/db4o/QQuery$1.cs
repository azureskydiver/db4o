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

   internal class QQuery__1 : Visitor4 {
      private bool[] val__anyClassCollected;
      private QQuery stathis0;
      
      internal QQuery__1(QQuery qquery, bool[] bools) : base() {
         stathis0 = qquery;
         val__anyClassCollected = bools;
      }
      
      public void visit(Object obj) {
         Object[] objs1 = (Object[])obj;
         YapClass yapclass1 = (YapClass)objs1[0];
         YapField yapfield1 = (YapField)objs1[1];
         YapClass yapclass_0_1 = yapfield1.getFieldYapClass(stathis0.i_trans.i_stream);
         bool xbool1 = true;
         if (yapclass_0_1 is YapClassAny) {
            if (val__anyClassCollected[0]) xbool1 = false; else val__anyClassCollected[0] = true;
         }
         if (xbool1) {
            QConClass qconclass1 = new QConClass(stathis0.i_trans, null, yapfield1.qField(stathis0.i_trans), yapclass1.getJavaClass());
            stathis0.addConstraint(qconclass1);
         }
      }
   }
}