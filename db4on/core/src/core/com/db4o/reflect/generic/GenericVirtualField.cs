namespace com.db4o.reflect.generic
{
	/// <exclude></exclude>
	public class GenericVirtualField : com.db4o.reflect.generic.GenericField
	{
		public GenericVirtualField(string name) : base(name, null, false, false, false)
		{
		}

		public override object DeepClone(object obj)
		{
			return new com.db4o.reflect.generic.GenericVirtualField(GetName());
		}

		public override object Get(object onObject)
		{
			return null;
		}

		public override com.db4o.reflect.ReflectClass GetFieldType()
		{
			return null;
		}

		public override bool IsPublic()
		{
			return false;
		}

		public override bool IsStatic()
		{
			return true;
		}

		public override bool IsTransient()
		{
			return true;
		}

		public override void Set(object onObject, object value)
		{
		}
	}
}
