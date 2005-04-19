/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
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
		/// <see cref="com.db4o.config.Configuration.callConstructors">com.db4o.config.Configuration.callConstructors
		/// 	</see>
		/// .<br /><br />
		/// </remarks>
		/// <param name="flag">
		/// - specify true, to request calling constructors, specify
		/// false to request <b>not</b> calling constructors.
		/// </param>
		/// <seealso cref="com.db4o.config.Configuration.callConstructors">com.db4o.config.Configuration.callConstructors
		/// 	</seealso>
		void callConstructor(bool flag);

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
		/// <seealso cref="com.db4o.config.ObjectField.cascadeOnActivate">com.db4o.config.ObjectField.cascadeOnActivate
		/// 	</seealso>
		/// <seealso cref="com.db4o.ObjectContainer.activate">com.db4o.ObjectContainer.activate
		/// 	</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		/// <seealso cref="com.db4o.config.Configuration.activationDepth">Why activation?</seealso>
		void cascadeOnActivate(bool flag);

		/// <summary>sets cascaded delete behaviour.</summary>
		/// <remarks>
		/// sets cascaded delete behaviour.
		/// <br /><br />
		/// Setting cascadeOnDelete to true will result in the deletion of
		/// all member objects of instances of this class, if they are
		/// passed to
		/// <see cref="com.db4o.ObjectContainer.delete">com.db4o.ObjectContainer.delete</see>
		/// .
		/// <br /><br />
		/// <b>Caution !</b><br />
		/// This setting will also trigger deletion of old member objects, on
		/// calls to
		/// <see cref="com.db4o.ObjectContainer.set">com.db4o.ObjectContainer.set</see>
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
		/// <seealso cref="com.db4o.config.ObjectField.cascadeOnDelete">com.db4o.config.ObjectField.cascadeOnDelete
		/// 	</seealso>
		/// <seealso cref="com.db4o.ObjectContainer.delete">com.db4o.ObjectContainer.delete</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		void cascadeOnDelete(bool flag);

		/// <summary>sets cascaded update behaviour.</summary>
		/// <remarks>
		/// sets cascaded update behaviour.
		/// <br /><br />
		/// Setting cascadeOnUpdate to true will result in the update
		/// of all member objects if a stored instance of this class is passed
		/// to
		/// <see cref="com.db4o.ObjectContainer.set">com.db4o.ObjectContainer.set</see>
		/// .<br /><br />
		/// The default setting is <b>false</b>.<br /><br />
		/// </remarks>
		/// <param name="flag">whether updates are to be cascaded to member objects.</param>
		/// <seealso cref="com.db4o.config.ObjectField.cascadeOnUpdate">com.db4o.config.ObjectField.cascadeOnUpdate
		/// 	</seealso>
		/// <seealso cref="com.db4o.ObjectContainer.set">com.db4o.ObjectContainer.set</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		void cascadeOnUpdate(bool flag);

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
		void compare(com.db4o.config.ObjectAttribute attributeProvider);

		/// <summary>generate UUIDs for stored objects of this class.</summary>
		/// <remarks>generate UUIDs for stored objects of this class.</remarks>
		/// <param name="setting"></param>
		void generateUUIDs(bool setting);

		/// <summary>generate version numbers for stored objects of this class.</summary>
		/// <remarks>generate version numbers for stored objects of this class.</remarks>
		/// <param name="setting"></param>
		void generateVersionNumbers(bool setting);

		/// <summary>sets the maximum activation depth to the desired value.</summary>
		/// <remarks>
		/// sets the maximum activation depth to the desired value.
		/// <br /><br />A class specific setting overrides the
		/// <see cref="com.db4o.config.Configuration.activationDepth">global setting</see>
		/// <br /><br />
		/// </remarks>
		/// <param name="depth">the desired maximum activation depth</param>
		/// <seealso cref="com.db4o.config.Configuration.activationDepth">Why activation?</seealso>
		/// <seealso cref="com.db4o.config.ObjectClass.cascadeOnActivate">com.db4o.config.ObjectClass.cascadeOnActivate
		/// 	</seealso>
		void maximumActivationDepth(int depth);

		/// <summary>sets the minimum activation depth to the desired value.</summary>
		/// <remarks>
		/// sets the minimum activation depth to the desired value.
		/// <br /><br />A class specific setting overrides the
		/// <see cref="com.db4o.config.Configuration.activationDepth">global setting</see>
		/// <br /><br />
		/// </remarks>
		/// <param name="depth">the desired minimum activation depth</param>
		/// <seealso cref="com.db4o.config.Configuration.activationDepth">Why activation?</seealso>
		/// <seealso cref="com.db4o.config.ObjectClass.cascadeOnActivate">com.db4o.config.ObjectClass.cascadeOnActivate
		/// 	</seealso>
		void minimumActivationDepth(int depth);

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
		com.db4o.config.ObjectField objectField(string fieldName);

		/// <summary>turns on storing static field values for this class.</summary>
		/// <remarks>
		/// turns on storing static field values for this class.
		/// <br /><br />By default, static field values of classes are not stored
		/// to the database file. By turning the setting on for a specific class
		/// with this switch, all <b>non-simple-typed</b> static field values of this
		/// class are stored the first time an object of the class is stored, and
		/// restored, every time a database file is opened afterwards.
		/// <br /><br />The setting will be ignored for simple types.
		/// <br /><br />Use this setting for constant static object members.
		/// <br /><br />This option will slow down the process of opening database
		/// files and the stored objects will occupy space in the database file.
		/// </remarks>
		void persistStaticFieldValues();

		/// <summary>renames a stored class.</summary>
		/// <remarks>
		/// renames a stored class.
		/// <br /><br />Use this method to refactor classes.
		/// <br /><br /><b>Examples: ../com/db4o/samples/rename.</b><br /><br />
		/// </remarks>
		/// <param name="newName">the new fully qualified classname.</param>
		void rename(string newName);

		/// <summary>allows to specify if transient fields are to be stored.</summary>
		/// <remarks>
		/// allows to specify if transient fields are to be stored.
		/// <br />The default for every class is <code>false</code>.<br /><br />
		/// </remarks>
		/// <param name="flag">whether or not transient fields are to be stored.</param>
		void storeTransientFields(bool flag);

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
		void translate(com.db4o.config.ObjectTranslator translator);

		/// <summary>specifies the updateDepth for this class.</summary>
		/// <remarks>
		/// specifies the updateDepth for this class.
		/// <br /><br />see the documentation of
		/// <see cref="com.db4o.ObjectContainer.set">com.db4o.ObjectContainer.set</see>
		/// for further details.<br /><br />
		/// The default setting is 0: Only the object passed to
		/// <see cref="com.db4o.ObjectContainer.set">com.db4o.ObjectContainer.set</see>
		/// will be updated.<br /><br />
		/// </remarks>
		/// <param name="depth">the depth of the desired update for this class.</param>
		/// <seealso cref="com.db4o.config.Configuration.updateDepth">com.db4o.config.Configuration.updateDepth
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.ObjectClass.cascadeOnUpdate">com.db4o.config.ObjectClass.cascadeOnUpdate
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.ObjectField.cascadeOnUpdate">com.db4o.config.ObjectField.cascadeOnUpdate
		/// 	</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		void updateDepth(int depth);
	}
}
