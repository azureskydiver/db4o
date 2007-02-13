namespace com.db4o.reflect.generic
{
	/// <exclude></exclude>
	public class GenericReflector : com.db4o.reflect.Reflector, com.db4o.foundation.DeepClone
	{
		private com.db4o.reflect.generic.KnownClassesRepository _repository;

		private com.db4o.reflect.Reflector _delegate;

		private com.db4o.reflect.generic.GenericArrayReflector _array;

		private com.db4o.foundation.Collection4 _collectionPredicates = new com.db4o.foundation.Collection4
			();

		private com.db4o.foundation.Collection4 _collectionUpdateDepths = new com.db4o.foundation.Collection4
			();

		private readonly com.db4o.foundation.Hashtable4 _classByClass = new com.db4o.foundation.Hashtable4
			();

		private com.db4o.@internal.Transaction _trans;

		private com.db4o.@internal.ObjectContainerBase _stream;

		public GenericReflector(com.db4o.@internal.Transaction trans, com.db4o.reflect.Reflector
			 delegateReflector)
		{
			_repository = new com.db4o.reflect.generic.KnownClassesRepository(new com.db4o.reflect.generic.GenericClassBuilder
				(this, delegateReflector));
			SetTransaction(trans);
			_delegate = delegateReflector;
			if (_delegate != null)
			{
				_delegate.SetParent(this);
			}
		}

		public virtual object DeepClone(object obj)
		{
			com.db4o.reflect.generic.GenericReflector myClone = new com.db4o.reflect.generic.GenericReflector
				(null, (com.db4o.reflect.Reflector)_delegate.DeepClone(this));
			myClone._collectionPredicates = (com.db4o.foundation.Collection4)_collectionPredicates
				.DeepClone(myClone);
			myClone._collectionUpdateDepths = (com.db4o.foundation.Collection4)_collectionUpdateDepths
				.DeepClone(myClone);
			return myClone;
		}

		internal virtual com.db4o.@internal.ObjectContainerBase GetStream()
		{
			return _stream;
		}

		public virtual bool HasTransaction()
		{
			return _trans != null;
		}

		public virtual void SetTransaction(com.db4o.@internal.Transaction trans)
		{
			if (trans != null)
			{
				_trans = trans;
				_stream = trans.Stream();
			}
			_repository.SetTransaction(trans);
		}

		public virtual com.db4o.reflect.ReflectArray Array()
		{
			if (_array == null)
			{
				_array = new com.db4o.reflect.generic.GenericArrayReflector(this);
			}
			return _array;
		}

		public virtual int CollectionUpdateDepth(com.db4o.reflect.ReflectClass candidate)
		{
			System.Collections.IEnumerator i = _collectionUpdateDepths.GetEnumerator();
			while (i.MoveNext())
			{
				com.db4o.reflect.generic.CollectionUpdateDepthEntry entry = (com.db4o.reflect.generic.CollectionUpdateDepthEntry
					)i.Current;
				if (entry._predicate.Match(candidate))
				{
					return entry._depth;
				}
			}
			return 2;
		}

		public virtual bool ConstructorCallsSupported()
		{
			return _delegate.ConstructorCallsSupported();
		}

		internal virtual com.db4o.reflect.generic.GenericClass EnsureDelegate(com.db4o.reflect.ReflectClass
			 clazz)
		{
			if (clazz == null)
			{
				return null;
			}
			com.db4o.reflect.generic.GenericClass claxx = (com.db4o.reflect.generic.GenericClass
				)_repository.LookupByName(clazz.GetName());
			if (claxx == null)
			{
				claxx = GenericClass(clazz);
				_repository.Register(claxx);
			}
			return claxx;
		}

		private com.db4o.reflect.generic.GenericClass GenericClass(com.db4o.reflect.ReflectClass
			 clazz)
		{
			com.db4o.reflect.generic.GenericClass ret;
			string name = clazz.GetName();
			if (name.Equals(j4o.lang.JavaSystem.GetClassForType(typeof(com.db4o.reflect.generic.GenericArray)
				).GetName()))
			{
				ret = new com.db4o.reflect.generic.GenericArrayClass(this, clazz, name, null);
			}
			else
			{
				ret = new com.db4o.reflect.generic.GenericClass(this, clazz, name, null);
			}
			return ret;
		}

		public virtual com.db4o.reflect.ReflectClass ForClass(j4o.lang.Class clazz)
		{
			if (clazz == null)
			{
				return null;
			}
			com.db4o.reflect.ReflectClass claxx = (com.db4o.reflect.ReflectClass)_classByClass
				.Get(clazz);
			if (claxx != null)
			{
				return claxx;
			}
			claxx = ForName(clazz.GetName());
			if (claxx != null)
			{
				_classByClass.Put(clazz, claxx);
				return claxx;
			}
			claxx = _delegate.ForClass(clazz);
			if (claxx == null)
			{
				return null;
			}
			claxx = EnsureDelegate(claxx);
			_classByClass.Put(clazz, claxx);
			return claxx;
		}

		public virtual com.db4o.reflect.ReflectClass ForName(string className)
		{
			com.db4o.reflect.ReflectClass clazz = _repository.LookupByName(className);
			if (clazz != null)
			{
				return clazz;
			}
			clazz = _delegate.ForName(className);
			if (clazz != null)
			{
				return EnsureDelegate(clazz);
			}
			return _repository.ForName(className);
		}

		public virtual com.db4o.reflect.ReflectClass ForObject(object obj)
		{
			if (obj is com.db4o.reflect.generic.GenericObject)
			{
				return ForGenericObject((com.db4o.reflect.generic.GenericObject)obj);
			}
			return _delegate.ForObject(obj);
		}

		private com.db4o.reflect.ReflectClass ForGenericObject(com.db4o.reflect.generic.GenericObject
			 genericObject)
		{
			com.db4o.reflect.generic.GenericClass claxx = genericObject._class;
			if (claxx == null)
			{
				throw new System.InvalidOperationException();
			}
			string name = claxx.GetName();
			if (name == null)
			{
				throw new System.InvalidOperationException();
			}
			com.db4o.reflect.generic.GenericClass existingClass = (com.db4o.reflect.generic.GenericClass
				)ForName(name);
			if (existingClass == null)
			{
				_repository.Register(claxx);
				return claxx;
			}
			if (existingClass != claxx)
			{
				throw new System.InvalidOperationException();
			}
			return claxx;
		}

		public virtual com.db4o.reflect.Reflector GetDelegate()
		{
			return _delegate;
		}

		public virtual bool IsCollection(com.db4o.reflect.ReflectClass candidate)
		{
			System.Collections.IEnumerator i = _collectionPredicates.GetEnumerator();
			while (i.MoveNext())
			{
				if (((com.db4o.reflect.ReflectClassPredicate)i.Current).Match(candidate))
				{
					return true;
				}
			}
			return _delegate.IsCollection(candidate.GetDelegate());
		}

		public virtual void RegisterCollection(j4o.lang.Class clazz)
		{
			RegisterCollection(ClassPredicate(clazz));
		}

		public virtual void RegisterCollection(com.db4o.reflect.ReflectClassPredicate predicate
			)
		{
			_collectionPredicates.Add(predicate);
		}

		private com.db4o.reflect.ReflectClassPredicate ClassPredicate(j4o.lang.Class clazz
			)
		{
			com.db4o.reflect.ReflectClass collectionClass = ForClass(clazz);
			com.db4o.reflect.ReflectClassPredicate predicate = new _AnonymousInnerClass220(this
				, collectionClass);
			return predicate;
		}

		private sealed class _AnonymousInnerClass220 : com.db4o.reflect.ReflectClassPredicate
		{
			public _AnonymousInnerClass220(GenericReflector _enclosing, com.db4o.reflect.ReflectClass
				 collectionClass)
			{
				this._enclosing = _enclosing;
				this.collectionClass = collectionClass;
			}

			public bool Match(com.db4o.reflect.ReflectClass candidate)
			{
				return collectionClass.IsAssignableFrom(candidate);
			}

			private readonly GenericReflector _enclosing;

			private readonly com.db4o.reflect.ReflectClass collectionClass;
		}

		public virtual void RegisterCollectionUpdateDepth(j4o.lang.Class clazz, int depth
			)
		{
			RegisterCollectionUpdateDepth(ClassPredicate(clazz), depth);
		}

		public virtual void RegisterCollectionUpdateDepth(com.db4o.reflect.ReflectClassPredicate
			 predicate, int depth)
		{
			_collectionUpdateDepths.Add(new com.db4o.reflect.generic.CollectionUpdateDepthEntry
				(predicate, depth));
		}

		public virtual void Register(com.db4o.reflect.generic.GenericClass clazz)
		{
			string name = clazz.GetName();
			if (_repository.LookupByName(name) == null)
			{
				_repository.Register(clazz);
			}
		}

		public virtual com.db4o.reflect.ReflectClass[] KnownClasses()
		{
			com.db4o.foundation.Collection4 classes = new com.db4o.foundation.Collection4();
			CollectKnownClasses(classes);
			return (com.db4o.reflect.ReflectClass[])classes.ToArray(new com.db4o.reflect.ReflectClass
				[classes.Size()]);
		}

		private void CollectKnownClasses(com.db4o.foundation.Collection4 classes)
		{
			System.Collections.IEnumerator i = _repository.Classes();
			while (i.MoveNext())
			{
				com.db4o.reflect.generic.GenericClass clazz = (com.db4o.reflect.generic.GenericClass
					)i.Current;
				if (!_stream.i_handlers.ICLASS_INTERNAL.IsAssignableFrom(clazz))
				{
					if (!clazz.IsSecondClass())
					{
						if (!clazz.IsArray())
						{
							classes.Add(clazz);
						}
					}
				}
			}
		}

		public virtual void RegisterPrimitiveClass(int id, string name, com.db4o.reflect.generic.GenericConverter
			 converter)
		{
			com.db4o.reflect.generic.GenericClass existing = (com.db4o.reflect.generic.GenericClass
				)_repository.LookupByID(id);
			if (existing != null)
			{
				if (null != converter)
				{
					existing.SetSecondClass();
				}
				else
				{
					existing.SetConverter(null);
				}
				return;
			}
			com.db4o.reflect.ReflectClass clazz = _delegate.ForName(name);
			com.db4o.reflect.generic.GenericClass claxx = null;
			if (clazz != null)
			{
				claxx = EnsureDelegate(clazz);
			}
			else
			{
				claxx = new com.db4o.reflect.generic.GenericClass(this, null, name, null);
				Register(claxx);
				claxx.InitFields(new com.db4o.reflect.generic.GenericField[] { new com.db4o.reflect.generic.GenericField
					(null, null, true, false, false) });
				claxx.SetConverter(converter);
			}
			claxx.SetSecondClass();
			claxx.SetPrimitive();
			_repository.Register(id, claxx);
		}

		public virtual void SetParent(com.db4o.reflect.Reflector reflector)
		{
		}
	}
}
