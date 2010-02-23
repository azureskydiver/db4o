/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.fileheader;

import com.db4o.internal.*;

/**
 * @exclude
 */
public class FileHeader2 extends FileHeader1 {
	
	@Override
    protected byte version() {
		return (byte) 2;
	}
    
    @Override
    protected FileHeader1 createNew() {
    	return new FileHeader2();
    }
    
    @Override
    protected FileHeaderVariablePart1 createVariablePart(
    		LocalObjectContainer file, int id) {
    	return new FileHeaderVariablePart2(file, id, file.systemData());
    }
    
    @Override
	public FileHeader convert(LocalObjectContainer file) {
    	return this;
    }

}
