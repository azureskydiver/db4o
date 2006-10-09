namespace Db4objects.Db4o.Reflect.Net
{

	public class NetField : Db4objects.Db4o.Reflect.ReflectField
	{
		private readonly Db4objects.Db4o.Reflect.Reflector reflector;

		private readonly Sharpen.Lang.Reflect.Field field;

		public NetField(Db4objects.Db4o.Reflect.Reflector reflector, Sharpen.Lang.Reflect.Field field
			)
		{
			this.reflector = reflector;
			this.field = field;
		}

		public virtual string GetName()
		{
			return field.GetName();
		}

		public virtual Db4objects.Db4o.Reflect.ReflectClass GetFieldType()
		{
			return reflector.ForClass(field.GetFieldType());
		}

		public virtual bool IsPublic()
		{
			return Sharpen.Lang.Reflect.Modifier.IsPublic(field.GetModifiers());
		}

		public virtual bool IsStatic()
		{
			return Sharpen.Lang.Reflect.Modifier.IsStatic(field.GetModifiers());
		}

		public virtual bool IsTransient()
		{
			return Sharpen.Lang.Reflect.Modifier.IsTransient(field.GetModifiers());
		}

		public virtual void SetAccessible()
		{
			Db4objects.Db4o.Platform4.SetAccessible(field);
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
