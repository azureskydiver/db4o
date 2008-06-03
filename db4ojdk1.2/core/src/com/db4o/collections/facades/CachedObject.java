/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.collections.facades;


/**
 * @exclude
 * @decaf.ignore
 */
public class CachedObject {
		public static transient CachedObject NONE = new CachedObject(null);

		public Object obj;

		public CachedObject(Object o) {
			obj = o;
		}

		public boolean equals(Object o) {
			if (!(o instanceof CachedObject)) {
				return false;
			}

			if ((this == NONE && o != NONE) || (this != NONE && o == NONE)) {
				return false;
			}

			CachedObject co = (CachedObject) o;
			return obj == null ? co.obj == null : obj.equals(co.obj);
		}
	}
