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

   internal class YapClass__2 : Visitor4 {
      private Transaction val__trans;
      private int[] val__idgen;
      private QCandidates val__a_candidates;
      private YapClass stathis0;
      
      internal YapClass__2(YapClass yapclass, Transaction transaction, int[] xis, QCandidates qcandidates) : base() {
         stathis0 = yapclass;
         val__trans = transaction;
         val__idgen = xis;
         val__a_candidates = qcandidates;
      }
      
      public void visit(Object obj) {
         int i1 = (int)val__trans.i_stream.getID(obj);
         if (i1 == 0) i1 = val__idgen[0]--;
         val__a_candidates.addByIdentity(new QCandidate(val__a_candidates, obj, i1));
      }
   }
}