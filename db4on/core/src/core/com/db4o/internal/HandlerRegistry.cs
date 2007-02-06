namespace com.db4o.@internal
{
	/// <exclude>
	/// TODO: This class was written to make ObjectContainerBase
	/// leaner, so TransportObjectContainer has less members.
	/// All funcionality of this class should become part of
	/// ObjectContainerBase and the functionality in
	/// ObjectContainerBase should delegate to independant
	/// modules without circular references.
	/// </exclude>
	public sealed class HandlerRegistry
	{
		private readonly com.db4o.@internal.ObjectContainerBase _masterStream;

		private static readonly com.db4o.@internal.Db4oTypeImpl[] i_db4oTypes = { new com.db4o.@internal.BlobImpl
			() };

		public const int ANY_ARRAY_ID = 12;

		public const int ANY_ARRAY_N_ID = 13;

		private const int CLASSCOUNT = 11;

		private com.db4o.@internal.ClassMetadata i_anyArray;

		private com.db4o.@internal.ClassMetadata i_anyArrayN;

		public readonly com.db4o.@internal.handlers.StringHandler i_stringHandler;

		private com.db4o.@internal.TypeHandler4[] i_handlers;

		private int i_maxTypeID = ANY_ARRAY_N_ID + 1;

		private com.db4o.@internal.handlers.NetTypeHandler[] i_platformTypes;

		private const int PRIMITIVECOUNT = 8;

		internal com.db4o.@internal.ClassMetadata[] i_yapClasses;

		private const int ANY_INDEX = 10;

		public const int ANY_ID = 11;

		public readonly com.db4o.@internal.VirtualFieldMetadata[] i_virtualFields = new com.db4o.@internal.VirtualFieldMetadata
			[2];

		private readonly com.db4o.foundation.Hashtable4 i_classByClass = new com.db4o.foundation.Hashtable4
			(32);

		internal com.db4o.types.Db4oCollections i_collections;

		internal com.db4o.@internal.SharedIndexedFields i_indexes;

		internal com.db4o.ReplicationImpl i_replication;

		internal com.db4o.@internal.replication.MigrationConnection i_migration;

		internal com.db4o.@internal.replication.Db4oReplicationReferenceProvider _replicationReferenceProvider;

		public readonly com.db4o.@internal.diagnostic.DiagnosticProcessor _diagnosticProcessor;

		public bool i_encrypt;

		internal byte[] i_encryptor;

		internal int i_lastEncryptorByte;

		internal readonly com.db4o.reflect.generic.GenericReflector _reflector;

		public com.db4o.reflect.ReflectClass ICLASS_COMPARE;

		internal com.db4o.reflect.ReflectClass ICLASS_DB4OTYPE;

		internal com.db4o.reflect.ReflectClass ICLASS_DB4OTYPEIMPL;

		public com.db4o.reflect.ReflectClass ICLASS_INTERNAL;

		internal com.db4o.reflect.ReflectClass ICLASS_UNVERSIONED;

		public com.db4o.reflect.ReflectClass ICLASS_OBJECT;

		internal com.db4o.reflect.ReflectClass ICLASS_OBJECTCONTAINER;

		public com.db4o.reflect.ReflectClass ICLASS_STATICCLASS;

		public com.db4o.reflect.ReflectClass ICLASS_STRING;

		internal com.db4o.reflect.ReflectClass ICLASS_TRANSIENTCLASS;

		internal HandlerRegistry(com.db4o.@internal.ObjectContainerBase a_stream, byte stringEncoding
			, com.db4o.reflect.generic.GenericReflector reflector)
		{
			_masterStream = a_stream;
			a_stream.i_handlers = this;
			_reflector = reflector;
			_diagnosticProcessor = a_stream.ConfigImpl().DiagnosticProcessor();
			InitClassReflectors(reflector);
			i_indexes = new com.db4o.@internal.SharedIndexedFields(a_stream);
			i_virtualFields[0] = i_indexes.i_fieldVersion;
			i_virtualFields[1] = i_indexes.i_fieldUUID;
			i_stringHandler = new com.db4o.@internal.handlers.StringHandler(a_stream, com.db4o.@internal.LatinStringIO
				.ForEncoding(stringEncoding));
			i_handlers = new com.db4o.@internal.TypeHandler4[] { new com.db4o.@internal.handlers.IntHandler
				(a_stream), new com.db4o.@internal.handlers.LongHandler(a_stream), new com.db4o.@internal.handlers.FloatHandler
				(a_stream), new com.db4o.@internal.handlers.BooleanHandler(a_stream), new com.db4o.@internal.handlers.DoubleHandler
				(a_stream), new com.db4o.@internal.handlers.ByteHandler(a_stream), new com.db4o.@internal.handlers.CharHandler
				(a_stream), new com.db4o.@internal.handlers.ShortHandler(a_stream), i_stringHandler
				, new com.db4o.@internal.handlers.DateHandler(a_stream), new com.db4o.@internal.UntypedFieldHandler
				(a_stream) };
			i_platformTypes = com.db4o.@internal.Platform4.Types(a_stream);
			if (i_platformTypes.Length > 0)
			{
				for (int i = 0; i < i_platformTypes.Length; i++)
				{
					i_platformTypes[i].Initialize();
					if (i_platformTypes[i].GetID() > i_maxTypeID)
					{
						i_maxTypeID = i_platformTypes[i].GetID();
					}
				}
				com.db4o.@internal.TypeHandler4[] temp = i_handlers;
				i_handlers = new com.db4o.@internal.TypeHandler4[i_maxTypeID];
				System.Array.Copy(temp, 0, i_handlers, 0, temp.Length);
				for (int i = 0; i < i_platformTypes.Length; i++)
				{
					int idx = i_platformTypes[i].GetID() - 1;
					i_handlers[idx] = i_platformTypes[i];
				}
			}
			i_yapClasses = new com.db4o.@internal.ClassMetadata[i_maxTypeID + 1];
			for (int i = 0; i < CLASSCOUNT; i++)
			{
				int id = i + 1;
				i_yapClasses[i] = new com.db4o.@internal.PrimitiveFieldHandler(a_stream, i_handlers
					[i]);
				i_yapClasses[i].SetID(id);
				i_classByClass.Put(i_handlers[i].ClassReflector(), i_yapClasses[i]);
				if (i < ANY_INDEX)
				{
					reflector.RegisterPrimitiveClass(id, i_handlers[i].ClassReflector().GetName(), null
						);
				}
			}
			for (int i = 0; i < i_platformTypes.Length; i++)
			{
				int id = i_platformTypes[i].GetID();
				int idx = id - 1;
				com.db4o.reflect.generic.GenericConverter converter = (i_platformTypes[i] is com.db4o.reflect.generic.GenericConverter
					) ? (com.db4o.reflect.generic.GenericConverter)i_platformTypes[i] : null;
				reflector.RegisterPrimitiveClass(id, i_platformTypes[i].GetName(), converter);
				i_handlers[idx] = i_platformTypes[i];
				i_yapClasses[idx] = new com.db4o.@internal.PrimitiveFieldHandler(a_stream, i_platformTypes
					[i]);
				i_yapClasses[idx].SetID(id);
				if (id > i_maxTypeID)
				{
					i_maxTypeID = idx;
				}
				i_classByClass.Put(i_platformTypes[i].ClassReflector(), i_yapClasses[idx]);
			}
			i_anyArray = new com.db4o.@internal.PrimitiveFieldHandler(a_stream, new com.db4o.@internal.handlers.ArrayHandler
				(_masterStream, AnyObject(), false));
			i_anyArray.SetID(ANY_ARRAY_ID);
			i_yapClasses[ANY_ARRAY_ID - 1] = i_anyArray;
			i_anyArrayN = new com.db4o.@internal.PrimitiveFieldHandler(a_stream, new com.db4o.@internal.handlers.MultidimensionalArrayHandler
				(_masterStream, AnyObject(), false));
			i_anyArrayN.SetID(ANY_ARRAY_N_ID);
			i_yapClasses[ANY_ARRAY_N_ID - 1] = i_anyArrayN;
		}

		internal int ArrayType(object a_object)
		{
			com.db4o.reflect.ReflectClass claxx = _masterStream.Reflector().ForObject(a_object
				);
			if (!claxx.IsArray())
			{
				return 0;
			}
			if (_masterStream.Reflector().Array().IsNDimensional(claxx))
			{
				return com.db4o.@internal.Const4.TYPE_NARRAY;
			}
			return com.db4o.@internal.Const4.TYPE_ARRAY;
		}

		internal bool CreateConstructor(com.db4o.reflect.ReflectClass claxx, bool skipConstructor
			)
		{
			if (claxx == null)
			{
				return false;
			}
			if (claxx.IsAbstract() || claxx.IsInterface())
			{
				return true;
			}
			if (!com.db4o.@internal.Platform4.CallConstructor())
			{
				if (claxx.SkipConstructor(skipConstructor))
				{
					return true;
				}
			}
			if (!_masterStream.ConfigImpl().TestConstructors())
			{
				return true;
			}
			if (claxx.NewInstance() != null)
			{
				return true;
			}
			if (_masterStream.Reflector().ConstructorCallsSupported())
			{
				try
				{
					com.db4o.reflect.ReflectConstructor[] constructors = claxx.GetDeclaredConstructors
						();
					com.db4o.foundation.Tree sortedConstructors = null;
					for (int i = 0; i < constructors.Length; i++)
					{
						try
						{
							constructors[i].SetAccessible();
							int parameterCount = constructors[i].GetParameterTypes().Length;
							sortedConstructors = com.db4o.foundation.Tree.Add(sortedConstructors, new com.db4o.@internal.TreeIntObject
								(i + constructors.Length * parameterCount, constructors[i]));
						}
						catch (System.Exception t)
						{
						}
					}
					bool[] foundConstructor = { false };
					if (sortedConstructors != null)
					{
						com.db4o.@internal.TypeHandler4[] handlers = i_handlers;
						sortedConstructors.Traverse(new _AnonymousInnerClass241(this, foundConstructor, handlers
							, claxx));
					}
					if (foundConstructor[0])
					{
						return true;
					}
				}
				catch (System.Exception t1)
				{
				}
			}
			return false;
		}

		private sealed class _AnonymousInnerClass241 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass241(HandlerRegistry _enclosing, bool[] foundConstructor
				, com.db4o.@internal.TypeHandler4[] handlers, com.db4o.reflect.ReflectClass claxx
				)
			{
				this._enclosing = _enclosing;
				this.foundConstructor = foundConstructor;
				this.handlers = handlers;
				this.claxx = claxx;
			}

			public void Visit(object a_object)
			{
				if (!foundConstructor[0])
				{
					com.db4o.reflect.ReflectConstructor constructor = (com.db4o.reflect.ReflectConstructor
						)((com.db4o.@internal.TreeIntObject)a_object)._object;
					try
					{
						com.db4o.reflect.ReflectClass[] pTypes = constructor.GetParameterTypes();
						object[] parms = new object[pTypes.Length];
						for (int j = 0; j < parms.Length; j++)
						{
							for (int k = 0; k < com.db4o.@internal.HandlerRegistry.PRIMITIVECOUNT; k++)
							{
								if (pTypes[j].Equals(handlers[k].PrimitiveClassReflector()))
								{
									parms[j] = ((com.db4o.@internal.handlers.PrimitiveHandler)handlers[k]).PrimitiveNull
										();
									break;
								}
							}
						}
						object res = constructor.NewInstance(parms);
						if (res != null)
						{
							foundConstructor[0] = true;
							claxx.UseConstructor(constructor, parms);
						}
					}
					catch (System.Exception t)
					{
					}
				}
			}

			private readonly HandlerRegistry _enclosing;

			private readonly bool[] foundConstructor;

			private readonly com.db4o.@internal.TypeHandler4[] handlers;

			private readonly com.db4o.reflect.ReflectClass claxx;
		}

		public void Decrypt(com.db4o.@internal.Buffer reader)
		{
			if (i_encrypt)
			{
				int encryptorOffSet = i_lastEncryptorByte;
				byte[] bytes = reader._buffer;
				for (int i = reader.GetLength() - 1; i >= 0; i--)
				{
					bytes[i] += i_encryptor[encryptorOffSet];
					if (encryptorOffSet == 0)
					{
						encryptorOffSet = i_lastEncryptorByte;
					}
					else
					{
						encryptorOffSet--;
					}
				}
			}
		}

		public void Encrypt(com.db4o.@internal.Buffer reader)
		{
			if (i_encrypt)
			{
				byte[] bytes = reader._buffer;
				int encryptorOffSet = i_lastEncryptorByte;
				for (int i = reader.GetLength() - 1; i >= 0; i--)
				{
					bytes[i] -= i_encryptor[encryptorOffSet];
					if (encryptorOffSet == 0)
					{
						encryptorOffSet = i_lastEncryptorByte;
					}
					else
					{
						encryptorOffSet--;
					}
				}
			}
		}

		public void OldEncryptionOff()
		{
			i_encrypt = false;
			i_encryptor = null;
			i_lastEncryptorByte = 0;
			_masterStream.ConfigImpl().OldEncryptionOff();
		}

		internal com.db4o.@internal.TypeHandler4 GetHandler(int a_index)
		{
			return i_handlers[a_index - 1];
		}

		internal com.db4o.@internal.TypeHandler4 HandlerForClass(com.db4o.reflect.ReflectClass
			 a_class, com.db4o.reflect.ReflectClass[] a_Supported)
		{
			for (int i = 0; i < a_Supported.Length; i++)
			{
				if (a_Supported[i].Equals(a_class))
				{
					return i_handlers[i];
				}
			}
			return null;
		}

		public com.db4o.@internal.TypeHandler4 HandlerForClass(com.db4o.@internal.ObjectContainerBase
			 a_stream, com.db4o.reflect.ReflectClass a_class)
		{
			if (a_class == null)
			{
				return null;
			}
			if (a_class.IsArray())
			{
				return HandlerForClass(a_stream, a_class.GetComponentType());
			}
			com.db4o.@internal.ClassMetadata yc = GetYapClassStatic(a_class);
			if (yc != null)
			{
				return ((com.db4o.@internal.PrimitiveFieldHandler)yc).i_handler;
			}
			return a_stream.ProduceYapClass(a_class);
		}

		private com.db4o.@internal.TypeHandler4 AnyObject()
		{
			return i_handlers[ANY_INDEX];
		}

		private void InitClassReflectors(com.db4o.reflect.generic.GenericReflector reflector
			)
		{
			ICLASS_COMPARE = reflector.ForClass(com.db4o.@internal.Const4.CLASS_COMPARE);
			ICLASS_DB4OTYPE = reflector.ForClass(com.db4o.@internal.Const4.CLASS_DB4OTYPE);
			ICLASS_DB4OTYPEIMPL = reflector.ForClass(com.db4o.@internal.Const4.CLASS_DB4OTYPEIMPL
				);
			ICLASS_INTERNAL = reflector.ForClass(com.db4o.@internal.Const4.CLASS_INTERNAL);
			ICLASS_UNVERSIONED = reflector.ForClass(com.db4o.@internal.Const4.CLASS_UNVERSIONED
				);
			ICLASS_OBJECT = reflector.ForClass(com.db4o.@internal.Const4.CLASS_OBJECT);
			ICLASS_OBJECTCONTAINER = reflector.ForClass(com.db4o.@internal.Const4.CLASS_OBJECTCONTAINER
				);
			ICLASS_STATICCLASS = reflector.ForClass(com.db4o.@internal.Const4.CLASS_STATICCLASS
				);
			ICLASS_STRING = reflector.ForClass(j4o.lang.JavaSystem.GetClassForType(typeof(string)
				));
			ICLASS_TRANSIENTCLASS = reflector.ForClass(com.db4o.@internal.Const4.CLASS_TRANSIENTCLASS
				);
			com.db4o.@internal.Platform4.RegisterCollections(reflector);
		}

		internal void InitEncryption(com.db4o.@internal.Config4Impl a_config)
		{
			if (a_config.Encrypt() && a_config.Password() != null && a_config.Password().Length
				 > 0)
			{
				i_encrypt = true;
				i_encryptor = new byte[a_config.Password().Length];
				for (int i = 0; i < i_encryptor.Length; i++)
				{
					i_encryptor[i] = (byte)(j4o.lang.JavaSystem.GetCharAt(a_config.Password(), i) & unchecked(
						(int)(0xff)));
				}
				i_lastEncryptorByte = a_config.Password().Length - 1;
				return;
			}
			OldEncryptionOff();
		}

		internal static com.db4o.@internal.Db4oTypeImpl GetDb4oType(com.db4o.reflect.ReflectClass
			 clazz)
		{
			for (int i = 0; i < i_db4oTypes.Length; i++)
			{
				if (clazz.IsInstance(i_db4oTypes[i]))
				{
					return i_db4oTypes[i];
				}
			}
			return null;
		}

		public com.db4o.@internal.ClassMetadata GetYapClassStatic(int a_id)
		{
			if (a_id > 0 && a_id <= i_maxTypeID)
			{
				return i_yapClasses[a_id - 1];
			}
			return null;
		}

		internal com.db4o.@internal.ClassMetadata GetYapClassStatic(com.db4o.reflect.ReflectClass
			 a_class)
		{
			if (a_class == null)
			{
				return null;
			}
			if (a_class.IsArray())
			{
				if (_masterStream.Reflector().Array().IsNDimensional(a_class))
				{
					return i_anyArrayN;
				}
				return i_anyArray;
			}
			return (com.db4o.@internal.ClassMetadata)i_classByClass.Get(a_class);
		}

		public bool IsSecondClass(object a_object)
		{
			if (a_object != null)
			{
				com.db4o.reflect.ReflectClass claxx = _masterStream.Reflector().ForObject(a_object
					);
				if (i_classByClass.Get(claxx) != null)
				{
					return true;
				}
				return com.db4o.@internal.Platform4.IsValueType(claxx);
			}
			return false;
		}

		public bool IsSystemHandler(int id)
		{
			return id <= i_maxTypeID;
		}

		public void MigrationConnection(com.db4o.@internal.replication.MigrationConnection
			 mgc)
		{
			i_migration = mgc;
		}

		public com.db4o.@internal.replication.MigrationConnection MigrationConnection()
		{
			return i_migration;
		}

		public void Replication(com.db4o.ReplicationImpl impl)
		{
			i_replication = impl;
		}

		public com.db4o.ReplicationImpl Replication()
		{
			return i_replication;
		}

		public com.db4o.@internal.ClassMetadata PrimitiveClassById(int id)
		{
			return i_yapClasses[id - 1];
		}
	}
}
