/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package  com.db4o;

import com.db4o.ext.*;

/**
 * query resultset.
 * <br><br>The <code>ObjectSet</code> class serves as a cursor to
 * iterate through a set of objects retrieved by a 
 * call to
 * <a href="ObjectContainer.html#get(java.lang.Object)"><code>
 * ObjectContainer.get(template)</code></a>.
 * <br><br>An <code>ObjectSet</code> can easily be wrapped to a
 * <code>java.util.List</code> (Java)  / <code>System.Collections.IList</code>  (.NET)
 * using the source code supplied in ../com/db4o/wrap/     
 * <br><br>Note that the used
 * <a href="ObjectContainer.html"><code>
 * ObjectContainer</code></a> needs to remain opened during the
 * use of an <code>ObjectSet</code> to allow lazy instantiation.
 * @see <a href="ext\ExtObjectSet.html"><code>ExtObjectSet</code></a> for
 * extended functionality.
 */
public interface ObjectSet {
	
	
	/**
     * returns an ObjectSet with extended functionality.
     * <br><br>Every ObjectSet that db4o provides can be casted to
     * an ExtObjectSet. This method is supplied for your convenience
     * to work without a cast.
     * <br><br>The ObjectSet functionality is split to two interfaces
     * to allow newcomers to focus on the essential methods.
     */
    public ExtObjectSet ext();
	
	
    /**
	 * returns <code>true</code> if the <code>ObjectSet</code> has more elements.
	 *
     * @return boolean - <code>true</code> if the <code>ObjectSet</code> has more
	 * elements.
     */
    public boolean hasNext ();

    /**
	 * returns the next object in the <code>ObjectSet</code>.
	 * <br><br>
	 * Before returning the Object, next() triggers automatic activation of the
	 * Object with the respective
	 * <a href="config/Configuration.html#activationDepth(int)">
	 * global</a> or
	 * <a href="config/ObjectClass.html#maximumActivationDepth(int)">class specific</a>
	 * setting.<br><br>
     * @return the next object in the <code>ObjectSet</code>.
     */
    public Object next ();

    /**
	 * resets the <code>ObjectSet</code> cursor before the first element.
	 * <br><br>A subsequent call to <code>next()</code> will return the first element.
     */
    public void reset ();

    /**
	 * returns the number of elements in the <code>ObjectSet</code>.
     * @return the number of elements in the <code>ObjectSet</code>.
     */
    public int size ();
}



