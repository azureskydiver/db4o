namespace com.db4o
{
	internal class Config4Field : com.db4o.Config4Abstract, com.db4o.config.ObjectField
		, com.db4o.foundation.DeepClone
	{
		private readonly com.db4o.Config4Class _configClass;

		private static readonly com.db4o.foundation.KeySpec QUERY_EVALUATION = new com.db4o.foundation.KeySpec
			(true);

		private static readonly com.db4o.foundation.KeySpec INDEXED = new com.db4o.foundation.KeySpec
			(com.db4o.YapConst.DEFAULT);

		private bool _initialized;

		protected Config4Field(com.db4o.Config4Class a_class, com.db4o.foundation.KeySpecHashtable4
			 config) : base(config)
		{
			_configClass = a_class;
		}

		internal Config4Field(com.db4o.Config4Class a_class, string a_name)
		{
			_configClass = a_class;
			SetName(a_name);
		}

		private com.db4o.Config4Class ClassConfig()
		{
			return _configClass;
		}

		internal override string ClassName()
		{
			return ClassConfig().GetName();
		}

		public virtual object DeepClone(object param)
		{
			return new com.db4o.Config4Field((com.db4o.Config4Class)param, _config);
		}

		public virtual void QueryEvaluation(bool flag)
		{
			_config.Put(QUERY_EVALUATION, flag);
		}

		public virtual void Rename(string newName)
		{
			ClassConfig().Config().Rename(new com.db4o.Rename(ClassName(), GetName(), newName
				));
			SetName(newName);
		}

		public virtual void Indexed(bool flag)
		{
			PutThreeValued(INDEXED, flag);
		}

		public virtual void InitOnUp(com.db4o.Transaction systemTrans, com.db4o.YapField 
			yapField)
		{
			if (_initialized)
			{
				return;
			}
			_initialized = true;
			com.db4o.YapStream anyStream = systemTrans.Stream();
			if (!anyStream.MaintainsIndices())
			{
				return;
			}
			if (!yapField.SupportsIndex())
			{
				Indexed(false);
			}
			com.db4o.YapFile stream = (com.db4o.YapFile)anyStream;
			int indexedFlag = _config.GetAsInt(INDEXED);
			if (indexedFlag == com.db4o.YapConst.NO)
			{
				yapField.DropIndex(systemTrans);
				return;
			}
			if (UseExistingIndex(systemTrans, yapField))
			{
				return;
			}
			if (indexedFlag != com.db4o.YapConst.YES)
			{
				return;
			}
			CreateIndex(systemTrans, yapField, stream);
		}

		private bool UseExistingIndex(com.db4o.Transaction systemTrans, com.db4o.YapField
			 yapField)
		{
			return yapField.GetIndex(systemTrans) != null;
		}

		private void CreateIndex(com.db4o.Transaction systemTrans, com.db4o.YapField yapField
			, com.db4o.YapFile stream)
		{
			if (stream.ConfigImpl().MessageLevel() > com.db4o.YapConst.NONE)
			{
				stream.Message("creating index " + yapField.ToString());
			}
			yapField.InitIndex(systemTrans);
			stream.SetDirtyInSystemTransaction(yapField.GetParentYapClass());
			Reindex(systemTrans, yapField, stream);
		}

		private void Reindex(com.db4o.Transaction systemTrans, com.db4o.YapField yapField
			, com.db4o.YapFile stream)
		{
			com.db4o.YapClass yapClass = yapField.GetParentYapClass();
			if (yapField.RebuildIndexForClass(stream, yapClass))
			{
				systemTrans.Commit();
			}
		}

		internal virtual bool QueryEvaluation()
		{
			return _config.GetAsBoolean(QUERY_EVALUATION);
		}
	}
}
