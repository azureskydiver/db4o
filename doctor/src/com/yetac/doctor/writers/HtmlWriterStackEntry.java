/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.writers;

import java.io.*;

class HtmlWriterStackEntry {
    
    RandomAccessFile raf;
    int outlineLevel;
    int embedOutLineLevel;
    
    public HtmlWriterStackEntry(RandomAccessFile raf, int outlineLevel){
        this.raf = raf;
        this.outlineLevel = outlineLevel;
        embedOutLineLevel = -1;
    }
    
}
