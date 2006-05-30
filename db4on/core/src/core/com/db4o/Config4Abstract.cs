namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class Config4Abstract
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
			_config = (com.db4o.foundation.KeySpecHashtable4)config.DeepClone(this);
		}

		public virtual void CascadeOnActivate(bool flag)
		{
			PutThreeValued(CASCADE_ON_ACTIVATE, flag);
		}

		public virtual void CascadeOnDelete(bool flag)
		{
			PutThreeValued(CASCADE_ON_DELETE, flag);
		}

		public virtual void CascadeOnUpdate(bool flag)
		{
			PutThreeValued(CASCADE_ON_UPDATE, flag);
		}

		protected virtual void PutThreeValued(com.db4o.foundation.KeySpec spec, bool flag
			)
		{
			_config.Put(spec, flag ? com.db4o.YapConst.YES : com.db4o.YapConst.NO);
		}

		public virtual int CascadeOnActivate()
		{
			return Cascade(CASCADE_ON_ACTIVATE);
		}

		public virtual int CascadeOnDelete()
		{
			return Cascade(CASCADE_ON_DELETE);
		}

		public virtual int CascadeOnUpdate()
		{
			return Cascade(CASCADE_ON_UPDATE);
		}

		private int Cascade(com.db4o.foundation.KeySpec spec)
		{
			return _config.GetAsInt(spec);
		}

		internal abstract string ClassName();

		public override bool Equals(object obj)
		{
			return GetName().Equals(((com.db4o.Config4Abstract)obj).GetName());
		}

		public virtual string GetName()
		{
			return _config.GetAsString(NAME);
		}

		protected virtual void SetName(string name)
		{
			_config.Put(NAME, name);
		}
	}
}
