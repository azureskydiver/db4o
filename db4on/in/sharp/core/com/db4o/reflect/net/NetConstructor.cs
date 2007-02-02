namespace com.db4o.reflect.net
{

	/// <remarks>Reflection implementation for Constructor to map to JDK reflection.</remarks>
	public class NetConstructor : com.db4o.reflect.ReflectConstructor
	{
		private readonly com.db4o.reflect.Reflector reflector;

		private readonly j4o.lang.reflect.Constructor constructor;

		public NetConstructor(com.db4o.reflect.Reflector reflector, j4o.lang.reflect.Constructor
			 constructor)
		{
			this.reflector = reflector;
			this.constructor = constructor;
		}

		public virtual com.db4o.reflect.ReflectClass[] GetParameterTypes()
		{
			return com.db4o.reflect.net.NetReflector.ToMeta(reflector, constructor.GetParameterTypes
				());
		}

		public virtual void SetAccessible()
		{
			com.db4o.inside.Platform4.SetAccessible(constructor);
		}

		public virtual object NewInstance(object[] parameters)
		{
			try
			{
				object obj = constructor.NewInstance(parameters);
				return obj;
			}
			catch (System.Exception e)
			{
				return null;
			}
		}
	}
}
