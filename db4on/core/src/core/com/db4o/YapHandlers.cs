namespace com.db4o
{
	/// <exclude></exclude>
	public sealed class YapHandlers
	{
		private readonly com.db4o.YapStream _masterStream;

		private static readonly com.db4o.Db4oTypeImpl[] i_db4oTypes = { new com.db4o.BlobImpl
			() };

		public const int ANY_ARRAY_ID = 12;

		public const int ANY_ARRAY_N_ID = 13;

		private const int CLASSCOUNT = 11;

		private com.db4o.YapClass i_anyArray;

		private com.db4o.YapClass i_anyArrayN;

		public readonly com.db4o.YapString i_stringHandler;

		private com.db4o.TypeHandler4[] i_handlers;

		private int i_maxTypeID = ANY_ARRAY_N_ID + 1;

		private com.db4o.YapTypeAbstract[] i_platformTypes;

		private const int PRIMITIVECOUNT = 8;

		internal com.db4o.YapClass[] i_yapClasses;

		private const int ANY_INDEX = 10;

		public const int ANY_ID = 11;

		public readonly com.db4o.YapFieldVirtual[] i_virtualFields = new com.db4o.YapFieldVirtual
			[2];

		private readonly com.db4o.foundation.Hashtable4 i_classByClass = new com.db4o.foundation.Hashtable4
			(32);

		internal com.db4o.types.Db4oCollections i_collections;

		internal com.db4o.YapIndexes i_indexes;

		internal com.db4o.ReplicationImpl i_replication;

		internal com.db4o.inside.replication.MigrationConnection i_migration;

		internal com.db4o.inside.replication.Db4oReplicationReferenceProvider _replicationReferenceProvider;

		public readonly com.db4o.inside.diagnostic.DiagnosticProcessor _diagnosticProcessor;

		internal bool i_encrypt;

		internal byte[] i_encryptor;

		internal int i_lastEncryptorByte;

		internal readonly com.db4o.reflect.generic.GenericReflector _reflector;

		internal com.db4o.reflect.ReflectClass ICLASS_COMPARE;

		internal com.db4o.reflect.ReflectClass ICLASS_DB4OTYPE;

		internal com.db4o.reflect.ReflectClass ICLASS_DB4OTYPEIMPL;

		public com.db4o.reflect.ReflectClass ICLASS_INTERNAL;

		internal com.db4o.reflect.ReflectClass ICLASS_UNVERSIONED;

		internal com.db4o.reflect.ReflectClass ICLASS_OBJECT;

		internal com.db4o.reflect.ReflectClass ICLASS_OBJECTCONTAINER;

		internal com.db4o.reflect.ReflectClass ICLASS_STATICCLASS;

		internal com.db4o.reflect.ReflectClass ICLASS_STRING;

		internal com.db4o.reflect.ReflectClass ICLASS_TRANSIENTCLASS;

		internal YapHandlers(com.db4o.YapStream a_stream, byte stringEncoding, com.db4o.reflect.generic.GenericReflector
			 reflector)
		{
			_masterStream = a_stream;
			a_stream.i_handlers = this;
			_reflector = reflector;
			_diagnosticProcessor = a_stream.ConfigImpl().DiagnosticProcessor();
			InitClassReflectors(reflector);
			i_indexes = new com.db4o.YapIndexes(a_stream);
			i_virtualFields[0] = i_indexes.i_fieldVersion;
			i_virtualFields[1] = i_indexes.i_fieldUUID;
			i_stringHandler = new com.db4o.YapString(a_stream, com.db4o.YapStringIO.ForEncoding
				(stringEncoding));
			i_handlers = new com.db4o.TypeHandler4[] { new com.db4o.YInt(a_stream), new com.db4o.YLong
				(a_stream), new com.db4o.YFloat(a_stream), new com.db4o.YBoolean(a_stream), new 
				com.db4o.YDouble(a_stream), new com.db4o.YByte(a_stream), new com.db4o.YChar(a_stream
				), new com.db4o.YShort(a_stream), i_stringHandler, new com.db4o.YDate(a_stream), 
				new com.db4o.YapClassAny(a_stream) };
			i_platformTypes = com.db4o.Platform4.Types(a_stream);
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
				com.db4o.TypeHandler4[] temp = i_handlers;
				i_handlers = new com.db4o.TypeHandler4[i_maxTypeID];
				System.Array.Copy(temp, 0, i_handlers, 0, temp.Length);
				for (int i = 0; i < i_platformTypes.Length; i++)
				{
					int idx = i_platformTypes[i].GetID() - 1;
					i_handlers[idx] = i_platformTypes[i];
				}
			}
			i_yapClasses = new com.db4o.YapClass[i_maxTypeID + 1];
			for (int i = 0; i < CLASSCOUNT; i++)
			{
				int id = i + 1;
				i_yapClasses[i] = new com.db4o.YapClassPrimitive(a_stream, i_handlers[i]);
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
				i_yapClasses[idx] = new com.db4o.YapClassPrimitive(a_stream, i_platformTypes[i]);
				i_yapClasses[idx].SetID(id);
				if (id > i_maxTypeID)
				{
					i_maxTypeID = idx;
				}
				i_classByClass.Put(i_platformTypes[i].ClassReflector(), i_yapClasses[idx]);
			}
			i_anyArray = new com.db4o.YapClassPrimitive(a_stream, new com.db4o.YapArray(_masterStream
				, i_handlers[ANY_INDEX], false));
			i_anyArray.SetID(ANY_ARRAY_ID);
			i_yapClasses[ANY_ARRAY_ID - 1] = i_anyArray;
			i_anyArrayN = new com.db4o.YapClassPrimitive(a_stream, new com.db4o.YapArrayN(_masterStream
				, i_handlers[ANY_INDEX], false));
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
				return com.db4o.YapConst.TYPE_NARRAY;
			}
			return com.db4o.YapConst.TYPE_ARRAY;
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
			if (!com.db4o.Platform4.CallConstructor())
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
							sortedConstructors = com.db4o.foundation.Tree.Add(sortedConstructors, new com.db4o.TreeIntObject
								(i + constructors.Length * parameterCount, constructors[i]));
						}
						catch (System.Exception t)
						{
						}
					}
					bool[] foundConstructor = { false };
					if (sortedConstructors != null)
					{
						com.db4o.TypeHandler4[] handlers = i_handlers;
						sortedConstructors.Traverse(new _AnonymousInnerClass229(this, foundConstructor, handlers
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

		private sealed class _AnonymousInnerClass229 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass229(YapHandlers _enclosing, bool[] foundConstructor, com.db4o.TypeHandler4[]
				 handlers, com.db4o.reflect.ReflectClass claxx)
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
						)((com.db4o.TreeIntObject)a_object)._object;
					try
					{
						com.db4o.reflect.ReflectClass[] pTypes = constructor.GetParameterTypes();
						object[] parms = new object[pTypes.Length];
						for (int j = 0; j < parms.Length; j++)
						{
							for (int k = 0; k < com.db4o.YapHandlers.PRIMITIVECOUNT; k++)
							{
								if (pTypes[j].Equals(handlers[k].PrimitiveClassReflector()))
								{
									parms[j] = ((com.db4o.YapJavaClass)handlers[k]).PrimitiveNull();
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

			private readonly YapHandlers _enclosing;

			private readonly bool[] foundConstructor;

			private readonly com.db4o.TypeHandler4[] handlers;

			private readonly com.db4o.reflect.ReflectClass claxx;
		}

		internal void Decrypt(com.db4o.YapReader reader)
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

		internal void Encrypt(com.db4o.YapReader reader)
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

		internal com.db4o.TypeHandler4 GetHandler(int a_index)
		{
			return i_handlers[a_index - 1];
		}

		internal com.db4o.TypeHandler4 HandlerForClass(com.db4o.reflect.ReflectClass a_class
			, com.db4o.reflect.ReflectClass[] a_Supported)
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

		/// <summary>
		/// Can't return ANY class for interfaces, since that would kill the
		/// translators built into the architecture.
		/// </summary>
		/// <remarks>
		/// Can't return ANY class for interfaces, since that would kill the
		/// translators built into the architecture.
		/// </remarks>
		internal com.db4o.TypeHandler4 HandlerForClass(com.db4o.YapStream a_stream, com.db4o.reflect.ReflectClass
			 a_class)
		{
			if (a_class == null)
			{
				return null;
			}
			if (a_class.IsArray())
			{
				return HandlerForClass(a_stream, a_class.GetComponentType());
			}
			com.db4o.YapClass yc = GetYapClassStatic(a_class);
			if (yc != null)
			{
				return ((com.db4o.YapClassPrimitive)yc).i_handler;
			}
			return a_stream.GetYapClass(a_class, true);
		}

		private void InitClassReflectors(com.db4o.reflect.generic.GenericReflector reflector
			)
		{
			ICLASS_COMPARE = reflector.ForClass(com.db4o.YapConst.CLASS_COMPARE);
			ICLASS_DB4OTYPE = reflector.ForClass(com.db4o.YapConst.CLASS_DB4OTYPE);
			ICLASS_DB4OTYPEIMPL = reflector.ForClass(com.db4o.YapConst.CLASS_DB4OTYPEIMPL);
			ICLASS_INTERNAL = reflector.ForClass(com.db4o.YapConst.CLASS_INTERNAL);
			ICLASS_UNVERSIONED = reflector.ForClass(com.db4o.YapConst.CLASS_UNVERSIONED);
			ICLASS_OBJECT = reflector.ForClass(com.db4o.YapConst.CLASS_OBJECT);
			ICLASS_OBJECTCONTAINER = reflector.ForClass(com.db4o.YapConst.CLASS_OBJECTCONTAINER
				);
			ICLASS_STATICCLASS = reflector.ForClass(com.db4o.YapConst.CLASS_STATICCLASS);
			ICLASS_STRING = reflector.ForClass(j4o.lang.Class.GetClassForType(typeof(string))
				);
			ICLASS_TRANSIENTCLASS = reflector.ForClass(com.db4o.YapConst.CLASS_TRANSIENTCLASS
				);
			com.db4o.Platform4.RegisterCollections(reflector);
		}

		internal void InitEncryption(com.db4o.Config4Impl a_config)
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

		internal static com.db4o.Db4oTypeImpl GetDb4oType(com.db4o.reflect.ReflectClass clazz
			)
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

		public com.db4o.YapClass GetYapClassStatic(int a_id)
		{
			if (a_id > 0 && a_id <= i_maxTypeID)
			{
				return i_yapClasses[a_id - 1];
			}
			return null;
		}

		internal com.db4o.YapClass GetYapClassStatic(com.db4o.reflect.ReflectClass a_class
			)
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
			return (com.db4o.YapClass)i_classByClass.Get(a_class);
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
				return com.db4o.Platform4.IsValueType(claxx);
			}
			return false;
		}

		public bool IsSystemHandler(int id)
		{
			return id <= i_maxTypeID;
		}
	}
}
