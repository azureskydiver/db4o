namespace com.db4o.reflect.generic
{
	/// <exclude></exclude>
	public class GenericClass : com.db4o.reflect.ReflectClass, com.db4o.foundation.DeepClone
	{
		private static readonly com.db4o.reflect.generic.GenericField[] NO_FIELDS = new com.db4o.reflect.generic.GenericField
			[0];

		private readonly com.db4o.reflect.generic.GenericReflector _reflector;

		private readonly com.db4o.reflect.ReflectClass _delegate;

		private readonly string _name;

		private com.db4o.reflect.generic.GenericClass _superclass;

		private com.db4o.reflect.generic.GenericClass _array;

		private bool _isSecondClass;

		private bool _isPrimitive;

		private int _isCollection;

		private com.db4o.reflect.generic.GenericConverter _converter;

		private com.db4o.reflect.generic.GenericField[] _fields = NO_FIELDS;

		private int _declaredFieldCount = -1;

		private int _fieldCount = -1;

		private readonly int _hashCode;

		public GenericClass(com.db4o.reflect.generic.GenericReflector reflector, com.db4o.reflect.ReflectClass
			 delegateClass, string name, com.db4o.reflect.generic.GenericClass superclass)
		{
			_reflector = reflector;
			_delegate = delegateClass;
			_name = name;
			_superclass = superclass;
			_hashCode = _name.GetHashCode();
		}

		public virtual com.db4o.reflect.generic.GenericClass ArrayClass()
		{
			if (_array != null)
			{
				return _array;
			}
			_array = new com.db4o.reflect.generic.GenericArrayClass(_reflector, this, _name, 
				_superclass);
			_array._isSecondClass = _isSecondClass;
			return _array;
		}

		public virtual object DeepClone(object obj)
		{
			com.db4o.reflect.generic.GenericReflector reflector = (com.db4o.reflect.generic.GenericReflector
				)obj;
			com.db4o.reflect.generic.GenericClass superClass = null;
			if (_superclass != null)
			{
				_superclass = (com.db4o.reflect.generic.GenericClass)reflector.ForName(_superclass
					.GetName());
			}
			com.db4o.reflect.generic.GenericClass ret = new com.db4o.reflect.generic.GenericClass
				(reflector, _delegate, _name, superClass);
			ret._isSecondClass = _isSecondClass;
			com.db4o.reflect.generic.GenericField[] fields = new com.db4o.reflect.generic.GenericField
				[_fields.Length];
			for (int i = 0; i < fields.Length; i++)
			{
				fields[i] = (com.db4o.reflect.generic.GenericField)_fields[i].DeepClone(reflector
					);
			}
			ret.InitFields(fields);
			return ret;
		}

		public override bool Equals(object obj)
		{
			if (obj == null)
			{
				return false;
			}
			if (this == obj)
			{
				return true;
			}
			if (!(obj is com.db4o.reflect.generic.GenericClass))
			{
				return false;
			}
			com.db4o.reflect.generic.GenericClass otherGC = (com.db4o.reflect.generic.GenericClass
				)obj;
			if (_hashCode != otherGC.GetHashCode())
			{
				return false;
			}
			return _name.Equals(otherGC._name);
		}

		public virtual com.db4o.reflect.ReflectClass GetComponentType()
		{
			if (_delegate != null)
			{
				return _delegate.GetComponentType();
			}
			return null;
		}

		public virtual com.db4o.reflect.ReflectConstructor[] GetDeclaredConstructors()
		{
			if (_delegate != null)
			{
				return _delegate.GetDeclaredConstructors();
			}
			return null;
		}

		public virtual com.db4o.reflect.ReflectField GetDeclaredField(string name)
		{
			if (_delegate != null)
			{
				return _delegate.GetDeclaredField(name);
			}
			for (int i = 0; i < _fields.Length; i++)
			{
				if (_fields[i].GetName().Equals(name))
				{
					return _fields[i];
				}
			}
			return null;
		}

		public virtual com.db4o.reflect.ReflectField[] GetDeclaredFields()
		{
			if (_delegate != null)
			{
				return _delegate.GetDeclaredFields();
			}
			return _fields;
		}

		public virtual com.db4o.reflect.ReflectClass GetDelegate()
		{
			if (_delegate != null)
			{
				return _delegate;
			}
			return this;
		}

		internal virtual int GetFieldCount()
		{
			if (_fieldCount != -1)
			{
				return _fieldCount;
			}
			_fieldCount = 0;
			if (_superclass != null)
			{
				_fieldCount = _superclass.GetFieldCount();
			}
			if (_declaredFieldCount == -1)
			{
				_declaredFieldCount = GetDeclaredFields().Length;
			}
			_fieldCount += _declaredFieldCount;
			return _fieldCount;
		}

		public virtual com.db4o.reflect.ReflectMethod GetMethod(string methodName, com.db4o.reflect.ReflectClass[]
			 paramClasses)
		{
			if (_delegate != null)
			{
				return _delegate.GetMethod(methodName, paramClasses);
			}
			return null;
		}

		public virtual string GetName()
		{
			return _name;
		}

		public virtual com.db4o.reflect.ReflectClass GetSuperclass()
		{
			if (_superclass != null)
			{
				return _superclass;
			}
			if (_delegate == null)
			{
				return _reflector.ForClass(j4o.lang.Class.GetClassForType(typeof(object)));
			}
			com.db4o.reflect.ReflectClass delegateSuperclass = _delegate.GetSuperclass();
			if (delegateSuperclass != null)
			{
				_superclass = _reflector.EnsureDelegate(delegateSuperclass);
			}
			return _superclass;
		}

		public override int GetHashCode()
		{
			return _hashCode;
		}

		public virtual void InitFields(com.db4o.reflect.generic.GenericField[] fields)
		{
			int startIndex = 0;
			if (_superclass != null)
			{
				startIndex = _superclass.GetFieldCount();
			}
			_fields = fields;
			for (int i = 0; i < _fields.Length; i++)
			{
				_fields[i].SetIndex(startIndex + i);
			}
		}

		public virtual bool IsAbstract()
		{
			if (_delegate != null)
			{
				return _delegate.IsAbstract();
			}
			return false;
		}

		public virtual bool IsArray()
		{
			if (_delegate != null)
			{
				return _delegate.IsArray();
			}
			return false;
		}

		public virtual bool IsAssignableFrom(com.db4o.reflect.ReflectClass subclassCandidate
			)
		{
			if (subclassCandidate == null)
			{
				return false;
			}
			if (Equals(subclassCandidate))
			{
				return true;
			}
			if (_delegate != null)
			{
				if (subclassCandidate is com.db4o.reflect.generic.GenericClass)
				{
					subclassCandidate = ((com.db4o.reflect.generic.GenericClass)subclassCandidate).GetDelegate
						();
				}
				return _delegate.IsAssignableFrom(subclassCandidate);
			}
			if (!(subclassCandidate is com.db4o.reflect.generic.GenericClass))
			{
				return false;
			}
			return IsAssignableFrom(subclassCandidate.GetSuperclass());
		}

		public virtual bool IsCollection()
		{
			if (_isCollection == 1)
			{
				return true;
			}
			if (_isCollection == -1)
			{
				return false;
			}
			_isCollection = _reflector.IsCollection(this) ? 1 : -1;
			return IsCollection();
		}

		public virtual bool IsInstance(object candidate)
		{
			if (_delegate != null)
			{
				return _delegate.IsInstance(candidate);
			}
			if (!(candidate is com.db4o.reflect.generic.GenericObject))
			{
				return false;
			}
			return IsAssignableFrom(((com.db4o.reflect.generic.GenericObject)candidate)._class
				);
		}

		public virtual bool IsInterface()
		{
			if (_delegate != null)
			{
				return _delegate.IsInterface();
			}
			return false;
		}

		public virtual bool IsPrimitive()
		{
			if (_delegate != null)
			{
				return _delegate.IsPrimitive();
			}
			return _isPrimitive;
		}

		public virtual bool IsSecondClass()
		{
			if (IsPrimitive())
			{
				return true;
			}
			return _isSecondClass;
		}

		public virtual object NewInstance()
		{
			if (_delegate != null)
			{
				return _delegate.NewInstance();
			}
			return new com.db4o.reflect.generic.GenericObject(this);
		}

		public virtual com.db4o.reflect.Reflector Reflector()
		{
			if (_delegate != null)
			{
				return _delegate.Reflector();
			}
			return _reflector;
		}

		internal virtual void SetConverter(com.db4o.reflect.generic.GenericConverter converter
			)
		{
			_converter = converter;
		}

		internal virtual void SetDeclaredFieldCount(int count)
		{
			_declaredFieldCount = count;
		}

		internal virtual void SetPrimitive()
		{
			_isPrimitive = true;
		}

		internal virtual void SetSecondClass()
		{
			_isSecondClass = true;
		}

		public virtual bool SkipConstructor(bool flag)
		{
			if (_delegate != null)
			{
				return _delegate.SkipConstructor(flag);
			}
			return false;
		}

		public override string ToString()
		{
			return "GenericClass " + _name;
		}

		public virtual string ToString(com.db4o.reflect.generic.GenericObject obj)
		{
			if (_converter == null)
			{
				return "(G) " + GetName();
			}
			return _converter.ToString(obj);
		}

		public virtual void UseConstructor(com.db4o.reflect.ReflectConstructor constructor
			, object[] @params)
		{
			if (_delegate != null)
			{
				_delegate.UseConstructor(constructor, @params);
			}
		}

		public virtual object[] ToArray(object obj)
		{
			if (!IsCollection())
			{
				return new object[] { obj };
			}
			return com.db4o.Platform4.CollectionToArray(_reflector.GetStream(), obj);
		}
	}
}
