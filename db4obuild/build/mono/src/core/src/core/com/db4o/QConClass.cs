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
using com.db4o.query;
namespace com.db4o {

   public class QConClass : QConObject {
      private Class i_class;
      private bool i_equal;
      
      public QConClass() : base() {
      }
      
      internal QConClass(Transaction transaction, QCon qcon, QField qfield, Class var_class) : base(transaction, qcon, qfield, (Object)null) {
         if (var_class != null) {
            i_yapClass = transaction.i_stream.getYapClass(var_class, true);
            if (var_class == YapConst.CLASS_OBJECT) i_yapClass = (YapClass)((YapClassPrimitive)i_yapClass).i_handler;
         }
         i_class = var_class;
      }
      
      internal override bool evaluate(QCandidate qcandidate) {
         bool xbool1 = true;
         Class var_class1 = qcandidate.getJavaClass();
         if (var_class1 == null) xbool1 = false; else xbool1 = i_equal ? i_class == var_class1 : i_class.isAssignableFrom(var_class1);
         return i_evaluator.not(xbool1);
      }
      
      internal override void evaluateSelf() {
         if (i_evaluator != QE.DEFAULT || i_orderID != 0 || i_joins != null || i_yapClass == null || i_candidates.i_yapClass == null || i_yapClass.getHigherHierarchy(i_candidates.i_yapClass) != i_yapClass) i_candidates.filter(this);
      }
      
      public override Constraint equal() {
         lock (this.streamLock()) {
            i_equal = true;
            return this;
         }
      }
      
      internal override bool isNullConstraint() {
         return false;
      }
      
      public override String ToString() {
         return base.ToString();
      }
   }
}