namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class FieldMetadata : com.db4o.ext.StoredField
	{
		private com.db4o.@internal.ClassMetadata i_yapClass;

		private int i_arrayPosition;

		protected string i_name;

		private bool i_isArray;

		private bool i_isNArray;

		private bool i_isPrimitive;

		private com.db4o.reflect.ReflectField i_javaField;

		internal com.db4o.@internal.TypeHandler4 i_handler;

		private int i_handlerID;

		private int i_state;

		private const int NOT_LOADED = 0;

		private const int UNAVAILABLE = -1;

		private const int AVAILABLE = 1;

		private com.db4o.@internal.Config4Field i_config;

		private com.db4o.@internal.Db4oTypeImpl i_db4oType;

		private com.db4o.@internal.btree.BTree _index;

		internal static readonly com.db4o.@internal.FieldMetadata[] EMPTY_ARRAY = new com.db4o.@internal.FieldMetadata
			[0];

		public FieldMetadata(com.db4o.@internal.ClassMetadata a_yapClass)
		{
			i_yapClass = a_yapClass;
		}

		internal FieldMetadata(com.db4o.@internal.ClassMetadata a_yapClass, com.db4o.config.ObjectTranslator
			 a_translator) : this(a_yapClass)
		{
			Init(a_yapClass, j4o.lang.JavaSystem.GetClassForObject(a_translator).GetName());
			i_state = AVAILABLE;
			com.db4o.@internal.ObjectContainerBase stream = GetStream();
			i_handler = stream.i_handlers.HandlerForClass(stream, stream.Reflector().ForClass
				(a_translator.StoredClass()));
		}

		internal FieldMetadata(com.db4o.@internal.ClassMetadata a_yapClass, com.db4o.reflect.ReflectField
			 a_field, com.db4o.@internal.TypeHandler4 a_handler) : this(a_yapClass)
		{
			Init(a_yapClass, a_field.GetName());
			i_javaField = a_field;
			i_javaField.SetAccessible();
			i_handler = a_handler;
			bool isPrimitive = false;
			if (a_field is com.db4o.reflect.generic.GenericField)
			{
				isPrimitive = ((com.db4o.reflect.generic.GenericField)a_field).IsPrimitive();
			}
			Configure(a_field.GetFieldType(), isPrimitive);
			CheckDb4oType();
			i_state = AVAILABLE;
		}

		public virtual void AddFieldIndex(com.db4o.@internal.marshall.MarshallerFamily mf
			, com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.StatefulBuffer writer
			, com.db4o.@internal.slots.Slot oldSlot)
		{
			if (!HasIndex())
			{
				writer.IncrementOffset(LinkLength());
				return;
			}
			AddIndexEntry(writer, ReadIndexEntry(mf, writer));
		}

		protected virtual void AddIndexEntry(com.db4o.@internal.StatefulBuffer a_bytes, object
			 indexEntry)
		{
			AddIndexEntry(a_bytes.GetTransaction(), a_bytes.GetID(), indexEntry);
		}

		public virtual void AddIndexEntry(com.db4o.@internal.Transaction trans, int parentID
			, object indexEntry)
		{
			if (!HasIndex())
			{
				return;
			}
			com.db4o.@internal.btree.BTree index = GetIndex(trans);
			if (index == null)
			{
				return;
			}
			index.Add(trans, CreateFieldIndexKey(parentID, indexEntry));
		}

		private com.db4o.@internal.btree.FieldIndexKey CreateFieldIndexKey(int parentID, 
			object indexEntry)
		{
			object convertedIndexEntry = IndexEntryFor(indexEntry);
			return new com.db4o.@internal.btree.FieldIndexKey(parentID, convertedIndexEntry);
		}

		protected virtual object IndexEntryFor(object indexEntry)
		{
			return i_javaField.IndexEntry(indexEntry);
		}

		public virtual bool CanUseNullBitmap()
		{
			return true;
		}

		public virtual object ReadIndexEntry(com.db4o.@internal.marshall.MarshallerFamily
			 mf, com.db4o.@internal.StatefulBuffer writer)
		{
			try
			{
				return i_handler.ReadIndexEntry(mf, writer);
			}
			catch (com.db4o.CorruptionException)
			{
			}
			return null;
		}

		public virtual void RemoveIndexEntry(com.db4o.@internal.Transaction trans, int parentID
			, object indexEntry)
		{
			if (!HasIndex())
			{
				return;
			}
			if (_index == null)
			{
				return;
			}
			_index.Remove(trans, CreateFieldIndexKey(parentID, indexEntry));
		}

		public virtual bool Alive()
		{
			if (i_state == AVAILABLE)
			{
				return true;
			}
			if (i_state == NOT_LOADED)
			{
				if (i_handler == null)
				{
					i_handler = LoadJavaField1();
					if (i_handler != null)
					{
						if (i_handlerID == 0)
						{
							i_handlerID = i_handler.GetID();
						}
						else
						{
							if (i_handler.GetID() != i_handlerID)
							{
								i_handler = null;
							}
						}
					}
				}
				LoadJavaField();
				if (i_handler == null || i_javaField == null)
				{
					i_state = UNAVAILABLE;
					i_javaField = null;
				}
				else
				{
					i_handler = WrapHandlerToArrays(GetStream(), i_handler);
					i_state = AVAILABLE;
					CheckDb4oType();
				}
			}
			return i_state == AVAILABLE;
		}

		internal virtual bool CanAddToQuery(string fieldName)
		{
			if (!Alive())
			{
				return false;
			}
			return fieldName.Equals(GetName()) && GetParentYapClass() != null && !GetParentYapClass
				().IsInternal();
		}

		public virtual bool CanHold(com.db4o.reflect.ReflectClass claxx)
		{
			if (claxx == null)
			{
				return !i_isPrimitive;
			}
			return i_handler.CanHold(claxx);
		}

		public virtual object Coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			if (claxx == null || obj == null)
			{
				return i_isPrimitive ? com.db4o.foundation.No4.INSTANCE : obj;
			}
			return i_handler.Coerce(claxx, obj);
		}

		public bool CanLoadByIndex()
		{
			if (i_handler is com.db4o.@internal.ClassMetadata)
			{
				com.db4o.@internal.ClassMetadata yc = (com.db4o.@internal.ClassMetadata)i_handler;
				if (yc.IsArray())
				{
					return false;
				}
			}
			return true;
		}

		internal virtual void CascadeActivation(com.db4o.@internal.Transaction a_trans, object
			 a_object, int a_depth, bool a_activate)
		{
			if (Alive())
			{
				try
				{
					object cascadeTo = GetOrCreate(a_trans, a_object);
					if (cascadeTo != null && i_handler != null)
					{
						i_handler.CascadeActivation(a_trans, cascadeTo, a_depth, a_activate);
					}
				}
				catch
				{
				}
			}
		}

		private void CheckDb4oType()
		{
			if (i_javaField != null)
			{
				if (GetStream().i_handlers.ICLASS_DB4OTYPE.IsAssignableFrom(i_javaField.GetFieldType
					()))
				{
					i_db4oType = com.db4o.@internal.HandlerRegistry.GetDb4oType(i_javaField.GetFieldType
						());
				}
			}
		}

		internal virtual void CollectConstraints(com.db4o.@internal.Transaction a_trans, 
			com.db4o.@internal.query.processor.QConObject a_parent, object a_template, com.db4o.foundation.Visitor4
			 a_visitor)
		{
			object obj = GetOn(a_trans, a_template);
			if (obj != null)
			{
				com.db4o.foundation.Collection4 objs = com.db4o.@internal.Platform4.FlattenCollection
					(a_trans.Stream(), obj);
				System.Collections.IEnumerator j = objs.GetEnumerator();
				while (j.MoveNext())
				{
					obj = j.Current;
					if (obj != null)
					{
						if (i_isPrimitive)
						{
							if (i_handler is com.db4o.@internal.handlers.PrimitiveHandler)
							{
								if (obj.Equals(((com.db4o.@internal.handlers.PrimitiveHandler)i_handler).PrimitiveNull
									()))
								{
									return;
								}
							}
						}
						if (com.db4o.@internal.Platform4.IgnoreAsConstraint(obj))
						{
							return;
						}
						if (!a_parent.HasObjectInParentPath(obj))
						{
							a_visitor.Visit(new com.db4o.@internal.query.processor.QConObject(a_trans, a_parent
								, QField(a_trans), obj));
						}
					}
				}
			}
		}

		public com.db4o.@internal.TreeInt CollectIDs(com.db4o.@internal.marshall.MarshallerFamily
			 mf, com.db4o.@internal.TreeInt tree, com.db4o.@internal.StatefulBuffer a_bytes)
		{
			if (Alive())
			{
				if (i_handler is com.db4o.@internal.ClassMetadata)
				{
					tree = (com.db4o.@internal.TreeInt)com.db4o.foundation.Tree.Add(tree, new com.db4o.@internal.TreeInt
						(a_bytes.ReadInt()));
				}
				else
				{
					if (i_handler is com.db4o.@internal.handlers.ArrayHandler)
					{
						tree = ((com.db4o.@internal.handlers.ArrayHandler)i_handler).CollectIDs(mf, tree, 
							a_bytes);
					}
				}
			}
			return tree;
		}

		internal virtual void Configure(com.db4o.reflect.ReflectClass a_class, bool isPrimitive
			)
		{
			i_isPrimitive = isPrimitive | a_class.IsPrimitive();
			i_isArray = a_class.IsArray();
			if (i_isArray)
			{
				com.db4o.reflect.ReflectArray reflectArray = GetStream().Reflector().Array();
				i_isNArray = reflectArray.IsNDimensional(a_class);
				a_class = reflectArray.GetComponentType(a_class);
				if (i_isNArray)
				{
					i_handler = new com.db4o.@internal.handlers.MultidimensionalArrayHandler(GetStream
						(), i_handler, i_isPrimitive);
				}
				else
				{
					i_handler = new com.db4o.@internal.handlers.ArrayHandler(GetStream(), i_handler, 
						i_isPrimitive);
				}
			}
		}

		internal virtual void Deactivate(com.db4o.@internal.Transaction a_trans, object a_onObject
			, int a_depth)
		{
			if (!Alive())
			{
				return;
			}
			try
			{
				bool isEnumClass = i_yapClass.IsEnum();
				if (i_isPrimitive && !i_isArray)
				{
					if (!isEnumClass)
					{
						i_javaField.Set(a_onObject, ((com.db4o.@internal.handlers.PrimitiveHandler)i_handler
							).PrimitiveNull());
					}
					return;
				}
				if (a_depth > 0)
				{
					CascadeActivation(a_trans, a_onObject, a_depth, false);
				}
				if (!isEnumClass)
				{
					i_javaField.Set(a_onObject, null);
				}
			}
			catch
			{
			}
		}

		public virtual void Delete(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 a_bytes, bool isUpdate)
		{
			if (!Alive())
			{
				IncrementOffset(a_bytes);
				return;
			}
			RemoveIndexEntry(mf, a_bytes);
			bool dotnetValueType = false;
			dotnetValueType = com.db4o.@internal.Platform4.IsValueType(i_handler.ClassReflector
				());
			if ((i_config != null && i_config.CascadeOnDelete() == com.db4o.@internal.Const4.
				YES) || dotnetValueType)
			{
				int preserveCascade = a_bytes.CascadeDeletes();
				a_bytes.SetCascadeDeletes(1);
				i_handler.DeleteEmbedded(mf, a_bytes);
				a_bytes.SetCascadeDeletes(preserveCascade);
			}
			else
			{
				if (i_config != null && i_config.CascadeOnDelete() == com.db4o.@internal.Const4.NO
					)
				{
					int preserveCascade = a_bytes.CascadeDeletes();
					a_bytes.SetCascadeDeletes(0);
					i_handler.DeleteEmbedded(mf, a_bytes);
					a_bytes.SetCascadeDeletes(preserveCascade);
				}
				else
				{
					i_handler.DeleteEmbedded(mf, a_bytes);
				}
			}
		}

		private void RemoveIndexEntry(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 a_bytes)
		{
			if (!HasIndex())
			{
				return;
			}
			int offset = a_bytes._offset;
			object obj = null;
			try
			{
				obj = i_handler.ReadIndexEntry(mf, a_bytes);
			}
			catch (com.db4o.CorruptionException e)
			{
			}
			RemoveIndexEntry(a_bytes.GetTransaction(), a_bytes.GetID(), obj);
			a_bytes._offset = offset;
		}

		public override bool Equals(object obj)
		{
			if (obj is com.db4o.@internal.FieldMetadata)
			{
				com.db4o.@internal.FieldMetadata yapField = (com.db4o.@internal.FieldMetadata)obj;
				yapField.Alive();
				Alive();
				return yapField.i_isPrimitive == i_isPrimitive && yapField.i_handler.Equals(i_handler
					) && yapField.i_name.Equals(i_name);
			}
			return false;
		}

		public override int GetHashCode()
		{
			return i_name.GetHashCode();
		}

		public virtual object Get(object a_onObject)
		{
			if (i_yapClass != null)
			{
				com.db4o.@internal.ObjectContainerBase stream = i_yapClass.GetStream();
				if (stream != null)
				{
					lock (stream.i_lock)
					{
						stream.CheckClosed();
						com.db4o.@internal.ObjectReference yo = stream.GetYapObject(a_onObject);
						if (yo != null)
						{
							int id = yo.GetID();
							if (id > 0)
							{
								com.db4o.@internal.StatefulBuffer writer = stream.ReadWriterByID(stream.GetTransaction
									(), id);
								if (writer != null)
								{
									writer._offset = 0;
									com.db4o.@internal.marshall.ObjectHeader oh = new com.db4o.@internal.marshall.ObjectHeader
										(stream, i_yapClass, writer);
									if (oh.ObjectMarshaller().FindOffset(i_yapClass, oh._headerAttributes, writer, this
										))
									{
										try
										{
											return Read(oh._marshallerFamily, writer);
										}
										catch (com.db4o.CorruptionException e)
										{
										}
									}
								}
							}
						}
					}
				}
			}
			return null;
		}

		public virtual string GetName()
		{
			return i_name;
		}

		public virtual com.db4o.@internal.ClassMetadata GetFieldYapClass(com.db4o.@internal.ObjectContainerBase
			 a_stream)
		{
			return i_handler.GetYapClass(a_stream);
		}

		public virtual com.db4o.@internal.TypeHandler4 GetHandler()
		{
			return i_handler;
		}

		public virtual int GetHandlerID()
		{
			return i_handlerID;
		}

		public virtual object GetOn(com.db4o.@internal.Transaction a_trans, object a_OnObject
			)
		{
			if (Alive())
			{
				try
				{
					return i_javaField.Get(a_OnObject);
				}
				catch
				{
				}
			}
			return null;
		}

		/// <summary>
		/// dirty hack for com.db4o.types some of them need to be set automatically
		/// TODO: Derive from YapField for Db4oTypes
		/// </summary>
		public virtual object GetOrCreate(com.db4o.@internal.Transaction a_trans, object 
			a_OnObject)
		{
			if (Alive())
			{
				try
				{
					object obj = i_javaField.Get(a_OnObject);
					if (i_db4oType != null)
					{
						if (obj == null)
						{
							obj = i_db4oType.CreateDefault(a_trans);
							i_javaField.Set(a_OnObject, obj);
						}
					}
					return obj;
				}
				catch (System.Exception t)
				{
				}
			}
			return null;
		}

		public virtual com.db4o.@internal.ClassMetadata GetParentYapClass()
		{
			return i_yapClass;
		}

		public virtual com.db4o.reflect.ReflectClass GetStoredType()
		{
			if (i_handler == null)
			{
				return null;
			}
			return i_handler.ClassReflector();
		}

		public virtual com.db4o.@internal.ObjectContainerBase GetStream()
		{
			if (i_yapClass == null)
			{
				return null;
			}
			return i_yapClass.GetStream();
		}

		public virtual bool HasConfig()
		{
			return i_config != null;
		}

		public virtual bool HasIndex()
		{
			return _index != null;
		}

		public void IncrementOffset(com.db4o.@internal.Buffer a_bytes)
		{
			a_bytes.IncrementOffset(LinkLength());
		}

		public void Init(com.db4o.@internal.ClassMetadata a_yapClass, string a_name)
		{
			i_yapClass = a_yapClass;
			i_name = a_name;
			InitIndex(a_yapClass, a_name);
		}

		internal void InitIndex(com.db4o.@internal.ClassMetadata a_yapClass, string a_name
			)
		{
			if (a_yapClass.i_config != null)
			{
				i_config = a_yapClass.i_config.ConfigField(a_name);
			}
		}

		public virtual void Init(int handlerID, bool isPrimitive, bool isArray, bool isNArray
			)
		{
			i_handlerID = handlerID;
			i_isPrimitive = isPrimitive;
			i_isArray = isArray;
			i_isNArray = isNArray;
		}

		private bool _initialized = false;

		internal void InitConfigOnUp(com.db4o.@internal.Transaction trans)
		{
			if (i_config != null && !_initialized)
			{
				_initialized = true;
				i_config.InitOnUp(trans, this);
			}
		}

		public virtual void Instantiate(com.db4o.@internal.marshall.MarshallerFamily mf, 
			com.db4o.@internal.ObjectReference a_yapObject, object a_onObject, com.db4o.@internal.StatefulBuffer
			 a_bytes)
		{
			if (!Alive())
			{
				IncrementOffset(a_bytes);
				return;
			}
			object toSet = null;
			try
			{
				toSet = Read(mf, a_bytes);
			}
			catch
			{
				throw new com.db4o.CorruptionException();
			}
			if (i_db4oType != null)
			{
				if (toSet != null)
				{
					((com.db4o.@internal.Db4oTypeImpl)toSet).SetTrans(a_bytes.GetTransaction());
				}
			}
			Set(a_onObject, toSet);
		}

		public virtual bool IsArray()
		{
			return i_isArray;
		}

		public virtual int LinkLength()
		{
			Alive();
			if (i_handler == null)
			{
				return com.db4o.@internal.Const4.ID_LENGTH;
			}
			return i_handler.LinkLength();
		}

		public virtual void CalculateLengths(com.db4o.@internal.Transaction trans, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 header, object obj)
		{
			Alive();
			if (i_handler == null)
			{
				header.AddBaseLength(com.db4o.@internal.Const4.ID_LENGTH);
				return;
			}
			i_handler.CalculateLengths(trans, header, true, obj, true);
		}

		public virtual void LoadHandler(com.db4o.@internal.ObjectContainerBase a_stream)
		{
			i_handler = a_stream.HandlerByID(i_handlerID);
		}

		private void LoadJavaField()
		{
			com.db4o.@internal.TypeHandler4 handler = LoadJavaField1();
			if (handler == null || (!handler.Equals(i_handler)))
			{
				i_javaField = null;
				i_state = UNAVAILABLE;
			}
		}

		private com.db4o.@internal.TypeHandler4 LoadJavaField1()
		{
			try
			{
				com.db4o.@internal.ObjectContainerBase stream = i_yapClass.GetStream();
				com.db4o.reflect.ReflectClass claxx = i_yapClass.ClassReflector();
				if (claxx == null)
				{
					return null;
				}
				i_javaField = claxx.GetDeclaredField(i_name);
				if (i_javaField == null)
				{
					return null;
				}
				i_javaField.SetAccessible();
				stream.ShowInternalClasses(true);
				com.db4o.@internal.TypeHandler4 handler = stream.i_handlers.HandlerForClass(stream
					, i_javaField.GetFieldType());
				stream.ShowInternalClasses(false);
				return handler;
			}
			catch (System.Exception e)
			{
			}
			return null;
		}

		public virtual void Marshall(com.db4o.@internal.ObjectReference yo, object obj, com.db4o.@internal.marshall.MarshallerFamily
			 mf, com.db4o.@internal.StatefulBuffer writer, com.db4o.@internal.Config4Class config
			, bool isNew)
		{
			object indexEntry = null;
			if (obj != null && ((config != null && (config.CascadeOnUpdate() == com.db4o.@internal.Const4
				.YES)) || (i_config != null && (i_config.CascadeOnUpdate() == com.db4o.@internal.Const4
				.YES))))
			{
				int min = 1;
				if (i_yapClass.IsCollection(obj))
				{
					com.db4o.reflect.generic.GenericReflector reflector = i_yapClass.Reflector();
					min = reflector.CollectionUpdateDepth(reflector.ForObject(obj));
				}
				int updateDepth = writer.GetUpdateDepth();
				if (updateDepth < min)
				{
					writer.SetUpdateDepth(min);
				}
				indexEntry = i_handler.WriteNew(mf, obj, true, writer, true, true);
				writer.SetUpdateDepth(updateDepth);
			}
			else
			{
				indexEntry = i_handler.WriteNew(mf, obj, true, writer, true, true);
			}
			AddIndexEntry(writer, indexEntry);
		}

		public virtual bool NeedsArrayAndPrimitiveInfo()
		{
			return true;
		}

		public virtual bool NeedsHandlerId()
		{
			return true;
		}

		public virtual com.db4o.@internal.Comparable4 PrepareComparison(object obj)
		{
			if (Alive())
			{
				i_handler.PrepareComparison(obj);
				return i_handler;
			}
			return null;
		}

		public virtual com.db4o.@internal.query.processor.QField QField(com.db4o.@internal.Transaction
			 a_trans)
		{
			int yapClassID = 0;
			if (i_yapClass != null)
			{
				yapClassID = i_yapClass.GetID();
			}
			return new com.db4o.@internal.query.processor.QField(a_trans, i_name, this, yapClassID
				, i_arrayPosition);
		}

		internal virtual object Read(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 a_bytes)
		{
			if (!Alive())
			{
				IncrementOffset(a_bytes);
				return null;
			}
			return i_handler.Read(mf, a_bytes, true);
		}

		public virtual object ReadQuery(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.marshall.MarshallerFamily
			 mf, com.db4o.@internal.Buffer a_reader)
		{
			return i_handler.ReadQuery(a_trans, mf, true, a_reader, false);
		}

		public virtual void ReadVirtualAttribute(com.db4o.@internal.Transaction a_trans, 
			com.db4o.@internal.Buffer a_reader, com.db4o.@internal.ObjectReference a_yapObject
			)
		{
			a_reader.IncrementOffset(i_handler.LinkLength());
		}

		internal virtual void Refresh()
		{
			com.db4o.@internal.TypeHandler4 handler = LoadJavaField1();
			if (handler != null)
			{
				handler = WrapHandlerToArrays(GetStream(), handler);
				if (handler.Equals(i_handler))
				{
					return;
				}
			}
			i_javaField = null;
			i_state = UNAVAILABLE;
		}

		public virtual void Rename(string newName)
		{
			com.db4o.@internal.ObjectContainerBase stream = i_yapClass.GetStream();
			if (!stream.IsClient())
			{
				i_name = newName;
				i_yapClass.SetStateDirty();
				i_yapClass.Write(stream.GetSystemTransaction());
			}
			else
			{
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(58);
			}
		}

		public virtual void SetArrayPosition(int a_index)
		{
			i_arrayPosition = a_index;
		}

		public void Set(object onObject, object obj)
		{
			try
			{
				i_javaField.Set(onObject, obj);
			}
			catch (System.Exception t)
			{
			}
		}

		internal virtual void SetName(string a_name)
		{
			i_name = a_name;
		}

		internal virtual bool SupportsIndex()
		{
			return Alive() && i_handler.SupportsIndex();
		}

		public void TraverseValues(com.db4o.foundation.Visitor4 userVisitor)
		{
			if (!Alive())
			{
				return;
			}
			AssertHasIndex();
			com.db4o.@internal.ObjectContainerBase stream = i_yapClass.GetStream();
			if (stream.IsClient())
			{
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(com.db4o.@internal.Messages.
					CLIENT_SERVER_UNSUPPORTED);
			}
			lock (stream.Lock())
			{
				com.db4o.@internal.Transaction trans = stream.GetTransaction();
				_index.TraverseKeys(trans, new _AnonymousInnerClass794(this, userVisitor, trans));
			}
		}

		private sealed class _AnonymousInnerClass794 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass794(FieldMetadata _enclosing, com.db4o.foundation.Visitor4
				 userVisitor, com.db4o.@internal.Transaction trans)
			{
				this._enclosing = _enclosing;
				this.userVisitor = userVisitor;
				this.trans = trans;
			}

			public void Visit(object obj)
			{
				com.db4o.@internal.btree.FieldIndexKey key = (com.db4o.@internal.btree.FieldIndexKey
					)obj;
				userVisitor.Visit(this._enclosing.i_handler.IndexEntryToObject(trans, key.Value()
					));
			}

			private readonly FieldMetadata _enclosing;

			private readonly com.db4o.foundation.Visitor4 userVisitor;

			private readonly com.db4o.@internal.Transaction trans;
		}

		private void AssertHasIndex()
		{
			if (!HasIndex())
			{
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(com.db4o.@internal.Messages.
					ONLY_FOR_INDEXED_FIELDS);
			}
		}

		private com.db4o.@internal.TypeHandler4 WrapHandlerToArrays(com.db4o.@internal.ObjectContainerBase
			 a_stream, com.db4o.@internal.TypeHandler4 a_handler)
		{
			if (i_isNArray)
			{
				a_handler = new com.db4o.@internal.handlers.MultidimensionalArrayHandler(a_stream
					, a_handler, i_isPrimitive);
			}
			else
			{
				if (i_isArray)
				{
					a_handler = new com.db4o.@internal.handlers.ArrayHandler(a_stream, a_handler, i_isPrimitive
						);
				}
			}
			return a_handler;
		}

		public override string ToString()
		{
			System.Text.StringBuilder sb = new System.Text.StringBuilder();
			if (i_yapClass != null)
			{
				sb.Append(i_yapClass.GetName());
				sb.Append(".");
				sb.Append(GetName());
			}
			return sb.ToString();
		}

		public string ToString(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 writer)
		{
			string str = "\n Field " + i_name;
			if (!Alive())
			{
				IncrementOffset(writer);
			}
			else
			{
				object obj = null;
				try
				{
					obj = Read(mf, writer);
				}
				catch
				{
				}
				if (obj == null)
				{
					str += "\n [null]";
				}
				else
				{
					str += "\n  " + obj.ToString();
				}
			}
			return str;
		}

		public virtual void InitIndex(com.db4o.@internal.Transaction systemTrans)
		{
			InitIndex(systemTrans, 0);
		}

		public virtual void InitIndex(com.db4o.@internal.Transaction systemTrans, int id)
		{
			if (_index != null)
			{
				throw new System.InvalidOperationException();
			}
			if (systemTrans.Stream().IsClient())
			{
				return;
			}
			_index = NewBTree(systemTrans, id);
		}

		protected com.db4o.@internal.btree.BTree NewBTree(com.db4o.@internal.Transaction 
			systemTrans, int id)
		{
			com.db4o.@internal.ObjectContainerBase stream = systemTrans.Stream();
			com.db4o.@internal.ix.Indexable4 indexHandler = IndexHandler(stream);
			if (indexHandler == null)
			{
				return null;
			}
			return new com.db4o.@internal.btree.BTree(systemTrans, id, new com.db4o.@internal.btree.FieldIndexKeyHandler
				(stream, indexHandler));
		}

		protected virtual com.db4o.@internal.ix.Indexable4 IndexHandler(com.db4o.@internal.ObjectContainerBase
			 stream)
		{
			com.db4o.reflect.ReflectClass indexType = null;
			if (i_javaField != null)
			{
				indexType = i_javaField.IndexType();
			}
			com.db4o.@internal.ix.Indexable4 indexHandler = stream.i_handlers.HandlerForClass
				(stream, indexType);
			return indexHandler;
		}

		public virtual com.db4o.@internal.btree.BTree GetIndex(com.db4o.@internal.Transaction
			 transaction)
		{
			return _index;
		}

		public virtual bool IsVirtual()
		{
			return false;
		}

		public virtual bool IsPrimitive()
		{
			return i_isPrimitive;
		}

		public virtual com.db4o.@internal.btree.BTreeRange Search(com.db4o.@internal.Transaction
			 transaction, object value)
		{
			AssertHasIndex();
			com.db4o.@internal.btree.BTreeNodeSearchResult lowerBound = SearchLowerBound(transaction
				, value);
			com.db4o.@internal.btree.BTreeNodeSearchResult upperBound = SearchUpperBound(transaction
				, value);
			return lowerBound.CreateIncludingRange(upperBound);
		}

		private com.db4o.@internal.btree.BTreeNodeSearchResult SearchUpperBound(com.db4o.@internal.Transaction
			 transaction, object value)
		{
			return SearchBound(transaction, int.MaxValue, value);
		}

		private com.db4o.@internal.btree.BTreeNodeSearchResult SearchLowerBound(com.db4o.@internal.Transaction
			 transaction, object value)
		{
			return SearchBound(transaction, 0, value);
		}

		private com.db4o.@internal.btree.BTreeNodeSearchResult SearchBound(com.db4o.@internal.Transaction
			 transaction, int parentID, object keyPart)
		{
			return GetIndex(transaction).SearchLeaf(transaction, CreateFieldIndexKey(parentID
				, keyPart), com.db4o.@internal.btree.SearchTarget.LOWEST);
		}

		public virtual bool RebuildIndexForClass(com.db4o.@internal.LocalObjectContainer 
			stream, com.db4o.@internal.ClassMetadata yapClass)
		{
			long[] ids = yapClass.GetIDs();
			for (int i = 0; i < ids.Length; i++)
			{
				RebuildIndexForObject(stream, yapClass, (int)ids[i]);
			}
			return ids.Length > 0;
		}

		protected virtual void RebuildIndexForObject(com.db4o.@internal.LocalObjectContainer
			 stream, com.db4o.@internal.ClassMetadata yapClass, int objectId)
		{
			com.db4o.@internal.StatefulBuffer writer = stream.ReadWriterByID(stream.GetSystemTransaction
				(), objectId);
			if (writer != null)
			{
				RebuildIndexForWriter(stream, writer, objectId);
			}
		}

		protected virtual void RebuildIndexForWriter(com.db4o.@internal.LocalObjectContainer
			 stream, com.db4o.@internal.StatefulBuffer writer, int objectId)
		{
			com.db4o.@internal.marshall.ObjectHeader oh = new com.db4o.@internal.marshall.ObjectHeader
				(stream, writer);
			object obj = ReadIndexEntryForRebuild(writer, oh);
			AddIndexEntry(stream.GetSystemTransaction(), objectId, obj);
		}

		private object ReadIndexEntryForRebuild(com.db4o.@internal.StatefulBuffer writer, 
			com.db4o.@internal.marshall.ObjectHeader oh)
		{
			return oh.ObjectMarshaller().ReadIndexEntry(oh.YapClass(), oh._headerAttributes, 
				this, writer);
		}

		public virtual void DropIndex(com.db4o.@internal.Transaction systemTrans)
		{
			if (_index == null)
			{
				return;
			}
			com.db4o.@internal.ObjectContainerBase stream = systemTrans.Stream();
			if (stream.ConfigImpl().MessageLevel() > com.db4o.@internal.Const4.NONE)
			{
				stream.Message("dropping index " + ToString());
			}
			_index.Free(systemTrans);
			stream.SetDirtyInSystemTransaction(GetParentYapClass());
			_index = null;
		}

		public virtual void DefragField(com.db4o.@internal.marshall.MarshallerFamily mf, 
			com.db4o.@internal.ReaderPair readers)
		{
			GetHandler().Defrag(mf, readers, true);
		}
	}
}
