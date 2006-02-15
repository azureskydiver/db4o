package com.db4o;

import com.db4o.ext.*;

/**
 * Workaround to provide the Java 5 version with a hook to add ExtObjectContainer.
 * (Generic method declarations won't match ungenerified YapStreamBase implementations
 * otherwise and implementing it directly kills .NET conversion.)
 * 
 * @exclude
 */
public interface YapStreamSpec extends ExtObjectContainer {
}
