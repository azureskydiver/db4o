/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o;
using Db4objects.Db4o.Internal;
using Db4objects.Drs.Db4o;

namespace Db4objects.Drs.Db4o
{
	/// <exclude></exclude>
	public class Db4oProviderFactory
	{
		public static IDb4oReplicationProvider NewInstance(IObjectContainer oc, string name
			)
		{
			if (IsClient(oc))
			{
				return new ClientServerReplicationProvider(oc, name);
			}
			else
			{
				return new FileReplicationProvider(oc, name);
			}
		}

		public static IDb4oReplicationProvider NewInstance(IObjectContainer oc)
		{
			return NewInstance(oc, null);
		}

		private static bool IsClient(IObjectContainer oc)
		{
			return ((IInternalObjectContainer)oc).IsClient();
		}
	}
}
