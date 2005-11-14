namespace com.db4o.ext
{
	/// <summary>
	/// extended functionality for the
	/// <see cref="com.db4o.ObjectSet">ObjectSet</see>
	/// interface.
	/// <br /><br />Every db4o
	/// <see cref="com.db4o.ObjectSet">ObjectSet</see>
	/// always is an ExtObjectSet so a cast is possible.<br /><br />
	/// <see cref="com.db4o.ObjectSet.ext">com.db4o.ObjectSet.ext</see>
	/// is a convenient method to perform the cast.<br /><br />
	/// The ObjectSet functionality is split to two interfaces to allow newcomers to
	/// focus on the essential methods.
	/// </summary>
	public interface ExtObjectSet : com.db4o.ObjectSet
	{
		/// <summary>returns an array of internal IDs that correspond to the contained objects.
		/// 	</summary>
		/// <remarks>
		/// returns an array of internal IDs that correspond to the contained objects.
		/// <br /><br />
		/// </remarks>
		/// <seealso cref="com.db4o.ext.ExtObjectContainer.getID">com.db4o.ext.ExtObjectContainer.getID
		/// 	</seealso>
		/// <seealso cref="com.db4o.ext.ExtObjectContainer.getByID">com.db4o.ext.ExtObjectContainer.getByID
		/// 	</seealso>
		long[] getIDs();

		/// <summary>returns the item at position [index] in this ObjectSet.</summary>
		/// <remarks>
		/// returns the item at position [index] in this ObjectSet.
		/// <br /><br />
		/// The object will be activated.
		/// </remarks>
		/// <param name="index">the index position in this ObjectSet.</param>
		/// <returns>the activated object.</returns>
		object get(int index);
	}
}
