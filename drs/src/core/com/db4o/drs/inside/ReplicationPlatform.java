package com.db4o.drs.inside;

import java.util.*;

/**
 * Platform dependent code goes here to minimize manually
 * converted code.
 */
public class ReplicationPlatform {

	@SuppressWarnings("unchecked")
	public static void copyCollectionState(Object original, Object destination, CounterpartFinder counterpartFinder) {
		Collection originalCollection = (Collection) original;
		Collection destinationCollection = (Collection) destination;
		destinationCollection.clear();
		Iterator it = originalCollection.iterator();
		while (it.hasNext()) {
			Object element = it.next();
			Object counterpart = counterpartFinder.findCounterpart(element);
			destinationCollection.add(counterpart);
		}
	}

	public static Collection emptyCollectionClone(Collection original) {
		if (original instanceof List) return new ArrayList(original.size());
		if (original instanceof Set) return new HashSet(original.size());
		return null;
	}
}
