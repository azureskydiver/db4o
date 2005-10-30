/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.writers;

import java.io.*;

import com.yetac.doctor.workers.*;

class HtmlWriterStackEntry {
    
    DocsFile outlineTarget;
    RandomAccessFile raf;
    int outlineLevel;
    int embedOutLineLevel;
    
    public HtmlWriterStackEntry(DocsFile outlineTarget, RandomAccessFile raf, int outlineLevel){
        this.outlineTarget = outlineTarget;
        this.raf = raf;
        this.outlineLevel = outlineLevel;
        embedOutLineLevel = -1;
    }
    
}
