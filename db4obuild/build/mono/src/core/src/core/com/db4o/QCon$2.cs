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

   internal class QCon__2 : Visitor4 {
      private YapField[] val__yfs;
      private int[] val__count;
      private QCon stathis0;
      
      internal QCon__2(QCon qcon, YapField[] yapfields, int[] xis) : base() {
         stathis0 = qcon;
         val__yfs = yapfields;
         val__count = xis;
      }
      
      public void visit(Object obj) {
         val__yfs[0] = (YapField)((Object[])obj)[1];
         val__count[0]++;
      }
   }
}