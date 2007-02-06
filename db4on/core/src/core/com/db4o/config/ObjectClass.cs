namespace com.db4o.config
{
	/// <summary>configuration interface for classes.</summary>
	/// <remarks>
	/// configuration interface for classes.
	/// <br /><br /><b>Examples: ../com/db4o/samples/translators/Default.java.</b><br /><br />
	/// Use the global Configuration object to configure db4o before opening an
	/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
	/// .<br /><br />
	/// <b>Example:</b><br />
	/// <code>
	/// Configuration config = Db4o.configure();<br />
	/// ObjectClass oc = config.objectClass("package.className");<br />
	/// oc.updateDepth(3);<br />
	/// oc.minimumActivationDepth(3);<br />
	/// </code>
	/// </remarks>
	public interface ObjectClass
	{
		/// <summary>
		/// advises db4o to try instantiating objects of this class with/without
		/// calling constructors.
		/// </summary>
		/// <remarks>
		/// advises db4o to try instantiating objects of this class with/without
		/// calling constructors.
		/// <br /><br />
		/// Not all JDKs / .NET-environments support this feature. db4o will
		/// attempt, to follow the setting as good as the enviroment supports.
		/// In doing so, it may call implementation-specific features like
		/// sun.reflect.ReflectionFactory#newConstructorForSerialization on the
		/// Sun Java 1.4.x/5 VM (not available on other VMs) and
		/// FormatterServices.GetUninitializedObject() on
		/// the .NET framework (not available on CompactFramework).<br /><br />
		/// This setting may also be set globally for all classes in
		/// <see cref="com.db4o.config.Configuration.CallConstructors">com.db4o.config.Configuration.CallConstructors
		/// 	</see>
		/// .<br /><br />
		/// </remarks>
		/// <param name="flag">
		/// - specify true, to request calling constructors, specify
		/// false to request <b>not</b> calling constructors.
		/// </param>
		/// <seealso cref="com.db4o.config.Configuration.CallConstructors">com.db4o.config.Configuration.CallConstructors
		/// 	</seealso>
		void CallConstructor(bool flag);

		/// <summary>sets cascaded activation behaviour.</summary>
		/// <remarks>
		/// sets cascaded activation behaviour.
		/// <br /><br />
		/// Setting cascadeOnActivate to true will result in the activation
		/// of all member objects if an instance of this class is activated.
		/// <br /><br />
		/// The default setting is <b>false</b>.<br /><br />
		/// </remarks>
		/// <param name="flag">whether activation is to be cascaded to member objects.</param>
		/// <seealso cref="com.db4o.config.ObjectField.CascadeOnActivate">com.db4o.config.ObjectField.CascadeOnActivate
		/// 	</seealso>
		/// <seealso cref="com.db4o.ObjectContainer.Activate">com.db4o.ObjectContainer.Activate
		/// 	</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		/// <seealso cref="com.db4o.config.Configuration.ActivationDepth">Why activation?</seealso>
		void CascadeOnActivate(bool flag);

		/// <summary>sets cascaded delete behaviour.</summary>
		/// <remarks>
		/// sets cascaded delete behaviour.
		/// <br /><br />
		/// Setting cascadeOnDelete to true will result in the deletion of
		/// all member objects of instances of this class, if they are
		/// passed to
		/// <see cref="com.db4o.ObjectContainer.Delete">com.db4o.ObjectContainer.Delete</see>
		/// .
		/// <br /><br />
		/// <b>Caution !</b><br />
		/// This setting will also trigger deletion of old member objects, on
		/// calls to
		/// <see cref="com.db4o.ObjectContainer.Set">com.db4o.ObjectContainer.Set</see>
		/// .<br /><br />
		/// An example of the behaviour:<br />
		/// <code>
		/// ObjectContainer con;<br />
		/// Bar bar1 = new Bar();<br />
		/// Bar bar2 = new Bar();<br />
		/// foo.bar = bar1;<br />
		/// con.set(foo);  // bar1 is stored as a member of foo<br />
		/// foo.bar = bar2;<br />
		/// con.set(foo);  // bar2 is stored as a member of foo
		/// </code><br />The last statement will <b>also</b> delete bar1 from the
		/// ObjectContainer, no matter how many other stored objects hold references
		/// to bar1.
		/// <br /><br />
		/// The default setting is <b>false</b>.<br /><br />
		/// </remarks>
		/// <param name="flag">whether deletes are to be cascaded to member objects.</param>
		/// <seealso cref="com.db4o.config.ObjectField.CascadeOnDelete">com.db4o.config.ObjectField.CascadeOnDelete
		/// 	</seealso>
		/// <seealso cref="com.db4o.ObjectContainer.Delete">com.db4o.ObjectContainer.Delete</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		void CascadeOnDelete(bool flag);

		/// <summary>sets cascaded update behaviour.</summary>
		/// <remarks>
		/// sets cascaded update behaviour.
		/// <br /><br />
		/// Setting cascadeOnUpdate to true will result in the update
		/// of all member objects if a stored instance of this class is passed
		/// to
		/// <see cref="com.db4o.ObjectContainer.Set">com.db4o.ObjectContainer.Set</see>
		/// .<br /><br />
		/// The default setting is <b>false</b>.<br /><br />
		/// </remarks>
		/// <param name="flag">whether updates are to be cascaded to member objects.</param>
		/// <seealso cref="com.db4o.config.ObjectField.CascadeOnUpdate">com.db4o.config.ObjectField.CascadeOnUpdate
		/// 	</seealso>
		/// <seealso cref="com.db4o.ObjectContainer.Set">com.db4o.ObjectContainer.Set</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		void CascadeOnUpdate(bool flag);

		/// <summary>registers an attribute provider for special query behavior.</summary>
		/// <remarks>
		/// registers an attribute provider for special query behavior.
		/// <br /><br />The query processor will compare the object returned by the
		/// attribute provider instead of the actual object, both for the constraint
		/// and the candidate persistent object.<br /><br /> Preinstalled attribute
		/// providers are documented
		/// in the sourcecode of
		/// com.db4o.samples.translators.Default.java#defaultConfiguration().<br /><br />
		/// </remarks>
		/// <param name="attributeProvider">the attribute provider to be used</param>
		void Compare(com.db4o.config.ObjectAttribute attributeProvider);

		/// <summary>
		/// Must be called before databases are created or opened
		/// so that db4o will control versions and generate UUIDs
		/// for objects of this class, which is required for using replication.
		/// </summary>
		/// <remarks>
		/// Must be called before databases are created or opened
		/// so that db4o will control versions and generate UUIDs
		/// for objects of this class, which is required for using replication.
		/// </remarks>
		/// <param name="setting"></param>
		void EnableReplication(bool setting);

		/// <summary>generate UUIDs for stored objects of this class.</summary>
		/// <remarks>generate UUIDs for stored objects of this class.</remarks>
		/// <param name="setting"></param>
		void GenerateUUIDs(bool setting);

		/// <summary>generate version numbers for stored objects of this class.</summary>
		/// <remarks>generate version numbers for stored objects of this class.</remarks>
		/// <param name="setting"></param>
		void GenerateVersionNumbers(bool setting);

		/// <summary>turns the class index on or off.</summary>
		/// <remarks>
		/// turns the class index on or off.
		/// <br /><br />db4o maintains an index for each class to be able to
		/// deliver all instances of a class in a query. If the class
		/// index is never needed, it can be turned off with this method
		/// to improve the performance to create and delete objects of
		/// a class.
		/// <br /><br />Common cases where a class index is not needed:<br />
		/// - The application always works with subclasses or superclasses.<br />
		/// - There are convenient field indexes that will always find instances
		/// of a class.<br />
		/// - The application always works with IDs.
		/// </remarks>
		void Indexed(bool flag);

		/// <summary>sets the maximum activation depth to the desired value.</summary>
		/// <remarks>
		/// sets the maximum activation depth to the desired value.
		/// <br /><br />A class specific setting overrides the
		/// <see cref="com.db4o.config.Configuration.ActivationDepth">global setting</see>
		/// <br /><br />
		/// </remarks>
		/// <param name="depth">the desired maximum activation depth</param>
		/// <seealso cref="com.db4o.config.Configuration.ActivationDepth">Why activation?</seealso>
		/// <seealso cref="com.db4o.config.ObjectClass.CascadeOnActivate">com.db4o.config.ObjectClass.CascadeOnActivate
		/// 	</seealso>
		void MaximumActivationDepth(int depth);

		/// <summary>sets the minimum activation depth to the desired value.</summary>
		/// <remarks>
		/// sets the minimum activation depth to the desired value.
		/// <br /><br />A class specific setting overrides the
		/// <see cref="com.db4o.config.Configuration.ActivationDepth">global setting</see>
		/// <br /><br />
		/// </remarks>
		/// <param name="depth">the desired minimum activation depth</param>
		/// <seealso cref="com.db4o.config.Configuration.ActivationDepth">Why activation?</seealso>
		/// <seealso cref="com.db4o.config.ObjectClass.CascadeOnActivate">com.db4o.config.ObjectClass.CascadeOnActivate
		/// 	</seealso>
		void MinimumActivationDepth(int depth);

		/// <summary>
		/// returns an
		/// <see cref="com.db4o.config.ObjectField">ObjectField</see>
		/// object
		/// to configure the specified field.
		/// <br /><br />
		/// </summary>
		/// <param name="fieldName">the fieldname of the field to be configured.<br /><br /></param>
		/// <returns>
		/// an instance of an
		/// <see cref="com.db4o.config.ObjectField">ObjectField</see>
		/// object for configuration.
		/// </returns>
		com.db4o.config.ObjectField ObjectField(string fieldName);

		/// <summary>turns on storing static field values for this class.</summary>
		/// <remarks>
		/// turns on storing static field values for this class.
		/// <br /><br />By default, static field values of classes are not stored
		/// to the database file. By turning the setting on for a specific class
		/// with this switch, all <b>non-simple-typed</b> static field values of this
		/// class are stored the first time an object of the class is stored, and
		/// restored, every time a database file is opened afterwards, <b>after
		/// class meta information is loaded for this class</b> (which can happen
		/// by querying for a class or by loading an instance of a class).<br /><br />
		/// To update a static field value, once it is stored, you have to the following
		/// in this order:<br />
		/// (1) open the database file you are working agains<br />
		/// (2) make sure the class metadata is loaded<br />
		/// <code>objectContainer.query().constrain(Foo.class); // Java</code><br />
		/// <code>objectContainer.Query().Constrain(typeof(Foo)); // C#</code><br />
		/// (3) change the static member<br />
		/// (4) store the static member explicitely<br />
		/// <code>objectContainer.set(Foo.staticMember); // C#</code>
		/// <br /><br />The setting will be ignored for simple types.
		/// <br /><br />Use this setting for constant static object members.
		/// <br /><br />This option will slow down the process of opening database
		/// files and the stored objects will occupy space in the database file.
		/// </remarks>
		void PersistStaticFieldValues();

		/// <summary>creates a temporary mapping of a persistent class to a different class.</summary>
		/// <remarks>
		/// creates a temporary mapping of a persistent class to a different class.
		/// <br /><br />If meta information for this ObjectClass has been stored to
		/// the database file, it will be read from the database file as if it
		/// was representing the class specified by the clazz parameter passed to
		/// this method.
		/// The clazz parameter can be any of the following:<br />
		/// - a fully qualified classname as a String.<br />
		/// - a Class object.<br />
		/// - any other object to be used as a template.<br /><br />
		/// This method will be ignored if the database file already contains meta
		/// information for clazz.
		/// </remarks>
		/// <param name="clazz">class name, Class object, or example object.<br /><br /></param>
		void ReadAs(object clazz);

		/// <summary>renames a stored class.</summary>
		/// <remarks>
		/// renames a stored class.
		/// <br /><br />Use this method to refactor classes.
		/// <br /><br /><b>Examples: ../com/db4o/samples/rename.</b><br /><br />
		/// </remarks>
		/// <param name="newName">the new fully qualified classname.</param>
		void Rename(string newName);

		/// <summary>allows to specify if transient fields are to be stored.</summary>
		/// <remarks>
		/// allows to specify if transient fields are to be stored.
		/// <br />The default for every class is <code>false</code>.<br /><br />
		/// </remarks>
		/// <param name="flag">whether or not transient fields are to be stored.</param>
		void StoreTransientFields(bool flag);

		/// <summary>registers a translator for this class.</summary>
		/// <remarks>
		/// registers a translator for this class.
		/// <br /><br />
		/// Preinstalled translators are documented in the sourcecode of
		/// com.db4o.samples.translators.Default.java#defaultConfiguration().
		/// <br /><br />Example translators can also be found in this folder.<br /><br />
		/// </remarks>
		/// <param name="translator">
		/// this may be an
		/// <see cref="com.db4o.config.ObjectTranslator">ObjectTranslator</see>
		/// or an
		/// <see cref="com.db4o.config.ObjectConstructor">ObjectConstructor</see>
		/// </param>
		/// <seealso cref="com.db4o.config.ObjectTranslator">com.db4o.config.ObjectTranslator
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.ObjectConstructor">com.db4o.config.ObjectConstructor
		/// 	</seealso>
		void Translate(com.db4o.config.ObjectTranslator translator);

		/// <summary>specifies the updateDepth for this class.</summary>
		/// <remarks>
		/// specifies the updateDepth for this class.
		/// <br /><br />see the documentation of
		/// <see cref="com.db4o.ObjectContainer.Set">com.db4o.ObjectContainer.Set</see>
		/// for further details.<br /><br />
		/// The default setting is 0: Only the object passed to
		/// <see cref="com.db4o.ObjectContainer.Set">com.db4o.ObjectContainer.Set</see>
		/// will be updated.<br /><br />
		/// </remarks>
		/// <param name="depth">the depth of the desired update for this class.</param>
		/// <seealso cref="com.db4o.config.Configuration.UpdateDepth">com.db4o.config.Configuration.UpdateDepth
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.ObjectClass.CascadeOnUpdate">com.db4o.config.ObjectClass.CascadeOnUpdate
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.ObjectField.CascadeOnUpdate">com.db4o.config.ObjectField.CascadeOnUpdate
		/// 	</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		void UpdateDepth(int depth);
	}
}
