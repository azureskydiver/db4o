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
namespace com.db4o.test.types {

   public class NtoNParent: RTest {
      
      public NtoNParent() : base() {
      }
      public NtoNChild[] children;
      
      public override void set(int ver) {
         children = new NtoNChild[3];
         for (int i = 0; i < 3; i++) {
            children[i] = new NtoNChild();
            children[i].parents = new NtoNParent[2];
            children[i].parents[0] = this;
            children[i].parents[1] = new NtoNParent();
            children[i].parents[1].children = new NtoNChild[1];
            children[i].parents[1].children[0] = children[i];
            children[i].name = "ver" + ver + "child" + i;
         }
      }
   }
}