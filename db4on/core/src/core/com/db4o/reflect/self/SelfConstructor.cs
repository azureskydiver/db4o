namespace com.db4o.reflect.self
{
	public class SelfConstructor : com.db4o.reflect.ReflectConstructor
	{
		private j4o.lang.Class _class;

		public SelfConstructor(j4o.lang.Class _class)
		{
			this._class = _class;
		}

		public virtual void setAccessible()
		{
		}

		public virtual com.db4o.reflect.ReflectClass[] getParameterTypes()
		{
			return new com.db4o.reflect.ReflectClass[] {  };
		}

		public virtual object newInstance(object[] parameters)
		{
			try
			{
				return _class.newInstance();
			}
			catch (System.Exception exc)
			{
				return null;
			}
		}
	}
}
