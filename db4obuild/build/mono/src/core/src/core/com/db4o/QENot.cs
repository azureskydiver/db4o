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

   public class QENot : QE {
      internal QE i_evaluator;
      
      internal QENot(QE qe) : base() {
         i_evaluator = qe;
      }
      
      internal override QE add(QE qe) {
         if (!(qe is QENot)) i_evaluator = i_evaluator.add(qe);
         return this;
      }
      
      internal override bool identity() {
         return i_evaluator.identity();
      }
      
      internal override bool evaluate(QConObject qconobject, QCandidate qcandidate, Object obj) {
         return !i_evaluator.evaluate(qconobject, qcandidate, obj);
      }
      
      internal override bool not(bool xbool) {
         return !xbool;
      }
      
      internal override void indexBitMap(bool[] bools) {
         i_evaluator.indexBitMap(bools);
         for (int i1 = 0; i1 < 4; i1++) bools[i1] = !bools[i1];
      }
      
      internal override bool supportsIndex() {
         return i_evaluator.supportsIndex();
      }
   }
}