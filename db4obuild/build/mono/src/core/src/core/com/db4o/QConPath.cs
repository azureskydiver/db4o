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

   public class QConPath : QConClass {
      
      public QConPath() : base() {
      }
      
      internal QConPath(Transaction transaction, QCon qcon, QField qfield) : base(transaction, qcon, qfield, null) {
         if (qfield != null) i_yapClass = qfield.getYapClass();
      }
      
      internal override int candidateCountByIndex(int i) {
         return -1;
      }
      
      internal override bool evaluate(QCandidate qcandidate) {
         if (qcandidate.getJavaClass() == null) this.visitOnNull(qcandidate.getRoot());
         return true;
      }
      
      internal override void evaluateSelf() {
      }
      
      internal override bool isNullConstraint() {
         return i_subConstraints == null;
      }
      
      internal override QConClass shareParentForClass(Class var_class, bool[] bools) {
         if (i_parent != null && i_field.canHold(var_class)) {
            QConClass qconclass1 = new QConClass(i_trans, i_parent, i_field, var_class);
            morph(bools, qconclass1, var_class);
            return qconclass1;
         }
         return null;
      }
      
      internal override QCon shareParent(Object obj, bool[] bools) {
         if (i_parent != null && i_field.canHold(obj)) {
            QConObject qconobject1 = new QConObject(i_trans, i_parent, i_field, obj);
            Class var_class1 = obj == null ? null : j4o.lang.Class.getClassForObject(obj);
            morph(bools, qconobject1, var_class1);
            return qconobject1;
         }
         return null;
      }
      
      private void morph(bool[] bools, QConObject qconobject, Class var_class) {
         bool xbool1 = true;
         if (var_class != null) {
            YapClass yapclass1 = i_trans.i_stream.getYapClass(var_class, true);
            if (yapclass1 != null && i_subConstraints != null) {
               Iterator4 iterator41 = new Iterator4(i_subConstraints);
               while (iterator41.hasNext()) {
                  QField qfield1 = ((QCon)iterator41.next()).getField();
                  if (!yapclass1.hasField(i_trans.i_stream, qfield1.i_name)) {
                     xbool1 = false;
                     break;
                  }
               }
            }
         }
         if (xbool1) {
            if (i_subConstraints != null) {
               Iterator4 iterator41 = new Iterator4(i_subConstraints);
               while (iterator41.hasNext()) qconobject.addConstraint((QCon)iterator41.next());
            }
            if (i_joins != null) {
               Iterator4 iterator41 = i_joins.iterator();
               while (iterator41.hasNext()) {
                  QConJoin qconjoin1 = (QConJoin)iterator41.next();
                  qconjoin1.exchangeConstraint(this, qconobject);
                  qconobject.addJoin(qconjoin1);
               }
            }
            i_parent.exchangeConstraint(this, qconobject);
            bools[0] = true;
         } else i_parent.addConstraint(qconobject);
      }
      
      internal override bool visitSelfOnNull() {
         return false;
      }
      
      public override String ToString() {
         return base.ToString();
      }
   }
}