namespace com.db4o.reflect.generic
{
	/// <exclude></exclude>
	public class GenericArrayClass : com.db4o.reflect.generic.GenericClass
	{
		public GenericArrayClass(com.db4o.reflect.generic.GenericReflector reflector, com.db4o.reflect.ReflectClass
			 delegateClass, string name, com.db4o.reflect.generic.GenericClass superclass) : 
			base(reflector, delegateClass, "(GA) " + name, superclass)
		{
		}

		public override com.db4o.reflect.ReflectClass GetComponentType()
		{
			return GetDelegate();
		}

		public override bool IsArray()
		{
			return true;
		}

		public override bool IsInstance(object candidate)
		{
			if (!(candidate is com.db4o.reflect.generic.GenericArray))
			{
				return false;
			}
			return IsAssignableFrom(((com.db4o.reflect.generic.GenericArray)candidate)._clazz
				);
		}

		public override bool Equals(object obj)
		{
			if (!(obj is com.db4o.reflect.generic.GenericArrayClass))
			{
				return false;
			}
			return base.Equals(obj);
		}
	}
}
