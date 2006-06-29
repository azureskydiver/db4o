namespace com.db4o.config
{
	/// <summary>configuration interface for fields of classes.</summary>
	/// <remarks>
	/// configuration interface for fields of classes.
	/// <br /><br /><b>Examples: ../com/db4o/samples/translators.</b><br /><br />
	/// Use the global Configuration object to configure db4o before opening an
	/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
	/// .<br /><br />
	/// <b>Example:</b><br />
	/// <code>
	/// Configuration config = Db4o.configure();<br />
	/// ObjectClass oc = config.objectClass("package.className");<br />
	/// ObjectField of = oc.objectField("fieldName");
	/// of.rename("newFieldName");
	/// of.queryEvaluation(false);
	/// </code>
	/// </remarks>
	public interface ObjectField
	{
		/// <summary>sets cascaded activation behaviour.</summary>
		/// <remarks>
		/// sets cascaded activation behaviour.
		/// <br /><br />
		/// Setting cascadeOnActivate to true will result in the activation
		/// of the object attribute stored in this field if the parent object
		/// is activated.
		/// <br /><br />
		/// The default setting is <b>false</b>.<br /><br />
		/// </remarks>
		/// <param name="flag">whether activation is to be cascaded to the member object.</param>
		/// <seealso cref="com.db4o.config.Configuration.ActivationDepth">Why activation?</seealso>
		/// <seealso cref="com.db4o.config.ObjectClass.CascadeOnActivate">com.db4o.config.ObjectClass.CascadeOnActivate
		/// 	</seealso>
		/// <seealso cref="com.db4o.ObjectContainer.Activate">com.db4o.ObjectContainer.Activate
		/// 	</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		void CascadeOnActivate(bool flag);

		/// <summary>sets cascaded delete behaviour.</summary>
		/// <remarks>
		/// sets cascaded delete behaviour.
		/// <br /><br />
		/// Setting cascadeOnDelete to true will result in the deletion of
		/// the object attribute stored in this field on the parent object
		/// if the parent object is passed to
		/// <see cref="com.db4o.ObjectContainer.Delete">ObjectContainer#delete()</see>
		/// .
		/// <br /><br />
		/// <b>Caution !</b><br />
		/// This setting will also trigger deletion of the old member object, on
		/// calls to
		/// <see cref="com.db4o.ObjectContainer.Set">ObjectContainer#set()</see>
		/// .
		/// An example of the behaviour can be found in
		/// <see cref="com.db4o.config.ObjectClass.CascadeOnDelete">ObjectClass#cascadeOnDelete()
		/// 	</see>
		/// <br /><br />
		/// The default setting is <b>false</b>.<br /><br />
		/// </remarks>
		/// <param name="flag">whether deletes are to be cascaded to the member object.</param>
		/// <seealso cref="com.db4o.config.ObjectClass.CascadeOnDelete">com.db4o.config.ObjectClass.CascadeOnDelete
		/// 	</seealso>
		/// <seealso cref="com.db4o.ObjectContainer.Delete">com.db4o.ObjectContainer.Delete</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		void CascadeOnDelete(bool flag);

		/// <summary>sets cascaded update behaviour.</summary>
		/// <remarks>
		/// sets cascaded update behaviour.
		/// <br /><br />
		/// Setting cascadeOnUpdate to true will result in the update
		/// of the object attribute stored in this field if the parent object
		/// is passed to
		/// <see cref="com.db4o.ObjectContainer.Set">ObjectContainer#set()</see>
		/// .
		/// <br /><br />
		/// The default setting is <b>false</b>.<br /><br />
		/// </remarks>
		/// <param name="flag">whether updates are to be cascaded to the member object.</param>
		/// <seealso cref="com.db4o.ObjectContainer.Set">com.db4o.ObjectContainer.Set</seealso>
		/// <seealso cref="com.db4o.config.ObjectClass.CascadeOnUpdate">com.db4o.config.ObjectClass.CascadeOnUpdate
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.ObjectClass.UpdateDepth">com.db4o.config.ObjectClass.UpdateDepth
		/// 	</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		void CascadeOnUpdate(bool flag);

		/// <summary>turns indexing on or off.</summary>
		/// <remarks>
		/// turns indexing on or off.
		/// <br /><br />Field indices dramatically improve query performance but they may
		/// considerably reduce storage and update performance.<br />The best benchmark whether
		/// or not an index on a field achieves the desired result is the completed application
		/// - with a data load that is typical for it's use.<br /><br />This configuration setting
		/// is only checked when the
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// is opened. If the
		/// setting is set to <code>true</code> and an index does not exist, the index will be
		/// created. If the setting is set to <code>false</code> and an index does exist the
		/// index will be dropped.<br /><br />
		/// </remarks>
		/// <param name="flag">
		/// specify <code>true</code> or <code>false</code> to turn indexing on for
		/// this field
		/// </param>
		void Indexed(bool flag);

		/// <summary>renames a field of a stored class.</summary>
		/// <remarks>
		/// renames a field of a stored class.
		/// <br /><br />Use this method to refactor classes.
		/// <br /><br /><b>Examples: ../com/db4o/samples/rename.</b><br /><br />
		/// </remarks>
		/// <param name="newName">the new fieldname.</param>
		void Rename(string newName);

		/// <summary>toggles query evaluation.</summary>
		/// <remarks>
		/// toggles query evaluation.
		/// <br /><br />All fields are evaluated by default. Use this method to turn query
		/// evaluation of for specific fields.<br /><br />
		/// </remarks>
		/// <param name="flag">specify <code>false</code> to ignore this field during query evaluation.
		/// 	</param>
		void QueryEvaluation(bool flag);
	}
}
