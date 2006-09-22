namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class FunctionApplicationIterator : com.db4o.foundation.Iterator4
	{
		private readonly com.db4o.foundation.Iterator4 _iterator;

		private readonly com.db4o.foundation.Function4 _function;

		private object _current;

		public FunctionApplicationIterator(com.db4o.foundation.Iterator4 iterator, com.db4o.foundation.Function4
			 function)
		{
			if (null == iterator)
			{
				throw new System.ArgumentNullException();
			}
			if (null == function)
			{
				throw new System.ArgumentNullException();
			}
			_iterator = iterator;
			_function = function;
			_current = com.db4o.foundation.Iterators.NO_ELEMENT;
		}

		public virtual bool MoveNext()
		{
			if (!_iterator.MoveNext())
			{
				_current = com.db4o.foundation.Iterators.NO_ELEMENT;
				return false;
			}
			_current = _function.Apply(_iterator.Current());
			return true;
		}

		public virtual object Current()
		{
			if (com.db4o.foundation.Iterators.NO_ELEMENT == _current)
			{
				throw new System.InvalidOperationException();
			}
			return _current;
		}
	}
}
