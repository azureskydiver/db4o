namespace com.db4o.ext
{
	/// <summary>
	/// extended functionality for the
	/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
	/// interface.
	/// <br /><br />Every db4o
	/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
	/// always is an <code>ExtObjectContainer</code> so a cast is possible.<br /><br />
	/// <see cref="com.db4o.ObjectContainer.Ext">com.db4o.ObjectContainer.Ext</see>
	/// is a convenient method to perform the cast.<br /><br />
	/// The ObjectContainer functionality is split to two interfaces to allow newcomers to
	/// focus on the essential methods.
	/// </summary>
	public interface ExtObjectContainer : com.db4o.ObjectContainer
	{
		/// <summary>backs up a database file of an open ObjectContainer.</summary>
		/// <remarks>
		/// backs up a database file of an open ObjectContainer.
		/// <br /><br />While the backup is running, the ObjectContainer can continue to be
		/// used. Changes that are made while the backup is in progress, will be applied to
		/// the open ObjectContainer and to the backup.<br /><br />
		/// While the backup is running, the ObjectContainer should not be closed.<br /><br />
		/// If a file already exists at the specified path, it will be overwritten.<br /><br />
		/// </remarks>
		/// <param name="path">a fully qualified path</param>
		void Backup(string path);

		/// <summary>binds an object to an internal object ID.</summary>
		/// <remarks>
		/// binds an object to an internal object ID.
		/// <br /><br />This method uses the ID parameter to load the
		/// correspondig stored object into memory and replaces this memory
		/// reference with the object parameter. The method may be used to replace
		/// objects or to reassociate an object with it's stored instance
		/// after closing and opening a database file. A subsequent call to
		/// <see cref="com.db4o.ObjectContainer.Set">set(Object)</see>
		/// is
		/// necessary to update the stored object.<br /><br />
		/// <b>Requirements:</b><br />- The ID needs to be a valid internal object ID,
		/// previously retrieved with
		/// <see cref="com.db4o.ext.ExtObjectContainer.GetID">getID(Object)</see>
		/// .<br />
		/// - The object parameter needs to be of the same class as the stored object.<br /><br />
		/// </remarks>
		/// <seealso cref="com.db4o.ext.ExtObjectContainer.GetID">com.db4o.ext.ExtObjectContainer.GetID
		/// 	</seealso>
		/// <param name="obj">the object that is to be bound</param>
		/// <param name="id">the internal id the object is to be bound to</param>
		void Bind(object obj, long id);

		/// <summary>
		/// returns the
		/// <see cref="com.db4o.types.Db4oCollections">com.db4o.types.Db4oCollections</see>
		/// interface to create or modify database-aware
		/// collections for this
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// .<br /><br />
		/// </summary>
		/// <returns>
		/// the
		/// <see cref="com.db4o.types.Db4oCollections">com.db4o.types.Db4oCollections</see>
		/// interface for this
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// .
		/// </returns>
		com.db4o.types.Db4oCollections Collections();

		/// <summary>returns the Configuration context for this ObjectContainer.</summary>
		/// <remarks>
		/// returns the Configuration context for this ObjectContainer.
		/// <br /><br />
		/// Upon opening an ObjectContainer with any of the factory methods in the
		/// <see cref="com.db4o.Db4o">Db4o class</see>
		/// , the global
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// context
		/// is copied into the ObjectContainer. The
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// can be modified individually for
		/// each ObjectContainer without any effects on the global settings.<br /><br />
		/// </remarks>
		/// <returns>
		/// 
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// the Configuration
		/// context for this ObjectContainer
		/// </returns>
		/// <seealso cref="com.db4o.Db4o.Configure">com.db4o.Db4o.Configure</seealso>
		com.db4o.config.Configuration Configure();

		/// <summary>returns a member at the specific path without activating intermediate objects.
		/// 	</summary>
		/// <remarks>
		/// returns a member at the specific path without activating intermediate objects.
		/// <br /><br />
		/// This method allows navigating from a persistent object to it's members in a
		/// performant way without activating or instantiating intermediate objects.
		/// </remarks>
		/// <param name="obj">the parent object that is to be used as the starting point.</param>
		/// <param name="path">an array of field names to navigate by</param>
		/// <returns>the object at the specified path or null if no object is found</returns>
		object Descend(object obj, string[] path);

		/// <summary>returns the stored object for an internal ID.</summary>
		/// <remarks>
		/// returns the stored object for an internal ID.
		/// <br /><br />This is the fastest method for direct access to objects. Internal
		/// IDs can be obtained with
		/// <see cref="com.db4o.ext.ExtObjectContainer.GetID">getID(Object)</see>
		/// .
		/// Objects will not be activated by this method. They will be returned in the
		/// activation state they are currently in, in the local cache.<br /><br />
		/// </remarks>
		/// <param name="ID">the internal ID</param>
		/// <returns>
		/// the object associated with the passed ID or <code>null</code>,
		/// if no object is associated with this ID in this <code>ObjectContainer</code>.
		/// </returns>
		/// <seealso cref="com.db4o.config.Configuration.ActivationDepth">Why activation?</seealso>
		object GetByID(long ID);

		/// <summary>
		/// returns a stored object for a
		/// <see cref="com.db4o.ext.Db4oUUID">com.db4o.ext.Db4oUUID</see>
		/// .
		/// <br /><br />
		/// This method is intended for replication and for long-term
		/// external references to objects. To get a
		/// <see cref="com.db4o.ext.Db4oUUID">com.db4o.ext.Db4oUUID</see>
		/// for an
		/// object use
		/// <see cref="com.db4o.ext.ExtObjectContainer.GetObjectInfo">com.db4o.ext.ExtObjectContainer.GetObjectInfo
		/// 	</see>
		/// and
		/// <see cref="com.db4o.ext.ObjectInfo.GetUUID">com.db4o.ext.ObjectInfo.GetUUID</see>
		/// .<br /><br />
		/// Objects will not be activated by this method. They will be returned in the
		/// activation state they are currently in, in the local cache.<br /><br />
		/// </summary>
		/// <param name="uuid">the UUID</param>
		/// <returns>the object for the UUID</returns>
		/// <seealso cref="com.db4o.config.Configuration.ActivationDepth">Why activation?</seealso>
		object GetByUUID(com.db4o.ext.Db4oUUID uuid);

		/// <summary>returns the internal unique object ID.</summary>
		/// <remarks>
		/// returns the internal unique object ID.
		/// <br /><br />db4o assigns an internal ID to every object that is stored. IDs are
		/// guaranteed to be unique within one <code>ObjectContainer</code>.
		/// An object carries the same ID in every db4o session. Internal IDs can
		/// be used to look up objects with the very fast
		/// <see cref="com.db4o.ext.ExtObjectContainer.GetByID">getByID</see>
		/// method.<br /><br />
		/// Internal IDs will change when a database is defragmented. Use
		/// <see cref="com.db4o.ext.ExtObjectContainer.GetObjectInfo">com.db4o.ext.ExtObjectContainer.GetObjectInfo
		/// 	</see>
		/// ,
		/// <see cref="com.db4o.ext.ObjectInfo.GetUUID">com.db4o.ext.ObjectInfo.GetUUID</see>
		/// and
		/// <see cref="com.db4o.ext.ExtObjectContainer.GetByUUID">com.db4o.ext.ExtObjectContainer.GetByUUID
		/// 	</see>
		/// for long-term external references to
		/// objects.<br /><br />
		/// </remarks>
		/// <param name="obj">any object</param>
		/// <returns>
		/// the associated internal ID or <code>0</code>, if the passed
		/// object is not stored in this <code>ObjectContainer</code>.
		/// </returns>
		long GetID(object obj);

		/// <summary>
		/// returns the
		/// <see cref="com.db4o.ext.ObjectInfo">com.db4o.ext.ObjectInfo</see>
		/// for a stored object.
		/// <br /><br />This method will return null, if the passed
		/// object is not stored to this <code>ObjectContainer</code>.<br /><br />
		/// </summary>
		/// <param name="obj">the stored object</param>
		/// <returns>
		/// the
		/// <see cref="com.db4o.ext.ObjectInfo">com.db4o.ext.ObjectInfo</see>
		/// 
		/// </returns>
		com.db4o.ext.ObjectInfo GetObjectInfo(object obj);

		/// <summary>returns the Db4oDatabase identity object for this ObjectContainer.</summary>
		/// <remarks>returns the Db4oDatabase identity object for this ObjectContainer.</remarks>
		/// <returns>the Db4oDatabase identity object for this ObjectContainer.</returns>
		com.db4o.ext.Db4oDatabase Identity();

		/// <summary>tests if an object is activated.</summary>
		/// <remarks>
		/// tests if an object is activated.
		/// <br /><br /><code>isActive</code> returns <code>false</code> if an object is not
		/// stored within the <code>ObjectContainer</code>.<br /><br />
		/// </remarks>
		/// <param name="obj">to be tested<br /><br /></param>
		/// <returns><code>true</code> if the passed object is active.</returns>
		bool IsActive(object obj);

		/// <summary>tests if an object with this ID is currently cached.</summary>
		/// <remarks>
		/// tests if an object with this ID is currently cached.
		/// <br /><br />
		/// </remarks>
		/// <param name="ID">the internal ID</param>
		bool IsCached(long ID);

		/// <summary>tests if this <code>ObjectContainer</code> is closed.</summary>
		/// <remarks>
		/// tests if this <code>ObjectContainer</code> is closed.
		/// <br /><br />
		/// </remarks>
		/// <returns><code>true</code> if this <code>ObjectContainer</code> is closed.</returns>
		bool IsClosed();

		/// <summary>tests if an object is stored in this <code>ObjectContainer</code>.</summary>
		/// <remarks>
		/// tests if an object is stored in this <code>ObjectContainer</code>.
		/// <br /><br />
		/// </remarks>
		/// <param name="obj">to be tested<br /><br /></param>
		/// <returns><code>true</code> if the passed object is stored.</returns>
		bool IsStored(object obj);

		/// <summary>
		/// returns all class representations that are known to this
		/// ObjectContainer because they have been used or stored.
		/// </summary>
		/// <remarks>
		/// returns all class representations that are known to this
		/// ObjectContainer because they have been used or stored.
		/// </remarks>
		/// <returns>
		/// all class representations that are known to this
		/// ObjectContainer because they have been used or stored.
		/// </returns>
		com.db4o.reflect.ReflectClass[] KnownClasses();

		/// <summary>returns the main synchronisation lock.</summary>
		/// <remarks>
		/// returns the main synchronisation lock.
		/// <br /><br />
		/// Synchronize over this object to ensure exclusive access to
		/// the ObjectContainer.<br /><br />
		/// Handle the use of this functionality with extreme care,
		/// since deadlocks can be produced with just two lines of code.
		/// </remarks>
		/// <returns>Object the ObjectContainer lock object</returns>
		object Lock();

		/// <summary>aids migration of objects between ObjectContainers.</summary>
		/// <remarks>
		/// aids migration of objects between ObjectContainers.
		/// <br /><br />When objects are migrated from one ObjectContainer to another, it is
		/// desirable to preserve virtual object attributes such as the object version number
		/// or the UUID. Use this method to signal to an ObjectContainer that it should read
		/// existing version numbers and UUIDs from another ObjectContainer. This method should
		/// also be used during the Defragment. It is included in the default
		/// implementation supplied in Defragment.java/Defragment.cs.<br /><br />
		/// </remarks>
		/// <param name="objectContainer">
		/// the
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// objects are to be migrated
		/// from or <code>null</code> to denote that migration is completed.
		/// </param>
		void MigrateFrom(com.db4o.ObjectContainer objectContainer);

		/// <summary>
		/// returns a transient copy of a persistent object with all members set
		/// to the values that are currently stored to the database.
		/// </summary>
		/// <remarks>
		/// returns a transient copy of a persistent object with all members set
		/// to the values that are currently stored to the database.
		/// <br /><br />
		/// The returned objects have no connection to the database.<br /><br />
		/// With the <code>committed</code> parameter it is possible to specify,
		/// whether the desired object should contain the committed values or the
		/// values that were set by the running transaction with
		/// <see cref="com.db4o.ObjectContainer.Set">com.db4o.ObjectContainer.Set</see>
		/// .
		/// <br /><br />A possible usecase for this feature:<br />
		/// An application might want to check all changes applied to an object
		/// by the running transaction.<br /><br />
		/// </remarks>
		/// <param name="@object">the object that is to be cloned</param>
		/// <param name="depth">the member depth to which the object is to be instantiated</param>
		/// <param name="committed">whether committed or set values are to be returned</param>
		/// <returns>the object</returns>
		object PeekPersisted(object @object, int depth, bool committed);

		/// <summary>unloads all clean indices from memory and frees unused objects.</summary>
		/// <remarks>
		/// unloads all clean indices from memory and frees unused objects.
		/// <br /><br />Call commit() and purge() consecutively to achieve the best
		/// result possible. This method can have a negative impact
		/// on performance since indices will have to be reread before further
		/// inserts, updates or queries can take place.
		/// </remarks>
		void Purge();

		/// <summary>unloads a specific object from the db4o reference mechanism.</summary>
		/// <remarks>
		/// unloads a specific object from the db4o reference mechanism.
		/// <br /><br />db4o keeps references to all newly stored and
		/// instantiated objects in memory, to be able to manage object identities.
		/// <br /><br />With calls to this method it is possible to remove an object from the
		/// reference mechanism, to allow it to be garbage collected. You are not required to
		/// call this method in the .NET and JDK 1.2 versions, since objects are
		/// referred to by weak references and garbage collection happens
		/// automatically.<br /><br />An object removed with  <code>purge(Object)</code> is not
		/// "known" to the <code>ObjectContainer</code> afterwards, so this method may also be
		/// used to create multiple copies of  objects.<br /><br /> <code>purge(Object)</code> has
		/// no influence on the persistence state of objects. "Purged" objects can be
		/// reretrieved with queries.<br /><br />
		/// </remarks>
		/// <param name="obj">the object to be removed from the reference mechanism.</param>
		void Purge(object obj);

		/// <summary>Return the reflector currently being used by db4objects.</summary>
		/// <remarks>Return the reflector currently being used by db4objects.</remarks>
		/// <returns>the current Reflector.</returns>
		com.db4o.reflect.generic.GenericReflector Reflector();

		/// <summary>refreshs all members on a stored object to the specified depth.</summary>
		/// <remarks>
		/// refreshs all members on a stored object to the specified depth.
		/// <br /><br />If a member object is not activated, it will be activated by this method.
		/// <br /><br />The isolation used is READ COMMITTED. This method will read all objects
		/// and values that have been committed by other transactions.<br /><br />
		/// </remarks>
		/// <param name="obj">the object to be refreshed.</param>
		/// <param name="depth">
		/// the member
		/// <see cref="com.db4o.config.Configuration.ActivationDepth">depth</see>
		/// to which refresh is to cascade.
		/// </param>
		void Refresh(object obj, int depth);

		/// <summary>releases a semaphore, if the calling transaction is the owner.</summary>
		/// <remarks>releases a semaphore, if the calling transaction is the owner.</remarks>
		/// <param name="name">the name of the semaphore to be released.</param>
		void ReleaseSemaphore(string name);

		/// <summary>
		/// prepares for replication with another
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// .
		/// <br /><br />An
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// can only be involved in a replication
		/// process with one other
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// at the same time.<br /><br />
		/// The returned
		/// <see cref="com.db4o.replication.ReplicationProcess">com.db4o.replication.ReplicationProcess
		/// 	</see>
		/// interface provides methods to commit
		/// and to cancel the replication process.
		/// <br /><br />This ObjectContainer will be "peerA" for the
		/// returned ReplicationProcess. The other ObjectContainer will be "peerB".
		/// </summary>
		/// <param name="peerB">
		/// the
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// to replicate with.
		/// </param>
		/// <param name="conflictHandler">
		/// the conflict handler for this ReplicationProcess.
		/// Conflicts occur
		/// whenever
		/// <see cref="com.db4o.replication.ReplicationProcess.Replicate">com.db4o.replication.ReplicationProcess.Replicate
		/// 	</see>
		/// is called with an
		/// object that was modified in both ObjectContainers since the last
		/// replication run between the two. Upon a conflict the
		/// <see cref="com.db4o.replication.ReplicationConflictHandler.ResolveConflict">com.db4o.replication.ReplicationConflictHandler.ResolveConflict
		/// 	</see>
		/// method will be called in the conflict handler.
		/// </param>
		/// <returns>
		/// the
		/// <see cref="com.db4o.replication.ReplicationProcess">com.db4o.replication.ReplicationProcess
		/// 	</see>
		/// interface for this replication process.
		/// </returns>
		com.db4o.replication.ReplicationProcess ReplicationBegin(com.db4o.ObjectContainer
			 peerB, com.db4o.replication.ReplicationConflictHandler conflictHandler);

		/// <summary>deep update interface to store or update objects.</summary>
		/// <remarks>
		/// deep update interface to store or update objects.
		/// <br /><br />In addition to the normal storage interface,
		/// <see cref="com.db4o.ObjectContainer.Set">ObjectContainer#set(Object)</see>
		/// ,
		/// this method allows a manual specification of the depth, the passed object is to be updated.<br /><br />
		/// </remarks>
		/// <param name="obj">the object to be stored or updated.</param>
		/// <param name="depth">the depth to which the object is to be updated</param>
		/// <seealso cref="com.db4o.ObjectContainer.Set">com.db4o.ObjectContainer.Set</seealso>
		void Set(object obj, int depth);

		/// <summary>attempts to set a semaphore.</summary>
		/// <remarks>
		/// attempts to set a semaphore.
		/// <br /><br />
		/// Semaphores are transient multi-purpose named flags for
		/// <see cref="com.db4o.ObjectContainer">ObjectContainers</see>
		/// .
		/// <br /><br />
		/// A transaction that successfully sets a semaphore becomes
		/// the owner of the semaphore. Semaphores can only be owned
		/// by a single transaction at one point in time.<br /><br />
		/// This method returns true, if the transaction already owned
		/// the semaphore before the method call or if it successfully
		/// acquires ownership of the semaphore.<br /><br />
		/// The waitForAvailability parameter allows to specify a time
		/// in milliseconds to wait for other transactions to release
		/// the semaphore, in case the semaphore is already owned by
		/// another transaction.<br /><br />
		/// Semaphores are released by the first occurence of one of the
		/// following:<br />
		/// - the transaction releases the semaphore with
		/// <see cref="com.db4o.ext.ExtObjectContainer.ReleaseSemaphore">com.db4o.ext.ExtObjectContainer.ReleaseSemaphore
		/// 	</see>
		/// <br /> - the transaction is closed with
		/// <see cref="com.db4o.ObjectContainer.Close">com.db4o.ObjectContainer.Close</see>
		/// <br /> - C/S only: the corresponding
		/// <see cref="com.db4o.ObjectServer">com.db4o.ObjectServer</see>
		/// is
		/// closed.<br /> - C/S only: the client
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// looses the connection and is timed
		/// out.<br /><br /> Semaphores are set immediately. They are independant of calling
		/// <see cref="com.db4o.ObjectContainer.Commit">com.db4o.ObjectContainer.Commit</see>
		/// or
		/// <see cref="com.db4o.ObjectContainer.Rollback">com.db4o.ObjectContainer.Rollback</see>
		/// .<br /><br /> <b>Possible usecases
		/// for semaphores:</b><br /> - prevent other clients from inserting a singleton at the same time.
		/// A suggested name for the semaphore:  "SINGLETON_" + Object#getClass().getName().<br />  - lock
		/// objects. A suggested name:   "LOCK_" +
		/// <see cref="com.db4o.ext.ExtObjectContainer.GetID">getID(Object)</see>
		/// <br /> -
		/// generate a unique client ID. A suggested name:  "CLIENT_" +
		/// System.currentTimeMillis().<br /><br />
		/// </remarks>
		/// <param name="name">the name of the semaphore to be set</param>
		/// <param name="waitForAvailability">
		/// the time in milliseconds to wait for other
		/// transactions to release the semaphore. The parameter may be zero, if
		/// the method is to return immediately.
		/// </param>
		/// <returns>
		/// boolean flag
		/// <br /><code>true</code>, if the semaphore could be set or if the
		/// calling transaction already owned the semaphore.
		/// <br /><code>false</code>, if the semaphore is owned by another
		/// transaction.
		/// </returns>
		bool SetSemaphore(string name, int waitForAvailability);

		/// <summary>
		/// returns a
		/// <see cref="com.db4o.ext.StoredClass">com.db4o.ext.StoredClass</see>
		/// meta information object.
		/// <br /><br />
		/// There are three options how to use this method.<br />
		/// Any of the following parameters are possible:<br />
		/// - a fully qualified classname.<br />
		/// - a Class object.<br />
		/// - any object to be used as a template.<br /><br />
		/// </summary>
		/// <param name="clazz">class name, Class object, or example object.<br /><br /></param>
		/// <returns>
		/// an instance of an
		/// <see cref="com.db4o.ext.StoredClass">com.db4o.ext.StoredClass</see>
		/// meta information object.
		/// </returns>
		com.db4o.ext.StoredClass StoredClass(object clazz);

		/// <summary>
		/// returns an array of all
		/// <see cref="com.db4o.ext.StoredClass">com.db4o.ext.StoredClass</see>
		/// meta information objects.
		/// </summary>
		com.db4o.ext.StoredClass[] StoredClasses();

		/// <summary>
		/// returns the
		/// <see cref="com.db4o.ext.SystemInfo">com.db4o.ext.SystemInfo</see>
		/// for this ObjectContainer.
		/// <br /><br />The
		/// <see cref="com.db4o.ext.SystemInfo">com.db4o.ext.SystemInfo</see>
		/// supplies methods that provide
		/// information about system state and system settings of this
		/// ObjectContainer.
		/// </summary>
		/// <returns>
		/// the
		/// <see cref="com.db4o.ext.SystemInfo">com.db4o.ext.SystemInfo</see>
		/// for this ObjectContainer.
		/// </returns>
		com.db4o.ext.SystemInfo SystemInfo();

		/// <summary>returns the current transaction serial number.</summary>
		/// <remarks>
		/// returns the current transaction serial number.
		/// <br /><br />This serial number can be used to query for modified objects
		/// and for replication purposes.
		/// </remarks>
		/// <returns>the current transaction serial number.</returns>
		long Version();
	}
}
