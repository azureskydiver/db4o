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
using com.db4o.ext;
using com.db4o.reflect;
namespace com.db4o {

   internal class YapField : StoredField {
      private YapClass i_yapClass;
      private int i_arrayPosition;
      protected String i_name;
      private bool i_isArray;
      private bool i_isNArray;
      private bool i_isPrimitive;
      private IField i_javaField;
      protected YapDataType i_handler;
      private int i_handlerID;
      private int i_state;
      private static int NOT_LOADED = 0;
      private static int UNAVAILABLE = -1;
      private static int AVAILABLE = 1;
      protected IxField i_index;
      private Config4Field i_config;
      private Db4oTypeImpl i_db4oType;
      static internal YapField[] EMPTY_ARRAY = new YapField[0];
      
      internal YapField(YapClass yapclass) : base() {
         i_yapClass = yapclass;
      }
      
      internal YapField(YapClass yapclass, ObjectTranslator objecttranslator) : base() {
         i_yapClass = yapclass;
         init(yapclass, j4o.lang.Class.getClassForObject(objecttranslator).getName(), 0);
         i_state = 1;
         i_handler = yapclass.getStream().i_handlers.handlerForClass(yapclass.getStream(), objecttranslator.storedClass());
      }
      
      internal YapField(YapClass yapclass, IField ifield, YapDataType yapdatatype) : base() {
         init(yapclass, ifield.getName(), 0);
         i_javaField = ifield;
         i_javaField.setAccessible();
         i_handler = yapdatatype;
         configure(ifield.getType());
         checkDb4oType();
         i_state = 1;
      }
      
      internal virtual void addFieldIndex(YapWriter yapwriter, bool xbool) {
         if (i_index == null) yapwriter.incrementOffset(linkLength()); else {
            try {
               {
                  addIndexEntry(i_handler.readIndexObject(yapwriter), yapwriter);
               }
            }  catch (CorruptionException corruptionexception) {
               {
               }
            }
         }
      }
      
      protected void addIndexEntry(Object obj, YapWriter yapwriter) {
         addIndexEntry(yapwriter.getTransaction(), yapwriter.getID(), obj);
      }
      
      internal void addIndexEntry(Transaction transaction, int i, Object obj) {
         i_handler.prepareLastIoComparison(transaction, obj);
         IxFieldTransaction ixfieldtransaction1 = getIndex(transaction).dirtyFieldTransaction(transaction);
         ixfieldtransaction1.add(new IxAdd(ixfieldtransaction1, i, i_handler.indexEntry(obj)));
      }
      
      public bool alive() {
         if (i_state == 1) return true;
         if (i_state == 0) {
            if (i_handler == null) {
               i_handler = loadJavaField1();
               if (i_handler != null) {
                  if (i_handlerID == 0) i_handlerID = i_handler.getID(); else if (i_handler.getID() != i_handlerID) i_handler = null;
               }
            }
            loadJavaField();
            if (i_handler != null) {
               i_handler = wrapHandlerToArrays(i_handler);
               i_state = 1;
               checkDb4oType();
            } else i_state = -1;
         }
         return i_state == 1;
      }
      
      public void appendEmbedded2(YapWriter yapwriter) {
         if (alive()) i_handler.appendEmbedded3(yapwriter); else yapwriter.incrementOffset(4);
      }
      
      internal bool canHold(Class var_class) {
         if (var_class == null) return !i_isPrimitive;
         return i_handler.canHold(var_class);
      }
      
      public bool canLoadByIndex(QConObject qconobject, QE qe) {
         if (i_handler is YapClass) {
            if (qe is QEIdentity) {
               YapClass yapclass1 = (YapClass)i_handler;
               yapclass1.i_lastID = qconobject.getObjectID();
               return true;
            }
            return false;
         }
         return true;
      }
      
      internal void cascadeActivation(Transaction transaction, Object obj, int i, bool xbool) {
         if (alive()) {
            try {
               {
                  Object obj_0_1 = getOrCreate(transaction, obj);
                  if (obj_0_1 != null && i_handler != null) i_handler.cascadeActivation(transaction, obj_0_1, i, xbool);
               }
            }  catch (Exception exception) {
               {
               }
            }
         }
      }
      
      private void checkDb4oType() {
         if (i_javaField != null && YapConst.CLASS_DB4OTYPE.isAssignableFrom(i_javaField.getType())) i_db4oType = YapHandlers.getDb4oType(i_javaField.getType());
      }
      
      internal void collectConstraints(Transaction transaction, QConObject qconobject, Object obj, Visitor4 visitor4) {
         Object obj_1_1 = getOn(transaction, obj);
         if (obj_1_1 != null) {
            Collection4 collection41 = Platform.flattenCollection(obj_1_1);
            Iterator4 iterator41 = collection41.iterator();
            while (iterator41.hasNext()) {
               obj_1_1 = iterator41.next();
               if (obj_1_1 != null) {
                  if (i_isPrimitive && i_handler is YapJavaClass && obj_1_1.Equals(((YapJavaClass)i_handler).primitiveNull()) || Platform.ignoreAsConstraint(obj_1_1)) break;
                  if (!qconobject.hasObjectInParentPath(obj_1_1)) visitor4.visit(new QConObject(transaction, qconobject, qField(transaction), obj_1_1));
               }
            }
         }
      }
      
      internal TreeInt collectIDs(TreeInt treeint, YapWriter yapwriter) {
         if (alive()) {
            if (i_handler is YapClass) treeint = (TreeInt)Tree.add(treeint, new TreeInt(yapwriter.readInt())); else if (i_handler is YapArray) treeint = ((YapArray)i_handler).collectIDs(treeint, yapwriter);
         }
         return treeint;
      }
      
      internal void configure(Class var_class) {
         i_isPrimitive = var_class.isPrimitive();
         i_isArray = var_class.isArray();
         if (i_isArray) {
            i_isNArray = Array4.isNDimensional(var_class);
            var_class = Array4.getComponentType(var_class);
            if (i_isNArray) i_handler = new YapArrayN(i_handler, i_isPrimitive); else i_handler = new YapArray(i_handler, i_isPrimitive);
         }
      }
      
      internal virtual void deactivate(Transaction transaction, Object obj, int i) {
         if (alive()) {
            try {
               {
                  bool xbool1 = YapConst.CLASS_ENUM != null && YapConst.CLASS_ENUM.isAssignableFrom(j4o.lang.Class.getClassForObject(obj));
                  if (i_isPrimitive && !i_isArray) {
                     if (!xbool1) i_javaField.set(obj, ((YapJavaClass)i_handler).primitiveNull());
                  } else {
                     if (i > 0) cascadeActivation(transaction, obj, i, false);
                     if (!xbool1) i_javaField.set(obj, null);
                  }
               }
            }  catch (Exception throwable) {
               {
               }
            }
         }
      }
      
      internal virtual void delete(YapWriter yapwriter) {
         if (alive()) {
            if (i_index != null) {
               int i1 = yapwriter._offset;
               Object obj1 = null;
               try {
                  {
                     obj1 = i_handler.read(yapwriter);
                  }
               }  catch (CorruptionException corruptionexception) {
                  {
                  }
               }
               i_handler.prepareComparison(obj1);
               IxFieldTransaction ixfieldtransaction1 = i_index.dirtyFieldTransaction(yapwriter.getTransaction());
               ixfieldtransaction1.add(new IxRemove(ixfieldtransaction1, yapwriter.getID(), i_handler.indexEntry(obj1)));
               yapwriter._offset = i1;
            }
            if (i_config != null && i_config.i_cascadeOnDelete == 1 || Platform.isSecondClass(i_handler.getJavaClass())) {
               int i1 = yapwriter.cascadeDeletes();
               yapwriter.setCascadeDeletes(1);
               i_handler.deleteEmbedded(yapwriter);
               yapwriter.setCascadeDeletes(i1);
            } else if (i_config != null && i_config.i_cascadeOnDelete == -1) {
               int i1 = yapwriter.cascadeDeletes();
               yapwriter.setCascadeDeletes(0);
               i_handler.deleteEmbedded(yapwriter);
               yapwriter.setCascadeDeletes(i1);
            } else i_handler.deleteEmbedded(yapwriter);
         }
      }
      
      public override bool Equals(Object obj) {
         if (obj is YapField) {
            YapField yapfield_2_1 = (YapField)obj;
            yapfield_2_1.alive();
            alive();
            return yapfield_2_1.i_isPrimitive == i_isPrimitive && yapfield_2_1.i_handler.Equals(i_handler) && yapfield_2_1.i_name.Equals(i_name);
         }
         return false;
      }
      
      public Object get(Object obj) {
         if (i_yapClass != null) {
            YapStream yapstream1 = i_yapClass.getStream();
            if (yapstream1 != null) {
               lock (yapstream1.i_lock) {
                  yapstream1.checkClosed();
                  YapObject yapobject1 = yapstream1.getYapObject(obj);
                  if (yapobject1 != null) {
                     int i1 = yapobject1.getID();
                     if (i1 > 0) {
                        YapWriter yapwriter1 = yapstream1.readWriterByID(yapstream1.getTransaction(), i1);
                        if (yapwriter1 != null && i_yapClass.findOffset(yapwriter1, this)) {
                           try {
                              {
                                 return read(yapwriter1);
                              }
                           }  catch (CorruptionException corruptionexception) {
                              {
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
         return null;
      }
      
      public String getName() {
         return i_name;
      }
      
      internal YapClass getFieldYapClass(YapStream yapstream) {
         return i_handler.getYapClass(yapstream);
      }
      
      internal virtual IxField getIndex(Transaction transaction) {
         return i_index;
      }
      
      internal Tree getIndexRoot(Transaction transaction) {
         return getIndex(transaction).getFieldTransaction(transaction).getRoot();
      }
      
      internal YapDataType getHandler() {
         return i_handler;
      }
      
      internal virtual Object getOn(Transaction transaction, Object obj) {
         if (alive()) {
            try {
               {
                  return i_javaField.get(obj);
               }
            }  catch (Exception throwable) {
               {
               }
            }
         }
         return null;
      }
      
      internal virtual Object getOrCreate(Transaction transaction, Object obj) {
         if (alive()) {
            try {
               {
                  Object obj_3_1 = i_javaField.get(obj);
                  if (i_db4oType != null && obj_3_1 == null) {
                     obj_3_1 = i_db4oType.createDefault(transaction);
                     i_javaField.set(obj, obj_3_1);
                  }
                  return obj_3_1;
               }
            }  catch (Exception throwable) {
               {
               }
            }
         }
         return null;
      }
      
      internal YapClass getParentYapClass() {
         return i_yapClass;
      }
      
      public Object getStoredType() {
         return Platform.getTypeForClass(i_handler.getJavaClass());
      }
      
      internal bool hasIndex() {
         return i_index != null;
      }
      
      internal void incrementOffset(YapReader yapreader) {
         if (alive()) yapreader.incrementOffset(i_handler.linkLength());
      }
      
      internal void init(YapClass yapclass, String xstring, int i) {
         i_yapClass = yapclass;
         i_name = xstring;
         if (yapclass.i_config != null) i_config = yapclass.i_config.configField(xstring);
      }
      
      internal void initConfigOnUp(Transaction transaction) {
         if (i_config != null) i_config.initOnUp(transaction, this);
      }
      
      internal void initIndex(Transaction transaction, MetaIndex metaindex) {
         if (supportsIndex()) i_index = new IxField(transaction, this, metaindex);
      }
      
      internal virtual void instantiate(YapObject yapobject, Object obj, YapWriter yapwriter) {
         if (alive()) {
            Object obj_4_1 = null;
            try {
               {
                  obj_4_1 = read(yapwriter);
               }
            }  catch (Exception exception) {
               {
                  throw new CorruptionException();
               }
            }
            if (i_db4oType != null && obj_4_1 != null) ((Db4oTypeImpl)obj_4_1).setTrans(yapwriter.getTransaction());
            try {
               {
                  i_javaField.set(obj, obj_4_1);
               }
            }  catch (Exception throwable) {
               {
               }
            }
         }
      }
      
      public bool isArray() {
         return i_isArray;
      }
      
      public virtual int linkLength() {
         alive();
         if (i_handler == null) return 4;
         return i_handler.linkLength();
      }
      
      internal virtual void loadHandler(YapStream yapstream) {
         if (i_handlerID < 1) i_handler = null; else if (i_handlerID <= YapHandlers.maxTypeID()) i_handler = yapstream.i_handlers.getHandler(i_handlerID); else i_handler = yapstream.getYapClass(i_handlerID);
      }
      
      private void loadJavaField() {
         YapDataType yapdatatype1 = loadJavaField1();
         if (yapdatatype1 == null || !yapdatatype1.Equals(i_handler)) {
            i_javaField = null;
            i_state = -1;
         }
      }
      
      private YapDataType loadJavaField1() {
         try {
            {
               i_javaField = i_yapClass.getReflectorClass().getDeclaredField(i_name);
               if (i_javaField == null) return null;
               YapStream yapstream1 = i_yapClass.getStream();
               i_javaField.setAccessible();
               yapstream1.showInternalClasses(true);
               YapDataType yapdatatype1 = yapstream1.i_handlers.handlerForClass(yapstream1, i_javaField.getType());
               yapstream1.showInternalClasses(false);
               return yapdatatype1;
            }
         }  catch (Exception exception) {
            {
               return null;
            }
         }
      }
      
      internal virtual void marshall(YapObject yapobject, Object obj, YapWriter yapwriter, Config4Class config4class, bool xbool) {
         if (obj != null && (config4class != null && config4class.i_cascadeOnUpdate == 1 || i_config != null && i_config.i_cascadeOnUpdate == 1)) {
            int i1 = 1;
            if (Platform.isCollection(j4o.lang.Class.getClassForObject(obj))) i1 = Platform.collectionUpdateDepth(j4o.lang.Class.getClassForObject(obj));
            int i_5_1 = yapwriter.getUpdateDepth();
            if (i_5_1 < i1) yapwriter.setUpdateDepth(i1);
            i_handler.writeNew(obj, yapwriter);
            yapwriter.setUpdateDepth(i_5_1);
         } else i_handler.writeNew(obj, yapwriter);
         if (i_index != null) addIndexEntry(obj, yapwriter);
      }
      
      internal virtual int ownLength(YapStream yapstream) {
         return yapstream.i_stringIo.shortLength(i_name) + 1 + 4;
      }
      
      internal virtual YapComparable prepareComparison(Object obj) {
         if (alive()) {
            i_handler.prepareComparison(obj);
            return i_handler;
         }
         return null;
      }
      
      internal QField qField(Transaction transaction) {
         return new QField(transaction, i_name, this, i_yapClass.getID(), i_arrayPosition);
      }
      
      internal virtual Object read(YapWriter yapwriter) {
         if (!alive()) return null;
         return i_handler.read(yapwriter);
      }
      
      internal virtual Object readQuery(Transaction transaction, YapReader yapreader) {
         return i_handler.readQuery(transaction, yapreader, false);
      }
      
      internal YapField readThis(YapStream yapstream, YapReader yapreader) {
         try {
            {
               i_name = yapstream.i_handlers.i_stringHandler.readShort(yapreader);
            }
         }  catch (CorruptionException corruptionexception) {
            {
               i_handler = null;
               return this;
            }
         }
         if (i_name.IndexOf("v4o") == 0) {
            YapFieldVirtual[] yapfieldvirtuals1 = yapstream.i_handlers.i_virtualFields;
            for (int i1 = 0; i1 < yapfieldvirtuals1.Length; i1++) {
               if (i_name.Equals(yapfieldvirtuals1[i1].i_name)) return yapfieldvirtuals1[i1];
            }
         }
         init(i_yapClass, i_name, 0);
         i_handlerID = yapreader.readInt();
         YapBit yapbit1 = new YapBit(yapreader.readByte());
         i_isPrimitive = yapbit1.get();
         i_isArray = yapbit1.get();
         i_isNArray = yapbit1.get();
         return this;
      }
      
      public virtual void readVirtualAttribute(Transaction transaction, YapReader yapreader, YapObject yapobject) {
         yapreader.incrementOffset(i_handler.linkLength());
      }
      
      internal virtual void refresh() {
         YapDataType yapdatatype1 = loadJavaField1();
         if (yapdatatype1 != null) {
            yapdatatype1 = wrapHandlerToArrays(yapdatatype1);
            if (yapdatatype1.Equals(i_handler)) return;
         }
         i_javaField = null;
         i_state = -1;
      }
      
      public void rename(String xstring) {
         YapStream yapstream1 = i_yapClass.getStream();
         if (!yapstream1.isClient()) {
            i_name = xstring;
            i_yapClass.setStateDirty();
            i_yapClass.write(yapstream1, yapstream1.getSystemTransaction());
         } else Db4o.throwRuntimeException(58);
      }
      
      internal void setArrayPosition(int i) {
         i_arrayPosition = i;
      }
      
      internal void setName(String xstring) {
         i_name = xstring;
      }
      
      internal bool supportsIndex() {
         return alive() && i_handler.supportsIndex();
      }
      
      private YapDataType wrapHandlerToArrays(YapDataType yapdatatype) {
         if (i_isNArray) yapdatatype = new YapArrayN(yapdatatype, i_isPrimitive); else if (i_isArray) yapdatatype = new YapArray(yapdatatype, i_isPrimitive);
         return yapdatatype;
      }
      
      internal virtual void writeThis(YapWriter yapwriter, YapClass yapclass) {
         alive();
         yapwriter.writeShortString(i_name);
         if (i_handler is YapClass && i_handler.getID() == 0) yapwriter.getStream().needsUpdate(yapclass);
         int i1 = 0;
         try {
            {
               i1 = i_handler.getID();
            }
         }  catch (Exception exception) {
            {
            }
         }
         if (i1 == 0) i1 = i_handlerID;
         yapwriter.writeInt(i1);
         YapBit yapbit1 = new YapBit(0);
         yapbit1.set(i_handler is YapArrayN);
         yapbit1.set(i_handler is YapArray);
         yapbit1.set(i_isPrimitive);
         yapwriter.append(yapbit1.getByte());
      }
      
      public override String ToString() {
         StringBuffer stringbuffer1 = new StringBuffer();
         if (i_yapClass != null) {
            stringbuffer1.append(i_yapClass.getName());
            stringbuffer1.append(".");
            stringbuffer1.append(getName());
         }
         return stringbuffer1.ToString();
      }
      
      public String ToString(YapWriter yapwriter, YapObject yapobject, int i, int i_6_) {
         String xstring1 = "\n Field " + i_name;
         if (!alive()) yapwriter.incrementOffset(linkLength()); else {
            Object obj1 = read(yapwriter);
            if (obj1 == null) xstring1 += "\n [null]"; else xstring1 += "\n  " + obj1.ToString();
         }
         return xstring1;
      }
   }
}