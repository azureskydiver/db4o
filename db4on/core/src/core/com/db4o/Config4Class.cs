namespace com.db4o
{
	internal class Config4Class : com.db4o.Config4Abstract, com.db4o.config.ObjectClass
		, j4o.lang.Cloneable, com.db4o.foundation.DeepClone
	{
		internal int i_callConstructor;

		internal com.db4o.Config4Impl i_config;

		private com.db4o.foundation.Hashtable4 i_exceptionalFields;

		internal int i_generateUUIDs;

		internal int i_generateVersionNumbers;

		/// <summary>
		/// We are running into cyclic dependancies on reading the PBootRecord
		/// object, if we maintain MetaClass information there
		/// </summary>
		internal bool _maintainMetaClass = true;

		internal int i_maximumActivationDepth;

		internal com.db4o.MetaClass i_metaClass;

		internal int i_minimumActivationDepth;

		internal bool i_persistStaticFieldValues;

		internal com.db4o.config.ObjectAttribute i_queryAttributeProvider;

		internal bool i_storeTransientFields;

		internal com.db4o.config.ObjectTranslator i_translator;

		internal string i_translatorName;

		internal int i_updateDepth;

		internal string _writeAs;

		internal Config4Class(com.db4o.Config4Impl a_configuration, string a_name)
		{
			i_config = a_configuration;
			i_name = a_name;
		}

		internal virtual int adjustActivationDepth(int a_depth)
		{
			if ((i_cascadeOnActivate == 1) && a_depth < 2)
			{
				a_depth = 2;
			}
			if ((i_cascadeOnActivate == -1) && a_depth > 1)
			{
				a_depth = 1;
			}
			if (i_config.i_classActivationDepthConfigurable)
			{
				if (i_minimumActivationDepth != 0)
				{
					if (a_depth < i_minimumActivationDepth)
					{
						a_depth = i_minimumActivationDepth;
					}
				}
				if (i_maximumActivationDepth != 0)
				{
					if (a_depth > i_maximumActivationDepth)
					{
						a_depth = i_maximumActivationDepth;
					}
				}
			}
			return a_depth;
		}

		public virtual void callConstructor(bool flag)
		{
			i_callConstructor = flag ? com.db4o.YapConst.YES : com.db4o.YapConst.NO;
		}

		internal override string className()
		{
			return getName();
		}

		internal virtual com.db4o.reflect.ReflectClass classReflector()
		{
			return i_config.reflector().forName(i_name);
		}

		public virtual void compare(com.db4o.config.ObjectAttribute comparator)
		{
			i_queryAttributeProvider = comparator;
		}

		internal virtual com.db4o.Config4Field configField(string fieldName)
		{
			if (i_exceptionalFields == null)
			{
				return null;
			}
			return (com.db4o.Config4Field)i_exceptionalFields.get(fieldName);
		}

		public virtual object deepClone(object param)
		{
			com.db4o.Config4Class ret = null;
			try
			{
				ret = (com.db4o.Config4Class)j4o.lang.JavaSystem.clone(this);
			}
			catch (j4o.lang.CloneNotSupportedException e)
			{
			}
			ret.i_config = (com.db4o.Config4Impl)param;
			if (i_exceptionalFields != null)
			{
				ret.i_exceptionalFields = (com.db4o.foundation.Hashtable4)i_exceptionalFields.deepClone
					(ret);
			}
			return ret;
		}

		public virtual void enableReplication(bool setting)
		{
			generateUUIDs(setting);
			generateVersionNumbers(setting);
		}

		public virtual void generateUUIDs(bool setting)
		{
			i_generateUUIDs = setting ? com.db4o.YapConst.YES : com.db4o.YapConst.NO;
		}

		public virtual void generateVersionNumbers(bool setting)
		{
			i_generateVersionNumbers = setting ? com.db4o.YapConst.YES : com.db4o.YapConst.NO;
		}

		public virtual com.db4o.config.ObjectTranslator getTranslator()
		{
			if (i_translator == null && i_translatorName != null)
			{
				try
				{
					i_translator = (com.db4o.config.ObjectTranslator)i_config.reflector().forName(i_translatorName
						).newInstance();
				}
				catch (System.Exception t)
				{
					com.db4o.Messages.logErr(i_config, 48, i_translatorName, null);
					i_translatorName = null;
				}
			}
			return i_translator;
		}

		public virtual void initOnUp(com.db4o.Transaction systemTrans)
		{
			com.db4o.YapStream stream = systemTrans.i_stream;
			if (stream.maintainsIndices())
			{
				if (_maintainMetaClass)
				{
					i_metaClass = (com.db4o.MetaClass)stream.get1(systemTrans, new com.db4o.MetaClass
						(i_name)).next();
					if (i_metaClass == null)
					{
						i_metaClass = new com.db4o.MetaClass(i_name);
						stream.setInternal(systemTrans, i_metaClass, int.MaxValue, false);
					}
					else
					{
						stream.activate1(systemTrans, i_metaClass, int.MaxValue);
					}
				}
			}
		}

		internal virtual object instantiate(com.db4o.YapStream a_stream, object a_toTranslate
			)
		{
			return ((com.db4o.config.ObjectConstructor)i_translator).onInstantiate(a_stream, 
				a_toTranslate);
		}

		internal virtual bool instantiates()
		{
			return getTranslator() is com.db4o.config.ObjectConstructor;
		}

		public virtual void maximumActivationDepth(int depth)
		{
			i_maximumActivationDepth = depth;
		}

		public virtual void minimumActivationDepth(int depth)
		{
			i_minimumActivationDepth = depth;
		}

		public virtual int callConstructor()
		{
			if (i_translator != null)
			{
				return com.db4o.YapConst.YES;
			}
			return i_callConstructor;
		}

		public virtual com.db4o.config.ObjectField objectField(string fieldName)
		{
			if (i_exceptionalFields == null)
			{
				i_exceptionalFields = new com.db4o.foundation.Hashtable4(16);
			}
			com.db4o.Config4Field c4f = (com.db4o.Config4Field)i_exceptionalFields.get(fieldName
				);
			if (c4f == null)
			{
				c4f = new com.db4o.Config4Field(this, fieldName);
				i_exceptionalFields.put(fieldName, c4f);
			}
			return c4f;
		}

		public virtual void persistStaticFieldValues()
		{
			i_persistStaticFieldValues = true;
		}

		internal virtual bool queryEvaluation(string fieldName)
		{
			if (i_exceptionalFields != null)
			{
				com.db4o.Config4Field field = (com.db4o.Config4Field)i_exceptionalFields.get(fieldName
					);
				if (field != null)
				{
					return field.i_queryEvaluation;
				}
			}
			return true;
		}

		public virtual void readAs(object clazz)
		{
			com.db4o.reflect.ReflectClass claxx = i_config.reflectorFor(clazz);
			if (claxx == null)
			{
				return;
			}
			_writeAs = i_name;
			i_config._readAs.put(_writeAs, claxx.getName());
		}

		public virtual void rename(string newName)
		{
			i_config.rename(new com.db4o.Rename("", i_name, newName));
			i_name = newName;
		}

		public virtual void storeTransientFields(bool flag)
		{
			i_storeTransientFields = flag;
		}

		public virtual void translate(com.db4o.config.ObjectTranslator translator)
		{
			if (translator == null)
			{
				i_translatorName = null;
			}
			i_translator = translator;
		}

		internal virtual void translateOnDemand(string a_translatorName)
		{
			i_translatorName = a_translatorName;
		}

		public virtual void updateDepth(int depth)
		{
			i_updateDepth = depth;
		}
	}
}
