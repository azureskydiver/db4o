namespace com.db4o.@internal
{
	internal class Config4Field : com.db4o.@internal.Config4Abstract, com.db4o.config.ObjectField
		, com.db4o.foundation.DeepClone
	{
		private readonly com.db4o.@internal.Config4Class _configClass;

		private static readonly com.db4o.foundation.KeySpec QUERY_EVALUATION = new com.db4o.foundation.KeySpec
			(true);

		private static readonly com.db4o.foundation.KeySpec INDEXED = new com.db4o.foundation.KeySpec
			(com.db4o.@internal.Const4.DEFAULT);

		protected Config4Field(com.db4o.@internal.Config4Class a_class, com.db4o.foundation.KeySpecHashtable4
			 config) : base(config)
		{
			_configClass = a_class;
		}

		internal Config4Field(com.db4o.@internal.Config4Class a_class, string a_name)
		{
			_configClass = a_class;
			SetName(a_name);
		}

		private com.db4o.@internal.Config4Class ClassConfig()
		{
			return _configClass;
		}

		internal override string ClassName()
		{
			return ClassConfig().GetName();
		}

		public virtual object DeepClone(object param)
		{
			return new com.db4o.@internal.Config4Field((com.db4o.@internal.Config4Class)param
				, _config);
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

		public virtual void InitOnUp(com.db4o.@internal.Transaction systemTrans, com.db4o.@internal.FieldMetadata
			 yapField)
		{
			com.db4o.@internal.ObjectContainerBase anyStream = systemTrans.Stream();
			if (!anyStream.MaintainsIndices())
			{
				return;
			}
			if (!yapField.SupportsIndex())
			{
				Indexed(false);
			}
			com.db4o.@internal.LocalObjectContainer stream = (com.db4o.@internal.LocalObjectContainer
				)anyStream;
			int indexedFlag = _config.GetAsInt(INDEXED);
			if (indexedFlag == com.db4o.@internal.Const4.NO)
			{
				yapField.DropIndex(systemTrans);
				return;
			}
			if (UseExistingIndex(systemTrans, yapField))
			{
				return;
			}
			if (indexedFlag != com.db4o.@internal.Const4.YES)
			{
				return;
			}
			CreateIndex(systemTrans, yapField, stream);
		}

		private bool UseExistingIndex(com.db4o.@internal.Transaction systemTrans, com.db4o.@internal.FieldMetadata
			 yapField)
		{
			return yapField.GetIndex(systemTrans) != null;
		}

		private void CreateIndex(com.db4o.@internal.Transaction systemTrans, com.db4o.@internal.FieldMetadata
			 yapField, com.db4o.@internal.LocalObjectContainer stream)
		{
			if (stream.ConfigImpl().MessageLevel() > com.db4o.@internal.Const4.NONE)
			{
				stream.Message("creating index " + yapField.ToString());
			}
			yapField.InitIndex(systemTrans);
			stream.SetDirtyInSystemTransaction(yapField.GetParentYapClass());
			Reindex(systemTrans, yapField, stream);
		}

		private void Reindex(com.db4o.@internal.Transaction systemTrans, com.db4o.@internal.FieldMetadata
			 yapField, com.db4o.@internal.LocalObjectContainer stream)
		{
			com.db4o.@internal.ClassMetadata yapClass = yapField.GetParentYapClass();
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
