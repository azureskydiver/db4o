/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.caching;

import com.db4o.foundation.*;

/**
 * @exclude
 * @decaf.ignore
 */
public interface Cache4 <K, V> {

	V produce(K key, Function4<K,V> producer);

}
