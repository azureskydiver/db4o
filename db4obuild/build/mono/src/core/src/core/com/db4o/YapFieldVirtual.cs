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

   abstract internal class YapFieldVirtual : YapField {
      static internal String PREFIX = "v4o";
      
      internal YapFieldVirtual() : base(null) {
      }
      
      internal override void addFieldIndex(YapWriter yapwriter, bool xbool) {
         yapwriter.incrementOffset(this.linkLength());
      }
      
      internal override void delete(YapWriter yapwriter) {
         yapwriter.incrementOffset(this.linkLength());
      }
      
      internal override int ownLength(YapStream yapstream) {
         return yapstream.i_stringIo.shortLength(i_name);
      }
      
      internal void initIndex(YapStream yapstream, MetaIndex metaindex) {
         if (i_index == null) i_index = new IxField(yapstream.getSystemTransaction(), this, metaindex);
      }
      
      internal override void instantiate(YapObject yapobject, Object obj, YapWriter yapwriter) {
         if (yapobject.i_virtualAttributes == null) yapobject.i_virtualAttributes = new VirtualAttributes();
         instantiate1(yapwriter.getTransaction(), yapobject, yapwriter);
      }
      
      abstract internal void instantiate1(Transaction transaction, YapObject yapobject, YapReader yapreader);
      
      internal override void loadHandler(YapStream yapstream) {
      }
      
      internal override void marshall(YapObject yapobject, Object obj, YapWriter yapwriter, Config4Class config4class, bool xbool) {
         YapStream yapstream1 = yapwriter.i_trans.i_stream;
         bool bool_0_1 = false;
         if (yapstream1 is YapFile) {
            if (yapstream1.i_migrateFrom != null) {
               bool_0_1 = true;
               if (yapobject.i_virtualAttributes == null) {
                  YapObject yapobject_1_1 = yapstream1.i_migrateFrom.getYapObject(yapobject.getObject());
                  if (yapobject_1_1 != null && yapobject_1_1.i_virtualAttributes != null && yapobject_1_1.i_virtualAttributes.i_database != null) {
                     bool_0_1 = true;
                     yapobject.i_virtualAttributes = yapobject_1_1.i_virtualAttributes.shallowClone();
                     yapobject.i_virtualAttributes.i_database = yapstream1.i_handlers.ensureDb4oDatabase(yapwriter.getTransaction(), yapobject_1_1.i_virtualAttributes.i_database);
                  }
               }
            }
            if (yapobject.i_virtualAttributes == null) {
               yapobject.i_virtualAttributes = new VirtualAttributes();
               bool_0_1 = false;
            }
         } else bool_0_1 = true;
         marshall1(yapobject, yapwriter, bool_0_1, xbool);
      }
      
      abstract internal void marshall1(YapObject yapobject, YapWriter yapwriter, bool xbool, bool bool_2_);
      
      public override void readVirtualAttribute(Transaction transaction, YapReader yapreader, YapObject yapobject) {
         instantiate1(transaction, yapobject, yapreader);
      }
      
      internal override void writeThis(YapWriter yapwriter, YapClass yapclass) {
         yapwriter.writeShortString(i_name);
      }
   }
}