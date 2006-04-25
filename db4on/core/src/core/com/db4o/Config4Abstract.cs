namespace com.db4o
{
	internal abstract class Config4Abstract
	{
		protected com.db4o.foundation.KeySpecHashtable4 _config;

		private static readonly com.db4o.foundation.KeySpec CASCADE_ON_ACTIVATE = new com.db4o.foundation.KeySpec
			(com.db4o.YapConst.DEFAULT);

		private static readonly com.db4o.foundation.KeySpec CASCADE_ON_DELETE = new com.db4o.foundation.KeySpec
			(com.db4o.YapConst.DEFAULT);

		private static readonly com.db4o.foundation.KeySpec CASCADE_ON_UPDATE = new com.db4o.foundation.KeySpec
			(com.db4o.YapConst.DEFAULT);

		private static readonly com.db4o.foundation.KeySpec NAME = new com.db4o.foundation.KeySpec
			(null);

		public Config4Abstract() : this(new com.db4o.foundation.KeySpecHashtable4(10))
		{
		}

		protected Config4Abstract(com.db4o.foundation.KeySpecHashtable4 config)
		{
			_config = (com.db4o.foundation.KeySpecHashtable4)config.deepClone(this);
		}

		public virtual void cascadeOnActivate(bool flag)
		{
			putThreeValued(CASCADE_ON_ACTIVATE, flag);
		}

		public virtual void cascadeOnDelete(bool flag)
		{
			putThreeValued(CASCADE_ON_DELETE, flag);
		}

		public virtual void cascadeOnUpdate(bool flag)
		{
			putThreeValued(CASCADE_ON_UPDATE, flag);
		}

		protected virtual void putThreeValued(com.db4o.foundation.KeySpec spec, bool flag
			)
		{
			_config.put(spec, flag ? com.db4o.YapConst.YES : com.db4o.YapConst.NO);
		}

		public virtual int cascadeOnActivate()
		{
			return cascade(CASCADE_ON_ACTIVATE);
		}

		public virtual int cascadeOnDelete()
		{
			return cascade(CASCADE_ON_DELETE);
		}

		public virtual int cascadeOnUpdate()
		{
			return cascade(CASCADE_ON_UPDATE);
		}

		private int cascade(com.db4o.foundation.KeySpec spec)
		{
			return _config.getAsInt(spec);
		}

		internal abstract string className();

		public override bool Equals(object obj)
		{
			return getName().Equals(((com.db4o.Config4Abstract)obj).getName());
		}

		public virtual string getName()
		{
			return _config.getAsString(NAME);
		}

		protected virtual void setName(string name)
		{
			_config.put(NAME, name);
		}
	}
}
