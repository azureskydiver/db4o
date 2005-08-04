/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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

		internal int elementActivationDepth() 
		{
			return i_activationDepth - 1;
		}
    }
}