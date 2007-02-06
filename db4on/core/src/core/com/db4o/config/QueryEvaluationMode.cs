namespace com.db4o.config
{
	/// <summary>
	/// This class provides static constants for the query evaluation
	/// modes that db4o supports.
	/// </summary>
	/// <remarks>
	/// This class provides static constants for the query evaluation
	/// modes that db4o supports.
	/// <br /><br /><b>For detailed documentation please see
	/// <see cref="com.db4o.config.QueryConfiguration.EvaluationMode">com.db4o.config.QueryConfiguration.EvaluationMode
	/// 	</see>
	/// </b>
	/// </remarks>
	public class QueryEvaluationMode
	{
		private QueryEvaluationMode()
		{
		}

		/// <summary>Constant for immediate query evaluation.</summary>
		/// <remarks>
		/// Constant for immediate query evaluation. The query is executed fully
		/// when Query#execute() is called.
		/// <br /><br /><b>For detailed documentation please see
		/// <see cref="com.db4o.config.QueryConfiguration.EvaluationMode">com.db4o.config.QueryConfiguration.EvaluationMode
		/// 	</see>
		/// </b>
		/// </remarks>
		public static readonly com.db4o.config.QueryEvaluationMode IMMEDIATE = new com.db4o.config.QueryEvaluationMode
			();

		/// <summary>Constant for snapshot query evaluation.</summary>
		/// <remarks>
		/// Constant for snapshot query evaluation. When Query#execute() is called,
		/// the query processor chooses the best indexes, does all index processing
		/// and creates a snapshot of the index at this point in time. Non-indexed
		/// constraints are evaluated lazily when the application iterates through
		/// the
		/// <see cref="ObjectSet">ObjectSet</see>
		/// resultset of the query.
		/// <br /><br /><b>For detailed documentation please see
		/// <see cref="com.db4o.config.QueryConfiguration.EvaluationMode">com.db4o.config.QueryConfiguration.EvaluationMode
		/// 	</see>
		/// </b>
		/// </remarks>
		public static readonly com.db4o.config.QueryEvaluationMode SNAPSHOT = new com.db4o.config.QueryEvaluationMode
			();

		/// <summary>Constant for lazy query evaluation.</summary>
		/// <remarks>
		/// Constant for lazy query evaluation. When Query#execute() is called, the
		/// query processor only chooses the best index and creates an iterator on
		/// this index. Indexes and constraints are evaluated lazily when the
		/// application iterates through the
		/// <see cref="ObjectSet">ObjectSet</see>
		/// resultset of the query.
		/// <br /><br /><b>For detailed documentation please see
		/// <see cref="com.db4o.config.QueryConfiguration.EvaluationMode">com.db4o.config.QueryConfiguration.EvaluationMode
		/// 	</see>
		/// </b>
		/// </remarks>
		public static readonly com.db4o.config.QueryEvaluationMode LAZY = new com.db4o.config.QueryEvaluationMode
			();

		private static readonly com.db4o.config.QueryEvaluationMode[] MODES = new com.db4o.config.QueryEvaluationMode
			[] { com.db4o.config.QueryEvaluationMode.IMMEDIATE, com.db4o.config.QueryEvaluationMode
			.SNAPSHOT, com.db4o.config.QueryEvaluationMode.LAZY };

		/// <summary>internal method, ignore please.</summary>
		/// <remarks>internal method, ignore please.</remarks>
		public virtual int AsInt()
		{
			for (int i = 0; i < MODES.Length; i++)
			{
				if (MODES[i] == this)
				{
					return i;
				}
			}
			throw new System.InvalidOperationException();
		}

		/// <summary>internal method, ignore please.</summary>
		/// <remarks>internal method, ignore please.</remarks>
		public static com.db4o.config.QueryEvaluationMode FromInt(int i)
		{
			return MODES[i];
		}
	}
}
