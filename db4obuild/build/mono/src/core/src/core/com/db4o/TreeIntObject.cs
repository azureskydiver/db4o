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

   internal class TreeIntObject : TreeInt {
      internal Object i_object;
      
      internal TreeIntObject(int i) : base(i) {
      }
      
      internal TreeIntObject(int i, Object obj) : base(i) {
         i_object = obj;
      }
      
      public override Object read(YapReader yapreader) {
         int i1 = yapreader.readInt();
         Object obj1 = null;
         if (i_object is Tree) obj1 = new TreeReader(yapreader, (Tree)i_object).read(); else obj1 = ((Readable)i_object).read(yapreader);
         return new TreeIntObject(i1, obj1);
      }
      
      public override void write(YapWriter yapwriter) {
         yapwriter.writeInt(i_key);
         if (i_object == null) yapwriter.writeInt(0); else if (i_object is Tree) Tree.write(yapwriter, (Tree)i_object); else ((ReadWriteable)i_object).write(yapwriter);
      }
      
      internal override int ownLength() {
         if (i_object == null) return 8;
         return 4 + ((Readable)i_object).byteCount();
      }
      
      internal override bool variableLength() {
         return true;
      }
   }
}