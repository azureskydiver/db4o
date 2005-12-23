namespace com.db4o.reflect.self
{
	public class SelfClass : com.db4o.reflect.ReflectClass
	{
		private static readonly com.db4o.reflect.self.SelfField[] EMPTY_FIELDS = new com.db4o.reflect.self.SelfField
			[0];

		private bool _isAbstract;

		private com.db4o.reflect.self.SelfField[] _fields;

		private com.db4o.reflect.Reflector _parentReflector;

		private com.db4o.reflect.self.SelfReflectionRegistry _registry;

		private j4o.lang.Class _class;

		private j4o.lang.Class _superClass;

		public SelfClass(com.db4o.reflect.Reflector parentReflector, com.db4o.reflect.self.SelfReflectionRegistry
			 registry, j4o.lang.Class clazz)
		{
			_parentReflector = parentReflector;
			_registry = registry;
			_class = clazz;
		}

		public virtual j4o.lang.Class getJavaClass()
		{
			return _class;
		}

		public virtual com.db4o.reflect.Reflector reflector()
		{
			return _parentReflector;
		}

		public virtual com.db4o.reflect.ReflectClass getComponentType()
		{
			if (!isArray())
			{
				return null;
			}
			return _parentReflector.forClass(_registry.componentType(_class));
		}

		public virtual com.db4o.reflect.ReflectConstructor[] getDeclaredConstructors()
		{
			if (isInterface())
			{
				return new com.db4o.reflect.self.SelfConstructor[0];
			}
			return new com.db4o.reflect.self.SelfConstructor[] { new com.db4o.reflect.self.SelfConstructor
				(_class) };
		}

		public virtual com.db4o.reflect.ReflectField[] getDeclaredFields()
		{
			ensureClassInfoLoaded();
			return _fields;
		}

		private void ensureClassInfoLoaded()
		{
			if (_fields == null)
			{
				com.db4o.reflect.self.ClassInfo classInfo = _registry.infoFor(_class);
				if (classInfo == null)
				{
					_fields = EMPTY_FIELDS;
					return;
				}
				_superClass = classInfo.superClass();
				_isAbstract = classInfo.isAbstract();
				com.db4o.reflect.self.FieldInfo[] fieldInfo = classInfo.fieldInfo();
				if (fieldInfo == null)
				{
					_fields = EMPTY_FIELDS;
					return;
				}
				_fields = new com.db4o.reflect.self.SelfField[fieldInfo.Length];
				for (int idx = 0; idx < fieldInfo.Length; idx++)
				{
					_fields[idx] = selfFieldFor(fieldInfo[idx]);
				}
			}
		}

		public virtual com.db4o.reflect.ReflectField getDeclaredField(string name)
		{
			ensureClassInfoLoaded();
			for (int idx = 0; idx < _fields.Length; idx++)
			{
				if (_fields[idx].getName().Equals(name))
				{
					return _fields[idx];
				}
			}
			return null;
		}

		private com.db4o.reflect.self.SelfField selfFieldFor(com.db4o.reflect.self.FieldInfo
			 fieldInfo)
		{
			return new com.db4o.reflect.self.SelfField(fieldInfo.name(), _parentReflector.forClass
				(fieldInfo.type()), this, _registry);
		}

		public virtual com.db4o.reflect.ReflectClass getDelegate()
		{
			return this;
		}

		public virtual com.db4o.reflect.ReflectMethod getMethod(string methodName, com.db4o.reflect.ReflectClass[]
			 paramClasses)
		{
			return null;
		}

		public virtual string getName()
		{
			return _class.getName();
		}

		public virtual com.db4o.reflect.ReflectClass getSuperclass()
		{
			ensureClassInfoLoaded();
			if (_superClass == null)
			{
				return null;
			}
			return _parentReflector.forClass(_superClass);
		}

		public virtual bool isAbstract()
		{
			ensureClassInfoLoaded();
			return _isAbstract || isInterface();
		}

		public virtual bool isArray()
		{
			return _class.isArray();
		}

		public virtual bool isAssignableFrom(com.db4o.reflect.ReflectClass type)
		{
			if (!(type is com.db4o.reflect.self.SelfClass))
			{
				return false;
			}
			return _class.isAssignableFrom(((com.db4o.reflect.self.SelfClass)type).getJavaClass
				());
		}

		public virtual bool isCollection()
		{
			return _parentReflector.isCollection(this);
		}

		public virtual bool isInstance(object obj)
		{
			return _class.isInstance(obj);
		}

		public virtual bool isInterface()
		{
			return _class.isInterface();
		}

		public virtual bool isPrimitive()
		{
			return _registry.isPrimitive(_class);
		}

		public virtual bool isSecondClass()
		{
			return isPrimitive();
		}

		public virtual object newInstance()
		{
			try
			{
				return _class.newInstance();
			}
			catch (System.Exception e)
			{
				j4o.lang.JavaSystem.printStackTrace(e);
			}
			return null;
		}

		public virtual bool skipConstructor(bool flag)
		{
			return false;
		}

		public virtual void useConstructor(com.db4o.reflect.ReflectConstructor constructor
			, object[] _params)
		{
		}

		public virtual object[] toArray(object obj)
		{
			return null;
		}
	}
}
