namespace com.db4o.@internal.marshall
{
	/// <exclude></exclude>
	public class MarshallerFamily
	{
		public class FamilyVersion
		{
			public const int PRE_MARSHALLER = 0;

			public const int MARSHALLER = 1;

			public const int BTREE_FIELD_INDEXES = 2;
		}

		private static int FAMILY_VERSION = com.db4o.@internal.marshall.MarshallerFamily.FamilyVersion
			.BTREE_FIELD_INDEXES;

		public readonly com.db4o.@internal.marshall.ArrayMarshaller _array;

		public readonly com.db4o.@internal.marshall.ClassMarshaller _class;

		public readonly com.db4o.@internal.marshall.FieldMarshaller _field;

		public readonly com.db4o.@internal.marshall.ObjectMarshaller _object;

		public readonly com.db4o.@internal.marshall.PrimitiveMarshaller _primitive;

		public readonly com.db4o.@internal.marshall.StringMarshaller _string;

		public readonly com.db4o.@internal.marshall.UntypedMarshaller _untyped;

		private readonly int _converterVersion;

		private static readonly com.db4o.@internal.marshall.MarshallerFamily[] allVersions
			 = new com.db4o.@internal.marshall.MarshallerFamily[] { new com.db4o.@internal.marshall.MarshallerFamily
			(0, new com.db4o.@internal.marshall.ArrayMarshaller0(), new com.db4o.@internal.marshall.ClassMarshaller0
			(), new com.db4o.@internal.marshall.FieldMarshaller0(), new com.db4o.@internal.marshall.ObjectMarshaller0
			(), new com.db4o.@internal.marshall.PrimitiveMarshaller0(), new com.db4o.@internal.marshall.StringMarshaller0
			(), new com.db4o.@internal.marshall.UntypedMarshaller0()), new com.db4o.@internal.marshall.MarshallerFamily
			(com.db4o.@internal.convert.conversions.ClassIndexesToBTrees_5_5.VERSION, new com.db4o.@internal.marshall.ArrayMarshaller1
			(), new com.db4o.@internal.marshall.ClassMarshaller1(), new com.db4o.@internal.marshall.FieldMarshaller0
			(), new com.db4o.@internal.marshall.ObjectMarshaller1(), new com.db4o.@internal.marshall.PrimitiveMarshaller1
			(), new com.db4o.@internal.marshall.StringMarshaller1(), new com.db4o.@internal.marshall.UntypedMarshaller1
			()), new com.db4o.@internal.marshall.MarshallerFamily(com.db4o.@internal.convert.conversions.FieldIndexesToBTrees_5_7
			.VERSION, new com.db4o.@internal.marshall.ArrayMarshaller1(), new com.db4o.@internal.marshall.ClassMarshaller2
			(), new com.db4o.@internal.marshall.FieldMarshaller1(), new com.db4o.@internal.marshall.ObjectMarshaller1
			(), new com.db4o.@internal.marshall.PrimitiveMarshaller1(), new com.db4o.@internal.marshall.StringMarshaller1
			(), new com.db4o.@internal.marshall.UntypedMarshaller1()) };

		private MarshallerFamily(int converterVersion, com.db4o.@internal.marshall.ArrayMarshaller
			 arrayMarshaller, com.db4o.@internal.marshall.ClassMarshaller classMarshaller, com.db4o.@internal.marshall.FieldMarshaller
			 fieldMarshaller, com.db4o.@internal.marshall.ObjectMarshaller objectMarshaller, 
			com.db4o.@internal.marshall.PrimitiveMarshaller primitiveMarshaller, com.db4o.@internal.marshall.StringMarshaller
			 stringMarshaller, com.db4o.@internal.marshall.UntypedMarshaller untypedMarshaller
			)
		{
			_converterVersion = converterVersion;
			_array = arrayMarshaller;
			_array._family = this;
			_class = classMarshaller;
			_class._family = this;
			_field = fieldMarshaller;
			_object = objectMarshaller;
			_object._family = this;
			_primitive = primitiveMarshaller;
			_primitive._family = this;
			_string = stringMarshaller;
			_untyped = untypedMarshaller;
			_untyped._family = this;
		}

		public static com.db4o.@internal.marshall.MarshallerFamily Version(int n)
		{
			return allVersions[n];
		}

		public static com.db4o.@internal.marshall.MarshallerFamily Current()
		{
			if (FAMILY_VERSION < com.db4o.@internal.marshall.MarshallerFamily.FamilyVersion.BTREE_FIELD_INDEXES
				)
			{
				throw new System.InvalidOperationException("Using old marshaller versions to write database files is not supported, source code has been removed."
					);
			}
			return Version(FAMILY_VERSION);
		}

		public static com.db4o.@internal.marshall.MarshallerFamily ForConverterVersion(int
			 n)
		{
			com.db4o.@internal.marshall.MarshallerFamily result = allVersions[0];
			for (int i = 1; i < allVersions.Length; i++)
			{
				if (allVersions[i]._converterVersion > n)
				{
					return result;
				}
				result = allVersions[i];
			}
			return result;
		}
	}
}
