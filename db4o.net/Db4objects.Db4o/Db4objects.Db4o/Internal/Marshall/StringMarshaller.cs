/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Internal;

namespace Db4objects.Db4o.Internal.Marshall
{
	public abstract class StringMarshaller
	{
		public abstract bool InlinedStrings();

		public abstract Db4objects.Db4o.Internal.Buffer ReadIndexEntry(StatefulBuffer parentSlot
			);

		public abstract void Defrag(ISlotBuffer reader);
	}
}
