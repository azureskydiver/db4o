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

   public class QField : Visitor4 {
      [Transient] internal Transaction i_trans;
      internal String i_name;
      [Transient] internal YapField i_yapField;
      internal int i_yapClassID;
      internal int i_index;
      
      public QField() : base() {
      }
      
      internal QField(Transaction transaction, String xstring, YapField yapfield, int i, int i_0_) : base() {
         i_trans = transaction;
         i_name = xstring;
         i_yapField = yapfield;
         i_yapClassID = i;
         i_index = i_0_;
         if (i_yapField != null && !i_yapField.alive()) i_yapField = null;
      }
      
      internal bool canHold(Object obj) {
         Object obj_1_1 = null;
         Class var_class1;
         if (obj != null) {
            if (obj is Class) var_class1 = (Class)obj; else var_class1 = j4o.lang.Class.getClassForObject(obj);
         } else return true;
         return i_yapField == null || i_yapField.canHold(var_class1);
      }
      
      internal YapClass getYapClass() {
         if (i_yapField != null) return i_yapField.getFieldYapClass(i_trans.i_stream);
         return null;
      }
      
      internal YapField getYapField(YapClass yapclass) {
         if (i_yapField != null) return i_yapField;
         YapField yapfield1 = yapclass.getYapField(i_name);
         if (yapfield1 != null) yapfield1.alive();
         return yapfield1;
      }
      
      internal bool isArray() {
         return i_yapField != null && i_yapField.getHandler() is YapArray;
      }
      
      internal bool isClass() {
         return i_yapField == null || i_yapField.getHandler().getType() == 2;
      }
      
      internal bool isSimple() {
         return i_yapField != null && i_yapField.getHandler().getType() == 1;
      }
      
      internal YapComparable prepareComparison(Object obj) {
         if (i_yapField != null) return i_yapField.prepareComparison(obj);
         if (obj == null) return Null.INSTANCE;
         YapClass yapclass1 = i_trans.i_stream.getYapClass(j4o.lang.Class.getClassForObject(obj), true);
         YapField yapfield1 = yapclass1.getYapField(i_name);
         if (yapfield1 != null) return yapfield1.prepareComparison(obj);
         return null;
      }
      
      internal void unmarshall(Transaction transaction) {
         if (i_yapClassID != 0) {
            YapClass yapclass1 = transaction.i_stream.getYapClass(i_yapClassID);
            i_yapField = yapclass1.i_fields[i_index];
         }
      }
      
      public void visit(Object obj) {
         ((QCandidate)obj).useField(this);
      }
   }
}