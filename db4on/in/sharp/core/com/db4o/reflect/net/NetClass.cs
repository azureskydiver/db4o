namespace com.db4o.reflect.net
{
	/// <summary>Reflection implementation for Class to map to .NET reflection.</summary>
	/// <remarks>Reflection implementation for Class to map to .NET reflection.</remarks>
	public class NetClass : com.db4o.reflect.ReflectClass
	{
		private readonly com.db4o.reflect.Reflector _reflector;

		private readonly j4o.lang.Class _clazz;

		private readonly System.Type _type;

		private com.db4o.reflect.ReflectConstructor _constructor;

		private object[] constructorParams;

		public NetClass(com.db4o.reflect.Reflector reflector, j4o.lang.Class clazz)
		{
			_reflector = reflector;
			_clazz = clazz;
			_type = clazz.GetNetType();
		}

		public virtual com.db4o.reflect.ReflectClass GetComponentType()
		{
			return _reflector.ForClass(_clazz.GetComponentType());
		}

		public virtual com.db4o.reflect.ReflectConstructor[] GetDeclaredConstructors()
		{
			j4o.lang.reflect.Constructor[] constructors = _clazz.GetDeclaredConstructors();
			com.db4o.reflect.ReflectConstructor[] reflectors = new com.db4o.reflect.ReflectConstructor
				[constructors.Length];
			for (int i = 0; i < constructors.Length; i++)
			{
				reflectors[i] = new com.db4o.reflect.net.NetConstructor(_reflector, constructors[i]);
			}
			return reflectors;
		}

		public virtual com.db4o.reflect.ReflectField GetDeclaredField(string name)
		{
			try
			{
				return new com.db4o.reflect.net.NetField(_reflector, _clazz.GetDeclaredField(name));
			}
			catch (System.Exception e)
			{
				return null;
			}
		}

		public virtual com.db4o.reflect.ReflectField[] GetDeclaredFields()
		{
			j4o.lang.reflect.Field[] fields = _clazz.GetDeclaredFields();
			com.db4o.reflect.ReflectField[] reflectors = new com.db4o.reflect.ReflectField[fields.Length];
			for (int i = 0; i < reflectors.Length; i++)
			{
				reflectors[i] = new com.db4o.reflect.net.NetField(_reflector, fields[i]);
			}
			return reflectors;
		}

		public virtual com.db4o.reflect.ReflectClass GetDelegate()
		{
			return this;
		}

		public virtual com.db4o.reflect.ReflectMethod GetMethod(
			string methodName, 
			com.db4o.reflect.ReflectClass[] paramClasses)
		{
			try
			{
				j4o.lang.reflect.Method method = _clazz.GetMethod(methodName, com.db4o.reflect.net.NetReflector
					.ToNative(paramClasses));
				if (method == null)
				{
					return null;
				}
				return new com.db4o.reflect.net.NetMethod(_reflector, method);
			}
			catch (System.Exception e)
			{
				return null;
			}
		}

		public virtual string GetName()
		{
			return _clazz.GetName();
		}

		public virtual com.db4o.reflect.ReflectClass GetSuperclass()
		{
			return _reflector.ForClass(_clazz.GetSuperclass());
		}

		public virtual bool IsAbstract()
		{
			return j4o.lang.reflect.Modifier.IsAbstract(_clazz.GetModifiers());
		}

		public virtual bool IsArray()
		{
			return _clazz.IsArray();
		}

		public virtual bool IsAssignableFrom(com.db4o.reflect.ReflectClass type)
		{
			if (!(type is com.db4o.reflect.net.NetClass))
			{
				return false;
			}
			return _clazz.IsAssignableFrom(((com.db4o.reflect.net.NetClass)type).GetJavaClass(
				));
		}

		public virtual bool IsInstance(object obj)
		{
			return _clazz.IsInstance(obj);
		}

		public virtual bool IsInterface()
		{
			return _clazz.IsInterface();
		}

		public virtual bool IsCollection() 
		{
			return _reflector.IsCollection(this);
		}

		public virtual bool IsPrimitive()
		{
			return _clazz.IsPrimitive();
		}
		
		public virtual bool IsSecondClass() 
		{
			return IsPrimitive();
		}

		public virtual object NewInstance()
		{
			try
			{
				if (_constructor == null)
				{
					return _clazz.NewInstance();
				}
				return _constructor.NewInstance(constructorParams);
			}
			catch (System.Exception t)
			{
			}
			return null;
		}

		internal virtual j4o.lang.Class GetJavaClass()
		{
			return _clazz;
		}

		public virtual System.Type GetNetType() 
		{
			return _type;
		}

		public virtual com.db4o.reflect.Reflector Reflector()
		{
			return _reflector;
		}

		public virtual bool SkipConstructor(bool flag)
		{
#if !CF_1_0 && !CF_2_0
			if (flag) 
			{
				ReflectConstructor constructor = new SerializationConstructor(GetNetType());
				if (constructor != null)
				{
					try
					{
						object o = constructor.NewInstance(null);
						if (o != null)
						{
							UseConstructor(constructor, null);
							return true;
						}
					}
					catch (System.Exception e)
					{
					}
				}
			}
#endif
			UseConstructor(null, null);
			return false;
		}

		public override string ToString()
		{
			return "CClass: " + _clazz.GetName();
		}

		public virtual void UseConstructor(
			com.db4o.reflect.ReflectConstructor constructor, 
			object[] _params)
		{
			_constructor = constructor;
			constructorParams = _params;
		}
		
		public virtual object[] ToArray(object obj) 
		{
			// handled in GenericClass
			return null;
		}
	}
}
