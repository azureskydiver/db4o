namespace com.db4o.reflect.self
{
	public class ClassInfo
	{
		private j4o.lang.Class _superClass;

		private bool _isAbstract;

		private com.db4o.reflect.self.FieldInfo[] _fieldInfo;

		public ClassInfo(bool isAbstract, j4o.lang.Class superClass, com.db4o.reflect.self.FieldInfo[]
			 fieldInfo)
		{
			_isAbstract = isAbstract;
			_superClass = superClass;
			_fieldInfo = fieldInfo;
		}

		public virtual bool isAbstract()
		{
			return _isAbstract;
		}

		public virtual j4o.lang.Class superClass()
		{
			return _superClass;
		}

		public virtual com.db4o.reflect.self.FieldInfo[] fieldInfo()
		{
			return _fieldInfo;
		}

		public virtual com.db4o.reflect.self.FieldInfo fieldByName(string name)
		{
			if (!(_fieldInfo.Length == 0))
			{
				for (int i = 0; i < _fieldInfo.Length; i++)
				{
					if (_fieldInfo[i].name().Equals(name))
					{
						return _fieldInfo[i];
					}
				}
			}
			return null;
		}
	}
}
