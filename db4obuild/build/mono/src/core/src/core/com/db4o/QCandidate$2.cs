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

   internal class QCandidate__2 : Visitor4 {
      private QCandidate__1 stathis1;
      
      internal QCandidate__2(QCandidate__1 var_1) : base() {
         stathis1 = var_1;
      }
      
      public void visit(Object obj) {
         QPending qpending1 = (QPending)obj;
         qpending1.changeConstraint();
         QPending qpending_0_1 = (QPending)Tree.find(QCandidate__1.access__000(stathis1)[0], qpending1);
         if (qpending_0_1 != null) {
            if (qpending_0_1.i_result != qpending1.i_result) qpending_0_1.i_result = 1;
         } else QCandidate__1.access__000(stathis1)[0] = Tree.add(QCandidate__1.access__000(stathis1)[0], qpending1);
      }
   }
}