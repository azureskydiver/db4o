/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
	/// <exclude></exclude>
	public class YapHandlers
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

		private com.db4o.YapDataType[] i_handlers;

		private int i_maxTypeID = ANY_ARRAY_N_ID + 1;

		private com.db4o.YapTypeAbstract[] i_platformTypes;

		private const int PRIMITIVECOUNT = 8;

		internal com.db4o.YapClass[] i_yapClasses;

		private const int ANY_INDEX = 10;

		public const int ANY_ID = 11;

		internal readonly com.db4o.YapFieldVirtual[] i_virtualFields = new com.db4o.YapFieldVirtual
			[2];

		private readonly com.db4o.Hashtable4 i_classByClass = new com.db4o.Hashtable4(32);

		internal com.db4o.types.Db4oCollections i_collections;

		internal com.db4o.YapIndexes i_indexes;

		internal com.db4o.ReplicationImpl i_replication;

		internal com.db4o.MigrationConnection i_migration;

		internal bool i_encrypt;

		internal byte[] i_encryptor;

		internal int i_lastEncryptorByte;

		internal com.db4o.reflect.ReflectClass ICLASS_COMPARE;

		internal com.db4o.reflect.ReflectClass ICLASS_DB4OTYPE;

		internal com.db4o.reflect.ReflectClass ICLASS_DB4OTYPEIMPL;

		internal com.db4o.reflect.ReflectClass ICLASS_ENUM;

		public com.db4o.reflect.ReflectClass ICLASS_INTERNAL;

		internal com.db4o.reflect.ReflectClass ICLASS_OBJECT;

		internal com.db4o.reflect.ReflectClass ICLASS_OBJECTCONTAINER;

		internal com.db4o.reflect.ReflectClass ICLASS_PBOOTRECORD;

		internal com.db4o.reflect.ReflectClass ICLASS_STATICCLASS;

		internal com.db4o.reflect.ReflectClass ICLASS_STRING;

		internal com.db4o.reflect.ReflectClass ICLASS_TRANSIENTCLASS;

		internal YapHandlers(com.db4o.YapStream a_stream, byte stringEncoding)
		{
			_masterStream = a_stream;
			a_stream.i_handlers = this;
			com.db4o.reflect.generic.GenericReflector reflector = a_stream.reflector();
			initClassReflectors(reflector);
			i_indexes = new com.db4o.YapIndexes(a_stream);
			i_virtualFields[0] = i_indexes.i_fieldVersion;
			i_virtualFields[1] = i_indexes.i_fieldUUID;
			i_stringHandler = new com.db4o.YapString(a_stream, com.db4o.YapStringIO.forEncoding
				(stringEncoding));
			i_handlers = new com.db4o.YapDataType[] { new com.db4o.YInt(a_stream), new com.db4o.YLong
				(a_stream), new com.db4o.YFloat(a_stream), new com.db4o.YBoolean(a_stream), new 
				com.db4o.YDouble(a_stream), new com.db4o.YByte(a_stream), new com.db4o.YChar(a_stream
				), new com.db4o.YShort(a_stream), i_stringHandler, new com.db4o.YDate(a_stream), 
				new com.db4o.YapClassAny(a_stream) };
			i_platformTypes = com.db4o.Platform.types(a_stream);
			if (i_platformTypes.Length > 0)
			{
				for (int i = 0; i < i_platformTypes.Length; i++)
				{
					i_platformTypes[i].initialize();
					if (i_platformTypes[i].getID() > i_maxTypeID)
					{
						i_maxTypeID = i_platformTypes[i].getID();
					}
				}
				com.db4o.YapDataType[] temp = i_handlers;
				i_handlers = new com.db4o.YapDataType[i_maxTypeID];
				j4o.lang.JavaSystem.arraycopy(temp, 0, i_handlers, 0, temp.Length);
				for (int i = 0; i < i_platformTypes.Length; i++)
				{
					int idx = i_platformTypes[i].getID() - 1;
					i_handlers[idx] = i_platformTypes[i];
				}
			}
			i_yapClasses = new com.db4o.YapClass[i_maxTypeID + 1];
			for (int i = 0; i < CLASSCOUNT; i++)
			{
				int id = i + 1;
				i_yapClasses[i] = new com.db4o.YapClassPrimitive(a_stream, i_handlers[i]);
				i_yapClasses[i].i_id = id;
				i_classByClass.put(i_handlers[i].classReflector(), i_yapClasses[i]);
				if (i < ANY_INDEX)
				{
					reflector.registerPrimitiveClass(id, i_handlers[i].classReflector().getName());
				}
			}
			for (int i = 0; i < i_platformTypes.Length; i++)
			{
				int id = i_platformTypes[i].getID();
				int idx = id - 1;
				reflector.registerPrimitiveClass(id, i_platformTypes[i].classReflector().getName(
					));
				i_handlers[idx] = i_platformTypes[i];
				i_yapClasses[idx] = new com.db4o.YapClassPrimitive(a_stream, i_platformTypes[i]);
				i_yapClasses[idx].i_id = id;
				if (i_yapClasses[idx].i_id > i_maxTypeID)
				{
					i_maxTypeID = idx;
				}
				i_classByClass.put(i_platformTypes[i].classReflector(), i_yapClasses[idx]);
			}
			i_anyArray = new com.db4o.YapClassPrimitive(a_stream, new com.db4o.YapArray(_masterStream
				, i_handlers[ANY_INDEX], false));
			i_anyArray.i_id = ANY_ARRAY_ID;
			i_yapClasses[ANY_ARRAY_ID - 1] = i_anyArray;
			i_anyArrayN = new com.db4o.YapClassPrimitive(a_stream, new com.db4o.YapArrayN(_masterStream
				, i_handlers[ANY_INDEX], false));
			i_anyArrayN.i_id = ANY_ARRAY_N_ID;
			i_yapClasses[ANY_ARRAY_N_ID - 1] = i_anyArrayN;
		}

		internal virtual int arrayType(object a_object)
		{
			com.db4o.reflect.ReflectClass claxx = _masterStream.reflector().forObject(a_object
				);
			if (claxx.isArray())
			{
				if (_masterStream.reflector().array().isNDimensional(claxx))
				{
					return com.db4o.YapConst.TYPE_NARRAY;
				}
				else
				{
					return com.db4o.YapConst.TYPE_ARRAY;
				}
			}
			return 0;
		}

		internal bool createConstructor(com.db4o.reflect.ReflectClass claxx, bool skipConstructor
			)
		{
			if (claxx == null)
			{
				return false;
			}
			if (claxx.isAbstract() || claxx.isInterface())
			{
				return true;
			}
			if (!com.db4o.Platform.callConstructor())
			{
				if (claxx.skipConstructor(skipConstructor))
				{
					return true;
				}
			}
			if (!_masterStream.i_config.i_testConstructors)
			{
				return true;
			}
			if (claxx.newInstance() != null)
			{
				return true;
			}
			if (_masterStream.reflector().constructorCallsSupported())
			{
				try
				{
					com.db4o.reflect.ReflectConstructor[] constructors = claxx.getDeclaredConstructors
						();
					com.db4o.Tree sortedConstructors = null;
					for (int i = 0; i < constructors.Length; i++)
					{
						try
						{
							constructors[i].setAccessible();
							int parameterCount = constructors[i].getParameterTypes().Length;
							sortedConstructors = com.db4o.Tree.add(sortedConstructors, new com.db4o.TreeIntObject
								(parameterCount, constructors[i]));
						}
						catch (System.Exception t)
						{
						}
					}
					bool[] foundConstructor = { false };
					if (sortedConstructors != null)
					{
						sortedConstructors.traverse(new _AnonymousInnerClass219(this, foundConstructor, claxx
							));
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

		private sealed class _AnonymousInnerClass219 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass219(YapHandlers _enclosing, bool[] foundConstructor, com.db4o.reflect.ReflectClass
				 claxx)
			{
				this._enclosing = _enclosing;
				this.foundConstructor = foundConstructor;
				this.claxx = claxx;
			}

			public void visit(object a_object)
			{
				if (!foundConstructor[0])
				{
					com.db4o.reflect.ReflectConstructor constructor = (com.db4o.reflect.ReflectConstructor
						)((com.db4o.TreeIntObject)a_object).i_object;
					try
					{
						com.db4o.reflect.ReflectClass[] pTypes = constructor.getParameterTypes();
						object[] parms = new object[pTypes.Length];
						for (int j = 0; j < parms.Length; j++)
						{
							for (int k = 0; k < com.db4o.YapHandlers.PRIMITIVECOUNT; k++)
							{
								if (pTypes[j] == this._enclosing.i_handlers[k].primitiveClassReflector())
								{
									parms[j] = ((com.db4o.YapJavaClass)this._enclosing.i_handlers[k]).primitiveNull();
									break;
								}
							}
						}
						object res = constructor.newInstance(parms);
						if (res != null)
						{
							foundConstructor[0] = true;
							claxx.useConstructor(constructor, parms);
						}
					}
					catch (System.Exception t)
					{
					}
				}
			}

			private readonly YapHandlers _enclosing;

			private readonly bool[] foundConstructor;

			private readonly com.db4o.reflect.ReflectClass claxx;
		}

		internal void decrypt(com.db4o.YapReader reader)
		{
			if (i_encrypt)
			{
				int encryptorOffSet = i_lastEncryptorByte;
				byte[] bytes = reader._buffer;
				for (int i = reader.getLength() - 1; i >= 0; i--)
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

		internal void encrypt(com.db4o.YapReader reader)
		{
			if (i_encrypt)
			{
				byte[] bytes = reader._buffer;
				int encryptorOffSet = i_lastEncryptorByte;
				for (int i = reader.getLength() - 1; i >= 0; i--)
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

		internal com.db4o.YapDataType getHandler(int a_index)
		{
			return i_handlers[a_index - 1];
		}

		internal com.db4o.YapDataType handlerForClass(com.db4o.reflect.ReflectClass a_class
			, com.db4o.reflect.ReflectClass[] a_Supported)
		{
			for (int i = 0; i < a_Supported.Length; i++)
			{
				if (a_Supported[i] == a_class)
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
		internal com.db4o.YapDataType handlerForClass(com.db4o.YapStream a_stream, com.db4o.reflect.ReflectClass
			 a_class)
		{
			if (a_class.isArray())
			{
				return handlerForClass(a_stream, a_class.getComponentType());
			}
			com.db4o.YapClass yc = getYapClassStatic(a_class);
			if (yc != null)
			{
				return ((com.db4o.YapClassPrimitive)yc).i_handler;
			}
			return a_stream.getYapClass(a_class, true);
		}

		private void initClassReflectors(com.db4o.reflect.generic.GenericReflector reflector
			)
		{
			ICLASS_COMPARE = reflector.forClass(com.db4o.YapConst.CLASS_COMPARE);
			ICLASS_DB4OTYPE = reflector.forClass(com.db4o.YapConst.CLASS_DB4OTYPE);
			ICLASS_DB4OTYPEIMPL = reflector.forClass(com.db4o.YapConst.CLASS_DB4OTYPEIMPL);
			ICLASS_ENUM = reflector.forClass(com.db4o.YapConst.CLASS_ENUM);
			ICLASS_INTERNAL = reflector.forClass(com.db4o.YapConst.CLASS_INTERNAL);
			ICLASS_OBJECT = reflector.forClass(com.db4o.YapConst.CLASS_OBJECT);
			ICLASS_OBJECTCONTAINER = reflector.forClass(com.db4o.YapConst.CLASS_OBJECTCONTAINER
				);
			ICLASS_PBOOTRECORD = reflector.forClass(com.db4o.YapConst.CLASS_PBOOTRECORD);
			ICLASS_STATICCLASS = reflector.forClass(com.db4o.YapConst.CLASS_STATICCLASS);
			ICLASS_STRING = reflector.forClass(j4o.lang.Class.getClassForType(typeof(string))
				);
			ICLASS_TRANSIENTCLASS = reflector.forClass(com.db4o.YapConst.CLASS_TRANSIENTCLASS
				);
			com.db4o.Platform.registerCollections(reflector);
		}

		internal virtual void initEncryption(com.db4o.Config4Impl a_config)
		{
			if (a_config.i_encrypt && a_config.i_password != null && j4o.lang.JavaSystem.getLengthOf
				(a_config.i_password) > 0)
			{
				i_encrypt = true;
				i_encryptor = new byte[j4o.lang.JavaSystem.getLengthOf(a_config.i_password)];
				for (int i = 0; i < i_encryptor.Length; i++)
				{
					i_encryptor[i] = (byte)(j4o.lang.JavaSystem.getCharAt(a_config.i_password, i) & 0xff
						);
				}
				i_lastEncryptorByte = j4o.lang.JavaSystem.getLengthOf(a_config.i_password) - 1;
			}
			else
			{
				i_encrypt = false;
				i_encryptor = null;
				i_lastEncryptorByte = 0;
			}
		}

		internal static com.db4o.Db4oTypeImpl getDb4oType(com.db4o.reflect.ReflectClass clazz
			)
		{
			for (int i = 0; i < i_db4oTypes.Length; i++)
			{
				if (clazz.isInstance(i_db4oTypes[i]))
				{
					return i_db4oTypes[i];
				}
			}
			return null;
		}

		public virtual com.db4o.YapClass getYapClassStatic(int a_id)
		{
			if (a_id > 0 && a_id <= i_maxTypeID)
			{
				return i_yapClasses[a_id - 1];
			}
			return null;
		}

		internal virtual com.db4o.YapClass getYapClassStatic(com.db4o.reflect.ReflectClass
			 a_class)
		{
			if (a_class == null)
			{
				return null;
			}
			if (a_class.isArray())
			{
				if (_masterStream.reflector().array().isNDimensional(a_class))
				{
					return i_anyArrayN;
				}
				return i_anyArray;
			}
			return (com.db4o.YapClass)i_classByClass.get(a_class);
		}

		public bool isSecondClass(object a_object)
		{
			if (a_object != null)
			{
				com.db4o.reflect.ReflectClass claxx = _masterStream.reflector().forObject(a_object
					);
				if (i_classByClass.get(claxx) != null)
				{
					return true;
				}
				return com.db4o.Platform.isValueType(claxx);
			}
			return false;
		}

		internal virtual int maxTypeID()
		{
			return i_maxTypeID;
		}
	}
}
