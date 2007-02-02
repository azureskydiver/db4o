/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.internal.freespace.*;

/**
 * @exclude
 */
public class SystemInfoFileImpl implements SystemInfo{
    
    private LocalObjectContainer _file;

    public SystemInfoFileImpl(LocalObjectContainer file){
        _file = file;
    }

    public int freespaceEntryCount() {
        if(! hasFreespaceManager()){
            return 0;
        }
        return freespaceManager().entryCount();
    }

    private boolean hasFreespaceManager() {
        return freespaceManager() != null;
    }
 

    private FreespaceManager freespaceManager() {
        return _file.freespaceManager();
    }

    public long freespaceSize() {
        if(! hasFreespaceManager()){
            return 0;
        }
        long blockSize = _file.blockSize();
        long blockedSize = freespaceManager().freeSize();
        return blockSize * blockedSize;
    }

    public long totalSize() {
        return _file.fileLength();
    }


}
