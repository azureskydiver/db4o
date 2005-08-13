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

		public virtual string getName()
		{
			return field.getName();
		}

		public virtual com.db4o.reflect.ReflectClass getType()
		{
			return reflector.forClass(field.getType());
		}

		public virtual bool isPublic()
		{
			return j4o.lang.reflect.Modifier.isPublic(field.getModifiers());
		}

		public virtual bool isStatic()
		{
			return j4o.lang.reflect.Modifier.isStatic(field.getModifiers());
		}

		public virtual bool isTransient()
		{
			return j4o.lang.reflect.Modifier.isTransient(field.getModifiers());
		}

		public virtual void setAccessible()
		{
			com.db4o.Platform4.setAccessible(field);
		}

		public virtual object get(object onObject)
		{
			try
			{
				return field.get(onObject);
			}
			catch (System.Exception e)
			{
				return null;
			}
		}

		public virtual void set(object onObject, object attribute)
		{
			try
			{
				field.set(onObject, attribute);
			}
			catch (System.Exception e)
			{
			}
		}
	}
}
