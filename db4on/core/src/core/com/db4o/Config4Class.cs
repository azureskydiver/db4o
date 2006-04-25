namespace com.db4o
{
	internal class Config4Class : com.db4o.Config4Abstract, com.db4o.config.ObjectClass
		, com.db4o.foundation.DeepClone
	{
		private static readonly com.db4o.foundation.KeySpec CALL_CONSTRUCTOR = new com.db4o.foundation.KeySpec
			(0);

		private static readonly com.db4o.foundation.KeySpec CONFIG = new com.db4o.foundation.KeySpec
			(null);

		private static readonly com.db4o.foundation.KeySpec EXCEPTIONAL_FIELDS = new com.db4o.foundation.KeySpec
			(null);

		private static readonly com.db4o.foundation.KeySpec GENERATE_UUIDS = new com.db4o.foundation.KeySpec
			(0);

		private static readonly com.db4o.foundation.KeySpec GENERATE_VERSION_NUMBERS = new 
			com.db4o.foundation.KeySpec(0);

		/// <summary>
		/// We are running into cyclic dependancies on reading the PBootRecord
		/// object, if we maintain MetaClass information there
		/// </summary>
		private static readonly com.db4o.foundation.KeySpec MAINTAIN_METACLASS = new com.db4o.foundation.KeySpec
			(true);

		private static readonly com.db4o.foundation.KeySpec MAXIMUM_ACTIVATION_DEPTH = new 
			com.db4o.foundation.KeySpec(0);

		private static readonly com.db4o.foundation.KeySpec METACLASS = new com.db4o.foundation.KeySpec
			(null);

		private static readonly com.db4o.foundation.KeySpec MINIMUM_ACTIVATION_DEPTH = new 
			com.db4o.foundation.KeySpec(0);

		private static readonly com.db4o.foundation.KeySpec PERSIST_STATIC_FIELD_VALUES = 
			new com.db4o.foundation.KeySpec(false);

		private static readonly com.db4o.foundation.KeySpec QUERY_ATTRIBUTE_PROVIDER = new 
			com.db4o.foundation.KeySpec(null);

		private static readonly com.db4o.foundation.KeySpec STORE_TRANSIENT_FIELDS = new 
			com.db4o.foundation.KeySpec(false);

		private static readonly com.db4o.foundation.KeySpec TRANSLATOR = new com.db4o.foundation.KeySpec
			(null);

		private static readonly com.db4o.foundation.KeySpec TRANSLATOR_NAME = new com.db4o.foundation.KeySpec
			((string)null);

		private static readonly com.db4o.foundation.KeySpec UPDATE_DEPTH = new com.db4o.foundation.KeySpec
			(0);

		private static readonly com.db4o.foundation.KeySpec WRITE_AS = new com.db4o.foundation.KeySpec
			((string)null);

		private bool _processing;

		protected Config4Class(com.db4o.foundation.KeySpecHashtable4 config) : base(config
			)
		{
		}

		internal Config4Class(com.db4o.Config4Impl a_configuration, string a_name)
		{
			_config.put(CONFIG, a_configuration);
			setName(a_name);
		}

		internal virtual int adjustActivationDepth(int a_depth)
		{
			if ((cascadeOnActivate() == com.db4o.YapConst.YES) && a_depth < 2)
			{
				a_depth = 2;
			}
			if ((cascadeOnActivate() == com.db4o.YapConst.NO) && a_depth > 1)
			{
				a_depth = 1;
			}
			if (config().classActivationDepthConfigurable())
			{
				int minimumActivationDepth = _config.getAsInt(MINIMUM_ACTIVATION_DEPTH);
				if (minimumActivationDepth != 0)
				{
					if (a_depth < minimumActivationDepth)
					{
						a_depth = minimumActivationDepth;
					}
				}
				int maximumActivationDepth = _config.getAsInt(MAXIMUM_ACTIVATION_DEPTH);
				if (maximumActivationDepth != 0)
				{
					if (a_depth > maximumActivationDepth)
					{
						a_depth = maximumActivationDepth;
					}
				}
			}
			return a_depth;
		}

		public virtual void callConstructor(bool flag)
		{
			putThreeValued(CALL_CONSTRUCTOR, flag);
		}

		internal override string className()
		{
			return getName();
		}

		internal virtual com.db4o.reflect.ReflectClass classReflector()
		{
			return config().reflector().forName(getName());
		}

		public virtual void compare(com.db4o.config.ObjectAttribute comparator)
		{
			_config.put(QUERY_ATTRIBUTE_PROVIDER, comparator);
		}

		internal virtual com.db4o.Config4Field configField(string fieldName)
		{
			com.db4o.foundation.Hashtable4 exceptionalFields = exceptionalFieldsOrNull();
			if (exceptionalFields == null)
			{
				return null;
			}
			return (com.db4o.Config4Field)exceptionalFields.get(fieldName);
		}

		public virtual object deepClone(object param)
		{
			return new com.db4o.Config4Class(_config);
		}

		public virtual void enableReplication(bool setting)
		{
			generateUUIDs(setting);
			generateVersionNumbers(setting);
		}

		public virtual void generateUUIDs(bool setting)
		{
			putThreeValued(GENERATE_UUIDS, setting);
		}

		public virtual void generateVersionNumbers(bool setting)
		{
			putThreeValued(GENERATE_VERSION_NUMBERS, setting);
		}

		public virtual com.db4o.config.ObjectTranslator getTranslator()
		{
			com.db4o.config.ObjectTranslator translator = (com.db4o.config.ObjectTranslator)_config
				.get(TRANSLATOR);
			if (translator != null)
			{
				return translator;
			}
			string translatorName = _config.getAsString(TRANSLATOR_NAME);
			if (translatorName == null)
			{
				return null;
			}
			try
			{
				translator = (com.db4o.config.ObjectTranslator)config().reflector().forName(translatorName
					).newInstance();
			}
			catch (System.Exception t)
			{
				com.db4o.Messages.logErr(config(), 48, translatorName, null);
				translateOnDemand(null);
			}
			translate(translator);
			return translator;
		}

		public virtual bool initOnUp(com.db4o.Transaction systemTrans, int[] metaClassID)
		{
			if (_processing)
			{
				return false;
			}
			_processing = true;
			com.db4o.YapStream stream = systemTrans.i_stream;
			if (stream.maintainsIndices())
			{
				bool maintainMetaClass = _config.getAsBoolean(MAINTAIN_METACLASS);
				if (maintainMetaClass)
				{
					com.db4o.MetaClass metaClassRef = metaClass();
					if (metaClassID[0] > 0)
					{
						metaClassRef = (com.db4o.MetaClass)stream.getByID1(systemTrans, metaClassID[0]);
						_config.put(METACLASS, metaClassRef);
					}
					if (metaClassRef == null)
					{
						metaClassRef = (com.db4o.MetaClass)stream.get1(systemTrans, new com.db4o.MetaClass
							(getName())).next();
						_config.put(METACLASS, metaClassRef);
						metaClassID[0] = stream.getID1(systemTrans, metaClassRef);
					}
					if (metaClassRef == null)
					{
						metaClassRef = new com.db4o.MetaClass(getName());
						_config.put(METACLASS, metaClassRef);
						stream.setInternal(systemTrans, metaClassRef, int.MaxValue, false);
						metaClassID[0] = stream.getID1(systemTrans, metaClassRef);
					}
					else
					{
						stream.activate1(systemTrans, metaClassRef, int.MaxValue);
					}
				}
			}
			_processing = false;
			return true;
		}

		internal virtual object instantiate(com.db4o.YapStream a_stream, object a_toTranslate
			)
		{
			return ((com.db4o.config.ObjectConstructor)_config.get(TRANSLATOR)).onInstantiate
				(a_stream, a_toTranslate);
		}

		internal virtual bool instantiates()
		{
			return getTranslator() is com.db4o.config.ObjectConstructor;
		}

		public virtual void maximumActivationDepth(int depth)
		{
			_config.put(MAXIMUM_ACTIVATION_DEPTH, depth);
		}

		public virtual void minimumActivationDepth(int depth)
		{
			_config.put(MINIMUM_ACTIVATION_DEPTH, depth);
		}

		public virtual int callConstructor()
		{
			if (_config.get(TRANSLATOR) != null)
			{
				return com.db4o.YapConst.YES;
			}
			return _config.getAsInt(CALL_CONSTRUCTOR);
		}

		private com.db4o.foundation.Hashtable4 exceptionalFieldsOrNull()
		{
			return (com.db4o.foundation.Hashtable4)_config.get(EXCEPTIONAL_FIELDS);
		}

		private com.db4o.foundation.Hashtable4 exceptionalFields()
		{
			com.db4o.foundation.Hashtable4 exceptionalFieldsCollection = exceptionalFieldsOrNull
				();
			if (exceptionalFieldsCollection == null)
			{
				exceptionalFieldsCollection = new com.db4o.foundation.Hashtable4(16);
				_config.put(EXCEPTIONAL_FIELDS, exceptionalFieldsCollection);
			}
			return exceptionalFieldsCollection;
		}

		public virtual com.db4o.config.ObjectField objectField(string fieldName)
		{
			com.db4o.foundation.Hashtable4 exceptionalFieldsCollection = exceptionalFields();
			com.db4o.Config4Field c4f = (com.db4o.Config4Field)exceptionalFieldsCollection.get
				(fieldName);
			if (c4f == null)
			{
				c4f = new com.db4o.Config4Field(this, fieldName);
				exceptionalFieldsCollection.put(fieldName, c4f);
			}
			return c4f;
		}

		public virtual void persistStaticFieldValues()
		{
			_config.put(PERSIST_STATIC_FIELD_VALUES, true);
		}

		internal virtual bool queryEvaluation(string fieldName)
		{
			com.db4o.foundation.Hashtable4 exceptionalFields = exceptionalFieldsOrNull();
			if (exceptionalFields != null)
			{
				com.db4o.Config4Field field = (com.db4o.Config4Field)exceptionalFields.get(fieldName
					);
				if (field != null)
				{
					return field.queryEvaluation();
				}
			}
			return true;
		}

		public virtual void readAs(object clazz)
		{
			com.db4o.Config4Impl configRef = config();
			com.db4o.reflect.ReflectClass claxx = configRef.reflectorFor(clazz);
			if (claxx == null)
			{
				return;
			}
			_config.put(WRITE_AS, getName());
			configRef.readAs().put(getName(), claxx.getName());
		}

		public virtual void rename(string newName)
		{
			config().rename(new com.db4o.Rename("", getName(), newName));
			setName(newName);
		}

		public virtual void storeTransientFields(bool flag)
		{
			_config.put(STORE_TRANSIENT_FIELDS, flag);
		}

		public virtual void translate(com.db4o.config.ObjectTranslator translator)
		{
			if (translator == null)
			{
				_config.put(TRANSLATOR_NAME, null);
			}
			_config.put(TRANSLATOR, translator);
		}

		internal virtual void translateOnDemand(string a_translatorName)
		{
			_config.put(TRANSLATOR_NAME, a_translatorName);
		}

		public virtual void updateDepth(int depth)
		{
			_config.put(UPDATE_DEPTH, depth);
		}

		internal virtual com.db4o.Config4Impl config()
		{
			return (com.db4o.Config4Impl)_config.get(CONFIG);
		}

		internal virtual int generateUUIDs()
		{
			return _config.getAsInt(GENERATE_UUIDS);
		}

		internal virtual int generateVersionNumbers()
		{
			return _config.getAsInt(GENERATE_VERSION_NUMBERS);
		}

		internal virtual void maintainMetaClass(bool flag)
		{
			_config.put(MAINTAIN_METACLASS, flag);
		}

		internal virtual com.db4o.MetaClass metaClass()
		{
			return (com.db4o.MetaClass)_config.get(METACLASS);
		}

		internal virtual bool staticFieldValuesArePersisted()
		{
			return _config.getAsBoolean(PERSIST_STATIC_FIELD_VALUES);
		}

		internal virtual com.db4o.config.ObjectAttribute queryAttributeProvider()
		{
			return (com.db4o.config.ObjectAttribute)_config.get(QUERY_ATTRIBUTE_PROVIDER);
		}

		internal virtual bool storeTransientFields()
		{
			return _config.getAsBoolean(STORE_TRANSIENT_FIELDS);
		}

		internal virtual int updateDepth()
		{
			return _config.getAsInt(UPDATE_DEPTH);
		}

		internal virtual string writeAs()
		{
			return _config.getAsString(WRITE_AS);
		}
	}
}
