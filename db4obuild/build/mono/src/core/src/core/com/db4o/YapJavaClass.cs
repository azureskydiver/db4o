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

   abstract internal class YapJavaClass : YapDataType {
      
      internal YapJavaClass() : base() {
      }
      private bool i_compareToIsNull;
      
      public void appendEmbedded3(YapWriter yapwriter) {
         yapwriter.incrementOffset(linkLength());
      }
      
      public bool canHold(Class var_class) {
         return getJavaClass() == var_class;
      }
      
      public void cascadeActivation(Transaction transaction, Object obj, int i, bool xbool) {
      }
      
      public virtual void copyValue(Object obj, Object obj_0_) {
      }
      
      public void deleteEmbedded(YapWriter yapwriter) {
         yapwriter.incrementOffset(linkLength());
      }
      
      public bool Equals(YapDataType yapdatatype) {
         return this == yapdatatype;
      }
      
      public virtual Class getJavaClass() {
         return j4o.lang.Class.getClassForObject(primitiveNull());
      }
      
      public virtual Class getPrimitiveJavaClass() {
         return getJavaClass();
      }
      
      public int getType() {
         return 1;
      }
      
      public YapClass getYapClass(YapStream yapstream) {
         return yapstream.i_handlers.i_yapClasses[getID() - 1];
      }
      
      public Object indexEntry(Object obj) {
         return obj;
      }
      
      public Object indexObject(Transaction transaction, Object obj) {
         return obj;
      }
      
      public void prepareLastIoComparison(Transaction transaction, Object obj) {
         prepareComparison(obj);
      }
      
      abstract internal Object primitiveNull();
      
      public YapDataType readArrayWrapper(Transaction transaction, YapReader[] yapreaders) {
         return null;
      }
      
      public Object readQuery(Transaction transaction, YapReader yapreader, bool xbool) {
         return read1(yapreader);
      }
      
      public Object read(YapWriter yapwriter) {
         return read1(yapwriter);
      }
      
      abstract internal Object read1(YapReader yapreader);
      
      public void readCandidates(YapReader yapreader, QCandidates qcandidates) {
      }
      
      public Object readIndexEntry(YapReader yapreader) {
         try {
            {
               return read1(yapreader);
            }
         }  catch (CorruptionException corruptionexception) {
            {
               return null;
            }
         }
      }
      
      public Object readIndexObject(YapWriter yapwriter) {
         return read(yapwriter);
      }
      
      public bool supportsIndex() {
         return true;
      }
      
      public abstract void write(Object obj, YapWriter yapwriter);
      
      public void writeIndexEntry(YapWriter yapwriter, Object obj) {
         write(obj, yapwriter);
      }
      
      public void writeNew(Object obj, YapWriter yapwriter) {
         if (obj == null) obj = primitiveNull();
         write(obj, yapwriter);
      }
      
      public YapComparable prepareComparison(Object obj) {
         if (obj == null) {
            i_compareToIsNull = true;
            return Null.INSTANCE;
         }
         i_compareToIsNull = false;
         prepareComparison1(obj);
         return this;
      }
      
      abstract internal void prepareComparison1(Object obj);
      
      public int compareTo(Object obj) {
         if (i_compareToIsNull) {
            if (obj == null) return 0;
            return 1;
         }
         if (obj == null) return -1;
         if (isEqual1(obj)) return 0;
         if (isGreater1(obj)) return 1;
         return -1;
      }
      
      public bool isEqual(Object obj) {
         if (i_compareToIsNull) return obj == null;
         return isEqual1(obj);
      }
      
      abstract internal bool isEqual1(Object obj);
      
      public bool isGreater(Object obj) {
         if (i_compareToIsNull) return obj != null;
         return isGreater1(obj);
      }
      
      abstract internal bool isGreater1(Object obj);
      
      public bool isSmaller(Object obj) {
         if (i_compareToIsNull) return false;
         return isSmaller1(obj);
      }
      
      abstract internal bool isSmaller1(Object obj);
      
      public abstract int linkLength();
      
      public abstract int getID();
   }
}