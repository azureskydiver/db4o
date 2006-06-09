/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package  com.db4o.config;
/**
 * configuration interface for fields of classes.
 * <br><br><b>Examples: ../com/db4o/samples/translators.</b><br><br>
 * Use the global Configuration object to configure db4o before opening an
 * {@link com.db4o.ObjectContainer ObjectContainer}.<br><br>
 * <b>Example:</b><br>
 * <code>
 * Configuration config = Db4o.configure();<br>
 * ObjectClass oc = config.objectClass("package.className");<br>
 * ObjectField of = oc.objectField("fieldName");
 * of.rename("newFieldName");
 * of.queryEvaluation(false);
 * </code>
 */
public interface ObjectField extends ObjectConfig {
	
	
	/**
	 * turns indexing on or off.
	 * <br><br>Field indices dramatically improve query performance but they may
	 * considerably reduce storage and update performance.<br>The best benchmark whether
	 * or not an index on a field achieves the desired result is the completed application
	 * - with a data load that is typical for it's use.<br><br>This configuration setting
	 * is only checked when the {@link com.db4o.ObjectContainer} is opened. If the
	 * setting is set to <code>true</code> and an index does not exist, the index will be
	 * created. If the setting is set to <code>false</code> and an index does exist the
	 * index will be dropped.<br><br>
	 * @param flag specify <code>true</code> or <code>false</code> to turn indexing on for
	 * this field
	 */
	public void indexed(boolean flag);
	

    /**
	 * renames a field of a stored class.
	 * <br><br>Use this method to refactor classes.
     * <br><br><b>Examples: ../com/db4o/samples/rename.</b><br><br>
     * @param newName the new fieldname.
     */
    public void rename (String newName);


    /**
	 * toggles query evaluation.
	 * <br><br>All fields are evaluated by default. Use this method to turn query
	 * evaluation of for specific fields.<br><br>
     * @param flag specify <code>false</code> to ignore this field during query evaluation.
     */
    public void queryEvaluation (boolean flag);
}
