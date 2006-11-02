package com.db4o.cs.server;

import com.db4o.ObjectContainer;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 1:50:11 AM
 */
public class DefaultSession implements Session {
	private ObjectContainer oc;

	public synchronized ObjectContainer getObjectContainer(Context context) {
		if(oc == null){
			oc = context.getObjectContainer();
		}
		return oc;
	}
}
