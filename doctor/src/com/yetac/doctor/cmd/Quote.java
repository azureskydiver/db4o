/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.yetac.doctor.cmd;

import com.yetac.doctor.workers.*;
import com.yetac.doctor.writers.*;

public class Quote extends Embed {
    
    public void setCmd() {
        cmd = (byte)'<';
    }
    
    public void write(DocsWriter writer) throws Exception{
        DocsFile docsFile = embeddedDocsFile();
        if(docsFile != null){
            docsFile.writeCommands(writer);
        }
    }

}
