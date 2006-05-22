namespace com.db4o.reflect.net
{

	public class NetField : com.db4o.reflect.ReflectField
	{
		private readonly com.db4o.reflect.Reflector reflector;

		private readonly j4o.lang.reflect.Field field;

		public NetField(com.db4o.reflect.Reflector reflector, j4o.lang.reflect.Field field
			)
		{
			this.reflector = reflector;
			this.field = field;
		}

		public virtual string GetName()
		{
			return field.GetName();
		}

		public virtual com.db4o.reflect.ReflectClass GetType()
		{
			return reflector.ForClass(field.GetType());
		}

		public virtual bool IsPublic()
		{
			return j4o.lang.reflect.Modifier.IsPublic(field.GetModifiers());
		}

		public virtual bool IsStatic()
		{
			return j4o.lang.reflect.Modifier.IsStatic(field.GetModifiers());
		}

		public virtual bool IsTransient()
		{
			return j4o.lang.reflect.Modifier.IsTransient(field.GetModifiers());
		}

		public virtual void SetAccessible()
		{
			com.db4o.Platform4.SetAccessible(field);
		}

		public virtual object Get(object onObject)
		{
			try
			{
				return field.Get(onObject);
			}
			catch (System.Exception e)
			{
				return null;
			}
		}

		public virtual void Set(object onObject, object attribute)
		{
			try
			{
				field.Set(onObject, attribute);
			}
			catch (System.Exception e)
			{
			}
		}
	}
}
