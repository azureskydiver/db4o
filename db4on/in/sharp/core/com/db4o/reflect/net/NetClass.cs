namespace com.db4o.reflect.net
{
	/// <summary>Reflection implementation for Class to map to JDK reflection.</summary>
	/// <remarks>Reflection implementation for Class to map to JDK reflection.</remarks>
	public class NetClass : com.db4o.reflect.ReflectClass
	{
		private readonly com.db4o.reflect.Reflector reflector;

		private readonly j4o.lang.Class clazz;

        private readonly System.Type _type;

		private com.db4o.reflect.ReflectConstructor constructor;

		private object[] constructorParams;

		public NetClass(com.db4o.reflect.Reflector reflector, j4o.lang.Class clazz)
		{
			this.reflector = reflector;
			this.clazz = clazz;
            _type = clazz.getNetType();
		}

		public virtual com.db4o.reflect.ReflectClass getComponentType()
		{
			return reflector.forClass(clazz.getComponentType());
		}

		public virtual com.db4o.reflect.ReflectConstructor[] getDeclaredConstructors()
		{
			j4o.lang.reflect.Constructor[] constructors = clazz.getDeclaredConstructors();
			com.db4o.reflect.ReflectConstructor[] reflectors = new com.db4o.reflect.ReflectConstructor
				[constructors.Length];
			for (int i = 0; i < constructors.Length; i++)
			{
				reflectors[i] = new com.db4o.reflect.net.NetConstructor(reflector, constructors[i
					]);
			}
			return reflectors;
		}

		public virtual com.db4o.reflect.ReflectField getDeclaredField(string name)
		{
			try
			{
				return new com.db4o.reflect.net.NetField(reflector, clazz.getDeclaredField(name));
			}
			catch (System.Exception e)
			{
				return null;
			}
		}

		public virtual com.db4o.reflect.ReflectField[] getDeclaredFields()
		{
			j4o.lang.reflect.Field[] fields = clazz.getDeclaredFields();
			com.db4o.reflect.ReflectField[] reflectors = new com.db4o.reflect.ReflectField[fields
				.Length];
			for (int i = 0; i < reflectors.Length; i++)
			{
				reflectors[i] = new com.db4o.reflect.net.NetField(reflector, fields[i]);
			}
			return reflectors;
		}

		public virtual com.db4o.reflect.ReflectMethod getMethod(string methodName, com.db4o.reflect.ReflectClass[]
			 paramClasses)
		{
			try
			{
				j4o.lang.reflect.Method method = clazz.getMethod(methodName, com.db4o.reflect.net.NetReflector
					.toNative(paramClasses));
				if (method == null)
				{
					return null;
				}
				return new com.db4o.reflect.net.NetMethod(method);
			}
			catch (System.Exception e)
			{
				return null;
			}
		}

		public virtual string getName()
		{
			return clazz.getName();
		}

		public virtual com.db4o.reflect.ReflectClass getSuperclass()
		{
			return reflector.forClass(clazz.getSuperclass());
		}

		public virtual bool isAbstract()
		{
			return j4o.lang.reflect.Modifier.isAbstract(clazz.getModifiers());
		}

		public virtual bool isArray()
		{
			return clazz.isArray();
		}

		public virtual bool isAssignableFrom(com.db4o.reflect.ReflectClass type)
		{
			if (!(type is com.db4o.reflect.net.NetClass))
			{
				return false;
			}
			return clazz.isAssignableFrom(((com.db4o.reflect.net.NetClass)type).getJavaClass(
				));
		}

		public virtual bool isInstance(object obj)
		{
			return clazz.isInstance(obj);
		}

		public virtual bool isInterface()
		{
			return clazz.isInterface();
		}

		public virtual bool isPrimitive()
		{
			return clazz.isPrimitive();
		}

		public virtual object newInstance()
		{
			try
			{
				if (constructor == null)
				{
					return clazz.newInstance();
				}
				return constructor.newInstance(constructorParams);
			}
			catch (System.Exception t)
			{
			}
			return null;
		}

		internal virtual j4o.lang.Class getJavaClass()
		{
			return clazz;
		}

        internal virtual System.Type getNetType() {
            return _type;
        }

        public virtual bool skipConstructor(bool flag)
		{
			if (flag)
			{
				j4o.lang.reflect.Constructor constructor = com.db4o.Platform.jdk().serializableConstructor
					(clazz);
				if (constructor != null)
				{
					try
					{
						object o = constructor.newInstance(null);
						if (o != null)
						{
							useConstructor(new com.db4o.reflect.net.NetConstructor(reflector, constructor), null
								);
							return true;
						}
					}
					catch (System.Exception e)
					{
					}
				}
			}
			useConstructor(null, null);
			return false;
		}

		public override string ToString()
		{
			return "CClass: " + clazz.getName();
		}

		public virtual void useConstructor(com.db4o.reflect.ReflectConstructor constructor
			, object[] _params)
		{
			this.constructor = constructor;
			constructorParams = _params;
		}
	}
}
