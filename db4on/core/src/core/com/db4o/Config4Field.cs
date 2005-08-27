
namespace com.db4o
{
	internal class Config4Field : com.db4o.Config4Abstract, com.db4o.config.ObjectField
		, j4o.lang.Cloneable, com.db4o.foundation.DeepClone
	{
		internal com.db4o.Config4Class i_class;

		internal com.db4o.reflect.ReflectField i_fieldReflector;

		internal bool i_queryEvaluation = true;

		internal int i_indexed = 0;

		internal com.db4o.MetaField i_metaField;

		internal bool i_initialized;

		internal Config4Field(com.db4o.Config4Class a_class, string a_name)
		{
			i_class = a_class;
			i_name = a_name;
		}

		internal override string className()
		{
			return i_class.getName();
		}

		public virtual object deepClone(object param)
		{
			com.db4o.Config4Field ret = null;
			try
			{
				ret = (com.db4o.Config4Field)j4o.lang.JavaSystem.clone(this);
				ret.i_metaField = null;
				ret.i_initialized = false;
			}
			catch (j4o.lang.CloneNotSupportedException e)
			{
			}
			ret.i_class = (com.db4o.Config4Class)param;
			return ret;
		}

		private com.db4o.reflect.ReflectField fieldReflector()
		{
			if (i_fieldReflector == null)
			{
				try
				{
					i_fieldReflector = i_class.classReflector().getDeclaredField(getName());
					i_fieldReflector.setAccessible();
				}
				catch (System.Exception e)
				{
				}
			}
			return i_fieldReflector;
		}

		public virtual void queryEvaluation(bool flag)
		{
			i_queryEvaluation = flag;
		}

		public virtual void rename(string newName)
		{
			i_class.i_config.rename(new com.db4o.Rename(i_class.getName(), i_name, newName));
			i_name = newName;
		}

		public virtual void indexed(bool flag)
		{
			if (flag)
			{
				i_indexed = 1;
			}
			else
			{
				i_indexed = -1;
			}
		}

		public virtual void initOnUp(com.db4o.Transaction systemTrans, com.db4o.YapField 
			yapField)
		{
			if (!i_initialized)
			{
				com.db4o.YapStream anyStream = systemTrans.i_stream;
				if (anyStream.maintainsIndices())
				{
					if (!yapField.supportsIndex())
					{
						i_indexed = -1;
					}
					bool indexInitCalled = false;
					com.db4o.YapFile stream = (com.db4o.YapFile)anyStream;
					i_metaField = i_class.i_metaClass.ensureField(systemTrans, i_name);
					if (i_indexed == 1)
					{
						if (i_metaField.index == null)
						{
							i_metaField.index = new com.db4o.MetaIndex();
							stream.setInternal(systemTrans, i_metaField.index, com.db4o.YapConst.UNSPECIFIED, 
								false);
							stream.setInternal(systemTrans, i_metaField, com.db4o.YapConst.UNSPECIFIED, false
								);
							yapField.initIndex(systemTrans, i_metaField.index);
							indexInitCalled = true;
							if (stream.i_config.i_messageLevel > com.db4o.YapConst.NONE)
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
					if (i_indexed == -1)
					{
						if (i_metaField.index != null)
						{
							if (stream.i_config.i_messageLevel > com.db4o.YapConst.NONE)
							{
								stream.message("dropping index " + yapField.ToString());
							}
							com.db4o.MetaIndex mi = i_metaField.index;
							if (mi.indexLength > 0)
							{
								stream.free(mi.indexAddress, mi.indexLength);
							}
							if (mi.patchLength > 0)
							{
								stream.free(mi.patchAddress, mi.patchLength);
							}
							stream.delete1(systemTrans, mi, false);
							i_metaField.index = null;
							stream.setInternal(systemTrans, i_metaField, com.db4o.YapConst.UNSPECIFIED, false
								);
						}
					}
					if (i_metaField.index != null)
					{
						if (!indexInitCalled)
						{
							yapField.initIndex(systemTrans, i_metaField.index);
						}
					}
				}
				i_initialized = true;
			}
		}
	}
}
