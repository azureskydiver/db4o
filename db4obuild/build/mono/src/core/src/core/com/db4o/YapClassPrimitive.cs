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

   internal class YapClassPrimitive : YapClass {
      internal YapDataType i_handler;
      
      internal YapClassPrimitive(YapStream yapstream, YapDataType yapdatatype) : base() {
         i_fields = YapField.EMPTY_ARRAY;
         i_handler = yapdatatype;
         i_objectLength = memberLength();
      }
      
      internal override void activateFields(Transaction transaction, Object obj, int i) {
      }
      
      internal override void addToIndex(YapFile yapfile, Transaction transaction, int i) {
      }
      
      internal override bool allowsQueries() {
         return false;
      }
      
      public override void appendEmbedded1(YapWriter yapwriter) {
      }
      
      internal override void cacheDirty(Collection4 collection4) {
      }
      
      public override bool canHold(Class var_class) {
         return i_handler.canHold(var_class);
      }
      
      internal override void deleteEmbedded1(YapWriter yapwriter, int i) {
         if (i_handler is YapArray) {
            YapArray yaparray1 = (YapArray)i_handler;
            if (yaparray1.i_isPrimitive) {
               yaparray1.deletePrimitiveEmbedded(yapwriter, this);
               yapwriter.getTransaction().freeOnCommit(yapwriter.getID(), yapwriter.getAddress(), yapwriter.getLength());
               yapwriter.getTransaction().setPointer(yapwriter.getID(), 0, 0);
               return;
            }
         }
         if (i_handler is YapClassAny) yapwriter.incrementOffset(i_handler.linkLength()); else i_handler.deleteEmbedded(yapwriter);
         free(yapwriter, i);
      }
      
      internal override void deleteMembers(YapWriter yapwriter, int i) {
         if (i == 3) new YapArray(this, true).deletePrimitiveEmbedded(yapwriter, this); else if (i == 4) new YapArrayN(this, true).deletePrimitiveEmbedded(yapwriter, this);
      }
      
      internal void free(Transaction transaction, int i, int i_0_, int i_1_) {
         transaction.freeOnCommit(i_0_, i_0_, i_1_);
         transaction.freePointer(i);
      }
      
      internal void free(YapWriter yapwriter, int i) {
         Transaction transaction1 = yapwriter.getTransaction();
         transaction1.freeOnCommit(yapwriter.getAddress(), yapwriter.getAddress(), yapwriter.getLength());
         transaction1.freePointer(i);
      }
      
      internal override ClassIndex getIndex() {
         return null;
      }
      
      public override Class getJavaClass() {
         return i_handler.getJavaClass();
      }
      
      internal override bool hasIndex() {
         return false;
      }
      
      internal override Object instantiate(YapObject yapobject, Object obj, YapWriter yapwriter, bool xbool) {
         if (obj == null) {
            try {
               {
                  obj = i_handler.read(yapwriter);
               }
            }  catch (CorruptionException corruptionexception) {
               {
                  return null;
               }
            }
            yapobject.setObjectWeak(yapwriter.getStream(), obj);
         }
         yapobject.setStateClean();
         return obj;
      }
      
      internal override Object instantiateTransient(YapObject yapobject, Object obj, YapWriter yapwriter) {
         try {
            {
               return i_handler.read(yapwriter);
            }
         }  catch (CorruptionException corruptionexception) {
            {
               return null;
            }
         }
      }
      
      internal override void instantiateFields(YapObject yapobject, Object obj, YapWriter yapwriter) {
         Object obj_2_1 = null;
         try {
            {
               obj_2_1 = i_handler.read(yapwriter);
            }
         }  catch (CorruptionException corruptionexception) {
            {
               obj_2_1 = null;
            }
         }
         if (obj_2_1 != null) i_handler.copyValue(obj_2_1, obj);
      }
      
      public override bool isArray() {
         return i_id == 12 || i_id == 13;
      }
      
      internal override bool isStrongTyped() {
         return false;
      }
      
      internal override void marshall(YapObject yapobject, Object obj, YapWriter yapwriter, bool xbool) {
         i_handler.writeNew(obj, yapwriter);
      }
      
      internal override void marshallNew(YapObject yapobject, YapWriter yapwriter, Object obj) {
         i_handler.writeNew(obj, yapwriter);
      }
      
      internal override int memberLength() {
         return i_handler.linkLength() + 0 + 4;
      }
      
      public override YapComparable prepareComparison(Object obj) {
         i_handler.prepareComparison(obj);
         return i_handler;
      }
      
      public override YapDataType readArrayWrapper(Transaction transaction, YapReader[] yapreaders) {
         if (isArray()) return i_handler;
         return null;
      }
      
      internal override void removeFromIndex(Transaction transaction, int i) {
      }
      
      public override bool supportsIndex() {
         return false;
      }
      
      internal override bool writeObjectBegin() {
         return false;
      }
   }
}