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
using com.db4o.config;
using com.db4o.query;
namespace com.db4o {

   internal class QCandidate : TreeInt, Candidate, Orderable {
      internal YapReader i_bytes;
      internal QCandidates i_candidates;
      private List4 i_dependants;
      internal bool i_include = true;
      private Object i_member;
      internal Orderable i_order;
      internal Tree i_pendingJoins;
      private QCandidate i_root;
      internal YapClass i_yapClass;
      internal YapField i_yapField;
      
      internal QCandidate() : base(0) {
         i_candidates = null;
      }
      
      internal QCandidate(QCandidates qcandidates, int i, bool xbool) : base(i) {
         i_candidates = qcandidates;
         i_order = this;
         i_include = xbool;
      }
      
      internal QCandidate(QCandidates qcandidates, Object obj, int i) : base(i) {
         i_candidates = qcandidates;
         i_order = this;
         i_member = obj;
      }
      
      internal void addDependant(QCandidate qcandidate_0_) {
         i_dependants = new List4(i_dependants, qcandidate_0_);
      }
      
      private void checkInstanceOfCompare() {
         if (i_member is Compare) {
            i_member = ((Compare)i_member).compare();
            YapFile yapfile1 = getStream();
            i_yapClass = yapfile1.getYapClass(j4o.lang.Class.getClassForObject(i_member), false);
            i_key = (int)yapfile1.getID(i_member);
            i_bytes = yapfile1.readReaderByID(getTransaction(), i_key);
         }
      }
      
      internal override int compare(Tree tree) {
         return i_order.compareTo(((QCandidate)tree).i_order);
      }
      
      public int compareTo(Object obj) {
         return i_key - ((TreeInt)obj).i_key;
      }
      
      internal bool createChild(QCandidates qcandidates) {
         if (!i_include) return false;
         QCandidate qcandidate_1_1 = null;
         if (i_yapField != null) {
            YapDataType yapdatatype1 = i_yapField.getHandler();
            if (yapdatatype1 != null) {
               YapReader[] yapreaders1 = {
                  i_bytes               };
               YapDataType yapdatatype_2_1 = yapdatatype1.readArrayWrapper(getTransaction(), yapreaders1);
               if (yapdatatype_2_1 != null) {
                  int i1 = yapreaders1[0]._offset;
                  bool xbool1 = true;
                  if (qcandidates.i_constraints != null) {
                     Iterator4 iterator41 = new Iterator4(qcandidates.i_constraints);
                     while (iterator41.hasNext()) {
                        QCon qcon1 = (QCon)iterator41.next();
                        QField qfield1 = qcon1.getField();
                        if (qfield1 == null || qfield1.i_name.Equals(i_yapField.getName())) {
                           QCon qcon_3_1 = qcon1.i_parent;
                           qcon1.setParent(null);
                           QCandidates qcandidates_4_1 = new QCandidates(qcandidates.i_trans, null, qfield1);
                           qcandidates_4_1.addConstraint(qcon1);
                           qcon1.setCandidates(qcandidates_4_1);
                           yapdatatype_2_1.readCandidates(yapreaders1[0], qcandidates_4_1);
                           yapreaders1[0]._offset = i1;
                           bool bool_5_1 = qcon1.isNot();
                           if (bool_5_1) qcon1.removeNot();
                           qcandidates_4_1.evaluate();
                           Tree[] trees1 = new Tree[1];
                           bool[] bools1 = {
                              bool_5_1                           };
                           qcandidates_4_1.traverse(new QCandidate__1(this, bools1, bool_5_1, trees1));
                           if (bool_5_1) qcon1.not();
                           if (trees1[0] != null) trees1[0].traverse(new QCandidate__3(this));
                           if (!bools1[0]) {
                              qcon1.visit(getRoot(), qcon1.i_evaluator.not(false));
                              xbool1 = false;
                           }
                           qcon1.setParent(qcon_3_1);
                        }
                     }
                  }
                  return xbool1;
               }
               if (yapdatatype1.getType() == 1) {
                  qcandidates.i_currentConstraint.visit(this);
                  return true;
               }
            }
         }
         if (qcandidate_1_1 == null) {
            qcandidate_1_1 = readSubCandidate(qcandidates);
            if (qcandidate_1_1 == null) return false;
         }
         if (qcandidates.i_yapClass != null && qcandidates.i_yapClass.isStrongTyped() && i_yapField != null) {
            YapDataType yapdatatype1 = i_yapField.getHandler();
            if (yapdatatype1 != null && yapdatatype1.getType() == 2) {
               YapClass yapclass1 = (YapClass)yapdatatype1;
               if (yapclass1 is YapClassAny) yapclass1 = qcandidate_1_1.readYapClass();
               if (!yapclass1.canHold(qcandidates.i_yapClass.getJavaClass())) return false;
            }
         }
         addDependant(qcandidates.addByIdentity(qcandidate_1_1));
         return true;
      }
      
      internal void doNotInclude() {
         i_include = false;
         if (i_dependants != null) {
            Iterator4 iterator41 = new Iterator4(i_dependants);
            i_dependants = null;
            while (iterator41.hasNext()) ((QCandidate)iterator41.next()).doNotInclude();
         }
      }
      
      internal override bool duplicates() {
         return i_order.hasDuplicates();
      }
      
      internal bool evaluate(QConObject qconobject, QE qe) {
         if (i_member == null) i_member = value();
         return qe.evaluate(qconobject, this, qconobject.translate(i_member));
      }
      
      internal bool evaluate(QPending qpending) {
         QPending qpending_6_1 = (QPending)Tree.find(i_pendingJoins, qpending);
         if (qpending_6_1 == null) {
            qpending.changeConstraint();
            i_pendingJoins = Tree.add(i_pendingJoins, qpending);
            return true;
         }
         i_pendingJoins = i_pendingJoins.removeNode(qpending_6_1);
         qpending_6_1.i_join.evaluatePending(this, qpending_6_1, qpending, qpending.i_result);
         return false;
      }
      
      internal Class getJavaClass() {
         readYapClass();
         if (i_yapClass == null) return null;
         return i_yapClass.getJavaClass();
      }
      
      public ObjectContainer objectContainer() {
         return getStream();
      }
      
      public Object getObject() {
         Object obj1 = value(true);
         if (obj1 is YapReader) {
            YapReader yapreader1 = (YapReader)obj1;
            int i1 = yapreader1._offset;
            obj1 = yapreader1.ToString(getTransaction());
            yapreader1._offset = i1;
         }
         return obj1;
      }
      
      internal QCandidate getRoot() {
         return i_root == null ? this : i_root;
      }
      
      private YapFile getStream() {
         return getTransaction().i_file;
      }
      
      private Transaction getTransaction() {
         return i_candidates.i_trans;
      }
      
      public bool hasDuplicates() {
         return i_root != null;
      }
      
      public void hintOrder(int i, bool xbool) {
         i_order = new Order();
         i_order.hintOrder(i, xbool);
      }
      
      public bool include() {
         return i_include;
      }
      
      public void include(bool xbool) {
         i_include = xbool;
      }
      
      internal override void isDuplicateOf(Tree tree) {
         i_size = 0;
         i_root = (QCandidate)tree;
      }
      
      internal YapComparable prepareComparison(YapStream yapstream, Object obj) {
         if (i_yapField != null) return i_yapField.prepareComparison(obj);
         if (i_yapClass == null) {
            YapClass yapclass1 = null;
            if (i_bytes != null) yapclass1 = yapstream.getYapClass(j4o.lang.Class.getClassForObject(obj), true); else if (i_member != null) yapclass1 = yapstream.getYapClass(j4o.lang.Class.getClassForObject(i_member), false);
            if (yapclass1 != null) {
               if (i_member != null && j4o.lang.Class.getClassForObject(i_member).isArray()) {
                  YapDataType yapdatatype1 = (YapDataType)yapclass1.prepareComparison(obj);
                  if (Array4.isNDimensional(j4o.lang.Class.getClassForObject(i_member))) {
                     YapArrayN yaparrayn1 = new YapArrayN(yapdatatype1, false);
                     return yaparrayn1;
                  }
                  YapArray yaparray1 = new YapArray(yapdatatype1, false);
                  return yaparray1;
               }
               return yapclass1.prepareComparison(obj);
            }
            return null;
         }
         return i_yapClass.prepareComparison(obj);
      }
      
      internal void read() {
         if (i_include && i_bytes == null) {
            if (i_key > 0) {
               i_bytes = getStream().readReaderByID(getTransaction(), i_key);
               if (i_bytes == null) i_include = false;
            } else i_include = false;
         }
      }
      
      private QCandidate readSubCandidate(QCandidates qcandidates) {
         bool xbool1 = false;
         read();
         if (i_bytes != null) {
            int i1 = i_bytes._offset;
            int i_7_1;
            try {
               {
                  i_7_1 = i_bytes.readInt();
               }
            }  catch (Exception exception) {
               {
                  return null;
               }
            }
            i_bytes._offset = i1;
            if (i_7_1 != 0) {
               QCandidate qcandidate_8_1 = new QCandidate(qcandidates, i_7_1, true);
               qcandidate_8_1.i_root = getRoot();
               return qcandidate_8_1;
            }
         }
         return null;
      }
      
      private void readThis(bool xbool) {
         read();
         Transaction transaction1 = getTransaction();
         if (transaction1 != null) {
            i_member = transaction1.i_stream.getByID1(transaction1, (long)i_key);
            if (i_member != null && (xbool || i_member is Compare)) {
               transaction1.i_stream.activate1(transaction1, i_member);
               checkInstanceOfCompare();
            }
         }
      }
      
      internal YapClass readYapClass() {
         if (i_yapClass == null) {
            read();
            if (i_bytes != null) {
               i_bytes._offset = 0;
               i_yapClass = getStream().getYapClass(i_bytes.readInt());
               if (i_yapClass != null && YapConst.CLASS_COMPARE.isAssignableFrom(i_yapClass.getJavaClass())) readThis(false);
            }
         }
         return i_yapClass;
      }
      
      public override String ToString() {
         return base.ToString();
      }
      
      internal void useField(QField qfield) {
         read();
         if (i_bytes == null) i_yapField = null; else {
            readYapClass();
            i_member = null;
            if (qfield == null) i_yapField = null; else if (i_yapClass == null) i_yapField = null; else {
               i_yapField = qfield.getYapField(i_yapClass);
               if (i_yapField == null | !i_yapClass.findOffset(i_bytes, i_yapField)) {
                  if (i_yapClass.holdsAnyClass()) i_yapField = null; else i_yapField = new YapFieldNull();
               }
            }
         }
      }
      
      internal Object value() {
         return value(false);
      }
      
      internal Object value(bool xbool) {
         if (i_member == null) {
            if (i_yapField == null) readThis(xbool); else {
               int i1 = i_bytes._offset;
               try {
                  {
                     i_member = i_yapField.readQuery(getTransaction(), i_bytes);
                  }
               }  catch (CorruptionException corruptionexception) {
                  {
                     i_member = null;
                  }
               }
               i_bytes._offset = i1;
               checkInstanceOfCompare();
            }
         }
         return i_member;
      }
   }
}