namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class KeySpecHashtable4 : com.db4o.foundation.Hashtable4
	{
		private KeySpecHashtable4() : base()
		{
		}

		public KeySpecHashtable4(int a_size) : base(a_size)
		{
		}

		public virtual void put(com.db4o.foundation.KeySpec spec, byte value)
		{
			base.put(spec, value);
		}

		public virtual void put(com.db4o.foundation.KeySpec spec, bool value)
		{
			base.put(spec, value);
		}

		public virtual void put(com.db4o.foundation.KeySpec spec, int value)
		{
			base.put(spec, value);
		}

		public virtual void put(com.db4o.foundation.KeySpec spec, object value)
		{
			base.put(spec, value);
		}

		public virtual byte getAsByte(com.db4o.foundation.KeySpec spec)
		{
			return ((byte)get(spec));
		}

		public virtual bool getAsBoolean(com.db4o.foundation.KeySpec spec)
		{
			return ((bool)get(spec));
		}

		public virtual int getAsInt(com.db4o.foundation.KeySpec spec)
		{
			return ((int)get(spec));
		}

		public virtual string getAsString(com.db4o.foundation.KeySpec spec)
		{
			return (string)get(spec);
		}

		public virtual object get(com.db4o.foundation.KeySpec spec)
		{
			object value = base.get(spec);
			return (value == null ? spec.defaultValue() : value);
		}

		public override object deepClone(object obj)
		{
			return deepCloneInternal(new com.db4o.foundation.KeySpecHashtable4(), obj);
		}
	}
}
