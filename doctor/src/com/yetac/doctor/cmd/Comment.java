/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import com.yetac.doctor.writers.*;


/**
 * 
 */
public class Comment extends Command{
    
    public void resolve() {
        detectParameters();
        hide();
    }
    
    public void setCmd() {
        cmd = (byte)'/';
    }

    public void write(DocsWriter writer) throws Exception{
        
    }

}
