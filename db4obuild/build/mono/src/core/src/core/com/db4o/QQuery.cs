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

   public class QQuery : Query {
      [Transient] private static IDGenerator i_orderingGenerator = new IDGenerator();
      [Transient] internal Transaction i_trans;
      private Collection4 i_constraints;
      private QQuery i_parent;
      private String i_field;
      
      public QQuery() : base() {
         i_constraints = new Collection4();
      }
      
      internal QQuery(Transaction transaction, QQuery qquery_0_, String xstring) : base() {
         i_constraints = new Collection4();
         i_trans = transaction;
         i_parent = qquery_0_;
         i_field = xstring;
      }
      
      internal void addConstraint(QCon qcon) {
         i_constraints.add(qcon);
      }
      
      private void addConstraint(Collection4 collection4, Object obj) {
         bool xbool1 = false;
         Iterator4 iterator41 = i_constraints.iterator();
         while (iterator41.hasNext()) {
            QCon qcon1 = (QCon)iterator41.next();
            bool[] bools1 = {
               false            };
            QCon qcon_1_1 = qcon1.shareParent(obj, bools1);
            if (qcon_1_1 != null) {
               addConstraint(qcon_1_1);
               collection4.add(qcon_1_1);
               if (bools1[0]) removeConstraint(qcon1);
               xbool1 = true;
            }
         }
         if (!xbool1) {
            QConObject qconobject1 = new QConObject(i_trans, null, null, obj);
            addConstraint(qconobject1);
            collection4.add(qconobject1);
         }
      }
      
      public Constraint constrain(Object obj) {
         lock (streamLock()) {
            Object obj_2_1 = null;
            obj = Platform.getClassForType(obj);
            if (YapConst.CLASS_CLASS.isInstance(obj)) {
               Class var_class1 = (Class)obj;
               Collection4 collection42 = new Collection4();
               if (var_class1.isInterface()) {
                  Collection4 collection4_3_1 = i_trans.i_stream.i_classCollection.forInterface(var_class1);
                  if (collection4_3_1.size() == 0) return null;
                  Iterator4 iterator42 = collection4_3_1.iterator();
                  Constraint constraint1 = null;
                  while (iterator42.hasNext()) {
                     YapClass yapclass1 = (YapClass)iterator42.next();
                     Class var_class_4_1 = yapclass1.getJavaClass();
                     if (var_class_4_1 != null && !var_class_4_1.isInterface()) {
                        if (constraint1 == null) constraint1 = constrain(var_class_4_1); else constraint1 = constraint1.or(constrain(yapclass1.getJavaClass()));
                     }
                  }
                  return constraint1;
               }
               Iterator4 iterator41 = i_constraints.iterator();
               while (iterator41.hasNext()) {
                  QConObject qconobject1 = (QConObject)iterator41.next();
                  bool[] bools1 = {
                     false                  };
                  QConClass qconclass1 = qconobject1.shareParentForClass(var_class1, bools1);
                  if (qconclass1 != null) {
                     addConstraint(qconclass1);
                     collection42.add(qconclass1);
                     if (bools1[0]) removeConstraint(qconobject1);
                  }
               }
               if (collection42.size() == 0) {
                  QConClass qconclass1 = new QConClass(i_trans, null, null, var_class1);
                  addConstraint(qconclass1);
                  return qconclass1;
               }
               if (collection42.size() == 1) return (Constraint)collection42.iterator().next();
               Constraint[] constraints1 = new Constraint[collection42.size()];
               collection42.toArray(constraints1);
               return new QConstraints(i_trans, constraints1);
            }
            QEvaluation qevaluation1 = Platform.evaluationCreate(i_trans, obj);
            if (qevaluation1 != null) {
               Iterator4 iterator41 = i_constraints.iterator();
               while (iterator41.hasNext()) ((QCon)iterator41.next()).addConstraint(qevaluation1);
               return null;
            }
            Collection4 collection41 = new Collection4();
            addConstraint(collection41, obj);
            return toConstraint(collection41);
         }
      }
      
      public Constraints constraints() {
         lock (streamLock()) {
            Constraint[] constraints1 = new Constraint[i_constraints.size()];
            i_constraints.toArray(constraints1);
            return new QConstraints(i_trans, constraints1);
         }
      }
      
      public Query descend(String xstring) {
         lock (streamLock()) {
            QQuery qquery_5_1 = new QQuery(i_trans, this, xstring);
            int[] xis1 = {
               1            };
            if (!descend1(qquery_5_1, xstring, xis1) && xis1[0] == 1) {
               xis1[0] = 2;
               if (!descend1(qquery_5_1, xstring, xis1)) return null;
            }
            return qquery_5_1;
         }
      }
      
      private bool descend1(QQuery qquery_6_, String xstring, int[] xis) {
         bool[] bools1 = {
            false         };
         if (xis[0] == 2 || i_constraints.size() == 0) {
            xis[0] = 0;
            bool[] bools_7_1 = {
               false            };
            i_trans.i_stream.i_classCollection.yapFields(xstring, new QQuery__1(this, bools_7_1));
         }
         Iterator4 iterator41 = i_constraints.iterator();
         while (iterator41.hasNext()) {
            if (((QCon)iterator41.next()).attach(qquery_6_, xstring)) bools1[0] = true;
         }
         return bools1[0];
      }
      
      public ObjectSet execute() {
         lock (streamLock()) {
            QResult qresult1 = new QResult(i_trans);
            execute1(qresult1);
            return qresult1;
         }
      }
      
      internal void execute1(QResult qresult) {
         if (i_trans.i_stream.isClient()) {
            marshall();
            ((YapClient)i_trans.i_stream).queryExecute(this, qresult);
         } else execute2(qresult);
      }
      
      internal void execute2(QResult qresult) {
         bool xbool1 = false;
         bool bool_8_1 = true;
         List4 list41 = null;
         Iterator4 iterator41 = i_constraints.iterator();
         while (iterator41.hasNext()) {
            QCon qcon1 = (QCon)iterator41.next();
            QCon qcon_9_1 = qcon1;
            bool bool_10_1 = false;
            qcon1 = qcon1.getRoot();
            if (qcon1 != qcon_9_1) {
               xbool1 = true;
               bool_8_1 = false;
            }
            YapClass yapclass1 = qcon1.getYapClass();
            if (yapclass1 != null) {
               if (list41 != null) {
                  Iterator4 iterator4_11_1 = new Iterator4(list41);
                  while (iterator4_11_1.hasNext()) {
                     QCandidates qcandidates1 = (QCandidates)iterator4_11_1.next();
                     if (qcandidates1.tryAddConstraint(qcon1)) {
                        bool_10_1 = true;
                        break;
                     }
                  }
               }
               if (!bool_10_1) {
                  QCandidates qcandidates1 = new QCandidates(i_trans, qcon1.getYapClass(), null);
                  qcandidates1.addConstraint(qcon1);
                  list41 = new List4(list41, qcandidates1);
               }
            }
         }
         if (list41 != null) {
            iterator41 = new Iterator4(list41);
            while (iterator41.hasNext()) ((QCandidates)iterator41.next()).execute();
            if (list41.i_next != null) xbool1 = true;
            if (xbool1) qresult.checkDuplicates();
            iterator41 = new Iterator4(list41);
            while (iterator41.hasNext()) {
               QCandidates qcandidates1 = (QCandidates)iterator41.next();
               if (bool_8_1) qcandidates1.traverse(qresult); else {
                  QQuery qquery_12_1 = this;
                  Collection4 collection41 = new Collection4();
                  for (; qquery_12_1.i_parent != null; qquery_12_1 = qquery_12_1.i_parent) collection41.add(qquery_12_1.i_field);
                  qcandidates1.traverse(new QQuery__2(this, collection41, qresult));
               }
            }
         }
         qresult.reset();
      }
      
      internal Transaction getTransaction() {
         return i_trans;
      }
      
      public Query orderAscending() {
         lock (streamLock()) {
            setOrdering(i_orderingGenerator.next());
            return this;
         }
      }
      
      public Query orderDescending() {
         lock (streamLock()) {
            setOrdering(-i_orderingGenerator.next());
            return this;
         }
      }
      
      private void setOrdering(int i) {
         Iterator4 iterator41 = i_constraints.iterator();
         while (iterator41.hasNext()) ((QCon)iterator41.next()).setOrdering(i);
      }
      
      internal void marshall() {
         Iterator4 iterator41 = i_constraints.iterator();
         while (iterator41.hasNext()) ((QCon)iterator41.next()).getRoot().marshall();
      }
      
      internal void removeConstraint(QCon qcon) {
         i_constraints.remove(qcon);
      }
      
      internal void unmarshall(Transaction transaction) {
         i_trans = transaction;
         Iterator4 iterator41 = i_constraints.iterator();
         while (iterator41.hasNext()) ((QCon)iterator41.next()).unmarshall(transaction);
      }
      
      internal Constraint toConstraint(Collection4 collection4) {
         Iterator4 iterator41 = collection4.iterator();
         if (collection4.size() == 1) return (Constraint)iterator41.next();
         if (collection4.size() > 0) {
            Constraint[] constraints1 = new Constraint[collection4.size()];
            collection4.toArray(constraints1);
            return new QConstraints(i_trans, constraints1);
         }
         return null;
      }
      
      protected Object streamLock() {
         return i_trans.i_stream.i_lock;
      }
   }
}