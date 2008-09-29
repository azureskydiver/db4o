/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;


public class MWriteBlob extends MsgBlob implements ServerSideMessage {
	
	public void processClient(Socket4 sock) throws IOException {
        Msg message = Msg.readMessage(messageDispatcher(), transaction(), sock);
        if (message.equals(Msg.OK)) {
            try {
                _currentByte = 0;
                _length = this._blob.getLength();
                _blob.getStatusFrom(this);
                _blob.setStatus(Status.PROCESSING);
                FileInputStream inBlob = this._blob.getClientInputStream();
                copy(inBlob,sock,true);
                sock.flush();
                ObjectContainerBase stream = stream();
                message = Msg.readMessage(messageDispatcher(), transaction(), sock);
                if (message.equals(Msg.OK)) {

                    // make sure to load the filename to i_blob
                    // to allow client databasefile switching
                    stream.deactivate(transaction(), _blob, Integer.MAX_VALUE);
                    stream.activate(transaction(), _blob, new FullActivationDepth());

                    this._blob.setStatus(Status.COMPLETED);
                } else {
                    this._blob.setStatus(Status.ERROR);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

	public boolean processAtServer() {
        try {
            BlobImpl blobImpl = this.serverGetBlobImpl();
            if (blobImpl != null) {
                blobImpl.setTrans(transaction());
                File file = blobImpl.serverFile(null, true);
                Socket4 sock = serverMessageDispatcher().socket();
                Msg.OK.write(sock);
                FileOutputStream fout = new FileOutputStream(file);
                copy(sock,fout,blobImpl.getLength(),false);
                Msg.OK.write(sock);
            }
        } catch (Exception e) {
        }
        return true;
    }
}