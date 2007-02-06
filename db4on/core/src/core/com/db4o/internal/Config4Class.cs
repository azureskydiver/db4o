namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class Config4Class : com.db4o.@internal.Config4Abstract, com.db4o.config.ObjectClass
		, com.db4o.foundation.DeepClone
	{
		private readonly com.db4o.@internal.Config4Impl _configImpl;

		private static readonly com.db4o.foundation.KeySpec CALL_CONSTRUCTOR = new com.db4o.foundation.KeySpec
			(0);

		private static readonly com.db4o.foundation.KeySpec CLASS_INDEXED = new com.db4o.foundation.KeySpec
			(true);

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

		protected Config4Class(com.db4o.@internal.Config4Impl configuration, com.db4o.foundation.KeySpecHashtable4
			 config) : base(config)
		{
			_configImpl = configuration;
		}

		internal Config4Class(com.db4o.@internal.Config4Impl a_configuration, string a_name
			)
		{
			_configImpl = a_configuration;
			SetName(a_name);
		}

		internal virtual int AdjustActivationDepth(int depth)
		{
			int cascadeOnActivate = CascadeOnActivate();
			if (cascadeOnActivate == com.db4o.@internal.Const4.YES && depth < 2)
			{
				depth = 2;
			}
			if (cascadeOnActivate == com.db4o.@internal.Const4.NO && depth > 1)
			{
				depth = 1;
			}
			if (Config().ClassActivationDepthConfigurable())
			{
				int minimumActivationDepth = MinimumActivationDepth();
				if (minimumActivationDepth != 0 && depth < minimumActivationDepth)
				{
					depth = minimumActivationDepth;
				}
				int maximumActivationDepth = MaximumActivationDepth();
				if (maximumActivationDepth != 0 && depth > maximumActivationDepth)
				{
					depth = maximumActivationDepth;
				}
			}
			return depth;
		}

		public virtual void CallConstructor(bool flag)
		{
			PutThreeValued(CALL_CONSTRUCTOR, flag);
		}

		internal override string ClassName()
		{
			return GetName();
		}

		internal virtual com.db4o.reflect.ReflectClass ClassReflector()
		{
			return Config().Reflector().ForName(GetName());
		}

		public virtual void Compare(com.db4o.config.ObjectAttribute comparator)
		{
			_config.Put(QUERY_ATTRIBUTE_PROVIDER, comparator);
		}

		internal virtual com.db4o.@internal.Config4Field ConfigField(string fieldName)
		{
			com.db4o.foundation.Hashtable4 exceptionalFields = ExceptionalFieldsOrNull();
			if (exceptionalFields == null)
			{
				return null;
			}
			return (com.db4o.@internal.Config4Field)exceptionalFields.Get(fieldName);
		}

		public virtual object DeepClone(object param)
		{
			return new com.db4o.@internal.Config4Class((com.db4o.@internal.Config4Impl)param, 
				_config);
		}

		public virtual void EnableReplication(bool setting)
		{
			GenerateUUIDs(setting);
			GenerateVersionNumbers(setting);
		}

		public virtual void GenerateUUIDs(bool setting)
		{
			PutThreeValued(GENERATE_UUIDS, setting);
		}

		public virtual void GenerateVersionNumbers(bool setting)
		{
			PutThreeValued(GENERATE_VERSION_NUMBERS, setting);
		}

		public virtual com.db4o.config.ObjectTranslator GetTranslator()
		{
			com.db4o.config.ObjectTranslator translator = (com.db4o.config.ObjectTranslator)_config
				.Get(TRANSLATOR);
			if (translator != null)
			{
				return translator;
			}
			string translatorName = _config.GetAsString(TRANSLATOR_NAME);
			if (translatorName == null)
			{
				return null;
			}
			try
			{
				translator = (com.db4o.config.ObjectTranslator)Config().Reflector().ForName(translatorName
					).NewInstance();
			}
			catch
			{
				com.db4o.@internal.Messages.LogErr(Config(), 48, translatorName, null);
				TranslateOnDemand(null);
			}
			Translate(translator);
			return translator;
		}

		public virtual void Indexed(bool flag)
		{
			_config.Put(CLASS_INDEXED, flag);
		}

		public virtual bool Indexed()
		{
			return _config.GetAsBoolean(CLASS_INDEXED);
		}

		internal virtual object Instantiate(com.db4o.@internal.ObjectContainerBase a_stream
			, object a_toTranslate)
		{
			return ((com.db4o.config.ObjectConstructor)_config.Get(TRANSLATOR)).OnInstantiate
				(a_stream, a_toTranslate);
		}

		internal virtual bool Instantiates()
		{
			return GetTranslator() is com.db4o.config.ObjectConstructor;
		}

		public virtual void MaximumActivationDepth(int depth)
		{
			_config.Put(MAXIMUM_ACTIVATION_DEPTH, depth);
		}

		internal virtual int MaximumActivationDepth()
		{
			return _config.GetAsInt(MAXIMUM_ACTIVATION_DEPTH);
		}

		public virtual void MinimumActivationDepth(int depth)
		{
			_config.Put(MINIMUM_ACTIVATION_DEPTH, depth);
		}

		internal virtual int MinimumActivationDepth()
		{
			return _config.GetAsInt(MINIMUM_ACTIVATION_DEPTH);
		}

		public virtual int CallConstructor()
		{
			if (_config.Get(TRANSLATOR) != null)
			{
				return com.db4o.@internal.Const4.YES;
			}
			return _config.GetAsInt(CALL_CONSTRUCTOR);
		}

		private com.db4o.foundation.Hashtable4 ExceptionalFieldsOrNull()
		{
			return (com.db4o.foundation.Hashtable4)_config.Get(EXCEPTIONAL_FIELDS);
		}

		private com.db4o.foundation.Hashtable4 ExceptionalFields()
		{
			com.db4o.foundation.Hashtable4 exceptionalFieldsCollection = ExceptionalFieldsOrNull
				();
			if (exceptionalFieldsCollection == null)
			{
				exceptionalFieldsCollection = new com.db4o.foundation.Hashtable4(16);
				_config.Put(EXCEPTIONAL_FIELDS, exceptionalFieldsCollection);
			}
			return exceptionalFieldsCollection;
		}

		public virtual com.db4o.config.ObjectField ObjectField(string fieldName)
		{
			com.db4o.foundation.Hashtable4 exceptionalFieldsCollection = ExceptionalFields();
			com.db4o.@internal.Config4Field c4f = (com.db4o.@internal.Config4Field)exceptionalFieldsCollection
				.Get(fieldName);
			if (c4f == null)
			{
				c4f = new com.db4o.@internal.Config4Field(this, fieldName);
				exceptionalFieldsCollection.Put(fieldName, c4f);
			}
			return c4f;
		}

		public virtual void PersistStaticFieldValues()
		{
			_config.Put(PERSIST_STATIC_FIELD_VALUES, true);
		}

		internal virtual bool QueryEvaluation(string fieldName)
		{
			com.db4o.foundation.Hashtable4 exceptionalFields = ExceptionalFieldsOrNull();
			if (exceptionalFields != null)
			{
				com.db4o.@internal.Config4Field field = (com.db4o.@internal.Config4Field)exceptionalFields
					.Get(fieldName);
				if (field != null)
				{
					return field.QueryEvaluation();
				}
			}
			return true;
		}

		public virtual void ReadAs(object clazz)
		{
			com.db4o.@internal.Config4Impl configRef = Config();
			com.db4o.reflect.ReflectClass claxx = configRef.ReflectorFor(clazz);
			if (claxx == null)
			{
				return;
			}
			_config.Put(WRITE_AS, GetName());
			configRef.ReadAs().Put(GetName(), claxx.GetName());
		}

		public virtual void Rename(string newName)
		{
			Config().Rename(new com.db4o.Rename(string.Empty, GetName(), newName));
			SetName(newName);
		}

		public virtual void StoreTransientFields(bool flag)
		{
			_config.Put(STORE_TRANSIENT_FIELDS, flag);
		}

		public virtual void Translate(com.db4o.config.ObjectTranslator translator)
		{
			if (translator == null)
			{
				_config.Put(TRANSLATOR_NAME, null);
			}
			_config.Put(TRANSLATOR, translator);
		}

		internal virtual void TranslateOnDemand(string a_translatorName)
		{
			_config.Put(TRANSLATOR_NAME, a_translatorName);
		}

		public virtual void UpdateDepth(int depth)
		{
			_config.Put(UPDATE_DEPTH, depth);
		}

		internal virtual com.db4o.@internal.Config4Impl Config()
		{
			return _configImpl;
		}

		internal virtual int GenerateUUIDs()
		{
			return _config.GetAsInt(GENERATE_UUIDS);
		}

		internal virtual int GenerateVersionNumbers()
		{
			return _config.GetAsInt(GENERATE_VERSION_NUMBERS);
		}

		internal virtual void MaintainMetaClass(bool flag)
		{
			_config.Put(MAINTAIN_METACLASS, flag);
		}

		internal virtual bool StaticFieldValuesArePersisted()
		{
			return _config.GetAsBoolean(PERSIST_STATIC_FIELD_VALUES);
		}

		public virtual com.db4o.config.ObjectAttribute QueryAttributeProvider()
		{
			return (com.db4o.config.ObjectAttribute)_config.Get(QUERY_ATTRIBUTE_PROVIDER);
		}

		internal virtual bool StoreTransientFields()
		{
			return _config.GetAsBoolean(STORE_TRANSIENT_FIELDS);
		}

		internal virtual int UpdateDepth()
		{
			return _config.GetAsInt(UPDATE_DEPTH);
		}

		internal virtual string WriteAs()
		{
			return _config.GetAsString(WRITE_AS);
		}
	}
}
