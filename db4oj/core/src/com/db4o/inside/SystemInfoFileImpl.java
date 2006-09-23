/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.inside.freespace.*;

/**
 * @exclude
 */
public class SystemInfoFileImpl implements SystemInfo{
    
    private YapFile _file;

    public SystemInfoFileImpl(YapFile file){
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
    
   
    
    
    

}
