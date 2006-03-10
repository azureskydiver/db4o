/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * client class index. Largly intended to do nothing or
 * redirect functionality to the server.
 */
final class ClassIndexClient extends ClassIndex {

	ClassIndexClient(YapClass aYapClass) {
		super(aYapClass);
	}

	void add(int a_id){
		throw YapConst.virtualException();
	}
    
    void ensureActive(){
        // do nothing
    }
	
	long[] getInternalIDs(Transaction trans, int yapClassID){
		YapClient stream = (YapClient)trans.i_stream;
		stream.writeMsg(Msg.GET_INTERNAL_IDS.getWriterForInt(trans, yapClassID));
		YapWriter reader = stream.expectedByteResponse(Msg.ID_LIST);
		int size = reader.readInt();
		long[] ids = new long[size];
		for (int i = 0; i < size; i++) {
			ids[i] = reader.readInt();
		}
		return ids;
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

	final void writeOwnID(Transaction trans, YapReader a_writer) {
		a_writer.writeInt(0);
	}
	

}