namespace Db4objects.Db4o.Reflect.Net
{
	public class NetMethod : Db4objects.Db4o.Reflect.ReflectMethod
	{
		private readonly Sharpen.Lang.Reflect.Method method;

		private readonly Db4objects.Db4o.Reflect.Reflector _reflector;

		public NetMethod(Db4objects.Db4o.Reflect.Reflector reflector, Sharpen.Lang.Reflect.Method method)
		{
			_reflector = reflector;
			this.method = method;
		}

		public Db4objects.Db4o.Reflect.ReflectClass GetReturnType() 
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
