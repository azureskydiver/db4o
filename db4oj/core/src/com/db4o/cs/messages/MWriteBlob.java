/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.ext.Status;
import com.db4o.foundation.network.YapSocket;


public class MWriteBlob extends MsgBlob {
	public void processClient(YapSocket sock) throws IOException {
        Msg message = Msg.readMessage(getTransaction(), sock);
        if (message.equals(Msg.OK)) {
            try {
                _currentByte = 0;
                _length = this._blob.getLength();
                _blob.getStatusFrom(this);
                _blob.setStatus(Status.PROCESSING);
                FileInputStream inBlob = this._blob.getClientInputStream();
                copy(inBlob,sock,true);
                sock.flush();
                YapStream stream = getStream();
                message = Msg.readMessage(getTransaction(), sock);
                if (message.equals(Msg.OK)) {

                    // make sure to load the filename to i_blob
                    // to allow client databasefile switching
                    stream.deactivate(_blob, Integer.MAX_VALUE);
                    stream.activate(_blob, Integer.MAX_VALUE);

                    this._blob.setStatus(Status.COMPLETED);
                } else {
                    this._blob.setStatus(Status.ERROR);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

	public boolean processAtServer(YapServerThread serverThread) {
        try {
            YapStream stream = getStream();
            BlobImpl blobImpl = this.serverGetBlobImpl();
            if (blobImpl != null) {
                blobImpl.setTrans(getTransaction());
                File file = blobImpl.serverFile(null, true);
                YapSocket sock = serverThread.socket();
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