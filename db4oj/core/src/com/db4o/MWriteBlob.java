/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.network.*;


class MWriteBlob extends MsgBlob {

    void processClient(YapSocket sock) throws IOException {
        Msg message = Msg.readMessage(getTransaction(), sock);
        if (message.equals(Msg.OK)) {
            try {
                i_currentByte = 0;
                i_length = this.i_blob.getLength();
                i_blob.getStatusFrom(this);
                i_blob.setStatus(Status.PROCESSING);
                FileInputStream inBlob = this.i_blob.getClientInputStream();
                copy(inBlob,sock,true);
                sock.flush();
                YapStream stream = getStream();
                message = Msg.readMessage(getTransaction(), sock);
                if (message.equals(Msg.OK)) {

                    // make sure to load the filename to i_blob
                    // to allow client databasefile switching
                    stream.deactivate(i_blob, Integer.MAX_VALUE);
                    stream.activate(i_blob, Integer.MAX_VALUE);

                    this.i_blob.setStatus(Status.COMPLETED);
                } else {
                    this.i_blob.setStatus(Status.ERROR);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    boolean processMessageAtServer(YapSocket sock) {
        try {
            YapStream stream = getStream();
            BlobImpl blobImpl = this.serverGetBlobImpl();
            if (blobImpl != null) {
                blobImpl.setTrans(getTransaction());
                File file = blobImpl.serverFile(null, true);
                Msg.OK.write(stream, sock);
                FileOutputStream fout = new FileOutputStream(file);
                copy(sock,fout,blobImpl.getLength(),false);
                Msg.OK.write(stream, sock);
            }
        } catch (Exception e) {
        }
        return true;
    }
}