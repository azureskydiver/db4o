namespace com.db4o.@internal
{
	/// <summary>
	/// improves YInt performance slightly by not doing checks
	/// and by doing the comparison with a substraction
	/// </summary>
	/// <exclude></exclude>
	public class PrimitiveIntHandler : com.db4o.@internal.handlers.IntHandler
	{
		public PrimitiveIntHandler(com.db4o.@internal.ObjectContainerBase stream) : base(
			stream)
		{
		}

		public override com.db4o.@internal.Comparable4 PrepareComparison(object obj)
		{
			_currentInteger = ((int)obj);
			_currentInt = _currentInteger;
			return this;
		}

		private int _currentInteger;

		private int _currentInt;

		public override int CompareTo(object obj)
		{
			return Val(obj) - _currentInt;
		}

		public override object Current()
		{
			return _currentInteger;
		}

		public override bool IsEqual(object obj)
		{
			return Val(obj) == _currentInt;
		}

		public override bool IsGreater(object obj)
		{
			return Val(obj) > _currentInt;
		}

		public override bool IsSmaller(object obj)
		{
			return Val(obj) < _currentInt;
		}
	}
}
