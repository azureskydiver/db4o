/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.network.*;

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

    protected void copy(YapSocket sock,OutputStream rawout,int length,boolean update) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(rawout);
        byte[] buffer=new byte[BlobImpl.COPYBUFFER_LENGTH];
        int totalread=0;
        while(totalread<length) {
            int stilltoread=length-totalread;
            int readsize=(stilltoread<buffer.length ? stilltoread : buffer.length);
            int curread=sock.read(buffer,0,readsize);
            
            if(curread < 0){
                throw new IOException();
            }
            
            out.write(buffer,0,curread);
            totalread+=curread;
            if(update) {
                i_currentByte+=curread;
            }
        }
        out.flush();
        out.close();
    }

    protected void copy(InputStream rawin,YapSocket sock,boolean update) throws IOException {
        BufferedInputStream in = new BufferedInputStream(rawin);
        byte[] buffer=new byte[BlobImpl.COPYBUFFER_LENGTH];
        int bytesread=-1;
        while((bytesread=rawin.read(buffer))>=0) {
            sock.write(buffer,0,bytesread);
            if(update) {
                i_currentByte+=bytesread;
            }
        }
        in.close();
    }
}
