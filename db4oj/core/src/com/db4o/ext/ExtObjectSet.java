/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.ext;

import com.db4o.*;

/**
 * extended functionality for the
 * <a href="../ObjectSet.html"><code>ObjectSet</code></a> interface.
 * <br><br>Every db4o <a href="../ObjectSet.html"><code>ObjectSet</code></a>
 * always is an ExtObjectSet so a cast is possible.<br><br>
 * <a href="../ObjectSet.html#ext()"><code>ObjectSet.ext()</code></a>
 * is a convenient method to perform the cast.<br><br>
 * The ObjectSet functionality is split to two interfaces to allow newcomers to
 * focus on the essential methods.
 */
public interface ExtObjectSet extends ObjectSet {
	
	/**
	 * returns an array of internal IDs that correspond to the contained objects.
	 * <br><br>
	 * @see <a href="ExtObjectContainer.html#getID(java.lang.Object)">
	 * <code>ExtObjectContainer.getID()</code></a>
	 * @see <a href="ExtObjectContainer.html#getByID(long)">
	 * <code>ExtObjectContainer.getByID()</code></a>
	 */
	public long[] getIDs();
	
	
}
