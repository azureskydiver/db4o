namespace com.db4o.@internal.convert
{
	/// <exclude></exclude>
	public class Converter
	{
		public const int VERSION = com.db4o.@internal.convert.conversions.FieldIndexesToBTrees_5_7
			.VERSION;

		private static com.db4o.@internal.convert.Converter _converter;

		private com.db4o.foundation.Hashtable4 _conversions;

		private Converter()
		{
			_conversions = new com.db4o.foundation.Hashtable4();
			com.db4o.@internal.convert.conversions.CommonConversions.Register(this);
		}

		public static bool Convert(com.db4o.@internal.convert.ConversionStage stage)
		{
			if (!NeedsConversion(stage.SystemData()))
			{
				return false;
			}
			if (_converter == null)
			{
				_converter = new com.db4o.@internal.convert.Converter();
			}
			return _converter.RunConversions(stage);
		}

		private static bool NeedsConversion(com.db4o.@internal.SystemData systemData)
		{
			return systemData.ConverterVersion() < VERSION;
		}

		public virtual void Register(int idx, com.db4o.@internal.convert.Conversion conversion
			)
		{
			if (_conversions.Get(idx) != null)
			{
				throw new System.InvalidOperationException();
			}
			_conversions.Put(idx, conversion);
		}

		public virtual bool RunConversions(com.db4o.@internal.convert.ConversionStage stage
			)
		{
			com.db4o.@internal.SystemData systemData = stage.SystemData();
			if (!NeedsConversion(systemData))
			{
				return false;
			}
			for (int i = systemData.ConverterVersion(); i <= VERSION; i++)
			{
				com.db4o.@internal.convert.Conversion conversion = (com.db4o.@internal.convert.Conversion
					)_conversions.Get(i);
				if (conversion != null)
				{
					stage.Accept(conversion);
				}
			}
			return true;
		}
	}
}
