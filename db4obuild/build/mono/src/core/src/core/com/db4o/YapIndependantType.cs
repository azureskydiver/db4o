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

   abstract internal class YapIndependantType : YapDataType {
      
      internal YapIndependantType() : base() {
      }
      internal YapWriter i_lastIo;
      
      public void copyValue(Object obj, Object obj_0_) {
      }
      
      public virtual void deleteEmbedded(YapWriter yapwriter) {
         int i1 = yapwriter.readInt();
         int i_1_1 = yapwriter.readInt();
         if (i1 > 0) yapwriter.getTransaction().freeOnCommit(i1, i1, i_1_1);
      }
      
      public Class getPrimitiveJavaClass() {
         return null;
      }
      
      public Object readIndexObject(YapWriter yapwriter) {
         return read(yapwriter);
      }
      
      public Object indexEntry(Object obj) {
         if (obj == null) return null;
         return new int[]{
            i_lastIo.getAddress(),
i_lastIo.getLength()         };
      }
      
      public int linkLength() {
         return 8;
      }
      
      public abstract void writeIndexEntry(YapWriter yapwriter, Object obj);
      
      public abstract YapDataType readArrayWrapper(Transaction transaction, YapReader[] yapreaders);
      
      public abstract Object readIndexEntry(YapReader yapreader);
      
      public abstract void readCandidates(YapReader yapreader, QCandidates qcandidates);
      
      public abstract YapClass getYapClass(YapStream yapstream);
      
      public abstract int getType();
      
      public abstract void writeNew(Object obj, YapWriter yapwriter);
      
      public abstract bool supportsIndex();
      
      public abstract Object readQuery(Transaction transaction, YapReader yapreader, bool xbool);
      
      public abstract Object read(YapWriter yapwriter);
      
      public abstract void prepareLastIoComparison(Transaction transaction, Object obj);
      
      public abstract Object indexObject(Transaction transaction, Object obj);
      
      public abstract bool Equals(YapDataType yapdatatype);
      
      public abstract Class getJavaClass();
      
      public abstract int getID();
      
      public abstract void cascadeActivation(Transaction transaction, Object obj, int i, bool xbool);
      
      public abstract bool canHold(Class var_class);
      
      public abstract void appendEmbedded3(YapWriter yapwriter);
      
      public abstract bool isSmaller(Object obj);
      
      public abstract bool isGreater(Object obj);
      
      public abstract bool isEqual(Object obj);
      
      public abstract int compareTo(Object obj);
      
      public abstract YapComparable prepareComparison(Object obj);
   }
}