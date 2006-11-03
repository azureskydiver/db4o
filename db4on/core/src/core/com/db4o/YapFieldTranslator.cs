namespace com.db4o
{
	internal sealed class YapFieldTranslator : com.db4o.YapField
	{
		private readonly com.db4o.config.ObjectTranslator i_translator;

		internal YapFieldTranslator(com.db4o.YapClass a_yapClass, com.db4o.config.ObjectTranslator
			 a_translator) : base(a_yapClass, a_translator)
		{
			i_translator = a_translator;
			com.db4o.YapStream stream = a_yapClass.GetStream();
			Configure(stream.Reflector().ForClass(a_translator.StoredClass()), false);
		}

		public override bool CanUseNullBitmap()
		{
			return false;
		}

		internal override void Deactivate(com.db4o.Transaction a_trans, object a_onObject
			, int a_depth)
		{
			if (a_depth > 0)
			{
				CascadeActivation(a_trans, a_onObject, a_depth, false);
			}
			SetOn(a_trans.Stream(), a_onObject, null);
		}

		public override object GetOn(com.db4o.Transaction a_trans, object a_OnObject)
		{
			try
			{
				return i_translator.OnStore(a_trans.Stream(), a_OnObject);
			}
			catch
			{
				return null;
			}
		}

		public override object GetOrCreate(com.db4o.Transaction a_trans, object a_OnObject
			)
		{
			return GetOn(a_trans, a_OnObject);
		}

		public override void Instantiate(com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapObject
			 a_yapObject, object a_onObject, com.db4o.YapWriter a_bytes)
		{
			object toSet = Read(mf, a_bytes);
			a_bytes.GetStream().Activate1(a_bytes.GetTransaction(), toSet, a_bytes.GetInstantiationDepth
				());
			SetOn(a_bytes.GetStream(), a_onObject, toSet);
		}

		internal override void Refresh()
		{
		}

		private void SetOn(com.db4o.YapStream a_stream, object a_onObject, object toSet)
		{
			try
			{
				i_translator.OnActivate(a_stream, a_onObject, toSet);
			}
			catch
			{
			}
		}
	}
}
