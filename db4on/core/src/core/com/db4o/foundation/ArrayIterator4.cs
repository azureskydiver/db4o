namespace com.db4o.foundation
{
	public class ArrayIterator4 : com.db4o.foundation.Iterator4
	{
		internal object[] _elements;

		internal int _next;

		public ArrayIterator4(object[] elements)
		{
			_elements = elements;
			_next = 0;
		}

		public virtual bool HasNext()
		{
			return _next < _elements.Length;
		}

		public virtual object Next()
		{
			return _elements[_next++];
		}
	}
}
