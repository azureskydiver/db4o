/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

namespace Db4objects.Db4o.Foundation
{
	public interface ISequence4
	{
		void Add(object element);

		bool IsEmpty();
	}
}
