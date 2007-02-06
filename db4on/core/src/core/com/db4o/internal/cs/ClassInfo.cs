namespace com.db4o.@internal.cs
{
	public class ClassInfo
	{
		public static com.db4o.@internal.cs.ClassInfo NewSystemClass(string className)
		{
			return new com.db4o.@internal.cs.ClassInfo(className, true);
		}

		public static com.db4o.@internal.cs.ClassInfo NewUserClass(string className)
		{
			return new com.db4o.@internal.cs.ClassInfo(className, false);
		}

		public string _className;

		public bool _isSystemClass;

		public com.db4o.@internal.cs.ClassInfo _superClass;

		public com.db4o.@internal.cs.FieldInfo[] _fields;

		public ClassInfo()
		{
		}

		private ClassInfo(string className, bool systemClass)
		{
			_className = className;
			_isSystemClass = systemClass;
		}

		public virtual com.db4o.@internal.cs.FieldInfo[] GetFields()
		{
			return _fields;
		}

		public virtual void SetFields(com.db4o.@internal.cs.FieldInfo[] fields)
		{
			this._fields = fields;
		}

		public virtual com.db4o.@internal.cs.ClassInfo GetSuperClass()
		{
			return _superClass;
		}

		public virtual void SetSuperClass(com.db4o.@internal.cs.ClassInfo superClass)
		{
			this._superClass = superClass;
		}

		public virtual string GetClassName()
		{
			return _className;
		}

		public virtual bool IsSystemClass()
		{
			return _isSystemClass;
		}
	}
}
