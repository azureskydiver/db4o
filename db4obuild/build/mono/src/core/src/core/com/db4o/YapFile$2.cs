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

   internal class YapFile__2 : Visitor4 {
      private Tree[] val__duplicates;
      private QResult val__a_res;
      private YapFile stathis0;
      
      internal YapFile__2(YapFile yapfile, Tree[] trees, QResult qresult) : base() {
         stathis0 = yapfile;
         val__duplicates = trees;
         val__a_res = qresult;
      }
      
      public void visit(Object obj) {
         int i1 = ((TreeInt)obj).i_key;
         TreeInt treeint1 = new TreeInt(i1);
         val__duplicates[0] = Tree.add(val__duplicates[0], treeint1);
         if (treeint1.i_size != 0) val__a_res.add(i1);
      }
   }
}