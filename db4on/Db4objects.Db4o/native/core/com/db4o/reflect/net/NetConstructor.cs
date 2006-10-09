namespace Db4objects.Db4o.Reflect.Net
{

	/// <remarks>Reflection implementation for Constructor to map to JDK reflection.</remarks>
	public class NetConstructor : Db4objects.Db4o.Reflect.ReflectConstructor
	{
		private readonly Db4objects.Db4o.Reflect.Reflector reflector;

		private readonly Sharpen.Lang.Reflect.Constructor constructor;

		public NetConstructor(Db4objects.Db4o.Reflect.Reflector reflector, Sharpen.Lang.Reflect.Constructor
			 constructor)
		{
			this.reflector = reflector;
			this.constructor = constructor;
		}

		public virtual Db4objects.Db4o.Reflect.ReflectClass[] GetParameterTypes()
		{
			return Db4objects.Db4o.Reflect.Net.NetReflector.ToMeta(reflector, constructor.GetParameterTypes
				());
		}

		public virtual void SetAccessible()
		{
			Db4objects.Db4o.Platform4.SetAccessible(constructor);
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
