namespace com.db4o.reflect.net
{
	public class NetMethod : com.db4o.reflect.ReflectMethod
	{
		private readonly j4o.lang.reflect.Method method;

		public NetMethod(j4o.lang.reflect.Method method)
		{
			this.method = method;
		}

		public virtual object invoke(object onObject, object[] parameters)
		{
			try
			{
				return method.invoke(onObject, parameters);
			}
			catch (System.Exception e)
			{
				return null;
			}
		}
	}
}
