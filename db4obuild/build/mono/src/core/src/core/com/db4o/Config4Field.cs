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
using com.db4o.reflect;
namespace com.db4o {

   internal class Config4Field : Config4Abstract, ObjectField, Cloneable, DeepClone {
      internal Config4Class i_class;
      internal IField i_fieldReflector;
      internal bool i_queryEvaluation = true;
      internal int i_indexed = 0;
      internal MetaField i_metaField;
      internal bool i_initialized;
      
      internal Config4Field(Config4Class config4class, String xstring) : base() {
         i_class = config4class;
         i_name = xstring;
      }
      
      internal override String className() {
         return i_class.getName();
      }
      
      public Object deepClone(Object obj) {
         Config4Field config4field_0_1 = (Config4Field)j4o.lang.JavaSystem.clone(this);
         config4field_0_1.i_class = (Config4Class)obj;
         return config4field_0_1;
      }
      
      internal Class invocationClass() {
         return fieldReflector().getType();
      }
      
      private IField fieldReflector() {
         if (i_fieldReflector == null) {
            try {
               {
                  IClass iclass1 = Db4o.reflector().forName(className());
                  i_fieldReflector = iclass1.getDeclaredField(this.getName());
                  i_fieldReflector.setAccessible();
               }
            }  catch (Exception exception) {
               {
               }
            }
         }
         return i_fieldReflector;
      }
      
      public void queryEvaluation(bool xbool) {
         i_queryEvaluation = xbool;
      }
      
      public void rename(String xstring) {
         i_class.i_config.rename(new Rename(i_class.getName(), i_name, xstring));
         i_name = xstring;
      }
      
      public void indexed(bool xbool) {
         if (xbool) i_indexed = 1; else i_indexed = -1;
      }
      
      public void initOnUp(Transaction transaction, YapField yapfield) {
         if (!i_initialized) {
            YapStream yapstream1 = transaction.i_stream;
            if (yapstream1.maintainsIndices()) {
               if (!yapfield.supportsIndex()) i_indexed = -1;
               bool xbool1 = false;
               YapFile yapfile1 = (YapFile)yapstream1;
               i_metaField = i_class.i_metaClass.ensureField(transaction, i_name);
               if (i_indexed == 1 && i_metaField.index == null) {
                  i_metaField.index = new MetaIndex();
                  yapfile1.set3(transaction, i_metaField.index, -2147483548, false);
                  yapfile1.set3(transaction, i_metaField, -2147483548, false);
                  yapfield.initIndex(transaction, i_metaField.index);
                  xbool1 = true;
                  if (yapfile1.i_config.i_messageLevel > 0) yapfile1.message("creating index " + yapfield.ToString());
                  YapClass yapclass1 = yapfield.getParentYapClass();
                  long[] ls1 = yapclass1.getIDs();
                  for (int i1 = 0; i1 < ls1.Length; i1++) {
                     YapWriter yapwriter1 = yapfile1.readWriterByID(transaction, (int)ls1[i1]);
                     if (yapwriter1 != null) {
                        Object obj1 = null;
                        YapClass yapclass_1_1 = YapClassAny.readYapClass(yapwriter1);
                        if (yapclass_1_1 != null && yapclass_1_1.findOffset(yapwriter1, yapfield)) {
                           try {
                              {
                                 obj1 = yapfield.read(yapwriter1);
                              }
                           }  catch (CorruptionException corruptionexception) {
                              {
                              }
                           }
                        }
                        yapfield.addIndexEntry(transaction, (int)ls1[i1], obj1);
                     }
                  }
                  if (ls1.Length > 0) transaction.commit();
               }
               if (i_indexed == -1 && i_metaField.index != null) {
                  if (yapfile1.i_config.i_messageLevel > 0) yapfile1.message("dropping index " + yapfield.ToString());
                  MetaIndex metaindex1 = i_metaField.index;
                  if (metaindex1.indexLength > 0) yapfile1.free(metaindex1.indexAddress, metaindex1.indexLength);
                  if (metaindex1.patchLength > 0) yapfile1.free(metaindex1.patchAddress, metaindex1.patchLength);
                  yapfile1.delete1(transaction, metaindex1);
                  i_metaField.index = null;
                  yapfile1.setInternal(transaction, i_metaField, -2147483548, false);
               }
               if (i_metaField.index != null && !xbool1) yapfield.initIndex(transaction, i_metaField.index);
            }
            i_initialized = true;
         }
      }
   }
}