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

   internal class YapConst {
      
      internal YapConst() : base() {
      }
      static internal Object initMe = init();
      static internal byte YAPFILEVERSION = 4;
      static internal byte YAPBEGIN = 123;
      static internal byte YAPFILE = 89;
      static internal byte YAPID = 35;
      static internal byte YAPPOINTER = 62;
      static internal byte YAPCLASSCOLLECTION = 65;
      static internal byte YAPCLASS = 67;
      static internal byte YAPFIELD = 70;
      static internal byte YAPOBJECT = 79;
      static internal byte YAPARRAY = 78;
      static internal byte YAPARRAYN = 90;
      static internal byte YAPINDEX = 88;
      static internal byte YAPSTRING = 83;
      static internal byte YAPLONG = 108;
      static internal byte YAPINTEGER = 105;
      static internal byte YAPBOOLEAN = 61;
      static internal byte YAPDOUBLE = 100;
      static internal byte YAPBYTE = 98;
      static internal byte YAPSHORT = 115;
      static internal byte YAPCHAR = 99;
      static internal byte YAPFLOAT = 102;
      static internal byte YAPEND = 125;
      static internal byte YAPNULL = 48;
      static internal int IDENTIFIER_LENGTH = 0;
      static internal int BRACKETS_BYTES = 0;
      static internal int BRACKETS_LENGTH = 0;
      static internal int LEADING_LENGTH = 0;
      static internal int ADDED_LENGTH = 0;
      static internal int SHORT_BYTES = 2;
      static internal int INTEGER_BYTES = 4;
      static internal int LONG_BYTES = 8;
      static internal int CHAR_BYTES = 2;
      static internal int UNSPECIFIED = -2147483548;
      static internal int YAPINT_LENGTH = 4;
      static internal int YAPID_LENGTH = 4;
      static internal int YAPLONG_LENGTH = 8;
      static internal int WRITE_LOOP = 24;
      static internal int OBJECT_LENGTH = 0;
      static internal int POINTER_LENGTH = 8;
      static internal int MESSAGE_LENGTH = 9;
      static internal byte SYSTEM_TRANS = 115;
      static internal byte USER_TRANS = 117;
      static internal byte XBYTE = 88;
      static internal int IGNORE_ID = -99999;
      static internal int PRIMITIVE = -2000000000;
      static internal int TYPE_SIMPLE = 1;
      static internal int TYPE_CLASS = 2;
      static internal int TYPE_ARRAY = 3;
      static internal int TYPE_NARRAY = 4;
      static internal int NONE = 0;
      static internal int STATE = 1;
      static internal int ACTIVATION = 2;
      static internal int TRANSIENT = -1;
      static internal int ADD_MEMBERS_TO_ID_TREE_ONLY = 0;
      static internal int ADD_TO_ID_TREE = 1;
      static internal byte ISO8859 = 1;
      static internal byte UNICODE = 2;
      static internal int LOCK_TIME_INTERVAL = 1000;
      static internal int SERVER_SOCKET_TIMEOUT = 5000;
      static internal int CLIENT_SOCKET_TIMEOUT = 300000;
      static internal int CONNECTION_TIMEOUT = 180000;
      static internal int PREFETCH_ID_COUNT = 10;
      static internal int PREFETCH_OBJECT_COUNT = 10;
      static internal int MAXIMUM_BLOCK_SIZE = 70000000;
      static internal int MAXIMUM_ARRAY_ENTRIES = 7000000;
      static internal int MAXIMUM_ARRAY_ENTRIES_PRIMITIVE = 700000000;
      static internal Class CLASS_CLASS;
      static internal Class CLASS_COMPARE;
      static internal Class CLASS_DB4ODATABASE;
      static internal Class CLASS_DB4OTYPE;
      static internal Class CLASS_DB4OTYPEIMPL;
      static internal Class CLASS_ENUM;
      static internal Class CLASS_INTERNAL;
      static internal Class CLASS_METACLASS;
      static internal Class CLASS_METAFIELD;
      static internal Class CLASS_METAINDEX;
      static internal Class CLASS_OBJECT;
      static internal Class CLASS_OBJECTCONTAINER;
      static internal Class CLASS_PBOOTRECORD;
      static internal Class CLASS_REPLICATIONRECORD;
      static internal Class CLASS_STATICFIELD;
      static internal Class CLASS_STATICCLASS;
      static internal Class CLASS_TRANSIENTCLASS;
      static internal String EMBEDDED_CLIENT_USER = "embedded client";
      static internal int CLEAN = 0;
      static internal int ACTIVE = 1;
      static internal int PROCESSING = 2;
      static internal int CACHED_DIRTY = 3;
      static internal int CONTINUE = 4;
      static internal int STATIC_FIELDS_STORED = 5;
      static internal int CHECKED_CHANGES = 6;
      static internal int DEAD = 7;
      static internal int READING = 8;
      static internal int UNCHECKED = 0;
      static internal int NO = -1;
      static internal int YES = 1;
      static internal int DEFAULT = 0;
      public static YapStringIOUnicode stringIO = new YapStringIOUnicode();
      static internal Class[] ESSENTIAL_CLASSES = {
         CLASS_METAINDEX,
CLASS_METAFIELD,
CLASS_METACLASS,
CLASS_STATICFIELD,
CLASS_STATICCLASS      };
      
      public static RuntimeException virtualException() {
         return new RuntimeException();
      }
      
      private static Object init() {
         CLASS_OBJECT = j4o.lang.Class.getClassForObject(new Object());
         CLASS_CLASS = j4o.lang.Class.getClassForObject(CLASS_OBJECT);
         CLASS_COMPARE = db4oClass("config.Compare");
         CLASS_DB4ODATABASE = j4o.lang.Class.getClassForObject(new Db4oDatabase());
         CLASS_DB4OTYPE = db4oClass("types.Db4oType");
         CLASS_DB4OTYPEIMPL = db4oClass("Db4oTypeImpl");
         CLASS_ENUM = classForName("java.lang.Enum");
         CLASS_INTERNAL = db4oClass("Internal");
         CLASS_METACLASS = j4o.lang.Class.getClassForObject(new MetaClass());
         CLASS_METAFIELD = j4o.lang.Class.getClassForObject(new MetaField());
         CLASS_METAINDEX = j4o.lang.Class.getClassForObject(new MetaIndex());
         CLASS_OBJECTCONTAINER = db4oClass("ObjectContainer");
         CLASS_PBOOTRECORD = j4o.lang.Class.getClassForObject(new PBootRecord());
         CLASS_REPLICATIONRECORD = j4o.lang.Class.getClassForObject(new ReplicationRecord());
         CLASS_STATICFIELD = j4o.lang.Class.getClassForObject(new StaticField());
         CLASS_STATICCLASS = j4o.lang.Class.getClassForObject(new StaticClass());
         CLASS_TRANSIENTCLASS = db4oClass("types.TransientClass");
         return null;
      }
      
      private static Class db4oClass(String xstring) {
         return classForName("com.db4o." + xstring);
      }
      
      private static Class classForName(String xstring) {
         try {
            {
               return Class.forName(xstring);
            }
         }  catch (Exception exception) {
            {
               return null;
            }
         }
      }
   }
}