/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import com.yetac.doctor.*;
import com.yetac.doctor.workers.*;
import com.yetac.doctor.writers.*;

public class Embed extends Command{
    
    public void resolve() {
        detectParameters();
        hide();
        embeddedDocsFile();
    }

    public void write(DocsWriter writer) throws Exception{
        DocsFile docsFile = embeddedDocsFile();
        if(docsFile != null){
            docsFile.writeEmbedded(writer);
        }
    }
    
    protected DocsFile embeddedDocsFile(){
        String fileName = new String(parameter) + "." + Configuration.FILE_EXTENSION;
        DocsFile docsFile = (DocsFile)source.files.filesByName.get(fileName);
        if(docsFile != null){
            docsFile.write = -1;    
        }
        return docsFile;
    }

}
