/* Copyright (C) 2009  db4objects Inc.  http://www.db4o.com */

using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4oUnit.Extensions;
using OManager.DataLayer.Reflection;
using Sharpen.Lang;
using Type=System.Type;

namespace OMNUnitTest
{
	public static class OMNUnitTestExtensions
	{
		public static IType NewGenericType(this Type type)
		{
			string databaseFileName = Path.GetTempFileName();
			StoreInstanceOf(databaseFileName, type);

			IEmbeddedConfiguration config2 = Db4oEmbedded.NewConfiguration();
			config2.Common.ReflectWith(new ExcludingReflector(new[] { type }));
			using (IObjectContainer db = Db4oEmbedded.OpenFile(config2, databaseFileName))
			{
				TypeResolver excludingResolver = new TypeResolver(db.Ext().Reflector());
				return excludingResolver.Resolve(TypeReference.FromType(type).GetUnversionedName());
			}
		}

		private static void StoreInstanceOf(string databaseFileName, Type type)
		{
			IEmbeddedConfiguration config = Db4oEmbedded.NewConfiguration();
			using (IObjectContainer db = Db4oEmbedded.OpenFile(config, databaseFileName))
			{
				db.Store(Activator.CreateInstance(type));
			}
		}
	}
}
