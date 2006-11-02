package com.db4o.cs.client.protocol.objectStream;

import com.db4o.cs.server.Entry;

import java.util.List;
import java.util.ArrayList;

/**
 * Probably wise to do this as a delegate to an internal ArrayList so no direct access.
 *
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 2:02:12 PM
 */
public class EntryList extends ArrayList implements List {

	public boolean add(Object o) {
		return super.add(o);
	}

	public Object get(int index) {
		Entry entry = (Entry) super.get(index);
		return entry.getObject();
	}
}
