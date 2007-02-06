namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public abstract class Config4Abstract
	{
		protected com.db4o.foundation.KeySpecHashtable4 _config;

		private static readonly com.db4o.foundation.KeySpec CASCADE_ON_ACTIVATE = new com.db4o.foundation.KeySpec
			(com.db4o.@internal.Const4.DEFAULT);

		private static readonly com.db4o.foundation.KeySpec CASCADE_ON_DELETE = new com.db4o.foundation.KeySpec
			(com.db4o.@internal.Const4.DEFAULT);

		private static readonly com.db4o.foundation.KeySpec CASCADE_ON_UPDATE = new com.db4o.foundation.KeySpec
			(com.db4o.@internal.Const4.DEFAULT);

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
			_config.Put(spec, flag ? com.db4o.@internal.Const4.YES : com.db4o.@internal.Const4
				.NO);
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

		/// <summary>Will raise an exception if argument class doesn't match this class - violates equals() contract in favor of failing fast.
		/// 	</summary>
		/// <remarks>Will raise an exception if argument class doesn't match this class - violates equals() contract in favor of failing fast.
		/// 	</remarks>
		public override bool Equals(object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (null == obj)
			{
				return false;
			}
			if (j4o.lang.JavaSystem.GetClassForObject(this) != j4o.lang.JavaSystem.GetClassForObject
				(obj))
			{
				com.db4o.@internal.Exceptions4.ShouldNeverHappen();
			}
			return GetName().Equals(((com.db4o.@internal.Config4Abstract)obj).GetName());
		}

		public override int GetHashCode()
		{
			return GetName().GetHashCode();
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
