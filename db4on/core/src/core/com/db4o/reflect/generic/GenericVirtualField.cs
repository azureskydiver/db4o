
namespace com.db4o.reflect.generic
{
	/// <exclude></exclude>
	public class GenericVirtualField : com.db4o.reflect.generic.GenericField
	{
		public GenericVirtualField(string name) : base(name, null, false, false, false)
		{
		}

		public override object deepClone(object obj)
		{
			com.db4o.reflect.Reflector reflector = (com.db4o.reflect.Reflector)obj;
			return new com.db4o.reflect.generic.GenericVirtualField(getName());
		}

		public override object get(object onObject)
		{
			return null;
		}

		public override com.db4o.reflect.ReflectClass getType()
		{
			return null;
		}

		public override bool isPublic()
		{
			return false;
		}

		public override bool isStatic()
		{
			return true;
		}

		public override bool isTransient()
		{
			return true;
		}

		public override void set(object onObject, object value)
		{
		}
	}
}
