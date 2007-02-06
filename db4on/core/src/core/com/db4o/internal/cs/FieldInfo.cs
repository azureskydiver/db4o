namespace com.db4o.@internal.cs
{
	public class FieldInfo
	{
		public string _fieldName;

		public com.db4o.@internal.cs.ClassInfo _fieldClass;

		public bool _isPrimitive;

		public bool _isArray;

		public bool _isNArray;

		public FieldInfo()
		{
		}

		public FieldInfo(string fieldName, com.db4o.@internal.cs.ClassInfo fieldClass, bool
			 isPrimitive, bool isArray, bool isNArray)
		{
			_fieldName = fieldName;
			_fieldClass = fieldClass;
			_isPrimitive = isPrimitive;
			_isArray = isArray;
			_isNArray = isNArray;
		}

		public virtual com.db4o.@internal.cs.ClassInfo GetFieldClass()
		{
			return _fieldClass;
		}

		public virtual string GetFieldName()
		{
			return _fieldName;
		}
	}
}
