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
using com.db4o.query;
using com.db4o.reflect;
namespace com.db4o {

   internal class YapClass : YapMeta, YapDataType, StoredClass, UseSystemTransaction {
      
      internal YapClass() : base() {
      }
      private Collection4 i_addMembersDependancies;
      internal YapClass i_ancestor;
      internal Config4Class i_config;
      internal YapConstructor i_constructor;
      internal YapField[] i_fields;
      private ClassIndex i_index;
      protected String i_name;
      protected int i_objectLength;
      private YapStream i_stream;
      internal byte[] i_nameBytes;
      private YapReader i_reader;
      private Db4oTypeImpl i_db4oType;
      internal int i_lastID;
      private Class i_compareTo;
      
      internal virtual void activateFields(Transaction transaction, Object obj, int i) {
         if (dispatchEvent(transaction.i_stream, obj, 6)) activateFields1(transaction, obj, i);
      }
      
      internal void activateFields1(Transaction transaction, Object obj, int i) {
         for (int i_0_1 = 0; i_0_1 < i_fields.Length; i_0_1++) i_fields[i_0_1].cascadeActivation(transaction, obj, i, true);
         if (i_ancestor != null) i_ancestor.activateFields1(transaction, obj, i);
      }
      
      internal void addFieldIndices(YapWriter yapwriter, bool xbool) {
         if (hasIndex() || hasVirtualAttributes()) {
            readObjectHeader(yapwriter, yapwriter.getID());
            addFieldIndices1(yapwriter, xbool);
         }
      }
      
      private void addFieldIndices1(YapWriter yapwriter, bool xbool) {
         int i1 = yapwriter.readInt();
         for (int i_1_1 = 0; i_1_1 < i1; i_1_1++) i_fields[i_1_1].addFieldIndex(yapwriter, xbool);
         if (i_ancestor != null) i_ancestor.addFieldIndices1(yapwriter, xbool);
      }
      
      internal void addMembers(YapStream yapstream) {
         i_stream = yapstream;
         this.bitTrue(6);
         if (i_config != null) {
            ObjectTranslator objecttranslator1 = i_config.getTranslator();
            if (objecttranslator1 != null) {
               if (i_fields == null || i_fields.Length <= 0 || !j4o.lang.Class.getClassForObject(objecttranslator1).getName().Equals(i_fields[0].getName())) i_stream.setDirty(this);
               i_fields = new YapField[1];
               i_fields[0] = new YapFieldTranslator(this, objecttranslator1);
               setStateOK();
               return;
            }
         }
         if (i_stream.detectSchemaChanges()) {
            bool xbool1 = isDirty();
            Collection4 collection41 = new Collection4();
            collection41.addAll(i_fields);
            if (generateVersionNumbers() && !hasVersionField()) {
               collection41.add(i_stream.i_handlers.i_indexes.i_fieldVersion);
               xbool1 = true;
            }
            if (generateUUIDs() && !hasUUIDField()) {
               collection41.add(i_stream.i_handlers.i_indexes.i_fieldUUID);
               xbool1 = true;
            }
            IField[] ifields1 = i_constructor.reflectorClass().getDeclaredFields();
            for (int i1 = 0; i1 < ifields1.Length; i1++) {
               if (storeField(ifields1[i1])) {
                  YapDataType yapdatatype1 = i_stream.i_handlers.handlerForClass(i_stream, ifields1[i1].getType());
                  if (yapdatatype1 != null) {
                     YapField yapfield1 = new YapField(this, ifields1[i1], yapdatatype1);
                     bool bool_2_1 = false;
                     Iterator4 iterator41 = collection41.iterator();
                     while (iterator41.hasNext()) {
                        if (((YapField)iterator41.next()).Equals(yapfield1)) {
                           bool_2_1 = true;
                           break;
                        }
                     }
                     if (!bool_2_1) {
                        xbool1 = true;
                        collection41.add(yapfield1);
                     }
                  }
               }
            }
            if (xbool1) {
               i_stream.setDirty(this);
               i_fields = new YapField[collection41.size()];
               collection41.toArray(i_fields);
               for (int i1 = 0; i1 < i_fields.Length; i1++) i_fields[i1].setArrayPosition(i1);
            } else if (collection41.size() == 0) i_fields = new YapField[0];
         } else if (i_fields == null) i_fields = new YapField[0];
         setStateOK();
      }
      
      internal void addMembersAddDependancy(YapClass yapclass_3_) {
         if (i_addMembersDependancies == null) i_addMembersDependancies = new Collection4();
         i_addMembersDependancies.add(yapclass_3_);
      }
      
      internal virtual void addToIndex(YapFile yapfile, Transaction transaction, int i) {
         if (yapfile.maintainsIndices()) addToIndex1(yapfile, transaction, i);
      }
      
      internal void addToIndex1(YapFile yapfile, Transaction transaction, int i) {
         if (i_ancestor != null) i_ancestor.addToIndex1(yapfile, transaction, i);
         if (hasIndex()) transaction.addToClassIndex(this.getID(), i);
      }
      
      internal virtual bool allowsQueries() {
         return hasIndex();
      }
      
      public virtual void appendEmbedded1(YapWriter yapwriter) {
         int i1 = readFieldLength(yapwriter);
         for (int i_4_1 = 0; i_4_1 < i1; i_4_1++) i_fields[i_4_1].appendEmbedded2(yapwriter);
         if (i_ancestor != null) i_ancestor.appendEmbedded1(yapwriter);
      }
      
      public void appendEmbedded3(YapWriter yapwriter) {
         yapwriter.incrementOffset(this.linkLength());
      }
      
      public virtual bool canHold(Class var_class) {
         if (var_class == null) return true;
         if (i_constructor != null) {
            if (Platform.isCollection(i_constructor.javaClass())) return true;
            return i_constructor.javaClass().isAssignableFrom(var_class);
         }
         return false;
      }
      
      public virtual void cascadeActivation(Transaction transaction, Object obj, int i, bool xbool) {
         Config4Class config4class1 = configOrAncestorConfig();
         if (config4class1 != null && xbool) i = config4class1.adjustActivationDepth(i);
         if (i > 0) {
            YapStream yapstream1 = transaction.i_stream;
            if (xbool) yapstream1.stillToActivate(obj, i - 1); else yapstream1.stillToDeactivate(obj, i - 1, false);
         }
      }
      
      internal void checkChanges() {
         if (stateOK() && !this.bitIsTrue(6)) {
            this.bitTrue(6);
            if (i_ancestor != null) i_ancestor.checkChanges();
            if (i_constructor != null) {
               addMembers(i_stream);
               if (!i_stream.isClient()) this.write(i_stream, i_stream.getSystemTransaction());
            }
         }
      }
      
      internal void checkUpdateDepth(YapWriter yapwriter) {
         int i1 = yapwriter.getUpdateDepth();
         Config4Class config4class1 = configOrAncestorConfig();
         if (i1 == -2147483548) i1 = checkUpdateDepthUnspecified(yapwriter.getStream());
         if (config4class1 != null && (config4class1.i_cascadeOnDelete == 1 || config4class1.i_cascadeOnUpdate == 1)) {
            int i_5_1 = Platform.collectionUpdateDepth(i_constructor.javaClass());
            if (i1 < i_5_1) i1 = i_5_1;
         }
         yapwriter.setUpdateDepth(i1 - 1);
      }
      
      internal int checkUpdateDepthUnspecified(YapStream yapstream) {
         int i1 = yapstream.i_config.i_updateDepth + 1;
         if (i_config != null && i_config.i_updateDepth != 0) i1 = i_config.i_updateDepth + 1;
         if (i_ancestor != null) {
            int i_6_1 = i_ancestor.checkUpdateDepthUnspecified(yapstream);
            if (i_6_1 > i1) return i_6_1;
         }
         return i1;
      }
      
      internal void collectConstraints(Transaction transaction, QConObject qconobject, Object obj, Visitor4 visitor4) {
         if (i_fields != null) {
            for (int i1 = 0; i1 < i_fields.Length; i1++) i_fields[i1].collectConstraints(transaction, qconobject, obj, visitor4);
         }
         if (i_ancestor != null) i_ancestor.collectConstraints(transaction, qconobject, obj, visitor4);
      }
      
      internal Config4Class configOrAncestorConfig() {
         if (i_config != null) return i_config;
         if (i_ancestor != null) return i_ancestor.configOrAncestorConfig();
         return null;
      }
      
      public void copyValue(Object obj, Object obj_7_) {
      }
      
      private void createConstructor(YapStream yapstream, String xstring) {
         Class var_class1 = null;
         try {
            {
               var_class1 = Db4o.classForName(yapstream, xstring);
            }
         }  catch (Exception throwable) {
            {
            }
         }
         createConstructor(yapstream, var_class1, xstring);
      }
      
      private void createConstructor(YapStream yapstream, Class var_class, String xstring) {
         if (i_config != null && i_config.instantiates()) i_constructor = new YapConstructor(yapstream, var_class, null, null, true, false); else {
            if (var_class != null && YapConst.CLASS_TRANSIENTCLASS.isAssignableFrom(var_class)) var_class = null;
            if (var_class == null) {
               if (xstring == null || xstring.IndexOf("com.db4o") != 0) yapstream.logMsg(23, xstring);
               setStateDead();
            } else {
               i_constructor = yapstream.i_handlers.createConstructorStatic(yapstream, this, var_class);
               if (i_constructor == null) {
                  setStateDead();
                  yapstream.logMsg(7, xstring);
               }
            }
         }
      }
      
      public void deactivate(Transaction transaction, Object obj, int i) {
         if (dispatchEvent(transaction.i_stream, obj, 7)) {
            deactivate1(transaction, obj, i);
            dispatchEvent(transaction.i_stream, obj, 3);
         }
      }
      
      internal void deactivate1(Transaction transaction, Object obj, int i) {
         for (int i_8_1 = 0; i_8_1 < i_fields.Length; i_8_1++) i_fields[i_8_1].deactivate(transaction, obj, i);
         if (i_ancestor != null) i_ancestor.deactivate1(transaction, obj, i);
      }
      
      internal void delete(YapWriter yapwriter, Object obj) {
         readObjectHeader(yapwriter, yapwriter.getID());
         delete1(yapwriter, obj);
      }
      
      internal void delete1(YapWriter yapwriter, Object obj) {
         removeFromIndex(yapwriter.getTransaction(), yapwriter.getID());
         deleteMembers(yapwriter, YapHandlers.arrayType(obj));
      }
      
      public virtual void deleteEmbedded(YapWriter yapwriter) {
         if (yapwriter.cascadeDeletes() > 0) {
            int i1 = yapwriter.readInt();
            if (i1 > 0) deleteEmbedded1(yapwriter, i1);
         } else yapwriter.incrementOffset(this.linkLength());
      }
      
      internal virtual void deleteEmbedded1(YapWriter yapwriter, int i) {
         if (yapwriter.cascadeDeletes() > 0) {
            Object obj1 = yapwriter.getStream().getByID2(yapwriter.getTransaction(), i);
            int i_9_1 = yapwriter.cascadeDeletes() - 1;
            if (obj1 != null && Platform.isCollection(j4o.lang.Class.getClassForObject(obj1))) i_9_1 += Platform.collectionUpdateDepth(j4o.lang.Class.getClassForObject(obj1)) - 1;
            YapObject yapobject1 = yapwriter.getStream().getYapObject(i);
            if (yapobject1 != null) yapwriter.getStream().delete3(yapwriter.getTransaction(), yapobject1, obj1, i_9_1);
         }
      }
      
      internal virtual void deleteMembers(YapWriter yapwriter, int i) {
         try {
            {
               Config4Class config4class1 = configOrAncestorConfig();
               if (config4class1 != null && config4class1.i_cascadeOnDelete == 1) {
                  int i_10_1 = yapwriter.cascadeDeletes();
                  if (Platform.isCollection(getJavaClass())) {
                     int i_11_1 = i_10_1 + Platform.collectionUpdateDepth(getJavaClass()) - 3;
                     if (i_11_1 < 1) i_11_1 = 1;
                     yapwriter.setCascadeDeletes(i_11_1);
                  } else yapwriter.setCascadeDeletes(1);
                  deleteMembers1(yapwriter, i);
                  yapwriter.setCascadeDeletes(i_10_1);
               } else deleteMembers1(yapwriter, i);
            }
         }  catch (Exception exception) {
            {
            }
         }
      }
      
      private void deleteMembers1(YapWriter yapwriter, int i) {
         int i_12_1 = readFieldLength(yapwriter);
         for (int i_13_1 = 0; i_13_1 < i_12_1; i_13_1++) i_fields[i_13_1].delete(yapwriter);
         if (i_ancestor != null) i_ancestor.deleteMembers(yapwriter, i);
      }
      
      internal bool dispatchEvent(YapStream yapstream, Object obj, int i) {
         if (i_constructor != null && yapstream.dispatchsEvents()) return i_constructor.dispatch(yapstream, obj, i);
         return true;
      }
      
      internal void dontDeleteLic(Object obj) {
      }
      
      public bool Equals(YapDataType yapdatatype) {
         return this == yapdatatype;
      }
      
      internal bool findOffset(YapReader yapreader, YapField yapfield) {
         if (yapreader == null) return false;
         yapreader._offset = 0;
         yapreader.incrementOffset(4);
         return findOffset1(yapreader, yapfield);
      }
      
      internal bool findOffset1(YapReader yapreader, YapField yapfield) {
         int i1 = readFieldLength(yapreader);
         for (int i_14_1 = 0; i_14_1 < i1; i_14_1++) {
            if (i_fields[i_14_1] == yapfield) return true;
            yapreader.incrementOffset(i_fields[i_14_1].linkLength());
         }
         if (i_ancestor != null) return i_ancestor.findOffset1(yapreader, yapfield);
         return false;
      }
      
      internal void forEachYapField(Visitor4 visitor4) {
         if (i_fields != null) {
            for (int i1 = 0; i1 < i_fields.Length; i1++) visitor4.visit(i_fields[i1]);
         }
         if (i_ancestor != null) i_ancestor.forEachYapField(visitor4);
      }
      
      private bool generateUUIDs() {
         if (i_stream is YapFile) {
            YapFile yapfile1 = (YapFile)i_stream;
            int i1 = i_config == null ? 0 : i_config.i_generateUUIDs;
            if (yapfile1.i_bootRecord == null) return false;
            return generate1(yapfile1.i_bootRecord.i_generateUUIDs, i1);
         }
         return false;
      }
      
      private bool generateVersionNumbers() {
         if (i_stream is YapFile) {
            YapFile yapfile1 = (YapFile)i_stream;
            int i1 = i_config == null ? 0 : i_config.i_generateVersionNumbers;
            if (yapfile1.i_bootRecord == null) return false;
            return generate1(yapfile1.i_bootRecord.i_generateVersionNumbers, i1);
         }
         return false;
      }
      
      private bool generate1(int i, int i_15_) {
         if (i < 0) return false;
         if (i_15_ < 0) return false;
         if (i > 1) return true;
         return i_15_ > 0;
      }
      
      internal YapClass getAncestor() {
         return i_ancestor;
      }
      
      internal Object getComparableObject(Object obj) {
         if (i_config != null && i_config.i_queryAttributeProvider != null) return i_config.i_queryAttributeProvider.attribute(obj);
         return obj;
      }
      
      internal YapClass getHigherHierarchy(YapClass yapclass_16_) {
         YapClass yapclass_17_1 = getHigherHierarchy1(yapclass_16_);
         if (yapclass_17_1 != null) return yapclass_17_1;
         return yapclass_16_.getHigherHierarchy1(this);
      }
      
      private YapClass getHigherHierarchy1(YapClass yapclass_18_) {
         if (yapclass_18_ == this) return this;
         if (i_ancestor != null) return i_ancestor.getHigherHierarchy1(yapclass_18_);
         return null;
      }
      
      internal YapClass getHigherOrCommonHierarchy(YapClass yapclass_19_) {
         YapClass yapclass_20_1 = getHigherHierarchy1(yapclass_19_);
         if (yapclass_20_1 != null) return yapclass_20_1;
         if (i_ancestor != null) {
            yapclass_20_1 = i_ancestor.getHigherOrCommonHierarchy(yapclass_19_);
            if (yapclass_20_1 != null) return yapclass_20_1;
         }
         return yapclass_19_.getHigherHierarchy1(this);
      }
      
      internal override byte getIdentifier() {
         return (byte)67;
      }
      
      public long[] getIDs() {
         lock (i_stream.i_lock) {
            if (stateOK()) return getIDs(i_stream.getTransaction());
            return new long[0];
         }
      }
      
      public long[] getIDs(Transaction transaction) {
         if (stateOK() && hasIndex()) return getIndex().getInternalIDs(transaction, this.getID());
         return new long[0];
      }
      
      internal virtual bool hasIndex() {
         return i_db4oType == null || i_db4oType.hasClassIndex();
      }
      
      private bool hasUUIDField() {
         if (i_ancestor != null && i_ancestor.hasUUIDField()) return true;
         if (i_fields != null) {
            for (int i1 = 0; i1 < i_fields.Length; i1++) {
               if (i_fields[i1] is YapFieldUUID) return true;
            }
         }
         return false;
      }
      
      private bool hasVersionField() {
         if (i_ancestor != null && i_ancestor.hasVersionField()) return true;
         if (i_fields != null) {
            for (int i1 = 0; i1 < i_fields.Length; i1++) {
               if (i_fields[i1] is YapFieldVersion) return true;
            }
         }
         return false;
      }
      
      internal virtual ClassIndex getIndex() {
         if (stateOK() && i_index != null) {
            if (!i_index.isActive()) {
               i_index.setStateDirty();
               i_index.read(i_stream.getSystemTransaction());
            }
            return i_index;
         }
         return null;
      }
      
      internal Tree getIndex(Transaction transaction) {
         if (hasIndex()) {
            ClassIndex classindex1 = getIndex();
            if (classindex1 != null) return classindex1.cloneForYapClass(transaction, this.getID());
         }
         return null;
      }
      
      internal TreeInt getIndexRoot() {
         if (hasIndex()) {
            ClassIndex classindex1 = getIndex();
            return (TreeInt)classindex1.i_root;
         }
         return null;
      }
      
      public virtual Class getJavaClass() {
         if (i_constructor == null) return null;
         return i_constructor.javaClass();
      }
      
      internal YapClass[] getMembersDependancies() {
         if (i_addMembersDependancies == null) return new YapClass[0];
         YapClass[] yapclasses1 = new YapClass[i_addMembersDependancies.size()];
         i_addMembersDependancies.toArray(yapclasses1);
         i_addMembersDependancies = null;
         return yapclasses1;
      }
      
      public String getName() {
         return i_name;
      }
      
      public StoredClass getParentStoredClass() {
         return getAncestor();
      }
      
      public Class getPrimitiveJavaClass() {
         return null;
      }
      
      public IClass getReflectorClass() {
         if (i_constructor == null) return null;
         return i_constructor.reflectorClass();
      }
      
      public StoredField[] getStoredFields() {
         lock (i_stream.i_lock) {
            if (i_fields == null) return null;
            StoredField[] storedfields1 = new StoredField[i_fields.Length];
            j4o.lang.JavaSystem.arraycopy(i_fields, 0, storedfields1, 0, i_fields.Length);
            return storedfields1;
         }
      }
      
      internal YapStream getStream() {
         return i_stream;
      }
      
      public int getType() {
         return 2;
      }
      
      public YapClass getYapClass(YapStream yapstream) {
         return this;
      }
      
      public YapField getYapField(String xstring) {
         YapField[] yapfields1 = new YapField[1];
         forEachYapField(new YapClass__1(this, xstring, yapfields1));
         return yapfields1[0];
      }
      
      public virtual bool hasField(YapStream yapstream, String xstring) {
         if (Platform.isCollection(getJavaClass())) return true;
         return getYapField(xstring) != null;
      }
      
      internal bool hasVirtualAttributes() {
         return hasVersionField() || hasUUIDField();
      }
      
      public virtual bool holdsAnyClass() {
         return Platform.isCollection(getJavaClass());
      }
      
      internal void incrementFieldsOffset1(YapReader yapreader) {
         int i1 = readFieldLength(yapreader);
         for (int i_21_1 = 0; i_21_1 < i1; i_21_1++) i_fields[i_21_1].incrementOffset(yapreader);
      }
      
      public Object indexObject(Transaction transaction, Object obj) {
         return obj;
      }
      
      internal void init(YapStream yapstream, YapClass yapclass_22_, YapConstructor yapconstructor) {
         i_stream = yapstream;
         i_constructor = yapconstructor;
         checkDb4oType();
         if (allowsQueries()) i_index = yapstream.createClassIndex(this);
         i_name = yapconstructor.getName();
         i_ancestor = yapclass_22_;
         this.bitTrue(6);
      }
      
      internal void initConfigOnUp(Transaction transaction) {
         if (i_config != null) {
            transaction.i_stream.showInternalClasses(true);
            i_config.initOnUp(transaction);
            if (i_fields != null) {
               for (int i1 = 0; i1 < i_fields.Length; i1++) i_fields[i1].initConfigOnUp(transaction);
            }
            transaction.i_stream.showInternalClasses(false);
         }
      }
      
      internal void initOnUp(Transaction transaction) {
         if (stateOK()) {
            initConfigOnUp(transaction);
            storeStaticFieldValues(transaction, false);
         }
      }
      
      internal virtual Object instantiate(YapObject yapobject, Object obj, YapWriter yapwriter, bool xbool) {
         YapStream yapstream1 = yapwriter.getStream();
         Transaction transaction1 = yapwriter.getTransaction();
         bool bool_23_1 = obj == null;
         if (i_config != null) yapwriter.setInstantiationDepth(i_config.adjustActivationDepth(yapwriter.getInstantiationDepth()));
         bool bool_24_1 = yapwriter.getInstantiationDepth() > 0 || i_config != null && i_config.i_cascadeOnActivate == 1;
         if (bool_23_1) {
            if (i_config != null && i_config.instantiates()) {
               int i1 = yapwriter._offset;
               yapwriter.incrementOffset(4);
               try {
                  {
                     obj = i_config.instantiate(yapstream1, i_fields[0].read(yapwriter));
                  }
               }  catch (Exception exception) {
                  {
                     Db4o.logErr(yapstream1.i_config, 6, getJavaClass().getName(), exception);
                     return null;
                  }
               }
               yapwriter._offset = i1;
            } else {
               if (i_constructor == null) return null;
               yapstream1.instantiating(true);
               try {
                  {
                     obj = i_constructor.newInstance();
                  }
               }  catch (NoSuchMethodError nosuchmethoderror) {
                  {
                     yapstream1.logMsg(7, getJavaClass().getName());
                     yapstream1.instantiating(false);
                     return null;
                  }
               } catch (Exception exception) {
                  {
                     yapstream1.instantiating(false);
                     return null;
                  }
               }
               yapstream1.instantiating(false);
            }
            if (obj is Db4oTypeImpl) {
               ((Db4oTypeImpl)obj).setTrans(yapwriter.getTransaction());
               ((Db4oTypeImpl)obj).setYapObject(yapobject);
            }
            yapobject.setObjectWeak(yapstream1, obj);
            yapstream1.hcTreeAdd(yapobject);
         } else if (!yapstream1.i_refreshInsteadOfActivate && yapobject.isActive()) bool_24_1 = false;
         if (xbool) yapobject.addToIDTree(yapstream1);
         if (bool_24_1) {
            if (dispatchEvent(yapstream1, obj, 6)) {
               yapobject.setStateClean();
               instantiateFields(yapobject, obj, yapwriter);
               dispatchEvent(yapstream1, obj, 2);
            } else if (bool_23_1) yapobject.setStateDeactivated();
         } else if (bool_23_1) yapobject.setStateDeactivated(); else if (yapwriter.getInstantiationDepth() > 1) activateFields(transaction1, obj, yapwriter.getInstantiationDepth() - 1);
         return obj;
      }
      
      internal virtual Object instantiateTransient(YapObject yapobject, Object obj, YapWriter yapwriter) {
         YapStream yapstream1 = yapwriter.getStream();
         if (i_config != null && i_config.instantiates()) {
            int i1 = yapwriter._offset;
            yapwriter.incrementOffset(4);
            try {
               {
                  obj = i_config.instantiate(yapstream1, i_fields[0].read(yapwriter));
               }
            }  catch (Exception exception) {
               {
                  Db4o.logErr(yapstream1.i_config, 6, getJavaClass().getName(), exception);
                  return null;
               }
            }
            yapwriter._offset = i1;
         } else {
            if (i_constructor == null) return null;
            yapstream1.instantiating(true);
            try {
               {
                  obj = i_constructor.newInstance();
               }
            }  catch (NoSuchMethodError nosuchmethoderror) {
               {
                  yapstream1.logMsg(7, getJavaClass().getName());
                  yapstream1.instantiating(false);
                  return null;
               }
            } catch (Exception exception) {
               {
                  yapstream1.instantiating(false);
                  return null;
               }
            }
            yapstream1.instantiating(false);
         }
         yapstream1.peeked(yapobject.getID(), obj);
         instantiateFields(yapobject, obj, yapwriter);
         return obj;
      }
      
      internal virtual void instantiateFields(YapObject yapobject, Object obj, YapWriter yapwriter) {
         int i1 = readFieldLength(yapwriter);
         try {
            {
               for (int i_25_1 = 0; i_25_1 < i1; i_25_1++) i_fields[i_25_1].instantiate(yapobject, obj, yapwriter);
               if (i_ancestor != null) i_ancestor.instantiateFields(yapobject, obj, yapwriter);
            }
         }  catch (CorruptionException corruptionexception) {
            {
            }
         }
      }
      
      internal TreeInt collectFieldIDs(TreeInt treeint, YapWriter yapwriter, String xstring) {
         int i1 = readFieldLength(yapwriter);
         for (int i_26_1 = 0; i_26_1 < i1; i_26_1++) {
            if (xstring.Equals(i_fields[i_26_1].getName())) treeint = i_fields[i_26_1].collectIDs(treeint, yapwriter); else i_fields[i_26_1].incrementOffset(yapwriter);
         }
         if (i_ancestor != null) return i_ancestor.collectFieldIDs(treeint, yapwriter, xstring);
         return treeint;
      }
      
      public Object indexEntry(Object obj) {
         return System.Convert.ToInt32(i_lastID);
      }
      
      public virtual bool isArray() {
         return Platform.isCollection(getJavaClass());
      }
      
      public override bool isDirty() {
         if (!stateOK()) return false;
         return base.isDirty();
      }
      
      internal virtual bool isStrongTyped() {
         return true;
      }
      
      internal virtual void marshall(YapObject yapobject, Object obj, YapWriter yapwriter, bool xbool) {
         Config4Class config4class1 = configOrAncestorConfig();
         yapwriter.writeInt(i_fields.Length);
         for (int i1 = 0; i1 < i_fields.Length; i1++) {
            Object obj_27_1 = i_fields[i1].getOrCreate(yapwriter.getTransaction(), obj);
            if (obj_27_1 is Db4oTypeImpl) obj_27_1 = ((Db4oTypeImpl)obj_27_1).storedTo(yapwriter.getTransaction());
            i_fields[i1].marshall(yapobject, obj_27_1, yapwriter, config4class1, xbool);
         }
         if (i_ancestor != null) i_ancestor.marshall(yapobject, obj, yapwriter, xbool);
      }
      
      internal virtual void marshallNew(YapObject yapobject, YapWriter yapwriter, Object obj) {
         checkUpdateDepth(yapwriter);
         marshall(yapobject, obj, yapwriter, true);
      }
      
      internal void marshallUpdate(Transaction transaction, int i, int i_28_, YapObject yapobject, Object obj) {
         int i_29_1 = objectLength();
         YapWriter yapwriter1 = new YapWriter(transaction, i_29_1);
         yapwriter1.setUpdateDepth(i_28_);
         checkUpdateDepth(yapwriter1);
         yapwriter1.useSlot(i, 0, i_29_1);
         yapwriter1.writeInt(this.getID());
         marshall(yapobject, obj, yapwriter1, false);
         YapStream yapstream1 = transaction.i_stream;
         yapstream1.writeUpdate(this, yapwriter1);
         if (yapobject.isActive()) yapobject.setStateClean();
         yapobject.endProcessing();
         dispatchEvent(yapstream1, obj, 5);
      }
      
      internal virtual int memberLength() {
         int i1 = 4;
         if (i_ancestor != null) i1 += i_ancestor.memberLength();
         if (i_fields != null) {
            for (int i_30_1 = 0; i_30_1 < i_fields.Length; i_30_1++) i1 += i_fields[i_30_1].linkLength();
         }
         return i1;
      }
      
      internal bool callConstructor(YapStream yapstream) {
         int i1 = callConstructorSpecialized();
         if (i1 != 0) return i1 == 1;
         return yapstream.i_config.i_callConstructors == 1;
      }
      
      internal int callConstructorSpecialized() {
         if (i_config != null) {
            int i1 = i_config.callConstructor();
            if (i1 != 0) return i1;
         }
         if (i_ancestor != null) return i_ancestor.callConstructorSpecialized();
         return 0;
      }
      
      internal int objectLength() {
         if (i_objectLength == 0) i_objectLength = memberLength() + 0 + 4;
         return i_objectLength;
      }
      
      internal override int ownLength() {
         int i1 = i_stream.i_stringIo.shortLength(getName()) + 0 + 8 + 8;
         if (i_fields != null) {
            for (int i_31_1 = 0; i_31_1 < i_fields.Length; i_31_1++) i1 += i_fields[i_31_1].ownLength(i_stream);
         }
         return i1;
      }
      
      internal void purge() {
         if (i_index != null && !i_index.isDirty()) {
            i_index.clear();
            i_index.setStateDeactivated();
         }
      }
      
      public Object read(YapWriter yapwriter) {
         try {
            {
               int i1 = yapwriter.readInt();
               int i_32_1 = yapwriter.getInstantiationDepth() - 1;
               Transaction transaction1 = yapwriter.getTransaction();
               YapStream yapstream1 = transaction1.i_stream;
               if (yapwriter.getUpdateDepth() == -1) return yapstream1.peekPersisted1(transaction1, i1, i_32_1);
               if (Platform.isValueType(getJavaClass())) {
                  if (i_32_1 < 1) i_32_1 = 1;
                  YapObject yapobject1 = yapstream1.getYapObject(i1);
                  if (yapobject1 != null) {
                     Object obj2 = yapobject1.getObject();
                     if (obj2 == null) yapstream1.yapObjectGCd(yapobject1); else {
                        yapobject1.activate(transaction1, obj2, i_32_1, false);
                        return yapobject1.getObject();
                     }
                  }
                  return new YapObject(i1).read(transaction1, null, null, i_32_1, 1);
               }
               Object obj1 = yapstream1.getByID2(transaction1, i1);
               if (obj1 is Db4oTypeImpl) i_32_1 = ((Db4oTypeImpl)obj1).adjustReadDepth(i_32_1);
               yapstream1.stillToActivate(obj1, i_32_1);
               return obj1;
            }
         }  catch (Exception exception) {
            {
               return null;
            }
         }
      }
      
      public Object readQuery(Transaction transaction, YapReader yapreader, bool xbool) {
         try {
            {
               return transaction.i_stream.getByID2(transaction, yapreader.readInt());
            }
         }  catch (Exception exception) {
            {
               return null;
            }
         }
      }
      
      public virtual YapDataType readArrayWrapper(Transaction transaction, YapReader[] yapreaders) {
         if (isArray()) return this;
         return null;
      }
      
      public YapDataType readArrayWrapper1(YapReader[] yapreaders) {
         if (isArray()) {
            if (Platform.isCollectionTranslator(i_config)) {
               yapreaders[0].incrementOffset(4);
               return new YapArray(null, false);
            }
            incrementFieldsOffset1(yapreaders[0]);
            if (i_ancestor != null) return i_ancestor.readArrayWrapper1(yapreaders);
         }
         return null;
      }
      
      public void readCandidates(YapReader yapreader, QCandidates qcandidates) {
         int i1 = 0;
         int i_33_1 = yapreader._offset;
         try {
            {
               i1 = yapreader.readInt();
            }
         }  catch (Exception exception) {
            {
            }
         }
         yapreader._offset = i_33_1;
         if (i1 != 0) {
            Transaction transaction1 = qcandidates.i_trans;
            Object obj1 = transaction1.i_stream.getByID1(transaction1, (long)i1);
            if (obj1 != null) {
               int[] xis1 = {
                  -2               };
               qcandidates.i_trans.i_stream.activate1(transaction1, obj1, 2);
               Platform.forEachCollectionElement(obj1, new YapClass__2(this, transaction1, xis1, qcandidates));
            }
         }
      }
      
      internal int readFieldLength(YapReader yapreader) {
         int i1 = yapreader.readInt();
         if (i1 > i_fields.Length) return i_fields.Length;
         return i1;
      }
      
      internal int readFieldLengthSodaAtHome(YapReader yapreader) {
         return 0;
      }
      
      public Object readIndexEntry(YapReader yapreader) {
         return System.Convert.ToInt32(yapreader.readInt());
      }
      
      public Object readIndexObject(YapWriter yapwriter) {
         return readIndexEntry(yapwriter);
      }
      
      internal byte[] readName(Transaction transaction) {
         i_stream = transaction.i_stream;
         i_reader = transaction.i_stream.readReaderByID(transaction, this.getID());
         if (i_reader != null) return readName1(transaction, i_reader);
         return null;
      }
      
      internal byte[] readName1(Transaction transaction, YapReader yapreader) {
         i_reader = yapreader;
         try {
            {
               int i1 = yapreader.readInt();
               i1 *= transaction.i_stream.i_stringIo.bytesPerChar();
               i_nameBytes = new byte[i1];
               j4o.lang.JavaSystem.arraycopy(yapreader._buffer, yapreader._offset, i_nameBytes, 0, i1);
               i_nameBytes = Platform.updateClassName(i_nameBytes);
               yapreader.incrementOffset(i1 + 4);
               setStateUnread();
               this.bitFalse(6);
               this.bitFalse(5);
               return i_nameBytes;
            }
         }  catch (Exception throwable) {
            {
               setStateDead();
               return null;
            }
         }
      }
      
      internal void readObjectHeader(YapReader yapreader, int i) {
         yapreader.incrementOffset(4);
      }
      
      internal void readVirtualAttributes(Transaction transaction, YapObject yapobject) {
         int i1 = yapobject.getID();
         YapStream yapstream1 = transaction.i_stream;
         YapReader yapreader1 = yapstream1.readReaderByID(transaction, i1);
         readObjectHeader(yapreader1, i1);
         readVirtualAttributes1(transaction, yapreader1, yapobject);
      }
      
      private void readVirtualAttributes1(Transaction transaction, YapReader yapreader, YapObject yapobject) {
         int i1 = readFieldLength(yapreader);
         for (int i_34_1 = 0; i_34_1 < i1; i_34_1++) i_fields[i_34_1].readVirtualAttribute(transaction, yapreader, yapobject);
         if (i_ancestor != null) i_ancestor.readVirtualAttributes1(transaction, yapreader, yapobject);
      }
      
      public void rename(String xstring) {
         if (!i_stream.isClient()) {
            int i1 = i_state;
            setStateOK();
            i_name = xstring;
            this.setStateDirty();
            this.write(i_stream, i_stream.getSystemTransaction());
            i_state = i1;
         } else Db4o.throwRuntimeException(58);
      }
      
      internal void createConfigAndConstructor(Hashtable4 hashtable4, YapStream yapstream, Class var_class) {
         if (var_class == null) {
            if (i_nameBytes != null) i_name = yapstream.i_stringIo.read(i_nameBytes);
         } else i_name = var_class.getName();
         i_config = i_stream.i_config.configClass(i_name);
         if (var_class == null) createConstructor(yapstream, i_name); else createConstructor(yapstream, var_class, i_name);
         if (i_nameBytes != null) {
            hashtable4.remove(i_nameBytes);
            i_nameBytes = null;
         }
      }
      
      internal bool readThis() {
         if (stateUnread()) {
            setStateOK();
            this.setStateClean();
            forceRead();
            return true;
         }
         return false;
      }
      
      internal void forceRead() {
         if (i_reader != null && this.bitIsFalse(8)) {
            this.bitTrue(8);
            i_ancestor = i_stream.getYapClass(i_reader.readInt());
            if (i_constructor != null && i_constructor.i_dontCallConstructors) createConstructor(i_stream, getJavaClass(), i_name);
            checkDb4oType();
            int i1 = i_reader.readInt();
            if (hasIndex()) {
               i_index = i_stream.createClassIndex(this);
               if (i1 > 0) i_index.setID(i_stream, i1);
               i_index.setStateDeactivated();
            }
            i_fields = new YapField[i_reader.readInt()];
            for (int i_35_1 = 0; i_35_1 < i_fields.Length; i_35_1++) {
               i_fields[i_35_1] = new YapField(this);
               i_fields[i_35_1].setArrayPosition(i_35_1);
            }
            for (int i_36_1 = 0; i_36_1 < i_fields.Length; i_36_1++) i_fields[i_36_1] = i_fields[i_36_1].readThis(i_stream, i_reader);
            for (int i_37_1 = 0; i_37_1 < i_fields.Length; i_37_1++) i_fields[i_37_1].loadHandler(i_stream);
            i_nameBytes = null;
            i_reader = null;
            this.bitFalse(8);
         }
      }
      
      private void checkDb4oType() {
         Class var_class1 = getJavaClass();
         if (var_class1 != null && YapConst.CLASS_DB4OTYPEIMPL.isAssignableFrom(var_class1)) {
            try {
               {
                  i_db4oType = (Db4oTypeImpl)var_class1.newInstance();
               }
            }  catch (Exception exception) {
               {
               }
            }
         }
      }
      
      internal override void readThis(Transaction transaction, YapReader yapreader) {
         throw YapConst.virtualException();
      }
      
      internal void refresh() {
         if (!stateUnread()) {
            createConstructor(i_stream, i_name);
            this.bitFalse(6);
            checkChanges();
            if (i_fields != null) {
               for (int i1 = 0; i1 < i_fields.Length; i1++) i_fields[i1].refresh();
            }
         }
      }
      
      internal virtual void removeFromIndex(Transaction transaction, int i) {
         if (hasIndex()) transaction.removeFromClassIndex(this.getID(), i);
         if (i_ancestor != null) i_ancestor.removeFromIndex(transaction, i);
      }
      
      internal bool renameField(String xstring, String string_38_) {
         bool xbool1 = false;
         for (int i1 = 0; i1 < i_fields.Length; i1++) {
            if (i_fields[i1].getName().Equals(string_38_)) {
               i_stream.logMsg(9, "class:" + getName() + " field:" + string_38_);
               return false;
            }
         }
         for (int i1 = 0; i1 < i_fields.Length; i1++) {
            if (i_fields[i1].getName().Equals(xstring)) {
               i_fields[i1].setName(string_38_);
               xbool1 = true;
            }
         }
         return xbool1;
      }
      
      internal override void setID(YapStream yapstream, int i) {
         i_stream = yapstream;
         base.setID(yapstream, i);
      }
      
      internal void setName(String xstring) {
         i_name = xstring;
      }
      
      private void setStateDead() {
         this.bitTrue(7);
         this.bitFalse(4);
      }
      
      private void setStateUnread() {
         this.bitFalse(7);
         this.bitTrue(4);
      }
      
      private void setStateOK() {
         this.bitFalse(7);
         this.bitFalse(4);
      }
      
      internal bool stateDead() {
         return this.bitIsTrue(7);
      }
      
      private bool stateOK() {
         return this.bitIsFalse(4) && this.bitIsFalse(7) && this.bitIsFalse(8);
      }
      
      internal bool stateOKAndAncestors() {
         if (!stateOK() || i_fields == null) return false;
         if (i_ancestor != null) return i_ancestor.stateOKAndAncestors();
         return true;
      }
      
      internal bool stateUnread() {
         return this.bitIsTrue(4) && this.bitIsFalse(7) && this.bitIsFalse(8);
      }
      
      internal bool storeField(IField ifield) {
         if (ifield.isStatic()) return false;
         if (ifield.isTransient()) {
            Config4Class config4class1 = configOrAncestorConfig();
            if (config4class1 == null) return false;
            if (!config4class1.i_storeTransientFields) return false;
         }
         return Platform.canSetAccessible() || ifield.isPublic();
      }
      
      public StoredField storedField(String xstring, Object obj) {
         lock (i_stream.i_lock) {
            obj = Platform.getClassForType(obj);
            if (obj is String) {
               try {
                  {
                     obj = Db4o.classForName((String)obj);
                  }
               }  catch (ClassNotFoundException classnotfoundexception) {
                  {
                     return null;
                  }
               }
            }
            YapClass yapclass_39_1 = null;
            if (obj is Class) yapclass_39_1 = i_stream.getYapClass((Class)obj, true); else if (obj != null) yapclass_39_1 = i_stream.getYapClass(j4o.lang.Class.getClassForObject(obj), true);
            if (i_fields != null) {
               for (int i1 = 0; i1 < i_fields.Length; i1++) {
                  if (i_fields[i1].getName().Equals(xstring) && (yapclass_39_1 == null || yapclass_39_1 == i_fields[i1].getFieldYapClass(i_stream))) return i_fields[i1];
               }
            }
            return null;
         }
      }
      
      internal void storeStaticFieldValues(Transaction transaction, bool xbool) {
         if (!this.bitIsTrue(5) || xbool) {
            this.bitTrue(5);
            bool bool_40_1 = i_config != null && i_config.i_persistStaticFieldValues || Platform.storeStaticFieldValues(getJavaClass());
            if (bool_40_1) {
               YapStream yapstream1 = transaction.i_stream;
               yapstream1.showInternalClasses(true);
               Query query1 = yapstream1.querySharpenBug(transaction);
               query1.constrain(YapConst.CLASS_STATICCLASS);
               query1.descend("name").constrain(i_name);
               StaticClass staticclass1 = new StaticClass();
               staticclass1.name = i_name;
               ObjectSet objectset1 = query1.execute();
               StaticField[] staticfields1 = null;
               if (objectset1.size() > 0) {
                  staticclass1 = (StaticClass)objectset1.next();
                  yapstream1.activate1(transaction, staticclass1, 4);
                  staticfields1 = staticclass1.fields;
               }
               IField[] ifields1 = i_constructor.reflectorClass().getDeclaredFields();
               Collection4 collection41 = new Collection4();
               for (int i1 = 0; i1 < ifields1.Length; i1++) {
                  if (ifields1[i1].isStatic()) {
                     ifields1[i1].setAccessible();
                     String xstring1 = ifields1[i1].getName();
                     Object obj1 = ifields1[i1].get(null);
                     bool bool_41_1 = false;
                     if (staticfields1 != null) {
                        for (int i_42_1 = 0; i_42_1 < staticfields1.Length; i_42_1++) {
                           if (xstring1.Equals(staticfields1[i_42_1].name)) {
                              if (staticfields1[i_42_1].value != null && obj1 != null && j4o.lang.Class.getClassForObject(staticfields1[i_42_1].value) == j4o.lang.Class.getClassForObject(obj1)) {
                                 long l1 = (long)yapstream1.getID1(transaction, staticfields1[i_42_1].value);
                                 if (l1 > 0L) {
                                    if (staticfields1[i_42_1].value != obj1) {
                                       yapstream1.bind1(transaction, obj1, l1);
                                       yapstream1.refresh(obj1, 2147483647);
                                       staticfields1[i_42_1].value = obj1;
                                    }
                                    bool_41_1 = true;
                                 }
                              }
                              if (!bool_41_1) {
                                 staticfields1[i_42_1].value = obj1;
                                 if (!yapstream1.isClient()) yapstream1.setInternal(transaction, staticfields1[i_42_1], true);
                              }
                              collection41.add(staticfields1[i_42_1]);
                              bool_41_1 = true;
                              break;
                           }
                        }
                     }
                     if (!bool_41_1) collection41.add(new StaticField(xstring1, obj1));
                  }
               }
               if (collection41.size() > 0) {
                  staticclass1.fields = new StaticField[collection41.size()];
                  collection41.toArray(staticclass1.fields);
                  if (!yapstream1.isClient()) yapstream1.setInternal(transaction, staticclass1, true);
               }
               yapstream1.showInternalClasses(false);
            }
         }
      }
      
      public virtual bool supportsIndex() {
         return true;
      }
      
      public override String ToString() {
         return i_name;
      }
      
      internal override bool writeObjectBegin() {
         if (!stateOK()) return false;
         return base.writeObjectBegin();
      }
      
      public void writeIndexEntry(YapWriter yapwriter, Object obj) {
         yapwriter.writeInt(System.Convert.ToInt32((Int32)obj));
      }
      
      public void writeNew(Object obj, YapWriter yapwriter) {
         if (obj == null) {
            yapwriter.writeInt(0);
            i_lastID = 0;
         } else {
            i_lastID = yapwriter.getStream().set3(yapwriter.getTransaction(), obj, yapwriter.getUpdateDepth(), true);
            yapwriter.writeInt(i_lastID);
         }
      }
      
      internal override void writeThis(YapWriter yapwriter) {
         yapwriter.writeShortString(i_name);
         yapwriter.writeInt(0);
         YapMeta.writeIDOf(i_ancestor, yapwriter);
         YapMeta.writeIDOf(i_index, yapwriter);
         if (i_fields == null) yapwriter.writeInt(0); else {
            yapwriter.writeInt(i_fields.Length);
            for (int i1 = 0; i1 < i_fields.Length; i1++) i_fields[i1].writeThis(yapwriter, this);
         }
         YapClassCollection yapclasscollection1 = yapwriter.i_trans.i_stream.i_classCollection;
         yapclasscollection1.yapClassRequestsInitOnUp(this);
      }
      
      public void prepareLastIoComparison(Transaction transaction, Object obj) {
         prepareComparison(obj);
      }
      
      public virtual YapComparable prepareComparison(Object obj) {
         if (obj != null) {
            if (obj is Int32) i_lastID = System.Convert.ToInt32((Int32)obj); else i_lastID = (int)i_stream.getID(obj);
            i_compareTo = j4o.lang.Class.getClassForObject(obj);
         } else i_compareTo = null;
         return this;
      }
      
      public int compareTo(Object obj) {
         if (obj is Int32) return System.Convert.ToInt32((Int32)obj) - i_lastID;
         return -1;
      }
      
      public bool isEqual(Object obj) {
         if (obj == null) return i_compareTo == null;
         return i_compareTo.isAssignableFrom(j4o.lang.Class.getClassForObject(obj));
      }
      
      public bool isGreater(Object obj) {
         return false;
      }
      
      public bool isSmaller(Object obj) {
         return false;
      }
      
      public String ToString(YapWriter yapwriter, YapObject yapobject, int i, int i_43_) {
         int i_44_1 = readFieldLength(yapwriter);
         String xstring1 = "";
         for (int i_45_1 = 0; i_45_1 < i_44_1; i_45_1++) xstring1 += i_fields[i_45_1].ToString(yapwriter, yapobject, i + 1, i_43_);
         if (i_ancestor != null) xstring1 += i_ancestor.ToString(yapwriter, yapobject, i, i_43_);
         return xstring1;
      }
   }
}