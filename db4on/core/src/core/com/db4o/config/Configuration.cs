namespace com.db4o.config
{
	/// <summary>configuration interface for db4o.</summary>
	/// <remarks>
	/// configuration interface for db4o.
	/// <br /><br />This interface contains methods to configure db4o. All methods
	/// should be called <b>before</b> starting the db4o engine.
	/// <br /><br />
	/// <see cref="com.db4o.Db4o.configure">Db4o.configure()</see>
	/// returns the single global Configuration object.
	/// </remarks>
	public interface Configuration
	{
		/// <summary>sets the activation depth to the specified value.</summary>
		/// <remarks>
		/// sets the activation depth to the specified value.
		/// <br /><br /><b>Why activation?</b><br />
		/// During the instantiation
		/// of stored objects from persistent storage, the instantiation of members
		/// needs to be limited to a certain depth. Otherwise a possible root object
		/// would completely instantiate all stored objects to memory.<br /><br /><b>db4o uses a
		/// preconfigured "activation depth" of 5.</b><br /><br />If an object is returned in an
		/// <see cref="com.db4o.ObjectSet">ObjectSet</see>
		/// as a result of a
		/// <see cref="com.db4o.ObjectContainer.get">query</see>
		/// <code>
		/// object.member1.member2.member3.member4.member5</code> will be instantiated.
		/// member5 will have all it's members set to null. Primitive
		/// types will have the default values respectively. In db4o terminology, the
		/// state of member5 is called <code>DEACTIVATED</code>. member5 can be
		/// activated by calling
		/// <see cref="com.db4o.ObjectContainer.activate">ObjectContainer#activate(member5, depth)
		/// 	</see>
		/// .
		/// <br /><br />
		/// Note that raising the global activation depth will consume more memory and
		/// have negative effects on the performance of first-time retrievals. Lowering
		/// the global activation depth needs more individual activation work but can
		/// increase performance of queries.<br /><br />
		/// <see cref="com.db4o.ObjectContainer.deactivate">ObjectContainer#deactivate(Object, depth)
		/// 	</see>
		/// can be used to manually free memory by deactivating objects.
		/// <br /><br /><b>Examples: ../com/db4o/samples/activate.</b><br /><br />
		/// </remarks>
		/// <param name="depth">the desired global activation depth.</param>
		/// <seealso cref="com.db4o.config.ObjectClass.maximumActivationDepth">configuring classes individually
		/// 	</seealso>
		void activationDepth(int depth);

		/// <summary>turns automatic shutdown of the engine on and off.</summary>
		/// <remarks>
		/// turns automatic shutdown of the engine on and off.
		/// <br /><br />Depending on the JDK, db4o uses one of the following
		/// two methods to shut down, if no more references to the ObjectContainer
		/// are being held or the JVM terminates:<br />
		/// - JDK 1.3 and above: <code>Runtime.addShutdownHook()</code><br />
		/// - JDK 1.2 and below: <code>System.runFinalizersOnExit(true)</code> and code
		/// in the finalizer.<br /><br />
		/// Some JVMs have severe problems with both methods. For these rare cases the
		/// autoShutDown feature may be turned off.<br /><br />
		/// The default and recommended setting is <code>true</code>.<br /><br />
		/// </remarks>
		/// <param name="flag">whether db4o should shut down automatically.</param>
		void automaticShutDown(bool flag);

		/// <summary>sets the storage data blocksize for new ObjectContainers.</summary>
		/// <remarks>
		/// sets the storage data blocksize for new ObjectContainers.
		/// <br /><br />The standard setting is 1 allowing for a maximum
		/// database file size of 2GB. This value can be increased
		/// to allow larger database files, although some space will
		/// be lost to padding because the size of some stored objects
		/// will not be an exact multiple of the block size. A
		/// recommended setting for large database files is 8, since
		/// internal pointers have this length.
		/// </remarks>
		/// <param name="bytes">the size in bytes from 1 to 127</param>
		void blockSize(int bytes);

		/// <summary>turns callback methods on and off.</summary>
		/// <remarks>
		/// turns callback methods on and off.
		/// <br /><br />Callbacks are turned on by default.<br /><br />
		/// A tuning hint: If callbacks are not used, you can turn this feature off, to
		/// prevent db4o from looking for callback methods in persistent classes. This will
		/// increase the performance on system startup.<br /><br />
		/// </remarks>
		/// <param name="flag">false to turn callback methods off</param>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		void callbacks(bool flag);

		/// <summary>
		/// advises db4o to try instantiating objects with/without calling
		/// constructors.
		/// </summary>
		/// <remarks>
		/// advises db4o to try instantiating objects with/without calling
		/// constructors.
		/// <br /><br />
		/// Not all JDKs / .NET-environments support this feature. db4o will
		/// attempt, to follow the setting as good as the enviroment supports.
		/// In doing so, it may call implementation-specific features like
		/// sun.reflect.ReflectionFactory#newConstructorForSerialization on the
		/// Sun Java 1.4.x/5 VM (not available on other VMs) and
		/// FormatterServices.GetUninitializedObject() on
		/// the .NET framework (not available on CompactFramework).
		/// This setting may also be overridden for individual classes in
		/// <see cref="com.db4o.config.ObjectClass.callConstructor">com.db4o.config.ObjectClass.callConstructor
		/// 	</see>
		/// .<br /><br />
		/// </remarks>
		/// <param name="flag">
		/// - specify true, to request calling constructors, specify
		/// false to request <b>not</b> calling constructors.
		/// </param>
		/// <seealso cref="com.db4o.config.ObjectClass.callConstructor">com.db4o.config.ObjectClass.callConstructor
		/// 	</seealso>
		void callConstructors(bool flag);

		/// <summary>
		/// turns
		/// <see cref="com.db4o.config.ObjectClass.maximumActivationDepth">individual class activation depth configuration
		/// 	</see>
		/// on
		/// and off.
		/// <br /><br />This feature is turned on by default.<br /><br />
		/// </summary>
		/// <param name="flag">
		/// false to turn the possibility to individually configure class
		/// activation depths off
		/// </param>
		/// <seealso cref="com.db4o.config.Configuration.activationDepth">Why activation?</seealso>
		void classActivationDepthConfigurable(bool flag);

		/// <summary>
		/// tuning feature: configures whether db4o checks all persistent classes upon system
		/// startup, for added or removed fields.
		/// </summary>
		/// <remarks>
		/// tuning feature: configures whether db4o checks all persistent classes upon system
		/// startup, for added or removed fields.
		/// <br /><br />In a production environment this setting can be set to <code>false</code>,
		/// if all necessary classes have been stored to the database file and none of them
		/// have been modified since the last use.
		/// <br /><br />Default value:<br />
		/// <code>true</code>
		/// </remarks>
		/// <param name="flag">the desired setting</param>
		void detectSchemaChanges(bool flag);

		/// <summary>turns commit recovery off.</summary>
		/// <remarks>
		/// turns commit recovery off.
		/// <br /><br />db4o uses a two-phase commit algorithm. In a first step all intended
		/// changes are written to a free place in the database file, the "transaction commit
		/// record". In a second step the
		/// actual changes are performed. If the system breaks down during commit, the
		/// commit process is restarted when the database file is opened the next time.
		/// On very rare occasions (possibilities: hardware failure or editing the database
		/// file with an external tool) the transaction commit record may be broken. In this
		/// case, this method can be used to try to open the database file without commit
		/// recovery. The method should only be used in emergency situations after consulting
		/// db4o support.
		/// </remarks>
		void disableCommitRecovery();

		/// <summary>
		/// tuning feature: configures the minimum size of free space slots in the database file
		/// that are to be reused.
		/// </summary>
		/// <remarks>
		/// tuning feature: configures the minimum size of free space slots in the database file
		/// that are to be reused.
		/// <br /><br />When objects are updated or deleted, the space previously occupied in the
		/// database file is marked as "free", so it can be reused. db4o maintains two lists
		/// in RAM, sorted by address and by size. Adjacent entries are merged. After a large
		/// number of updates or deletes have been executed, the lists can become large, causing
		/// RAM consumption and performance loss for maintenance. With this method you can
		/// specify an upper bound for the byte slot size to discard.
		/// <br /><br />Pass <code>Integer.MAX_VALUE</code> to this method to discard all free slots for
		/// the best possible startup time.<br /><br />
		/// The downside of setting this value: Database files will necessarily grow faster.
		/// <br /><br />Default value:<br />
		/// <code>0</code> all space is reused
		/// </remarks>
		/// <param name="byteCount">Slots with this size or smaller will be lost.</param>
		void discardFreeSpace(int byteCount);

		/// <summary>configures the use of encryption.</summary>
		/// <remarks>
		/// configures the use of encryption.
		/// <br /><br />This method needs to be called <b>before</b> a database file
		/// is created with the first
		/// <see cref="com.db4o.Db4o.openFile">Db4o.openFile()</see>
		/// .
		/// <br /><br />If encryption is set to true,
		/// you need to supply a password to seed the encryption mechanism.<br /><br />
		/// db4o database files keep their encryption format after creation.<br /><br />
		/// </remarks>
		/// <param name="flag">
		/// true for turning encryption on, false for turning encryption
		/// off.
		/// </param>
		/// <seealso cref="com.db4o.config.Configuration.password">com.db4o.config.Configuration.password
		/// 	</seealso>
		void encrypt(bool flag);

		/// <summary>configures whether Exceptions are to be thrown, if objects can not be stored.
		/// 	</summary>
		/// <remarks>
		/// configures whether Exceptions are to be thrown, if objects can not be stored.
		/// <br /><br />db4o requires the presence of a constructor that can be used to
		/// instantiate objects. If no default public constructor is present, all
		/// available constructors are tested, whether an instance of the class can
		/// be instantiated. Null is passed to all constructor parameters.
		/// The first constructor that is successfully tested will
		/// be used throughout the running db4o session. If an instance of the class
		/// can not be instantiated, the object will not be stored. By default,
		/// execution will continue without any message or error. This method can
		/// be used to configure db4o to throw an
		/// <see cref="com.db4o.ext.ObjectNotStorableException">ObjectNotStorableException</see>
		/// if an object can not be stored.
		/// <br /><br />
		/// The default for this setting is <b>false</b>.<br /><br />
		/// </remarks>
		/// <param name="flag">true to throw Exceptions if objects can not be stored.</param>
		void exceptionsOnNotStorable(bool flag);

		/// <summary>configures db4o to generate UUIDs for stored objects.</summary>
		/// <remarks>configures db4o to generate UUIDs for stored objects.</remarks>
		/// <param name="setting">
		/// one of the following values:<br />
		/// -1 - off<br />
		/// 1 - configure classes individually<br />
		/// Integer.MAX_Value - on for all classes
		/// </param>
		void generateUUIDs(int setting);

		/// <summary>configures db4o to generate version numbers for stored objects.</summary>
		/// <remarks>configures db4o to generate version numbers for stored objects.</remarks>
		/// <param name="setting">
		/// one of the following values:<br />
		/// -1 - off<br />
		/// 1 - configure classes individually<br />
		/// Integer.MAX_Value - on for all classes
		/// </param>
		void generateVersionNumbers(int setting);

		/// <summary>returns the MessageSender for this Configuration context.</summary>
		/// <remarks>returns the MessageSender for this Configuration context.</remarks>
		/// <returns>MessageSender</returns>
		com.db4o.messaging.MessageSender getMessageSender();

		/// <summary>allows to configure db4o to use a customized byte IO adapter.</summary>
		/// <remarks>
		/// allows to configure db4o to use a customized byte IO adapter.
		/// <br /><br />Derive from the abstract class
		/// <see cref="com.db4o.io.IoAdapter">com.db4o.io.IoAdapter</see>
		/// to
		/// write your own. Possible usecases could be improved performance
		/// with a native library, mirrored write to two files or
		/// read-on-write fail-safety control.<br /><br />Sample IoAdapters
		/// are supplied with the distribution as source code.
		/// </remarks>
		/// <param name="adapter">- the IoAdapter</param>
		void io(com.db4o.io.IoAdapter adapter);

		/// <summary>allows to mark fields as transient with custom attributes.</summary>
		/// <remarks>
		/// allows to mark fields as transient with custom attributes.
		/// <br /><br />.NET only: Call this method with the attribute name that you
		/// wish to use to mark fields as transient. Multiple transient attributes
		/// are possible by calling this method multiple times with different
		/// attribute names.<br /><br />
		/// </remarks>
		/// <param name="attributeName">
		/// - the fully qualified name of the attribute, including
		/// it's namespace
		/// </param>
		void markTransient(string attributeName);

		/// <summary>sets the detail level of db4o messages.</summary>
		/// <remarks>
		/// sets the detail level of db4o messages.
		/// <br /><br />
		/// Level 0 - no messages<br />
		/// Level 1 - open and close messages<br />
		/// Level 2 - messages for new, update and delete<br />
		/// Level 3 - messages for activate and deactivate<br /><br />
		/// </remarks>
		/// <param name="level">integer from 0 to 3</param>
		/// <seealso cref="com.db4o.config.Configuration.setOut">com.db4o.config.Configuration.setOut
		/// 	</seealso>
		void messageLevel(int level);

		/// <summary>can be used to turn the database file locking thread off.</summary>
		/// <remarks>
		/// can be used to turn the database file locking thread off.
		/// <br /><br />Since Java does not support file locking up to JDK 1.4,
		/// db4o uses an additional thread per open database file to prohibit
		/// concurrent access to the same database file by different db4o
		/// sessions in different VMs.<br /><br />
		/// To improve performance and to lower ressource consumption, this
		/// method provides the possibility to prevent the locking thread
		/// from being started.<br /><br /><b>Caution!</b><br />If database file
		/// locking is turned off, concurrent write access to the same
		/// database file from different JVM sessions will <b>corrupt</b> the
		/// database file immediately.<br /><br /> This method
		/// has no effect on open ObjectContainers. It will only affect how
		/// ObjectContainers are opened.<br /><br />
		/// The default setting is <code>true</code>.<br /><br />
		/// </remarks>
		/// <param name="flag"><code>false</code> to turn database file locking off.</param>
		void lockDatabaseFile(bool flag);

		/// <summary>
		/// returns an
		/// <see cref="com.db4o.config.ObjectClass">ObjectClass</see>
		/// object
		/// to configure the specified class.
		/// <br /><br />
		/// The clazz parameter can be any of the following:<br />
		/// - a fully qualified classname as a String.<br />
		/// - a Class object.<br />
		/// - any other object to be used as a template.<br /><br />
		/// </summary>
		/// <param name="clazz">class name, Class object, or example object.<br /><br /></param>
		/// <returns>
		/// an instance of an
		/// <see cref="com.db4o.config.ObjectClass">ObjectClass</see>
		/// object for configuration.
		/// </returns>
		com.db4o.config.ObjectClass objectClass(object clazz);

		/// <summary>protects the database file with a password.</summary>
		/// <remarks>
		/// protects the database file with a password.
		/// <br /><br />To set a password for a database file, this method needs to be
		/// called <b>before</b> a database file is created with the first
		/// <see cref="com.db4o.Db4o.openFile">Db4o.openFile()</see>
		/// .
		/// <br /><br />All further attempts to open
		/// the file, are required to set the same password.<br /><br />The password
		/// is used to seed the encryption mechanism, which makes it impossible
		/// to read the database file without knowing the password.<br /><br />
		/// </remarks>
		/// <param name="pass">the password to be used.</param>
		void password(string pass);

		/// <summary>turns readOnly mode on and off.</summary>
		/// <remarks>
		/// turns readOnly mode on and off.
		/// <br /><br />This method configures the mode in which subsequent calls to
		/// <see cref="com.db4o.Db4o.openFile">Db4o.openFile()</see>
		/// will open files.
		/// <br /><br />Readonly mode allows to open an unlimited number of reading
		/// processes on one database file. It is also convenient
		/// for deploying db4o database files on CD-ROM.<br /><br />If mixed access
		/// using many readOnly and one readWrite session is used, there is no
		/// guarantee that the data in the readOnly sessions will be kept up-to-date.
		/// <br /><br />
		/// </remarks>
		/// <param name="flag">
		/// <code>true</code> for configuring readOnly mode for subsequent
		/// calls to
		/// <see cref="com.db4o.Db4o.openFile">Db4o.openFile()</see>
		/// .
		/// </param>
		void readOnly(bool flag);

		/// <summary>configures the use of a specially designed reflection implementation.</summary>
		/// <remarks>
		/// configures the use of a specially designed reflection implementation.
		/// <br /><br />
		/// db4o internally uses java.lang.reflect.* by default. On platforms that
		/// do not support this package, customized implementations may be written
		/// to supply all the functionality of the interfaces in the com.db4o.reflect
		/// package. This method can be used to install a custom reflection
		/// implementation.
		/// </remarks>
		void reflectWith(com.db4o.reflect.Reflector reflector);

		/// <summary>forces analysation of all Classes during a running session.</summary>
		/// <remarks>
		/// forces analysation of all Classes during a running session.
		/// <br /><br />
		/// This method may be useful in combination with a modified ClassLoader and
		/// allows exchanging classes during a running db4o session.<br /><br />
		/// Calling this method on the global Configuration context will refresh
		/// the classes in all db4o sessions in the running VM. Calling this method
		/// in an ObjectContainer Configuration context, only the classes of the
		/// respective ObjectContainer will be refreshed.<br /><br />
		/// </remarks>
		/// <seealso cref="com.db4o.config.Configuration.setClassLoader">com.db4o.config.Configuration.setClassLoader
		/// 	</seealso>
		void refreshClasses();

		/// <summary>tuning feature only: reserves a number of bytes in database files.</summary>
		/// <remarks>
		/// tuning feature only: reserves a number of bytes in database files.
		/// <br /><br />The global setting is used for the creation of new database
		/// files. Continous calls on an ObjectContainer Configuration context
		/// (see
		/// <see cref="com.db4o.ext.ExtObjectContainer.configure">com.db4o.ext.ExtObjectContainer.configure
		/// 	</see>
		/// ) will
		/// continually allocate space.
		/// <br /><br />The allocation of a fixed number of bytes at one time
		/// makes it more likely that the database will be stored in one
		/// chunk on the mass storage. Less read/write head movevement can result
		/// in improved performance.<br /><br />
		/// <b>Note:</b><br /> Allocated space will be lost on abnormal termination
		/// of the database engine (hardware crash, VM crash). A Defragment run
		/// will recover the lost space. For the best possible performance, this
		/// method should be called before the Defragment run to configure the
		/// allocation of storage space to be slightly greater than the anticipated
		/// database file size.
		/// <br /><br /> Default configuration: 0<br /><br />
		/// </remarks>
		/// <param name="byteCount">the number of bytes to reserve</param>
		void reserveStorageSpace(long byteCount);

		/// <summary>
		/// configures the path to be used to store and read
		/// Blob data.
		/// </summary>
		/// <remarks>
		/// configures the path to be used to store and read
		/// Blob data.
		/// <br /><br />
		/// </remarks>
		/// <param name="path">the path to be used</param>
		void setBlobPath(string path);

		/// <summary>configures db4o to use a custom ClassLoader.</summary>
		/// <remarks>
		/// configures db4o to use a custom ClassLoader.
		/// <br /><br />
		/// </remarks>
		/// <param name="classLoader">the ClassLoader to be used</param>
		void setClassLoader(j4o.lang.ClassLoader classLoader);

		/// <summary>sets the MessageRecipient to receive Client Server messages.</summary>
		/// <remarks>
		/// sets the MessageRecipient to receive Client Server messages.
		/// <br /><br />
		/// </remarks>
		/// <param name="messageRecipient">the MessageRecipient to be used</param>
		void setMessageRecipient(com.db4o.messaging.MessageRecipient messageRecipient);

		/// <summary>assigns a <code>PrintStream</code> where db4o is to print its event messages.
		/// 	</summary>
		/// <remarks>
		/// assigns a <code>PrintStream</code> where db4o is to print its event messages.
		/// <br /><br />Messages are useful for debugging purposes and for learning
		/// to understand, how db4o works. The message level can be raised with
		/// <see cref="com.db4o.config.Configuration.messageLevel">Db4o.configure().messageLevel()
		/// 	</see>
		/// to produce more detailed messages.
		/// <br /><br />Use <code>setOut(System.out)</code> to print messages to the
		/// console.<br /><br />
		/// </remarks>
		/// <param name="outStream">the new <code>PrintStream</code> for messages.</param>
		void setOut(j4o.io.PrintStream outStream);

		/// <summary>
		/// configures the client messaging system to be single threaded
		/// or multithreaded.
		/// </summary>
		/// <remarks>
		/// configures the client messaging system to be single threaded
		/// or multithreaded.
		/// <br /><br />Recommended settings:<br />
		/// - <code>true</code> for low ressource systems.<br />
		/// - <code>false</code> for best asynchronous performance and fast
		/// GUI response.
		/// <br /><br />Default value:<br />
		/// - .NET Compactframework: <code>true</code><br />
		/// - all other plaforms: <code>false</code><br /><br />
		/// </remarks>
		/// <param name="flag">the desired setting</param>
		void singleThreadedClient(bool flag);

		/// <summary>
		/// tuning feature: configures whether db4o should try to instantiate one instance
		/// of each persistent class on system startup.
		/// </summary>
		/// <remarks>
		/// tuning feature: configures whether db4o should try to instantiate one instance
		/// of each persistent class on system startup.
		/// <br /><br />In a production environment this setting can be set to <code>false</code>,
		/// if all persistent classes have public default constructors.
		/// <br /><br />Default value:<br />
		/// <code>true</code>
		/// </remarks>
		/// <param name="flag">the desired setting</param>
		void testConstructors(bool flag);

		/// <summary>
		/// configures the time a client waits for a message
		/// response from the server.
		/// </summary>
		/// <remarks>
		/// configures the time a client waits for a message
		/// response from the server.
		/// <br /><br />Default value: 300000ms (5 minutes)<br /><br />
		/// </remarks>
		/// <param name="milliseconds">time in milliseconds</param>
		void timeoutClientSocket(int milliseconds);

		/// <summary>configures the timeout of the serverside socket.</summary>
		/// <remarks>
		/// configures the timeout of the serverside socket.
		/// <br /><br />All server connection threads jump out of the
		/// socket read statement on a regular interval to check
		/// if the server was shut down. Use this method to configure
		/// the duration of the interval.<br /><br />
		/// Default value: 5000ms (5 seconds)<br /><br />
		/// </remarks>
		/// <param name="milliseconds">time in milliseconds</param>
		void timeoutServerSocket(int milliseconds);

		/// <summary>
		/// configures the delay time after which the server starts pinging
		/// connected clients to check the connection.
		/// </summary>
		/// <remarks>
		/// configures the delay time after which the server starts pinging
		/// connected clients to check the connection.
		/// <br /><br />If no client messages are received by the server for the
		/// configured interval, the server sends a "PING" message to the
		/// client and wait's for an "OK" response. After 5 unsuccessful
		/// attempts, the client connection is closed.
		/// <br /><br />This value may need to be increased for single-threaded
		/// clients, since they can't respond instantaneously.
		/// <br /><br />Default value: 180000ms (3 minutes)<br /><br />
		/// </remarks>
		/// <param name="milliseconds">time in milliseconds</param>
		/// <seealso cref="com.db4o.config.Configuration.singleThreadedClient"></seealso>
		void timeoutPingClients(int milliseconds);

		/// <summary>configures the storage format of Strings.</summary>
		/// <remarks>
		/// configures the storage format of Strings.
		/// <br /><br />This method needs to be called <b>before</b> a database file
		/// is created with the first
		/// <see cref="com.db4o.Db4o.openFile">Db4o.openFile()</see>
		/// .
		/// db4o database files keep their string format after creation.<br /><br />
		/// Turning Unicode support off reduces the file storage space for strings
		/// by factor 2 and improves performance.<br /><br />
		/// Default setting: <b>true</b><br /><br />
		/// </remarks>
		/// <param name="flag">
		/// <code>true</code> for turning Unicode support on, <code>false</code> for turning
		/// Unicode support off.
		/// </param>
		void unicode(bool flag);

		/// <summary>specifies the global updateDepth.</summary>
		/// <remarks>
		/// specifies the global updateDepth.
		/// <br /><br />see the documentation of
		/// <see cref="com.db4o.ObjectContainer.set">ObjectContainer.set()</see>
		/// for further details.<br /><br />
		/// The value be may be overridden for individual classes.<br /><br />
		/// The default setting is 1: Only the object passed to
		/// <see cref="com.db4o.ObjectContainer.set">ObjectContainer.set()</see>
		/// will be updated.<br /><br />
		/// </remarks>
		/// <param name="depth">the depth of the desired update.</param>
		/// <seealso cref="com.db4o.config.ObjectClass.updateDepth">com.db4o.config.ObjectClass.updateDepth
		/// 	</seealso>
		/// <seealso cref="com.db4o.config.ObjectClass.cascadeOnUpdate">com.db4o.config.ObjectClass.cascadeOnUpdate
		/// 	</seealso>
		/// <seealso cref="com.db4o.ext.ObjectCallbacks">Using callbacks</seealso>
		void updateDepth(int depth);

		/// <summary>turns weak reference management on or off.</summary>
		/// <remarks>
		/// turns weak reference management on or off.
		/// <br /><br />
		/// Performance may be improved by running db4o without weak
		/// reference memory management at the cost of higher
		/// memory consumption or by alternatively implementing a manual
		/// memory management using
		/// <see cref="com.db4o.ext.ExtObjectContainer.purge">com.db4o.ext.ExtObjectContainer.purge
		/// 	</see>
		/// <br /><br />The default setting is <code>true</code>.
		/// <br /><br />Ignored on JDKs before 1.2.
		/// </remarks>
		void weakReferences(bool flag);

		/// <summary>configures the timer for WeakReference collection.</summary>
		/// <remarks>
		/// configures the timer for WeakReference collection.
		/// <br /><br />The default setting is 1000 milliseconds.
		/// <br /><br />Configure this setting to zero to turn WeakReference
		/// collection off.
		/// <br /><br />Ignored on JDKs before 1.2.<br /><br />
		/// </remarks>
		/// <param name="milliseconds">the time in milliseconds</param>
		void weakReferenceCollectionInterval(int milliseconds);
	}
}
