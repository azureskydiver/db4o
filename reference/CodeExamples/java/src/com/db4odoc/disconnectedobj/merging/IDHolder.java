package com.db4odoc.disconnectedobj.merging;

import java.util.UUID;

public abstract class IDHolder {
    private final String uuid = UUID.randomUUID().toString();

    public String getObjectId(){
        return uuid;
    }
}