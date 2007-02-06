namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class ClassMetadata : com.db4o.@internal.PersistentBase, com.db4o.@internal.TypeHandler4
		, com.db4o.ext.StoredClass
	{
		public com.db4o.@internal.ClassMetadata i_ancestor;

		internal com.db4o.@internal.Config4Class i_config;

		public int _metaClassID;

		public com.db4o.@internal.FieldMetadata[] i_fields;

		private readonly com.db4o.@internal.classindex.ClassIndexStrategy _index;

		protected string i_name;

		protected readonly com.db4o.@internal.ObjectContainerBase i_stream;

		internal byte[] i_nameBytes;

		private com.db4o.@internal.Buffer i_reader;

		private bool _classIndexed;

		private com.db4o.reflect.ReflectClass _reflector;

		private bool _isEnum;

		public bool i_dontCallConstructors;

		private com.db4o.@internal.EventDispatcher _eventDispatcher;

		private bool _internal;

		private bool _unversioned;

		private int i_lastID;

		private int _canUpdateFast;

		public bool CanUpdateFast()
		{
			if (_canUpdateFast == com.db4o.@internal.Const4.UNCHECKED)
			{
				_canUpdateFast = CheckCanUpdateFast() ? com.db4o.@internal.Const4.YES : com.db4o.@internal.Const4
					.NO;
			}
			return _canUpdateFast == com.db4o.@internal.Const4.YES;
		}

		private bool CheckCanUpdateFast()
		{
			if (i_ancestor != null && !i_ancestor.CanUpdateFast())
			{
				return false;
			}
			if (i_config != null && i_config.CascadeOnDelete() == com.db4o.@internal.Const4.YES
				)
			{
				return false;
			}
			for (int i = 0; i < i_fields.Length; ++i)
			{
				if (i_fields[i].HasIndex())
				{
					return false;
				}
			}
			return true;
		}

		internal virtual bool IsInternal()
		{
			return _internal;
		}

		private com.db4o.@internal.classindex.ClassIndexStrategy CreateIndexStrategy()
		{
			return new com.db4o.@internal.classindex.BTreeClassIndexStrategy(this);
		}

		internal ClassMetadata(com.db4o.@internal.ObjectContainerBase stream, com.db4o.reflect.ReflectClass
			 reflector)
		{
			i_stream = stream;
			_reflector = reflector;
			_index = CreateIndexStrategy();
			_classIndexed = true;
		}

		internal virtual void ActivateFields(com.db4o.@internal.Transaction a_trans, object
			 a_object, int a_depth)
		{
			if (ObjectCanActivate(a_trans.Stream(), a_object))
			{
				ActivateFields1(a_trans, a_object, a_depth);
			}
		}

		internal virtual void ActivateFields1(com.db4o.@internal.Transaction a_trans, object
			 a_object, int a_depth)
		{
			for (int i = 0; i < i_fields.Length; i++)
			{
				i_fields[i].CascadeActivation(a_trans, a_object, a_depth, true);
			}
			if (i_ancestor != null)
			{
				i_ancestor.ActivateFields1(a_trans, a_object, a_depth);
			}
		}

		public void AddFieldIndices(com.db4o.@internal.StatefulBuffer a_writer, com.db4o.@internal.slots.Slot
			 oldSlot)
		{
			if (HasIndex() || HasVirtualAttributes())
			{
				com.db4o.@internal.marshall.ObjectHeader oh = new com.db4o.@internal.marshall.ObjectHeader
					(i_stream, this, a_writer);
				oh._marshallerFamily._object.AddFieldIndices(this, oh._headerAttributes, a_writer
					, oldSlot);
			}
		}

		internal virtual void AddMembers(com.db4o.@internal.ObjectContainerBase a_stream)
		{
			BitTrue(com.db4o.@internal.Const4.CHECKED_CHANGES);
			if (AddTranslatorFields(a_stream))
			{
				return;
			}
			if (a_stream.DetectSchemaChanges())
			{
				bool dirty = IsDirty();
				com.db4o.foundation.Collection4 members = new com.db4o.foundation.Collection4();
				if (null != i_fields)
				{
					members.AddAll(i_fields);
					if (i_fields.Length == 1 && i_fields[0] is com.db4o.@internal.TranslatedFieldMetadata
						)
					{
						SetStateOK();
						return;
					}
				}
				if (GenerateVersionNumbers())
				{
					if (!HasVersionField())
					{
						members.Add(a_stream.GetVersionIndex());
						dirty = true;
					}
				}
				if (GenerateUUIDs())
				{
					if (!HasUUIDField())
					{
						members.Add(a_stream.GetUUIDIndex());
						dirty = true;
					}
				}
				dirty = CollectReflectFields(a_stream, members) | dirty;
				if (dirty)
				{
					i_stream.SetDirtyInSystemTransaction(this);
					i_fields = new com.db4o.@internal.FieldMetadata[members.Size()];
					members.ToArray(i_fields);
					for (int i = 0; i < i_fields.Length; i++)
					{
						i_fields[i].SetArrayPosition(i);
					}
				}
				else
				{
					if (members.Size() == 0)
					{
						i_fields = new com.db4o.@internal.FieldMetadata[0];
					}
				}
				com.db4o.@internal.diagnostic.DiagnosticProcessor dp = i_stream.i_handlers._diagnosticProcessor;
				if (dp.Enabled())
				{
					dp.CheckClassHasFields(this);
				}
			}
			else
			{
				if (i_fields == null)
				{
					i_fields = new com.db4o.@internal.FieldMetadata[0];
				}
			}
			SetStateOK();
		}

		private bool CollectReflectFields(com.db4o.@internal.ObjectContainerBase stream, 
			com.db4o.foundation.Collection4 collectedFields)
		{
			bool dirty = false;
			com.db4o.reflect.ReflectField[] fields = ClassReflector().GetDeclaredFields();
			for (int i = 0; i < fields.Length; i++)
			{
				if (StoreField(fields[i]))
				{
					com.db4o.@internal.TypeHandler4 wrapper = stream.i_handlers.HandlerForClass(stream
						, fields[i].GetFieldType());
					if (wrapper == null)
					{
						continue;
					}
					com.db4o.@internal.FieldMetadata field = new com.db4o.@internal.FieldMetadata(this
						, fields[i], wrapper);
					bool found = false;
					System.Collections.IEnumerator m = collectedFields.GetEnumerator();
					while (m.MoveNext())
					{
						if (((com.db4o.@internal.FieldMetadata)m.Current).Equals(field))
						{
							found = true;
							break;
						}
					}
					if (found)
					{
						continue;
					}
					dirty = true;
					collectedFields.Add(field);
				}
			}
			return dirty;
		}

		private bool AddTranslatorFields(com.db4o.@internal.ObjectContainerBase a_stream)
		{
			com.db4o.config.ObjectTranslator ot = GetTranslator();
			if (ot == null)
			{
				return false;
			}
			if (IsNewTranslator(ot))
			{
				i_stream.SetDirtyInSystemTransaction(this);
			}
			int fieldCount = 1;
			bool versions = GenerateVersionNumbers() && !AncestorHasVersionField();
			bool uuids = GenerateUUIDs() && !AncestorHasUUIDField();
			if (versions)
			{
				fieldCount = 2;
			}
			if (uuids)
			{
				fieldCount = 3;
			}
			i_fields = new com.db4o.@internal.FieldMetadata[fieldCount];
			i_fields[0] = new com.db4o.@internal.TranslatedFieldMetadata(this, ot);
			if (versions || uuids)
			{
				i_fields[1] = a_stream.GetVersionIndex();
			}
			if (uuids)
			{
				i_fields[2] = a_stream.GetUUIDIndex();
			}
			SetStateOK();
			return true;
		}

		private com.db4o.config.ObjectTranslator GetTranslator()
		{
			return i_config == null ? null : i_config.GetTranslator();
		}

		private bool IsNewTranslator(com.db4o.config.ObjectTranslator ot)
		{
			return !HasFields() || !j4o.lang.JavaSystem.GetClassForObject(ot).GetName().Equals
				(i_fields[0].GetName());
		}

		private bool HasFields()
		{
			return i_fields != null && i_fields.Length > 0;
		}

		internal virtual void AddToIndex(com.db4o.@internal.LocalObjectContainer a_stream
			, com.db4o.@internal.Transaction a_trans, int a_id)
		{
			if (a_stream.MaintainsIndices())
			{
				AddToIndex1(a_stream, a_trans, a_id);
			}
		}

		internal virtual void AddToIndex1(com.db4o.@internal.LocalObjectContainer a_stream
			, com.db4o.@internal.Transaction a_trans, int a_id)
		{
			if (i_ancestor != null)
			{
				i_ancestor.AddToIndex1(a_stream, a_trans, a_id);
			}
			if (HasIndex())
			{
				_index.Add(a_trans, a_id);
			}
		}

		internal virtual bool AllowsQueries()
		{
			return HasIndex();
		}

		public virtual bool CanHold(com.db4o.reflect.ReflectClass claxx)
		{
			if (claxx == null)
			{
				return true;
			}
			if (_reflector != null)
			{
				if (ClassReflector().IsCollection())
				{
					return true;
				}
				return ClassReflector().IsAssignableFrom(claxx);
			}
			return false;
		}

		public virtual void CascadeActivation(com.db4o.@internal.Transaction a_trans, object
			 a_object, int a_depth, bool a_activate)
		{
			com.db4o.@internal.Config4Class config = ConfigOrAncestorConfig();
			if (config != null)
			{
				if (a_activate)
				{
					a_depth = config.AdjustActivationDepth(a_depth);
				}
			}
			if (a_depth > 0)
			{
				com.db4o.@internal.ObjectContainerBase stream = a_trans.Stream();
				if (a_activate)
				{
					if (IsValueType())
					{
						ActivateFields(a_trans, a_object, a_depth - 1);
					}
					else
					{
						stream.StillToActivate(a_object, a_depth - 1);
					}
				}
				else
				{
					stream.StillToDeactivate(a_object, a_depth - 1, false);
				}
			}
		}

		internal virtual void CheckChanges()
		{
			if (StateOK())
			{
				if (!BitIsTrue(com.db4o.@internal.Const4.CHECKED_CHANGES))
				{
					BitTrue(com.db4o.@internal.Const4.CHECKED_CHANGES);
					if (i_ancestor != null)
					{
						i_ancestor.CheckChanges();
					}
					if (_reflector != null)
					{
						AddMembers(i_stream);
						if (!i_stream.IsClient())
						{
							Write(i_stream.GetSystemTransaction());
						}
					}
				}
			}
		}

		public virtual void CheckType()
		{
			com.db4o.reflect.ReflectClass claxx = ClassReflector();
			if (claxx == null)
			{
				return;
			}
			if (i_stream.i_handlers.ICLASS_INTERNAL.IsAssignableFrom(claxx))
			{
				_internal = true;
			}
			if (i_stream.i_handlers.ICLASS_UNVERSIONED.IsAssignableFrom(claxx))
			{
				_unversioned = true;
			}
			if (i_stream.i_handlers.ICLASS_DB4OTYPEIMPL.IsAssignableFrom(claxx))
			{
				com.db4o.@internal.Db4oTypeImpl db4oTypeImpl = (com.db4o.@internal.Db4oTypeImpl)claxx
					.NewInstance();
				_classIndexed = (db4oTypeImpl == null || db4oTypeImpl.HasClassIndex());
			}
			else
			{
				if (i_config != null)
				{
					_classIndexed = i_config.Indexed();
				}
			}
		}

		public virtual void CheckUpdateDepth(com.db4o.@internal.StatefulBuffer a_bytes)
		{
			int depth = a_bytes.GetUpdateDepth();
			com.db4o.@internal.Config4Class config = ConfigOrAncestorConfig();
			if (depth == com.db4o.@internal.Const4.UNSPECIFIED)
			{
				depth = CheckUpdateDepthUnspecified(a_bytes.GetStream());
				if (ClassReflector().IsCollection())
				{
					depth = AdjustDepth(depth);
				}
			}
			if ((config != null && (config.CascadeOnDelete() == com.db4o.@internal.Const4.YES
				 || config.CascadeOnUpdate() == com.db4o.@internal.Const4.YES)))
			{
				depth = AdjustDepth(depth);
			}
			a_bytes.SetUpdateDepth(depth - 1);
		}

		private int AdjustDepth(int depth)
		{
			int depthBorder = Reflector().CollectionUpdateDepth(ClassReflector());
			if (depth > int.MinValue && depth < depthBorder)
			{
				depth = depthBorder;
			}
			return depth;
		}

		internal virtual int CheckUpdateDepthUnspecified(com.db4o.@internal.ObjectContainerBase
			 a_stream)
		{
			int depth = a_stream.ConfigImpl().UpdateDepth() + 1;
			if (i_config != null && i_config.UpdateDepth() != 0)
			{
				depth = i_config.UpdateDepth() + 1;
			}
			if (i_ancestor != null)
			{
				int ancestordepth = i_ancestor.CheckUpdateDepthUnspecified(a_stream);
				if (ancestordepth > depth)
				{
					return ancestordepth;
				}
			}
			return depth;
		}

		public virtual object Coerce(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			return CanHold(claxx) ? obj : com.db4o.foundation.No4.INSTANCE;
		}

		public virtual void CollectConstraints(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.query.processor.QConObject
			 a_parent, object a_object, com.db4o.foundation.Visitor4 a_visitor)
		{
			if (i_fields != null)
			{
				for (int i = 0; i < i_fields.Length; i++)
				{
					i_fields[i].CollectConstraints(a_trans, a_parent, a_object, a_visitor);
				}
			}
			if (i_ancestor != null)
			{
				i_ancestor.CollectConstraints(a_trans, a_parent, a_object, a_visitor);
			}
		}

		public com.db4o.@internal.TreeInt CollectFieldIDs(com.db4o.@internal.marshall.MarshallerFamily
			 mf, com.db4o.@internal.marshall.ObjectHeaderAttributes attributes, com.db4o.@internal.TreeInt
			 tree, com.db4o.@internal.StatefulBuffer a_bytes, string name)
		{
			return mf._object.CollectFieldIDs(tree, this, attributes, a_bytes, name);
		}

		public bool ConfigInstantiates()
		{
			return i_config != null && i_config.Instantiates();
		}

		public virtual com.db4o.@internal.Config4Class Config()
		{
			return i_config;
		}

		public virtual com.db4o.@internal.Config4Class ConfigOrAncestorConfig()
		{
			if (i_config != null)
			{
				return i_config;
			}
			if (i_ancestor != null)
			{
				return i_ancestor.ConfigOrAncestorConfig();
			}
			return null;
		}

		public virtual void CopyValue(object a_from, object a_to)
		{
		}

		private bool CreateConstructor(com.db4o.@internal.ObjectContainerBase a_stream, string
			 a_name)
		{
			com.db4o.reflect.ReflectClass claxx;
			try
			{
				claxx = a_stream.Reflector().ForName(a_name);
			}
			catch
			{
				claxx = null;
			}
			return CreateConstructor(a_stream, claxx, a_name, true);
		}

		public virtual bool CreateConstructor(com.db4o.@internal.ObjectContainerBase a_stream
			, com.db4o.reflect.ReflectClass a_class, string a_name, bool errMessages)
		{
			_reflector = a_class;
			_eventDispatcher = com.db4o.@internal.EventDispatcher.ForClass(a_stream, a_class);
			if (ConfigInstantiates())
			{
				return true;
			}
			if (a_class != null)
			{
				if (a_stream.i_handlers.ICLASS_TRANSIENTCLASS.IsAssignableFrom(a_class) || com.db4o.@internal.Platform4
					.IsTransient(a_class))
				{
					a_class = null;
				}
			}
			if (a_class == null)
			{
				if (a_name == null || !com.db4o.@internal.Platform4.IsDb4oClass(a_name))
				{
					if (errMessages)
					{
						a_stream.LogMsg(23, a_name);
					}
				}
				SetStateDead();
				return false;
			}
			if (a_stream.i_handlers.CreateConstructor(a_class, !CallConstructor()))
			{
				return true;
			}
			SetStateDead();
			if (errMessages)
			{
				a_stream.LogMsg(7, a_name);
			}
			if (a_stream.ConfigImpl().ExceptionsOnNotStorable())
			{
				throw new com.db4o.ext.ObjectNotStorableException(a_class);
			}
			return false;
		}

		public virtual void Deactivate(com.db4o.@internal.Transaction a_trans, object a_object
			, int a_depth)
		{
			if (ObjectCanDeactivate(a_trans.Stream(), a_object))
			{
				Deactivate1(a_trans, a_object, a_depth);
				ObjectOnDeactivate(a_trans.Stream(), a_object);
			}
		}

		private void ObjectOnDeactivate(com.db4o.@internal.ObjectContainerBase stream, object
			 obj)
		{
			stream.Callbacks().ObjectOnDeactivate(obj);
			DispatchEvent(stream, obj, com.db4o.@internal.EventDispatcher.DEACTIVATE);
		}

		private bool ObjectCanDeactivate(com.db4o.@internal.ObjectContainerBase stream, object
			 obj)
		{
			return stream.Callbacks().ObjectCanDeactivate(obj) && DispatchEvent(stream, obj, 
				com.db4o.@internal.EventDispatcher.CAN_DEACTIVATE);
		}

		internal virtual void Deactivate1(com.db4o.@internal.Transaction a_trans, object 
			a_object, int a_depth)
		{
			for (int i = 0; i < i_fields.Length; i++)
			{
				i_fields[i].Deactivate(a_trans, a_object, a_depth);
			}
			if (i_ancestor != null)
			{
				i_ancestor.Deactivate1(a_trans, a_object, a_depth);
			}
		}

		internal void Delete(com.db4o.@internal.StatefulBuffer a_bytes, object a_object)
		{
			com.db4o.@internal.marshall.ObjectHeader oh = new com.db4o.@internal.marshall.ObjectHeader
				(i_stream, this, a_bytes);
			Delete1(oh._marshallerFamily, oh._headerAttributes, a_bytes, a_object);
		}

		private void Delete1(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.StatefulBuffer a_bytes, object a_object)
		{
			RemoveFromIndex(a_bytes.GetTransaction(), a_bytes.GetID());
			DeleteMembers(mf, attributes, a_bytes, a_bytes.GetTransaction().Stream().i_handlers
				.ArrayType(a_object), false);
		}

		public virtual void DeleteEmbedded(com.db4o.@internal.marshall.MarshallerFamily mf
			, com.db4o.@internal.StatefulBuffer a_bytes)
		{
			if (a_bytes.CascadeDeletes() > 0)
			{
				int id = a_bytes.ReadInt();
				if (id > 0)
				{
					DeleteEmbedded1(mf, a_bytes, id);
				}
			}
			else
			{
				a_bytes.IncrementOffset(LinkLength());
			}
		}

		public virtual void DeleteEmbedded1(com.db4o.@internal.marshall.MarshallerFamily 
			mf, com.db4o.@internal.StatefulBuffer a_bytes, int a_id)
		{
			if (a_bytes.CascadeDeletes() > 0)
			{
				com.db4o.@internal.ObjectContainerBase stream = a_bytes.GetStream();
				object obj = stream.GetByID2(a_bytes.GetTransaction(), a_id);
				int cascade = a_bytes.CascadeDeletes() - 1;
				if (obj != null)
				{
					if (IsCollection(obj))
					{
						cascade += Reflector().CollectionUpdateDepth(Reflector().ForObject(obj)) - 1;
					}
				}
				com.db4o.@internal.ObjectReference yo = stream.GetYapObject(a_id);
				if (yo != null)
				{
					a_bytes.GetStream().Delete2(a_bytes.GetTransaction(), yo, obj, cascade, false);
				}
			}
		}

		internal virtual void DeleteMembers(com.db4o.@internal.marshall.MarshallerFamily 
			mf, com.db4o.@internal.marshall.ObjectHeaderAttributes attributes, com.db4o.@internal.StatefulBuffer
			 a_bytes, int a_type, bool isUpdate)
		{
			try
			{
				com.db4o.@internal.Config4Class config = ConfigOrAncestorConfig();
				if (config != null && (config.CascadeOnDelete() == com.db4o.@internal.Const4.YES)
					)
				{
					int preserveCascade = a_bytes.CascadeDeletes();
					if (ClassReflector().IsCollection())
					{
						int newCascade = preserveCascade + Reflector().CollectionUpdateDepth(ClassReflector
							()) - 3;
						if (newCascade < 1)
						{
							newCascade = 1;
						}
						a_bytes.SetCascadeDeletes(newCascade);
					}
					else
					{
						a_bytes.SetCascadeDeletes(1);
					}
					mf._object.DeleteMembers(this, attributes, a_bytes, a_type, isUpdate);
					a_bytes.SetCascadeDeletes(preserveCascade);
				}
				else
				{
					mf._object.DeleteMembers(this, attributes, a_bytes, a_type, isUpdate);
				}
			}
			catch (System.Exception e)
			{
			}
		}

		public bool DispatchEvent(com.db4o.@internal.ObjectContainerBase stream, object obj
			, int message)
		{
			if (_eventDispatcher == null || !stream.DispatchsEvents())
			{
				return true;
			}
			return _eventDispatcher.Dispatch(stream, obj, message);
		}

		public bool Equals(com.db4o.@internal.TypeHandler4 a_dataType)
		{
			return (this == a_dataType);
		}

		public int FieldCount()
		{
			int count = i_fields.Length;
			if (i_ancestor != null)
			{
				count += i_ancestor.FieldCount();
			}
			return count;
		}

		private class FieldMetadataIterator : System.Collections.IEnumerator
		{
			private readonly com.db4o.@internal.ClassMetadata _initialClazz;

			private com.db4o.@internal.ClassMetadata _curClazz;

			private int _curIdx;

			public FieldMetadataIterator(com.db4o.@internal.ClassMetadata clazz)
			{
				_initialClazz = clazz;
				Reset();
			}

			public virtual object Current
			{
				get
				{
					return _curClazz.i_fields[_curIdx];
				}
			}

			public virtual bool MoveNext()
			{
				if (_curClazz == null)
				{
					_curClazz = _initialClazz;
					_curIdx = 0;
				}
				else
				{
					_curIdx++;
				}
				while (_curClazz != null && !IndexInRange())
				{
					_curClazz = _curClazz.i_ancestor;
					_curIdx = 0;
				}
				return _curClazz != null && IndexInRange();
			}

			public virtual void Reset()
			{
				_curClazz = null;
				_curIdx = -1;
			}

			private bool IndexInRange()
			{
				return _curIdx < _curClazz.i_fields.Length;
			}
		}

		public virtual System.Collections.IEnumerator Fields()
		{
			return new com.db4o.@internal.ClassMetadata.FieldMetadataIterator(this);
		}

		public com.db4o.@internal.marshall.MarshallerFamily FindOffset(com.db4o.@internal.Buffer
			 a_bytes, com.db4o.@internal.FieldMetadata a_field)
		{
			if (a_bytes == null)
			{
				return null;
			}
			a_bytes._offset = 0;
			com.db4o.@internal.marshall.ObjectHeader oh = new com.db4o.@internal.marshall.ObjectHeader
				(i_stream, this, a_bytes);
			bool res = oh.ObjectMarshaller().FindOffset(this, oh._headerAttributes, a_bytes, 
				a_field);
			if (!res)
			{
				return null;
			}
			return oh._marshallerFamily;
		}

		internal virtual void ForEachYapField(com.db4o.foundation.Visitor4 visitor)
		{
			if (i_fields != null)
			{
				for (int i = 0; i < i_fields.Length; i++)
				{
					visitor.Visit(i_fields[i]);
				}
			}
			if (i_ancestor != null)
			{
				i_ancestor.ForEachYapField(visitor);
			}
		}

		public static com.db4o.@internal.ClassMetadata ForObject(com.db4o.@internal.Transaction
			 trans, object obj, bool allowCreation)
		{
			com.db4o.reflect.ReflectClass reflectClass = trans.Reflector().ForObject(obj);
			if (reflectClass != null && reflectClass.GetSuperclass() == null && obj != null)
			{
				throw new com.db4o.ext.ObjectNotStorableException(obj.ToString());
			}
			if (allowCreation)
			{
				return trans.Stream().ProduceYapClass(reflectClass);
			}
			return trans.Stream().GetYapClass(reflectClass);
		}

		public virtual bool GenerateUUIDs()
		{
			if (!GenerateVirtual())
			{
				return false;
			}
			int configValue = (i_config == null) ? 0 : i_config.GenerateUUIDs();
			return Generate1(i_stream.Config().GenerateUUIDs(), configValue);
		}

		private bool GenerateVersionNumbers()
		{
			if (!GenerateVirtual())
			{
				return false;
			}
			int configValue = (i_config == null) ? 0 : i_config.GenerateVersionNumbers();
			return Generate1(i_stream.Config().GenerateVersionNumbers(), configValue);
		}

		private bool GenerateVirtual()
		{
			if (_unversioned)
			{
				return false;
			}
			if (_internal)
			{
				return false;
			}
			return true;
		}

		private bool Generate1(int bootRecordValue, int configValue)
		{
			if (bootRecordValue < 0)
			{
				return false;
			}
			if (configValue < 0)
			{
				return false;
			}
			if (bootRecordValue > 1)
			{
				return true;
			}
			return configValue > 0;
		}

		internal virtual com.db4o.@internal.ClassMetadata GetAncestor()
		{
			return i_ancestor;
		}

		public virtual object GetComparableObject(object forObject)
		{
			if (i_config != null)
			{
				if (i_config.QueryAttributeProvider() != null)
				{
					return i_config.QueryAttributeProvider().Attribute(forObject);
				}
			}
			return forObject;
		}

		public virtual com.db4o.@internal.ClassMetadata GetHigherHierarchy(com.db4o.@internal.ClassMetadata
			 a_yapClass)
		{
			com.db4o.@internal.ClassMetadata yc = GetHigherHierarchy1(a_yapClass);
			if (yc != null)
			{
				return yc;
			}
			return a_yapClass.GetHigherHierarchy1(this);
		}

		private com.db4o.@internal.ClassMetadata GetHigherHierarchy1(com.db4o.@internal.ClassMetadata
			 a_yapClass)
		{
			if (a_yapClass == this)
			{
				return this;
			}
			if (i_ancestor != null)
			{
				return i_ancestor.GetHigherHierarchy1(a_yapClass);
			}
			return null;
		}

		public virtual com.db4o.@internal.ClassMetadata GetHigherOrCommonHierarchy(com.db4o.@internal.ClassMetadata
			 a_yapClass)
		{
			com.db4o.@internal.ClassMetadata yc = GetHigherHierarchy1(a_yapClass);
			if (yc != null)
			{
				return yc;
			}
			if (i_ancestor != null)
			{
				yc = i_ancestor.GetHigherOrCommonHierarchy(a_yapClass);
				if (yc != null)
				{
					return yc;
				}
			}
			return a_yapClass.GetHigherHierarchy1(this);
		}

		public override byte GetIdentifier()
		{
			return com.db4o.@internal.Const4.YAPCLASS;
		}

		public virtual long[] GetIDs()
		{
			lock (i_stream.i_lock)
			{
				if (!StateOK())
				{
					return new long[0];
				}
				return GetIDs(i_stream.GetTransaction());
			}
		}

		public virtual long[] GetIDs(com.db4o.@internal.Transaction trans)
		{
			if (!StateOK())
			{
				return new long[0];
			}
			if (!HasIndex())
			{
				return new long[0];
			}
			return trans.Stream().GetIDsForClass(trans, this);
		}

		public virtual bool HasIndex()
		{
			return _classIndexed;
		}

		private bool AncestorHasUUIDField()
		{
			if (i_ancestor == null)
			{
				return false;
			}
			return i_ancestor.HasUUIDField();
		}

		private bool HasUUIDField()
		{
			if (AncestorHasUUIDField())
			{
				return true;
			}
			return com.db4o.foundation.Arrays4.ContainsInstanceOf(i_fields, j4o.lang.JavaSystem.GetClassForType
				(typeof(com.db4o.@internal.UUIDFieldMetadata)));
		}

		private bool AncestorHasVersionField()
		{
			if (i_ancestor == null)
			{
				return false;
			}
			return i_ancestor.HasVersionField();
		}

		private bool HasVersionField()
		{
			if (AncestorHasVersionField())
			{
				return true;
			}
			return com.db4o.foundation.Arrays4.ContainsInstanceOf(i_fields, j4o.lang.JavaSystem.GetClassForType
				(typeof(com.db4o.@internal.VersionFieldMetadata)));
		}

		public virtual com.db4o.@internal.classindex.ClassIndexStrategy Index()
		{
			return _index;
		}

		public virtual int IndexEntryCount(com.db4o.@internal.Transaction ta)
		{
			if (!StateOK())
			{
				return 0;
			}
			return _index.EntryCount(ta);
		}

		public virtual object IndexEntryToObject(com.db4o.@internal.Transaction trans, object
			 indexEntry)
		{
			if (indexEntry == null)
			{
				return null;
			}
			int id = ((int)indexEntry);
			return GetStream().GetByID2(trans, id);
		}

		public virtual com.db4o.reflect.ReflectClass ClassReflector()
		{
			return _reflector;
		}

		public virtual string GetName()
		{
			if (i_name == null)
			{
				if (_reflector != null)
				{
					i_name = _reflector.GetName();
				}
			}
			return i_name;
		}

		public virtual com.db4o.ext.StoredClass GetParentStoredClass()
		{
			return GetAncestor();
		}

		public virtual com.db4o.ext.StoredField[] GetStoredFields()
		{
			lock (i_stream.i_lock)
			{
				if (i_fields == null)
				{
					return null;
				}
				com.db4o.ext.StoredField[] fields = new com.db4o.ext.StoredField[i_fields.Length];
				System.Array.Copy(i_fields, 0, fields, 0, i_fields.Length);
				return fields;
			}
		}

		internal virtual com.db4o.@internal.ObjectContainerBase GetStream()
		{
			return i_stream;
		}

		public virtual int GetTypeID()
		{
			return com.db4o.@internal.Const4.TYPE_CLASS;
		}

		public virtual com.db4o.@internal.ClassMetadata GetYapClass(com.db4o.@internal.ObjectContainerBase
			 a_stream)
		{
			return this;
		}

		public virtual com.db4o.@internal.FieldMetadata GetYapField(string name)
		{
			com.db4o.@internal.FieldMetadata[] yf = new com.db4o.@internal.FieldMetadata[1];
			ForEachYapField(new _AnonymousInnerClass928(this, name, yf));
			return yf[0];
		}

		private sealed class _AnonymousInnerClass928 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass928(ClassMetadata _enclosing, string name, com.db4o.@internal.FieldMetadata[]
				 yf)
			{
				this._enclosing = _enclosing;
				this.name = name;
				this.yf = yf;
			}

			public void Visit(object obj)
			{
				if (name.Equals(((com.db4o.@internal.FieldMetadata)obj).GetName()))
				{
					yf[0] = (com.db4o.@internal.FieldMetadata)obj;
				}
			}

			private readonly ClassMetadata _enclosing;

			private readonly string name;

			private readonly com.db4o.@internal.FieldMetadata[] yf;
		}

		public virtual bool HasFixedLength()
		{
			return true;
		}

		public virtual bool HasField(com.db4o.@internal.ObjectContainerBase a_stream, string
			 a_field)
		{
			if (ClassReflector().IsCollection())
			{
				return true;
			}
			return GetYapField(a_field) != null;
		}

		internal virtual bool HasVirtualAttributes()
		{
			if (_internal)
			{
				return false;
			}
			return HasVersionField() || HasUUIDField();
		}

		public virtual bool HoldsAnyClass()
		{
			return ClassReflector().IsCollection();
		}

		internal virtual void IncrementFieldsOffset1(com.db4o.@internal.Buffer a_bytes)
		{
			int length = com.db4o.Debug.atHome ? ReadFieldCountSodaAtHome(a_bytes) : ReadFieldCount
				(a_bytes);
			for (int i = 0; i < length; i++)
			{
				i_fields[i].IncrementOffset(a_bytes);
			}
		}

		public virtual object ComparableObject(com.db4o.@internal.Transaction a_trans, object
			 a_object)
		{
			return a_object;
		}

		internal bool Init(com.db4o.@internal.ObjectContainerBase a_stream, com.db4o.@internal.ClassMetadata
			 a_ancestor, com.db4o.reflect.ReflectClass claxx)
		{
			i_ancestor = a_ancestor;
			com.db4o.@internal.Config4Impl config = a_stream.ConfigImpl();
			string className = claxx.GetName();
			SetConfig(config.ConfigClass(className));
			if (!CreateConstructor(a_stream, claxx, className, false))
			{
				return false;
			}
			CheckType();
			if (AllowsQueries())
			{
				_index.Initialize(a_stream);
			}
			i_name = className;
			i_ancestor = a_ancestor;
			BitTrue(com.db4o.@internal.Const4.CHECKED_CHANGES);
			return true;
		}

		internal void InitConfigOnUp(com.db4o.@internal.Transaction systemTrans)
		{
			com.db4o.@internal.Config4Class extendedConfig = com.db4o.@internal.Platform4.ExtendConfiguration
				(_reflector, i_stream.Configure(), i_config);
			if (extendedConfig != null)
			{
				i_config = extendedConfig;
			}
			if (i_config == null)
			{
				return;
			}
			if (!StateOK())
			{
				return;
			}
			if (i_fields == null)
			{
				return;
			}
			for (int i = 0; i < i_fields.Length; i++)
			{
				com.db4o.@internal.FieldMetadata curField = i_fields[i];
				string fieldName = curField.GetName();
				if (!curField.HasConfig() && extendedConfig != null && extendedConfig.ConfigField
					(fieldName) != null)
				{
					curField.InitIndex(this, fieldName);
				}
				curField.InitConfigOnUp(systemTrans);
			}
		}

		internal virtual void InitOnUp(com.db4o.@internal.Transaction systemTrans)
		{
			if (!StateOK())
			{
				return;
			}
			InitConfigOnUp(systemTrans);
			StoreStaticFieldValues(systemTrans, false);
		}

		internal virtual object Instantiate(com.db4o.@internal.ObjectReference yapObject, 
			object obj, com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.StatefulBuffer buffer, bool a_addToIDTree)
		{
			AdjustInstantiationDepth(buffer);
			com.db4o.@internal.ObjectContainerBase stream = buffer.GetStream();
			bool instantiating = (obj == null);
			if (instantiating)
			{
				obj = InstantiateObject(buffer, mf);
				if (obj == null)
				{
					return null;
				}
				ShareTransaction(obj, buffer.GetTransaction());
				ShareYapObject(obj, yapObject);
				yapObject.SetObjectWeak(stream, obj);
				stream.HcTreeAdd(yapObject);
			}
			if (a_addToIDTree)
			{
				yapObject.AddToIDTree(stream);
			}
			bool doFields = buffer.GetInstantiationDepth() > 0 || CascadeOnActivate();
			if (doFields && !ActivatingAlreadyActiveObject(instantiating, stream, yapObject))
			{
				if (ObjectCanActivate(stream, obj))
				{
					yapObject.SetStateClean();
					InstantiateFields(yapObject, obj, mf, attributes, buffer);
					ObjectOnActivate(stream, obj);
				}
				else
				{
					if (instantiating)
					{
						yapObject.SetStateDeactivated();
					}
				}
			}
			else
			{
				if (instantiating)
				{
					yapObject.SetStateDeactivated();
				}
				else
				{
					if (buffer.GetInstantiationDepth() > 1)
					{
						ActivateFields(buffer.GetTransaction(), obj, buffer.GetInstantiationDepth() - 1);
					}
				}
			}
			return obj;
		}

		private bool ActivatingAlreadyActiveObject(bool instantiating, com.db4o.@internal.ObjectContainerBase
			 stream, com.db4o.@internal.ObjectReference yapObject)
		{
			return !instantiating && !stream.i_refreshInsteadOfActivate && yapObject.IsActive
				();
		}

		private object InstantiateObject(com.db4o.@internal.StatefulBuffer a_bytes, com.db4o.@internal.marshall.MarshallerFamily
			 mf)
		{
			object instance = null;
			if (ConfigInstantiates())
			{
				instance = InstantiateFromConfig(a_bytes.GetStream(), a_bytes, mf);
			}
			else
			{
				instance = InstantiateFromReflector(a_bytes.GetStream());
			}
			return instance;
		}

		private object InstantiateFromReflector(com.db4o.@internal.ObjectContainerBase stream
			)
		{
			if (_reflector == null)
			{
				return null;
			}
			stream.Instantiating(true);
			try
			{
				return _reflector.NewInstance();
			}
			catch (System.MissingMethodException)
			{
				stream.LogMsg(7, ClassReflector().GetName());
				return null;
			}
			catch
			{
				return null;
			}
			finally
			{
				stream.Instantiating(false);
			}
		}

		private object InstantiateFromConfig(com.db4o.@internal.ObjectContainerBase stream
			, com.db4o.@internal.StatefulBuffer a_bytes, com.db4o.@internal.marshall.MarshallerFamily
			 mf)
		{
			int bytesOffset = a_bytes._offset;
			a_bytes.IncrementOffset(com.db4o.@internal.Const4.INT_LENGTH);
			try
			{
				return i_config.Instantiate(stream, i_fields[0].Read(mf, a_bytes));
			}
			catch (System.Exception e)
			{
				com.db4o.@internal.Messages.LogErr(stream.ConfigImpl(), 6, ClassReflector().GetName
					(), e);
				return null;
			}
			finally
			{
				a_bytes._offset = bytesOffset;
			}
		}

		private void AdjustInstantiationDepth(com.db4o.@internal.StatefulBuffer a_bytes)
		{
			if (i_config != null)
			{
				a_bytes.SetInstantiationDepth(i_config.AdjustActivationDepth(a_bytes.GetInstantiationDepth
					()));
			}
		}

		private bool CascadeOnActivate()
		{
			return i_config != null && (i_config.CascadeOnActivate() == com.db4o.@internal.Const4
				.YES);
		}

		private void ShareYapObject(object obj, com.db4o.@internal.ObjectReference yapObj
			)
		{
			if (obj is com.db4o.@internal.Db4oTypeImpl)
			{
				((com.db4o.@internal.Db4oTypeImpl)obj).SetYapObject(yapObj);
			}
		}

		private void ShareTransaction(object obj, com.db4o.@internal.Transaction transaction
			)
		{
			if (obj is com.db4o.TransactionAware)
			{
				((com.db4o.TransactionAware)obj).SetTrans(transaction);
			}
		}

		private void ObjectOnActivate(com.db4o.@internal.ObjectContainerBase stream, object
			 obj)
		{
			stream.Callbacks().ObjectOnActivate(obj);
			DispatchEvent(stream, obj, com.db4o.@internal.EventDispatcher.ACTIVATE);
		}

		private bool ObjectCanActivate(com.db4o.@internal.ObjectContainerBase stream, object
			 obj)
		{
			return stream.Callbacks().ObjectCanActivate(obj) && DispatchEvent(stream, obj, com.db4o.@internal.EventDispatcher
				.CAN_ACTIVATE);
		}

		internal virtual object InstantiateTransient(com.db4o.@internal.ObjectReference yapObject
			, object obj, com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.StatefulBuffer buffer)
		{
			obj = InstantiateObject(buffer, mf);
			if (obj == null)
			{
				return null;
			}
			buffer.GetStream().Peeked(yapObject.GetID(), obj);
			InstantiateFields(yapObject, obj, mf, attributes, buffer);
			return obj;
		}

		internal virtual void InstantiateFields(com.db4o.@internal.ObjectReference a_yapObject
			, object a_onObject, com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.StatefulBuffer a_bytes)
		{
			mf._object.InstantiateFields(this, attributes, a_yapObject, a_onObject, a_bytes);
		}

		public virtual bool IndexNullHandling()
		{
			return true;
		}

		public virtual bool IsArray()
		{
			return ClassReflector().IsCollection();
		}

		internal virtual bool IsCollection(object obj)
		{
			return Reflector().ForObject(obj).IsCollection();
		}

		public override bool IsDirty()
		{
			if (!StateOK())
			{
				return false;
			}
			return base.IsDirty();
		}

		internal virtual bool IsEnum()
		{
			return _isEnum;
		}

		public virtual bool IsPrimitive()
		{
			return false;
		}

		public virtual int IsSecondClass()
		{
			return com.db4o.@internal.Const4.NO;
		}

		/// <summary>no any, primitive, array or other tricks.</summary>
		/// <remarks>
		/// no any, primitive, array or other tricks. overriden in YapClassAny and
		/// YapClassPrimitive
		/// </remarks>
		public virtual bool IsStrongTyped()
		{
			return true;
		}

		internal virtual bool IsValueType()
		{
			return com.db4o.@internal.Platform4.IsValueType(ClassReflector());
		}

		public virtual void CalculateLengths(com.db4o.@internal.Transaction trans, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 header, bool topLevel, object obj, bool withIndirection)
		{
			if (topLevel)
			{
				header.AddBaseLength(LinkLength());
			}
			else
			{
				header.AddPayLoadLength(LinkLength());
			}
		}

		public virtual string NameToWrite()
		{
			if (i_config != null && i_config.WriteAs() != null)
			{
				return i_config.WriteAs();
			}
			if (i_name == null)
			{
				return string.Empty;
			}
			return i_stream.ConfigImpl().ResolveAliasRuntimeName(i_name);
		}

		internal bool CallConstructor()
		{
			i_dontCallConstructors = !CallConstructor1();
			return !i_dontCallConstructors;
		}

		private bool CallConstructor1()
		{
			int res = CallConstructorSpecialized();
			if (res != com.db4o.@internal.Const4.DEFAULT)
			{
				return res == com.db4o.@internal.Const4.YES;
			}
			return (i_stream.ConfigImpl().CallConstructors() == com.db4o.@internal.Const4.YES
				);
		}

		private int CallConstructorSpecialized()
		{
			if (i_config != null)
			{
				int res = i_config.CallConstructor();
				if (res != com.db4o.@internal.Const4.DEFAULT)
				{
					return res;
				}
			}
			if (_isEnum)
			{
				return com.db4o.@internal.Const4.NO;
			}
			if (i_ancestor != null)
			{
				return i_ancestor.CallConstructorSpecialized();
			}
			return com.db4o.@internal.Const4.DEFAULT;
		}

		public override int OwnLength()
		{
			return com.db4o.@internal.marshall.MarshallerFamily.Current()._class.MarshalledLength
				(i_stream, this);
		}

		public virtual com.db4o.reflect.ReflectClass PrimitiveClassReflector()
		{
			return null;
		}

		internal virtual void Purge()
		{
			_index.Purge();
		}

		public virtual object Read(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 a_bytes, bool redirect)
		{
			try
			{
				int id = a_bytes.ReadInt();
				int depth = a_bytes.GetInstantiationDepth() - 1;
				com.db4o.@internal.Transaction trans = a_bytes.GetTransaction();
				com.db4o.@internal.ObjectContainerBase stream = trans.Stream();
				if (a_bytes.GetUpdateDepth() == com.db4o.@internal.Const4.TRANSIENT)
				{
					return stream.PeekPersisted1(trans, id, depth);
				}
				if (IsValueType())
				{
					if (depth < 1)
					{
						depth = 1;
					}
					com.db4o.@internal.ObjectReference yo = stream.GetYapObject(id);
					if (yo != null)
					{
						object obj = yo.GetObject();
						if (obj == null)
						{
							stream.RemoveReference(yo);
						}
						else
						{
							yo.Activate(trans, obj, depth, false);
							return yo.GetObject();
						}
					}
					return new com.db4o.@internal.ObjectReference(id).Read(trans, null, null, depth, 
						com.db4o.@internal.Const4.ADD_TO_ID_TREE, false);
				}
				object ret = stream.GetByID2(trans, id);
				if (ret is com.db4o.@internal.Db4oTypeImpl)
				{
					depth = ((com.db4o.@internal.Db4oTypeImpl)ret).AdjustReadDepth(depth);
				}
				stream.StillToActivate(ret, depth);
				return ret;
			}
			catch
			{
			}
			return null;
		}

		public virtual object ReadQuery(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.marshall.MarshallerFamily
			 mf, bool withRedirection, com.db4o.@internal.Buffer a_reader, bool a_toArray)
		{
			try
			{
				return a_trans.Stream().GetByID2(a_trans, a_reader.ReadInt());
			}
			catch (System.Exception e)
			{
			}
			return null;
		}

		public virtual com.db4o.@internal.TypeHandler4 ReadArrayHandler(com.db4o.@internal.Transaction
			 a_trans, com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.Buffer[]
			 a_bytes)
		{
			if (IsArray())
			{
				return this;
			}
			return null;
		}

		public virtual com.db4o.@internal.TypeHandler4 ReadArrayHandler1(com.db4o.@internal.Buffer[]
			 a_bytes)
		{
			if (IsArray())
			{
				if (com.db4o.@internal.Platform4.IsCollectionTranslator(this.i_config))
				{
					a_bytes[0].IncrementOffset(com.db4o.@internal.Const4.INT_LENGTH);
					return new com.db4o.@internal.handlers.ArrayHandler(i_stream, null, false);
				}
				IncrementFieldsOffset1(a_bytes[0]);
				if (i_ancestor != null)
				{
					return i_ancestor.ReadArrayHandler1(a_bytes);
				}
			}
			return null;
		}

		public virtual com.db4o.@internal.query.processor.QCandidate ReadSubCandidate(com.db4o.@internal.marshall.MarshallerFamily
			 mf, com.db4o.@internal.Buffer reader, com.db4o.@internal.query.processor.QCandidates
			 candidates, bool withIndirection)
		{
			int id = reader.ReadInt();
			if (id == 0)
			{
				return null;
			}
			return new com.db4o.@internal.query.processor.QCandidate(candidates, null, id, true
				);
		}

		public virtual void ReadCandidates(com.db4o.@internal.marshall.MarshallerFamily mf
			, com.db4o.@internal.Buffer a_bytes, com.db4o.@internal.query.processor.QCandidates
			 a_candidates)
		{
			int id = 0;
			int offset = a_bytes._offset;
			try
			{
				id = a_bytes.ReadInt();
			}
			catch
			{
			}
			a_bytes._offset = offset;
			if (id != 0)
			{
				com.db4o.@internal.Transaction trans = a_candidates.i_trans;
				object obj = trans.Stream().GetByID1(trans, id);
				if (obj != null)
				{
					a_candidates.i_trans.Stream().Activate1(trans, obj, 2);
					com.db4o.@internal.Platform4.ForEachCollectionElement(obj, new _AnonymousInnerClass1412
						(this, a_candidates, trans));
				}
			}
		}

		private sealed class _AnonymousInnerClass1412 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass1412(ClassMetadata _enclosing, com.db4o.@internal.query.processor.QCandidates
				 a_candidates, com.db4o.@internal.Transaction trans)
			{
				this._enclosing = _enclosing;
				this.a_candidates = a_candidates;
				this.trans = trans;
			}

			public void Visit(object elem)
			{
				a_candidates.AddByIdentity(new com.db4o.@internal.query.processor.QCandidate(a_candidates
					, elem, (int)trans.Stream().GetID(elem), true));
			}

			private readonly ClassMetadata _enclosing;

			private readonly com.db4o.@internal.query.processor.QCandidates a_candidates;

			private readonly com.db4o.@internal.Transaction trans;
		}

		public int ReadFieldCount(com.db4o.@internal.Buffer a_bytes)
		{
			int count = a_bytes.ReadInt();
			if (count > i_fields.Length)
			{
				return i_fields.Length;
			}
			return count;
		}

		public virtual int ReadFieldCountSodaAtHome(com.db4o.@internal.Buffer a_bytes)
		{
			return 0;
		}

		public virtual object ReadIndexEntry(com.db4o.@internal.Buffer a_reader)
		{
			return a_reader.ReadInt();
		}

		public virtual object ReadIndexEntry(com.db4o.@internal.marshall.MarshallerFamily
			 mf, com.db4o.@internal.StatefulBuffer a_writer)
		{
			return ReadIndexEntry(a_writer);
		}

		internal virtual byte[] ReadName(com.db4o.@internal.Transaction a_trans)
		{
			i_reader = a_trans.Stream().ReadReaderByID(a_trans, GetID());
			return ReadName1(a_trans, i_reader);
		}

		public byte[] ReadName1(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer
			 reader)
		{
			if (reader == null)
			{
				return null;
			}
			i_reader = reader;
			try
			{
				com.db4o.@internal.marshall.ClassMarshaller marshaller = com.db4o.@internal.marshall.MarshallerFamily
					.Current()._class;
				i_nameBytes = marshaller.ReadName(trans, reader);
				_metaClassID = marshaller.ReadMetaClassID(reader);
				SetStateUnread();
				BitFalse(com.db4o.@internal.Const4.CHECKED_CHANGES);
				BitFalse(com.db4o.@internal.Const4.STATIC_FIELDS_STORED);
				return i_nameBytes;
			}
			catch (System.Exception t)
			{
				SetStateDead();
			}
			return null;
		}

		internal virtual void ReadVirtualAttributes(com.db4o.@internal.Transaction a_trans
			, com.db4o.@internal.ObjectReference a_yapObject)
		{
			int id = a_yapObject.GetID();
			com.db4o.@internal.ObjectContainerBase stream = a_trans.Stream();
			com.db4o.@internal.Buffer reader = stream.ReadReaderByID(a_trans, id);
			com.db4o.@internal.marshall.ObjectHeader oh = new com.db4o.@internal.marshall.ObjectHeader
				(stream, this, reader);
			oh.ObjectMarshaller().ReadVirtualAttributes(a_trans, this, a_yapObject, oh._headerAttributes
				, reader);
		}

		internal virtual com.db4o.reflect.generic.GenericReflector Reflector()
		{
			return i_stream.Reflector();
		}

		public virtual void Rename(string newName)
		{
			if (!i_stream.IsClient())
			{
				int tempState = i_state;
				SetStateOK();
				i_name = newName;
				SetStateDirty();
				Write(i_stream.GetSystemTransaction());
				i_state = tempState;
			}
			else
			{
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(58);
			}
		}

		internal virtual void CreateConfigAndConstructor(com.db4o.foundation.Hashtable4 a_byteHashTable
			, com.db4o.@internal.ObjectContainerBase a_stream, com.db4o.reflect.ReflectClass
			 a_class)
		{
			if (a_class == null)
			{
				if (i_nameBytes != null)
				{
					string name = a_stream.StringIO().Read(i_nameBytes);
					i_name = a_stream.ConfigImpl().ResolveAliasStoredName(name);
				}
			}
			else
			{
				i_name = a_class.GetName();
			}
			SetConfig(i_stream.ConfigImpl().ConfigClass(i_name));
			if (a_class == null)
			{
				CreateConstructor(a_stream, i_name);
			}
			else
			{
				CreateConstructor(a_stream, a_class, i_name, true);
			}
			if (i_nameBytes != null)
			{
				a_byteHashTable.Remove(i_nameBytes);
				i_nameBytes = null;
			}
		}

		internal virtual bool ReadThis()
		{
			if (StateUnread())
			{
				SetStateOK();
				SetStateClean();
				ForceRead();
				return true;
			}
			return false;
		}

		internal void ForceRead()
		{
			if (i_reader == null || BitIsTrue(com.db4o.@internal.Const4.READING))
			{
				return;
			}
			BitTrue(com.db4o.@internal.Const4.READING);
			com.db4o.@internal.marshall.MarshallerFamily.ForConverterVersion(i_stream.ConverterVersion
				())._class.Read(i_stream, this, i_reader);
			i_nameBytes = null;
			i_reader = null;
			BitFalse(com.db4o.@internal.Const4.READING);
		}

		public virtual bool ReadArray(object array, com.db4o.@internal.Buffer reader)
		{
			return false;
		}

		public override void ReadThis(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.Buffer
			 a_reader)
		{
			throw com.db4o.@internal.Exceptions4.VirtualException();
		}

		public virtual void Refresh()
		{
			if (!StateUnread())
			{
				CreateConstructor(i_stream, i_name);
				BitFalse(com.db4o.@internal.Const4.CHECKED_CHANGES);
				CheckChanges();
				if (i_fields != null)
				{
					for (int i = 0; i < i_fields.Length; i++)
					{
						i_fields[i].Refresh();
					}
				}
			}
		}

		internal virtual void RemoveFromIndex(com.db4o.@internal.Transaction ta, int id)
		{
			if (HasIndex())
			{
				_index.Remove(ta, id);
			}
			if (i_ancestor != null)
			{
				i_ancestor.RemoveFromIndex(ta, id);
			}
		}

		internal virtual bool RenameField(string a_from, string a_to)
		{
			bool renamed = false;
			for (int i = 0; i < i_fields.Length; i++)
			{
				if (i_fields[i].GetName().Equals(a_to))
				{
					i_stream.LogMsg(9, "class:" + GetName() + " field:" + a_to);
					return false;
				}
			}
			for (int i = 0; i < i_fields.Length; i++)
			{
				if (i_fields[i].GetName().Equals(a_from))
				{
					i_fields[i].SetName(a_to);
					renamed = true;
				}
			}
			return renamed;
		}

		internal virtual void SetConfig(com.db4o.@internal.Config4Class config)
		{
			if (i_config == null)
			{
				i_config = config;
			}
		}

		internal virtual void SetName(string a_name)
		{
			i_name = a_name;
		}

		private void SetStateDead()
		{
			BitTrue(com.db4o.@internal.Const4.DEAD);
			BitFalse(com.db4o.@internal.Const4.CONTINUE);
		}

		private void SetStateUnread()
		{
			BitFalse(com.db4o.@internal.Const4.DEAD);
			BitTrue(com.db4o.@internal.Const4.CONTINUE);
		}

		private void SetStateOK()
		{
			BitFalse(com.db4o.@internal.Const4.DEAD);
			BitFalse(com.db4o.@internal.Const4.CONTINUE);
		}

		internal virtual bool StateDead()
		{
			return BitIsTrue(com.db4o.@internal.Const4.DEAD);
		}

		private bool StateOK()
		{
			return BitIsFalse(com.db4o.@internal.Const4.CONTINUE) && BitIsFalse(com.db4o.@internal.Const4
				.DEAD) && BitIsFalse(com.db4o.@internal.Const4.READING);
		}

		internal bool StateOKAndAncestors()
		{
			if (!StateOK() || i_fields == null)
			{
				return false;
			}
			if (i_ancestor != null)
			{
				return i_ancestor.StateOKAndAncestors();
			}
			return true;
		}

		internal virtual bool StateUnread()
		{
			return BitIsTrue(com.db4o.@internal.Const4.CONTINUE) && BitIsFalse(com.db4o.@internal.Const4
				.DEAD) && BitIsFalse(com.db4o.@internal.Const4.READING);
		}

		internal virtual bool StoreField(com.db4o.reflect.ReflectField a_field)
		{
			if (a_field.IsStatic())
			{
				return false;
			}
			if (a_field.IsTransient())
			{
				com.db4o.@internal.Config4Class config = ConfigOrAncestorConfig();
				if (config == null)
				{
					return false;
				}
				if (!config.StoreTransientFields())
				{
					return false;
				}
			}
			return com.db4o.@internal.Platform4.CanSetAccessible() || a_field.IsPublic();
		}

		public virtual com.db4o.ext.StoredField StoredField(string a_name, object a_type)
		{
			lock (i_stream.i_lock)
			{
				com.db4o.@internal.ClassMetadata yc = i_stream.GetYapClass(i_stream.ConfigImpl().
					ReflectorFor(a_type));
				if (i_fields != null)
				{
					for (int i = 0; i < i_fields.Length; i++)
					{
						if (i_fields[i].GetName().Equals(a_name))
						{
							if (yc == null || yc == i_fields[i].GetFieldYapClass(i_stream))
							{
								return (i_fields[i]);
							}
						}
					}
				}
				return null;
			}
		}

		internal virtual void StoreStaticFieldValues(com.db4o.@internal.Transaction trans
			, bool force)
		{
			if (!BitIsTrue(com.db4o.@internal.Const4.STATIC_FIELDS_STORED) || force)
			{
				BitTrue(com.db4o.@internal.Const4.STATIC_FIELDS_STORED);
				bool store = (i_config != null && i_config.StaticFieldValuesArePersisted()) || com.db4o.@internal.Platform4
					.StoreStaticFieldValues(trans.Reflector(), ClassReflector());
				if (store)
				{
					com.db4o.@internal.ObjectContainerBase stream = trans.Stream();
					stream.ShowInternalClasses(true);
					com.db4o.query.Query q = stream.Query(trans);
					q.Constrain(com.db4o.@internal.Const4.CLASS_STATICCLASS);
					q.Descend("name").Constrain(i_name);
					com.db4o.StaticClass sc = new com.db4o.StaticClass();
					sc.name = i_name;
					com.db4o.ObjectSet os = q.Execute();
					com.db4o.StaticField[] oldFields = null;
					if (os.Size() > 0)
					{
						sc = (com.db4o.StaticClass)os.Next();
						stream.Activate1(trans, sc, 4);
						oldFields = sc.fields;
					}
					com.db4o.reflect.ReflectField[] fields = ClassReflector().GetDeclaredFields();
					com.db4o.foundation.Collection4 newFields = new com.db4o.foundation.Collection4();
					for (int i = 0; i < fields.Length; i++)
					{
						if (fields[i].IsStatic())
						{
							fields[i].SetAccessible();
							string fieldName = fields[i].GetName();
							object value = fields[i].Get(null);
							bool handled = false;
							if (oldFields != null)
							{
								for (int j = 0; j < oldFields.Length; j++)
								{
									if (fieldName.Equals(oldFields[j].name))
									{
										if (oldFields[j].value != null && value != null && j4o.lang.JavaSystem.GetClassForObject
											(oldFields[j].value) == j4o.lang.JavaSystem.GetClassForObject(value))
										{
											long id = stream.GetID1(oldFields[j].value);
											if (id > 0)
											{
												if (oldFields[j].value != value)
												{
													stream.Bind1(trans, value, id);
													stream.Refresh(value, int.MaxValue);
													oldFields[j].value = value;
												}
												handled = true;
											}
										}
										if (!handled)
										{
											if (value == null)
											{
												try
												{
													fields[i].Set(null, oldFields[j].value);
												}
												catch
												{
												}
											}
											else
											{
												oldFields[j].value = value;
												if (!stream.IsClient())
												{
													stream.SetInternal(trans, oldFields[j], true);
												}
											}
										}
										newFields.Add(oldFields[j]);
										handled = true;
										break;
									}
								}
							}
							if (!handled)
							{
								newFields.Add(new com.db4o.StaticField(fieldName, value));
							}
						}
					}
					if (newFields.Size() > 0)
					{
						sc.fields = new com.db4o.StaticField[newFields.Size()];
						newFields.ToArray(sc.fields);
						if (!stream.IsClient())
						{
							stream.SetInternal(trans, sc, true);
						}
					}
					stream.ShowInternalClasses(false);
				}
			}
		}

		public virtual bool SupportsIndex()
		{
			return true;
		}

		public override string ToString()
		{
			if (i_name != null)
			{
				return i_name;
			}
			if (i_nameBytes == null)
			{
				return "*CLASS NAME UNKNOWN*";
			}
			com.db4o.@internal.LatinStringIO stringIO = i_stream == null ? com.db4o.@internal.Const4
				.stringIO : i_stream.StringIO();
			return stringIO.Read(i_nameBytes);
		}

		public virtual bool WriteArray(object array, com.db4o.@internal.Buffer reader)
		{
			return false;
		}

		public override bool WriteObjectBegin()
		{
			if (!StateOK())
			{
				return false;
			}
			return base.WriteObjectBegin();
		}

		public virtual void WriteIndexEntry(com.db4o.@internal.Buffer a_writer, object a_object
			)
		{
			if (a_object == null)
			{
				a_writer.WriteInt(0);
				return;
			}
			a_writer.WriteInt(((int)a_object));
		}

		public virtual object WriteNew(com.db4o.@internal.marshall.MarshallerFamily mf, object
			 a_object, bool topLevel, com.db4o.@internal.StatefulBuffer a_bytes, bool withIndirection
			, bool restoreLinkOffset)
		{
			if (a_object == null)
			{
				a_bytes.WriteInt(0);
				return 0;
			}
			int id = a_bytes.GetStream().SetInternal(a_bytes.GetTransaction(), a_object, a_bytes
				.GetUpdateDepth(), true);
			a_bytes.WriteInt(id);
			return id;
		}

		public sealed override void WriteThis(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer
			 writer)
		{
			com.db4o.@internal.marshall.MarshallerFamily.Current()._class.Write(trans, this, 
				writer);
		}

		private com.db4o.reflect.ReflectClass i_compareTo;

		public virtual void PrepareComparison(com.db4o.@internal.Transaction a_trans, object
			 obj)
		{
			PrepareComparison(obj);
		}

		public virtual com.db4o.@internal.Comparable4 PrepareComparison(object obj)
		{
			if (obj != null)
			{
				if (obj is int)
				{
					i_lastID = ((int)obj);
				}
				else
				{
					i_lastID = (int)i_stream.GetID(obj);
				}
				i_compareTo = Reflector().ForObject(obj);
			}
			else
			{
				i_lastID = 0;
				i_compareTo = null;
			}
			return this;
		}

		public virtual object Current()
		{
			if (i_compareTo == null)
			{
				return null;
			}
			return i_lastID;
		}

		public virtual int CompareTo(object a_obj)
		{
			if (a_obj is int)
			{
				return ((int)a_obj) - i_lastID;
			}
			if ((a_obj == null) && (i_compareTo == null))
			{
				return 0;
			}
			return -1;
		}

		public virtual bool IsEqual(object obj)
		{
			if (obj == null)
			{
				return i_compareTo == null;
			}
			return i_compareTo.IsAssignableFrom(Reflector().ForObject(obj));
		}

		public virtual bool IsGreater(object obj)
		{
			return false;
		}

		public virtual bool IsSmaller(object obj)
		{
			return false;
		}

		public virtual string ToString(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 writer, com.db4o.@internal.ObjectReference yapObject, int depth, int maxDepth)
		{
			int length = ReadFieldCount(writer);
			string str = string.Empty;
			for (int i = 0; i < length; i++)
			{
				str += i_fields[i].ToString(mf, writer);
			}
			if (i_ancestor != null)
			{
				str += i_ancestor.ToString(mf, writer, yapObject, depth, maxDepth);
			}
			return str;
		}

		public static void DefragObject(com.db4o.@internal.ReaderPair readers)
		{
			com.db4o.@internal.marshall.ObjectHeader header = com.db4o.@internal.marshall.ObjectHeader
				.Defrag(readers);
			header._marshallerFamily._object.DefragFields(header.YapClass(), header, readers);
		}

		public virtual void Defrag(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.ReaderPair
			 readers, bool redirect)
		{
			if (HasIndex())
			{
				readers.CopyID();
			}
			else
			{
				readers.CopyUnindexedID();
			}
			int restLength = (LinkLength() - com.db4o.@internal.Const4.INT_LENGTH);
			readers.IncrementOffset(restLength);
		}

		public virtual void DefragClass(com.db4o.@internal.ReaderPair readers, int classIndexID
			)
		{
			com.db4o.@internal.marshall.MarshallerFamily mf = com.db4o.@internal.marshall.MarshallerFamily
				.Current();
			mf._class.Defrag(this, i_stream.StringIO(), readers, classIndexID);
		}

		public static com.db4o.@internal.ClassMetadata ReadClass(com.db4o.@internal.ObjectContainerBase
			 stream, com.db4o.@internal.Buffer reader)
		{
			com.db4o.@internal.marshall.ObjectHeader oh = new com.db4o.@internal.marshall.ObjectHeader
				(stream, reader);
			return oh.YapClass();
		}

		public virtual bool IsAssignableFrom(com.db4o.@internal.ClassMetadata other)
		{
			return ClassReflector().IsAssignableFrom(other.ClassReflector());
		}

		public void DefragIndexEntry(com.db4o.@internal.ReaderPair readers)
		{
			readers.CopyID();
		}
	}
}
