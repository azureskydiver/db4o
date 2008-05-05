/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Delete;
using Db4objects.Db4o.Internal.Fieldhandlers;
using Db4objects.Db4o.Marshall;

namespace Db4objects.Db4o.Internal
{
	/// <exclude></exclude>
	public interface ITypeHandler4 : IComparable4, IFieldHandler
	{
		/// <exception cref="Db4oIOException"></exception>
		void Delete(IDeleteContext context);

		void Defragment(IDefragmentContext context);

		object Read(IReadContext context);

		void Write(IWriteContext context, object obj);
	}
}
