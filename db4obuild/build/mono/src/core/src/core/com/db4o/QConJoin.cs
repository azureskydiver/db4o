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

   public class QConJoin : QCon {
      internal bool i_and;
      internal QCon i_constraint1;
      internal QCon i_constraint2;
      
      public QConJoin() : base() {
      }
      
      internal QConJoin(Transaction transaction, QCon qcon, QCon qcon_0_, bool xbool) : base(transaction) {
         i_constraint1 = qcon;
         i_constraint2 = qcon_0_;
         i_and = xbool;
      }
      
      internal override void doNotInclude(QCandidate qcandidate) {
         i_constraint1.doNotInclude(qcandidate);
         i_constraint2.doNotInclude(qcandidate);
      }
      
      internal override void exchangeConstraint(QCon qcon, QCon qcon_1_) {
         base.exchangeConstraint(qcon, qcon_1_);
         if (qcon == i_constraint1) i_constraint1 = qcon_1_;
         if (qcon == i_constraint2) i_constraint2 = qcon_1_;
      }
      
      internal void evaluatePending(QCandidate qcandidate, QPending qpending, QPending qpending_2_, int i) {
         bool xbool1 = i_evaluator.not(i_and ? qpending.i_result + i > 0 : qpending.i_result + i > -4);
         if (i_joins != null) {
            Iterator4 iterator41 = i_joins.iterator();
            while (iterator41.hasNext()) {
               QConJoin qconjoin_3_1 = (QConJoin)iterator41.next();
               qcandidate.evaluate(new QPending(qconjoin_3_1, this, xbool1));
            }
         } else if (!xbool1) {
            i_constraint1.doNotInclude(qcandidate);
            i_constraint2.doNotInclude(qcandidate);
         }
      }
      
      internal QCon getOtherConstraint(QCon qcon) {
         if (qcon == i_constraint1) return i_constraint2;
         if (qcon == i_constraint2) return i_constraint1;
         return null;
      }
      
      internal override String logObject() {
         return "";
      }
      
      internal bool removeForParent(QCon qcon) {
         if (i_and) {
            QCon qcon_4_1 = getOtherConstraint(qcon);
            qcon_4_1.removeJoin(this);
            qcon_4_1.remove();
            return true;
         }
         return false;
      }
      
      public override String ToString() {
         return base.ToString();
      }
   }
}