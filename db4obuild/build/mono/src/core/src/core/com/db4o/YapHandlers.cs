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
using com.db4o.reflect;
using com.db4o.types;
namespace com.db4o {

   internal class YapHandlers {
      private static Db4oTypeImpl[] i_db4oTypes = {
         new BlobImpl()      };
      static internal int ANYARRAYID = 12;
      static internal int ANYARRAYNID = 13;
      private static int CLASSCOUNT = 11;
      private YapClass i_anyArray;
      private YapClass i_anyArrayN;
      internal YapString i_stringHandler;
      private YapDataType[] i_handlers;
      private static int i_maxTypeID = 14;
      private YapTypeAbstract[] i_platformTypes = Platform.types();
      private static int PRIMITIVECOUNT = 8;
      internal YapClass[] i_yapClasses;
      static internal Class licenseClass = j4o.lang.Class.getClassForObject(new License());
      static internal int YAPANY = 10;
      static internal int YAPANYID = 11;
      internal YapFieldVirtual[] i_virtualFields = new YapFieldVirtual[2];
      private Hashtable4 i_classByClass = new Hashtable4(32);
      internal Db4oCollections i_collections;
      internal YapIndexes i_indexes = new YapIndexes();
      internal ReplicationImpl i_replication;
      internal bool i_encrypt;
      internal byte[] i_encryptor;
      internal int i_lastEncryptorByte;
      
      internal YapHandlers(YapStream yapstream) : base() {
         i_virtualFields[0] = i_indexes.i_fieldVersion;
         i_virtualFields[1] = i_indexes.i_fieldUUID;
         i_stringHandler = new YapString();
         i_handlers = new YapDataType[]{
            new YInt(),
new YLong(),
new YFloat(),
new YBoolean(),
new YDouble(),
new YByte(),
new YChar(),
new YShort(),
i_stringHandler,
new YDate(),
new YapClassAny()         };
         if (i_platformTypes.Length > 0) {
            for (int i1 = 0; i1 < i_platformTypes.Length; i1++) {
               i_platformTypes[i1].initialize();
               if (i_platformTypes[i1].getID() > i_maxTypeID) i_maxTypeID = i_platformTypes[i1].getID();
            }
            YapDataType[] yapdatatypes1 = i_handlers;
            i_handlers = new YapDataType[i_maxTypeID];
            j4o.lang.JavaSystem.arraycopy(yapdatatypes1, 0, i_handlers, 0, yapdatatypes1.Length);
            for (int i1 = 0; i1 < i_platformTypes.Length; i1++) {
               int i_0_1 = i_platformTypes[i1].getID() - 1;
               i_handlers[i_0_1] = i_platformTypes[i1];
            }
         }
         i_yapClasses = new YapClass[i_maxTypeID + 1];
         for (int i1 = 0; i1 < 11; i1++) {
            i_yapClasses[i1] = new YapClassPrimitive(null, i_handlers[i1]);
            i_yapClasses[i1].i_id = i1 + 1;
            i_classByClass.put(i_handlers[i1].getJavaClass(), i_yapClasses[i1]);
         }
         for (int i1 = 0; i1 < i_platformTypes.Length; i1++) {
            int i_1_1 = i_platformTypes[i1].getID() - 1;
            i_handlers[i_1_1] = i_platformTypes[i1];
            i_yapClasses[i_1_1] = new YapClassPrimitive(null, i_platformTypes[i1]);
            i_yapClasses[i_1_1].i_id = i_1_1 + 1;
            if (i_yapClasses[i_1_1].i_id > i_maxTypeID) i_maxTypeID = i_1_1;
            i_classByClass.put(i_platformTypes[i1].getJavaClass(), i_yapClasses[i_1_1]);
         }
         i_anyArray = new YapClassPrimitive(null, new YapArray(i_handlers[10], false));
         i_anyArray.i_id = 12;
         i_yapClasses[11] = i_anyArray;
         i_anyArrayN = new YapClassPrimitive(null, new YapArrayN(i_handlers[10], false));
         i_anyArrayN.i_id = 13;
         i_yapClasses[12] = i_anyArrayN;
      }
      
      static internal int arrayType(Object obj) {
         if (j4o.lang.Class.getClassForObject(obj).isArray()) {
            if (Array4.isNDimensional(j4o.lang.Class.getClassForObject(obj))) return 4;
            return 3;
         }
         return 0;
      }
      
      internal YapConstructor createConstructorStatic(YapStream yapstream, YapClass yapclass, Class var_class) {
         IReflect ireflect1 = Db4o.reflector();
         IClass iclass1;
         try {
            {
               iclass1 = ireflect1.forName(var_class.getName());
            }
         }  catch (ClassNotFoundException classnotfoundexception) {
            {
               return null;
            }
         }
         if (iclass1 == null) {
            if (yapstream.i_config.i_exceptionsOnNotStorable) throw new ObjectNotStorableException(var_class);
            return null;
         }
         if (iclass1.isAbstract() || iclass1.isInterface()) return new YapConstructor(yapstream, var_class, null, null, false, false);
         if (yapstream.i_config.i_testConstructors) {
            Object obj1 = iclass1.newInstance();
            if (obj1 != null) return new YapConstructor(yapstream, var_class, null, null, true, false);
         } else return new YapConstructor(yapstream, var_class, null, null, true, false);
         if (ireflect1.constructorCallsSupported()) {
            try {
               {
                  IConstructor[] iconstructors1 = iclass1.getDeclaredConstructors();
                  Tree tree1 = null;
                  for (int i1 = 0; i1 < iconstructors1.Length; i1++) {
                     try {
                        {
                           iconstructors1[i1].setAccessible();
                           int i_2_1 = iconstructors1[i1].getParameterTypes().Length;
                           tree1 = Tree.add(tree1, new TreeIntObject(i_2_1, iconstructors1[i1]));
                        }
                     }  catch (Exception throwable) {
                        {
                        }
                     }
                  }
                  YapConstructor[] yapconstructors1 = new YapConstructor[1];
                  if (tree1 != null) tree1.traverse(new YapHandlers__1(this, yapconstructors1, yapstream, var_class));
                  if (yapconstructors1[0] != null) return yapconstructors1[0];
               }
            }  catch (Exception throwable) {
               {
               }
            }
         }
         if (yapstream.i_config.i_exceptionsOnNotStorable) throw new ObjectNotStorableException(var_class);
         return null;
      }
      
      internal void decrypt(YapReader yapreader) {
         if (i_encrypt) {
            int i1 = i_lastEncryptorByte;
            byte[] xis1 = yapreader._buffer;
            for (int i_3_1 = yapreader.getLength() - 1; i_3_1 >= 0; i_3_1--) {
               xis1[i_3_1] += i_encryptor[i1];
               if (i1 == 0) i1 = i_lastEncryptorByte; else i1--;
            }
         }
      }
      
      internal void encrypt(YapReader yapreader) {
         if (i_encrypt) {
            byte[] xis1 = yapreader._buffer;
            int i1 = i_lastEncryptorByte;
            for (int i_4_1 = yapreader.getLength() - 1; i_4_1 >= 0; i_4_1--) {
               xis1[i_4_1] -= i_encryptor[i1];
               if (i1 == 0) i1 = i_lastEncryptorByte; else i1--;
            }
         }
      }
      
      internal Db4oDatabase ensureDb4oDatabase(Transaction transaction, Db4oDatabase db4odatabase) {
         YapStream yapstream1 = transaction.i_stream;
         Object obj1 = yapstream1.db4oTypeStored(transaction, db4odatabase);
         if (obj1 != null) return (Db4oDatabase)obj1;
         yapstream1.set3(transaction, db4odatabase, 2, false);
         return db4odatabase;
      }
      
      internal YapDataType getHandler(int i) {
         return i_handlers[i - 1];
      }
      
      internal YapDataType handlerForClass(Class var_class, Class[] var_classes) {
         for (int i1 = 0; i1 < var_classes.Length; i1++) {
            if (var_classes[i1] == var_class) return i_handlers[i1];
         }
         return null;
      }
      
      internal YapDataType handlerForClass(YapStream yapstream, Class var_class) {
         if (var_class.isArray()) return handlerForClass(yapstream, var_class.getComponentType());
         YapClass yapclass1 = getYapClassStatic(var_class);
         if (yapclass1 != null) return ((YapClassPrimitive)yapclass1).i_handler;
         return yapstream.getYapClass(var_class, true);
      }
      
      internal void initEncryption(Config4Impl config4impl) {
         if (config4impl.i_encrypt && config4impl.i_password != null && j4o.lang.JavaSystem.getLengthOf(config4impl.i_password) > 0) {
            i_encrypt = true;
            i_encryptor = new byte[j4o.lang.JavaSystem.getLengthOf(config4impl.i_password)];
            for (int i1 = 0; i1 < i_encryptor.Length; i1++) i_encryptor[i1] = (byte)(j4o.lang.JavaSystem.getCharAt(config4impl.i_password, i1) & (char)255);
            i_lastEncryptorByte = j4o.lang.JavaSystem.getLengthOf(config4impl.i_password) - 1;
         } else {
            i_encrypt = false;
            i_encryptor = null;
            i_lastEncryptorByte = 0;
         }
      }
      
      static internal Db4oTypeImpl getDb4oType(Class var_class) {
         for (int i1 = 0; i1 < i_db4oTypes.Length; i1++) {
            if (var_class.isInstance(i_db4oTypes[i1])) return i_db4oTypes[i1];
         }
         return null;
      }
      
      internal YapClass getYapClassStatic(int i) {
         if (i > 0 && i <= i_maxTypeID) return i_yapClasses[i - 1];
         return null;
      }
      
      internal YapClass getYapClassStatic(Class var_class) {
         if (var_class == null) return null;
         if (var_class.isArray()) {
            if (Array4.isNDimensional(var_class)) return i_anyArrayN;
            return i_anyArray;
         }
         return (YapClass)i_classByClass.get(var_class);
      }
      
      public bool isSecondClass(Object obj) {
         if (obj != null) {
            Class var_class1 = j4o.lang.Class.getClassForObject(obj);
            if (i_classByClass.get(var_class1) != null) return true;
            return Platform.isSecondClass(var_class1);
         }
         return false;
      }
      
      static internal int maxTypeID() {
         return i_maxTypeID;
      }
      
      static internal YapDataType[] access__000(YapHandlers yaphandlers) {
         return yaphandlers.i_handlers;
      }
   }
}