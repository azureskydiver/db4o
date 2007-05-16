/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Internal.Btree;
using Db4objects.Db4o.Internal.Fieldindex;

namespace Db4objects.Db4o.Internal.Fieldindex
{
	public interface IIndexedNodeWithRange : IIndexedNode
	{
		IBTreeRange GetRange();
	}
}
