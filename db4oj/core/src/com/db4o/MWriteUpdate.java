/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

final class MWriteUpdate extends MsgObject {
	final boolean processMessageAtServer(YapSocket sock) {
	    int yapClassId = payLoad.readInt();
	    YapFile stream = (YapFile)getStream();
	    unmarshall(YapConst.YAPINT_LENGTH);
	    synchronized(stream.i_lock){
	        YapClass yc = stream.getYapClass(yapClassId);
			payLoad.writeEmbedded();
			yc.addFieldIndices(payLoad, false);
			stream.writeUpdate(yc, payLoad);
		}
		return true;
	}
	
}