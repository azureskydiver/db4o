namespace com.db4o
{
	internal class Config4Field : com.db4o.Config4Abstract, com.db4o.config.ObjectField
		, com.db4o.foundation.DeepClone
	{
		private static readonly com.db4o.foundation.KeySpec CLASS = new com.db4o.foundation.KeySpec
			(null);

		private static readonly com.db4o.foundation.KeySpec FIELD_REFLECTOR = new com.db4o.foundation.KeySpec
			(null);

		private static readonly com.db4o.foundation.KeySpec QUERY_EVALUATION = new com.db4o.foundation.KeySpec
			(true);

		private static readonly com.db4o.foundation.KeySpec INDEXED = new com.db4o.foundation.KeySpec
			(com.db4o.YapConst.DEFAULT);

		private static readonly com.db4o.foundation.KeySpec METAFIELD = new com.db4o.foundation.KeySpec
			(null);

		private static readonly com.db4o.foundation.KeySpec INITIALIZED = new com.db4o.foundation.KeySpec
			(false);

		protected Config4Field(com.db4o.foundation.KeySpecHashtable4 config) : base(config
			)
		{
		}

		internal Config4Field(com.db4o.Config4Class a_class, string a_name)
		{
			_config.put(CLASS, a_class);
			setName(a_name);
		}

		private com.db4o.Config4Class classConfig()
		{
			return (com.db4o.Config4Class)_config.get(CLASS);
		}

		internal override string className()
		{
			return classConfig().getName();
		}

		public virtual object deepClone(object param)
		{
			return new com.db4o.Config4Field(_config);
		}

		private com.db4o.reflect.ReflectField fieldReflector()
		{
			com.db4o.reflect.ReflectField fieldReflector = (com.db4o.reflect.ReflectField)_config
				.get(FIELD_REFLECTOR);
			if (fieldReflector == null)
			{
				try
				{
					fieldReflector = classConfig().classReflector().getDeclaredField(getName());
					fieldReflector.setAccessible();
					_config.put(FIELD_REFLECTOR, fieldReflector);
				}
				catch (System.Exception e)
				{
				}
			}
			return fieldReflector;
		}

		public virtual void queryEvaluation(bool flag)
		{
			_config.put(QUERY_EVALUATION, flag);
		}

		public virtual void rename(string newName)
		{
			classConfig().config().rename(new com.db4o.Rename(className(), getName(), newName
				));
			setName(newName);
		}

		public virtual void indexed(bool flag)
		{
			putThreeValued(INDEXED, flag);
		}

		public virtual void initOnUp(com.db4o.Transaction systemTrans, com.db4o.YapField 
			yapField)
		{
			if (!_config.getAsBoolean(INITIALIZED))
			{
				com.db4o.YapStream anyStream = systemTrans.i_stream;
				if (anyStream.maintainsIndices())
				{
					if (!yapField.supportsIndex())
					{
						indexed(false);
					}
					bool indexInitCalled = false;
					com.db4o.YapFile stream = (com.db4o.YapFile)anyStream;
					com.db4o.MetaField metaField = classConfig().metaClass().ensureField(systemTrans, 
						getName());
					_config.put(METAFIELD, metaField);
					int indexedFlag = _config.getAsInt(INDEXED);
					if (indexedFlag == com.db4o.YapConst.YES)
					{
						if (metaField.index == null)
						{
							metaField.index = new com.db4o.MetaIndex();
							stream.setInternal(systemTrans, metaField.index, com.db4o.YapConst.UNSPECIFIED, false
								);
							stream.setInternal(systemTrans, metaField, com.db4o.YapConst.UNSPECIFIED, false);
							yapField.initIndex(systemTrans, metaField.index);
							indexInitCalled = true;
							if (stream.i_config.messageLevel() > com.db4o.YapConst.NONE)
							{
								stream.message("creating index " + yapField.ToString());
							}
							com.db4o.YapClass yapClassField = yapField.getParentYapClass();
							long[] ids = yapClassField.getIDs();
							for (int i = 0; i < ids.Length; i++)
							{
								com.db4o.YapWriter writer = stream.readWriterByID(systemTrans, (int)ids[i]);
								if (writer != null)
								{
									object obj = null;
									com.db4o.YapClass yapClassObject = com.db4o.YapClassAny.readYapClass(writer);
									if (yapClassObject != null)
									{
										if (yapClassObject.findOffset(writer, yapField))
										{
											try
											{
												obj = yapField.i_handler.readIndexValueOrID(writer);
											}
											catch (com.db4o.CorruptionException e)
											{
												if (com.db4o.Deploy.debug || com.db4o.Debug.atHome)
												{
													j4o.lang.JavaSystem.printStackTrace(e);
												}
											}
										}
									}
									yapField.addIndexEntry(systemTrans, (int)ids[i], obj);
								}
							}
							if (ids.Length > 0)
							{
								systemTrans.commit();
							}
						}
					}
					if (indexedFlag == com.db4o.YapConst.NO)
					{
						if (metaField.index != null)
						{
							if (stream.i_config.messageLevel() > com.db4o.YapConst.NONE)
							{
								stream.message("dropping index " + yapField.ToString());
							}
							com.db4o.MetaIndex mi = metaField.index;
							if (mi.indexLength > 0)
							{
								stream.free(mi.indexAddress, mi.indexLength);
							}
							if (mi.patchLength > 0)
							{
								stream.free(mi.patchAddress, mi.patchLength);
							}
							stream.delete1(systemTrans, mi, false);
							metaField.index = null;
							stream.setInternal(systemTrans, metaField, com.db4o.YapConst.UNSPECIFIED, false);
						}
					}
					if (metaField.index != null)
					{
						if (!indexInitCalled)
						{
							yapField.initIndex(systemTrans, metaField.index);
						}
					}
				}
				_config.put(INITIALIZED, true);
			}
		}

		internal virtual bool queryEvaluation()
		{
			return _config.getAsBoolean(QUERY_EVALUATION);
		}
	}
}
