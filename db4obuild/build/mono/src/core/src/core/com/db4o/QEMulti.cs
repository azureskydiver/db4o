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

   public class QEMulti : QE {
      
      public QEMulti() : base() {
      }
      internal Collection4 i_evaluators = new Collection4();
      
      internal override QE add(QE qe) {
         i_evaluators.ensure(qe);
         return this;
      }
      
      internal override bool identity() {
         bool xbool1 = false;
         Iterator4 iterator41 = i_evaluators.iterator();
         while (iterator41.hasNext()) {
            if (((QE)iterator41.next()).identity()) xbool1 = true; else return false;
         }
         return xbool1;
      }
      
      internal override bool evaluate(QConObject qconobject, QCandidate qcandidate, Object obj) {
         Iterator4 iterator41 = i_evaluators.iterator();
         while (iterator41.hasNext()) {
            if (((QE)iterator41.next()).evaluate(qconobject, qcandidate, obj)) return true;
         }
         return false;
      }
      
      internal override void indexBitMap(bool[] bools) {
         Iterator4 iterator41 = i_evaluators.iterator();
         while (iterator41.hasNext()) ((QE)iterator41.next()).indexBitMap(bools);
      }
      
      internal override bool supportsIndex() {
         Iterator4 iterator41 = i_evaluators.iterator();
         while (iterator41.hasNext()) {
            if (!((QE)iterator41.next()).supportsIndex()) return false;
         }
         return true;
      }
   }
}