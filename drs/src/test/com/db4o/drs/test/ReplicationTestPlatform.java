/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.util.*;

import com.db4o.foundation.*;

/**
 * @sharpen.ignore
 */
public class ReplicationTestPlatform {

	public static Iterator4 adapt(final Iterator iterator) {
		return new Iterator4() {
			private Object _current;
			
			public Object current() {
				return _current;
			}

			public boolean moveNext() {
				if (!iterator.hasNext()) {
					return false;
				}
				_current = iterator.next();
				return true;
			}

			public void reset() {
				throw new NotImplementedException();
			}
		};
	}

}
