/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;

abstract class MsgBlob extends MsgD {

    BlobImpl i_blob;
    int i_currentByte;
    int i_length;

    double getStatus() {
        if (i_length != 0) {
            return (double) i_currentByte / (double) i_length;
        }
        return Status.ERROR;
    }

    abstract void processClient(YapSocket sock);

    BlobImpl serverGetBlobImpl() {
        BlobImpl blobImpl = null;
        int id = payLoad.readInt();
        YapStream stream = getStream();
        synchronized (stream.i_lock) {
            blobImpl = (BlobImpl) stream.getByID1(getTransaction(), id);
            stream.activate1(getTransaction(), blobImpl, 3);
        }
        return blobImpl;
    }

}
