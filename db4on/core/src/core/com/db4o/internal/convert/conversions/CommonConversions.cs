namespace com.db4o.@internal.convert.conversions
{
	/// <exclude></exclude>
	public class CommonConversions
	{
		public static void Register(com.db4o.@internal.convert.Converter converter)
		{
			converter.Register(com.db4o.@internal.convert.conversions.ClassIndexesToBTrees_5_5
				.VERSION, new com.db4o.@internal.convert.conversions.ClassIndexesToBTrees_5_5());
			converter.Register(com.db4o.@internal.convert.conversions.FieldIndexesToBTrees_5_7
				.VERSION, new com.db4o.@internal.convert.conversions.FieldIndexesToBTrees_5_7());
		}
	}
}
