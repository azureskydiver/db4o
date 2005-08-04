namespace com.db4o
{
	/// <summary>storage and query interface.</summary>
	/// <remarks>
	/// storage and query interface.
	/// <br /><br />The <code>ObjectContainer</code> interface provides methods
	/// to store, retrieve and delete objects and to commit and rollback
	/// transactions.
	/// </remarks>
	/// <seealso cref="com.db4o.ext.ExtObjectContainer">ExtObjectContainer for extended functionality.
	/// 	</seealso>
	public interface ObjectContainer
	{
		/// <summary>activates all members on a stored object to the specified depth.</summary>
		/// <remarks>
		/// activates all members on a stored object to the specified depth.
		/// <br /><br /><b>Examples: ../com/db4o/samples/activate.</b><br /><br />
		/// This method serves to traverse the graph of persistent objects.
		/// All members of an object can be activated in turn with subsequent calls.<br /><br />
		/// Only objects in <code>DEACTIVATED</code> state are modified.
		/// <code>Object</code> members at the specified depth are
		/// instantiated in <code>DEACTIVATED</code> state.
		/// <br /><br />Duplicate <code>activate()</code> calls on the same object have no effect.
		/// Passing an object that is not stored in the <code>ObjectContainer
		/// </code> has no effect.<br /><br />
		/// The activation depth of individual classes can be overruled
		/// with the methods
		/// <see cref="com.db4o.config.ObjectClass.maximumActivationDepth">maximumActivationDepth()
		/// 	</see>
		/// and
		/// <see cref="com.db4o.config.ObjectClass.minimumActivationDepth">minimumActivationDepth()
		/// 	</see>
		/// in the
		/// <see cref="com.db4o.config.ObjectClass">ObjectClass interface</see>
		/// .<br /><br />
		/// A successful <code>activate()</code> triggers the callback method
		/// <see cref="com.db4o.ext.ObjectCallbacks.objectOnActivate">objectOnActivate</see>
		/// which can be used for cascaded activation.<br /><br />
		/// </remarks>
		/// <seealso cref="com.db4o.config.Configuration.activationDepth">Why activation?</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		/// <param name="obj">the object to be activated.</param>
		/// <param name="depth">
		/// the member
		/// <see cref="com.db4o.config.Configuration.activationDepth">depth</see>
		/// to which activate is to cascade.
		/// </param>
		void activate(object obj, int depth);

		/// <summary>closes the <code>ObjectContainer</code>.</summary>
		/// <remarks>
		/// closes the <code>ObjectContainer</code>.
		/// <br /><br />A call to <code>close()</code> automatically performs a
		/// <see cref="com.db4o.ObjectContainer.commit">commit()</see>
		/// .
		/// <br /><br />Note that every session opened with Db4o.openFile() requires one
		/// close()call, even if the same filename was used multiple times.<br /><br />
		/// Use <code>while(!close()){}</code> to kill all sessions using this container.<br /><br />
		/// </remarks>
		/// <returns>
		/// success - true denotes that the last used instance of this container
		/// and the database file were closed.
		/// </returns>
		bool close();

		/// <summary>commits the running transaction.</summary>
		/// <remarks>commits the running transaction.</remarks>
		void commit();

		/// <summary>deactivates a stored object by setting all members to <code>NULL</code>.
		/// 	</summary>
		/// <remarks>
		/// deactivates a stored object by setting all members to <code>NULL</code>.
		/// <br />Primitive types will be set to their default values.
		/// <br /><br /><b>Examples: ../com/db4o/samples/activate.</b><br /><br />
		/// Calls to this method save memory.
		/// The method has no effect, if the passed object is not stored in the
		/// <code>ObjectContainer</code>.<br /><br />
		/// <code>deactivate()</code> triggers the callback method
		/// <see cref="com.db4o.ext.ObjectCallbacks.objectOnDeactivate">objectOnDeactivate</see>
		/// .
		/// <br /><br />
		/// Be aware that calling this method with a depth parameter greater than
		/// 1 sets members on member objects to null. This may have side effects
		/// in other places of the application.<br /><br />
		/// </remarks>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		/// <seealso cref="com.db4o.config.Configuration.activationDepth">Why activation?</seealso>
		/// <param name="obj">the object to be deactivated.</param>
		/// <param name="depth">
		/// the member
		/// <see cref="com.db4o.config.Configuration.activationDepth">depth</see>
		/// 
		/// to which deactivate is to cascade.
		/// </param>
		void deactivate(object obj, int depth);

		/// <summary>deletes a stored object permanently.</summary>
		/// <remarks>
		/// deletes a stored object permanently.
		/// <br /><br />Note that this method has to be called <b>for every single object
		/// individually</b>. Delete does not recurse to object members. Simple
		/// and array member types are destroyed.
		/// <br /><br />Object members of the passed object remain untouched, unless
		/// cascaded deletes are
		/// <see cref="com.db4o.config.ObjectClass.cascadeOnDelete">configured for the class</see>
		/// or for
		/// <see cref="com.db4o.config.ObjectField.cascadeOnDelete">one of the member fields</see>
		/// .
		/// <br /><br />The method has no effect, if
		/// the passed object is not stored in the <code>ObjectContainer</code>.
		/// <br /><br />A subsequent call to
		/// <code>set()</code> with the same object newly stores the object
		/// to the <code>ObjectContainer</code>.<br /><br />
		/// <code>delete()</code> triggers the callback method
		/// <see cref="com.db4o.ext.ObjectCallbacks.objectOnDelete">objectOnDelete</see>
		/// which can be also used for cascaded deletes.<br /><br />
		/// </remarks>
		/// <seealso cref="com.db4o.config.ObjectClass.cascadeOnDelete">com.db4o.config.ObjectClass.cascadeOnDelete
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.ObjectField.cascadeOnDelete">com.db4o.config.ObjectField.cascadeOnDelete
		/// 	</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		/// <param name="obj">
		/// the object to be deleted from the
		/// <code>ObjectContainer</code>.<br />
		/// </param>
		void delete(object obj);

		/// <summary>returns an ObjectContainer with extended functionality.</summary>
		/// <remarks>
		/// returns an ObjectContainer with extended functionality.
		/// <br /><br />Every ObjectContainer that db4o provides can be casted to
		/// an ExtObjectContainer. This method is supplied for your convenience
		/// to work without a cast.
		/// <br /><br />The ObjectContainer functionality is split to two interfaces
		/// to allow newcomers to focus on the essential methods.<br /><br />
		/// </remarks>
		/// <returns>this, casted to ExtObjectContainer</returns>
		com.db4o.ext.ExtObjectContainer ext();

		/// <summary>Query-By-Example interface to retrieve objects.</summary>
		/// <remarks>
		/// Query-By-Example interface to retrieve objects.
		/// <br /><br /><code>get()</code> creates an
		/// <see cref="com.db4o.ObjectSet">ObjectSet</see>
		/// containing
		/// all objects in the <code>ObjectContainer</code> that match the passed
		/// template object.<br /><br />
		/// Calling <code>get(NULL)</code> returns all objects stored in the
		/// <code>ObjectContainer</code>.<br /><br /><br />
		/// <b>Query Evaluation</b>
		/// <br />All non-null members of the template object are compared against
		/// all stored objects of the same class.
		/// Primitive type members are ignored if they are 0 or false respectively.
		/// <br /><br />Arrays and all supported <code>Collection</code> classes are
		/// evaluated for containment. Differences in <code>length/size()</code> are
		/// ignored.
		/// <br /><br />Consult the documentation of the Configuration package to
		/// configure class-specific behaviour.<br /><br /><br />
		/// <b>Returned Objects</b><br />
		/// The objects returned in the
		/// <see cref="com.db4o.ObjectSet">ObjectSet</see>
		/// are instantiated
		/// and activated to the preconfigured depth of 5. The
		/// <see cref="com.db4o.config.Configuration.activationDepth">activation depth</see>
		/// may be configured
		/// <see cref="com.db4o.config.Configuration.activationDepth">globally</see>
		/// or
		/// <see cref="com.db4o.config.ObjectClass">individually for classes</see>
		/// .
		/// <br /><br />
		/// db4o keeps track of all instantiatied objects. Queries will return
		/// references to these objects instead of instantiating them a second time.
		/// <br /><br />
		/// Objects newly activated by <code>get()</code> can respond to the callback
		/// method
		/// <see cref="com.db4o.ext.ObjectCallbacks.objectOnActivate">objectOnActivate</see>
		/// .
		/// <br /><br />
		/// </remarks>
		/// <param name="template">object to be used as an example to find all matching objects.<br /><br />
		/// 	</param>
		/// <returns>
		/// 
		/// <see cref="com.db4o.ObjectSet">ObjectSet</see>
		/// containing all found objects.<br /><br />
		/// </returns>
		/// <seealso cref="com.db4o.config.Configuration.activationDepth">Why activation?</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		com.db4o.ObjectSet get(object template);

		/// <summary>
		/// factory method to create a new
		/// <see cref="com.db4o.query.Query">Query</see>
		/// object.
		/// <br /><br />
		/// Use
		/// <see cref="com.db4o.ObjectContainer.get">get(Object template)</see>
		/// for
		/// simple Query-By-Example.
		/// <br /><br />
		/// </summary>
		/// <returns>a new Query object</returns>
		com.db4o.query.Query query();

		/// <param name="predicate"></param>
		/// <returns></returns>
		com.db4o.ObjectSet query(com.db4o.query.Predicate predicate);

		/// <summary>rolls back the running transaction.</summary>
		/// <remarks>
		/// rolls back the running transaction.
		/// <br /><br />Modified application objects im memory are not restored.
		/// Use combined calls to
		/// <see cref="com.db4o.ObjectContainer.deactivate">deactivate()</see>
		/// and
		/// <see cref="com.db4o.ObjectContainer.activate">activate()</see>
		/// to reload an objects member values.
		/// </remarks>
		void rollback();

		/// <summary>newly stores objects or updates stored objects.</summary>
		/// <remarks>
		/// newly stores objects or updates stored objects.
		/// <br /><br />An object not yet stored in the <code>ObjectContainer</code> will be
		/// stored when it is passed to <code>set()</code>. An object already stored
		/// in the <code>ObjectContainer</code> will be updated.
		/// <br /><br /><b>Updates</b><br />
		/// - will affect all simple type object members.<br />
		/// - links to object members that are already stored will be updated.<br />
		/// - new object members will be newly stored. The algorithm traverses down
		/// new members, as long as further new members are found.<br />
		/// - object members that are already stored will <b>not</b> be updated
		/// themselves.<br />Every object member needs to be updated individually with a
		/// call to <code>set()</code> unless a deep
		/// <see cref="com.db4o.config.Configuration.updateDepth">global</see>
		/// or
		/// <see cref="com.db4o.config.ObjectClass.updateDepth">class-specific</see>
		/// update depth was configured or cascaded updates were
		/// <see cref="com.db4o.config.ObjectClass.cascadeOnUpdate">defined in the class</see>
		/// or in
		/// <see cref="com.db4o.config.ObjectField.cascadeOnUpdate">one of the member fields</see>
		/// .
		/// <br /><br /><b>Examples: ../com/db4o/samples/update.</b><br /><br />
		/// Depending if the passed object is newly stored or updated, the
		/// callback method
		/// <see cref="com.db4o.ext.ObjectCallbacks.objectOnNew">objectOnNew</see>
		/// or
		/// <see cref="com.db4o.ext.ObjectCallbacks.objectOnUpdate">objectOnUpdate</see>
		/// is triggered.
		/// <see cref="com.db4o.ext.ObjectCallbacks.objectOnUpdate">objectOnUpdate</see>
		/// might also be used for cascaded updates.<br /><br />
		/// </remarks>
		/// <param name="obj">the object to be stored or updated.</param>
		/// <seealso cref="com.db4o.ext.ExtObjectContainer.set">ExtObjectContainer#set(object, depth)
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.Configuration.updateDepth">com.db4o.config.Configuration.updateDepth
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.ObjectClass.updateDepth">com.db4o.config.ObjectClass.updateDepth
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.ObjectClass.cascadeOnUpdate">com.db4o.config.ObjectClass.cascadeOnUpdate
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.ObjectField.cascadeOnUpdate">com.db4o.config.ObjectField.cascadeOnUpdate
		/// 	</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		void set(object obj);
	}
}
