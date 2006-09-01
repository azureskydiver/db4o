namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public class FieldIndexKey
	{
		private readonly int _parentID;

		private readonly object _value;

		public FieldIndexKey(int parentID_, object value_)
		{
			_parentID = parentID_;
			_value = value_;
		}

		public virtual int ParentID()
		{
			return _parentID;
		}

		public virtual object Value()
		{
			return _value;
		}

		public override string ToString()
		{
			return "FieldIndexKey(" + _parentID + ", " + SafeString(_value) + ")";
		}

		private string SafeString(object value)
		{
			if (null == value)
			{
				return "null";
			}
			return value.ToString();
		}
	}
}
