/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.db4o.ext.Status;
import com.db4o.foundation.network.YapSocket;


class MReadBlob extends MsgBlob {
    void processClient(YapSocket sock) throws IOException {
        Msg message = Msg.readMessage(getTransaction(), sock);
        if (message.equals(Msg.LENGTH)) {
            try {
                _currentByte = 0;
                _length = message.getPayLoad().readInt();
                _blob.getStatusFrom(this);
                _blob.setStatus(Status.PROCESSING);
                copy(sock,this._blob.getClientOutputStream(),_length,true);
                message = Msg.readMessage(getTransaction(), sock);
                if (message.equals(Msg.OK)) {
                    this._blob.setStatus(Status.COMPLETED);
                } else {
                    this._blob.setStatus(Status.ERROR);
                }
            } catch (Exception e) {
            }
        } else if (message.equals(Msg.ERROR)) {
            this._blob.setStatus(Status.ERROR);
        }

    }
    boolean processMessageAtServer(YapSocket sock) {
        YapStream stream = getStream();
        try {
            BlobImpl blobImpl = this.serverGetBlobImpl();
            if (blobImpl != null) {
                blobImpl.setTrans(getTransaction());
                File file = blobImpl.serverFile(null, false);
                int length = (int) file.length();
                Msg.LENGTH.getWriterForInt(getTransaction(), length).write(stream, sock);
                FileInputStream fin = new FileInputStream(file);
                copy(fin,sock,false);
                sock.flush();
                Msg.OK.write(stream, sock);
            }
        } catch (Exception e) {
            Msg.ERROR.write(stream, sock);
        }
        return true;
    }
}