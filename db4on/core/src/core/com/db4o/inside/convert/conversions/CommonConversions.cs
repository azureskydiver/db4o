namespace com.db4o.inside.convert.conversions
{
	/// <exclude></exclude>
	public class CommonConversions
	{
		public CommonConversions(com.db4o.inside.convert.Converter converter)
		{
			converter.Register(5, new com.db4o.inside.convert.conversions.ClassIndexesToBTrees
				());
		}
	}
}
