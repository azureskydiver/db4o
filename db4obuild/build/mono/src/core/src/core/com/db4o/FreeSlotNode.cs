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

   internal class FreeSlotNode : TreeInt {
      static internal int sizeLimit;
      internal FreeSlotNode i_peer;
      
      internal FreeSlotNode(int i) : base(i) {
      }
      
      internal void createPeer(int i) {
         i_peer = new FreeSlotNode(i);
         i_peer.i_peer = this;
      }
      
      internal override bool duplicates() {
         return true;
      }
      
      internal override int ownLength() {
         return 8;
      }
      
      static internal Tree removeGreaterOrEqual(FreeSlotNode freeslotnode, TreeIntObject treeintobject) {
         if (freeslotnode == null) return null;
         int i1 = freeslotnode.i_key - treeintobject.i_key;
         if (i1 == 0) {
            treeintobject.i_object = freeslotnode;
            return freeslotnode.remove();
         }
         if (i1 > 0) {
            freeslotnode.i_preceding = removeGreaterOrEqual((FreeSlotNode)freeslotnode.i_preceding, treeintobject);
            if (treeintobject.i_object != null) {
               freeslotnode.i_size--;
               return freeslotnode;
            }
            treeintobject.i_object = freeslotnode;
            return freeslotnode.remove();
         }
         freeslotnode.i_subsequent = removeGreaterOrEqual((FreeSlotNode)freeslotnode.i_subsequent, treeintobject);
         if (treeintobject.i_object != null) freeslotnode.i_size--;
         return freeslotnode;
      }
      
      public override Object read(YapReader yapreader) {
         int i1 = yapreader.readInt();
         int i_0_1 = yapreader.readInt();
         if (i1 > sizeLimit) {
            FreeSlotNode freeslotnode_1_1 = new FreeSlotNode(i1);
            freeslotnode_1_1.createPeer(i_0_1);
            return freeslotnode_1_1;
         }
         return null;
      }
      
      public override void write(YapWriter yapwriter) {
         yapwriter.writeInt(i_key);
         yapwriter.writeInt(i_peer.i_key);
      }
   }
}