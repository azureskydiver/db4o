namespace com.db4o.reflect.self
{
	public class FieldInfo
	{
		private string _name;

		private j4o.lang.Class _clazz;

		private bool _isPublic;

		private bool _isStatic;

		private bool _isTransient;

		public FieldInfo(string _name, j4o.lang.Class _clazz, bool isPublic, bool isStatic
			, bool isTransient)
		{
			this._name = _name;
			this._clazz = _clazz;
			this._isPublic = isPublic;
			this._isStatic = isStatic;
			this._isTransient = isTransient;
		}

		public virtual string name()
		{
			return _name;
		}

		public virtual j4o.lang.Class type()
		{
			return _clazz;
		}

		public virtual bool isPublic()
		{
			return _isPublic;
		}

		public virtual bool isStatic()
		{
			return _isStatic;
		}

		public virtual bool isTransient()
		{
			return _isTransient;
		}
	}
}
