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

   internal class P1Object : Db4oTypeImpl {
      [Transient] private Transaction i_trans;
      [Transient] private YapObject i_yapObject;
      
      public P1Object() : base() {
      }
      
      internal P1Object(Transaction transaction) : base() {
         i_trans = transaction;
      }
      
      public void activate(Object obj, int i) {
         if (i_trans != null) {
            if (i < 0) i_trans.i_stream.activate1(i_trans, obj); else i_trans.i_stream.activate1(i_trans, obj, i);
         }
      }
      
      public virtual int activationDepth() {
         return 1;
      }
      
      public virtual int adjustReadDepth(int i) {
         return i;
      }
      
      internal virtual void checkActive() {
         if (i_trans != null) {
            if (i_yapObject == null) {
               i_yapObject = i_trans.i_stream.getYapObject(this);
               if (i_yapObject == null) {
                  i_trans.i_stream.set(this);
                  i_yapObject = i_trans.i_stream.getYapObject(this);
               }
            }
            if (validYapObject()) i_yapObject.activate(i_trans, this, activationDepth(), false);
         }
      }
      
      public virtual Object createDefault(Transaction transaction) {
         throw YapConst.virtualException();
      }
      
      internal void deactivate() {
         if (validYapObject()) i_yapObject.deactivate(i_trans, activationDepth());
      }
      
      internal void delete() {
         if (i_trans != null) {
            if (i_yapObject == null) i_yapObject = i_trans.i_stream.getYapObject(this);
            if (validYapObject()) i_trans.i_stream.delete3(i_trans, i_yapObject, this, 0);
         }
      }
      
      protected void delete(Object obj) {
         if (i_trans != null) i_trans.i_stream.delete(obj);
      }
      
      protected long getIDOf(Object obj) {
         if (i_trans == null) return 0L;
         return i_trans.i_stream.getID(obj);
      }
      
      protected Transaction getTrans() {
         return i_trans;
      }
      
      public virtual bool hasClassIndex() {
         return false;
      }
      
      public virtual void preDeactivate() {
      }
      
      public void setTrans(Transaction transaction) {
         i_trans = transaction;
      }
      
      public void setYapObject(YapObject yapobject) {
         i_yapObject = yapobject;
      }
      
      protected void store(Object obj) {
         if (i_trans != null) i_trans.i_stream.setInternal(i_trans, obj, true);
      }
      
      public virtual Object storedTo(Transaction transaction) {
         i_trans = transaction;
         return this;
      }
      
      internal Object streamLock() {
         if (i_trans != null) {
            i_trans.i_stream.checkClosed();
            return i_trans.i_stream.Lock();
         }
         return this;
      }
      
      internal virtual void store(int i) {
         if (i_trans != null) {
            if (i_yapObject == null) {
               i_yapObject = i_trans.i_stream.getYapObject(this);
               if (i_yapObject == null) {
                  i_trans.i_stream.setInternal(i_trans, this, true);
                  i_yapObject = i_trans.i_stream.getYapObject(this);
                  return;
               }
            }
            update(i);
         }
      }
      
      internal void update() {
         update(activationDepth());
      }
      
      internal void update(int i) {
         if (validYapObject()) {
            i_trans.i_stream.beginEndSet(i_trans);
            i_yapObject.writeUpdate(i_trans, i);
            i_trans.i_stream.checkStillToSet();
            i_trans.i_stream.beginEndSet(i_trans);
         }
      }
      
      private bool validYapObject() {
         return i_trans != null && i_yapObject != null && i_yapObject.getID() > 0;
      }
   }
}