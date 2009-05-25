package com.db4o.cs.caching;

import com.db4o.internal.*;

public interface ClientSlotCache {

	void add(Transaction transaction, int id, ByteArrayBuffer slot);

	ByteArrayBuffer get(Transaction transaction, int id);

}
