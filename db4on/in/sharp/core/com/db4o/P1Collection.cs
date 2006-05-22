/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System.Collections;
using com.db4o.types;

namespace com.db4o
{
	internal abstract class P1Collection : P1Object, Db4oCollection, Db4oTypeImpl
	{
		[Transient]
		internal int i_activationDepth = -1;

		[Transient]
		internal bool i_deleteRemoved;

		internal P1Collection()
			: base()
		{
		}

		public void ActivationDepth(int depth)
		{
			i_activationDepth = depth;
		}

		public void DeleteRemoved(bool flag)
		{
			i_deleteRemoved = flag;
		}

		public IEnumerator GetEnumerator()
		{
			// This is a bit of a mess, because IDictionary has
			// two GetEnumerator signatures.
			return GetEnumerator1();
		}

		protected abstract IEnumerator GetEnumerator1();

		internal int ElementActivationDepth()
		{
			return i_activationDepth - 1;
		}
	}
}