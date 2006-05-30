namespace com.db4o.config
{
	/// <summary>translator interface to translate objects on storage and activation.</summary>
	/// <remarks>
	/// translator interface to translate objects on storage and activation.
	/// <br /><br /><b>Examples: ../com/db4o/samples/translators.</b><br /><br />
	/// By writing classes that implement this interface, it is possible to
	/// define how application classes are to be converted to be stored more efficiently.
	/// <br /><br />
	/// Before starting a db4o session, translator classes need to be registered. An example:<br />
	/// <code>
	/// Configuration config = Db4o.configure();<br />
	/// ObjectClass oc = config.objectClass("package.className");<br />
	/// oc.translate(new FooTranslator());</code><br /><br />
	/// </remarks>
	public interface ObjectTranslator
	{
		/// <summary>db4o calls this method during storage and query evaluation.</summary>
		/// <remarks>db4o calls this method during storage and query evaluation.</remarks>
		/// <param name="container">the ObjectContainer used</param>
		/// <param name="applicationObject">the Object to be translated</param>
		/// <returns>
		/// return the object to store.<br />It needs to be of the class
		/// <see cref="com.db4o.config.ObjectTranslator.StoredClass">storedClass()</see>
		/// .
		/// </returns>
		object OnStore(com.db4o.ObjectContainer container, object applicationObject);

		/// <summary>db4o calls this method during activation.</summary>
		/// <remarks>db4o calls this method during activation.</remarks>
		/// <param name="container">the ObjectContainer used</param>
		/// <param name="applicationObject">the object to set the members on</param>
		/// <param name="storedObject">the object that was stored</param>
		void OnActivate(com.db4o.ObjectContainer container, object applicationObject, object
			 storedObject);

		/// <summary>return the Class you are converting to.</summary>
		/// <remarks>return the Class you are converting to.</remarks>
		/// <returns>
		/// the Class of the object you are returning with the method
		/// <see cref="com.db4o.config.ObjectTranslator.OnStore">onStore()</see>
		/// </returns>
		j4o.lang.Class StoredClass();
	}
}
