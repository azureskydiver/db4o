/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * client class index. Largly intended to do nothing or
 * redirect functionality to the server.
 */
final class ClassIndexClient extends ClassIndex {

	private final YapClass i_yapClass;

	ClassIndexClient(YapClass aYapClass) {
		i_yapClass = aYapClass;
	}

	void add(int a_id){
		throw YapConst.virtualException();
	}
	
	long[] getInternalIDs(Transaction trans, int yapClassID){
		YapClient stream = (YapClient) i_yapClass.getStream();
		stream.writeMsg(Msg.GET_INTERNAL_IDS.getWriterForInt(trans, yapClassID));
		YapWriter reader = stream.expectedByteResponse(Msg.ID_LIST);
		int size = reader.readInt();
		long[] ids = new long[size];
		for (int i = 0; i < size; i++) {
			ids[i] = reader.readInt();
		}
		return ids;
	}
	
	void read(Transaction a_trans) {
		// do nothing
	}

	void setDirty(YapStream a_stream) {
		// do nothing
	}

	void setID(YapStream a_stream, int a_id) {
		// do nothing and dont cache
		// ID will remain zero, so the index
		// will be stored automatically on the server side
	}

	void write(YapStream a_stream) {
		// do nothing
	}

	final void writeOwnID(YapWriter a_writer) {
		a_writer.writeInt(0);
	}
	

}