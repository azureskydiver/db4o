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

   internal class YapClassCollection : YapMeta, UseSystemTransaction {
      private YapClass i_addingMembersTo;
      private Collection4 i_classes;
      private Hashtable4 i_creating;
      private YapStream i_stream;
      private Transaction i_systemTrans;
      private Hashtable4 i_yapClassByBytes;
      private Hashtable4 i_yapClassByClass;
      private Hashtable4 i_yapClassByID;
      private int i_yapClassCreationDepth;
      private Queue4 i_initYapClassesOnUp;
      
      internal YapClassCollection(Transaction transaction) : base() {
         i_systemTrans = transaction;
         i_stream = transaction.i_stream;
         i_initYapClassesOnUp = new Queue4();
      }
      
      internal void addYapClass(YapClass yapclass) {
         i_stream.setDirty(this);
         i_classes.add(yapclass);
         if (yapclass.stateUnread()) i_yapClassByBytes.put(yapclass.i_nameBytes, yapclass); else i_yapClassByClass.put(yapclass.getJavaClass(), yapclass);
         if (yapclass.getID() == 0) yapclass.write(i_stream, i_systemTrans);
         i_yapClassByID.put(yapclass.getID(), yapclass);
      }
      
      internal void checkChanges() {
         Iterator4 iterator41 = i_classes.iterator();
         while (iterator41.hasNext()) ((YapClass)iterator41.next()).checkChanges();
      }
      
      private void classAddMembers(YapClass yapclass) {
         if (i_addingMembersTo != null) i_addingMembersTo.addMembersAddDependancy(yapclass); else {
            YapClass yapclass_0_1 = yapclass.getAncestor();
            if (yapclass_0_1 != null) classAddMembers(yapclass_0_1);
            i_addingMembersTo = yapclass;
            yapclass.addMembers(i_stream);
            yapclass.storeStaticFieldValues(i_systemTrans, true);
            i_addingMembersTo = null;
            YapClass[] yapclasses1 = yapclass.getMembersDependancies();
            for (int i1 = 0; i1 < yapclasses1.Length; i1++) classAddMembers(yapclasses1[i1]);
            yapclass.write(i_stream, i_stream.getSystemTransaction());
            for (int i1 = 0; i1 < yapclasses1.Length; i1++) {
               yapclasses1[i1].setStateDirty();
               yapclasses1[i1].write(i_stream, i_stream.getSystemTransaction());
            }
         }
      }
      
      internal bool createYapClass(YapClass yapclass, Class var_class) {
         i_yapClassCreationDepth++;
         Class var_class_1_1 = var_class.getSuperclass();
         YapClass yapclass_2_1 = null;
         if (var_class_1_1 != null && var_class_1_1 != YapConst.CLASS_OBJECT) yapclass_2_1 = getYapClass(var_class_1_1, true);
         bool xbool1 = i_stream.createYapClass(yapclass, var_class, yapclass_2_1);
         i_yapClassCreationDepth--;
         initYapClassesOnUp();
         return xbool1;
      }
      
      internal bool fieldExists(String xstring) {
         YapClassCollectionIterator yapclasscollectioniterator1 = iterator();
         while (yapclasscollectioniterator1.hasNext()) {
            if (yapclasscollectioniterator1.nextClass().getYapField(xstring) != null) return true;
         }
         return false;
      }
      
      internal Collection4 forInterface(Class var_class) {
         Collection4 collection41 = new Collection4();
         YapClassCollectionIterator yapclasscollectioniterator1 = iterator();
         while (yapclasscollectioniterator1.hasNext()) {
            YapClass yapclass1 = yapclasscollectioniterator1.nextClass();
            if (var_class.isAssignableFrom(yapclass1.getJavaClass())) {
               bool xbool1 = false;
               Iterator4 iterator41 = collection41.iterator();
               while (iterator41.hasNext()) {
                  YapClass yapclass_3_1 = (YapClass)iterator41.next();
                  YapClass yapclass_4_1 = yapclass1.getHigherHierarchy(yapclass_3_1);
                  if (yapclass_4_1 != null) {
                     xbool1 = true;
                     if (yapclass_4_1 == yapclass1) {
                        collection41.remove(yapclass_3_1);
                        collection41.add(yapclass1);
                     }
                     break;
                  }
               }
               if (!xbool1) collection41.add(yapclass1);
            }
         }
         return collection41;
      }
      
      internal override byte getIdentifier() {
         return (byte)65;
      }
      
      internal YapClass getYapClass(Class var_class, bool xbool) {
         YapClass yapclass1 = (YapClass)i_yapClassByClass.get(var_class);
         if (yapclass1 == null) {
            byte[] xis1 = i_stream.i_stringIo.write(var_class.getName());
            yapclass1 = (YapClass)i_yapClassByBytes.remove(xis1);
            readYapClass(yapclass1, var_class);
         }
         if (yapclass1 != null || !xbool) return yapclass1;
         yapclass1 = (YapClass)i_creating.get(var_class);
         if (yapclass1 != null) return yapclass1;
         yapclass1 = new YapClass();
         i_creating.put(var_class, yapclass1);
         if (!createYapClass(yapclass1, var_class)) {
            i_creating.remove(var_class);
            return null;
         }
         bool bool_5_1 = false;
         if (i_yapClassByClass.get(var_class) == null) {
            addYapClass(yapclass1);
            bool_5_1 = true;
         }
         int i1 = yapclass1.getID();
         if (i1 == 0) {
            yapclass1.write(i_stream, i_stream.getSystemTransaction());
            i1 = yapclass1.getID();
         }
         if (i_yapClassByID.get(i1) == null) {
            i_yapClassByID.put(i1, yapclass1);
            bool_5_1 = true;
         }
         if (bool_5_1 || yapclass1.i_fields == null) classAddMembers(yapclass1);
         i_creating.remove(var_class);
         return yapclass1;
      }
      
      internal YapClass getYapClass(int i) {
         return readYapClass((YapClass)i_yapClassByID.get(i), null);
      }
      
      internal YapClass getYapClass(String xstring) {
         byte[] xis1 = i_stream.i_stringIo.write(xstring);
         YapClass yapclass1 = (YapClass)i_yapClassByBytes.remove(xis1);
         readYapClass(yapclass1, null);
         if (yapclass1 == null) {
            YapClassCollectionIterator yapclasscollectioniterator1 = iterator();
            while (yapclasscollectioniterator1.hasNext()) {
               yapclass1 = yapclasscollectioniterator1.nextClass();
               if (xstring.Equals(yapclass1.getName())) return yapclass1;
            }
            return null;
         }
         return yapclass1;
      }
      
      internal void initOnUp(Transaction transaction) {
         i_yapClassCreationDepth++;
         transaction.i_stream.showInternalClasses(true);
         Iterator4 iterator41 = i_classes.iterator();
         while (iterator41.hasNext()) ((YapClass)iterator41.next()).initOnUp(transaction);
         transaction.i_stream.showInternalClasses(false);
         i_yapClassCreationDepth--;
         initYapClassesOnUp();
      }
      
      internal void initTables(int i) {
         i_classes = new Collection4();
         i_yapClassByBytes = new Hashtable4(i);
         if (i < 16) i = 16;
         i_yapClassByClass = new Hashtable4(i);
         i_yapClassByID = new Hashtable4(i);
         i_creating = new Hashtable4(1);
      }
      
      private void initYapClassesOnUp() {
         if (i_yapClassCreationDepth == 0) {
            for (YapClass yapclass1 = (YapClass)i_initYapClassesOnUp.next(); yapclass1 != null; yapclass1 = (YapClass)i_initYapClassesOnUp.next()) yapclass1.initOnUp(i_systemTrans);
         }
      }
      
      internal YapClassCollectionIterator iterator() {
         return new YapClassCollectionIterator(this, i_classes.i_first);
      }
      
      internal override int ownLength() {
         return 4 + i_classes.size() * 4;
      }
      
      internal void purge() {
         Iterator4 iterator41 = i_classes.iterator();
         while (iterator41.hasNext()) ((YapClass)iterator41.next()).purge();
      }
      
      internal override void readThis(Transaction transaction, YapReader yapreader) {
         int i1 = yapreader.readInt();
         initTables(i1);
         for (int i_6_1 = i1; i_6_1 > 0; i_6_1--) {
            YapClass yapclass1 = new YapClass();
            int i_7_1 = yapreader.readInt();
            yapclass1.setID(i_stream, i_7_1);
            i_classes.add(yapclass1);
            i_yapClassByID.put(i_7_1, yapclass1);
            i_yapClassByBytes.put(yapclass1.readName(transaction), yapclass1);
         }
      }
      
      internal YapClass readYapClass(YapClass yapclass, Class var_class) {
         i_yapClassCreationDepth++;
         if (yapclass != null && yapclass.stateUnread()) {
            yapclass.createConfigAndConstructor(i_yapClassByBytes, i_stream, var_class);
            Class var_class_8_1 = yapclass.getJavaClass();
            if (var_class_8_1 != null) {
               i_yapClassByClass.put(var_class_8_1, yapclass);
               yapclass.readThis();
               yapclass.checkChanges();
               i_initYapClassesOnUp.add(yapclass);
            }
         }
         i_yapClassCreationDepth--;
         initYapClassesOnUp();
         return yapclass;
      }
      
      internal void refreshClasses() {
         YapClassCollection yapclasscollection_9_1 = new YapClassCollection(i_systemTrans);
         yapclasscollection_9_1.i_id = i_id;
         yapclasscollection_9_1.read(i_stream.getSystemTransaction());
         Iterator4 iterator41 = yapclasscollection_9_1.i_classes.iterator();
         while (iterator41.hasNext()) {
            YapClass yapclass1 = (YapClass)iterator41.next();
            if (i_yapClassByID.get(yapclass1.getID()) == null) {
               i_classes.add(yapclass1);
               i_yapClassByID.put(yapclass1.getID(), yapclass1);
               if (yapclass1.stateUnread()) i_yapClassByBytes.put(yapclass1.readName(i_systemTrans), yapclass1); else i_yapClassByClass.put(yapclass1.getJavaClass(), yapclass1);
            }
         }
         iterator41 = i_classes.iterator();
         while (iterator41.hasNext()) {
            YapClass yapclass1 = (YapClass)iterator41.next();
            yapclass1.refresh();
         }
      }
      
      internal void reReadYapClass(YapClass yapclass) {
         if (yapclass != null) {
            reReadYapClass(yapclass.i_ancestor);
            yapclass.readName(i_systemTrans);
            yapclass.forceRead();
            yapclass.setStateClean();
            yapclass.bitFalse(6);
            yapclass.bitFalse(8);
            yapclass.bitFalse(4);
            yapclass.bitFalse(7);
            yapclass.checkChanges();
         }
      }
      
      public StoredClass[] storedClasses() {
         Collection4 collection41 = new Collection4();
         Iterator4 iterator41 = i_classes.iterator();
         while (iterator41.hasNext()) {
            YapClass yapclass1 = (YapClass)iterator41.next();
            readYapClass(yapclass1, null);
            if (yapclass1.getJavaClass() == null) yapclass1.forceRead();
            collection41.add(yapclass1);
         }
         StoredClass[] storedclasses1 = new StoredClass[collection41.size()];
         collection41.toArray(storedclasses1);
         return storedclasses1;
      }
      
      internal override void writeThis(YapWriter yapwriter) {
         yapwriter.writeInt(i_classes.size());
         Iterator4 iterator41 = i_classes.iterator();
         while (iterator41.hasNext()) YapMeta.writeIDOf((YapClass)iterator41.next(), yapwriter);
      }
      
      internal void yapClassRequestsInitOnUp(YapClass yapclass) {
         if (i_yapClassCreationDepth == 0) yapclass.initOnUp(i_systemTrans); else i_initYapClassesOnUp.add(yapclass);
      }
      
      internal void yapFields(String xstring, Visitor4 visitor4) {
         YapClassCollectionIterator yapclasscollectioniterator1 = iterator();
         while (yapclasscollectioniterator1.hasNext()) {
            YapClass yapclass1 = yapclasscollectioniterator1.nextClass();
            yapclass1.forEachYapField(new YapClassCollection__1(this, xstring, visitor4, yapclass1));
         }
      }
   }
}