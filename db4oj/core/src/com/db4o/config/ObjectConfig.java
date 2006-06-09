package com.db4o.config;

public interface ObjectConfig {

	/**
	 * sets cascaded activation behaviour.
	 * <br><br>
	 * Setting cascadeOnActivate to true will result in the activation
	 * of the object attribute stored in this field if the parent object
	 * is activated.
	 * <br><br>
	 * The default setting is <b>false</b>.<br><br>
	 * @param flag whether activation is to be cascaded to the member object.
	 * @see Configuration#activationDepth Why activation?
	 * @see ObjectClass#cascadeOnActivate
	 * @see com.db4o.ObjectContainer#activate
	 * @see com.db4o.ext.ObjectCallbacks Using callbacks
	 */
	void cascadeOnActivate(boolean flag);

	/**
	 * sets cascaded delete behaviour.
	 * <br><br>
	 * Setting cascadeOnDelete to true will result in the deletion of
	 * the object attribute stored in this field on the parent object
	 * if the parent object is passed to 
	 * {@link com.db4o.ObjectContainer#delete ObjectContainer#delete()}.
	 * <br><br>
	 * <b>Caution !</b><br>
	 * This setting will also trigger deletion of the old member object, on
	 * calls to {@link com.db4o.ObjectContainer#set ObjectContainer#set()}.
	 * An example of the behaviour can be found in 
	 * {@link ObjectClass#cascadeOnDelete ObjectClass#cascadeOnDelete()}
	 * <br><br>
	 * The default setting is <b>false</b>.<br><br>
	 * @param flag whether deletes are to be cascaded to the member object.
	 * @see ObjectClass#cascadeOnDelete
	 * @see com.db4o.ObjectContainer#delete
	 * @see com.db4o.ext.ObjectCallbacks Using callbacks
	 */
	void cascadeOnDelete(boolean flag);

	/**
	 * sets cascaded update behaviour.
	 * <br><br>
	 * Setting cascadeOnUpdate to true will result in the update
	 * of the object attribute stored in this field if the parent object
	 * is passed to
	 * {@link com.db4o.ObjectContainer#set ObjectContainer#set()}.
	 * <br><br>
	 * The default setting is <b>false</b>.<br><br>
	 * @param flag whether updates are to be cascaded to the member object.
	 * @see com.db4o.ObjectContainer#set
	 * @see ObjectClass#cascadeOnUpdate
	 * @see ObjectClass#updateDepth
	 * @see com.db4o.ext.ObjectCallbacks Using callbacks
	 */
	void cascadeOnUpdate(boolean flag);

}