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

   internal class Slot : ReadWriteable {
      internal int i_address;
      internal int i_length;
      internal int i_references;
      
      internal Slot(int i, int i_0_) : base() {
         i_address = i;
         i_length = i_0_;
      }
      
      public int byteCount() {
         return 8;
      }
      
      public void write(YapWriter yapwriter) {
         yapwriter.writeInt(i_address);
         yapwriter.writeInt(i_length);
      }
      
      public Object read(YapReader yapreader) {
         int i1 = yapreader.readInt();
         int i_1_1 = yapreader.readInt();
         return new Slot(i1, i_1_1);
      }
   }
}