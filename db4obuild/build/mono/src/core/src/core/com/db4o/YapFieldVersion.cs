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

   internal class YapFieldVersion : YapFieldVirtual {
      
      internal YapFieldVersion() : base() {
         i_name = "v4oversion";
      }
      
      internal override void addFieldIndex(YapWriter yapwriter, bool xbool) {
         YLong.writeLong(((YapFile)yapwriter.getStream()).i_bootRecord.version(), yapwriter);
      }
      
      internal override void instantiate1(Transaction transaction, YapObject yapobject, YapReader yapreader) {
         yapobject.i_virtualAttributes.i_version = YLong.readLong(yapreader);
      }
      
      internal override void marshall1(YapObject yapobject, YapWriter yapwriter, bool xbool, bool bool_0_) {
         if (!xbool) {
            YapStream yapstream1 = yapwriter.getStream().i_parent;
            if (yapstream1 is YapFile && ((YapFile)yapstream1).i_bootRecord != null) yapobject.i_virtualAttributes.i_version = ((YapFile)yapstream1).i_bootRecord.version();
         }
         if (yapobject.i_virtualAttributes == null) YLong.writeLong(0L, yapwriter); else YLong.writeLong(yapobject.i_virtualAttributes.i_version, yapwriter);
      }
      
      public override int linkLength() {
         return 8;
      }
   }
}