namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class Visitor4Dispatch : com.db4o.foundation.Visitor4
	{
		public readonly com.db4o.foundation.Visitor4 _target;

		public Visitor4Dispatch(com.db4o.foundation.Visitor4 visitor)
		{
			_target = visitor;
		}

		public virtual void Visit(object a_object)
		{
			((com.db4o.foundation.Visitor4)a_object).Visit(_target);
		}
	}
}
