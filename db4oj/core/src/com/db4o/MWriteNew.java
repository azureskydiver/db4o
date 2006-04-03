/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

final class MWriteNew extends MsgObject {
    final boolean processMessageAtServer(YapSocket sock) {
        int yapClassId = _payLoad.readInt();
        YapFile stream = (YapFile)getStream();
        unmarshall(YapConst.YAPINT_LENGTH);
        synchronized (stream.i_lock) {
            YapClass yc = stream.getYapClass(yapClassId);
            _payLoad.writeEmbedded();
            stream.prefetchedIDConsumed(_payLoad.getID());
            _payLoad.address(stream.getSlot(_payLoad.getLength()));
            yc.addFieldIndices(_payLoad, true);
            stream.writeNew(yc, _payLoad);
            getTransaction().writePointer(
                _payLoad.getID(),
                _payLoad.getAddress(),
                _payLoad.getLength());
        }
        return true;
    }
}