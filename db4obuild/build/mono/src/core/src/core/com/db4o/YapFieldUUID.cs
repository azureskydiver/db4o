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

   internal class YapFieldUUID : YapFieldVirtual {
      
      internal YapFieldUUID() : base() {
         i_name = "v4ouuid";
         i_handler = new YLong();
      }
      
      internal override void addFieldIndex(YapWriter yapwriter, bool xbool) {
         int i1 = yapwriter._offset;
         int i_0_1 = yapwriter.readInt();
         long l1 = YLong.readLong(yapwriter);
         yapwriter._offset = i1;
         YapFile yapfile1 = (YapFile)yapwriter.getStream();
         if (i_0_1 == 0) yapwriter.writeInt(yapfile1.identity().getID(yapfile1)); else yapwriter.incrementOffset(4);
         if (l1 == 0L) l1 = yapfile1.i_bootRecord.newUUID();
         YLong.writeLong(l1, yapwriter);
         if (xbool) this.addIndexEntry(System.Convert.ToInt64(l1), yapwriter);
      }
      
      internal override IxField getIndex(Transaction transaction) {
         YapFile yapfile1 = (YapFile)transaction.i_stream;
         if (i_index == null) {
            PBootRecord pbootrecord1 = yapfile1.i_bootRecord;
            i_index = new IxField(yapfile1.getSystemTransaction(), this, pbootrecord1.getUUIDMetaIndex());
         }
         return i_index;
      }
      
      internal override void instantiate1(Transaction transaction, YapObject yapobject, YapReader yapreader) {
         int i1 = yapreader.readInt();
         Db4oDatabase db4odatabase1 = (Db4oDatabase)transaction.i_stream.getByID2(transaction, i1);
         if (db4odatabase1 != null && db4odatabase1.i_signature == null) transaction.i_stream.activate2(transaction, db4odatabase1, 2);
         yapobject.i_virtualAttributes.i_database = db4odatabase1;
         yapobject.i_virtualAttributes.i_uuid = YLong.readLong(yapreader);
      }
      
      public override int linkLength() {
         return 12;
      }
      
      internal override void marshall1(YapObject yapobject, YapWriter yapwriter, bool xbool, bool bool_1_) {
         YapStream yapstream1 = yapwriter.getStream();
         bool bool_2_1 = bool_1_ && yapstream1.maintainsIndices();
         int i1 = 0;
         if (!xbool) {
            if (yapobject.i_virtualAttributes.i_database == null) {
               yapobject.i_virtualAttributes.i_database = yapstream1.identity();
               if (yapstream1 is YapFile && ((YapFile)yapstream1).i_bootRecord != null) {
                  PBootRecord pbootrecord1 = ((YapFile)yapstream1).i_bootRecord;
                  yapobject.i_virtualAttributes.i_uuid = pbootrecord1.newUUID();
                  bool_2_1 = true;
               }
            }
            Db4oDatabase db4odatabase1 = yapobject.i_virtualAttributes.i_database;
            if (db4odatabase1 != null) i1 = db4odatabase1.getID(yapstream1);
         } else {
            Object obj1 = null;
            if (yapobject.i_virtualAttributes != null && yapobject.i_virtualAttributes.i_database != null) {
               Db4oDatabase db4odatabase1 = yapobject.i_virtualAttributes.i_database;
               i1 = db4odatabase1.getID(yapstream1);
            }
         }
         yapwriter.writeInt(i1);
         if (yapobject.i_virtualAttributes != null) {
            YLong.writeLong(yapobject.i_virtualAttributes.i_uuid, yapwriter);
            if (bool_2_1) this.addIndexEntry(System.Convert.ToInt64(yapobject.i_virtualAttributes.i_uuid), yapwriter);
         } else YLong.writeLong(0L, yapwriter);
      }
   }
}