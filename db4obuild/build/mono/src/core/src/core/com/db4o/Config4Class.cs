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
namespace com.db4o {

   internal class Config4Class : Config4Abstract, ObjectClass, Cloneable, DeepClone {
      internal int i_callConstructor;
      internal Config4Impl i_config;
      private Hashtable4 i_exceptionalFields;
      internal int i_generateUUIDs;
      internal int i_generateVersionNumbers;
      internal int i_maximumActivationDepth;
      internal MetaClass i_metaClass;
      internal int i_minimumActivationDepth;
      internal bool i_persistStaticFieldValues;
      internal ObjectAttribute i_queryAttributeProvider;
      internal bool i_storeTransientFields;
      internal ObjectTranslator i_translator;
      internal String i_translatorName;
      internal int i_updateDepth;
      
      internal Config4Class(Config4Impl config4impl, String xstring) : base() {
         i_config = config4impl;
         i_name = xstring;
      }
      
      internal int adjustActivationDepth(int i) {
         if (i_cascadeOnActivate == 1 && i < 2) i = 2;
         if (i_cascadeOnActivate == -1 && i > 1) i = 1;
         if (i_config.i_classActivationDepthConfigurable) {
            if (i_minimumActivationDepth != 0 && i < i_minimumActivationDepth) i = i_minimumActivationDepth;
            if (i_maximumActivationDepth != 0 && i > i_maximumActivationDepth) i = i_maximumActivationDepth;
         }
         return i;
      }
      
      public void callConstructor(bool xbool) {
         i_callConstructor = xbool ? 1 : -1;
      }
      
      internal override String className() {
         return this.getName();
      }
      
      public void compare(ObjectAttribute objectattribute) {
         i_queryAttributeProvider = objectattribute;
      }
      
      internal Config4Field configField(String xstring) {
         if (i_exceptionalFields == null) return null;
         return (Config4Field)i_exceptionalFields.get(xstring);
      }
      
      public Object deepClone(Object obj) {
         Config4Class config4class_0_1 = (Config4Class)j4o.lang.JavaSystem.clone(this);
         config4class_0_1.i_config = (Config4Impl)obj;
         if (i_exceptionalFields != null) config4class_0_1.i_exceptionalFields = (Hashtable4)i_exceptionalFields.deepClone(config4class_0_1);
         return config4class_0_1;
      }
      
      public void generateUUIDs(bool xbool) {
         i_generateUUIDs = xbool ? 1 : -1;
      }
      
      public void generateVersionNumbers(bool xbool) {
         i_generateVersionNumbers = xbool ? 1 : -1;
      }
      
      public ObjectTranslator getTranslator() {
         if (i_translator == null && i_translatorName != null) {
            try {
               {
                  i_translator = (ObjectTranslator)Db4o.classForName(i_translatorName).newInstance();
               }
            }  catch (Exception throwable) {
               {
                  Db4o.logErr(i_config, 48, i_translatorName, null);
                  i_translatorName = null;
               }
            }
         }
         return i_translator;
      }
      
      public void initOnUp(Transaction transaction) {
         YapStream yapstream1 = transaction.i_stream;
         if (yapstream1.maintainsIndices()) {
            i_metaClass = (MetaClass)yapstream1.get1(transaction, new MetaClass(i_name)).next();
            if (i_metaClass == null) {
               i_metaClass = new MetaClass(i_name);
               yapstream1.set3(transaction, i_metaClass, 2147483647, false);
            } else yapstream1.activate1(transaction, i_metaClass, 2147483647);
         }
      }
      
      internal Object instantiate(YapStream yapstream, Object obj) {
         return ((ObjectConstructor)i_translator).onInstantiate(yapstream, obj);
      }
      
      internal bool instantiates() {
         return getTranslator() is ObjectConstructor;
      }
      
      public void maximumActivationDepth(int i) {
         i_maximumActivationDepth = i;
      }
      
      public void minimumActivationDepth(int i) {
         i_minimumActivationDepth = i;
      }
      
      public int callConstructor() {
         if (i_translator != null) return 1;
         return i_callConstructor;
      }
      
      public ObjectField objectField(String xstring) {
         if (i_exceptionalFields == null) i_exceptionalFields = new Hashtable4(16);
         Config4Field config4field1 = (Config4Field)i_exceptionalFields.get(xstring);
         if (config4field1 == null) {
            config4field1 = new Config4Field(this, xstring);
            i_exceptionalFields.put(xstring, config4field1);
         }
         return config4field1;
      }
      
      public void persistStaticFieldValues() {
         i_persistStaticFieldValues = true;
      }
      
      internal bool queryEvaluation(String xstring) {
         if (i_exceptionalFields != null) {
            Config4Field config4field1 = (Config4Field)i_exceptionalFields.get(xstring);
            if (config4field1 != null) return config4field1.i_queryEvaluation;
         }
         return true;
      }
      
      public void rename(String xstring) {
         i_config.rename(new Rename("", i_name, xstring));
         i_name = xstring;
      }
      
      public void storeTransientFields(bool xbool) {
         i_storeTransientFields = xbool;
      }
      
      public void translate(ObjectTranslator objecttranslator) {
         if (objecttranslator == null) i_translatorName = null;
         i_translator = objecttranslator;
      }
      
      internal void translateOnDemand(String xstring) {
         i_translatorName = xstring;
      }
      
      public void updateDepth(int i) {
         i_updateDepth = i;
      }
   }
}