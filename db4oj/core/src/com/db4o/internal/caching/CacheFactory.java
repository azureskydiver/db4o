/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.caching;

/**
 * @exclude
 */
public class CacheFactory {

	public static <K, V> Cache4<K, V> new2QCache(int size) {
		return new LRU2QCache<K, V>(size);
	}

	public static <K, V> Cache4<K, V> new2QXCache(int size) {
		return new LRU2QXCache<K, V>(size);
	}

	public static <K, V> Cache4<K, V> newLRUCache(int size) {
		return new LRUCache<K, V>(size);
	}

}
