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

   internal class TreeReader {
      private Readable i_template;
      private YapReader i_bytes;
      private int i_current = 0;
      private int i_levels = 0;
      private int i_size;
      private bool i_orderOnRead = false;
      
      internal TreeReader(YapReader yapreader, Readable readable) : base() {
         i_template = readable;
         i_bytes = yapreader;
      }
      
      internal TreeReader(YapReader yapreader, Readable readable, bool xbool) : this(yapreader, readable) {
         i_orderOnRead = xbool;
      }
      
      public Tree read() {
         return read(i_bytes.readInt());
      }
      
      public Tree read(int i) {
         i_size = i;
         if (i_size > 0) {
            if (i_orderOnRead) {
               Tree tree1 = null;
               for (int i_0_1 = 0; i_0_1 < i_size; i_0_1++) tree1 = Tree.add(tree1, (Tree)i_template.read(i_bytes));
               return tree1;
            }
            for (; 1 << i_levels < i_size + 1; i_levels++) {
            }
            return linkUp(null, i_levels);
         }
         return null;
      }
      
      private Tree linkUp(Tree tree, int i) {
         Tree tree_1_1 = (Tree)i_template.read(i_bytes);
         i_current++;
         tree_1_1.i_preceding = tree;
         tree_1_1.i_subsequent = linkDown(i + 1);
         tree_1_1.calculateSize();
         if (i_current < i_size) return linkUp(tree_1_1, i - 1);
         return tree_1_1;
      }
      
      private Tree linkDown(int i) {
         if (i_current < i_size) {
            i_current++;
            if (i < i_levels) {
               Tree tree1 = linkDown(i + 1);
               Tree tree_2_1 = (Tree)i_template.read(i_bytes);
               tree_2_1.i_preceding = tree1;
               tree_2_1.i_subsequent = linkDown(i + 1);
               tree_2_1.calculateSize();
               return tree_2_1;
            }
            return (Tree)i_template.read(i_bytes);
         }
         return null;
      }
   }
}