namespace Db4objects.Db4o.Reflect.Net
{
	/// <summary>Reflection implementation for Class to map to .NET reflection.</summary>
	/// <remarks>Reflection implementation for Class to map to .NET reflection.</remarks>
	public class NetClass : Db4objects.Db4o.Reflect.ReflectClass
	{
		private readonly Db4objects.Db4o.Reflect.Reflector _reflector;

		private readonly Sharpen.Lang.Class _clazz;

		private readonly System.Type _type;

		private Db4objects.Db4o.Reflect.ReflectConstructor _constructor;

		private object[] constructorParams;

		public NetClass(Db4objects.Db4o.Reflect.Reflector reflector, Sharpen.Lang.Class clazz)
		{
			_reflector = reflector;
			_clazz = clazz;
			_type = clazz.GetNetType();
		}

		public virtual Db4objects.Db4o.Reflect.ReflectClass GetComponentType()
		{
			return _reflector.ForClass(_clazz.GetComponentType());
		}

		public virtual Db4objects.Db4o.Reflect.ReflectConstructor[] GetDeclaredConstructors()
		{
			Sharpen.Lang.Reflect.Constructor[] constructors = _clazz.GetDeclaredConstructors();
			Db4objects.Db4o.Reflect.ReflectConstructor[] reflectors = new Db4objects.Db4o.Reflect.ReflectConstructor
				[constructors.Length];
			for (int i = 0; i < constructors.Length; i++)
			{
				reflectors[i] = new Db4objects.Db4o.Reflect.Net.NetConstructor(_reflector, constructors[i]);
			}
			return reflectors;
		}

		public virtual Db4objects.Db4o.Reflect.ReflectField GetDeclaredField(string name)
		{
			try
			{
				return new Db4objects.Db4o.Reflect.Net.NetField(_reflector, _clazz.GetDeclaredField(name));
			}
			catch (System.Exception e)
			{
				return null;
			}
		}

		public virtual Db4objects.Db4o.Reflect.ReflectField[] GetDeclaredFields()
		{
			Sharpen.Lang.Reflect.Field[] fields = _clazz.GetDeclaredFields();
			Db4objects.Db4o.Reflect.ReflectField[] reflectors = new Db4objects.Db4o.Reflect.ReflectField[fields.Length];
			for (int i = 0; i < reflectors.Length; i++)
			{
				reflectors[i] = new Db4objects.Db4o.Reflect.Net.NetField(_reflector, fields[i]);
			}
			return reflectors;
		}

		public virtual Db4objects.Db4o.Reflect.ReflectClass GetDelegate()
		{
			return this;
		}

		public virtual Db4objects.Db4o.Reflect.ReflectMethod GetMethod(
			string methodName, 
			Db4objects.Db4o.Reflect.ReflectClass[] paramClasses)
		{
			try
			{
				Sharpen.Lang.Reflect.Method method = _clazz.GetMethod(methodName, Db4objects.Db4o.Reflect.Net.NetReflector
					.ToNative(paramClasses));
				if (method == null)
				{
					return null;
				}
				return new Db4objects.Db4o.Reflect.Net.NetMethod(_reflector, method);
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

		public virtual Db4objects.Db4o.Reflect.ReflectClass GetSuperclass()
		{
			return _reflector.ForClass(_clazz.GetSuperclass());
		}

		public virtual bool IsAbstract()
		{
			return Sharpen.Lang.Reflect.Modifier.IsAbstract(_clazz.GetModifiers());
		}

		public virtual bool IsArray()
		{
			return _clazz.IsArray();
		}

		public virtual bool IsAssignableFrom(Db4objects.Db4o.Reflect.ReflectClass type)
		{
			if (!(type is Db4objects.Db4o.Reflect.Net.NetClass))
			{
				return false;
			}
			return _clazz.IsAssignableFrom(((Db4objects.Db4o.Reflect.Net.NetClass)type).GetJavaClass(
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

		internal virtual Sharpen.Lang.Class GetJavaClass()
		{
			return _clazz;
		}

		public virtual System.Type GetNetType() 
		{
			return _type;
		}

		public virtual Db4objects.Db4o.Reflect.Reflector Reflector()
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
			Db4objects.Db4o.Reflect.ReflectConstructor constructor, 
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
