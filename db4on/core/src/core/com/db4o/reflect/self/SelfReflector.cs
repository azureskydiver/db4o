namespace com.db4o.reflect.self
{
	public class SelfReflector : com.db4o.reflect.Reflector
	{
		private com.db4o.reflect.self.SelfArray _arrayHandler;

		private com.db4o.reflect.self.SelfReflectionRegistry _registry;

		private com.db4o.reflect.Reflector _parent;

		public SelfReflector(com.db4o.reflect.self.SelfReflectionRegistry registry)
		{
			_registry = registry;
		}

		public virtual com.db4o.reflect.ReflectArray array()
		{
			if (_arrayHandler == null)
			{
				_arrayHandler = new com.db4o.reflect.self.SelfArray(this, _registry);
			}
			return _arrayHandler;
		}

		public virtual bool constructorCallsSupported()
		{
			return true;
		}

		public virtual com.db4o.reflect.ReflectClass forClass(j4o.lang.Class clazz)
		{
			return new com.db4o.reflect.self.SelfClass(_parent, _registry, clazz);
		}

		public virtual com.db4o.reflect.ReflectClass forName(string className)
		{
			try
			{
				j4o.lang.Class clazz = j4o.lang.Class.forName(className);
				return forClass(clazz);
			}
			catch (j4o.lang.ClassNotFoundException e)
			{
				return null;
			}
		}

		public virtual com.db4o.reflect.ReflectClass forObject(object a_object)
		{
			if (a_object == null)
			{
				return null;
			}
			return forClass(j4o.lang.Class.getClassForObject(a_object));
		}

		public virtual bool isCollection(com.db4o.reflect.ReflectClass claxx)
		{
			return false;
		}

		public virtual void setParent(com.db4o.reflect.Reflector reflector)
		{
			_parent = reflector;
		}

		public virtual object deepClone(object context)
		{
			return new com.db4o.reflect.self.SelfReflector(_registry);
		}
	}
}
