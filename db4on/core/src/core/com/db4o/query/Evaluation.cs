namespace com.db4o.query
{
	/// <summary>for implementation of callback evaluations.</summary>
	/// <remarks>
	/// for implementation of callback evaluations.
	/// <br /><br />
	/// To constrain a
	/// <see cref="com.db4o.query.Query">com.db4o.query.Query</see>
	/// node with your own callback
	/// <code>Evaluation</code>, construct an object that implements the
	/// <code>Evaluation</code> interface and register it by passing it
	/// to
	/// <see cref="com.db4o.query.Query.Constrain">com.db4o.query.Query.Constrain</see>
	/// .
	/// <br /><br />
	/// Evaluations are called as the last step during query execution,
	/// after all other constraints have been applied. Evaluations in higher
	/// level
	/// <see cref="com.db4o.query.Query">com.db4o.query.Query</see>
	/// nodes in the query graph are called first.
	/// <br /><br />Java client/server only:<br />
	/// db4o first attempts to use Java Serialization to allow to pass final
	/// variables to the server. Please make sure that all variables that are
	/// used within the evaluate() method are Serializable. This may include
	/// the class an anonymous Evaluation object is created in. If db4o is
	/// not successful at using Serialization, the Evaluation is transported
	/// to the server in a db4o MemoryFile. In this case final variables can
	/// not be restored.
	/// </remarks>
	public interface Evaluation
	{
		/// <summary>
		/// callback method during
		/// <see cref="com.db4o.query.Query.Execute">query execution</see>
		/// .
		/// </summary>
		/// <param name="candidate">reference to the candidate persistent object.</param>
		void Evaluate(com.db4o.query.Candidate candidate);
	}
}
