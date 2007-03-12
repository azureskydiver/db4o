namespace com.db4o.@internal.cs
{
	/// <summary>Platform specific defaults.</summary>
	/// <remarks>Platform specific defaults.</remarks>
	public class ClientServerPlatform
	{
		/// <summary>
		/// The default
		/// <see cref="com.db4o.@internal.cs.ClientQueryResultIterator">com.db4o.@internal.cs.ClientQueryResultIterator
		/// 	</see>
		/// for this platform.
		/// </summary>
		/// <returns></returns>
		public static System.Collections.IEnumerator CreateClientQueryResultIterator(com.db4o.@internal.query.result.AbstractQueryResult
			 result)
		{
			com.db4o.@internal.cs.QueryResultIteratorFactory factory = result.Config().QueryResultIteratorFactory
				();
			if (null != factory)
			{
				return factory.NewInstance(result);
			}
			return new com.db4o.@internal.cs.ClientQueryResultIterator(result);
		}
	}
}
