/* Copyright (C) 2009  Versant Inc.  http://www.db4o.com */
using System;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Reflect.Net;

namespace Db4objects.Db4o.Internal.Convert.Conversions
{
	public partial class DropGuidClassAndFieldIndexes_7_12 : DropClassIndexesConversion
	{
		private readonly FieldReindexer<Guid> reindexer = new FieldReindexer<Guid>();

		protected override bool Accept(ClassMetadata classmetadata)
		{
			var isGuid = NetReflector.ToNative(classmetadata.ClassReflector()) == typeof (Guid);
			if (!isGuid)
			{
				classmetadata.TraverseDeclaredFields(reindexer);
			}
			return isGuid;
		}

		private class FieldReindexer<T> : IProcedure4 where T : struct
		{
			public void Apply(object field)
			{
				if (!((FieldMetadata)field).HasIndex())
				{
					return;
				}
				ReindexDateTimeField(((FieldMetadata)field));
			}

			private static void ReindexDateTimeField(IStoredField field)
			{
				var claxx = field.GetStoredType();
				if (claxx == null)
				{
					return;
				}

				var t = NetReflector.ToNative(claxx);
				if (t == typeof(T) || t == typeof(T?))
				{
					field.DropIndex();
					field.CreateIndex();
				}
			}
		}
	}
}
