/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;

import com.db4o.*;

/**
 * client class index. Largly intended to do nothing or
 * redirect functionality to the server.
 */
final class ClassIndexClient extends ClassIndex {

	ClassIndexClient(YapClass aYapClass) {
		super(aYapClass);
	}

	public void add(int a_id){
		throw Exceptions4.virtualException();
	}
    
    void ensureActive(){
        // do nothing
    }
	
	public void read(Transaction a_trans) {
		// do nothing
	}

	void setDirty(YapStream a_stream) {
		// do nothing
	}

	void write(YapStream a_stream) {
		// do nothing
	}

	public final void writeOwnID(Transaction trans, YapReader a_writer) {
		a_writer.writeInt(0);
	}
	

}