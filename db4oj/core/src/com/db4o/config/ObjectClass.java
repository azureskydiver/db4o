/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package  com.db4o.config;

/**
 * configuration interface for classes.
 * <br><br><b>Examples: ../com/db4o/samples/translators/Default.java.</b><br><br>
 * Use the global Configuration object to configure db4o before opening an
 * <a href="../ObjectContainer.html"><code>ObjectContainer</code></a>.<br><br>
 * <b>Example:</b><br>
 * <code>
 * Configuration config = Db4o.configure();<br>
 * ObjectClass oc = config.objectClass("package.className");<br>
 * oc.updateDepth(3);<br>
 * oc.minimumActivationDepth(3);<br>
 * </code>
 */
public interface ObjectClass {
    
    /**
     * advises db4o to try instantiating objects of this class with/without
     * calling constructors.
     * <br><br>
     * Not all JDKs / .NET-environments support this feature. db4o will
     * attempt, to follow the setting as good as the enviroment supports.
     * In doing so, it may call implementation-specific features like
     * sun.reflect.ReflectionFactory#newConstructorForSerialization on the
     * Sun Java 1.4.x/5 VM (not available on other VMs) and 
     * FormatterServices.GetUninitializedObject() on
     * the .NET framework (not available on CompactFramework).<br><br>
     * This setting may also be set globally for all classes in
     * {@link Configuration#callConstructors(boolean)}.<br><br>
     * @param flag - specify true, to request calling constructors, specify
     * false to request <b>not</b> calling constructors.
	 * @see <a href="Configuration.html#callConstructors(boolean)">
     */
    public void callConstructor(boolean flag);
	
	
	/**
	 * sets cascaded activation behaviour.
	 * <br><br>
	 * Setting cascadeOnActivate to true will result in the activation
	 * of all member objects if an instance of this class is activated.
	 * <br><br>
	 * The default setting is <b>false</b>.<br><br>
	 * @param flag whether activation is to be cascaded to member objects.
	 * @see <a href="ObjectField.html#cascadeOnActivate(boolean)">
	 * <code>ObjectField#cascadeOnActivate()</code></a>
	 * @see <a href="../ObjectContainer.html#activate(java.lang.Object, int)">
	 * <code>ObjectContainer#activate()</code></a>
	 * @see <br><a href="../ext/ObjectCallbacks.html">Using callbacks</a>
	 * @see <a href="Configuration.html#activationDepth(int)">
	 * Why activation?</a>
	 */
	public void cascadeOnActivate(boolean flag);


	/**
	 * sets cascaded delete behaviour.
	 * <br><br>
	 * Setting cascadeOnDelete to true will result in the deletion of
	 * all member objects of instances of this class, if they are 
	 * passed to 
	 * <a href="../ObjectContainer.html#delete(java.lang.Object)">
	 * <code>ObjectContainer#delete()</code></a>.
	 * <br><br>
	 * <b>Caution !</b><br>
	 * This setting will also trigger deletion of old member objects, on
	 * calls to
	 * <a href="../ObjectContainer.html#set(java.lang.Object)">
	 * <code>ObjectContainer#set()</code></a>.<br><br>
	 * An example of the behaviour:<br>
	 * <code>
	 * ObjectContainer con;<br>
	 * Bar bar1 = new Bar();<br>
	 * Bar bar2 = new Bar();<br>
	 * foo.bar = bar1;<br>
	 * con.set(foo);  // bar1 is stored as a member of foo<br>
	 * foo.bar = bar2;<br>
	 * con.set(foo);  // bar2 is stored as a member of foo
	 * </code><br>The last statement will <b>also</b> delete bar1 from the
	 * ObjectContainer, no matter how many other stored objects hold references
	 * to bar1.
	 * <br><br>
	 * The default setting is <b>false</b>.<br><br>
	 * @param flag whether deletes are to be cascaded to member objects.
	 * @see <a href="ObjectField.html#cascadeOnDelete(boolean)">
	 * <code>ObjectField#cascadeOnDelete()</code></a>
	 * @see <a href="../ObjectContainer.html#delete(java.lang.Object)">
	 * <code>ObjectContainer#delete()</code></a>
	 * @see <br><a href="../ext/ObjectCallbacks.html">Using callbacks</a>
	 */
	public void cascadeOnDelete(boolean flag);
	
	
	/**
	 * sets cascaded update behaviour.
	 * <br><br>
	 * Setting cascadeOnUpdate to true will result in the update
	 * of all member objects if a stored instance of this class is passed
	 * to
	 * <a href="../ObjectContainer.html#set(java.lang.Object)">
	 * <code>ObjectContainer#set()</code></a>.
	 * <br><br>
	 * The default setting is <b>false</b>.<br><br>
	 * @param flag whether updates are to be cascaded to member objects.
	 * @see <a href="ObjectField.html#cascadeOnUpdate(boolean)">
	 * <code>ObjectField#cascadeOnUpdate()</code></a>
	 * @see <a href="../ObjectContainer.html#set(java.lang.Object)">
	 * <code>ObjectContainer#set()</code></a>
	 * @see <br><a href="../ext/ObjectCallbacks.html">Using callbacks</a>
	 */
	public void cascadeOnUpdate(boolean flag);
	
	
	/**
	 * registers an attribute provider for special query behavior.
	 * <br><br>The query processor will compare the object returned by the
	 * attribute provider instead of the actual object, both for the constraint
	 * and the candidate persistent object.<br><br> Preinstalled attribute
	 * providers are documented
	 * in the sourcecode of 
	 * com.db4o.samples.translators.Default.java#defaultConfiguration().<br><br>
	 * @param attributeProvider the attribute provider to be used
	 */
	public void compare(ObjectAttribute attributeProvider);
	
	
    /**
     * generate UUIDs for stored objects of this class.
     * 
     * @param setting 
     */
    // public void generateUUIDs(boolean setting);

    
    /**
     * generate version numbers for stored objects of this class.
     * 
     * @param setting
     */
    // public void generateVersionNumbers(boolean setting);
    

    /**
	 * sets the maximum activation depth to the desired value.
	 * <br><br>A class specific setting overrides the
	 * <a href="Configuration.html#activationDepth(int)">
	 * global setting</a>.<br><br>
     * @param depth the desired maximum activation depth
	 * @see <a href="Configuration.html#activationDepth(int)">
	 * Why activation?</a>
	 * @see <a href="ObjectClass.html#cascadeOnActivate(boolean)">
	 * <code>ObjectClass#cascadeOnActivate()</code></a>
     */
    public void maximumActivationDepth (int depth);



    /**
	 * sets the minimum activation depth to the desired value.
	 * <br><br>A class specific setting overrides the
	 * <a href="Configuration.html#activationDepth(int)">
	 * global setting</a>.<br><br>
     * @param depth the desired minimum activation depth
	 * @see <a href="Configuration.html#activationDepth(int)">
	 * Why activation?</a>
	 * @see <a href="ObjectClass.html#cascadeOnActivate(boolean)">
	 * <code>ObjectClass#cascadeOnActivate()</code></a>
     */
    public void minimumActivationDepth (int depth);


    /**
	 * returns an <a href="ObjectField.html"><code>ObjectField</code></a> object
	 * to configure the specified field.
	 * <br><br>
     * @param fieldName the fieldname of the field to be configured.<br><br>
     * @return an instance of an <a href="ObjectField.html"><code>ObjectField</code></a>
	 *  object for configuration.
     */
    public ObjectField objectField (String fieldName);
    
    
    /**
     * turns on storing static field values for this class.
     * <br><br>By default, static field values of classes are not stored
     * to the database file. By turning the setting on for a specific class
     * with this switch, all <b>non-simple-typed</b> static field values of this
     * class are stored the first time an object of the class is stored, and
     * restored, every time a database file is opened afterwards.
     * <br><br>The setting will be ignored for simple types.
     * <br><br>Use this setting for constant static object members.
     * <br><br>This option will slow down the process of opening database
     * files and the stored objects will occupy space in the database file.
     */
    public void persistStaticFieldValues();


    /**
	 * renames a stored class.
	 * <br><br>Use this method to refactor classes.
     * <br><br><b>Examples: ../com/db4o/samples/rename.</b><br><br>
     * <i>This feature is not available in db4o community edition.</i><br><br>
     * @param newName the new fully qualified classname.
     */
    public void rename (String newName);



    /**
	 * allows to specify if transient fields are to be stored.
	 * <br>The default for every class is <code>false</code>.<br><br>
     * @param flag whether or not transient fields are to be stored.
     */
    public void storeTransientFields (boolean flag);



    /**
	 * registers a translator for this class.
     * <br><br>
	 * Preinstalled translators are documented in the sourcecode of
	 * com.db4o.samples.translators.Default.java#defaultConfiguration().
	 * <br><br>Example translators can also be found in this folder.<br><br>
     * @param translator this may be an
     * <a href="ObjectTranslator.html"><code>ObjectTranslator</code></a>
     *  or an <a href="ObjectConstructor.html"><code>ObjectConstructor</code></a>
	 * @see <a href="ObjectTranslator.html">ObjectTranslator</a>
	 * @see <a href="ObjectConstructor.html">ObjectConstructor</a>
     */
    public void translate (ObjectTranslator translator);



    /**
	 * specifies the updateDepth for this class.
	 * <br><br>see the documentation of
	 * <a href="../ObjectContainer.html#set(java.lang.Object)">
	 * ObjectContainer.set()</a> for further details.<br><br>
	 * The default setting is 0: Only the object passed to
	 * <a href="../ObjectContainer.html#set(java.lang.Object)">
	 * ObjectContainer.set()</a> will be updated.<br><br>
     * @param depth the depth of the desired update for this class.
	 * @see <a href="Configuration.html#updateDepth(int)">
	 * <code>Configuration#updateDepth()</code></a>
	 * @see <a href="ObjectClass.html#cascadeOnUpdate(boolean)">
	 * <code>ObjectClass#cascadeOnUpdate()</code></a>
	 * @see <a href="ObjectField.html#cascadeOnUpdate(boolean)">
	 * <code>ObjectField#cascadeOnUpdate()</code></a>
	 * @see <br><a href="../ext/ObjectCallbacks.html">Using callbacks</a>
     */
    public void updateDepth (int depth);
}



