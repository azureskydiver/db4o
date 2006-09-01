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
				DropIndex(systemTrans, yapField, stream);
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
			if (com.db4o.inside.marshall.MarshallerFamily.OLD_FIELD_INDEX)
			{
				com.db4o.MetaField metaField = GetMetaField(systemTrans);
				if (metaField.index == null)
				{
					return false;
				}
				yapField.InitOldIndex(systemTrans, metaField.index);
			}
			if (com.db4o.inside.marshall.MarshallerFamily.BTREE_FIELD_INDEX)
			{
				return yapField.GetIndex() != null;
			}
			return true;
		}

		private void CreateIndex(com.db4o.Transaction systemTrans, com.db4o.YapField yapField
			, com.db4o.YapFile stream)
		{
			if (com.db4o.inside.marshall.MarshallerFamily.BTREE_FIELD_INDEX)
			{
				yapField.InitIndex(systemTrans);
				stream.SetDirtyInSystemTransaction(yapField.GetParentYapClass());
			}
			if (com.db4o.inside.marshall.MarshallerFamily.OLD_FIELD_INDEX)
			{
				com.db4o.MetaField metaField = GetMetaField(systemTrans);
				if (metaField.index == null)
				{
					metaField.index = new com.db4o.MetaIndex();
					stream.SetInternal(systemTrans, metaField.index, com.db4o.YapConst.UNSPECIFIED, false
						);
					stream.SetInternal(systemTrans, metaField, com.db4o.YapConst.UNSPECIFIED, false);
					yapField.InitOldIndex(systemTrans, metaField.index);
					if (stream.ConfigImpl().MessageLevel() > com.db4o.YapConst.NONE)
					{
						stream.Message("creating index " + yapField.ToString());
					}
					Reindex(systemTrans, yapField, stream);
				}
			}
		}

		private com.db4o.MetaField GetMetaField(com.db4o.Transaction systemTrans)
		{
			return ClassConfig().MetaClass().EnsureField(systemTrans, GetName());
		}

		private void DropIndex(com.db4o.Transaction systemTrans, com.db4o.YapField yapField
			, com.db4o.YapFile stream)
		{
			com.db4o.MetaField metaField = GetMetaField(systemTrans);
			if (metaField.index != null)
			{
				if (stream.ConfigImpl().MessageLevel() > com.db4o.YapConst.NONE)
				{
					stream.Message("dropping index " + yapField.ToString());
				}
				com.db4o.MetaIndex mi = metaField.index;
				mi.Free(stream);
				stream.Delete1(systemTrans, mi, false);
				metaField.index = null;
				stream.SetInternal(systemTrans, metaField, com.db4o.YapConst.UNSPECIFIED, false);
			}
		}

		private void Reindex(com.db4o.Transaction systemTrans, com.db4o.YapField yapField
			, com.db4o.YapFile stream)
		{
			com.db4o.YapClass yapClassField = yapField.GetParentYapClass();
			long[] ids = yapClassField.GetIDs();
			for (int i = 0; i < ids.Length; i++)
			{
				com.db4o.YapWriter writer = stream.ReadWriterByID(systemTrans, (int)ids[i]);
				if (writer != null)
				{
					com.db4o.inside.marshall.ObjectHeader oh = new com.db4o.inside.marshall.ObjectHeader
						(stream, writer);
					object obj = oh.ObjectMarshaller().ReadIndexEntry(oh._yapClass, oh._headerAttributes
						, yapField, writer);
					yapField.AddIndexEntry(systemTrans, (int)ids[i], obj);
				}
			}
			if (ids.Length > 0)
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
