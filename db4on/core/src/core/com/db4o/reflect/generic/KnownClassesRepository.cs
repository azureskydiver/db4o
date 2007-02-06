namespace com.db4o.reflect.generic
{
	public class KnownClassesRepository
	{
		private com.db4o.@internal.ObjectContainerBase _stream;

		private com.db4o.@internal.Transaction _trans;

		private com.db4o.reflect.generic.ReflectClassBuilder _builder;

		private readonly com.db4o.foundation.Hashtable4 _classByName = new com.db4o.foundation.Hashtable4
			();

		private readonly com.db4o.foundation.Hashtable4 _classByID = new com.db4o.foundation.Hashtable4
			();

		private com.db4o.foundation.Collection4 _pendingClasses = new com.db4o.foundation.Collection4
			();

		private readonly com.db4o.foundation.Collection4 _classes = new com.db4o.foundation.Collection4
			();

		public KnownClassesRepository(com.db4o.reflect.generic.ReflectClassBuilder builder
			)
		{
			_builder = builder;
		}

		public virtual void SetTransaction(com.db4o.@internal.Transaction trans)
		{
			if (trans != null)
			{
				_trans = trans;
				_stream = trans.Stream();
			}
		}

		public virtual void Register(com.db4o.reflect.ReflectClass clazz)
		{
			_classByName.Put(clazz.GetName(), clazz);
			_classes.Add(clazz);
		}

		public virtual com.db4o.reflect.ReflectClass ForID(int id)
		{
			if (_stream.Handlers().IsSystemHandler(id))
			{
				return _stream.HandlerByID(id).ClassReflector();
			}
			EnsureClassAvailability(id);
			return LookupByID(id);
		}

		public virtual com.db4o.reflect.ReflectClass ForName(string className)
		{
			com.db4o.reflect.ReflectClass clazz = (com.db4o.reflect.ReflectClass)_classByName
				.Get(className);
			if (clazz != null)
			{
				return clazz;
			}
			if (_stream == null)
			{
				return null;
			}
			if (_stream.ClassCollection() != null)
			{
				int classID = _stream.ClassCollection().GetYapClassID(className);
				if (classID > 0)
				{
					clazz = EnsureClassInitialised(classID);
					_classByName.Put(className, clazz);
					return clazz;
				}
			}
			return null;
		}

		private void ReadAll()
		{
			for (System.Collections.IEnumerator idIter = _stream.ClassCollection().Ids(); idIter
				.MoveNext(); )
			{
				EnsureClassAvailability(((int)idIter.Current));
			}
			for (System.Collections.IEnumerator idIter = _stream.ClassCollection().Ids(); idIter
				.MoveNext(); )
			{
				EnsureClassRead(((int)idIter.Current));
			}
		}

		private com.db4o.reflect.ReflectClass EnsureClassAvailability(int id)
		{
			if (id == 0)
			{
				return null;
			}
			com.db4o.reflect.ReflectClass ret = (com.db4o.reflect.ReflectClass)_classByID.Get
				(id);
			if (ret != null)
			{
				return ret;
			}
			com.db4o.@internal.Buffer classreader = _stream.ReadWriterByID(_trans, id);
			com.db4o.@internal.marshall.ClassMarshaller marshaller = MarshallerFamily()._class;
			com.db4o.@internal.marshall.RawClassSpec spec = marshaller.ReadSpec(_trans, classreader
				);
			string className = spec.Name();
			ret = (com.db4o.reflect.ReflectClass)_classByName.Get(className);
			if (ret != null)
			{
				_classByID.Put(id, ret);
				_pendingClasses.Add(id);
				return ret;
			}
			ret = _builder.CreateClass(className, EnsureClassAvailability(spec.SuperClassID()
				), spec.NumFields());
			_classByID.Put(id, ret);
			_pendingClasses.Add(id);
			return ret;
		}

		private void EnsureClassRead(int id)
		{
			com.db4o.reflect.ReflectClass clazz = LookupByID(id);
			com.db4o.@internal.Buffer classreader = _stream.ReadWriterByID(_trans, id);
			com.db4o.@internal.marshall.ClassMarshaller classMarshaller = MarshallerFamily().
				_class;
			com.db4o.@internal.marshall.RawClassSpec classInfo = classMarshaller.ReadSpec(_trans
				, classreader);
			string className = classInfo.Name();
			if (_classByName.Get(className) != null)
			{
				return;
			}
			_classByName.Put(className, clazz);
			_classes.Add(clazz);
			int numFields = classInfo.NumFields();
			com.db4o.reflect.ReflectField[] fields = _builder.FieldArray(numFields);
			com.db4o.@internal.marshall.FieldMarshaller fieldMarshaller = MarshallerFamily().
				_field;
			for (int i = 0; i < numFields; i++)
			{
				com.db4o.@internal.marshall.RawFieldSpec fieldInfo = fieldMarshaller.ReadSpec(_stream
					, classreader);
				string fieldName = fieldInfo.Name();
				int handlerID = fieldInfo.HandlerID();
				com.db4o.reflect.ReflectClass fieldClass = null;
				switch (handlerID)
				{
					case com.db4o.@internal.HandlerRegistry.ANY_ID:
					{
						fieldClass = _stream.Reflector().ForClass(j4o.lang.JavaSystem.GetClassForType(typeof(object)
							));
						break;
					}

					case com.db4o.@internal.HandlerRegistry.ANY_ARRAY_ID:
					{
						fieldClass = _builder.ArrayClass(_stream.Reflector().ForClass(j4o.lang.JavaSystem.GetClassForType
							(typeof(object))));
						break;
					}

					default:
					{
						fieldClass = ForID(handlerID);
						fieldClass = _stream.Reflector().ForName(fieldClass.GetName());
						break;
					}
				}
				fields[i] = _builder.CreateField(clazz, fieldName, fieldClass, fieldInfo.IsVirtual
					(), fieldInfo.IsPrimitive(), fieldInfo.IsArray(), fieldInfo.IsNArray());
			}
			_builder.InitFields(clazz, fields);
		}

		private com.db4o.@internal.marshall.MarshallerFamily MarshallerFamily()
		{
			return com.db4o.@internal.marshall.MarshallerFamily.ForConverterVersion(_stream.ConverterVersion
				());
		}

		private com.db4o.reflect.ReflectClass EnsureClassInitialised(int id)
		{
			com.db4o.reflect.ReflectClass ret = EnsureClassAvailability(id);
			while (_pendingClasses.Size() > 0)
			{
				com.db4o.foundation.Collection4 pending = _pendingClasses;
				_pendingClasses = new com.db4o.foundation.Collection4();
				System.Collections.IEnumerator i = pending.GetEnumerator();
				while (i.MoveNext())
				{
					EnsureClassRead(((int)i.Current));
				}
			}
			return ret;
		}

		public virtual System.Collections.IEnumerator Classes()
		{
			ReadAll();
			return _classes.GetEnumerator();
		}

		public virtual void Register(int id, com.db4o.reflect.ReflectClass clazz)
		{
			_classByID.Put(id, clazz);
		}

		public virtual com.db4o.reflect.ReflectClass LookupByID(int id)
		{
			return (com.db4o.reflect.ReflectClass)_classByID.Get(id);
		}

		public virtual com.db4o.reflect.ReflectClass LookupByName(string name)
		{
			return (com.db4o.reflect.ReflectClass)_classByName.Get(name);
		}
	}
}
