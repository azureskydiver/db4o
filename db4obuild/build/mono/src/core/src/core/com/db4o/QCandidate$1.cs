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

   internal class QCandidate__1 : Visitor4 {
      private bool[] val__innerRes;
      private bool val__isNot;
      private Tree[] val__pending;
      private QCandidate stathis0;
      
      internal QCandidate__1(QCandidate qcandidate, bool[] bools, bool xbool, Tree[] trees) : base() {
         stathis0 = qcandidate;
         val__innerRes = bools;
         val__isNot = xbool;
         val__pending = trees;
      }
      
      public void visit(Object obj) {
         QCandidate qcandidate1 = (QCandidate)obj;
         if (qcandidate1.include()) val__innerRes[0] = !val__isNot;
         if (qcandidate1.i_pendingJoins != null) qcandidate1.i_pendingJoins.traverse(new QCandidate__2(this));
      }
      
      static internal Tree[] access__000(QCandidate__1 var_1) {
         return var_1.val__pending;
      }
   }
}