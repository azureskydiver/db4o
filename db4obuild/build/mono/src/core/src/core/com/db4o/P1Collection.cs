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
using System.Collections;
using j4o.lang;
using com.db4o.types;

namespace com.db4o {

    internal abstract class P1Collection : P1Object, Db4oCollection, Db4oTypeImpl {
      
        [Transient]
        internal int i_activationDepth = -1;

        [Transient]
        internal bool i_deleteRemoved;

        internal P1Collection() : base() {
        }
      
        public void activationDepth(int depth) {
            i_activationDepth = depth;
        }
      
        public void deleteRemoved(bool flag) {
            i_deleteRemoved = flag;
        }

        public IEnumerator GetEnumerator(){
            // This is a bit of a mess, because IDictionary has
            // two GetEnumerator signatures.
            return getEnumerator1();
        }

        protected abstract IEnumerator getEnumerator1();
    }
}