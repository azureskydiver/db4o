
namespace com.db4o.reflect.generic
{
	/// <exclude></exclude>
	public class GenericObject
	{
		internal readonly com.db4o.reflect.generic.GenericClass _class;

		private object[] _values;

		public GenericObject(com.db4o.reflect.generic.GenericClass clazz)
		{
			_class = clazz;
		}

		private void ensureValuesInitialized()
		{
			if (_values == null)
			{
				_values = new object[_class.getFieldCount()];
			}
		}

		public virtual void set(int index, object value)
		{
			ensureValuesInitialized();
			_values[index] = value;
		}

		public virtual object get(int index)
		{
			ensureValuesInitialized();
			return _values[index];
		}

		public override string ToString()
		{
			if (_class == null)
			{
				return base.ToString();
			}
			return _class.toString(this);
		}
	}
}
