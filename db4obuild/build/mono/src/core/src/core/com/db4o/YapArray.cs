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

   internal class YapArray : YapIndependantType {
      internal YapDataType i_handler;
      internal bool i_isPrimitive;
      
      internal YapArray(YapDataType yapdatatype, bool xbool) : base() {
         i_handler = yapdatatype;
         i_isPrimitive = xbool;
      }
      
      internal virtual Object[] allElements(Object obj) {
         Object[] objs1 = new Object[Array4.reflector().getLength(obj)];
         for (int i1 = objs1.Length - 1; i1 >= 0; i1--) objs1[i1] = Array4.reflector().get(obj, i1);
         return objs1;
      }
      
      public override void appendEmbedded3(YapWriter yapwriter) {
         yapwriter.incrementOffset(this.linkLength());
      }
      
      public override bool canHold(Class var_class) {
         return i_handler.canHold(var_class);
      }
      
      public override void cascadeActivation(Transaction transaction, Object obj, int i, bool xbool) {
         if (i_handler is YapClass) {
            i--;
            YapStream yapstream1 = transaction.i_stream;
            Object[] objs1 = allElements(obj);
            if (xbool) {
               for (int i_0_1 = objs1.Length - 1; i_0_1 >= 0; i_0_1--) yapstream1.stillToActivate(objs1[i_0_1], i);
            } else {
               for (int i_1_1 = objs1.Length - 1; i_1_1 >= 0; i_1_1--) yapstream1.stillToDeactivate(objs1[i_1_1], i, false);
            }
         }
      }
      
      internal TreeInt collectIDs(TreeInt treeint, YapWriter yapwriter) {
         Transaction transaction1 = yapwriter.getTransaction();
         YapReader yapreader1 = yapwriter.readEmbeddedObject(transaction1);
         if (yapreader1 != null) {
            int i1 = elementCount(transaction1, yapreader1);
            for (int i_2_1 = 0; i_2_1 < i1; i_2_1++) treeint = (TreeInt)Tree.add(treeint, new TreeInt(yapreader1.readInt()));
         }
         return treeint;
      }
      
      public override void deleteEmbedded(YapWriter yapwriter) {
         int i1 = yapwriter.readInt();
         int i_3_1 = yapwriter.readInt();
         if (i1 > 0) {
            Transaction transaction1 = yapwriter.getTransaction();
            if (yapwriter.cascadeDeletes() > 0 && i_handler is YapClass) {
               YapWriter yapwriter_4_1 = yapwriter.getStream().readObjectWriterByAddress(transaction1, i1, i_3_1);
               if (yapwriter_4_1 != null) {
                  yapwriter_4_1.setCascadeDeletes(yapwriter.cascadeDeletes());
                  for (int i_5_1 = elementCount(transaction1, yapwriter_4_1); i_5_1 > 0; i_5_1--) i_handler.deleteEmbedded(yapwriter_4_1);
               }
            }
            transaction1.freeOnCommit(i1, i1, i_3_1);
         }
      }
      
      public void deletePrimitiveEmbedded(YapWriter yapwriter, YapClassPrimitive yapclassprimitive) {
         int i1 = yapwriter.readInt();
         int i_6_1 = yapwriter.readInt();
         if (i1 > 0) {
            Transaction transaction1 = yapwriter.getTransaction();
            YapWriter yapwriter_7_1 = yapwriter.getStream().readObjectWriterByAddress(transaction1, i1, i_6_1);
            if (yapwriter_7_1 != null) {
               for (int i_8_1 = elementCount(transaction1, yapwriter_7_1); i_8_1 > 0; i_8_1--) {
                  int i_9_1 = yapwriter_7_1.readInt();
                  int[] xis1 = new int[2];
                  transaction1.getSlotInformation(i_9_1, xis1);
                  yapclassprimitive.free(transaction1, i_9_1, xis1[0], xis1[1]);
               }
            }
            transaction1.freeOnCommit(i1, i1, i_6_1);
         }
      }
      
      internal virtual int elementCount(Transaction transaction, YapReader yapreader) {
         int i1 = yapreader.readInt();
         if (i1 >= 0) return i1;
         return yapreader.readInt();
      }
      
      public override bool Equals(YapDataType yapdatatype) {
         if (yapdatatype is YapArray && ((YapArray)yapdatatype).identifier() == identifier()) return i_handler.Equals(((YapArray)yapdatatype).i_handler);
         return false;
      }
      
      public static Class getComponentType(Class var_class) {
         if (var_class.isArray()) return getComponentType(var_class.getComponentType());
         return var_class;
      }
      
      public override int getID() {
         return i_handler.getID();
      }
      
      public override Class getJavaClass() {
         return i_handler.getJavaClass();
      }
      
      public override int getType() {
         return i_handler.getType();
      }
      
      public override YapClass getYapClass(YapStream yapstream) {
         return i_handler.getYapClass(yapstream);
      }
      
      internal virtual byte identifier() {
         return (byte)78;
      }
      
      public override Object indexObject(Transaction transaction, Object obj) {
         throw YapConst.virtualException();
      }
      
      internal virtual int objectLength(Object obj) {
         return 8 + Array4.reflector().getLength(obj) * i_handler.linkLength();
      }
      
      public override void prepareLastIoComparison(Transaction transaction, Object obj) {
         prepareComparison(obj);
      }
      
      public override Object read(YapWriter yapwriter) {
         YapWriter yapwriter_10_1 = yapwriter.readEmbeddedObject();
         i_lastIo = yapwriter_10_1;
         if (yapwriter_10_1 == null) return null;
         yapwriter_10_1.setUpdateDepth(yapwriter.getUpdateDepth());
         yapwriter_10_1.setInstantiationDepth(yapwriter.getInstantiationDepth());
         Object obj1 = read1(yapwriter_10_1);
         return obj1;
      }
      
      public override Object readIndexEntry(YapReader yapreader) {
         throw YapConst.virtualException();
      }
      
      public override Object readQuery(Transaction transaction, YapReader yapreader, bool xbool) {
         YapReader yapreader_11_1 = yapreader.readEmbeddedObject(transaction);
         if (yapreader_11_1 == null) return null;
         Object obj1 = read1Query(transaction, yapreader_11_1);
         return obj1;
      }
      
      internal virtual Object read1Query(Transaction transaction, YapReader yapreader) {
         int[] xis1 = new int[1];
         Object obj1 = readCreate(transaction, yapreader, xis1);
         if (obj1 != null) {
            for (int i1 = 0; i1 < xis1[0]; i1++) Array4.reflector().set(obj1, i1, i_handler.readQuery(transaction, yapreader, true));
         }
         return obj1;
      }
      
      internal virtual Object read1(YapWriter yapwriter) {
         int[] xis1 = new int[1];
         Object obj1 = readCreate(yapwriter.getTransaction(), yapwriter, xis1);
         if (obj1 != null) {
            for (int i1 = 0; i1 < xis1[0]; i1++) Array4.reflector().set(obj1, i1, i_handler.read(yapwriter));
         }
         return obj1;
      }
      
      private Object readCreate(Transaction transaction, YapReader yapreader, int[] xis) {
         Class[] var_classes1 = new Class[1];
         xis[0] = readElementsAndClass(transaction, yapreader, var_classes1);
         if (i_isPrimitive) return Array4.reflector().newInstance(i_handler.getPrimitiveJavaClass(), xis[0]);
         if (var_classes1[0] != null) return Array4.reflector().newInstance(var_classes1[0], xis[0]);
         return null;
      }
      
      public override YapDataType readArrayWrapper(Transaction transaction, YapReader[] yapreaders) {
         return this;
      }
      
      public override void readCandidates(YapReader yapreader, QCandidates qcandidates) {
         YapReader yapreader_12_1 = yapreader.readEmbeddedObject(qcandidates.i_trans);
         if (yapreader_12_1 != null) {
            int i1 = elementCount(qcandidates.i_trans, yapreader_12_1);
            for (int i_13_1 = 0; i_13_1 < i1; i_13_1++) qcandidates.addByIdentity(new QCandidate(qcandidates, yapreader_12_1.readInt(), true));
         }
      }
      
      internal int readElementsAndClass(Transaction transaction, YapReader yapreader, Class[] var_classes) {
         int i1 = yapreader.readInt();
         var_classes[0] = i_handler.getJavaClass();
         if (i1 < 0) {
            if (i1 != -99999) {
               bool xbool1 = false;
               YapClass yapclass1 = transaction.i_stream.getYapClass(-i1);
               if (yapclass1 != null) {
                  if (xbool1) var_classes[0] = yapclass1.getPrimitiveJavaClass(); else var_classes[0] = yapclass1.getJavaClass();
               }
            }
            i1 = yapreader.readInt();
         }
         if (Debug.exceedsMaximumArrayEntries(i1, i_isPrimitive)) return 0;
         return i1;
      }
      
      static internal Object[] toArray(Object obj) {
         if (obj != null && j4o.lang.Class.getClassForObject(obj).isArray()) {
            YapArray yaparray1;
            if (Array4.isNDimensional(j4o.lang.Class.getClassForObject(obj))) yaparray1 = new YapArrayN(null, false); else yaparray1 = new YapArray(null, false);
            return yaparray1.allElements(obj);
         }
         return new Object[0];
      }
      
      internal void writeClass(Object obj, YapWriter yapwriter) {
         int i1 = 0;
         Class var_class1 = getComponentType(j4o.lang.Class.getClassForObject(obj));
         bool xbool1 = false;
         YapStream yapstream1 = yapwriter.getStream();
         if (xbool1) {
            YapJavaClass yapjavaclass1 = (YapJavaClass)yapstream1.i_handlers.handlerForClass(yapstream1, var_class1);
            var_class1 = yapjavaclass1.getJavaClass();
         }
         YapClass yapclass1 = yapstream1.getYapClass(var_class1, true);
         if (yapclass1 != null) i1 = yapclass1.getID();
         if (i1 == 0) {
            i1 = 99999;
            if (xbool1) i1 += -2000000000;
         }
         yapwriter.writeInt(-i1);
      }
      
      public override void writeIndexEntry(YapWriter yapwriter, Object obj) {
         throw YapConst.virtualException();
      }
      
      public override void writeNew(Object obj, YapWriter yapwriter) {
         if (obj == null) yapwriter.writeEmbeddedNull(); else {
            int i1 = objectLength(obj);
            YapWriter yapwriter_14_1 = new YapWriter(yapwriter.getTransaction(), i1);
            yapwriter_14_1.setUpdateDepth(yapwriter.getUpdateDepth());
            writeNew1(obj, yapwriter_14_1);
            yapwriter_14_1.setID(yapwriter._offset);
            i_lastIo = yapwriter_14_1;
            yapwriter.getStream().writeEmbedded(yapwriter, yapwriter_14_1);
            yapwriter.incrementOffset(4);
            yapwriter.writeInt(i1);
         }
      }
      
      internal virtual void writeNew1(Object obj, YapWriter yapwriter) {
         int i1 = Array4.reflector().getLength(obj);
         writeClass(obj, yapwriter);
         yapwriter.writeInt(i1);
         for (int i_15_1 = 0; i_15_1 < i1; i_15_1++) i_handler.writeNew(Array4.reflector().get(obj, i_15_1), yapwriter);
      }
      
      public override YapComparable prepareComparison(Object obj) {
         i_handler.prepareComparison(obj);
         return this;
      }
      
      public override int compareTo(Object obj) {
         return -1;
      }
      
      public override bool isEqual(Object obj) {
         Object[] objs1 = allElements(obj);
         for (int i1 = 0; i1 < objs1.Length; i1++) {
            if (i_handler.isEqual(objs1[i1])) return true;
         }
         return false;
      }
      
      public override bool isGreater(Object obj) {
         Object[] objs1 = allElements(obj);
         for (int i1 = 0; i1 < objs1.Length; i1++) {
            if (i_handler.isGreater(objs1[i1])) return true;
         }
         return false;
      }
      
      public override bool isSmaller(Object obj) {
         Object[] objs1 = allElements(obj);
         for (int i1 = 0; i1 < objs1.Length; i1++) {
            if (i_handler.isSmaller(objs1[i1])) return true;
         }
         return false;
      }
      
      public override bool supportsIndex() {
         return false;
      }
   }
}