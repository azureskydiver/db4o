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
using j4o.io;
using j4o.lang.reflect;
using com.db4o.types;
namespace com.db4o {

   public class JDK {
      
      public JDK() : base() {
      }
      
      internal Thread addShutdownHook(Runnable runnable) {
         return null;
      }
      
      internal int collectionUpdateDepth(Class var_class) {
         return 0;
      }
      
      internal Db4oCollections collections(YapStream yapstream) {
         return null;
      }
      
      internal Object createReferenceQueue() {
         return null;
      }
      
      internal YapRef createYapRef(Object obj, YapObject yapobject, Object obj_0_) {
         return null;
      }
      
      internal void flattenCollection2(Object obj, Collection4 collection4) {
      }
      
      internal void forEachCollectionElement(Object obj, Visitor4 visitor4) {
      }
      
      internal ClassLoader getContextClassLoader() {
         return null;
      }
      
      internal Object getYapRefObject(Object obj) {
         return null;
      }
      
      internal bool isCollection(Class var_class) {
         return false;
      }
      
      public int ver() {
         return 1;
      }
      
      internal void killYapRef(Object obj) {
      }
      
      internal void Lock(RandomAccessFile randomaccessfile) {
      }
      
      internal void pollReferenceQueue(YapStream yapstream, Object obj) {
      }
      
      internal void removeShutdownHook(Thread thread) {
      }
      
      internal Constructor serializableConstructor(Class var_class) {
         return null;
      }
      
      internal void setAccessible(Object obj) {
      }
      
      internal bool storeStaticFieldValues(Class var_class) {
         return false;
      }
      
      internal void unlock(RandomAccessFile randomaccessfile) {
      }
   }
}