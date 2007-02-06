namespace com.db4o.@internal.query.processor
{
	/// <summary>QQuery is the users hook on our graph.</summary>
	/// <remarks>
	/// QQuery is the users hook on our graph.
	/// A QQuery is defined by it's constraints.
	/// </remarks>
	/// <exclude></exclude>
	public class QQuery : com.db4o.@internal.query.processor.QQueryBase, com.db4o.query.Query
	{
		public QQuery(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.query.processor.QQuery
			 a_parent, string a_field) : base(a_trans, a_parent, a_field)
		{
		}

		public QQuery() : base()
		{
		}
	}
}
