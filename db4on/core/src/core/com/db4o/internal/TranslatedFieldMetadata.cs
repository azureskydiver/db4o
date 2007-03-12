namespace com.db4o.@internal
{
	internal sealed class TranslatedFieldMetadata : com.db4o.@internal.FieldMetadata
	{
		private readonly com.db4o.config.ObjectTranslator i_translator;

		internal TranslatedFieldMetadata(com.db4o.@internal.ClassMetadata containingClass
			, com.db4o.config.ObjectTranslator translator) : base(containingClass, translator
			)
		{
			i_translator = translator;
			com.db4o.@internal.ObjectContainerBase stream = containingClass.GetStream();
			Configure(stream.Reflector().ForClass(translator.StoredClass()), false);
		}

		public override bool CanUseNullBitmap()
		{
			return false;
		}

		internal override void Deactivate(com.db4o.@internal.Transaction a_trans, object 
			a_onObject, int a_depth)
		{
			if (a_depth > 0)
			{
				CascadeActivation(a_trans, a_onObject, a_depth, false);
			}
			SetOn(a_trans.Stream(), a_onObject, null);
		}

		public override object GetOn(com.db4o.@internal.Transaction a_trans, object a_OnObject
			)
		{
			return i_translator.OnStore(a_trans.Stream(), a_OnObject);
		}

		public override object GetOrCreate(com.db4o.@internal.Transaction a_trans, object
			 a_OnObject)
		{
			return GetOn(a_trans, a_OnObject);
		}

		public override void Instantiate(com.db4o.@internal.marshall.MarshallerFamily mf, 
			com.db4o.@internal.ObjectReference a_yapObject, object a_onObject, com.db4o.@internal.StatefulBuffer
			 a_bytes)
		{
			object toSet = Read(mf, a_bytes);
			a_bytes.GetStream().Activate1(a_bytes.GetTransaction(), toSet, a_bytes.GetInstantiationDepth
				());
			SetOn(a_bytes.GetStream(), a_onObject, toSet);
		}

		internal override void Refresh()
		{
		}

		private void SetOn(com.db4o.@internal.ObjectContainerBase a_stream, object a_onObject
			, object toSet)
		{
			i_translator.OnActivate(a_stream, a_onObject, toSet);
		}

		protected override object IndexEntryFor(object indexEntry)
		{
			return indexEntry;
		}

		protected override com.db4o.@internal.ix.Indexable4 IndexHandler(com.db4o.@internal.ObjectContainerBase
			 stream)
		{
			return i_handler;
		}
	}
}
