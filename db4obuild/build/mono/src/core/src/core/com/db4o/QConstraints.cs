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

   public class QConstraints : QCon, Constraints {
      private Constraint[] i_constraints;
      
      internal QConstraints(Transaction transaction, Constraint[] constraints) : base(transaction) {
         i_constraints = constraints;
      }
      
      internal override Constraint join(Constraint constraint, bool xbool) {
         lock (this.streamLock()) {
            if (!(constraint is QCon)) return null;
            return ((QCon)constraint).join1(this, xbool);
         }
      }
      
      public Constraint[] toArray() {
         lock (this.streamLock()) {
            return i_constraints;
         }
      }
      
      public override Constraint contains() {
         lock (this.streamLock()) {
            for (int i1 = 0; i1 < i_constraints.Length; i1++) i_constraints[i1].contains();
            return this;
         }
      }
      
      public override Constraint equal() {
         lock (this.streamLock()) {
            for (int i1 = 0; i1 < i_constraints.Length; i1++) i_constraints[i1].equal();
            return this;
         }
      }
      
      public override Constraint greater() {
         lock (this.streamLock()) {
            for (int i1 = 0; i1 < i_constraints.Length; i1++) i_constraints[i1].greater();
            return this;
         }
      }
      
      public override Constraint identity() {
         lock (this.streamLock()) {
            for (int i1 = 0; i1 < i_constraints.Length; i1++) i_constraints[i1].identity();
            return this;
         }
      }
      
      public override Constraint not() {
         lock (this.streamLock()) {
            for (int i1 = 0; i1 < i_constraints.Length; i1++) i_constraints[i1].not();
            return this;
         }
      }
      
      public override Constraint like() {
         lock (this.streamLock()) {
            for (int i1 = 0; i1 < i_constraints.Length; i1++) i_constraints[i1].like();
            return this;
         }
      }
      
      public override Constraint smaller() {
         lock (this.streamLock()) {
            for (int i1 = 0; i1 < i_constraints.Length; i1++) i_constraints[i1].smaller();
            return this;
         }
      }
      
      public override Object getObject() {
         lock (this.streamLock()) {
            Object[] objs1 = new Object[i_constraints.Length];
            for (int i1 = 0; i1 < i_constraints.Length; i1++) objs1[i1] = i_constraints[i1].getObject();
            return objs1;
         }
      }
   }
}