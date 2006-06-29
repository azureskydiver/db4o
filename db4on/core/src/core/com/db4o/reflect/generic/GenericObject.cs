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

		private void EnsureValuesInitialized()
		{
			if (_values == null)
			{
				_values = new object[_class.GetFieldCount()];
			}
		}

		public virtual void Set(int index, object value)
		{
			EnsureValuesInitialized();
			_values[index] = value;
		}

		public virtual object Get(int index)
		{
			EnsureValuesInitialized();
			return _values[index];
		}

		public override string ToString()
		{
			if (_class == null)
			{
				return base.ToString();
			}
			return _class.ToString(this);
		}
	}
}
