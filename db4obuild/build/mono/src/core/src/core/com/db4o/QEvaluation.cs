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

   public class QEvaluation : QCon {
      [Transient] private Object i_evaluation;
      internal byte[] i_marshalledEvaluation;
      internal int i_marshalledID;
      internal bool i_isDelegate;
      
      public QEvaluation() : base() {
      }
      
      internal QEvaluation(Transaction transaction, Object obj, bool xbool) : base(transaction) {
         i_evaluation = obj;
         i_isDelegate = xbool;
      }
      
      internal override void evaluateEvaluationsExec(QCandidates qcandidates, bool xbool) {
         if (xbool) qcandidates.traverse(new QEvaluation__1(this));
         qcandidates.filter(this);
      }
      
      internal override void marshall() {
         base.marshall();
         int[] xis1 = {
            0         };
         i_marshalledEvaluation = i_trans.i_stream.marshall(i_evaluation, xis1);
         i_marshalledID = xis1[0];
      }
      
      internal override void unmarshall(Transaction transaction) {
         if (i_trans == null) {
            base.unmarshall(transaction);
            i_evaluation = i_trans.i_stream.unmarshall(i_marshalledEvaluation, i_marshalledID);
         }
      }
      
      public override void visit(Object obj) {
         QCandidate qcandidate1 = (QCandidate)obj;
         try {
            {
               Platform.evaluationEvaluate(i_evaluation, qcandidate1);
               if (!qcandidate1.i_include) this.doNotInclude(qcandidate1.getRoot());
            }
         }  catch (Exception exception) {
            {
               qcandidate1.include(false);
               this.doNotInclude(qcandidate1.getRoot());
            }
         }
      }
      
      internal bool supportsIndex() {
         return false;
      }
   }
}