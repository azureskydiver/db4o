package com.db4odoc.pitfall.uuidhashcode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;


public class UUIDContainer implements Iterable<UUID>{
    private final Set<UUID> knowsId = new HashSet<UUID>();

    public boolean add(UUID uuid) {
        return knowsId.add(uuid);
    }

    public boolean contains(Object o) {
        return knowsId.contains(o);
    }

    public Iterator<UUID> iterator() {
        return knowsId.iterator();
    }
}
