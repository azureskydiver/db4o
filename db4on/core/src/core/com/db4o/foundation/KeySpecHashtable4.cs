namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class KeySpecHashtable4 : com.db4o.foundation.Hashtable4
	{
		private KeySpecHashtable4() : base((com.db4o.foundation.DeepClone)null)
		{
		}

		public KeySpecHashtable4(int a_size) : base(a_size)
		{
		}

		public virtual void Put(com.db4o.foundation.KeySpec spec, byte value)
		{
			base.Put(spec, value);
		}

		public virtual void Put(com.db4o.foundation.KeySpec spec, bool value)
		{
			base.Put(spec, value);
		}

		public virtual void Put(com.db4o.foundation.KeySpec spec, int value)
		{
			base.Put(spec, value);
		}

		public virtual void Put(com.db4o.foundation.KeySpec spec, object value)
		{
			base.Put(spec, value);
		}

		public virtual byte GetAsByte(com.db4o.foundation.KeySpec spec)
		{
			return ((byte)Get(spec));
		}

		public virtual bool GetAsBoolean(com.db4o.foundation.KeySpec spec)
		{
			return ((bool)Get(spec));
		}

		public virtual int GetAsInt(com.db4o.foundation.KeySpec spec)
		{
			return ((int)Get(spec));
		}

		public virtual string GetAsString(com.db4o.foundation.KeySpec spec)
		{
			return (string)Get(spec);
		}

		public virtual object Get(com.db4o.foundation.KeySpec spec)
		{
			object value = base.Get(spec);
			if (value == null)
			{
				value = spec.DefaultValue();
				if (value != null)
				{
					base.Put(spec, value);
				}
			}
			return value;
		}

		public override object DeepClone(object obj)
		{
			return DeepCloneInternal(new com.db4o.foundation.KeySpecHashtable4(), obj);
		}
	}
}
