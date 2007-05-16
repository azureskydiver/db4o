/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Internal;

namespace Db4objects.Db4o.Internal.Marshall
{
	/// <exclude></exclude>
	public abstract class ObjectHeaderAttributes
	{
		public abstract void AddBaseLength(int length);

		public abstract void AddPayLoadLength(int length);

		public abstract void PrepareIndexedPayLoadEntry(Transaction trans);
	}
}
