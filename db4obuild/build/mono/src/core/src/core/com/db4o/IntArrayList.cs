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

   internal class IntArrayList {
      
      internal IntArrayList() : base() {
      }
      static internal int INC = 20;
      private int[] i_content = new int[20];
      private int i_current;
      private int i_count;
      
      internal void add(int i) {
         if (i_count >= i_content.Length) {
            int[] xis1 = new int[i_content.Length + 20];
            j4o.lang.JavaSystem.arraycopy(i_content, 0, xis1, 0, i_content.Length);
            i_content = xis1;
         }
         i_content[i_count++] = i;
      }
      
      public int size() {
         return i_count;
      }
      
      public virtual void reset() {
         i_current = i_count - 1;
      }
      
      public virtual bool hasNext() {
         return i_current >= 0;
      }
      
      public int nextInt() {
         return i_content[i_current--];
      }
      
      public long[] asLong() {
         long[] ls1 = new long[i_count];
         for (int i1 = 0; i1 < i_count; i1++) ls1[i1] = (long)i_content[i1];
         return ls1;
      }
   }
}