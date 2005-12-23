namespace com.db4o.reflect.self
{
	public class SelfField : com.db4o.reflect.ReflectField
	{
		private string _name;

		private com.db4o.reflect.ReflectClass _type;

		private com.db4o.reflect.self.SelfClass _selfclass;

		private com.db4o.reflect.self.SelfReflectionRegistry _registry;

		public SelfField(string name, com.db4o.reflect.ReflectClass type, com.db4o.reflect.self.SelfClass
			 selfclass, com.db4o.reflect.self.SelfReflectionRegistry registry)
		{
			_name = name;
			_type = type;
			_selfclass = selfclass;
			_registry = registry;
		}

		public virtual object get(object onObject)
		{
			if (onObject is com.db4o.reflect.self.SelfReflectable)
			{
				return ((com.db4o.reflect.self.SelfReflectable)onObject).self_get(_name);
			}
			return null;
		}

		public virtual string getName()
		{
			return _name;
		}

		public virtual com.db4o.reflect.ReflectClass getType()
		{
			return _type;
		}

		public virtual bool isPublic()
		{
			return _registry.infoFor(_selfclass.getJavaClass()).fieldByName(_name).isPublic();
		}

		public virtual bool isStatic()
		{
			return _registry.infoFor(_selfclass.getJavaClass()).fieldByName(_name).isStatic();
		}

		public virtual bool isTransient()
		{
			return _registry.infoFor(_selfclass.getJavaClass()).fieldByName(_name).isTransient
				();
		}

		public virtual void set(object onObject, object value)
		{
			if (onObject is com.db4o.reflect.self.SelfReflectable)
			{
				((com.db4o.reflect.self.SelfReflectable)onObject).self_set(_name, value);
			}
		}

		public virtual void setAccessible()
		{
		}
	}
}
