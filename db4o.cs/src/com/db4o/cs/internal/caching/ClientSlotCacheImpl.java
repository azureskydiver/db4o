package com.db4o.cs.internal.caching;

import static com.db4o.foundation.Environments.*;

import java.util.*;

import com.db4o.*;
import com.db4o.cs.caching.*;
import com.db4o.events.*;
import com.db4o.internal.*;

// TODO: use Cache4 internally
public class ClientSlotCacheImpl implements ClientSlotCache {

	private final TransactionLocal<Map<Integer, ByteArrayBuffer>> _cache = new TransactionLocal<Map<Integer, ByteArrayBuffer>>() {
		public java.util.Map<Integer, ByteArrayBuffer> initialValueFor(Transaction transaction) {
			return new HashMap();
		};
	};
	
	public ClientSlotCacheImpl() {
		final EventRegistry eventRegistry = EventRegistryFactory.forObjectContainer(my(ObjectContainer.class));
		eventRegistry.activated().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4 e, ObjectInfoEventArgs args) {
				purge((Transaction) args.transaction(), (int)args.info().getInternalID());
            }
		});
	}
	
	public void add(Transaction provider, int id, ByteArrayBuffer slot) {
		cacheOn(provider).put(id, slot);
    }

	public ByteArrayBuffer get(Transaction provider, int id) {
		final ByteArrayBuffer buffer = cacheOn(provider).get(id);
		if (null == buffer) {
			return null;
		}
		buffer.seek(0);
		return buffer;
    }
	
	private void purge(Transaction provider, int id) {
		cacheOn(provider).remove(id);
	}

	private Map<Integer, ByteArrayBuffer> cacheOn(Transaction provider) {
		return provider.get(_cache).value;
	}
}
