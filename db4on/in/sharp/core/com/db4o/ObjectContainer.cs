namespace com.db4o
{
	/// <summary>the interface to a db4o database, stand-alone or client/server.</summary>
	/// <remarks>
	/// the interface to a db4o database, stand-alone or client/server.
	/// <br /><br />The ObjectContainer interface provides methods
	/// to store, query and delete objects and to commit and rollback
	/// transactions.<br /><br />
	/// An ObjectContainer can either represent a stand-alone database
	/// or a connection to a
	/// <see cref="com.db4o.Db4o.openServer">db4o server</see>
	/// .
	/// <br /><br />An ObjectContainer also represents a transaction. All work
	/// with db4o always is transactional. Both
	/// <see cref="com.db4o.ObjectContainer.commit">com.db4o.ObjectContainer.commit</see>
	/// and
	/// <see cref="com.db4o.ObjectContainer.rollback">com.db4o.ObjectContainer.rollback</see>
	/// start new transactions immediately. For working
	/// against the same database with multiple transactions, open a db4o server
	/// with
	/// <see cref="com.db4o.Db4o.openServer">com.db4o.Db4o.openServer</see>
	/// and
	/// <see cref="com.db4o.ObjectServer.openClient">connect locally</see>
	/// or
	/// <see cref="com.db4o.Db4o.openClient">over TCP</see>
	/// .
	/// </remarks>
	/// <seealso cref="com.db4o.ext.ExtObjectContainer">ExtObjectContainer for extended functionality.
	/// 	</seealso>
	public interface ObjectContainer : System.IDisposable
	{
		/// <summary>activates all members on a stored object to the specified depth.</summary>
		/// <remarks>
		/// activates all members on a stored object to the specified depth.
		/// <br /><br />
		/// See
		/// <see cref="com.db4o.config.Configuration.activationDepth">"Why activation"</see>
		/// for an explanation why activation is necessary.<br /><br />
		/// The activate method activates a graph of persistent objects in memory.
		/// Only deactivated objects in the graph will be touched: their
		/// fields will be loaded from the database.
		/// The activate methods starts from a
		/// root object and traverses all member objects to the depth specified by the
		/// depth parameter. The depth parameter is the distance in "field hops"
		/// (object.field.field) away from the root object. The nodes at 'depth' level
		/// away from the root (for a depth of 3: object.member.member) will be instantiated
		/// but deactivated, their fields will be null.
		/// The activation depth of individual classes can be overruled
		/// with the methods
		/// <see cref="com.db4o.config.ObjectClass.maximumActivationDepth">MaximumActivationDepth()
		/// 	</see>
		/// and
		/// <see cref="com.db4o.config.ObjectClass.minimumActivationDepth">MinimumActivationDepth()
		/// 	</see>
		/// in the
		/// <see cref="com.db4o.config.ObjectClass">ObjectClass interface</see>
		/// .<br /><br />
		/// A successful call to activate triggers the callback method
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
		void Activate(object obj, int depth);

		/// <summary>closes this ObjectContainer.</summary>
		/// <remarks>
		/// closes this ObjectContainer.
		/// <br /><br />A call to Close() automatically performs a
		/// <see cref="com.db4o.ObjectContainer.commit">Commit()</see>
		/// .
		/// <br /><br />Note that every session opened with Db4o.OpenFile() requires one
		/// Close()call, even if the same filename was used multiple times.<br /><br />
		/// Use <code>while(!Close()){}</code> to kill all sessions using this container.<br /><br />
		/// </remarks>
		/// <returns>
		/// success - true denotes that the last used instance of this container
		/// and the database file were closed.
		/// </returns>
		bool Close();

		/// <summary>commits the running transaction.</summary>
		/// <remarks>
		/// commits the running transaction.
		/// <br /><br />Transactions are back-to-back. A call to commit will starts
		/// a new transaction immedidately.
		/// </remarks>
		void Commit();

		/// <summary>deactivates a stored object by setting all members to <code>NULL</code>.
		/// 	</summary>
		/// <remarks>
		/// deactivates a stored object by setting all members to <code>NULL</code>.
		/// <br />Primitive types will be set to their default values.
		/// <br /><br /><b>Examples: ../com/db4o/samples/activate.</b><br /><br />
		/// Calls to this method save memory.
		/// The method has no effect, if the passed object is not stored in the
		/// <code>ObjectContainer</code>.<br /><br />
		/// <code>Deactivate()</code> triggers the callback method
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
		void Deactivate(object obj, int depth);

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
		/// <code>Set()</code> with the same object newly stores the object
		/// to the <code>ObjectContainer</code>.<br /><br />
		/// <code>Delete()</code> triggers the callback method
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
		void Delete(object obj);

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
		com.db4o.ext.ExtObjectContainer Ext();

		/// <summary>Query-By-Example interface to retrieve objects.</summary>
		/// <remarks>
		/// Query-By-Example interface to retrieve objects.
		/// <br /><br /><code>Get()</code> creates an
		/// <see cref="com.db4o.ObjectSet">ObjectSet</see>
		/// containing
		/// all objects in the <code>ObjectContainer</code> that match the passed
		/// template object.<br /><br />
		/// Calling <code>Get(NULL)</code> returns all objects stored in the
		/// <code>ObjectContainer</code>.<br /><br /><br />
		/// <b>Query Evaluation</b>
		/// <br />All non-null members of the template object are compared against
		/// all stored objects of the same class.
		/// Primitive type members are ignored if they are 0 or false respectively.
		/// <br /><br />Arrays and all supported <code>Collection</code> classes are
		/// evaluated for containment. Differences in <code>length/Size()</code> are
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
		/// Objects newly activated by <code>Get()</code> can respond to the callback
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
		com.db4o.ObjectSet Get(object template);

		/// <summary>
		/// creates a new SODA
		/// <see cref="com.db4o.query.Query">Query</see>
		/// .
		/// <br /><br />
		/// Use
		/// <see cref="com.db4o.ObjectContainer.get">Get(Object template)</see>
		/// for simple Query-By-Example.<br /><br />
		/// <see cref="com.db4o.ObjectContainer.query">Native queries</see>
		/// are the recommended main db4o query
		/// interface.
		/// <br /><br />
		/// </summary>
		/// <returns>a new Query object</returns>
		com.db4o.query.Query Query();

		/// <summary>queries for all instances of a class.</summary>
		/// <remarks>queries for all instances of a class.</remarks>
		/// <param name="clazz">the class to query for.</param>
		/// <returns>
		/// the
		/// <see cref="com.db4o.ObjectSet">com.db4o.ObjectSet</see>
		/// returned by the query.
		/// </returns>
		com.db4o.ObjectSet Query(System.Type clazz);

		/// <summary>Native Query Interface.</summary>
		/// <remarks>
		/// Native Query Interface.
		/// <br /><br />Native Queries allow typesafe, compile-time checked and refactorable
		/// querying, following object-oriented principles. Native Queries expressions
		/// are written as if one or more lines of code would be run against all
		/// instances of a class. A Native Query expression should return true to mark
		/// specific instances as part of the result set.
		/// db4o will  attempt to optimize native query expressions and execute them
		/// against indexes and without instantiating actual objects, where this is
		/// possible.<br /><br />
		/// The syntax of the enclosing object for the native query expression varies,
		/// depending on the language version used. Here are some examples,
		/// how a simple native query will look like in some of the programming languages
		/// and dialects that db4o supports:<br /><br />
		/// <code>
		/// <b>// C# .NET 2.0</b><br />
		/// IList &lt;Cat&gt; cats = db.Query &lt;Cat&gt; (delegate(Cat cat) {<br />
		/// &#160;&#160;&#160;return cat.Name == "Occam";<br />
		/// });<br />
		/// <br />
		/// <br />
		/// <b>// Java JDK 5</b><br />
		/// List &lt;Cat&gt; cats = db.Query(new Predicate&lt;Cat&gt;() {<br />
		/// &#160;&#160;&#160;public boolean Match(Cat cat) {<br />
		/// &#160;&#160;&#160;&#160;&#160;&#160;return cat.GetName().Equals("Occam");<br />
		/// &#160;&#160;&#160;}<br />
		/// });<br />
		/// <br />
		/// <br />
		/// <b>// Java JDK 1.2 to 1.4</b><br />
		/// List cats = db.Query(new Predicate() {<br />
		/// &#160;&#160;&#160;public boolean Match(Cat cat) {<br />
		/// &#160;&#160;&#160;&#160;&#160;&#160;return cat.GetName().Equals("Occam");<br />
		/// &#160;&#160;&#160;}<br />
		/// });<br />
		/// <br />
		/// <br />
		/// <b>// Java JDK 1.1</b><br />
		/// ObjectSet cats = db.Query(new CatOccam());<br />
		/// <br />
		/// public static class CatOccam extends Predicate {<br />
		/// &#160;&#160;&#160;public boolean Match(Cat cat) {<br />
		/// &#160;&#160;&#160;&#160;&#160;&#160;return cat.GetName().Equals("Occam");<br />
		/// &#160;&#160;&#160;}<br />
		/// });<br />
		/// <br />
		/// <br />
		/// <b>// C# .NET 1.1</b><br />
		/// IList cats = db.Query(new CatOccam());<br />
		/// <br />
		/// public class CatOccam : Predicate {<br />
		/// &#160;&#160;&#160;public boolean Match(Cat cat) {<br />
		/// &#160;&#160;&#160;&#160;&#160;&#160;return cat.Name == "Occam";<br />
		/// &#160;&#160;&#160;}<br />
		/// });<br />
		/// </code>
		/// <br />
		/// Summing up the above:<br />
		/// In order to run a Native Query, you can<br />
		/// - use the delegate notation for .NET 2.0.<br />
		/// - extend the Predicate class for all other language dialects<br /><br />
		/// A class that extends Predicate is required to
		/// implement the #Match() / #Match() method, following the native query
		/// conventions:<br />
		/// - The name of the method is "#Match()" (Java) / "#Match()" (.NET).<br />
		/// - The method must be public public.<br />
		/// - The method returns a boolean.<br />
		/// - The method takes one parameter.<br />
		/// - The Type (.NET) / Class (Java) of the parameter specifies the extent.<br />
		/// - For all instances of the extent that are to be included into the
		/// resultset of the query, the match method should return true. For all
		/// instances that are not to be included, the match method should return
		/// false.<br /><br />
		/// </remarks>
		/// <param name="predicate">
		/// the
		/// <see cref="com.db4o.query.Predicate">com.db4o.query.Predicate</see>
		/// containing the native query expression.
		/// </param>
		/// <returns>
		/// the
		/// <see cref="com.db4o.ObjectSet">com.db4o.ObjectSet</see>
		/// returned by the query.
		/// </returns>
		com.db4o.ObjectSet Query(com.db4o.query.Predicate predicate);

		/// <summary>Native Query Interface.</summary>
		/// <remarks>
		/// Native Query Interface. Queries as with
		/// <see cref="M:com.db4o.ObjectContainer.Query(com.db4o.query.Predicate)">com.db4o.ObjectContainer.Query(Predicate)</see>
		/// ,
		/// but will sort the resulting
		/// <see cref="com.db4o.ObjectSet">com.db4o.ObjectSet</see>
		/// according to the given
		/// <see cref="com.db4o.query.QueryComparator">com.db4o.query.QueryComparator</see>
		/// .
		/// </remarks>
		/// <param name="predicate">
		/// the
		/// <see cref="com.db4o.query.Predicate">com.db4o.query.Predicate</see>
		/// containing the native query expression.
		/// </param>
		/// <param name="comparator">
		/// the
		/// <see cref="com.db4o.query.QueryComparator">com.db4o.query.QueryComparator</see>
		/// specifiying the sort order of the result
		/// </param>
		/// <returns>
		/// the
		/// <see cref="com.db4o.ObjectSet">com.db4o.ObjectSet</see>
		/// returned by the query.
		/// </returns>
		com.db4o.ObjectSet Query(com.db4o.query.Predicate predicate, com.db4o.query.QueryComparator
			 comparator);

		/// <summary>Native Query Interface.</summary>
		/// <remarks>
		/// Native Query Interface. Queries as with
		/// <see cref="M:com.db4o.ObjectContainer.Query(com.db4o.query.Predicate)">com.db4o.ObjectContainer.Query(Predicate)</see>
		/// ,
		/// but will sort the resulting
		/// <see cref="com.db4o.ObjectSet">com.db4o.ObjectSet</see>
		/// according to the given
		/// <see cref="System.Collections.IComparer">System.Collections.IComparer</see>
		/// .
		/// </remarks>
		/// <param name="predicate">
		/// the
		/// <see cref="com.db4o.query.Predicate">com.db4o.query.Predicate</see>
		/// containing the native query expression.
		/// </param>
		/// <param name="comparator">
		/// the
		/// <see cref="System.Collections.IComparer">System.Collections.IComparer</see>
		/// specifiying the sort order of the result
		/// </param>
		/// <returns>
		/// the
		/// <see cref="com.db4o.ObjectSet">com.db4o.ObjectSet</see>
		/// returned by the query.
		/// </returns>
		com.db4o.ObjectSet Query(com.db4o.query.Predicate predicate, System.Collections.IComparer comparer);

		/// <summary>rolls back the running transaction.</summary>
		/// <remarks>
		/// rolls back the running transaction.
		/// <br /><br />Transactions are back-to-back. A call to rollback will starts
		/// a new transaction immedidately.
		/// <br /><br />rollback will not restore modified objects in memory. They
		/// can be refreshed from the database by calling
		/// <see cref="com.db4o.ext.ExtObjectContainer.refresh">com.db4o.ext.ExtObjectContainer.refresh
		/// 	</see>
		/// .
		/// </remarks>
		void Rollback();

		/// <summary>newly stores objects or updates stored objects.</summary>
		/// <remarks>
		/// newly stores objects or updates stored objects.
		/// <br /><br />An object not yet stored in the <code>ObjectContainer</code> will be
		/// stored when it is passed to <code>Set()</code>. An object already stored
		/// in the <code>ObjectContainer</code> will be updated.
		/// <br /><br /><b>Updates</b><br />
		/// - will affect all simple type object members.<br />
		/// - links to object members that are already stored will be updated.<br />
		/// - new object members will be newly stored. The algorithm traverses down
		/// new members, as long as further new members are found.<br />
		/// - object members that are already stored will <b>not</b> be updated
		/// themselves.<br />Every object member needs to be updated individually with a
		/// call to <code>Set()</code> unless a deep
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
		/// <seealso cref="com.db4o.ext.ExtObjectContainer.set">ExtObjectContainer#Set(object, depth)
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
		void Set(object obj);
		
#if NET_2_0 || CF_2_0
        /// <summary>.NET 2.0 Native Query interface.</summary>
        /// <remarks>
        /// Native Query Interface.
        /// <br /><br />Native Queries allow typesafe, compile-time checked and refactorable
        /// querying, following object-oriented principles. Native Queries expressions
        /// are written as if one or more lines of code would be run against all
        /// instances of a class. A Native Query expression should return true to mark
        /// specific instances as part of the result set.
        /// db4o will  attempt to optimize native query expressions and execute them
        /// against indexes and without instantiating actual objects, where this is
        /// possible.<br /><br />
        /// The syntax of the enclosing object for the native query expression varies,
        /// depending on the language version used. Here are some examples,
        /// how a simple native query will look like in some of the programming languages
        /// and dialects that db4o supports:<br /><br />
        /// <code>
        /// <b>// C# .NET 2.0</b><br />
        /// IList &lt;Cat&gt; cats = db.Query &lt;Cat&gt; (delegate(Cat cat) {<br />
        /// &#160;&#160;&#160;return cat.Name == "Occam";<br />
        /// });<br />
        /// <br />
        /// <br />
        /// <b>// Java JDK 5</b><br />
        /// List &lt;Cat&gt; cats = db.Query(new Predicate&lt;Cat&gt;() {<br />
        /// &#160;&#160;&#160;public boolean Match(Cat cat) {<br />
        /// &#160;&#160;&#160;&#160;&#160;&#160;return cat.GetName().Equals("Occam");<br />
        /// &#160;&#160;&#160;}<br />
        /// });<br />
        /// <br />
        /// <br />
        /// <b>// Java JDK 1.2 to 1.4</b><br />
        /// List cats = db.Query(new Predicate() {<br />
        /// &#160;&#160;&#160;public boolean Match(Cat cat) {<br />
        /// &#160;&#160;&#160;&#160;&#160;&#160;return cat.GetName().Equals("Occam");<br />
        /// &#160;&#160;&#160;}<br />
        /// });<br />
        /// <br />
        /// <br />
        /// <b>// Java JDK 1.1</b><br />
        /// ObjectSet cats = db.Query(new CatOccam());<br />
        /// <br />
        /// public static class CatOccam extends Predicate {<br />
        /// &#160;&#160;&#160;public boolean Match(Cat cat) {<br />
        /// &#160;&#160;&#160;&#160;&#160;&#160;return cat.GetName().Equals("Occam");<br />
        /// &#160;&#160;&#160;}<br />
        /// });<br />
        /// <br />
        /// <br />
        /// <b>// C# .NET 1.1</b><br />
        /// IList cats = db.Query(new CatOccam());<br />
        /// <br />
        /// public class CatOccam : Predicate {<br />
        /// &#160;&#160;&#160;public boolean Match(Cat cat) {<br />
        /// &#160;&#160;&#160;&#160;&#160;&#160;return cat.Name == "Occam";<br />
        /// &#160;&#160;&#160;}<br />
        /// });<br />
        /// </code>
        /// <br />
        /// Summing up the above:<br />
        /// In order to run a Native Query, you can<br />
        /// - use the delegate notation for .NET 2.0.<br />
        /// - extend the Predicate class for all other language dialects<br /><br />
        /// A class that extends Predicate is required to
        /// implement the #Match() / #Match() method, following the native query
        /// conventions:<br />
        /// - The name of the method is "#Match()" (Java) / "#Match()" (.NET).<br />
        /// - The method must be public public.<br />
        /// - The method returns a boolean.<br />
        /// - The method takes one parameter.<br />
        /// - The Type (.NET) / Class (Java) of the parameter specifies the extent.<br />
        /// - For all instances of the extent that are to be included into the
        /// resultset of the query, the match method should return true. For all
        /// instances that are not to be included, the match method should return
        /// false.<br /><br />
        /// </remarks>
        /// <param name="match">
        /// use an anonymous delegate that takes a single paramter and returns
        /// a bool value, see the syntax example above
        /// </param>
        /// <returns>
        /// the
        /// <see cref="com.db4o.ObjectSet">com.db4o.ObjectSet</see>
        /// returned by the query.
        /// </returns>
        System.Collections.Generic.IList<Extent> Query<Extent>(System.Predicate<Extent> match);
        
		/// <summary>Native Query Interface.</summary>
		/// <remarks>
		/// Native Query Interface. Queries as with
		/// <see cref="M:com.db4o.ObjectContainer.Query(com.db4o.query.Predicate)">com.db4o.ObjectContainer.Query(Predicate)</see>
		/// ,
		/// but will sort the resulting
		/// <see cref="com.db4o.ObjectSet">com.db4o.ObjectSet</see>
		/// according to the given
		/// <see cref="System.Collections.Generic.IComparer">System.Collections.Generic.IComparer</see>
		/// .
		/// </remarks>
		/// <param name="predicate">
		/// the
		/// <see cref="com.db4o.query.Predicate">com.db4o.query.Predicate</see>
		/// containing the native query expression.
		/// </param>
		/// <param name="comparator">
		/// the
		/// <see cref="System.Collections.Generic.IComparer">System.Collections.Generic.IComparer</see>
		/// specifiying the sort order of the result
		/// </param>
		/// <returns>
		/// the
		/// <see cref="com.db4o.ObjectSet">com.db4o.ObjectSet</see>
		/// returned by the query.
		/// </returns>
		System.Collections.Generic.IList<Extent> Query<Extent>(System.Predicate<Extent> match, System.Collections.Generic.IComparer<Extent> comparer);

		/// <summary>Native Query Interface.</summary>
		/// <remarks>
		/// Native Query Interface. Queries as with
		/// <see cref="M:com.db4o.ObjectContainer.Query(com.db4o.query.Predicate)">com.db4o.ObjectContainer.Query(Predicate)</see>
		/// ,
		/// but will sort the resulting
		/// <see cref="com.db4o.ObjectSet">com.db4o.ObjectSet</see>
		/// according to the given
		/// <see cref="System.Comparison">System.Comparison</see>
		/// .
		/// </remarks>
		/// <param name="predicate">
		/// the
		/// <see cref="com.db4o.query.Predicate">com.db4o.query.Predicate</see>
		/// containing the native query expression.
		/// </param>
		/// <param name="comparator">
		/// the
		/// <see cref="System.Comparison">System.Comparison</see>
		/// specifiying the sort order of the result
		/// </param>
		/// <returns>
		/// the
		/// <see cref="com.db4o.ObjectSet">com.db4o.ObjectSet</see>
		/// returned by the query.
		/// </returns>
		System.Collections.Generic.IList<Extent> Query<Extent>(System.Predicate<Extent> match, System.Comparison<Extent> comparison);

		/// <summary>
		/// queries for all instances of the type extent, returning
		/// a IList of ElementType which must be assignable from
		/// extent.
		/// </summary>
		System.Collections.Generic.IList<ElementType> Query<ElementType>(System.Type extent);
		
		/// <summary>
		/// queries for all instances of the type extent.
		/// </summary>
		System.Collections.Generic.IList<Extent> Query<Extent>();

		/// <summary>
		/// queries for all instances of the type extent sorting with the specified comparer.
		/// </summary>
		System.Collections.Generic.IList<Extent> Query<Extent>(System.Collections.Generic.IComparer<Extent> comparer);
#endif
	}
}
