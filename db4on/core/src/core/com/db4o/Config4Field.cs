namespace com.db4o
{
	internal class Config4Field : com.db4o.Config4Abstract, com.db4o.config.ObjectField
		, com.db4o.foundation.DeepClone
	{
		private readonly com.db4o.Config4Class _configClass;

		private static readonly com.db4o.foundation.KeySpec FIELD_REFLECTOR = new com.db4o.foundation.KeySpec
			(null);

		private static readonly com.db4o.foundation.KeySpec QUERY_EVALUATION = new com.db4o.foundation.KeySpec
			(true);

		private static readonly com.db4o.foundation.KeySpec INDEXED = new com.db4o.foundation.KeySpec
			(com.db4o.YapConst.DEFAULT);

		private static readonly com.db4o.foundation.KeySpec METAFIELD = new com.db4o.foundation.KeySpec
			(null);

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

		private com.db4o.reflect.ReflectField FieldReflector()
		{
			com.db4o.reflect.ReflectField fieldReflector = (com.db4o.reflect.ReflectField)_config
				.Get(FIELD_REFLECTOR);
			if (fieldReflector == null)
			{
				try
				{
					fieldReflector = ClassConfig().ClassReflector().GetDeclaredField(GetName());
					fieldReflector.SetAccessible();
					_config.Put(FIELD_REFLECTOR, fieldReflector);
				}
				catch (System.Exception e)
				{
				}
			}
			return fieldReflector;
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
			if (!_initialized)
			{
				com.db4o.YapStream anyStream = systemTrans.i_stream;
				if (anyStream.MaintainsIndices())
				{
					if (!yapField.SupportsIndex())
					{
						Indexed(false);
					}
					bool indexInitCalled = false;
					com.db4o.YapFile stream = (com.db4o.YapFile)anyStream;
					com.db4o.MetaField metaField = ClassConfig().MetaClass().EnsureField(systemTrans, 
						GetName());
					_config.Put(METAFIELD, metaField);
					int indexedFlag = _config.GetAsInt(INDEXED);
					if (indexedFlag == com.db4o.YapConst.YES)
					{
						if (metaField.index == null)
						{
							metaField.index = new com.db4o.MetaIndex();
							stream.SetInternal(systemTrans, metaField.index, com.db4o.YapConst.UNSPECIFIED, false
								);
							stream.SetInternal(systemTrans, metaField, com.db4o.YapConst.UNSPECIFIED, false);
							yapField.InitIndex(systemTrans, metaField.index);
							indexInitCalled = true;
							if (stream.i_config.MessageLevel() > com.db4o.YapConst.NONE)
							{
								stream.Message("creating index " + yapField.ToString());
							}
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
					}
					if (indexedFlag == com.db4o.YapConst.NO)
					{
						if (metaField.index != null)
						{
							if (stream.i_config.MessageLevel() > com.db4o.YapConst.NONE)
							{
								stream.Message("dropping index " + yapField.ToString());
							}
							com.db4o.MetaIndex mi = metaField.index;
							if (mi.indexLength > 0)
							{
								stream.Free(mi.indexAddress, mi.indexLength);
							}
							if (mi.patchLength > 0)
							{
								stream.Free(mi.patchAddress, mi.patchLength);
							}
							stream.Delete1(systemTrans, mi, false);
							metaField.index = null;
							stream.SetInternal(systemTrans, metaField, com.db4o.YapConst.UNSPECIFIED, false);
						}
					}
					if (metaField.index != null)
					{
						if (!indexInitCalled)
						{
							yapField.InitIndex(systemTrans, metaField.index);
						}
					}
				}
				_initialized = true;
			}
		}

		internal virtual bool QueryEvaluation()
		{
			return _config.GetAsBoolean(QUERY_EVALUATION);
		}
	}
}
