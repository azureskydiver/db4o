/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package  com.db4o;

import com.db4o.ext.*;
import com.db4o.query.*;


/**
 * storage and query interface.
 * <br><br>The <code>ObjectContainer</code> interface provides methods
 * to store, retrieve and delete objects and to commit and rollback
 * transactions.
 * @see ExtObjectContainer ExtObjectContainer for extended functionality.
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
     * {@link com.db4o.config.ObjectClass#maximumActivationDepth maximumActivationDepth()} and
     * {@link com.db4o.config.ObjectClass#minimumActivationDepth minimumActivationDepth()} in the
     * {@link com.db4o.config.ObjectClass ObjectClass interface}.<br><br>
     * A successful <code>activate()</code> triggers the callback method
     * {@link com.db4o.ext.ObjectCallbacks#objectOnActivate objectOnActivate}
     * which can be used for cascaded activation.<br><br>
	 * @see com.db4o.config.Configuration#activationDepth Why activation?
	 * @see ObjectCallbacks Using callbacks
     * @param obj the object to be activated.
	 * @param depth the member {@link com.db4o.config.Configuration#activationDepth depth}
	 *  to which activate is to cascade.
     */
    public void activate (Object obj, int depth);
    
    /**
     * closes the <code>ObjectContainer</code>.
     * <br><br>A call to <code>close()</code> automatically performs a 
     * {@link #commit commit()}.
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
     * {@link com.db4o.ext.ObjectCallbacks#objectOnDeactivate objectOnDeactivate}.
     * <br><br>
     * Be aware that calling this method with a depth parameter greater than 
     * 1 sets members on member objects to null. This may have side effects 
     * in other places of the application.<br><br>
	 * @see ObjectCallbacks Using callbacks
  	 * @see com.db4o.config.Configuration#activationDepth Why activation?
     * @param obj the object to be deactivated.
	 * @param depth the member {@link com.db4o.config.Configuration#activationDepth depth} 
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
     * {@link com.db4o.config.ObjectClass#cascadeOnDelete configured for the class}
     * or for {@link com.db4o.config.ObjectField#cascadeOnDelete one of the member fields}.
     * <br><br>The method has no effect, if
     * the passed object is not stored in the <code>ObjectContainer</code>.
     * <br><br>A subsequent call to
     * <code>set()</code> with the same object newly stores the object
     * to the <code>ObjectContainer</code>.<br><br>
     * <code>delete()</code> triggers the callback method
     * {@link com.db4o.ext.ObjectCallbacks#objectOnDelete objectOnDelete}
     * which can be also used for cascaded deletes.<br><br>
	 * @see com.db4o.config.ObjectClass#cascadeOnDelete
	 * @see com.db4o.config.ObjectField#cascadeOnDelete
	 * @see ObjectCallbacks Using callbacks
     * @param obj the object to be deleted from the
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
     * {@link ObjectSet ObjectSet} containing
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
     * <br><br>Consult the documentation of the Configuration package to
     * configure class-specific behaviour.<br><br><br>
     * <b>Returned Objects</b><br>
     * The objects returned in the
     * {@link ObjectSet ObjectSet} are instantiated
     * and activated to the preconfigured depth of 5. The
	 * {@link com.db4o.config.Configuration#activationDepth activation depth}
	 * may be configured {@link com.db4o.config.Configuration#activationDepth globally} or
     * {@link com.db4o.config.ObjectClass individually for classes}.
	 * <br><br>
     * db4o keeps track of all instantiatied objects. Queries will return
     * references to these objects instead of instantiating them a second time.
     * <br><br>
     * Objects newly activated by <code>get()</code> can respond to the callback
     * method {@link com.db4o.ext.ObjectCallbacks#objectOnActivate objectOnActivate}.
     * <br><br>
     * @param template object to be used as an example to find all matching objects.<br><br>
     * @return {@link ObjectSet ObjectSet} containing all found objects.<br><br>
	 * @see com.db4o.config.Configuration#activationDepth Why activation?
	 * @see ObjectCallbacks Using callbacks
	 */
    public ObjectSet get (Object template);
    
    /**
     * factory method to create a new {@link Query Query} object.
     * <br><br>
     * Use {@link #get get(Object template)} for
     * simple Query-By-Example.
     * <br><br>
     * @return a new Query object
     */
    public Query query ();
    
    
    /**
     * rolls back the running transaction.
     * <br><br>Modified application objects im memory are not restored.
     * Use combined calls to {@link #deactivate deactivate()}
     * and {@link #activate activate()} to reload an objects member values.
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
	 * {@link com.db4o.config.Configuration#updateDepth global} or 
     * {@link com.db4o.config.ObjectClass#updateDepth class-specific}
     * update depth was configured or cascaded updates were 
     * {@link com.db4o.config.ObjectClass#cascadeOnUpdate defined in the class}
     * or in {@link com.db4o.config.ObjectField#cascadeOnUpdate one of the member fields}.
     * <br><br><b>Examples: ../com/db4o/samples/update.</b><br><br>
     * Depending if the passed object is newly stored or updated, the
     * callback method
     * {@link com.db4o.ext.ObjectCallbacks#objectOnNew objectOnNew} or
     * {@link com.db4o.ext.ObjectCallbacks#objectOnUpdate objectOnUpdate} is triggered.
     * {@link com.db4o.ext.ObjectCallbacks#objectOnUpdate objectOnUpdate}
     * might also be used for cascaded updates.<br><br>
     * @param obj the object to be stored or updated.
	 * @see ExtObjectContainer#set(java.lang.Object, int) ExtObjectContainer#set(object, depth)
	 * @see com.db4o.config.Configuration#updateDepth
	 * @see com.db4o.config.ObjectClass#updateDepth
	 * @see com.db4o.config.ObjectClass#cascadeOnUpdate
	 * @see com.db4o.config.ObjectField#cascadeOnUpdate
	 * @see ObjectCallbacks Using callbacks
     */
    public void set (Object obj);
    
    
    
}



