namespace com.db4o.reflect.net
{
	public class NetMethod : com.db4o.reflect.ReflectMethod
	{
		private readonly j4o.lang.reflect.Method method;

		private readonly com.db4o.reflect.Reflector _reflector;

		public NetMethod(com.db4o.reflect.Reflector reflector, j4o.lang.reflect.Method method)
		{
			_reflector = reflector;
			this.method = method;
		}

		public com.db4o.reflect.ReflectClass GetReturnType() 
		{
			return _reflector.ForClass(method.GetReturnType());
		}

		public virtual object Invoke(object onObject, object[] parameters)
		{
			try
			{
				return method.Invoke(onObject, parameters);
			}
			catch (System.Exception e)
			{
				return null;
			}
		}
	}
}
