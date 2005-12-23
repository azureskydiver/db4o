namespace com.db4o.reflect.self
{
	public class SelfMethod : com.db4o.reflect.ReflectMethod
	{
		public virtual object invoke(object onObject, object[] parameters)
		{
			return null;
		}

		public virtual com.db4o.reflect.ReflectClass getReturnType()
		{
			return null;
		}
	}
}
