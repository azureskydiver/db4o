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
		/// <seealso cref="com.db4o.config.Configuration.activationDepth">Why activation?</seealso>
		/// <seealso cref="com.db4o.config.ObjectClass.cascadeOnActivate">com.db4o.config.ObjectClass.cascadeOnActivate
		/// 	</seealso>
		/// <seealso cref="com.db4o.ObjectContainer.activate">com.db4o.ObjectContainer.activate
		/// 	</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		void cascadeOnActivate(bool flag);

		/// <summary>sets cascaded delete behaviour.</summary>
		/// <remarks>
		/// sets cascaded delete behaviour.
		/// <br /><br />
		/// Setting cascadeOnDelete to true will result in the deletion of
		/// the object attribute stored in this field on the parent object
		/// if the parent object is passed to
		/// <see cref="com.db4o.ObjectContainer.delete">ObjectContainer#delete()</see>
		/// .
		/// <br /><br />
		/// <b>Caution !</b><br />
		/// This setting will also trigger deletion of the old member object, on
		/// calls to
		/// <see cref="com.db4o.ObjectContainer.set">ObjectContainer#set()</see>
		/// .
		/// An example of the behaviour can be found in
		/// <see cref="com.db4o.config.ObjectClass.cascadeOnDelete">ObjectClass#cascadeOnDelete()
		/// 	</see>
		/// <br /><br />
		/// The default setting is <b>false</b>.<br /><br />
		/// </remarks>
		/// <param name="flag">whether deletes are to be cascaded to the member object.</param>
		/// <seealso cref="com.db4o.config.ObjectClass.cascadeOnDelete">com.db4o.config.ObjectClass.cascadeOnDelete
		/// 	</seealso>
		/// <seealso cref="com.db4o.ObjectContainer.delete">com.db4o.ObjectContainer.delete</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		void cascadeOnDelete(bool flag);

		/// <summary>sets cascaded update behaviour.</summary>
		/// <remarks>
		/// sets cascaded update behaviour.
		/// <br /><br />
		/// Setting cascadeOnUpdate to true will result in the update
		/// of the object attribute stored in this field if the parent object
		/// is passed to
		/// <see cref="com.db4o.ObjectContainer.set">ObjectContainer#set()</see>
		/// .
		/// <br /><br />
		/// The default setting is <b>false</b>.<br /><br />
		/// </remarks>
		/// <param name="flag">whether updates are to be cascaded to the member object.</param>
		/// <seealso cref="com.db4o.ObjectContainer.set">com.db4o.ObjectContainer.set</seealso>
		/// <seealso cref="com.db4o.config.ObjectClass.cascadeOnUpdate">com.db4o.config.ObjectClass.cascadeOnUpdate
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.ObjectClass.updateDepth">com.db4o.config.ObjectClass.updateDepth
		/// 	</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		void cascadeOnUpdate(bool flag);

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
		void indexed(bool flag);

		/// <summary>renames a field of a stored class.</summary>
		/// <remarks>
		/// renames a field of a stored class.
		/// <br /><br />Use this method to refactor classes.
		/// <br /><br /><b>Examples: ../com/db4o/samples/rename.</b><br /><br />
		/// </remarks>
		/// <param name="newName">the new fieldname.</param>
		void rename(string newName);

		/// <summary>toggles query evaluation.</summary>
		/// <remarks>
		/// toggles query evaluation.
		/// <br /><br />All fields are evaluated by default. Use this method to turn query
		/// evaluation of for specific fields.<br /><br />
		/// </remarks>
		/// <param name="flag">specify <code>false</code> to ignore this field during query evaluation.
		/// 	</param>
		void queryEvaluation(bool flag);
	}
}
