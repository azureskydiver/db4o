namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class FunctionApplicationIterator : com.db4o.foundation.MappingIterator
	{
		private readonly com.db4o.foundation.Function4 _function;

		public FunctionApplicationIterator(System.Collections.IEnumerator iterator, com.db4o.foundation.Function4
			 function) : base(iterator)
		{
			if (null == function)
			{
				throw new System.ArgumentNullException();
			}
			_function = function;
		}

		protected override object Map(object current)
		{
			return _function.Apply(current);
		}
	}
}
