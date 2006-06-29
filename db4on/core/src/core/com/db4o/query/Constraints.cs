namespace com.db4o.query
{
	/// <summary>
	/// set of
	/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
	/// objects.
	/// <br /><br />This extension of the
	/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
	/// interface allows
	/// setting the evaluation mode of all contained
	/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
	/// objects with single calls.
	/// <br /><br />
	/// See also
	/// <see cref="com.db4o.query.Query.Constraints">com.db4o.query.Query.Constraints</see>
	/// .
	/// </summary>
	public interface Constraints : com.db4o.query.Constraint
	{
		/// <summary>
		/// returns an array of the contained
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// objects.
		/// </summary>
		/// <returns>
		/// an array of the contained
		/// <see cref="com.db4o.query.Constraint">com.db4o.query.Constraint</see>
		/// objects.
		/// </returns>
		com.db4o.query.Constraint[] ToArray();
	}
}
