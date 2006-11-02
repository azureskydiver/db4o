package com.db4o.cs.server;

import com.db4o.ObjectContainer;

/**
 * This is specific to a single client connection.
 * 
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 1:50:06 AM
 */
public interface Session {
	/**
	 *
	 * @param context todo: decide of context should be part os session, ie: session.getContext() instead of passing it in here.
	 * @return
	 */
	ObjectContainer getObjectContainer(Context context);

}
