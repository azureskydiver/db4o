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
using com.db4o.ext;
namespace com.db4o {

   internal class QResult : IntArrayList, ObjectSet, ExtObjectSet, Visitor4 {
      internal Tree i_candidates;
      internal bool i_checkDuplicates;
      internal Transaction i_trans;
      
      internal QResult(Transaction transaction) : base() {
         i_trans = transaction;
      }
      
      internal Object activate(Object obj) {
         YapStream yapstream1 = i_trans.i_stream;
         yapstream1.beginEndActivation();
         yapstream1.activate2(i_trans, obj, yapstream1.i_config.i_activationDepth);
         yapstream1.beginEndActivation();
         return obj;
      }
      
      internal void checkDuplicates() {
         i_checkDuplicates = true;
      }
      
      public ExtObjectSet ext() {
         return this;
      }
      
      public long[] getIDs() {
         lock (streamLock()) {
            return this.asLong();
         }
      }
      
      public override bool hasNext() {
         lock (streamLock()) {
            return base.hasNext();
         }
      }
      
      public virtual Object next() {
         lock (streamLock()) {
            YapStream yapstream1 = i_trans.i_stream;
            yapstream1.checkClosed();
            if (base.hasNext()) {
               Object obj1 = yapstream1.getByID2(i_trans, this.nextInt());
               if (obj1 == null) return next();
               return activate(obj1);
            }
            return null;
         }
      }
      
      public override void reset() {
         lock (streamLock()) {
            base.reset();
         }
      }
      
      public void visit(Object obj) {
         QCandidate qcandidate1 = (QCandidate)obj;
         if (qcandidate1.include()) addKeyCheckDuplicates(qcandidate1.i_key);
      }
      
      internal void addKeyCheckDuplicates(int i) {
         if (i_checkDuplicates) {
            TreeInt treeint1 = new TreeInt(i);
            i_candidates = Tree.add(i_candidates, treeint1);
            if (treeint1.i_size == 0) return;
         }
         this.add(i);
      }
      
      protected Object streamLock() {
         return i_trans.i_stream.i_lock;
      }
   }
}