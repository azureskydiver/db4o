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
using j4o.lang.reflect;
using com.db4o.ext;
namespace com.db4o {

   internal class PBootRecord : P1Object, Db4oTypeImpl {
      [Transient] internal YapFile i_stream;
      public Db4oDatabase i_db;
      public long i_uuidGenerator;
      public long i_versionGenerator;
      public int i_generateVersionNumbers;
      public int i_generateUUIDs;
      [Transient] private bool i_dirty;
      public MetaIndex i_uuidMetaIndex;
      
      public PBootRecord() : base() {
      }
      
      public override int activationDepth() {
         return 2147483647;
      }
      
      internal void init(Config4Impl config4impl) {
         i_db = Db4oDatabase.generate();
         i_uuidGenerator = Unobfuscated.randomLong();
         initConfig(config4impl);
         i_dirty = true;
      }
      
      internal bool initConfig(Config4Impl config4impl) {
         bool xbool1 = false;
         Class var_class1 = j4o.lang.Class.getClassForObject(this);
         Class var_class_0_1 = j4o.lang.Class.getClassForObject(config4impl);
         Field[] fields1 = var_class1.getDeclaredFields();
         for (int i1 = 0; i1 < fields1.Length; i1++) {
            try {
               {
                  Field field1 = var_class_0_1.getField(fields1[i1].getName());
                  if (field1 != null) {
                     Object obj1 = field1.get(config4impl);
                     if (obj1 != null) {
                        YapClass yapclass1 = i_stream.i_handlers.getYapClassStatic(j4o.lang.Class.getClassForObject(obj1));
                        if (yapclass1 is YapClassPrimitive) {
                           YapJavaClass yapjavaclass1 = (YapJavaClass)((YapClassPrimitive)yapclass1).i_handler;
                           if (!yapjavaclass1.primitiveNull().Equals(obj1)) {
                              fields1[i1].set(this, obj1);
                              xbool1 = true;
                           }
                        }
                     }
                  }
               }
            }  catch (Exception exception) {
               {
               }
            }
         }
         return xbool1;
      }
      
      internal MetaIndex getUUIDMetaIndex() {
         if (i_uuidMetaIndex == null) {
            i_uuidMetaIndex = new MetaIndex();
            Transaction transaction1 = i_stream.getSystemTransaction();
            i_stream.setInternal(transaction1, this, false);
            transaction1.commit();
         }
         return i_uuidMetaIndex;
      }
      
      internal long newUUID() {
         i_dirty = true;
         return i_uuidGenerator++;
      }
      
      internal void setDirty() {
         i_dirty = true;
      }
      
      internal override void store(int i) {
         if (i_dirty) {
            i_versionGenerator++;
            base.store(i);
         }
         i_dirty = false;
      }
      
      internal long version() {
         i_dirty = true;
         return i_versionGenerator;
      }
   }
}