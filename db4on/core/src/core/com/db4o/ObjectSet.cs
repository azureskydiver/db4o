namespace com.db4o
{
	/// <summary>query resultset.</summary>
	/// <remarks>
	/// query resultset.
	/// <br /><br />The <code>ObjectSet</code> class serves as a cursor to
	/// iterate through a set of objects retrieved by a
	/// call to
	/// <see cref="com.db4o.ObjectContainer.get">ObjectContainer.get(template)</see>
	/// .
	/// <br /><br />An <code>ObjectSet</code> can easily be wrapped to a
	/// <code>java.util.List</code> (Java)  / <code>System.Collections.IList</code>  (.NET)
	/// using the source code supplied in ../com/db4o/wrap/
	/// <br /><br />Note that the used
	/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
	/// needs to remain opened during the
	/// use of an <code>ObjectSet</code> to allow lazy instantiation.
	/// </remarks>
	/// <seealso cref="com.db4o.ext.ExtObjectSet">for extended functionality.</seealso>
	public interface ObjectSet : System.Collections.IList
	{
		/// <summary>returns an ObjectSet with extended functionality.</summary>
		/// <remarks>
		/// returns an ObjectSet with extended functionality.
		/// <br /><br />Every ObjectSet that db4o provides can be casted to
		/// an ExtObjectSet. This method is supplied for your convenience
		/// to work without a cast.
		/// <br /><br />The ObjectSet functionality is split to two interfaces
		/// to allow newcomers to focus on the essential methods.
		/// </remarks>
		com.db4o.ext.ExtObjectSet ext();

		/// <summary>returns <code>true</code> if the <code>ObjectSet</code> has more elements.
		/// 	</summary>
		/// <remarks>returns <code>true</code> if the <code>ObjectSet</code> has more elements.
		/// 	</remarks>
		/// <returns>
		/// boolean - <code>true</code> if the <code>ObjectSet</code> has more
		/// elements.
		/// </returns>
		bool hasNext();

		/// <summary>returns the next object in the <code>ObjectSet</code>.</summary>
		/// <remarks>
		/// returns the next object in the <code>ObjectSet</code>.
		/// <br /><br />
		/// Before returning the Object, next() triggers automatic activation of the
		/// Object with the respective
		/// <see cref="com.db4o.config.Configuration.activationDepth">global</see>
		/// or
		/// <see cref="com.db4o.config.ObjectClass.maximumActivationDepth">class specific</see>
		/// setting.<br /><br />
		/// </remarks>
		/// <returns>the next object in the <code>ObjectSet</code>.</returns>
		object next();

		/// <summary>resets the <code>ObjectSet</code> cursor before the first element.</summary>
		/// <remarks>
		/// resets the <code>ObjectSet</code> cursor before the first element.
		/// <br /><br />A subsequent call to <code>next()</code> will return the first element.
		/// </remarks>
		void reset();

		/// <summary>returns the number of elements in the <code>ObjectSet</code>.</summary>
		/// <remarks>returns the number of elements in the <code>ObjectSet</code>.</remarks>
		/// <returns>the number of elements in the <code>ObjectSet</code>.</returns>
		int size();
	}
}
