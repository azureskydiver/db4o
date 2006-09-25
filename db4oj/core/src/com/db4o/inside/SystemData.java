/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;

import com.db4o.header.*;


/**
 * @exclude
 */
public class SystemData {
    
    private final FileHeader _fileHeader;
    
    private int _uuidIndexId;
    
    private int _classCollectionID;
    
    private int _freeSpaceID; 
    
    public SystemData(FileHeader fileHeader){
        _fileHeader = fileHeader;
    }
    
    public int uuidIndexId(){
        return _uuidIndexId;
    }
    
    public void uuidIndexId(int id){
        _uuidIndexId = id;
    }
    
    public void uuidIndexCreated(int id){
        _uuidIndexId = id;
        _fileHeader.variablePartChanged();
    }
    
    public int classCollectionID() {
        return _classCollectionID;
    }

    public int freeSpaceID() {
        return _freeSpaceID;
    }

    public void classCollectionID(int id) {
        _classCollectionID = id;
    }

    public void freeSpaceID(int id) {
        _freeSpaceID = id;
    }

}
