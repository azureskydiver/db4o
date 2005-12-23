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

		public virtual com.db4o.reflect.generic.GenericClass arrayClass()
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

		public virtual object deepClone(object obj)
		{
			com.db4o.reflect.generic.GenericReflector reflector = (com.db4o.reflect.generic.GenericReflector
				)obj;
			com.db4o.reflect.generic.GenericClass superClass = null;
			if (_superclass != null)
			{
				_superclass = (com.db4o.reflect.generic.GenericClass)reflector.forName(_superclass
					.getName());
			}
			com.db4o.reflect.generic.GenericClass ret = new com.db4o.reflect.generic.GenericClass
				(reflector, _delegate, _name, superClass);
			ret._isSecondClass = _isSecondClass;
			com.db4o.reflect.generic.GenericField[] fields = new com.db4o.reflect.generic.GenericField
				[_fields.Length];
			for (int i = 0; i < fields.Length; i++)
			{
				fields[i] = (com.db4o.reflect.generic.GenericField)_fields[i].deepClone(reflector
					);
			}
			ret.initFields(fields);
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

		public virtual com.db4o.reflect.ReflectClass getComponentType()
		{
			if (_delegate != null)
			{
				return _delegate.getComponentType();
			}
			return null;
		}

		public virtual com.db4o.reflect.ReflectConstructor[] getDeclaredConstructors()
		{
			if (_delegate != null)
			{
				return _delegate.getDeclaredConstructors();
			}
			return null;
		}

		public virtual com.db4o.reflect.ReflectField getDeclaredField(string name)
		{
			if (_delegate != null)
			{
				return _delegate.getDeclaredField(name);
			}
			for (int i = 0; i < _fields.Length; i++)
			{
				if (_fields[i].getName().Equals(name))
				{
					return _fields[i];
				}
			}
			return null;
		}

		public virtual com.db4o.reflect.ReflectField[] getDeclaredFields()
		{
			if (_delegate != null)
			{
				return _delegate.getDeclaredFields();
			}
			return _fields;
		}

		public virtual com.db4o.reflect.ReflectClass getDelegate()
		{
			if (_delegate != null)
			{
				return _delegate;
			}
			return this;
		}

		internal virtual int getFieldCount()
		{
			if (_fieldCount != -1)
			{
				return _fieldCount;
			}
			_fieldCount = 0;
			if (_superclass != null)
			{
				_fieldCount = _superclass.getFieldCount();
			}
			if (_declaredFieldCount == -1)
			{
				_declaredFieldCount = getDeclaredFields().Length;
			}
			_fieldCount += _declaredFieldCount;
			return _fieldCount;
		}

		public virtual com.db4o.reflect.ReflectMethod getMethod(string methodName, com.db4o.reflect.ReflectClass[]
			 paramClasses)
		{
			if (_delegate != null)
			{
				return _delegate.getMethod(methodName, paramClasses);
			}
			return null;
		}

		public virtual string getName()
		{
			return _name;
		}

		public virtual com.db4o.reflect.ReflectClass getSuperclass()
		{
			if (_superclass != null)
			{
				return _superclass;
			}
			if (_delegate == null)
			{
				return null;
			}
			com.db4o.reflect.ReflectClass delegateSuperclass = _delegate.getSuperclass();
			if (delegateSuperclass != null)
			{
				_superclass = _reflector.ensureDelegate(delegateSuperclass);
			}
			return _superclass;
		}

		public override int GetHashCode()
		{
			return _hashCode;
		}

		public virtual void initFields(com.db4o.reflect.generic.GenericField[] fields)
		{
			int startIndex = 0;
			if (_superclass != null)
			{
				startIndex = _superclass.getFieldCount();
			}
			_fields = fields;
			for (int i = 0; i < _fields.Length; i++)
			{
				_fields[i].setIndex(startIndex + i);
			}
		}

		public virtual bool isAbstract()
		{
			if (_delegate != null)
			{
				return _delegate.isAbstract();
			}
			return false;
		}

		public virtual bool isArray()
		{
			if (_delegate != null)
			{
				return _delegate.isArray();
			}
			return false;
		}

		public virtual bool isAssignableFrom(com.db4o.reflect.ReflectClass subclassCandidate
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
					subclassCandidate = ((com.db4o.reflect.generic.GenericClass)subclassCandidate).getDelegate
						();
				}
				return _delegate.isAssignableFrom(subclassCandidate);
			}
			if (!(subclassCandidate is com.db4o.reflect.generic.GenericClass))
			{
				return false;
			}
			return isAssignableFrom(subclassCandidate.getSuperclass());
		}

		public virtual bool isCollection()
		{
			if (_isCollection == 1)
			{
				return true;
			}
			if (_isCollection == -1)
			{
				return false;
			}
			_isCollection = _reflector.isCollection(this) ? 1 : -1;
			return isCollection();
		}

		public virtual bool isInstance(object candidate)
		{
			if (_delegate != null)
			{
				return _delegate.isInstance(candidate);
			}
			if (!(candidate is com.db4o.reflect.generic.GenericObject))
			{
				return false;
			}
			return isAssignableFrom(((com.db4o.reflect.generic.GenericObject)candidate)._class
				);
		}

		public virtual bool isInterface()
		{
			if (_delegate != null)
			{
				return _delegate.isInterface();
			}
			return false;
		}

		public virtual bool isPrimitive()
		{
			if (_delegate != null)
			{
				return _delegate.isPrimitive();
			}
			return _isPrimitive;
		}

		public virtual bool isSecondClass()
		{
			if (isPrimitive())
			{
				return true;
			}
			return _isSecondClass;
		}

		public virtual object newInstance()
		{
			if (_delegate != null)
			{
				return _delegate.newInstance();
			}
			return new com.db4o.reflect.generic.GenericObject(this);
		}

		public virtual com.db4o.reflect.Reflector reflector()
		{
			if (_delegate != null)
			{
				return _delegate.reflector();
			}
			return _reflector;
		}

		internal virtual void setConverter(com.db4o.reflect.generic.GenericConverter converter
			)
		{
			_converter = converter;
		}

		internal virtual void setDeclaredFieldCount(int count)
		{
			_declaredFieldCount = count;
		}

		internal virtual void setPrimitive()
		{
			_isPrimitive = true;
		}

		internal virtual void setSecondClass()
		{
			_isSecondClass = true;
		}

		public virtual bool skipConstructor(bool flag)
		{
			if (_delegate != null)
			{
				return _delegate.skipConstructor(flag);
			}
			return false;
		}

		public override string ToString()
		{
			return "GenericClass " + _name;
		}

		public virtual string toString(com.db4o.reflect.generic.GenericObject obj)
		{
			if (_converter == null)
			{
				return "(G) " + getName();
			}
			return _converter.toString(obj);
		}

		public virtual void useConstructor(com.db4o.reflect.ReflectConstructor constructor
			, object[] _params)
		{
			if (_delegate != null)
			{
				_delegate.useConstructor(constructor, _params);
			}
		}

		public virtual object[] toArray(object obj)
		{
			if (!isCollection())
			{
				return new object[] { obj };
			}
			return com.db4o.Platform4.collectionToArray(_reflector.getStream(), obj);
		}
	}
}
