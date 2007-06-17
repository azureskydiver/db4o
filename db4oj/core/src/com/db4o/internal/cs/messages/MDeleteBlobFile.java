/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import java.io.*;

import com.db4o.foundation.network.*;
import com.db4o.types.*;


public class MDeleteBlobFile extends MsgBlob implements ServerSideMessage {

	public boolean processAtServer() {
        try {
            Blob blob = this.serverGetBlobImpl();
            if (blob != null) {
                blob.deleteFile();
            }
        } catch (Exception e) {
        }
        return true;
	}

    public void processClient(Socket4 sock) throws IOException {
        // nothing to do here
    }

}