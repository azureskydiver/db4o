namespace com.db4o
{
	internal sealed class YapFieldTranslator : com.db4o.YapField
	{
		private readonly com.db4o.config.ObjectTranslator i_translator;

		internal YapFieldTranslator(com.db4o.YapClass a_yapClass, com.db4o.config.ObjectTranslator
			 a_translator) : base(a_yapClass, a_translator)
		{
			i_translator = a_translator;
			com.db4o.YapStream stream = a_yapClass.getStream();
			configure(stream.reflector().forClass(a_translator.storedClass()), false);
		}

		internal override void deactivate(com.db4o.Transaction a_trans, object a_onObject
			, int a_depth)
		{
			if (a_depth > 0)
			{
				cascadeActivation(a_trans, a_onObject, a_depth, false);
			}
			setOn(a_trans.i_stream, a_onObject, null);
		}

		internal override object getOn(com.db4o.Transaction a_trans, object a_OnObject)
		{
			try
			{
				return i_translator.onStore(a_trans.i_stream, a_OnObject);
			}
			catch (System.Exception t)
			{
				return null;
			}
		}

		internal override object getOrCreate(com.db4o.Transaction a_trans, object a_OnObject
			)
		{
			return getOn(a_trans, a_OnObject);
		}

		internal override void instantiate(com.db4o.YapObject a_yapObject, object a_onObject
			, com.db4o.YapWriter a_bytes)
		{
			object toSet = read(a_bytes);
			a_bytes.getStream().activate2(a_bytes.getTransaction(), toSet, a_bytes.getInstantiationDepth
				());
			setOn(a_bytes.getStream(), a_onObject, toSet);
		}

		internal override void refresh()
		{
		}

		private void setOn(com.db4o.YapStream a_stream, object a_onObject, object toSet)
		{
			try
			{
				i_translator.onActivate(a_stream, a_onObject, toSet);
			}
			catch (System.Exception t)
			{
			}
		}
	}
}
