/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.ext.*;


class MReadBlob extends MsgBlob {

    void processClient(YapSocket sock) {
        Msg message = Msg.readMessage(getTransaction(), sock);
        if (message.equals(Msg.LENGTH)) {
            try {
                i_currentByte = 0;
                i_length = message.getPayLoad().readInt();
                i_blob.getStatusFrom(this);
                i_blob.setStatus(Status.PROCESSING);
                FileOutputStream outBlob = this.i_blob.getClientOutputStream();
                while (i_currentByte < i_length) {
                    outBlob.write(sock.read());
                    i_currentByte++;
                }
                outBlob.flush();
                outBlob.close();
                message = Msg.readMessage(getTransaction(), sock);
                if (message.equals(Msg.OK)) {
                    this.i_blob.setStatus(Status.COMPLETED);
                } else {
                    this.i_blob.setStatus(Status.ERROR);
                }
            } catch (Exception e) {
            }
        } else if (message.equals(Msg.ERROR)) {
            this.i_blob.setStatus(Status.ERROR);
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

                int bytes = 0;
                while (bytes < length) {
                    // TODO: handle termination
					sock.write(fin.read());
                    bytes++;
                }
                fin.close();
				sock.flush();
                Msg.OK.write(stream, sock);
            }
        } catch (Exception e) {
            Msg.ERROR.write(stream, sock);
        }
        return true;
    }
}