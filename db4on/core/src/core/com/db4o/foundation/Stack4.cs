namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class Stack4
	{
		private com.db4o.foundation.List4 _tail;

		public virtual void Push(object obj)
		{
			_tail = new com.db4o.foundation.List4(_tail, obj);
		}

		public virtual object Peek()
		{
			if (_tail == null)
			{
				return null;
			}
			return _tail._element;
		}

		public virtual object Pop()
		{
			if (_tail == null)
			{
				throw new System.InvalidOperationException();
			}
			object res = _tail._element;
			_tail = _tail._next;
			return res;
		}

		public virtual bool IsEmpty()
		{
			return _tail == null;
		}
	}
}
