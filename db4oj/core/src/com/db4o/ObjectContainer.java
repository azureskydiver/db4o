/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package  com.db4o;

import com.db4o.ext.*;
import com.db4o.query.*;


/**
 * storage and query interface.
 * <br><br>The <code>ObjectContainer</code> interface provides methods
 * to store, retrieve and delete objects and to commit and rollback
 * transactions.
 * @see <a href="ext/ExtObjectContainer.html">ExtObjectContainer</a> for
 * extended functionality.
 */
public interface ObjectContainer {
	
    /**
     * activates all members on a stored object to the specified depth.
	 * <br><br><b>Examples: ../com/db4o/samples/activate.</b><br><br>
     * This method serves to traverse the graph of persistent objects.
     * All members of an object can be activated in turn with subsequent calls.<br><br>
     * Only objects in <code>DEACTIVATED</code> state are modified.
     * <code>Object</code> members at the specified depth are
     * instantiated in <code>DEACTIVATED</code> state.
     * <br><br>Duplicate <code>activate()</code> calls on the same object have no effect.
     * Passing an object that is not stored in the <code>ObjectContainer
     * </code> has no effect.<br><br>
     * The activation depth of individual classes can be overruled
     * with the methods
     * <a href="config/ObjectClass.html#maximumActivationDepth(int)">
     * <code>maximumActivationDepth()</code></a> and
     * <a href="config/ObjectClass.html#minimumActivationDepth(int)">
     * <code>minimumActivationDepth()</code></a> in the
     * <a href="config/ObjectClass.html">
     * <code>ObjectClass</code></a> interface.<br><br>
     * A successful <code>activate()</code> triggers the callback method
     * <a href="ext/ObjectCallbacks.html#objectOnActivate(com.db4o.ObjectContainer)">
     * <code>objectOnActivate</code></a> which can be used for cascaded activation.<br><br>
	 * @see <a href="config/Configuration.html#activationDepth(int)">
	 * Why activation?</a>
	 * @see <br><a href="ext/ObjectCallbacks.html">Using callbacks</a>
     * @param Object the object to be activated.
	 * @param depth the member 
	 * <a href="config/Configuration.html#activationDepth(int)">depth</a>
	 *  to which activate is to cascade.
     */
    public void activate (Object obj, int depth);
    
    /**
     * closes the <code>ObjectContainer</code>.
     * <br><br>A call to <code>close()</code> automatically performs a 
     * <a href=#commit()><code>commit()</code></a>.
     * <br><br>Note that every session opened with Db4o.openFile() requires one
     * close()call, even if the same filename was used multiple times.<br><br>
     * Use <code>while(!close()){}</code> to kill all sessions using this container.<br><br>
     * @return success - true denotes that the last used instance of this container
     * and the database file were closed.
     */
	public boolean close ();

    /**
     * commits the running transaction.
     */
    public void commit ();
    

    /**
     * deactivates a stored object by setting all members to <code>NULL</code>.
     * <br>Primitive types will be set to their default values.
     * <br><br><b>Examples: ../com/db4o/samples/activate.</b><br><br>
     * Calls to this method save memory.
     * The method has no effect, if the passed object is not stored in the
     * <code>ObjectContainer</code>.<br><br>
     * <code>deactivate()</code> triggers the callback method
     * <a href="ext/ObjectCallbacks.html#objectOnDeactivate(com.db4o.ObjectContainer)">
     * <code>objectOnDeactivate</code></a>.<br><br>
     * Be aware that calling this method with a depth parameter greater than 
     * 1 sets members on member objects to null. This may have side effects 
     * in other places of the application.<br><br>
	 * @see <a href="ext/ObjectCallbacks.html">Using callbacks</a>
  	 * @see <a href="config/Configuration.html#activationDepth(int)">
	 * Why activation?</a>
     * @param obj the object to be deactivated.
	 * @param depth the member 
	 * <a href="config/Configuration.html#activationDepth(int)">depth</a> 
	 * to which deactivate is to cascade.
	*/
    public void deactivate (Object obj, int depth);

    /**
     * deletes a stored object permanently.
     * <br><br>Note that this method has to be called <b>for every single object
     * individually</b>. Delete does not recurse to object members. Simple
     * and array member types are destroyed.
     * <br><br>Object members of the passed object remain untouched, unless
     * cascaded deletes are  
     * <a href="config/ObjectClass.html#cascadeOnDelete(boolean)">
     * configured for the class</a>
     * or for <a href="config/ObjectField.html#cascadeOnDelete(boolean)">
     * one of the member fields</a>.
     * <br><br>The method has no effect, if
     * the passed object is not stored in the <code>ObjectContainer</code>.
     * <br><br>A subsequent call to
     * <code>set()</code> with the same object newly stores the object
     * to the <code>ObjectContainer</code>.<br><br>
     * <code>delete()</code> triggers the callback method
     * <a href="ext/ObjectCallbacks.html#objectOnDelete(com.db4o.ObjectContainer)">
     * <code>objectOnDelete</code></a> which can be also used for cascaded deletes.<br><br>
	 * @see <a href="config/ObjectClass.html#cascadeOnDelete(boolean)">
	 * <code>ObjectClass#cascadeOnDelete</code></a>
	 * @see <a href="config/ObjectField.html#cascadeOnDelete(boolean)">
	 * <code>ObjectField#cascadeOnDelete</code></a>
	 * @see <a href="ext/ObjectCallbacks.html">Using callbacks</a>
     * @param object the object to be deleted from the
     * <code>ObjectContainer</code>.<br>
     */
    public void delete (Object obj);
    
    /**
     * returns an ObjectContainer with extended functionality.
     * <br><br>Every ObjectContainer that db4o provides can be casted to
     * an ExtObjectContainer. This method is supplied for your convenience
     * to work without a cast.
     * <br><br>The ObjectContainer functionality is split to two interfaces
     * to allow newcomers to focus on the essential methods.<br><br>
     * @return this, casted to ExtObjectContainer
     */
    public ExtObjectContainer ext();
	
    /**
     * Query-By-Example interface to retrieve objects.
     * <br><br><code>get()</code> creates an
     * <a href="ObjectSet.html"><code>ObjectSet</code></a> containing
     * all objects in the <code>ObjectContainer</code> that match the passed
     * template object.<br><br>
	 * Calling <code>get(NULL)</code> returns all objects stored in the
     * <code>ObjectContainer</code>.<br><br><br>
     * <b>Query Evaluation</b>
     * <br>All non-null members of the template object are compared against
     * all stored objects of the same class.
     * Primitive type members are ignored if they are 0 or false respectively.
     * <br><br>Arrays and all supported <code>Collection</code> classes are
     * evaluated for containment. Differences in <code>length/size()</code> are
     * ignored.
     * <br><br>Consult the documentation of the
     * <a href="config\package-summary.html">Configuration package</a> to
     * configure class-specific behaviour.<br><br><br>
     * <b>Returned Objects</b><br>
     * The objects returned in the
     * <a href="ObjectSet.html"><code>ObjectSet</code></a> are instantiated
     * and activated to the preconfigured depth of 5. The
	 * <a href="config/Configuration.html#activationDepth(int)">activation depth</a>
	 *  may be configured
     * <a href="config/Configuration.html#activationDepth(int)">globally</a> or
     * <a href="config\ObjectClass.html">individually for classes</a>.
	 * <br><br>
     * db4o keeps track of all instantiatied objects. Queries will return
     * references to these objects instead of instantiating them a second time.
     * <br><br>
     * Objects newly activated by <code>get()</code> can respond to the callback
     * method <a href="ext/ObjectCallbacks.html#objectOnActivate(com.db4o.ObjectContainer)">
     * <code>objectOnActivate</code></a>.<br><br>
     * @param template object to be used as an example to find all matching objects.<br><br>
     * @return <a href="ObjectSet.html"><code>ObjectSet</code></a>
	 * containing all found objects.<br><br>
	 * @see <a href="config/Configuration.html#activationDepth(int)">
	 * Why activation?</a>
	 * @see <br><a href="ext/ObjectCallbacks.html">Using callbacks</a>
	 */
    public ObjectSet get (Object template);
    
    /**
     * factory method to create a new <a href="query/Query.html">
     * <code>Query</code></a> object.
     * <br><br>
     * Use <a href="#get(java.lang.Object)"><code>get(Object template)</code></a> for
     * simple Query-By-Example.
     * <br><br>
     * @return a new Query object
     */
    public Query query ();
    
    
    /**
     * rolls back the running transaction.
     * <br><br>Modified application objects im memory are not restored.
     * Use combined calls to 
     * <a href="#deactivate(java.lang.Object, int)"><code>deactivate()</code></a>
     * and
     * <a href="#activate(java.lang.Object, int)"><code>activate()</code></a>
     * to reload an objects member values.
     */
    public void rollback();
    
    /**
     * newly stores objects or updates stored objects.
     * <br><br>An object not yet stored in the <code>ObjectContainer</code> will be
     * stored when it is passed to <code>set()</code>. An object already stored
     * in the <code>ObjectContainer</code> will be updated.
     * <br><br><b>Updates</b><br>
	 * - will affect all simple type object members.<br>
     * - links to object members that are already stored will be updated.<br>
	 * - new object members will be newly stored. The algorithm traverses down
	 * new members, as long as further new members are found.<br>
     * - object members that are already stored will <b>not</b> be updated
     * themselves.<br>Every object member needs to be updated individually with a
	 * call to <code>set()</code> unless a deep
	 * <a href="config/Configuration.html#updateDepth(int)">global</a> or 
     * <a href="config/ObjectClass.html#updateDepth(int)">class-specific</a>
     * update depth was configured or cascaded updates were 
     * <a href="config/ObjectClass.html#cascadeOnUpdate(boolean)">
     * defined in the class</a>
     * or in <a href="config/ObjectField.html#cascadeOnUpdate(boolean)">
     * one of the member fields</a>.
     * <br><br><b>Examples: ../com/db4o/samples/update.</b><br><br>
     * Depending if the passed object is newly stored or updated, the
     * callback method
     * <a href="ext/ObjectCallbacks.html#objectOnNew(com.db4o.ObjectContainer)">
     * <code>objectOnNew</code></a> or
     * <a href="ext/ObjectCallbacks.html#objectOnUpdate(com.db4o.ObjectContainer)">
     * <code>objectOnUpdate</code></a> is triggered.
     * <a href="ext/ObjectCallbacks.html#objectOnUpdate(com.db4o.ObjectContainer)">
     * <code>objectOnUpdate</code></a> might also be used for cascaded updates.<br><br>
     * @param obj the object to be stored or updated.
	 * @see <a href="ext/ExtObjectContainer.html#set(java.lang.Object, int)">
	 * <code>ExtObjectContainer#set(object, depth)</code></a>
	 * @see <a href="config/Configuration.html#updateDepth(int)">
	 * <code>Configuration#updateDepth()</code></a>
	 * @see <a href="config/ObjectClass.html#updateDepth(int)">
	 * <code>ObjectClass#updateDepth()</code></a>
	 * @see <a href="config/ObjectClass.html#cascadeOnUpdate(boolean)">
	 * <code>ObjectClass#cascadeOnUpdate()</code></a>
	 * @see <a href="config/ObjectField.html#cascadeOnUpdate(boolean)">
	 * <code>ObjectField#cascadeOnUpdate()</code></a>
	 * @see <br><a href="ext/ObjectCallbacks.html">Using callbacks</a>
     */
    public void set (Object obj);
    
    
    
}



