/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;


public class MReadBlob extends MsgBlob implements ServerSideMessage {
	public void processClient(Socket4 sock) throws IOException {
        Msg message = Msg.readMessage(messageDispatcher(), transaction(), sock);
        if (message.equals(Msg.LENGTH)) {
            try {
                _currentByte = 0;
                _length = message.payLoad().readInt();
                _blob.getStatusFrom(this);
                _blob.setStatus(Status.PROCESSING);
                copy(sock,this._blob.getClientOutputStream(),_length,true);
                message = Msg.readMessage(messageDispatcher(), transaction(), sock);
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
    public boolean processAtServer() {
        try {
            BlobImpl blobImpl = this.serverGetBlobImpl();
            if (blobImpl != null) {
                blobImpl.setTrans(transaction());
                File file = blobImpl.serverFile(null, false);
                int length = (int) file.length();
                Socket4 sock = serverMessageDispatcher().socket();
                Msg.LENGTH.getWriterForInt(transaction(), length).write(sock);
                FileInputStream fin = new FileInputStream(file);
                copy(fin,sock,false);
                sock.flush();
                Msg.OK.write(sock);
            }
        } catch (Exception e) {
        	write(Msg.ERROR);
        }
        return true;
    }
}