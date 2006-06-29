namespace com.db4o.reflect.generic
{
	/// <exclude></exclude>
	public class GenericArray
	{
		internal com.db4o.reflect.generic.GenericClass _clazz;

		internal object[] _data;

		public GenericArray(com.db4o.reflect.generic.GenericClass clazz, int size)
		{
			_clazz = clazz;
			_data = new object[size];
		}

		internal virtual int GetLength()
		{
			return _data.Length;
		}
	}
}
